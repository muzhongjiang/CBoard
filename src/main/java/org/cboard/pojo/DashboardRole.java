package org.cboard.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by yfyuan on 2016/12/6.
 */
@Getter
@Setter
@ToString
public class DashboardRole {
    private String roleId;
    private String roleName;
    private String userId;

    public String getRoleId() {
        return roleId;
    }


}
