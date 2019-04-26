package org.cboard.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by yfyuan on 2016/12/2.
 */
@Getter
@Setter
@ToString
public class DashboardUser {
    private String userId;
    private String loginName;
    private String userName;
    private String userPassword;
    private String userStatus;


}
