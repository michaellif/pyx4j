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
import com.propertyvista.portal.server.preloader.site.prod.ProdSitePreloader;
import com.propertyvista.portal.server.preloader.site.redridge.RedridgeSitePreloader;
import com.propertyvista.portal.server.preloader.site.rockville.RockvilleSitePreloader;
import com.propertyvista.portal.server.preloader.site.star.StarlightSitePreloader;
import com.propertyvista.portal.server.preloader.site.vista.VistaSitePreloader;

public class VistaDataPreloaders extends DataPreloaderCollection {

    public VistaDataPreloaders(VistaDevPreloadConfig config) {
        this(false);
        setParameterValue(VistaDataPreloaderParameter.devPreloadConfig.name(), config);
    }

    private VistaDataPreloaders(boolean production) {
        add(new LocationPreloader());
        add(new ServiceCatalogPreloader());

        if (production) {
            //DEMO add(new ProdSitePreloader());
        } else {

            // TODO - these two should be moved to production preload when structure has been agreed!..  
            add(new DashboardPreloader());
            add(new ReportPreloader());
            // end TODO

            add(new RefferenceDataPreloader());
            add(new UserPreloader());
            add(new CampaignPreloader());

            PmcDataPreloader pmcDataPreloader = new PmcDataPreloader();
            if (pmcDataPreloader.hasData()) {
                add(pmcDataPreloader);
            } else {
                add(new BuildingPreloader());
            }
            // TODO move to if in pmcData
            add(new PreloadTenants());
            add(new PtPreloader());

            add(new DevelopmentSecurityPreloader());

            // DEMO - temporary!!!
            add(new MockupAvailabilityReportEventPreloader());
            add(new MockupTenantPreloader());
        }

        //DEMO Different data for different PMC
        DemoPmc demoPmc;
        try {
            demoPmc = DemoPmc.valueOf(NamespaceManager.getNamespace());
        } catch (Throwable ignore) {
            demoPmc = null;
        }
        if (demoPmc == null) {
            add(new ProdSitePreloader());
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
            default:
                add(new ProdSitePreloader());
            }
        }
    }

    public static VistaDataPreloaders productionPmcPreloaders() {
        return new VistaDataPreloaders(true);
    }
}
