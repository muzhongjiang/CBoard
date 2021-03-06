package org.cboard.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.cboard.dataprovider.DataProvider;
import org.cboard.dataprovider.Initializing;
import org.cboard.dataprovider.aggregator.Aggregatable;
import org.cboard.dataprovider.annotation.DatasourceParameter;
import org.cboard.dataprovider.annotation.QueryParameter;
import org.cboard.dataprovider.config.AggConfig;
import org.cboard.dataprovider.result.AggregateResult;
import org.cboard.dataprovider.util.DPCommonUtils;
import org.cboard.dataprovider.util.SqlHelper;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.alibaba.druid.util.JdbcConstants.*;

/**
 * Created by yfyuan on 2016/8/17.
 */
@Component("jdbc")
@Scope("prototype")
public class JdbcDataProvider extends DataProvider implements Aggregatable, Initializing {

    @DatasourceParameter(label = "{{'DATAPROVIDER.JDBC.DRIVER'|translate}} *",
            type = DatasourceParameter.Type.Select,
            optionsText = {
                    MYSQL_DRIVER,
                    POSTGRESQL_DRIVER,
                    "com.pivotal.jdbc.GreenplumDriver",
                    ORACLE_DRIVER,
                    SQL_SERVER_DRIVER,
                    PRESTO_DRIVER,
                    HIVE_DRIVER, //hiveserver2
                    "com.cloudera.impala.jdbc41.Driver",
                    CLICKHOUSE_DRIVER
            },
            optionsValue = {
                    MYSQL,
                    POSTGRESQL,
                    "Greenplum",
                    ORACLE,
                    SQL_SERVER,
                    PRESTO,
                    HIVE, //hiveserver2
                    "Impala",
                    CLICKHOUSE
            },
            required = true,
            order = 1)

    private String DRIVER = "driver";


    @DatasourceParameter(label = "{{'DATAPROVIDER.JDBC.JDBCURL'|translate}} *",
            type = DatasourceParameter.Type.Input,
            required = true,
            order = 2)
    private String JDBC_URL = "jdbcurl";

    @DatasourceParameter(label = "{{'DATAPROVIDER.JDBC.USERNAME'|translate}} *",
            type = DatasourceParameter.Type.Input,
            required = true,
            order = 3)
    private String USERNAME = "username";

    @DatasourceParameter(label = "{{'DATAPROVIDER.JDBC.PASSWORD'|translate}}",
            type = DatasourceParameter.Type.Password,
            order = 4)
    private String PASSWORD = "password";

    @DatasourceParameter(label = "{{'DATAPROVIDER.POOLEDCONNECTION'|translate}}",
            checked = true,
            type = DatasourceParameter.Type.Checkbox,
            order = 99)
    private String POOLED = "pooled";

    @DatasourceParameter(label = "{{'DATAPROVIDER.AGGREGATABLE_PROVIDER'|translate}}",
            checked = true,
            type = DatasourceParameter.Type.Checkbox,
            order = 100)
    private String aggregateProvider = "aggregateProvider";

    @QueryParameter(label = "{{'DATAPROVIDER.JDBC.SQLTEXT'|translate}}",
            type = QueryParameter.Type.TextArea,
            required = true,
            order = 1)
    private String SQL = "sql";

    private static final ConcurrentMap<String, DataSource> datasourceMap = new ConcurrentHashMap<>();

    private SqlHelper sqlHelper;


    @Override
    public void test() throws Exception {
        String queryStr = query.get(SQL);
        LOG.info("Execute query: {}", queryStr);
        try (Connection con = getConnection();
             Statement ps = con.createStatement()) {
            ps.executeQuery(queryStr);
        } catch (Exception e) {
            LOG.error("Error when execute: {}", queryStr);
            throw new Exception("ERROR:" + e.getMessage(), e);
        }
    }


    /**
     * 将sql文本转换为子查询字符串:
     * 删除空行
     * 删除结束分号
     *
     * @param rawQueryText
     * @return
     */
    private String getAsSubQuery(String rawQueryText) {
        String deletedBlankLine = rawQueryText.replaceAll("(?m)^[\\s\t]*\r?\n", "").trim();
        return deletedBlankLine.endsWith(";") ? deletedBlankLine.substring(0, deletedBlankLine.length() - 1) : deletedBlankLine;
    }

    /**
     * 获取jdbc connection （使用druid连接池方式）
     */
    private Connection getConnection() throws Exception {
//        String usePool = dataSource.get(POOLED);//是否使用jdbc连接池
        String username = dataSource.get(USERNAME);
        String password = dataSource.get(PASSWORD);
        Connection conn = null;
// if ("true".equals(usePool)) {//都用jdbc连接池
        String key = DigestUtils.md5Hex(JSONObject.toJSON(dataSource).toString());

        DataSource ds = datasourceMap.get(key);
        if (ds == null) {
            synchronized (key.intern()) {
                ds = datasourceMap.get(key);
                if (ds == null) {
                    Map<String, String> conf = new HashedMap<>();
                    conf.put(DruidDataSourceFactory.PROP_DRIVERCLASSNAME, dataSource.get(DRIVER));
                    conf.put(DruidDataSourceFactory.PROP_URL, dataSource.get(JDBC_URL));
                    conf.put(DruidDataSourceFactory.PROP_USERNAME, dataSource.get(USERNAME));
                    if (StringUtils.isNotBlank(password)) {
                        conf.put(DruidDataSourceFactory.PROP_PASSWORD, dataSource.get(PASSWORD));
                    }
                    conf.put(DruidDataSourceFactory.PROP_INITIALSIZE, "3");
                    DruidDataSource druidDS = (DruidDataSource) DruidDataSourceFactory.createDataSource(conf);
                    druidDS.setBreakAfterAcquireFailure(true);
                    druidDS.setConnectionErrorRetryAttempts(5);
                    datasourceMap.put(key, druidDS);
                    ds = datasourceMap.get(key);
                }
            }
        }
        try {
            conn = ds.getConnection();
        } catch (SQLException e) {
            datasourceMap.remove(key);
            throw e;
        }
        return conn;

    }

