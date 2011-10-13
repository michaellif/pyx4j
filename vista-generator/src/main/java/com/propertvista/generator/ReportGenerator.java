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

import java.util.Random;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata.GadgetType;

public class ReportGenerator {

    static public DashboardMetadata DefaultSystem1() {
        DashboardMetadata dmd = EntityFactory.create(DashboardMetadata.class);
        dmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
        dmd.type().setValue(DashboardType.system);
        dmd.layoutType().setValue(LayoutType.Report);
        dmd.name().setValue("System Report");

        GadgetMetadata gmd;
        for (int i = 0; i < 3; ++i) {
            gmd = EntityFactory.create(GadgetMetadata.class);
            gmd.type().setValue(GadgetType.Demo);
            gmd.name().setValue("Gadget #" + i);
            gmd.column().setValue(new Random().nextInt(2));

            gmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage
            dmd.gadgets().add(gmd);
        }

        gmd = EntityFactory.create(GadgetMetadata.class);
        gmd.type().setValue(GadgetType.BuildingLister);
        gmd.name().setValue("Building lister");
        gmd.column().setValue(-1);

        gmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
        dmd.gadgets().add(gmd);

        gmd = EntityFactory.create(GadgetMetadata.class);
        gmd.type().setValue(GadgetType.BarChartDisplay);
        gmd.name().setValue("Bar Chart Demo");
        gmd.column().setValue(-1);

        gmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
        dmd.gadgets().add(gmd);

        gmd = EntityFactory.create(GadgetMetadata.class);
        gmd.type().setValue(GadgetType.PieChartDisplay);
        gmd.name().setValue("Pie Chart Demo");
        gmd.column().setValue(0);

        gmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
        dmd.gadgets().add(gmd);

        gmd = EntityFactory.create(GadgetMetadata.class);
        gmd.type().setValue(GadgetType.GaugeDisplay);
        gmd.name().setValue("Gauge Demo");
        gmd.column().setValue(0);

        gmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
        dmd.gadgets().add(gmd);

        return dmd;
    }

    static public DashboardMetadata DefaultSystem2() {
        DashboardMetadata dmd = EntityFactory.create(DashboardMetadata.class);
        dmd = EntityFactory.create(DashboardMetadata.class);
        dmd.type().setValue(DashboardType.system);
        dmd.layoutType().setValue(LayoutType.Report);
        dmd.name().setValue("Test Report");

        GadgetMetadata gmd;
        for (int i = 0; i < 3; ++i) {
            gmd = EntityFactory.create(GadgetMetadata.class);
            gmd.type().setValue(GadgetType.Demo);
            gmd.name().setValue("Gadget #" + i);
            gmd.column().setValue(new Random().nextInt(2));

            gmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage
            dmd.gadgets().add(gmd);
        }

        gmd = EntityFactory.create(GadgetMetadata.class);
        gmd.type().setValue(GadgetType.BarChartDisplay);
        gmd.name().setValue("Bar Chart Demo");
        gmd.column().setValue(-1);

        gmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
        dmd.gadgets().add(gmd);

        gmd = EntityFactory.create(GadgetMetadata.class);
        gmd.type().setValue(GadgetType.PieChartDisplay);
        gmd.name().setValue("Pie Chart Demo");
        gmd.column().setValue(0);

        gmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
        dmd.gadgets().add(gmd);

        gmd = EntityFactory.create(GadgetMetadata.class);
        gmd.type().setValue(GadgetType.GaugeDisplay);
        gmd.name().setValue("Gauge Demo");
        gmd.column().setValue(0);

        gmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
        dmd.gadgets().add(gmd);

        dmd.user().id().setValue(Key.DORMANT_KEY); // shared for everyone usage 
        return dmd;
    }
}
