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
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.propertyvista.portal.client.ui.residents.MaintenanceDetailsView;
import com.propertyvista.portal.client.ui.viewfactories.PortalViewFactory;
import com.propertyvista.portal.rpc.portal.services.TenantMaintenanceService;

public class MaintenanceDetailsActivity extends SecurityAwareActivity implements MaintenanceDetailsView.Presenter {

    private final MaintenanceDetailsView view;

    private final TenantMaintenanceService srv;

    public MaintenanceDetailsActivity(Place place) {
        this.view = PortalViewFactory.instance(MaintenanceDetailsView.class);
        this.view.setPresenter(this);
        srv = GWT.create(TenantMaintenanceService.class);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

    }

    @Override
    public void submit() {
        // TODO Auto-generated method stub

    }

}
