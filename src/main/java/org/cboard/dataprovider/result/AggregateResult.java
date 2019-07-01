package org.cboard.dataprovider.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Result类的实现相对简单，为了通用所有数据源，结果的形式为ColumnIndex(列定义)和String[][]二维数据；
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AggregateResult {

    private List<ColumnIndex> columnList; //LinkedList!!!
    private String[][] data; //二维(多行数据)

}
