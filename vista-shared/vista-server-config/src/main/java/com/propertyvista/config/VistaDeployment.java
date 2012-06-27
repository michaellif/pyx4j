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
package com.propertyvista.config;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.admin.SystemMaintenance;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.admin.domain.pmc.Pmc;
import com.propertyvista.admin.domain.pmc.PmcDnsName;
import com.propertyvista.admin.domain.pmc.PmcDnsName.DnsNameTarget;
import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.shared.VistaSystemIdentification;

public class VistaDeployment {

    public static VistaSystemIdentification getSystemIdentification() {
        String systemIdentification = SystemMaintenance.getSystemMaintenanceInfo().systemIdentification().getValue();
        if (systemIdentification == null) {
            return VistaSystemIdentification.development;
        }
        return VistaSystemIdentification.valueOf(systemIdentification);
    }

    public static boolean isVistaProduction() {
        return ((!ApplicationMode.isDevelopment()) && (VistaSystemIdentification.production == VistaDeployment.getSystemIdentification()));
    }

    public static Pmc getCurrentPmc() {
        final String namespace = NamespaceManager.getNamespace();
        assert (!namespace.equals(VistaNamespace.adminNamespace)) : "PMC not available when running in admin namespace";
        try {
            NamespaceManager.setNamespace(VistaNamespace.adminNamespace);
            EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().namespace(), namespace));
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
     * @return full URL
     */
    public static String getBaseApplicationURL(VistaBasicBehavior application, boolean secure) {
        if (application == VistaBasicBehavior.Admin) {
            return getBaseApplicationURL(null, application, secure);
        } else {
            return getBaseApplicationURL(getCurrentPmc(), application, secure);
        }
    }

    public static String getBaseApplicationURL(Pmc pmc, VistaBasicBehavior application, boolean secure) {
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
        switch (target) {
        case prospectPortal:
            return ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getDefaultBaseURLprospectPortal(pmc.dnsName().getValue());
        case residentPortal:
            return ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance())
                    .getDefaultBaseURLresidentPortal(pmc.dnsName().getValue(), secure);
        case vistaCrm:
            return ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getDefaultBaseURLvistaCrm(pmc.dnsName().getValue());
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
