package org.cboard.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * Created by yfyuan on 2017/2/16.
 */
@Getter
@Setter
@ToString
public class DashboardJob {

    private Long id;
    private String name;
    private String cronExp;
    private Date startDate;
    private Date endDate;
    private String jobType;
    private String config;
    private String userId;
    private String userName;
    private Date lastExecTime;
    private Long jobStatus;
    private String execLog;
    private String permission;

}
