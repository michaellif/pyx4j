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
 * @version $Id$
 */
package com.propertyvista.portal.resident.activity.communication;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.domain.communication.CommunicationThread;
import com.propertyvista.dto.MessagesDTO;
import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.communication.CommunicationMessageView;
import com.propertyvista.portal.rpc.portal.resident.services.CommunicationMessagePortalCrudService;

public class CommunicationMessageViewActivity extends AbstractActivity implements CommunicationMessageView.Presenter {

    private final CommunicationMessageView view;

    private final CommunicationMessagePortalCrudService service;

    public CommunicationMessageViewActivity() {
        this.service = GWT.<CommunicationMessagePortalCrudService> create(CommunicationMessagePortalCrudService.class);
        this.view = ResidentPortalSite.getViewFactory().getView(CommunicationMessageView.class);
        this.view.setPresenter(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        populate();
    }

    public void populate() {
        service.retreiveCommunicationMessages(new DefaultAsyncCallback<MessagesDTO>() {
            @Override
            public void onSuccess(MessagesDTO result) {
                if (result != null) {
                    view.populate(result.messages());
                }
            }
        }, false);
    }

    @Override
    public void setThreadContext(CommunicationThread thread) {

    }

}
