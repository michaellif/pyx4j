/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 21, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.server.common.util;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.PmcDnsName;
import com.propertyvista.domain.PmcDnsName.DnsNameTarget;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.server.domain.admin.Pmc;

public class VistaDeployment {

    private static Pmc getCurrentPmc() {
        final String namespace = NamespaceManager.getNamespace();
        try {
            NamespaceManager.setNamespace(Pmc.adminNamespace);
            EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().dnsName(), namespace));
            return Persistence.service().retrieve(criteria);
        } finally {
            NamespaceManager.setNamespace(namespace);
        }
    }

    public static String getBaseApplicationURL(DnsNameTarget target) {
        Pmc pmc = getCurrentPmc();
        for (PmcDnsName alias : pmc.dnsNameAliases()) {
            if (alias.target().getValue() == target) {
                //TODO https
                return "http://" + alias.dnsName().getValue();
            }
        }
        String defaultUrlBase = ServerSideConfiguration.instance().getMainApplicationURL();
        switch (target) {
        case prospectPortal:
            return defaultUrlBase + DeploymentConsts.PTAPP_URL;
        case residentPortal:
            return defaultUrlBase + DeploymentConsts.PORTAL_URL;
        case vistaCrm:
            return defaultUrlBase + DeploymentConsts.CRM_URL;
        default:
            throw new IllegalArgumentException();
        }
    }
}
