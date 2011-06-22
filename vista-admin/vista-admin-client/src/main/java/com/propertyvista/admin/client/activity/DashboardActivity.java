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
package com.propertyvista.admin.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.admin.client.ui.dashboard.DashboardView;
import com.propertyvista.admin.client.viewfactories.DashboardVeiwFactory;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.GadgetMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.LayoutType;
import com.propertyvista.domain.dashboard.GadgetMetadata.GadgetType;

public class DashboardActivity extends AbstractActivity {

    private final DashboardView view;

    public DashboardActivity(Place place) {
        view = (DashboardView) DashboardVeiwFactory.instance(DashboardView.class);
        assert (view != null);
        withPlace(place);
    }

    public DashboardActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);

        // TODO - load metadata with service...
//        DashboardMetadataService dmds = GWT.create(DashboardMetadataService.class);

        // just create a demo dashboard: 
        DashboardMetadata dmd = EntityFactory.create(DashboardMetadata.class);
        dmd.name().setValue("test dashboard");
        dmd.layoutType().setValue(LayoutType.One);

        GadgetMetadata gmd = EntityFactory.create(GadgetMetadata.class);
        gmd.type().setValue(GadgetType.BuildingLister);
        gmd.name().setValue("PMC lister");
        gmd.column().setValue(0);
        dmd.gadgets().add(gmd);

        view.fillDashboard(dmd);
    }
}