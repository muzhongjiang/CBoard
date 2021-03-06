package org.cboard.controller;

import org.apache.commons.lang3.StringUtils;
import org.cboard.dataprovider.NetworkUtils;
import org.cboard.dto.CBoardActionLog;
import org.cboard.dto.User;
import org.cboard.services.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * Controller共有功能
 */
public class BaseController {

    protected  Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected AuthenticationService authenticationService;

    protected ThreadLocal<User> tlUser = new ThreadLocal<>();

    @Value("${log.negativeFilter}")
    protected String negativeFilter;

    @Value("${log.positveFilter}")
    protected String positveFilter;


    /**
     * 打印操作日志（记录用户请求的url链接）
     */
    @ModelAttribute
    public void initialAuthUser(HttpServletRequest request) {
        String url = request.getRequestURL().toString();
        User user = authenticationService.getCurrentUser();//????
        tlUser.set(user);
        String ip= NetworkUtils.getIPAddress(request);
        String log = new CBoardActionLog(user, url,ip).toString();

        boolean isNegtiveMatch = false;
        boolean isPositveMatch = true;

        if (StringUtils.isNotBlank(positveFilter)) {
            isPositveMatch = Pattern.compile(positveFilter).matcher(log).find();
        }

        if (StringUtils.isNotBlank(negativeFilter)) {
            isNegtiveMatch = Pattern.compile(negativeFilter).matcher(log).find();
        }

        if (user != null && !isNegtiveMatch && isPositveMatch) {
            LOG.info(log); //FIXME 操作日志存到DB中
        }
    }

}
