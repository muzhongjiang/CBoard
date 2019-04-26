package org.cboard.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

/**
 * Created by yfyuan on 2016/10/11.
 */
@Getter
@Setter
@ToString
public class DashboardDataset {

    private Long id;
    private String userId;
    private String name;
    private String categoryName;
    private String data;
    private String permission;
    private String userName;
    private String loginName;
    private Timestamp createTime;
    private Timestamp updateTime;


}
