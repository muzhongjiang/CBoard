package org.cboard.dto;

import com.alibaba.fastjson.JSON;

import java.util.Date;

/**
 * "用户操作日志"实体类
 */
public class CBoardActionLog {

    private User user;
    private String requestUrl;
    private Date actionTime = new Date();
    private String ip;

    public CBoardActionLog() {}

    public CBoardActionLog(User user, String requestUrl,String ip) {
        this.user = user;
        this.requestUrl = requestUrl;
        this.ip=ip;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getIp() {
        return ip;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public Date getActionTime() {
        return actionTime;
    }

    public void setActionTime(Date actionTime) {
        this.actionTime = actionTime;
    }

    @Override
    public String toString() {
        return JSON.toJSONStringWithDateFormat(this, "yyyy-MM-dd HH:mm:ss.SSS");
    }
}