    @Override
    public String[] queryDimVals(String columnName, AggConfig config) throws Exception {
        String fsql = null;
        String exec = null;
        String sql = getAsSubQuery(query.get(SQL));
        List<String> filtered = new ArrayList<>();
        String whereStr = "";
        if (config != null) {
            whereStr = sqlHelper.assembleFilterSql(config);
        }
        fsql = "SELECT cb_view.%s FROM ( %s ) cb_view %s GROUP BY cb_view.%s";
        exec = String.format(fsql, columnName, sql, whereStr, columnName);
        LOG.info("【{}】", exec);
        try (Connection connection = getConnection();
             Statement stat = connection.createStatement();
             ResultSet rs = stat.executeQuery(exec)) {
            while (rs.next()) {
                filtered.add(rs.getString(1));
            }
        } catch (Exception e) {
            LOG.error("ERROR:{}", e.getMessage());
            throw new Exception("ERROR:" + e.getMessage(), e);
        }
        return filtered.toArray(new String[]{});
    }

    private ResultSetMetaData getMetaData(String subQuerySql, Statement stat) throws Exception {
        ResultSetMetaData metaData;
        try {
            //stat.setMaxRows(100);
            String fsql = "  SELECT * FROM ( %s ) cb_view WHERE 1=0";//WHERE 1=0 不需要返回数据，只需要元数据
            String sql = String.format(fsql, subQuerySql);
            LOG.info("getMetaData  sql=【{}】", sql);
            ResultSet rs = stat.executeQuery(sql);
            metaData = rs.getMetaData();
        } catch (Exception e) {
            LOG.error("ERROR:{}", e.getMessage());
            throw new Exception("ERROR:" + e.getMessage(), e);
        }
        return metaData;
    }

    /**
     * 获取column名称和对应column type
     */
    private Map<String, Integer> getColumnType() throws Exception {
        Map<String, Integer> result = null;
        String key = getLockKey();
        result = (Map<String, Integer>) cache.get(key);
        if (result != null) {
            return result;
        } else {
            try (
                    Connection connection = getConnection();
                    Statement stat = connection.createStatement()
            ) {
                String subQuerySql = getAsSubQuery(query.get(SQL));
                ResultSetMetaData metaData = getMetaData(subQuerySql, stat);
                int columnCount = metaData.getColumnCount();
                result = new HashedMap<>();
                for (int i = 0; i < columnCount; i++) {
                    result.put(
                            metaData.getColumnLabel(i + 1).toUpperCase(),//方便后面忽略列名大小写
                            metaData.getColumnType(i + 1)
                    );
                }
                cache.put(key, result, cacheExpire);
                return result;
            }
        }
    }

    @Override
    public String[] getColumn() throws Exception {
        String subQuerySql = getAsSubQuery(query.get(SQL));
        try (
                Connection connection = getConnection();
                Statement stat = connection.createStatement()
        ) {
            ResultSetMetaData metaData = getMetaData(subQuerySql, stat);
            int columnCount = metaData.getColumnCount();
            String[] row = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                row[i] = metaData.getColumnLabel(i + 1);
            }
            return row;
        }
    }


    //FIXME
    @Override
    public AggregateResult queryAggData(AggConfig config) throws Exception {
        String exec = SqlHelper.limitSql(dataSource.get(DRIVER), sqlHelper.assembleAggDataSql(config), resultLimit);
        List<String[]> list = new LinkedList<>();
        LOG.info("【{}】", exec);
        try (
                Connection connection = getConnection();
                Statement stat = connection.createStatement();
                ResultSet rs = stat.executeQuery(exec)
        ) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();//有多少字段
            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int j = 0; j < columnCount; j++) {
                    int columType = metaData.getColumnType(j + 1);
                    switch (columType) {
                        case java.sql.Types.DATE:
                            row[j] = rs.getDate(j + 1).toString();
                            break;
                        default:
                            row[j] = rs.getString(j + 1);
                            break;
                    }
                }
                list.add(row);
            }
        } catch (Exception e) {
            LOG.error("ERROR:{}", e);
            throw new Exception("ERROR:" + e.getMessage(), e);
        }
        return DPCommonUtils.transform2AggResult(config, list);
    }


    @Override
    public String viewAggDataQuery(AggConfig config) throws Exception {
        String sql = SqlHelper.limitSql(dataSource.get(DRIVER), sqlHelper.assembleAggDataSql(config), resultLimit);
        return SQLUtils.formatMySql(sql);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        String subQuery = null;
        if (query != null) {
            subQuery = getAsSubQuery(query.get(SQL));
        }
        SqlHelper sqlHelper = new SqlHelper(subQuery, true);
        if (!isUsedForTest()) {
            Map<String, Integer> columnTypes = null;
            try {
                columnTypes = getColumnType();
            } catch (Exception e) {
                //  getColumns() and test() methods do not need columnTypes properties,
                // it doesn't matter for these methods even getMeta failed
                LOG.warn("getColumnType failed: {}", ExceptionUtils.getStackTrace(e));
            }
            sqlHelper.getSqlSyntaxHelper().setColumnTypes(columnTypes);
        }
        this.sqlHelper = sqlHelper;
    }

}
