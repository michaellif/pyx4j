/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 18, 2011
 * @author vadims
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.portal.client.ui.residents.DashboardView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.dto.TenantDashboardDTO;
import com.propertyvista.portal.rpc.portal.services.PersonalInfoCrudService;

public class DashboardActivity extends SecurityAwareActivity {

    private final DashboardView view;

    PersonalInfoCrudService srv;

    public DashboardActivity(Place place) {
        this.view = (DashboardView) PortalViewFactory.instance(DashboardView.class);
        withPlace(place);
        srv = GWT.create(PersonalInfoCrudService.class);
    }

    public DashboardActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        TenantDashboardDTO dashboard = EntityFactory.create(TenantDashboardDTO.class);
        //  dashboard.notification().setValue("Notification");

        view.populate(dashboard);

    }

}
