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
import com.google.inject.Inject;
import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.rpc.domain.DashboardMetadata;
import com.propertyvista.crm.rpc.domain.DashboardMetadata.LayoutType;

import com.pyx4j.entity.shared.EntityFactory;

public class DashboardActivity extends AbstractActivity {

    private final DashboardView view;

    @Inject
    public DashboardActivity(DashboardView view) {
        this.view = view;
    }

    public DashboardActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);

        DashboardMetadata dmd = EntityFactory.create(DashboardMetadata.class);
        dmd.name().setValue("test dashboard");
        dmd.layoutType().setValue(LayoutType.Two12);

        view.fillDashboard(dmd);
    }
}