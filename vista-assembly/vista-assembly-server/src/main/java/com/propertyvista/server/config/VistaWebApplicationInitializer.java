/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 3, 2013
 * @author vlads
 */
package com.propertyvista.server.config;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.wicket.protocol.http.ContextParamWebApplicationFactory;
import org.apache.wicket.protocol.http.WicketFilter;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;

import com.sun.xml.ws.transport.http.servlet.WSServlet;

import com.pyx4j.config.server.ApplicationVersionServlet;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.servlet.InitializationServletContextListener;
import com.pyx4j.essentials.server.admin.DeployVerificationServlet;
import com.pyx4j.essentials.server.dev.DebugRequestEchoServlet;
import com.pyx4j.essentials.server.dev.DebugServlet;
import com.pyx4j.essentials.server.dev.DevDumpProxyServlet;
import com.pyx4j.essentials.server.dev.OutOfMemorySimulationServlet;
import com.pyx4j.essentials.server.download.DownloadServlet;
import com.pyx4j.gwt.server.GWTCacheFilter;
import com.pyx4j.log4j.LoggerConfig;
import com.pyx4j.rpc.server.RemoteServiceServlet;
import com.pyx4j.server.contexts.DeploymentContextFilter;
import com.pyx4j.server.contexts.LifecycleFilter;

import com.propertyvista.biz.system.AuditSessionListener;
import com.propertyvista.config.BankingSimulatorConfiguration;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.ils.ILSAuthFilter;
import com.propertyvista.ils.kijiji.rs.KijijiApiRsApplication;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.oapi.v1.rs.OapiRsApplication;
import com.propertyvista.oapi.v1.rs.wadl.OapiWadlGeneratorConfig;
import com.propertyvista.operations.server.services.simulator.CardServiceSimulationServlet;
import com.propertyvista.operations.server.servlet.VistaConfigInfoServlet;
import com.propertyvista.operations.server.servlet.VistaStackTraceViewServlet;
import com.propertyvista.pmsite.server.PMSiteApplication;
import com.propertyvista.pmsite.server.PMSiteFilter;
import com.propertyvista.pmsite.server.PMSiteRobotsTxtFilter;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.server.portal.PublicMediaServlet;
import com.propertyvista.portal.server.upload.SiteImageResourceServlet;
import com.propertyvista.portal.server.upload.VistaFileAccessServlet;
import com.propertyvista.server.VistaLogViewServlet;
import com.propertyvista.server.VistaUploadServlet;
import com.propertyvista.server.ci.DBResetServlet;
import com.propertyvista.server.ci.EnvLinksServlet;
import com.propertyvista.server.ci.TestTimeoutServlet;
import com.propertyvista.server.ci.VistaStatusServlet;
import com.propertyvista.server.ci.bugs.WSServletContextListenerFix;
import com.propertyvista.server.config.filter.VistaApplicationContextDispatcherFilter;
import com.propertyvista.server.oapi.OAPIFilter;
import com.propertyvista.server.security.RobotsFilter;
import com.propertyvista.server.security.idp.IdpEndpointServlet;
import com.propertyvista.server.security.idp.IdpXrdsServlet;
import com.propertyvista.server.security.openId.OpenIdFilter;
import com.propertyvista.server.security.openId.OpenIdServlet;
import com.propertyvista.server.security.openId.VistaDevSessionServlet;

/**
 * This is refactorable web.xml replacement
 */
public class VistaWebApplicationInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {

        // Initialize log first
        String configContextName = InitializationServletContextListener.getContextName(ctx);
        LoggerConfig.setContextName(configContextName);

        ctx.setInitParameter(ServerSideConfiguration.class.getName(), VistaServerSideConfiguration.class.getName());
        ctx.addListener(VistaInitializationServletContextListener.class);
        ctx.addListener(AuditSessionListener.class);

        // Understands URLs of our forwarding proxy server
        {
            FilterRegistration.Dynamic fc = ctx.addFilter("DeploymentContextFilter", DeploymentContextFilter.class);
            fc.setInitParameter(DeploymentContextFilter.PARAM_developmentDebugResponse, "false");
            fc.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
        }

