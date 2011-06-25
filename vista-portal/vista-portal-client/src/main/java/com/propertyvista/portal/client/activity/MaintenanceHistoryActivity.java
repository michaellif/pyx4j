/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 25, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import java.util.Map;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.client.ui.MaintenanceHistoryView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.domain.dto.MaintenanceRequestDTO;

public class MaintenanceHistoryActivity extends AbstractActivity implements MaintenanceHistoryView.Presenter {

    MaintenanceHistoryView view;

    public MaintenanceHistoryActivity(Place place) {
        this.view = (MaintenanceHistoryView) PortalViewFactory.instance(MaintenanceHistoryView.class);
        this.view.setPresenter(this);
        withPlace(place);

    }

    public MaintenanceHistoryActivity withPlace(Place place) {
        Map<String, String> args = ((AppPlace) place).getArgs();
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);

    }

    @Override
    public void showSystemStatus() {
        // TODO Auto-generated method stub

    }

    @Override
    public void showDetails(MaintenanceRequestDTO maintanenceRequest) {
        // TODO Auto-generated method stub

    }

}
