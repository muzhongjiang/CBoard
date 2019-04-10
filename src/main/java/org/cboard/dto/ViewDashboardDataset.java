package org.cboard.dto;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cboard.pojo.DashboardDataset;
import org.cboard.services.role.RolePermission;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.Map;

/**
 * Created by yfyuan on 2016/10/11.
 */
@Getter
@Setter
@ToString
public class ViewDashboardDataset {
    private Long id;
    private String userId;
    private String name;
    private String categoryName;
    private String userName;
    private String loginName;
    private String createTime;
    private String updateTime;
    private Map<String, Object> data;
    private boolean edit;
    private boolean delete;


    public static final Function TO = new Function<DashboardDataset, ViewDashboardDataset>() {
        @Nullable
        @Override
        public ViewDashboardDataset apply(@Nullable DashboardDataset input) {
            return new ViewDashboardDataset(input);
        }
    };

    public ViewDashboardDataset(DashboardDataset dataset) {
        this.id = dataset.getId();
        this.userId = dataset.getUserId();
        this.name = dataset.getName();
        this.userName = dataset.getUserName();
        this.categoryName = dataset.getCategoryName();
        this.loginName = dataset.getLoginName();
        this.createTime = dataset.getCreateTime().toString();
        this.updateTime = dataset.getUpdateTime().toString();
        this.data = JSONObject.parseObject(dataset.getData());
        this.edit = RolePermission.isEdit(dataset.getPermission());
        this.delete = RolePermission.isDelete(dataset.getPermission());
    }


}
