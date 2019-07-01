package org.cboard.dataprovider.config;

import lombok.Data;

import java.util.List;

/**
 * （对应"数据集管理"页面）维度定义列，包括列名，过滤条件（类型，值），重命名；
 */
@Data
public class DimensionConfig extends ConfigComponent {
    private String columnName;
    private String filterType;
    private List<String> values;
    private String id;
    private String custom; //定制语句  FIXME 作用？

}
