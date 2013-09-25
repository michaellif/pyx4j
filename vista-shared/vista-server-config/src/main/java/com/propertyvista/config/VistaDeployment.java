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

import java.util.List;

import com.pyx4j.commons.Key;
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
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.security.common.VistaApplication;
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
        assert (!namespace.equals(VistaNamespace.operationsNamespace)) : "Function not available when running in operations namespace";
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
     * @param application
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
            return ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getDefaultApplicationURL(application, null);
        case onboarding:
            return ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getDefaultApplicationURL(application, null);
        case crm:
            target = DnsNameTarget.crm;
            break;
        case field:
            target = DnsNameTarget.field;
            break;
        case prospect:
            target = DnsNameTarget.prospect;
            break;
        case portal:
            target = DnsNameTarget.portal;
            break;
        case site:
            target = DnsNameTarget.site;
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
        return ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getDefaultApplicationURL(application, pmc.dnsName().getValue());
    }

    public static String getPortalGoogleAPIKey() {
        Pmc pmc = getCurrentPmc();
        for (PmcDnsName alias : pmc.dnsNameAliases()) {
            if (alias.target().getValue() == DnsNameTarget.portal) {
                if (!alias.googleAPIKey().isNull()) {
                    return alias.googleAPIKey().getValue();
                } else {
                    return VistaSettings.googleAPIKey;
                }
            }
        }
        return VistaSettings.googleAPIKey;
    }

    public static Key getPmcYardiInterfaceId(Building building) {
        final String namespace = NamespaceManager.getNamespace();
        assert (!namespace.equals(VistaNamespace.operationsNamespace)) : "Function not available when running in operations namespace";
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.eq(criteria.proto().id(), building);
        Building buildingOrigin = Persistence.service().retrieve(criteria);
        return buildingOrigin.integrationSystemId().getValue();
    }

    public static PmcYardiCredential getPmcYardiCredential(Building building) {
        final String namespace = NamespaceManager.getNamespace();
        assert (!namespace.equals(VistaNamespace.operationsNamespace)) : "Function not available when running in operations namespace";
        try {
            Key yardiInterfaceId = getPmcYardiInterfaceId(building);
            if (yardiInterfaceId != null) {
                NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
                EntityQueryCriteria<PmcYardiCredential> criteria = EntityQueryCriteria.create(PmcYardiCredential.class);
                criteria.eq(criteria.proto().id(), yardiInterfaceId);
                criteria.eq(criteria.proto().pmc().namespace(), namespace);
                return Persistence.service().retrieve(criteria);
            } else {
                return null;
            }
        } finally {
            NamespaceManager.setNamespace(namespace);
        }
    }

    public static List<PmcYardiCredential> getPmcYardiCredentials() {
        final String namespace = NamespaceManager.getNamespace();
        assert (!namespace.equals(VistaNamespace.operationsNamespace)) : "Function not available when running in operations namespace";
        try {
            NamespaceManager.setNamespace(VistaNamespace.operationsNamespace);
            EntityQueryCriteria<PmcYardiCredential> criteria = EntityQueryCriteria.create(PmcYardiCredential.class);
            criteria.eq(criteria.proto().pmc().namespace(), namespace);
            return Persistence.service().query(criteria);
        } finally {
            NamespaceManager.setNamespace(namespace);
        }
    }

    public static List<Building> getPmcYardiBuildings(PmcYardiCredential yc) {
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.eq(criteria.proto().integrationSystemId(), yc.getPrimaryKey());
        return Persistence.service().query(criteria);
    }
}
