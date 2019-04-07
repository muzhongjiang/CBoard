package org.cboard.dataprovider.aggregator;

import org.cboard.dataprovider.config.AggConfig;
import org.cboard.dataprovider.result.AggregateResult;

/**
 * Created by yfyuan on 2017/1/13.
 */
public interface Aggregatable {

    /**
     * 获取某一维度过滤后的值（支持DataSource端聚合的数据提供程序必须实现此方法。）
     *
     * @param columnName
     * @return
     */
    String[] queryDimVals(String columnName, AggConfig config) throws Exception;

    /**
     * 支持DataSource端聚合的数据提供程序必须实现此方法。
     *
     * @return
     */
    String[] getColumn() throws Exception;

    /**
     * 获取聚合后的数据集（支持DataSource端聚合的数据提供程序必须实现此方法。）
     *
     * @param ac aggregate configuration
     * @return
     */
    AggregateResult queryAggData(AggConfig ac) throws Exception;


    /**
     * '预览查询'即：显示最终的sql
     */
    default String viewAggDataQuery(AggConfig ac) throws Exception {
        return "Not Support";
    }

}
