package org.cboard.dataprovider.config;

/**
 * 指标列定义，包括列名和聚合运算类型
 */
public class ValueConfig {
    private String column;
    private String aggType;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getAggType() {
        return aggType;
    }

    public void setAggType(String aggType) {
        this.aggType = aggType;
    }
}

