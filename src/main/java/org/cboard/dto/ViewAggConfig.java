package org.cboard.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cboard.dataprovider.config.AggConfig;
import org.cboard.dataprovider.config.ConfigComponent;
import org.cboard.dataprovider.config.DimensionConfig;
import org.cboard.dataprovider.config.ValueConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yfyuan on 2017/4/27.
 */
@Getter
@Setter
@ToString
public class ViewAggConfig {

    private List<DimensionConfig> rows;
    private List<DimensionConfig> columns;
    private List<DimensionConfig> filters;
    private List<ValueConfig> values;

    public static AggConfig getAggConfig(ViewAggConfig viewAggConfig) {
        if (viewAggConfig != null) {
            AggConfig aggConfig = new AggConfig();
            aggConfig.setRows(viewAggConfig.getRows());
            aggConfig.setColumns(viewAggConfig.getColumns());
            if (viewAggConfig.getFilters() != null) {
                aggConfig.setFilters(viewAggConfig.getFilters().stream().map(e -> (ConfigComponent) e).collect(Collectors.toList()));
            } else {
                aggConfig.setFilters(new ArrayList<>());
            }
            aggConfig.setValues(viewAggConfig.getValues());
            return aggConfig;
        }
        return null;
    }


}
