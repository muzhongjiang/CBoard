package org.cboard.dataprovider.config;

import lombok.Data;

import java.util.List;

/**
 * 一个图表对应的数据集描述，包括行，列，指标列，过滤条件；
 */
@Data
public class AggConfig {

    private List<DimensionConfig> rows;//行
    private List<DimensionConfig> columns;//列
    private List<ConfigComponent> filters;//过滤条件
    private List<ValueConfig> values;//指标列


}
