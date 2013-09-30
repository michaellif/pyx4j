/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 30, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity.maintenance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService.RetrieveTarget;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceRequestDTO;
import com.propertyvista.portal.rpc.portal.web.services.maintenance.MaintenanceRequestCrudService;
import com.propertyvista.portal.web.client.PortalWebSite;
import com.propertyvista.portal.web.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.web.client.ui.maintenance.MaintenanceRequestConfirmationPageView;
import com.propertyvista.portal.web.client.ui.maintenance.MaintenanceRequestConfirmationPageView.MaintenanceRequestConfirmationPagePresenter;

public class MaintenanceRequestConfirmationActivity extends SecurityAwareActivity implements MaintenanceRequestConfirmationPagePresenter {

    private final MaintenanceRequestConfirmationPageView view;

    protected final MaintenanceRequestCrudService srv;

    private final Key entityId;

    public MaintenanceRequestConfirmationActivity(AppPlace place) {
        this.view = PortalWebSite.getViewFactory().instantiate(MaintenanceRequestConfirmationPageView.class);
        this.view.setPresenter(this);

        srv = GWT.create(MaintenanceRequestCrudService.class);

        entityId = place.getItemId();
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        assert (entityId != null);
        srv.retrieve(new DefaultAsyncCallback<MaintenanceRequestDTO>() {
            @Override
            public void onSuccess(MaintenanceRequestDTO result) {
                view.populate(result);
            }
        }, entityId, RetrieveTarget.View);
    }

    @Override
    public void back() {
        // TODO Auto-generated method stub

    }

}