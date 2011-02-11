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

import org.eclipse.jetty.rewrite.handler.RedirectPatternRule;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.webapp.WebAppContext;

public abstract class JettyLaunch {

    public abstract String getContextPath();

    public int getServerPort() {
        return 8080;
    }

    public String getWarResourceBase() {
        return "src/main/webapp";
    }

    public String getHashLoginServiceConfig() {
        return "jetty-realm.properties";
    }

    public static void launch(JettyLaunch jettyLaunch) throws Exception {

        Server server = new Server(jettyLaunch.getServerPort());

        RewriteHandler rewrite = new RewriteHandler();
        rewrite.setRewriteRequestURI(false);

        RedirectPatternRule redirect = new RedirectPatternRule();
        redirect.setPattern("/");
        redirect.setLocation(jettyLaunch.getContextPath());
        rewrite.addRule(redirect);

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setDescriptor(jettyLaunch.getWarResourceBase() + "/WEB-INF/web.xml");
        webAppContext.setContextPath(jettyLaunch.getContextPath());
        webAppContext.setParentLoaderPriority(true);
        webAppContext.getInitParams().put("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");
        webAppContext.setResourceBase(jettyLaunch.getWarResourceBase());

        if (jettyLaunch.getHashLoginServiceConfig() != null) {
            webAppContext.getSecurityHandler().setLoginService(new HashLoginService("default", jettyLaunch.getHashLoginServiceConfig()));
        }

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { webAppContext, rewrite });
        server.setHandler(handlers);

        server.start();
        server.join();
    }
}
