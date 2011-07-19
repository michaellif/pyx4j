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

import com.propertyvista.common.domain.PreloadConfig;

public class VistaDataPreloaders extends DataPreloaderCollection {

//    private Map<String, Serializable> parameters = Collections.emptyMap();

    public VistaDataPreloaders(PreloadConfig config) {
//        parameters.put(PreloadConfig.KEY, config);

        add(new LocationsPreload());
        add(new PreloadUsers(config));
        add(new CampaignPreload(config));
        add(new PreloadBuildings(config));
        add(new PreloadTenants(config));
        add(new PreloadPT(config));
        add(new PortalSitePreload());
        add(new DevelopmentSecurityPreload());
        add(new DashboardPreload());
        add(new ReportPreload());
    }

//    @Override
//    protected void add(DataPreloader preloader) {
//        preloader.setParametersValues(parameters);
//        super.add(preloader);
//    }
}
