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
package com.propertyvista.portal.shared.activity.communication;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.communication.MessageDTO;
import com.propertyvista.portal.rpc.portal.resident.services.MessagePortalCrudService;
import com.propertyvista.portal.shared.activity.AbstractEditorActivity;
import com.propertyvista.portal.shared.ui.communication.MessagePageView;
import com.propertyvista.portal.shared.ui.communication.MessagePageView.MessagePagePresenter;

public class MessagePageActivity extends AbstractEditorActivity<MessageDTO> implements MessagePagePresenter {

    public MessagePageActivity(AppPlace place) {
        super(MessagePageView.class, GWT.<MessagePortalCrudService> create(MessagePortalCrudService.class), place);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        getView().setPresenter(this);
    }

    @Override
    public void saveMessageItem(AsyncCallback<MessageDTO> callback, MessageDTO message) {

        ((MessagePortalCrudService) getService()).saveChildMessage(callback, message);
    }

    @Override
    public void hideThread() {
        ((MessagePortalCrudService) getService()).hideThread(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                AppSite.getPlaceController().goTo(new PortalSiteMap.Message.MessageView());
            }
        }, getEntityId());

    }

}
