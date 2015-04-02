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
 */
package com.propertyvista.portal.server.preloader;

import com.pyx4j.entity.server.dataimport.DataPreloaderCollection;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.biz.preloader.CrmRolesPreloader;
import com.propertyvista.biz.preloader.DashboardPreloader;
import com.propertyvista.biz.preloader.ILSMarketingDevPreloader;
import com.propertyvista.biz.preloader.ProductCatalogPreloader;
import com.propertyvista.biz.preloader.ReportsAdministrationPreloader;
import com.propertyvista.biz.preloader.ref.LocationPreloader;
import com.propertyvista.biz.preloader.ref.MessageCategoryPreloader;
import com.propertyvista.biz.preloader.ref.ReferenceDataPreloader;
import com.propertyvista.biz.preloader.ref.SystemEndpointPreloader;
import com.propertyvista.biz.preloader.site.ProdSitePreloader;
import com.propertyvista.domain.DemoData.DemoPmc;
import com.propertyvista.misc.VistaDataPreloaderParameter;
import com.propertyvista.misc.VistaDevPreloadConfig;
import com.propertyvista.preloader.AggregatedTransfersDevPreloader;
import com.propertyvista.preloader.BuildingPreloader;
import com.propertyvista.preloader.CampaignPreloader;
import com.propertyvista.preloader.CommunicationDevPreloader;
import com.propertyvista.preloader.CreditChecksPaymentsPreloader;
import com.propertyvista.preloader.CrmRolesDevPreloader;
import com.propertyvista.preloader.MaintenanceRequestsMockupPreloader;
import com.propertyvista.preloader.MerchantAccountPreloader;
import com.propertyvista.preloader.N4BatchesPreloader;
import com.propertyvista.preloader.PreloadNewTenantsAndLeads;
import com.propertyvista.preloader.UpdateArrearsHistoryDevPreloader;
import com.propertyvista.preloader.UserPreloader;
import com.propertyvista.preloader.leases.LeasePreloader;
import com.propertyvista.preloader.policy.MockupN4PolicyPreloader;
import com.propertyvista.preloader.policy.PaymentMethodSelectionPolicyDevPreloader;
import com.propertyvista.preloader.site.demo.DemoSitePreloader;
import com.propertyvista.preloader.site.gondor.GondorSitePreloader;
import com.propertyvista.preloader.site.greenwin.GreenwinSitePreloader;
import com.propertyvista.preloader.site.metcap.MetCapSitePreloader;
import com.propertyvista.preloader.site.redridge.RedridgeSitePreloader;
import com.propertyvista.preloader.site.rockville.RockvilleSitePreloader;
import com.propertyvista.preloader.site.star.StarlightSitePreloader;
import com.propertyvista.preloader.site.timbercreek.TimbercreekSitePreloader;
import com.propertyvista.preloader.site.vista.VistaSitePreloader;

public class VistaDataPreloaders extends DataPreloaderCollection {

    public VistaDataPreloaders(VistaDevPreloadConfig config) {
        this(false);
        setParameterValue(VistaDataPreloaderParameter.devPreloadConfig.name(), config);
    }

    private VistaDataPreloaders(boolean production) {
        add(new PmcInformationPreloader());

        add(new LocationPreloader());
        add(new ProductCatalogPreloader());
        add(new CrmRolesPreloader());
        add(new PreloadPolicies(production));
        add(new DashboardPreloader());
        add(new ReferenceDataPreloader());
        add(new MessageCategoryPreloader());
        add(new SystemEndpointPreloader());
        add(new ReportsAdministrationPreloader());

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
                //add(new TimbercreekSitePreloader());
                //add(new MetCapSitePreloader());
                //add(new GreenwinSitePreloader());
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
            case metcap:
                add(new MetCapSitePreloader());
                break;
            case timbercreek:
                add(new TimbercreekSitePreloader());
                break;
            case greenwin:
                add(new GreenwinSitePreloader());
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
            add(new CrmRolesDevPreloader());
            add(new UserPreloader());
            add(new MerchantAccountPreloader());
            add(new PaymentMethodSelectionPolicyDevPreloader());
            add(new MockupN4PolicyPreloader());
            add(new CampaignPreloader());

            PmcDataPreloader pmcDataPreloader = new PmcDataPreloader();
            if (pmcDataPreloader.hasData()) {
                add(pmcDataPreloader);
            } else {
                add(new BuildingPreloader());
            }

            add(new LeasePreloader());
            add(new AggregatedTransfersDevPreloader());
            add(new PreloadNewTenantsAndLeads());
            add(new UpdateArrearsHistoryDevPreloader());
            add(new MaintenanceRequestsMockupPreloader());
            add(new CommunicationDevPreloader());
            add(new ILSMarketingDevPreloader());
            add(new CreditChecksPaymentsPreloader());
            add(new N4BatchesPreloader());
        }
    }

    public static VistaDataPreloaders productionPmcPreloaders() {
        return new VistaDataPreloaders(true);
    }
}
