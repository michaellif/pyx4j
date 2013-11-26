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
 * @version $Id$
 */
package com.propertyvista.server.config;

import java.util.ArrayList;
import java.util.Set;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.wicket.protocol.http.ContextParamWebApplicationFactory;
import org.apache.wicket.protocol.http.WicketFilter;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.sun.xml.ws.transport.http.servlet.WSServlet;

import com.pyx4j.config.server.ApplicationVersionServlet;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.servlet.InitializationServletContextListener;
import com.pyx4j.essentials.server.admin.DeployVerificationServlet;
import com.pyx4j.essentials.server.dev.DebugServlet;
import com.pyx4j.essentials.server.dev.DevDumpProxyServlet;
import com.pyx4j.essentials.server.dev.OutOfMemorySimulationServlet;
import com.pyx4j.essentials.server.download.DownloadServlet;
import com.pyx4j.essentials.server.download.LogViewServlet;
import com.pyx4j.gwt.server.GWTCacheFilter;
import com.pyx4j.log4j.LoggerConfig;
import com.pyx4j.rpc.server.RemoteServiceServlet;
import com.pyx4j.server.contexts.LifecycleFilter;

import com.propertyvista.biz.system.AuditSessionListener;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.ils.ILSAuthFilter;
import com.propertyvista.ils.kijiji.rs.KijijiApiRsApplication;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.oapi.rs.OpenApiRsApplication;
import com.propertyvista.operations.server.services.VistaConfigInfoServlet;
import com.propertyvista.operations.server.services.simulator.CardServiceSimulationServlet;
import com.propertyvista.pmsite.server.PMSiteApplication;
import com.propertyvista.pmsite.server.PMSiteFilter;
import com.propertyvista.pmsite.server.PMSiteRobotsTxtFilter;
import com.propertyvista.portal.server.portal.PublicMediaServlet;
import com.propertyvista.portal.server.upload.LegalLetterDocumentServlet;
import com.propertyvista.portal.server.upload.PmcDocumentServlet;
import com.propertyvista.portal.server.upload.SiteImageResourceServlet;
import com.propertyvista.portal.server.upload.VistaFileAccessServlet;
import com.propertyvista.server.VistaUploadServlet;
import com.propertyvista.server.ci.DBResetServlet;
import com.propertyvista.server.ci.EnvLinksServlet;
import com.propertyvista.server.ci.TestTimeoutServlet;
import com.propertyvista.server.ci.VistaStatusServlet;
import com.propertyvista.server.ci.bugs.WSServletContextListenerFix;
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

        {
            FilterRegistration.Dynamic fc = ctx.addFilter("GWTCacheFilter", GWTCacheFilter.class);
            fc.setInitParameter(GWTCacheFilter.PARAM_cacheExpiresHours, "20");
            fc.addMappingForUrlPatterns(null, true, "/*");
        }

        {
            FilterRegistration.Dynamic fc = ctx.addFilter("RobotsFilter", RobotsFilter.class);
            fc.addMappingForUrlPatterns(null, true, "/*");
        }

        {
            FilterRegistration.Dynamic fc = ctx.addFilter("LifecycleFilter", LifecycleFilter.class);
            fc.addMappingForUrlPatterns(null, true, "/*");
        }

        // Open Id; client and server
        {
            {
                FilterRegistration.Dynamic fc = ctx.addFilter("OpenIdFilter", OpenIdFilter.class);
                fc.addMappingForUrlPatterns(null, true, "/*");
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
                fc.addMappingForUrlPatterns(null, true, urlPattern(VistaApplication.site, "/robots.txt"));
            }
            {
                FilterRegistration.Dynamic fc = ctx.addFilter("PMSiteFilter", PMSiteFilter.class);
                fc.addMappingForUrlPatterns(null, true, urlPattern(VistaApplication.site, "/*"));
                fc.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, urlPattern(VistaApplication.site, "/*"));
                fc.setInitParameter(ContextParamWebApplicationFactory.APP_CLASS_PARAM, PMSiteApplication.class.getName());
                // TODO use java constants in the code
                fc.setInitParameter(PMSiteFilter.IGNORE_URLS_PARAM, "/robots.txt,/favicon.ico,/sitegwt/\\w*,/vista/https/*,/media/*,/o/*,*.siteimgrc,/debug/*");
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
                fc.addMappingForUrlPatterns(null, true, "/interfaces/oapi/*");
            }

            if (!VistaTODO.removedForProductionOAPI) {
                // TODO avoid WSServletContainerInitializer invocation, it produces MemoryLeaks
                // We used web.xml
                if (false) {
                    ctx.addListener(WSServletContextListenerFix.class);
                }
                {
                    ServletRegistration.Dynamic sc = ctx.addServlet("OpenApiWsService", WSServlet.class);
                    sc.addMapping("/interfaces/oapi/ws/*");
                }

                {
                    ServletRegistration.Dynamic sc = ctx.addServlet("OpenApiRsService", ServletContainer.class);
                    sc.addMapping("/interfaces/oapi/rs/*");
                    sc.setInitParameter("javax.ws.rs.Application", OpenApiRsApplication.class.getName());
                }
            }

            {
                FilterRegistration.Dynamic fc = ctx.addFilter("ILSAuthFilter", ILSAuthFilter.class);
                fc.addMappingForUrlPatterns(null, true, "/interfaces/ils/*");
            }
            {
                ServletRegistration.Dynamic sc = ctx.addServlet("ILSKijijiService", ServletContainer.class);
                sc.addMapping("/interfaces/ils/kijiji/*");
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
            sc.addMapping(allApplicationsUrlPatterns("/download/*"));
        }
        {
            ServletRegistration.Dynamic sc = ctx.addServlet("VistaUploadServlet", VistaUploadServlet.class);
            sc.addMapping(allApplicationsUrlPatterns("/upload/*"));
        }

        {
            ServletRegistration.Dynamic sc = ctx.addServlet("PublicMediaServlet", PublicMediaServlet.class);
            sc.addMapping(allApplicationsUrlPatterns("/media/*"));
        }

        // Special downloads; TODO unify
        {
            {
                ServletRegistration.Dynamic sc = ctx.addServlet("PmcDocumentServlet", PmcDocumentServlet.class);
                sc.addMapping(urlPattern(VistaApplication.crm, "/pmc_document/*"));
                sc.addMapping(urlPattern(VistaApplication.operations, "/pmc_document/*"));
            }
            {
                ServletRegistration.Dynamic sc = ctx.addServlet("VistaFileAccessServlet", VistaFileAccessServlet.class);
                sc.addMapping(urlPattern(VistaApplication.crm, "/file/*"));
                sc.addMapping(urlPattern(VistaApplication.resident, "/file/*"));
            }
            {
                ServletRegistration.Dynamic sc = ctx.addServlet("LegalLetterDocumentServlet", LegalLetterDocumentServlet.class);
                sc.addMapping(urlPattern(VistaApplication.crm, "/legal_letter/*"));
            }

        }

        // Development environment
        {
            {
                ServletRegistration.Dynamic sc = ctx.addServlet("CardServiceSimulationServlet", CardServiceSimulationServlet.class);
                sc.addMapping("/o/CardServiceSimulation/*");
            }
        }

        // CI part of application
        {
            {
                ServletRegistration.Dynamic sc = ctx.addServlet("EnvLinksServlet", EnvLinksServlet.class);
                sc.addMapping("/index.html");
            }
            {
                ServletRegistration.Dynamic sc = ctx.addServlet("StatusServlet", VistaStatusServlet.class);
                sc.addMapping("/status");
                sc.addMapping("/o/status");
                sc.addMapping("/public/status");
            }
            {
                ServletRegistration.Dynamic sc = ctx.addServlet("DeployVerificationServlet", DeployVerificationServlet.class);
                sc.addMapping("/public/verify");
            }
            {
                ServletRegistration.Dynamic sc = ctx.addServlet("ApplicationVersionServlet", ApplicationVersionServlet.class);
                sc.addMapping("/version");
                sc.addMapping("/o/version");
                sc.addMapping("/public/version");
            }
            if (false) {
                ServletRegistration.Dynamic sc = ctx.addServlet("LogViewServlet", LogViewServlet.class);
                sc.addMapping("/logs/*");
            }
            {
                ServletRegistration.Dynamic sc = ctx.addServlet("DBResetServlet", DBResetServlet.class);
                sc.addMapping("/o/db-reset");
            }
            {
                ServletRegistration.Dynamic sc = ctx.addServlet("VistaConfigInfoServlet", VistaConfigInfoServlet.class);
                sc.addMapping(urlPattern(VistaApplication.operations, "/config"));
            }

            //TODO if Not production
            {
                {
                    ServletRegistration.Dynamic sc = ctx.addServlet("OutOfMemorSimulationServlet", OutOfMemorySimulationServlet.class);
                    sc.addMapping("/o/ooms");
                }
                {
                    ServletRegistration.Dynamic sc = ctx.addServlet("TestTimeoutServlet", TestTimeoutServlet.class);
                    sc.addMapping("/o/tt");
                }
                {
                    ServletRegistration.Dynamic sc = ctx.addServlet("DebugServlet", DebugServlet.class);
                    sc.addMapping("/debug/*");
                    sc.addMapping("/o/debug/*");
                    sc.addMapping(allApplicationsUrlPatterns("/debug/*"));
                }

                //http://static.dev.birchwoodsoftwaregroup.com:8888/vista/o/wsp/yardi.starlightinvest.com/voyager6008sp17/webservices/itfresidenttransactions20.asmx
                {
                    ServletRegistration.Dynamic sc = ctx.addServlet("DevDumpProxyServlet", DevDumpProxyServlet.class);
                    sc.addMapping("/o/wsp/*");
                }
            }
        }

    }

    private String urlPattern(VistaApplication application, String urlPattern) {
        return "/" + application.name() + urlPattern;
    }

    private String[] allApplicationsUrlPatterns(String urlPattern) {
        ArrayList<String> arlPatterns = new ArrayList<String>();
        for (VistaApplication application : VistaApplication.values()) {
            arlPatterns.add("/" + application.name() + urlPattern);
        }
        return arlPatterns.toArray(new String[arlPatterns.size()]);
    }
}
