package org.cboard.dataprovider;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.googlecode.aviator.AviatorEvaluator;
import org.cboard.cache.CacheManager;
import org.cboard.dataprovider.aggregator.Aggregatable;
import org.cboard.dataprovider.config.AggConfig;
import org.cboard.dataprovider.config.CompositeConfig;
import org.cboard.dataprovider.config.ConfigComponent;
import org.cboard.dataprovider.config.DimensionConfig;
import org.cboard.dataprovider.expression.NowFunction;
import org.cboard.dataprovider.result.AggregateResult;
import org.cboard.pojo.DashboardRole;
import org.cboard.services.AuthenticationService;
import org.cboard.services.RoleService;
import org.cboard.util.NaturalOrderComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by zyong on 2017/1/9.
 */
public abstract class DataProvider {
    protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Value("${dataprovider.resultLimit:300000}")
    protected int resultLimit;

    @Value("${cache.expire:600000}")//默认10分钟
    protected long cacheExpire;

    @Autowired
    protected CacheManager cache;//缓存"查询结果数据"

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private RoleService roleService;

    protected Map<String, String> dataSource;
    protected Map<String, String> query;

    private boolean isUsedForTest = false;
    private long interval = 12 * 60 * 60; // second

    public static final String NULL_STRING = "#NULL";


    static {
        AviatorEvaluator.addFunction(new NowFunction());// FIXME ?????
    }


    /**
     * 通过用户的小部件设计器获取聚合数据
     */
    public final AggregateResult getAggData(AggConfig ac, boolean reload) throws Exception {
        evalValueExpression(ac);
        return ((Aggregatable) this).queryAggData(ac);
    }

    public final String getViewAggDataQuery(AggConfig config) throws Exception {
        evalValueExpression(config);
        return ((Aggregatable) this).viewAggDataQuery(config);
    }

    /**
     * 获取维度列的选项值
     *
     * @param columnName
     * @return
     */
    public final String[] getDimVals(String columnName, AggConfig config, boolean reload) throws Exception {
        String[] dimVals = null;
        evalValueExpression(config);
        dimVals = ((Aggregatable) this).queryDimVals(columnName, config);

        return Arrays.stream(dimVals)
                .map(member -> {
                    return Objects.isNull(member) ? NULL_STRING : member;
                })
                .sorted(new NaturalOrderComparator()).limit(1000).toArray(String[]::new);
    }

    //FIXME reload 没使用？？
    public final String[] invokeGetColumn(boolean reload) throws Exception {
        String[] columns = ((Aggregatable) this).getColumn();
        Arrays.sort(columns);
        return columns;
    }

    //FIXME
    private void evalValueExpression(AggConfig ac) {
        if (ac == null) {
            return;
        }
        ac.getFilters().forEach(e -> evaluator(e));
        ac.getColumns().forEach(e -> evaluator(e));
        ac.getRows().forEach(e -> evaluator(e));
    }

    private void evaluator(ConfigComponent e) {
        if (e instanceof DimensionConfig) {
            DimensionConfig dc = (DimensionConfig) e;
            dc.setValues(dc.getValues().stream().flatMap(v -> getFilterValue(v)).collect(Collectors.toList()));
        }
        if (e instanceof CompositeConfig) {
            CompositeConfig cc = (CompositeConfig) e;
            cc.getConfigComponents().forEach(_e -> evaluator(_e));
        }
    }

    /**
     * Dashboard传参
     */
    private Stream<String> getFilterValue(String value) {
        List<String> list = new ArrayList<>();
        if (value == null || !(value.startsWith("{") && value.endsWith("}"))) {
            list.add(value);
        } else if ("{loginName}".equals(value)) {
            list.add(authenticationService.getCurrentUser().getUsername());
        } else if ("{userName}".equals(value)) {
            list.add(authenticationService.getCurrentUser().getName());
        } else if ("{userRoles}".equals(value)) {
            List<DashboardRole> roles = roleService.getCurrentRoleList();
            roles.forEach(role -> list.add(role.getRoleName()));
        } else {//FIXME 作用？
            list.add(AviatorEvaluator.compile(value.substring(1, value.length() - 1), true).execute().toString());
        }
        return list.stream();
    }

    public String getLockKey() {
        String dataSourceStr = JSONObject.toJSON(dataSource).toString();
        String queryStr = JSONObject.toJSON(query).toString();
        return Hashing.md5().newHasher().putString(dataSourceStr + queryStr, Charsets.UTF_8).hash().toString();
    }

    //FIXME
    public List<DimensionConfig> filterCCList2DCList(List<ConfigComponent> filters) {
        List<DimensionConfig> result = new LinkedList<>();
        filters.stream().forEach(cc -> {
            result.addAll(configComp2DimConfigList(cc));
        });
        return result;
    }

    public List<DimensionConfig> configComp2DimConfigList(ConfigComponent cc) {
        List<DimensionConfig> result = new LinkedList<>();
        if (cc instanceof DimensionConfig) {
            result.add((DimensionConfig) cc);
        } else {
            Iterator<ConfigComponent> iterator = cc.getIterator();
            while (iterator.hasNext()) {
                ConfigComponent next = iterator.next();
                result.addAll(configComp2DimConfigList(next));
            }
        }
        return result;
    }


    /**
     * 页面配置完成后，测试功能是否可用
     */
    abstract public void test() throws Exception;


    public void setDataSource(Map<String, String> dataSource) {
        this.dataSource = dataSource;
    }

    public void setQuery(Map<String, String> query) {
        this.query = query;
    }


    public void setInterval(long interval) {
        this.interval = interval;
    }

    public boolean isUsedForTest() {
        return isUsedForTest;
    }

    public void setUsedForTest(boolean usedForTest) {
        isUsedForTest = usedForTest;
    }


    /**
     * 对null值得处理，有默认实现；
     */
    public static ConfigComponent separateNull(ConfigComponent configComponent) {
        if (configComponent instanceof DimensionConfig) {
            DimensionConfig cc = (DimensionConfig) configComponent;
            if (("=".equals(cc.getFilterType()) || "≠".equals(cc.getFilterType())) && cc.getValues().size() > 1 &&
                    cc.getValues().stream().anyMatch(s -> DataProvider.NULL_STRING.equals(s))) {
                CompositeConfig compositeConfig = new CompositeConfig();
                compositeConfig.setType("=".equals(cc.getFilterType()) ? "OR" : "AND");
                cc.setValues(cc.getValues().stream().filter(s -> !DataProvider.NULL_STRING.equals(s)).collect(Collectors.toList()));
                compositeConfig.getConfigComponents().add(cc);
                DimensionConfig nullCc = new DimensionConfig();
                nullCc.setColumnName(cc.getColumnName());
                nullCc.setFilterType(cc.getFilterType());
                nullCc.setValues(new ArrayList<>());
                nullCc.getValues().add(DataProvider.NULL_STRING);
                compositeConfig.getConfigComponents().add(nullCc);
                return compositeConfig;
            }
        }
        return configComponent;
    }

}
