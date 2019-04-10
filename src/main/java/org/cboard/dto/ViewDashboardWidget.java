package org.cboard.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cboard.pojo.DashboardWidget;
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
public class ViewDashboardWidget {

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

    public static final Function TO = new Function<DashboardWidget, ViewDashboardWidget>() {
        @Nullable
        @Override
        public ViewDashboardWidget apply(@Nullable DashboardWidget input) {
            return new ViewDashboardWidget(input);
        }
    };

    public ViewDashboardWidget(DashboardWidget widget) {
        this.id = widget.getId();
        this.userId = widget.getUserId();
        this.name = widget.getName();
        this.categoryName = widget.getCategoryName();
        this.userName = widget.getUserName();
        this.loginName = widget.getLoginName();
        this.createTime = widget.getCreateTime().toString();
        this.updateTime = widget.getUpdateTime().toString();
        this.data = JSONObject.parseObject(widget.getData());
        this.edit = RolePermission.isEdit(widget.getPermission());
        this.delete = RolePermission.isDelete(widget.getPermission());
    }


}
