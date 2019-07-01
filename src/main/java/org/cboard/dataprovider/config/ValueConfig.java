package org.cboard.dataprovider.config;

import lombok.Data;

/**
 * 指标列定义，包括列名和聚合运算类型
 */
@Data
public class ValueConfig {
    private String column;
    private String aggType;



}

