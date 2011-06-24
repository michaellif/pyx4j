/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-23
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.server.dataimport.AbstractDataPreloader;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata.GadgetType;

public class DashboardPreload extends AbstractDataPreloader {

    @Override
    public String create() {

// first demo dashboard:        
        DashboardMetadata dmd = EntityFactory.create(DashboardMetadata.class);
        dmd = EntityFactory.create(DashboardMetadata.class);
        dmd.name().setValue("System dashboard");
        dmd.layoutType().setValue(LayoutType.One);

        GadgetMetadata gmd;
        gmd = EntityFactory.create(GadgetMetadata.class);
        gmd.type().setValue(GadgetType.BuildingLister);
        gmd.name().setValue("Building lister");
        gmd.column().setValue(0);

        gmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
        dmd.gadgets().add(gmd);

        gmd = EntityFactory.create(GadgetMetadata.class);
        gmd.type().setValue(GadgetType.BarChartDisplay);
        gmd.name().setValue("Bar Chart Demo");
        gmd.column().setValue(0);

        gmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
        dmd.gadgets().add(gmd);

        gmd = EntityFactory.create(GadgetMetadata.class);
        gmd.type().setValue(GadgetType.LineChartDisplay);
        gmd.name().setValue("Line Chart Demo");
        gmd.column().setValue(0);

        gmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
        dmd.gadgets().add(gmd);

        dmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
        PersistenceServicesFactory.getPersistenceService().persist(dmd);

// the second one:
        dmd = EntityFactory.create(DashboardMetadata.class);
        dmd.name().setValue("Building dashboard");
        dmd.layoutType().setValue(LayoutType.Two21);
        for (int i = 0; i < 3; ++i) {
            gmd = EntityFactory.create(GadgetMetadata.class);
            gmd.type().setValue(GadgetType.Demo);
            gmd.name().setValue("Gadget #" + i);
            gmd.column().setValue(1);

            gmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
            dmd.gadgets().add(gmd);
        }

        gmd = EntityFactory.create(GadgetMetadata.class);
        gmd.type().setValue(GadgetType.BarChartDisplay);
        gmd.name().setValue("Bar Chart Demo");
        gmd.column().setValue(0);

        gmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
        dmd.gadgets().add(gmd);

        gmd = EntityFactory.create(GadgetMetadata.class);
        gmd.type().setValue(GadgetType.LineChartDisplay);
        gmd.name().setValue("Line Chart Demo");
        gmd.column().setValue(0);

        gmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
        dmd.gadgets().add(gmd);

        gmd = EntityFactory.create(GadgetMetadata.class);
        gmd.type().setValue(GadgetType.PieChartDisplay);
        gmd.name().setValue("Pie Chart Demo");
        gmd.column().setValue(1);

        gmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
        dmd.gadgets().add(gmd);

        dmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
        PersistenceServicesFactory.getPersistenceService().persist(dmd);

        return "Created " + 2 + "demo dashboards";
    }

    @SuppressWarnings("unchecked")
    @Override
    public String delete() {
        return deleteAll(DashboardMetadata.class, GadgetMetadata.class);
    }

}
