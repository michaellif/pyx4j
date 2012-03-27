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

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.domain.PmcDnsName;
import com.propertyvista.domain.PmcDnsName.DnsNameTarget;
import com.propertyvista.domain.security.VistaBasicBehavior;
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

    /**
     * @param target
     *            residentPortal. Crm or PtApp
     * @param secure
     *            only matters for residentPortal, for other types will be ignored
     * @return
     */
    public static String getBaseApplicationURL(VistaBasicBehavior application, boolean secure) {
        DnsNameTarget target;
        switch (application) {
        case Admin:
            return ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getDefaultBaseURLvistaAdmin();
        case CRM:
            target = DnsNameTarget.vistaCrm;
            break;
        case ProspectiveApp:
            target = DnsNameTarget.prospectPortal;
            break;
        case TenantPortal:
            target = DnsNameTarget.residentPortal;
            break;
        default:
            throw new IllegalArgumentException();
        }
        Pmc pmc = getCurrentPmc();
        if (pmc != null) {
            for (PmcDnsName alias : pmc.dnsNameAliases()) {
                if (alias.target().getValue() == target) {
                    if (secure && !alias.httpsEnabled().isBooleanTrue()) {
                        // Fallback to default
                        continue;
                    }
                    String protocol = "http://";
                    if (secure) {
                        protocol = "https://";
                    }
                    return protocol + alias.dnsName().getValue();
                }
            }
        }
        switch (target) {
        case prospectPortal:
            return ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getDefaultBaseURLprospectPortal();
        case residentPortal:
            return ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getDefaultBaseURLresidentPortal(secure);
        case vistaCrm:
            return ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getDefaultBaseURLvistaCrm();
        default:
            throw new IllegalArgumentException();
        }
    }

    public static String getPortalGoogleAPIKey() {
        Pmc pmc = getCurrentPmc();
        for (PmcDnsName alias : pmc.dnsNameAliases()) {
            if (alias.target().getValue() == DnsNameTarget.residentPortal) {
                if (!alias.googleAPIKey().isNull()) {
                    return alias.googleAPIKey().getValue();
                } else {
                    return "";
                }
            }
        }
        return "";
    }
}
