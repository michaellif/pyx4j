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

import com.propertyvista.portal.rpc.portal.resident.services.MessagePortalCrudService;
import com.propertyvista.portal.shared.PortalSite;
import com.propertyvista.portal.shared.ui.communication.MessageView;

public class MessageViewActivity extends AbstractActivity implements MessageView.Presenter {

    private final MessageView view;

    private final MessagePortalCrudService service;

    public MessageViewActivity() {
        this.service = GWT.<MessagePortalCrudService> create(MessagePortalCrudService.class);
        this.view = PortalSite.getViewFactory().getView(MessageView.class);
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
    public MessagePortalCrudService getService() {
        return service;
    }

}
