package org.cboard.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by yfyuan on 2016/12/7.
 */
@Getter
@Setter
@ToString
public class DashboardRoleRes {
    private Long roleResId;
    private String roleId;
    private Long resId;
    private String resType;
    private String permission;


}
