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
public class DashboardDatasource {

    private Long id;
    private String userId;
    private String name;
    private String type;
    private String config;
    private String permission;
    private String userName;
    private String loginName;
    private Timestamp createTime;
    private Timestamp updateTime;


}
