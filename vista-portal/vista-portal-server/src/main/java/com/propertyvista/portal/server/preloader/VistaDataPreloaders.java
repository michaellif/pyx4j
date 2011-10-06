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

import com.propertyvista.domain.PreloadConfig;

public class VistaDataPreloaders extends DataPreloaderCollection {

    public VistaDataPreloaders(PreloadConfig config) {
        this(config, false);
    }

    private VistaDataPreloaders(PreloadConfig config, boolean production) {
        add(new LocationPreloader());
        add(new ServiceCatalogPreloader());

        if (!production) {
            add(new RefferenceDataPreloader());
            add(new UserPreloader(config));
            add(new CampaignPreloader(config));
            add(new BuildingPreloader(config));
            add(new PreloadTenants(config));
            add(new PtPreloader(config));
            add(new DevelopmentSecurityPreloader());
            add(new DashboardPreloader());
            add(new ReportPreloader());
            // DEMO - temporary!!!
            add(new UnitVacancyReportDTOPreloader(config));
        }
        add(new PortalSitePreloader());
    }

    public static VistaDataPreloaders productionPmcPreloaders() {
        return new VistaDataPreloaders(null, true);
    }

}