        // URLs mapping to application
        {
            FilterRegistration.Dynamic fc = ctx.addFilter("VistaApplicationContextDispatcherFilter", VistaApplicationContextDispatcherFilter.class);
            fc.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
        }

        // TODO make them handle only forwarded requested once transition complete.
        EnumSet<DispatcherType> appFiltersDispatcherTypes = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
        {
            FilterRegistration.Dynamic fc = ctx.addFilter("GWTCacheFilter", GWTCacheFilter.class);
            fc.setInitParameter(GWTCacheFilter.PARAM_cacheExpiresHours, "20");
            fc.addMappingForUrlPatterns(appFiltersDispatcherTypes, true, "/*");
        }

        {
            FilterRegistration.Dynamic fc = ctx.addFilter("RobotsFilter", RobotsFilter.class);
            fc.addMappingForUrlPatterns(appFiltersDispatcherTypes, true, "/*");
        }

        {
            FilterRegistration.Dynamic fc = ctx.addFilter("LifecycleFilter", LifecycleFilter.class);
            fc.addMappingForUrlPatterns(appFiltersDispatcherTypes, true, "/*");
        }

        // Open Id; client and server
        {
            {
                FilterRegistration.Dynamic fc = ctx.addFilter("OpenIdFilter", OpenIdFilter.class);
                fc.addMappingForUrlPatterns(appFiltersDispatcherTypes, true, "/*");
            }

            {
                ServletRegistration.Dynamic sc = ctx.addServlet("OpenIdServlet", OpenIdServlet.class);
                sc.addMapping(OpenIdServlet.MAPPING);
                sc.addMapping(allApplicationsUrlPatterns(OpenIdServlet.MAPPING));
            }
            // QA Hack
            {
                ServletRegistration.Dynamic sc = ctx.addServlet("VistaDevSessionServlet", VistaDevSessionServlet.class);
                sc.addMapping("/o/sim-sim-otkroisa");
                sc.addMapping(allApplicationsUrlPatterns("/o/sim-sim-otkroisa"));
            }

            // Server
            {
                ServletRegistration.Dynamic sc = ctx.addServlet("IdpXrdsServlet", IdpXrdsServlet.class);
                sc.addMapping("/static/accounts/idp");
            }
            {
                ServletRegistration.Dynamic sc = ctx.addServlet("IdpEndpointServlet", IdpEndpointServlet.class);
                sc.addMapping("/static/accounts/endpoint");
            }

        }

        // Site
        {
            {
                FilterRegistration.Dynamic fc = ctx.addFilter("PMSiteRobotsTxtFilter", PMSiteRobotsTxtFilter.class);
                fc.addMappingForUrlPatterns(appFiltersDispatcherTypes, true, urlPattern(VistaApplication.site, "/robots.txt"));
            }
            {
                FilterRegistration.Dynamic fc = ctx.addFilter("PMSiteFilter", PMSiteFilter.class);
                fc.addMappingForUrlPatterns(appFiltersDispatcherTypes, true, urlPattern(VistaApplication.site, "/*"));
                fc.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, urlPattern(VistaApplication.site, "/*"));
                fc.setInitParameter(ContextParamWebApplicationFactory.APP_CLASS_PARAM, PMSiteApplication.class.getName());
                // TODO use java constants in the code
                fc.setInitParameter(PMSiteFilter.IGNORE_URLS_PARAM,
                        "/robots.txt,/favicon.ico,/sitegwt/\\w*,/vista/https/*,/media/*,/o/*,*.siteimgrc,/debug/*,/echo/*");
            }

