/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import com.pyx4j.entity.server.dataimport.DataPreloaderCollection;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.misc.VistaDataPreloaderParameter;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.portal.server.preloader.site.blacknight.BlackNightSitePreloader;
import com.propertyvista.portal.server.preloader.site.demo.DemoSitePreloader;
import com.propertyvista.portal.server.preloader.site.gondor.GondorSitePreloader;
import com.propertyvista.portal.server.preloader.site.prod.ProdSitePreloader;
import com.propertyvista.portal.server.preloader.site.redridge.RedridgeSitePreloader;
import com.propertyvista.portal.server.preloader.site.rockville.RockvilleSitePreloader;
import com.propertyvista.portal.server.preloader.site.star.StarlightSitePreloader;
import com.propertyvista.portal.server.preloader.site.vista.VistaSitePreloader;
import com.propertyvista.preloader.MerchantAccountPreloader;

public class VistaDataPreloaders extends DataPreloaderCollection {

    public VistaDataPreloaders(VistaDevPreloadConfig config) {
        this(false);
        setParameterValue(VistaDataPreloaderParameter.devPreloadConfig.name(), config);
    }

    private VistaDataPreloaders(boolean production) {
        add(new LocationPreloader());
        add(new ProductCatalogPreloader());
        add(new CrmRolesPreloader());
        add(new PreloadPolicies(production));
        add(new DashboardPreloader());
        add(new RefferenceDataPreloader());

        //DEMO Different data for different PMC
        DemoPmc demoPmc;
        try {
            demoPmc = DemoPmc.valueOf(NamespaceManager.getNamespace());
        } catch (Throwable ignore) {
            demoPmc = null;
        }
        if (demoPmc == null) {
            if (NamespaceManager.getNamespace().equals("starlight")) {
                add(new StarlightSitePreloader());
            } else {
                add(new ProdSitePreloader());
            }
        } else {
            switch (demoPmc) {
            case vista:
                add(new VistaSitePreloader());
                break;
            case star:
                add(new StarlightSitePreloader());
                break;
            case redridge:
                add(new RedridgeSitePreloader());
                break;
            case rockville:
                add(new RockvilleSitePreloader());
                break;
            case gondor:
                add(new GondorSitePreloader());
                break;
            case blacknight:
                add(new BlackNightSitePreloader());
                break;
            case demo:
                add(new DemoSitePreloader());
                break;
            default:
                add(new ProdSitePreloader());
            }
        }

        if (production) {
            //DEMO add(new ProdSitePreloader());
        } else {

            add(new UserPreloader());
            add(new MerchantAccountPreloader());
            add(new CampaignPreloader());

            PmcDataPreloader pmcDataPreloader = new PmcDataPreloader();
            if (pmcDataPreloader.hasData()) {
                add(pmcDataPreloader);
            } else {
                add(new BuildingPreloader());
            }

            add(new LeasePreloader());
            add(new PreloadNewTenantsAndLeads());
            add(new UpdateArrearsHistoryDevPreloader());
            add(new MaintenanceRequestsDevPreloader());
            add(new CommunicationDevPreloader());
        }
    }

    public static VistaDataPreloaders productionPmcPreloaders() {
        return new VistaDataPreloaders(true);
    }
}
