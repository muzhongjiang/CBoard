package org.cboard.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * Created by yfyuan on 2016/8/18.
 */
@Getter
@Setter
@ToString
public class DashboardBoard {

    private Long id;
    private String userId;
    private Long categoryId;
    private String name;
    private String layout;
    private String categoryName;
    private String userName;
    private String loginName;
    private String permission;
    private Timestamp createTime;
    private Timestamp updateTime;


}
