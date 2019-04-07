package org.cboard;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ErrorPage;

import java.io.File;

/**
 * Created by zhongjian on 11/17/16.
 */
public class DebugTomcat {

    public static void main(String[] args) throws Exception {

        int port = 7090;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }

        String webBase = new File("src/main/webapp").getAbsolutePath();
        Tomcat tomcat = new Tomcat();
//        tomcat.setPort(port);
        tomcat.setBaseDir(".");

        // Add AprLifecycleListener（默认AprLifecycleListener）
//        StandardServer server = (StandardServer) tomcat.getServer();
//        AprLifecycleListener listener = new AprLifecycleListener();
//        server.addLifecycleListener(listener);

        Context webContext = tomcat.addWebapp("/", webBase);
        ErrorPage notFound = new ErrorPage();
        notFound.setErrorCode(404);
        notFound.setLocation("/main.html");
        webContext.addErrorPage(notFound);
        webContext.addWelcomeFile("main.html");

        //配置Connector：
        Connector httpConnector = new Connector("org.apache.coyote.http11.Http11Nio2Protocol");
        httpConnector.setPort(port);
        tomcat.getService().addConnector(httpConnector);

        // tomcat start
        tomcat.getConnector();//Tomcat9特有：触发创建默认的connector
        tomcat.start();
        tomcat.getServer().await();
    }

}
