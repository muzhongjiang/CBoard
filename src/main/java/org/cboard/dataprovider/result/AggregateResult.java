package org.cboard.dataprovider.result;

import java.util.List;

/**
 * Result类的实现相对简单，为了通用所有数据源，结果的形式为ColumnIndex(列定义)和String[][]二维数据；
 */
public class AggregateResult {
    private List<ColumnIndex> columnList;
    private String[][] data;

    public AggregateResult(List<ColumnIndex> columnList, String[][] data) {
        this.columnList = columnList;
        this.data = data;
    }

    public AggregateResult() {
    }

    public List<ColumnIndex> getColumnList() {
        return columnList;
    }

    public void setColumnList(List<ColumnIndex> columnList) {
        this.columnList = columnList;
    }

    public String[][] getData() {
        return data;
    }

    public void setData(String[][] data) {
        this.data = data;
    }
}
