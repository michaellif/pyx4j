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

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.Pmc.PmcStatus;
import com.propertyvista.domain.security.common.VistaApplication;

public abstract class AbstractApplicationContextResolver implements VistaApplicationContextResolver {

    protected final String dnsNameBase;

    public AbstractApplicationContextResolver(String dnsNameBase) {
        this.dnsNameBase = dnsNameBase.toLowerCase(Locale.ENGLISH);
    }

    @Override
    public final VistaApplicationContext resolve(HttpServletRequest httpRequest) {
        String serverName = httpRequest.getServerName().toLowerCase(Locale.ENGLISH);
        if (!serverName.endsWith(dnsNameBase)) {
            return null;
        }

        String normalizedHostName = serverName.substring(0, serverName.length() - dnsNameBase.length());

        VistaApplication application = resolveApplication(httpRequest, normalizedHostName);
        if (application == null) {
            return null;
        }
        String namespaceProposal = resolveNamespaceProposal(httpRequest, normalizedHostName, application);
        if (namespaceProposal == null) {
            return null;
        }
        if (namespaceProposal != null && application.requirePmcResolution()) {
            return resolvePmc(application, namespaceProposal);
        } else {
            return new VistaApplicationContext(namespaceProposal, application, null);
        }
    }

    protected abstract VistaApplication resolveApplication(HttpServletRequest httpRequest, String normalizedHostName);

    protected abstract String resolveNamespaceProposal(HttpServletRequest httpRequest, String normalizedHostName, VistaApplication application);

    protected VistaApplicationContext resolvePmc(VistaApplication application, String namespaceProposal) {
        final EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
        criteria.eq(criteria.proto().dnsName(), namespaceProposal);

        Pmc pmc = NamespaceManager.runInTargetNamespace(VistaNamespace.operationsNamespace, new Callable<Pmc>() {
            @Override
            public Pmc call() {
                return Persistence.service().retrieve(criteria);
            }
        });

        if ((pmc == null) || (pmc.status().getValue() != PmcStatus.Active)) {
            return null;
        } else {
            return new VistaApplicationContext(pmc.namespace().getValue(), application, pmc);
        }
    }

}
