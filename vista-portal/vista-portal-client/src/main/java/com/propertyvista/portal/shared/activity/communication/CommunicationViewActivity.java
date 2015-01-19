/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 21, 2013
 * @author michaellif
 */
package com.propertyvista.portal.shared.activity.communication;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.propertyvista.portal.rpc.portal.resident.services.CommunicationPortalCrudService;
import com.propertyvista.portal.shared.PortalSite;
import com.propertyvista.portal.shared.ui.communication.CommunicationView;

public class CommunicationViewActivity extends AbstractActivity implements CommunicationView.Presenter {

    private final CommunicationView view;

    private final CommunicationPortalCrudService service;

    public CommunicationViewActivity() {
        this.service = GWT.<CommunicationPortalCrudService> create(CommunicationPortalCrudService.class);
        this.view = PortalSite.getViewFactory().getView(CommunicationView.class);
        this.view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        populate();
    }

    public void populate() {
        view.populate();
    }

    @Override
    public CommunicationPortalCrudService getService() {
        return service;
    }

}
