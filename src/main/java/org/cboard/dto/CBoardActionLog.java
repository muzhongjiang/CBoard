package org.cboard.dto;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * "用户操作日志"实体类
 */
@Getter
@Setter
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


    @Override
    public String toString() {
        return JSON.toJSONStringWithDateFormat(this, "yyyy-MM-dd HH:mm:ss.SSS");
    }
}
