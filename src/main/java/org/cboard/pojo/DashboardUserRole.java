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
public class DashboardUserRole {
    private Long userRoleId;
    private String userId;
    private String roleId;

    public Long getUserRoleId() {
        return userRoleId;
    }


}
