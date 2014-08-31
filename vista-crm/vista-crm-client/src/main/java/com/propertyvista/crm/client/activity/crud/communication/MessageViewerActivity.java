/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.communication;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.gwt.commons.ClientEventBus;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.site.rpc.CrudAppPlace.Type;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.CrmClientCommunicationManager;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.event.CommunicationStatusUpdateEvent;
import com.propertyvista.crm.client.ui.crud.communication.MessageViewerView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.MessageCrudService;
import com.propertyvista.domain.communication.CommunicationThread.ThreadStatus;
import com.propertyvista.dto.MessageDTO;

public class MessageViewerActivity extends CrmViewerActivity<MessageDTO> implements MessageViewerView.Presenter {

    public MessageViewerActivity(CrudAppPlace place) {
        super(MessageDTO.class, place, CrmSite.getViewFactory().getView(MessageViewerView.class), GWT
                .<AbstractCrudService<MessageDTO>> create(MessageCrudService.class));
    }

    @Override
    public void saveMessage(MessageDTO message, final ThreadStatus threadStatus, final boolean rePopulate) {
        ((MessageCrudService) getService()).saveMessage(new DefaultAsyncCallback<MessageDTO>() {
            @Override
            public void onSuccess(MessageDTO result) {
                if (rePopulate) {
                    getView().populate(result);
                }
                ClientEventBus.fireEvent(new CommunicationStatusUpdateEvent(CrmClientCommunicationManager.instance().getLatestCommunicationNotification()));
            }
        }, message, threadStatus);

    }

    @Override
    public void assignOwnership(MessageDTO message, IEntity empoyee) {
        ((MessageCrudService) getService()).assignOwnership(new DefaultAsyncCallback<MessageDTO>() {
            @Override
            public void onSuccess(MessageDTO result) {
                getView().populate(result);
                ClientEventBus.fireEvent(new CommunicationStatusUpdateEvent(CrmClientCommunicationManager.instance().getLatestCommunicationNotification()));
            }
        }, message, empoyee);
    }

    @Override
    public void hideUnhide(MessageDTO source) {
        ((MessageCrudService) getService()).hideUnhide(new DefaultAsyncCallback<MessageDTO>() {
            @Override
            public void onSuccess(MessageDTO result) {
                boolean gotoLister = result.hidden().getValue(false);
                if (gotoLister) {
                    CrudAppPlace place = new CrmSiteMap.Communication.Message();
                    place.setType(Type.lister);
                    AppSite.getPlaceController().goTo(place);
                } else {
                    getView().populate(result);
                }
            }
        }, source);
    }

}
