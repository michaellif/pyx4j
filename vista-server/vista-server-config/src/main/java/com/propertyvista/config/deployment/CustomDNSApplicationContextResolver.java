/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 18, 2015
 * @author vlads
 */
package com.propertyvista.config.deployment;

import java.util.Locale;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.pmc.PmcDnsName;
import com.propertyvista.domain.security.common.VistaApplication;

public class CustomDNSApplicationContextResolver implements VistaApplicationContextResolver {

    private static Logger log = LoggerFactory.getLogger(CustomDNSApplicationContextResolver.class);

    public CustomDNSApplicationContextResolver() {
        log.debug("Custom DNS resolver created");
    }

    @Override
    public VistaApplicationContext resolve(HttpServletRequest httpRequest) {
        String serverName = httpRequest.getServerName().toLowerCase(Locale.ENGLISH);

        final EntityQueryCriteria<PmcDnsName> criteria = EntityQueryCriteria.create(PmcDnsName.class);
        criteria.eq(criteria.proto().pmc().status(), PmcStatus.Active);
        criteria.eq(criteria.proto().enabled(), Boolean.TRUE);
        criteria.eq(criteria.proto().dnsName(), serverName);

        PmcDnsName dnsName = NamespaceManager.runInTargetNamespace(VistaNamespace.operationsNamespace, new Callable<PmcDnsName>() {
            @Override
            public PmcDnsName call() {
                return Persistence.service().retrieve(criteria);
            }
        });

        if (dnsName == null) {
            return null;
        } else {
            return new VistaApplicationContext(dnsName.pmc().namespace().getValue(), resolveApplication(httpRequest, dnsName), dnsName.pmc());
        }
    }

    protected VistaApplication resolveApplication(HttpServletRequest httpRequest, PmcDnsName dnsName) {
        switch (dnsName.target().getValue()) {
        case crm:
            return VistaApplication.crm;
        case site:
            return VistaApplication.site;
        case portal:
            if ("prospect".equalsIgnoreCase(HttpRequestUtils.getRootServletPath(httpRequest))) {
                return VistaApplication.prospect;
            } else {
                return VistaApplication.resident;
            }
        default:
            return null;
        }
    }

}
