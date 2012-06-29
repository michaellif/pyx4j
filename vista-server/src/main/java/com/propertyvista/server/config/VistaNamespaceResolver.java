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

import com.pyx4j.config.server.NamespaceResolver;
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

    private static final I18n i18n = I18n.get(VistaNamespaceResolver.class);

    private final static Set<String> prodSystemDnsBase = new HashSet<String>();

    static {
        prodSystemDnsBase.add("residentportalsite.com");
        prodSystemDnsBase.add("residentportalsite.ca");
        prodSystemDnsBase.add("prospectportalsite.com");
        prodSystemDnsBase.add("prospectportalsite.ca");
        prodSystemDnsBase.add("propertyvista.com");
        prodSystemDnsBase.add("propertyvista.ca");
    }

    @Override
    public String getNamespace(HttpServletRequest httprequest) {
        if (httprequest.getServletPath() != null) {
            if ((httprequest.getServletPath().startsWith("/" + DeploymentConsts.ADMIN_URL) || httprequest.getServletPath().startsWith("/public/onboarding"))) {
                return VistaNamespace.adminNamespace;
            }
            if (httprequest.getServletPath().startsWith("/public/schema") || httprequest.getServletPath().startsWith("/static/")
                    || httprequest.getServletPath().startsWith("/public/status")) {
                return "_";
            }
        }

        // Dev: Get the 4th part of URL.
        // www.ABC.22.birchwoodsoftwaregroup.com
        // www.ABC.dev.birchwoodsoftwaregroup.com 

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
            if (dnsBase.equals("birchwoodsoftwaregroup.com")) {
                if (parts.length >= 4) {
                    namespaceProposal = parts[parts.length - 4];
                }
            } else if (prodSystemDnsBase.contains(dnsBase)) {
                namespaceProposal = parts[parts.length - 3];
            }
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
                        pmcNamespace = "_";
                    } else {
                        pmcNamespace = pmc.namespace().getValue();
                    }
                } else {
                    pmcNamespace = "_";
                }
                CacheService.put(VistaNamespaceResolver.class.getName() + "." + host, pmcNamespace);
            }
        } finally {
            NamespaceManager.remove();
        }

        if ((pmcNamespace == null) || ("_".equals(pmcNamespace))) {
            if (httprequest.getServletPath() != null) {
                if (httprequest.getServletPath().startsWith("/o/db-reset")) {
                    return VistaNamespace.demoNamespace;
                }
            }
            throw new SiteWasNotActivatedUserRuntimeException(i18n.tr("This property management site was not activated yet"));
        } else {
            return pmcNamespace;
        }
    }
}
