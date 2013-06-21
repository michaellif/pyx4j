/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-15
 * @author matheszabi
 * @version $Id$
 */
package com.propertyvista.portal.web.client.activity.residents.communicationcenter;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;

import com.propertyvista.domain.security.common.AbstractUser;
import com.propertyvista.dto.CommunicationCenterDTO;
import com.propertyvista.portal.rpc.portal.services.resident.CommunicationCenterService;
import com.propertyvista.portal.web.client.activity.SecurityAwareActivity;
import com.propertyvista.portal.web.client.ui.residents.communicationcenter.CommunicationCenterView;
import com.propertyvista.portal.web.client.ui.viewfactories.PortalWebViewFactory;

public class CommunicationCenterActivity extends SecurityAwareActivity implements CommunicationCenterView.Presenter {

    private final Logger log = LoggerFactory.getLogger(CommunicationCenterActivity.class);

    private final CommunicationCenterView view;

    private final CommunicationCenterService srv;

    public CommunicationCenterActivity(Place place) {
        this.view = PortalWebViewFactory.instance(CommunicationCenterView.class);
        this.view.setPresenter(this);
        srv = GWT.create(CommunicationCenterService.class);
    }

    @Override
    public void start(final AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        panel.setWidget(view);

        getMyMessages();
    }

    private void getMyMessages() {
        srv.listMyMessages(new DefaultAsyncCallback<Vector<CommunicationCenterDTO>>() {
            @Override
            public void onSuccess(Vector<CommunicationCenterDTO> result) {
                if (result == null) {
                    log.info("Service sent null vector!");
                } else {
                    log.info("Service sent {} comm messages to build the ui.", result.size());
                    view.populateMyMessages(result);
                }
            }
        });
    }

    @Override
    public void sendNewMessage(String topic, String messageContent, boolean isHighImportance, AbstractUser[] destinations) {
        srv.createAndSendMessage(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                getMyMessages();
            }
        }, topic, messageContent, isHighImportance, destinations);

    }

    @Override
    public void sendReply(String topic, String messageContent, boolean isHighImportance, CommunicationCenterDTO parentMessage) {
        srv.sendReply(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                getMyMessages();
                view.viewDefault();
            }
        }, topic, messageContent, isHighImportance, parentMessage);

    }
}
