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
import com.pyx4j.rpc.shared.UserRuntimeException;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.domain.DemoData;
import com.propertyvista.portal.rpc.DeploymentConsts;

public class VistaNamespaceResolver implements NamespaceResolver {

    private static final I18n i18n = I18n.get(VistaNamespaceResolver.class);

    public static final String demoNamespace = DemoData.DemoPmc.vista.name();

    private final static Set<String> prodSystemDnsBase = new HashSet<String>();

    static {
        prodSystemDnsBase.add("residentportalsite.com");
        prodSystemDnsBase.add("prospectportalsite.com");
        prodSystemDnsBase.add("propertyvista.com");
        prodSystemDnsBase.add("propertyvista.ca");
    }

    @Override
    public String getNamespace(HttpServletRequest httprequest) {
        if (httprequest.getServletPath() != null) {
            if ((httprequest.getServletPath().startsWith("/" + DeploymentConsts.ADMIN_URL) || httprequest.getServletPath().startsWith("/public/onboarding"))) {
                return Pmc.adminNamespace;
            }
            if (httprequest.getServletPath().startsWith("/public/schema") || httprequest.getServletPath().startsWith("/public/status")) {
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
                return demoNamespace;
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
            NamespaceManager.setNamespace(Pmc.adminNamespace);
            pmcNamespace = CacheService.get(host);
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

                    if (!pmc.enabled().isBooleanTrue()) {
                        throw new UserRuntimeException(i18n.tr("This property management site was not activated yet"));
                    }

                    pmcNamespace = pmc.namespace().getValue();
                    CacheService.put(host, pmcNamespace);
                }
            }
        } finally {
            NamespaceManager.remove();
        }

        if (pmcNamespace == null) {
            if (httprequest.getServletPath() != null) {
                if (httprequest.getServletPath().startsWith("/o/db-reset")) {
                    return demoNamespace;
                }
            }
            throw new UserRuntimeException(i18n.tr("This property management site was not set-up yet"));
        }

        // Avoid Query for every request
        try {
            NamespaceManager.setNamespace(Pmc.adminNamespace);
            CacheService.put(host, pmcNamespace);
        } finally {
            NamespaceManager.remove();
        }
        return pmcNamespace;
    }
}
