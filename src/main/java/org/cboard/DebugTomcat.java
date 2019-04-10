package org.cboard;

import org.apache.catalina.Context;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ErrorPage;

import java.io.File;

/**
 * Created by zhongjian on 11/17/16.
 */
public class DebugTomcat {
    private static final String CONTEXT_PATH = "";//根目录不是"/"而是""
    private static final int PORT = 7090;//端口
    private static final String PROTOCOL = "org.apache.coyote.http11.Http11Nio2Protocol";


    public static void main(String[] args) throws Exception {

        String webBase = new File("src/main/webapp").getAbsolutePath();
        Tomcat tomcat = new Tomcat();
        //tomcat.setPort(port);
        tomcat.setBaseDir(".");

//Server
        Server server = tomcat.getServer();
        // Add AprLifecycleListener（默认AprLifecycleListener）
        AprLifecycleListener listener = new AprLifecycleListener();
        server.addLifecycleListener(listener);

//Context
        Context webContext = tomcat.addWebapp(CONTEXT_PATH, webBase);
        ErrorPage notFound = new ErrorPage();
        notFound.setErrorCode(404);
        notFound.setLocation("/main.html");
        webContext.addErrorPage(notFound);
        webContext.addWelcomeFile("main.html");

//配置Connector(ProtocolHandler)：
        Connector httpConnector = new Connector(PROTOCOL);
        httpConnector.setPort(PORT);
        //
        Service service =tomcat.getService();
        service.addConnector(httpConnector);

// tomcat start
        tomcat.getConnector();/* Tomcat9特有：触发创建默认的connector */
        tomcat.start();
        server.await();
    }

}
