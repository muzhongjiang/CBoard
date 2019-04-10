package org.cboard.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cboard.pojo.DashboardRoleRes;
import org.cboard.services.role.RolePermission;

/**
 * Created by yfyuan on 2017/3/14.
 */
@Getter
@Setter
@ToString
public class ViewDashboardRoleRes {

    private Long roleResId;
    private String roleId;
    private Long resId;
    private String resType;
    private boolean edit;
    private boolean delete;


    public ViewDashboardRoleRes(DashboardRoleRes dashboardRoleRes) {
        this.roleResId = dashboardRoleRes.getRoleResId();
        this.roleId = dashboardRoleRes.getRoleId();
        this.resId = dashboardRoleRes.getResId();
        this.resType = dashboardRoleRes.getResType();
        this.edit = RolePermission.isEdit(dashboardRoleRes.getPermission());
        this.delete = RolePermission.isDelete(dashboardRoleRes.getPermission());
    }


}
