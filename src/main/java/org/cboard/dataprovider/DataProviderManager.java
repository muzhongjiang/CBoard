package org.cboard.dataprovider;


import org.cboard.dataprovider.annotation.DatasourceParameter;
import org.reflections.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 管理所有 DataProvider（JdbcDataProvider、ElasticsearchDataProvider、KylinDataProvider）
 */
@Component
public class DataProviderManager implements ApplicationContextAware {

    private final Logger LOG = LoggerFactory.getLogger(DataProviderManager.class);
    private ApplicationContext applicationContext;

    /**
     * 获取所有DataProvider的实现类的ProviderName（Elasticsearch、jdbc、kylin）
     */
    public String[] getProviderList() {
        //所有的DataProvider名称（spring bean名称）:
        return applicationContext.getBeanNamesForType(DataProvider.class);
    }

    /**
     * 根据ProviderName获取DataProvider对象
     *
     * @param type ProviderName
     */
    public DataProvider getProviderByName(String type) {
        return applicationContext.getBean(type, DataProvider.class);
    }


    /**
     * 获取对应的DataProvider对象
     *
     * @param type       ProviderName
     * @param dataSource 数据源
     * @param query
     */
    public DataProvider getDataProvider(
            String type,
            Map<String, String> dataSource,
            Map<String, String> query
    ) throws Exception {
        return getDataProvider(type, dataSource, query, false);
    }

    /**
     * 创建DataProvider对象（JdbcDataProvider、ElasticsearchDataProvider、KylinDataProvider）
     *
     * @param type       ProviderName
     * @param dataSource 数据源
     * @param query
     */
    public DataProvider getDataProvider(
            String type,
            Map<String, String> dataSource,
            Map<String, String> query,
            boolean isUseForTest) throws Exception {

        DataProvider provider =this.getProviderByName(type);

        if (provider != null) {
            provider.setQuery(query);//数据集（cube）对应sql
            provider.setDataSource(dataSource);//数据源配置项
            provider.setUsedForTest(isUseForTest);
            if (provider instanceof Initializing) {
                ((Initializing) provider).afterPropertiesSet();
            }
            return provider;
        }
        return null;
    }

    /**
     * 获取指定DataProvider的指定类型DatasourceParameter对应字段（比如：username、password）
     *
     * @param type                    ProviderName
     * @param dataSourceParameterType DatasourceParameter的Type枚举（比如：username、password）
     */
    public List<String> getProviderFieldByType(String type, DatasourceParameter.Type dataSourceParameterType) {
        DataProvider provider = this.getProviderByName(type);
        Set<Field> fieldSet = ReflectionUtils.getAllFields(provider.getClass(), ReflectionUtils.withAnnotation(DatasourceParameter.class));
        return fieldSet.stream().filter(e ->
                e.getAnnotation(DatasourceParameter.class).type().equals(dataSourceParameterType)
        ).map(e -> {
            try {
                e.setAccessible(true);
                return e.get(provider).toString();
            } catch (IllegalAccessException e1) {
                LOG.error("{}", e);
            }
            return null;
        }).collect(Collectors.toList());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
