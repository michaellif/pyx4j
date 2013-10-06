/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on 2011-01-13
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.jetty;

import java.io.File;
import java.net.ServerSocket;
import java.util.TimeZone;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.rewrite.handler.RedirectPatternRule;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

public abstract class JettyLaunch {

    public abstract String getContextPath();

    public int getServerPort() {
        return 8080;
    }

    /**
     * <pre>
     *  keytool -genkey -keystore jetty.keystore -keyalg rsa -alias  jetty -storepass 123456 -keypass 123456 -keyalg RSA -keysize 1024 -validity 4386 -dname "cn=pyx4j Team, ou=Testing, o=pyx4j, c=CA"
     * </pre>
     */
    public int getServerSslPort() {
        return 0;
    }

    public String getWarResourceBase() {
        return "src/main/webapp";
    }

    public String getHashLoginServiceConfig() {
        return "jetty-realm.properties";
    }

    /**
     * @see http://wiki.eclipse.org/Jetty/Tutorial/RequestLog#Configuring_Request_Log
     */
    public String getRequestLogFile() {
        return "./logs/jetty.request.log";
    }

    protected String getSessionCookiePath() {
        return "/";
    }

    /**
     * @return the max age to set on the session cookie, in seconds
     */
    protected int getSessionMaxAge() {
        return 60 * 60;
    }

    public boolean isRunningInDeveloperEnviroment() {
        return true;
    }

    /**
     * Override to do custom configuration
     */
    protected void configure(WebAppContext webAppContext) {

    }

    public static void launch(JettyLaunch jettyLaunch) throws Exception {
        jettyLaunch.configureAndStart();
    }

    protected final void configureAndStart() throws Exception {
        int port = getServerPort();
        //see if port is available
        try {
            ServerSocket s = new ServerSocket(port);
            s.close();
        } catch (Exception e) {
            throw new RuntimeException("Port already in use", e);
        }

        if (isRunningInDeveloperEnviroment()) {
            System.setProperty("com.pyx4j.DeveloperEnviroment", Boolean.TRUE.toString());
        }

        Server server = new Server(port);

        if (getServerSslPort() != 0) {
            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setKeyStorePassword("123456");
            sslContextFactory.setKeyStoreType("JKS");
            sslContextFactory.setKeyStorePath("./src/test/ssl/jetty.keystore");
            sslContextFactory.setKeyManagerPassword("123456");

            ServerConnector connector = new ServerConnector(server, sslContextFactory);
            connector.setPort(getServerSslPort());

            server.addConnector(connector);
        }

        HandlerList handlers = new HandlerList();

        if (getRequestLogFile() != null) {
            File logFile = new File(getRequestLogFile());
            if (!logFile.getParentFile().isDirectory()) {
                logFile.getParentFile().mkdirs();
            }

            NCSARequestLog requestLog = new NCSARequestLog(getRequestLogFile());
            requestLog.setRetainDays(1);
            requestLog.setAppend(true);
            requestLog.setExtended(true);
            requestLog.setLogTimeZone(TimeZone.getDefault().getID());
            RequestLogHandler requestLogHandler = new RequestLogHandler();
            requestLogHandler.setRequestLog(requestLog);
            handlers.addHandler(requestLogHandler);
        }

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setDescriptor(getWarResourceBase() + "/WEB-INF/web.xml");
        webAppContext.setContextPath(getContextPath());
        webAppContext.setParentLoaderPriority(true);
        webAppContext.getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
        webAppContext.setResourceBase(getWarResourceBase());

        webAppContext.setConfigurations(new Configuration[] { new WebInfConfiguration(), new WebXmlConfiguration(), new AnnotationConfiguration() });

        webAppContext.setConfigurationDiscovered(false);

        if (getSessionCookiePath() != null) {
            webAppContext.getSessionHandler().getSessionManager().getSessionCookieConfig().setPath(getSessionCookiePath());
        }
        webAppContext.getSessionHandler().getSessionManager().getSessionCookieConfig().setMaxAge(getSessionMaxAge());

        if (getHashLoginServiceConfig() != null) {
            webAppContext.getSecurityHandler().setLoginService(new HashLoginService("default", getHashLoginServiceConfig()));
        }

        configure(webAppContext);

        handlers.addHandler(webAppContext);

        //handle default /
        RewriteHandler rewrite = new RewriteHandler();
        rewrite.setRewriteRequestURI(false);

        RedirectPatternRule redirect = new RedirectPatternRule();
        redirect.setPattern("/");
        redirect.setLocation(getContextPath());
        rewrite.addRule(redirect);
        handlers.addHandler(rewrite);

        server.setHandler(handlers);

        server.start();
        server.join();
    }

}