            {
                ServletRegistration.Dynamic sc = ctx.addServlet("SiteImageResourceServlet", SiteImageResourceServlet.class);
                sc.addMapping("*.siteimgrc");
            }
        }

        // oapi
        {
            {
                FilterRegistration.Dynamic fc = ctx.addFilter("OAPIFilter", OAPIFilter.class);
                fc.addMappingForUrlPatterns(appFiltersDispatcherTypes, true, urlPattern(VistaApplication.interfaces, "oapi/v1/*"));
            }

            if (!VistaTODO.removedForProductionOAPI) {
                // TODO avoid WSServletContainerInitializer invocation, it produces MemoryLeaks
                // We used web.xml
                if (false) {
                    ctx.addListener(WSServletContextListenerFix.class);
                }
                {
                    ServletRegistration.Dynamic sc = ctx.addServlet("OpenApiWsService", WSServlet.class);
                    sc.addMapping(urlPattern(VistaApplication.interfaces, "oapi/v1/ws/*"));
                }

                {
                    ServletRegistration.Dynamic sc = ctx.addServlet("OpenApiRsService", ServletContainer.class);
                    sc.addMapping(urlPattern(VistaApplication.interfaces, "oapi/v1/rs/*"));
                    sc.setInitParameter("javax.ws.rs.Application", OapiRsApplication.class.getName());
                    sc.setInitParameter(ServerProperties.WADL_GENERATOR_CONFIG, OapiWadlGeneratorConfig.class.getName());
                }
            }

            {
                FilterRegistration.Dynamic fc = ctx.addFilter("ILSAuthFilter", ILSAuthFilter.class);
                fc.addMappingForUrlPatterns(appFiltersDispatcherTypes, true, "/interfaces/ils/*");
            }
            {
                ServletRegistration.Dynamic sc = ctx.addServlet("ILSKijijiService", ServletContainer.class);
                sc.addMapping(urlPattern(VistaApplication.interfaces, "ils/kijiji/*"));
                sc.setInitParameter("javax.ws.rs.Application", KijijiApiRsApplication.class.getName());
            }

        }

        // Main GWT Entry point
        {
            ServletRegistration.Dynamic sc = ctx.addServlet("RemoteServiceServlet", RemoteServiceServlet.class);
            sc.addMapping(allApplicationsUrlPatterns("/srv/*"));
            sc.addMapping(urlPattern(VistaApplication.site, "/sitegwt/srv/*"));
        }
        {
            ServletRegistration.Dynamic sc = ctx.addServlet("DownloadServlet", DownloadServlet.class);
            sc.addMapping(allApplicationsUrlPatterns(DeploymentConsts.downloadServletMapping + "*"));
        }
        {
            ServletRegistration.Dynamic sc = ctx.addServlet("VistaUploadServlet", VistaUploadServlet.class);
            sc.addMapping(allApplicationsUrlPatterns(DeploymentConsts.uploadServletMapping + "*"));
        }

        {
            ServletRegistration.Dynamic sc = ctx.addServlet("PublicMediaServlet", PublicMediaServlet.class);
            sc.addMapping(allApplicationsUrlPatterns(DeploymentConsts.mediaImagesServletMapping + "*"));
        }

        // Special downloads
        {
            ServletRegistration.Dynamic sc = ctx.addServlet("VistaFileAccessServlet", VistaFileAccessServlet.class);
            sc.addMapping(urlPattern(VistaApplication.crm, DeploymentConsts.FILE_SERVLET_MAPPING + "*"));
            sc.addMapping(urlPattern(VistaApplication.resident, DeploymentConsts.FILE_SERVLET_MAPPING + "*"));
            sc.addMapping(urlPattern(VistaApplication.prospect, DeploymentConsts.FILE_SERVLET_MAPPING + "*"));
        }

        // Development environment
        {
            {
                ServletRegistration.Dynamic sc = ctx.addServlet("CardServiceSimulationServlet", CardServiceSimulationServlet.class);
                sc.addMapping(urlPattern(VistaApplication.interfaces, BankingSimulatorConfiguration.cardServiceSimulatorServletMapping + "*"));
            }
        }

        // CI part of application
        {
            {
                ServletRegistration.Dynamic sc = ctx.addServlet("EnvLinksServlet", EnvLinksServlet.class);
                sc.addMapping(urlPattern(VistaApplication.env, "/"));
                sc.addMapping(urlPattern(VistaApplication.env, "/index.html")); // <!-- this works in Tomcat
            }

            {
                ServletRegistration.Dynamic sc = ctx.addServlet("StatusServlet", VistaStatusServlet.class);
                sc.addMapping(urlPattern(VistaApplication.env, "o/status"));
                sc.addMapping(urlPattern(VistaApplication.env, "public/status"));
                sc.addMapping(urlPattern(VistaApplication.staticContext, "public/status"));
            }

            {
                ServletRegistration.Dynamic sc = ctx.addServlet("DeployVerificationServlet", DeployVerificationServlet.class);
                sc.addMapping(urlPattern(VistaApplication.env, "o/verify"));
                sc.addMapping(urlPattern(VistaApplication.env, "public/verify"));
                sc.addMapping(urlPattern(VistaApplication.staticContext, "public/verify"));
            }

            {
                ServletRegistration.Dynamic sc = ctx.addServlet("ApplicationVersionServlet", ApplicationVersionServlet.class);
                sc.addMapping(urlPattern(VistaApplication.env, "o/version"));
                sc.addMapping(urlPattern(VistaApplication.env, "public/version"));
                sc.addMapping(urlPattern(VistaApplication.staticContext, "public/version"));
            }
            {
                ServletRegistration.Dynamic sc = ctx.addServlet("LogViewServlet", VistaLogViewServlet.class);
                sc.addMapping(urlPattern(VistaApplication.operations, "log/*"));
                sc.addMapping(urlPattern(VistaApplication.operations, "logs/*"));
            }
            {
                ServletRegistration.Dynamic sc = ctx.addServlet("DBResetServlet", DBResetServlet.class);
                sc.addMapping(urlPattern(VistaApplication.env, "o/db-reset"));
            }
            {
                ServletRegistration.Dynamic sc = ctx.addServlet("VistaConfigInfoServlet", VistaConfigInfoServlet.class);
                sc.addMapping(urlPattern(VistaApplication.operations, "config"));
            }

            {
                ServletRegistration.Dynamic sc = ctx.addServlet("VistaStackTraceViewServlet", VistaStackTraceViewServlet.class);
                sc.addMapping(urlPattern(VistaApplication.operations, "stack/*"));
            }

            //TODO if Not production
            {
                {
                    ServletRegistration.Dynamic sc = ctx.addServlet("OutOfMemorSimulationServlet", OutOfMemorySimulationServlet.class);
                    sc.addMapping(urlPattern(VistaApplication.operations, "ooms"));
                }

                {
                    ServletRegistration.Dynamic sc = ctx.addServlet("TestTimeoutServlet", TestTimeoutServlet.class);
                    sc.addMapping(urlPattern(VistaApplication.operations, "tt"));
                }

                {
                    ServletRegistration.Dynamic sc = ctx.addServlet("DebugServlet", DebugServlet.class);
                    sc.addMapping(allApplicationsUrlPatterns("debug/*"));
                    sc.addMapping(allApplicationsUrlPatterns("o/debug/*"));
                }

                {
                    ServletRegistration.Dynamic sc = ctx.addServlet("DebugRequestEchoServlet", DebugRequestEchoServlet.class);
                    sc.addMapping(allApplicationsUrlPatterns("echo/*"));
                    sc.addMapping(allApplicationsUrlPatterns("o/echo/*"));
                }

                //http://static.dev.birchwoodsoftwaregroup.com:8888/vista/o/wsp/yardi.starlightinvest.com/voyager6008sp17/webservices/itfresidenttransactions20.asmx
                if (false) {
                    ServletRegistration.Dynamic sc = ctx.addServlet("DevDumpProxyServlet", DevDumpProxyServlet.class);
                    sc.addMapping("/o/wsp/*");
                }
            }
        }

    }

    private String urlPattern(VistaApplication application, String urlPattern) {
        return "/" + application.getInternalMappingName() + (urlPattern.startsWith("/") ? "" : "/") + urlPattern;
    }

    private String[] allApplicationsUrlPatterns(String urlPattern) {
        ArrayList<String> arlPatterns = new ArrayList<String>();
        for (VistaApplication application : VistaApplication.values()) {
            arlPatterns.add(urlPattern(application, urlPattern));
        }
        return arlPatterns.toArray(new String[arlPatterns.size()]);
    }
}
