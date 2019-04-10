package org.cboard.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cboard.pojo.DashboardDatasource;
import com.google.common.base.Function;
import org.cboard.services.role.RolePermission;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by yfyuan on 2016/8/19.
 */
@Getter
@Setter
@ToString
public class ViewDashboardDatasource {

    private Long id;
    private String userId;
    private String name;
    private String type;
    private Map<String, Object> config;
    private boolean edit;
    private boolean delete;
    private String userName;
    private String loginName;
    private String createTime;
    private String updateTime;

    public static final Function TO = new Function<DashboardDatasource, ViewDashboardDatasource>() {
        @Nullable
        @Override
        public ViewDashboardDatasource apply(@Nullable DashboardDatasource input) {
            return new ViewDashboardDatasource(input);
        }
    };

    public ViewDashboardDatasource(DashboardDatasource datasource) {
        this.id = datasource.getId();
        this.userId = datasource.getUserId();
        this.name = datasource.getName();
        this.type = datasource.getType();
        this.config = JSONObject.parseObject(datasource.getConfig());
        this.edit = RolePermission.isEdit(datasource.getPermission());
        this.delete = RolePermission.isDelete(datasource.getPermission());
        this.userName = datasource.getUserName();
        this.loginName = datasource.getLoginName();
        this.createTime = datasource.getCreateTime().toString();
        this.updateTime = datasource.getUpdateTime().toString();
    }

}
