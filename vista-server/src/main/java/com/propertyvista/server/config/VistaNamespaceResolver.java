/*
 * (C) Copyright Pro;perty Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-12
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.config;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.NamespaceResolver;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.cache.CacheService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.OrCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.gwt.server.ServletUtils;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.shared.SiteWasNotActivatedUserRuntimeException;

public class VistaNamespaceResolver implements NamespaceResolver {

    private final static Logger log = LoggerFactory.getLogger(VistaNamespaceResolver.class);

    private static final I18n i18n = I18n.get(VistaNamespaceResolver.class);

    private final static Set<String> prodSystemDnsBase = new HashSet<String>();

    static {
        prodSystemDnsBase.add("residentportalsite.com");
        prodSystemDnsBase.add("residentportalsite.ca");
        prodSystemDnsBase.add("prospectportalsite.com");
        prodSystemDnsBase.add("prospectportalsite.ca");
        prodSystemDnsBase.add("propertyvista.com");
        prodSystemDnsBase.add("propertyvista.ca");

        prodSystemDnsBase.add("propertyvista.biz");
        prodSystemDnsBase.add("residentportal.info");
        prodSystemDnsBase.add("prospectportal.info");
    }

    @Override
    public String getNamespace(HttpServletRequest httprequest) {
        if (httprequest.getServletPath() != null) {
            String servletPath = httprequest.getServletPath();
            if ((servletPath.startsWith("/" + DeploymentConsts.ADMIN_URL) || servletPath.startsWith("/interfaces"))) {
                return VistaNamespace.adminNamespace;
            }
            if (servletPath.startsWith("/public/schema") || servletPath.startsWith("/public/version") || servletPath.startsWith("/static/")
                    || servletPath.startsWith("/demo/") || servletPath.startsWith("/public/status") || servletPath.startsWith("/o/")) {
                return VistaNamespace.noNamespace;
            }
            if (ApplicationMode.isDevelopment() && servletPath.equals("/index.html")) {
                return VistaNamespace.noNamespace;
            }
        }

        // Dev: Get the 4th part of URL.
        // www.ABC.22.birchwoodsoftwaregroup.com
        // www.ABC.dev.birchwoodsoftwaregroup.com

        // Support wildcard HTTPS on dev
        // vista-crm-22.birchwoodsoftwaregroup.com
        // vista-portal-22.birchwoodsoftwaregroup.com

        // Prod: Get the 3rd part of URL.
        // www.ABC.propertyvista.com 

        String host = ServletUtils.getForwardedHost(httprequest);
        if (host == null) {
            host = httprequest.getServerName();
            if ("localhost".equals(host) || httprequest.getLocalAddr().equals(host)) {
                return VistaNamespace.demoNamespace;
            }
        }
        host = host.toLowerCase(Locale.ENGLISH);
        String[] parts = host.split("\\.");

        String namespaceProposal = null;
        if (parts.length >= 3) {
            String dnsBase = parts[parts.length - 2] + "." + parts[parts.length - 1];
            if (prodSystemDnsBase.contains(dnsBase)) {
                namespaceProposal = parts[parts.length - 3];
            } else if (dnsBase.equals("birchwoodsoftwaregroup.com")) {
                if (parts.length >= 4) {
                    namespaceProposal = parts[0];
                } else if (parts.length == 3) {
                    String finalHostName = parts[parts.length - 3];
                    int envIdx = finalHostName.lastIndexOf('-');
                    if (envIdx > 0) {
                        int appIdx = finalHostName.lastIndexOf('-', envIdx - 1);
                        if (appIdx > 0) {
                            namespaceProposal = finalHostName.substring(0, appIdx);
                        }
                    }
                }
            }
        }

        if ("static".equals(namespaceProposal)) {
            return VistaNamespace.noNamespace;
        }

        String pmcNamespace;
        try {
            NamespaceManager.setNamespace(VistaNamespace.adminNamespace);
            pmcNamespace = CacheService.get(VistaNamespaceResolver.class.getName() + "." + host);
            if (pmcNamespace == null) {
                EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
                if (namespaceProposal != null) {
                    OrCriterion or = criteria.or();
                    or.left(PropertyCriterion.eq(criteria.proto().dnsName(), namespaceProposal));
                    or.right(PropertyCriterion.eq(criteria.proto().dnsNameAliases().$().enabled(), Boolean.TRUE));
                    or.right(PropertyCriterion.eq(criteria.proto().dnsNameAliases().$().dnsName(), host));
                } else {
                    criteria.add(PropertyCriterion.eq(criteria.proto().dnsNameAliases().$().enabled(), Boolean.TRUE));
                    criteria.add(PropertyCriterion.eq(criteria.proto().dnsNameAliases().$().dnsName(), host));
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
                CacheService.put(VistaNamespaceResolver.class.getName() + "." + host, pmcNamespace);
            }
        } finally {
            NamespaceManager.remove();
        }

        if ((pmcNamespace == null) || (VistaNamespace.noNamespace.equals(pmcNamespace))) {
            log.warn("accessing host {}, path {}", host, httprequest.getServletPath());
            if (httprequest.getServletPath().endsWith("robots.txt")) {
                return VistaNamespace.noNamespace;
            } else {
                throw new SiteWasNotActivatedUserRuntimeException(i18n.tr("This property management site was not activated yet"));
            }
        } else {
            return pmcNamespace;
        }
    }
}
