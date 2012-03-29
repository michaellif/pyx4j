/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 13, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertvista.generator;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.ISharedUserEntity;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;
import com.propertyvista.domain.dashboard.gadgets.type.BuildingLister;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata.RefreshInterval;

public class DashboardGenerator extends Dashboards {

    public DashboardGenerator() {

        systemDashboards.add(DefaultSystem());
    }

    private DashboardMetadata DefaultSystem() {
        DashboardMetadata dmd = EntityFactory.create(DashboardMetadata.class);
        dmd.user().id().setValue(ISharedUserEntity.DORMANT_KEY); // shared for everyone usage 
        dmd.type().setValue(DashboardType.system);
        dmd.isShared().setValue(true);
        dmd.name().setValue("System Dashboard");
        dmd.description().setValue("Displays default system data");
        dmd.layoutType().setValue(LayoutType.One);

        BuildingLister buildingLister = EntityFactory.create(BuildingLister.class);
        buildingLister.user().id().setValue(ISharedUserEntity.DORMANT_KEY); // shared for everyone usage
        buildingLister.pageSize().setValue(10);
        buildingLister.pageNumber().setValue(0);
        buildingLister.refreshInterval().setValue(RefreshInterval.Never);
        buildingLister.docking().column().setValue(0);

        dmd.gadgets().add(buildingLister);

        return dmd;
    }
}
