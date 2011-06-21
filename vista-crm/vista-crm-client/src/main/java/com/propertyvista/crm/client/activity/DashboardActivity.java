/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.crm.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.client.ui.viewfactories.DashboardVeiwFactory;
import com.propertyvista.crm.rpc.domain.DashboardMetadata;
import com.propertyvista.crm.rpc.domain.DashboardMetadata.LayoutType;
import com.propertyvista.crm.rpc.domain.GadgetMetadata;
import com.propertyvista.crm.rpc.domain.GadgetMetadata.GadgetType;

public class DashboardActivity extends AbstractActivity implements DashboardView.Presenter {

    private final DashboardView view;

    public DashboardActivity(Place place) {
        view = (DashboardView) DashboardVeiwFactory.instance(DashboardView.class);
        assert (view != null);
        withPlace(place);
    }

    public DashboardActivity(DashboardView view, Place place) {
        this.view = view;
        assert (view != null);
        withPlace(place);
    }

    public DashboardActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);
        populate();
    }

    @Override
    public void populate() {
        // TODO - load metadata with service...
//      DashboardMetadataService dmds = GWT.create(DashboardMetadataService.class);

        // just create a demo dashboard: 
        DashboardMetadata dmd = EntityFactory.create(DashboardMetadata.class);
        dmd.name().setValue("test dashboard");
        dmd.layoutType().setValue(LayoutType.Two21);
        for (int i = 0; i < 3; ++i) {
            GadgetMetadata gmd = EntityFactory.create(GadgetMetadata.class);
            gmd.type().setValue(GadgetType.Demo);
            gmd.name().setValue("Gadget #" + i);
            gmd.column().setValue(1);
            dmd.gadgets().add(gmd);
        }

        GadgetMetadata gmd;
//        gmd = EntityFactory.create(GadgetMetadata.class);
//        gmd.type().setValue(GadgetType.BuildingLister);
//        gmd.name().setValue("Building lister");
//        gmd.column().setValue(0);
//        dmd.gadgets().add(gmd);

        gmd = EntityFactory.create(GadgetMetadata.class);
        gmd.type().setValue(GadgetType.BarChartDisplay);
        gmd.name().setValue("Bar Chart Demo");
        gmd.column().setValue(0);
        dmd.gadgets().add(gmd);

        gmd = EntityFactory.create(GadgetMetadata.class);
        gmd.type().setValue(GadgetType.LineChartDisplay);
        gmd.name().setValue("Line Chart Demo");
        gmd.column().setValue(0);
        dmd.gadgets().add(gmd);

        gmd = EntityFactory.create(GadgetMetadata.class);
        gmd.type().setValue(GadgetType.PieChartDisplay);
        gmd.name().setValue("Pie Chart Demo");
        gmd.column().setValue(1);
        dmd.gadgets().add(gmd);

        view.fill(dmd);
    }

    @Override
    public void save(DashboardMetadata dashboardMetadata) {
        // TODO Auto-generated method stub
    }
}