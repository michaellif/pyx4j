/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 19, 2014
 * @author ernestog
 */
package com.propertyvista.server.config.filter.namespace;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.OrCriterion;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.portal.rpc.shared.SiteWasNotActivatedUserRuntimeException;

public class VistaNamespaceResolverHelper {

    private final static Logger log = LoggerFactory.getLogger(VistaNamespaceResolverHelper.class);

    private static final I18n i18n = I18n.get(VistaNamespaceResolverHelper.class);

    private final static Set<String> prodSystemDnsBase = new HashSet<String>();

    private final static Set<String> testSystemDnsBase = new HashSet<String>();

    private final static Set<String> mapToNoNamespace = new HashSet<String>();

    private final static String prodPmcAppEnvRegex = "^.*prod\\d*$|^.*staging\\d*$";

    static {
        prodSystemDnsBase.add("my-community.co");
        prodSystemDnsBase.add("residentportalsite.com");
        prodSystemDnsBase.add("prospectportalsite.com");
        prodSystemDnsBase.add("propertyvista.com");

        testSystemDnsBase.add("birchwoodsoftwaregroup.com");
        testSystemDnsBase.add("devpv.com");
        testSystemDnsBase.add("pyx4j.com");
        testSystemDnsBase.add("propertyvista.biz");
        // Old names not used
//        testSystemDnsBase.add("propertyvista.ca");
//        testSystemDnsBase.add("residentportalsite.ca");
//        testSystemDnsBase.add("prospectportalsite.ca");
//        testSystemDnsBase.add("residentportal.info");
//        testSystemDnsBase.add("prospectportal.info");

        mapToNoNamespace.add("env");
        mapToNoNamespace.add("static");
        mapToNoNamespace.add("operations");
        mapToNoNamespace.add("onboarding");
        mapToNoNamespace.add("h");
        mapToNoNamespace.add("m");
    }

    static String getNamespace(HttpServletRequest httprequest) {
//        if (httprequest.getServletPath() != null) {
//            String servletPath = httprequest.getServletPath();
//            if ((servletPath.startsWith("/" + VistaApplication.operations) || servletPath.startsWith("/interfaces"))) {
//                return VistaNamespace.operationsNamespace;
//            }
//            if (servletPath.startsWith("/public/schema") || servletPath.startsWith("/public/version") || servletPath.startsWith("/static/")
//                    || servletPath.startsWith("/demo/") || servletPath.startsWith("/public/verify") || servletPath.startsWith("/public/status")
//                    || servletPath.startsWith("/o/") || servletPath.equals("/index.html") || servletPath.startsWith("/" + VistaApplication.onboarding)) {
//                return VistaNamespace.noNamespace;
//            }
//        }

        // Dev: Get the 4th part of URL.
        // {PMC}-crm.local.devpv.com
        // {PMC}-crm.dev.birchwoodsoftwaregroup.com

        // Support wildcard HTTPS on dev
        // {PMC}-crm-22.birchwoodsoftwaregroup.com
        // {PMC}-portal-22.birchwoodsoftwaregroup.com

        // Prod: Get the 3rd part of URL.
        // {PMC}.propertyvista.com

        String serverName = httprequest.getServerName();
        if ("localhost".equals(serverName) || httprequest.getLocalAddr().equals(serverName)) {
            return VistaNamespace.demoNamespace;
        }

        serverName = serverName.toLowerCase(Locale.ENGLISH);
        String[] serverNameParts = serverName.split("\\.");

        String namespaceProposal = null;
        if (serverNameParts.length >= 3) {
            String dnsBase = serverNameParts[serverNameParts.length - 2] + "." + serverNameParts[serverNameParts.length - 1];
            if (prodSystemDnsBase.contains(dnsBase)) {
                namespaceProposal = serverNameParts[serverNameParts.length - 3];
                if (namespaceProposal.matches(prodPmcAppEnvRegex)) {
                    namespaceProposal = getNamespaceFromPmcAppEnv(serverNameParts);
                }
            } else if (testSystemDnsBase.contains(dnsBase)) {
                namespaceProposal = getNamespaceFromPmcAppEnv(serverNameParts);
            }
        }

        if (mapToNoNamespace.contains(namespaceProposal)) {
            return VistaNamespace.noNamespace;
        }

        String pmcNamespace;
        EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
        if (namespaceProposal != null) {
            OrCriterion or = criteria.or();
            or.left(PropertyCriterion.eq(criteria.proto().dnsName(), namespaceProposal));
            or.right(PropertyCriterion.eq(criteria.proto().dnsNameAliases().$().enabled(), Boolean.TRUE));
            or.right(PropertyCriterion.eq(criteria.proto().dnsNameAliases().$().dnsName(), serverName));
        } else {
            criteria.eq(criteria.proto().dnsNameAliases().$().enabled(), Boolean.TRUE);
            criteria.eq(criteria.proto().dnsNameAliases().$().dnsName(), serverName);
        }
        Pmc pmc = Persistence.service().retrieve(criteria);
        if (pmc != null) {
            if (pmc.status().getValue() != PmcStatus.Active) {
                // Avoid Query for every request
                pmcNamespace = VistaNamespace.noNamespace;
            } else {
                pmcNamespace = pmc.namespace().getValue();
            }
        } else {
            pmcNamespace = VistaNamespace.noNamespace;
        }

        if ((pmcNamespace == null) || (VistaNamespace.noNamespace.equals(pmcNamespace))) {
            log.warn("accessing host {}, {}, path {}", serverName, namespaceProposal, httprequest.getServletPath());
            if (httprequest.getServletPath().endsWith("robots.txt") || httprequest.getServletPath().endsWith("favicon.ico")) {
                return VistaNamespace.noNamespace;
            } else {
                throw new SiteWasNotActivatedUserRuntimeException(i18n.tr("This property management site was not activated yet"));
            }
        } else {
            return pmcNamespace;
        }
    }

    private static String getNamespaceFromPmcAppEnv(String[] serverNameParts) {
        if (serverNameParts.length >= 4) {
            String hostName = serverNameParts[serverNameParts.length - 4];
            int appIdx = hostName.lastIndexOf('-');
            if (appIdx > 0) {
                return hostName.substring(0, appIdx);
            } else {
                return hostName;
            }
        } else if (serverNameParts.length == 3) {
            String hostName = serverNameParts[serverNameParts.length - 3];
            int envIdx = hostName.lastIndexOf('-');
            if (envIdx > 0) {
                int appIdx = hostName.lastIndexOf('-', envIdx - 1);
                if (appIdx > 0) {
                    return hostName.substring(0, appIdx);
                } else {
                    return hostName.substring(0, envIdx);
                }
            }
            return hostName;
        } else {
            return null;
        }
    }
}
