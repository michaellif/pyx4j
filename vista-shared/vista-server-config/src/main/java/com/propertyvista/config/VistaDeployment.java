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

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcDnsName;
import com.propertyvista.domain.pmc.PmcDnsName.DnsNameTarget;
import com.propertyvista.domain.security.common.VistaApplication;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.domain.settings.PmcVistaFeatures;
import com.propertyvista.domain.settings.PmcYardiCredential;
import com.propertyvista.server.config.VistaFeatures;
import com.propertyvista.shared.VistaSystemIdentification;
import com.propertyvista.shared.config.VistaDemo;
import com.propertyvista.shared.config.VistaSettings;

public class VistaDeployment {

    public static void changePmcContext() {
        VistaFeatures.removeThreadLocale();
    }

    public static VistaSystemIdentification getSystemIdentification() {
        String systemIdentification = SystemMaintenance.getSystemMaintenanceInfo().systemIdentification().getValue();
        if (systemIdentification == null) {
            return VistaSystemIdentification.development;
        }
        return VistaSystemIdentification.valueOf(systemIdentification);
    }

    public static boolean isVistaProduction() {
        return ((!ApplicationMode.isDevelopment()) && (!VistaDemo.isDemo()) && (VistaSystemIdentification.production == VistaDeployment
                .getSystemIdentification()));
    }

    public static boolean isVistaStaging() {
        return ((VistaSystemIdentification.staging == VistaDeployment.getSystemIdentification()));
    }

    public static boolean isSystemNamespace() {
        String namespace = NamespaceManager.getNamespace();
        return VistaNamespace.noNamespace.equals(namespace) || VistaNamespace.operationsNamespace.equals(namespace);
    }

    public static Pmc getCurrentPmc() {
        final String namespace = NamespaceManager.getNamespace();
        assert (!namespace.equals(VistaNamespace.operationsNamespace)) : "PMC not available when running in admin namespace";
        try {
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
            EntityQueryCriteria<Pmc> criteria = EntityQueryCriteria.create(Pmc.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().namespace(), namespace));
            return Persistence.service().retrieve(criteria);
        } finally {
            NamespaceManager.setNamespace(namespace);
        }
    }

    public static PmcVistaFeatures getCurrentVistaFeatures() {
        return VistaFeatures.VistaFeaturesCustomizationImpl.getCurrentVistaFeatures();
    }

    /**
     * @deprecated Use function with VistaApplication argument
     */
    @Deprecated
    public static String getBaseApplicationURL(VistaBasicBehavior behavior, boolean secure) {
        if (behavior == VistaBasicBehavior.Operations) {
            return getBaseApplicationURL(null, behavior, secure);
        } else {
            return getBaseApplicationURL(getCurrentPmc(), behavior, secure);
        }
    }

    /**
     * @deprecated Use function with VistaApplication argument
     */
    @Deprecated
    public static String getBaseApplicationURL(Pmc pmc, VistaBasicBehavior behavior, boolean secure) {
        VistaApplication application;
        switch (behavior) {
        case Operations:
            application = VistaApplication.operations;
            break;
        case Onboarding:
            application = VistaApplication.onboarding;
            break;
        case CRM:
            application = VistaApplication.crm;
            break;
        case ProspectiveApp:
            application = VistaApplication.prospect;
            break;
        case TenantPortal:
            application = VistaApplication.resident;
            break;
        default:
            throw new IllegalArgumentException();
        }
        return getBaseApplicationURL(pmc, application, secure);
    }

    /**
     * @param target
     *            residentPortal. Crm or PtApp
     * @param secure
     *            only matters for residentPortal, for other types will be ignored
     * @return full URL
     */
    public static String getBaseApplicationURL(VistaApplication application, boolean secure) {
        if (application == VistaApplication.operations) {
            return getBaseApplicationURL(null, application, secure);
        } else {
            return getBaseApplicationURL(getCurrentPmc(), application, secure);
        }
    }

    public static String getBaseApplicationURL(Pmc pmc, VistaApplication application, boolean secure) {
        DnsNameTarget target;
        switch (application) {
        case operations:
            return ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getDefaultBaseURLvistaOperations();
        case onboarding:
            return ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getDefaultBaseURLvistaOnboarding();
        case crm:
            target = DnsNameTarget.vistaCrm;
            break;
        case prospect:
            target = DnsNameTarget.prospectPortal;
            break;
        case resident:
            target = DnsNameTarget.residentPortal;
        case field:
            target = DnsNameTarget.field;
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
        case field:
            return ((AbstractVistaServerSideConfiguration) ServerSideConfiguration.instance()).getDefaultBaseURLvistaField(pmc.dnsName().getValue());
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
                    return VistaSettings.googleAPIKey;
                }
            }
        }
        return VistaSettings.googleAPIKey;
    }

    public static PmcYardiCredential getPmcYardiCredential() {
        final String namespace = NamespaceManager.getNamespace();
        assert (!namespace.equals(VistaNamespace.operationsNamespace)) : "PMC not available when running in admin namespace";
        try {
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
            EntityQueryCriteria<PmcYardiCredential> criteria = EntityQueryCriteria.create(PmcYardiCredential.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().pmc().namespace(), namespace));
            return Persistence.service().retrieve(criteria);
        } finally {
            NamespaceManager.setNamespace(namespace);
        }
    }
}
