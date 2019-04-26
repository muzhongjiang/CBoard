package org.cboard.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * Created by yfyuan on 2016/8/22.
 */
@Getter
@Setter
@ToString
public class DashboardWidget {

    private Long id;
    private String userId;
    private String name;
    private String categoryName;
    private String userName;
    private String loginName;
    private String data;
    private String permission;
    private Timestamp createTime;
    private Timestamp updateTime;


}
