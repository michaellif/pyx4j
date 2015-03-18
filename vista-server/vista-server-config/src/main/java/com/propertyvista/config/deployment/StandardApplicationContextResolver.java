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

import javax.servlet.http.HttpServletRequest;

import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.security.common.VistaApplication;

public class StandardApplicationContextResolver implements VistaApplicationContextResolver {

    @Override
    public VistaApplicationContext resolve(HttpServletRequest httpRequest) {
        String serverName = httpRequest.getServerName();
        serverName = serverName.toLowerCase(Locale.ENGLISH);
        String[] serverNameParts = serverName.split("\\.");

        VistaApplication application = resolveApplication(httpRequest, serverNameParts);
        if (application == null) {
            return null;
        }
        String namespaceProposal = resolveNamespaceProposal(httpRequest, serverNameParts);
        if (namespaceProposal == null) {
            return null;
        }
        if (namespaceProposal != null && application.requirePmcResolution()) {
            return resolvePmc(application, namespaceProposal);
        } else {
            return new VistaApplicationContext(namespaceProposal, application, null);
        }
    }

    protected VistaApplication resolveApplication(HttpServletRequest httpRequest, String[] serverNameParts) {
        // TODO Auto-generated method stub
        return null;
    }

    protected String resolveNamespaceProposal(HttpServletRequest httpRequest, String[] serverNameParts) {
        // TODO Auto-generated method stub
        return null;
    }

    protected VistaApplicationContext resolvePmc(VistaApplication application, String namespaceProposal) {
        EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
        criteria.eq(criteria.proto().dnsName(), namespaceProposal);
        Pmc pmc = Persistence.service().retrieve(criteria);
        if (pmc == null) {
            return null;
        } else {
            return new VistaApplicationContext(pmc.namespace().getValue(), application, pmc);
        }
    }

}
