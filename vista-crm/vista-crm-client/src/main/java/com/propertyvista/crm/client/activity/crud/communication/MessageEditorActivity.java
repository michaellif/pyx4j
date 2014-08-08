/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 23, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.communication;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.rpc.AbstractCrudService.InitializationData;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.communication.MessageEditorView;
import com.propertyvista.crm.rpc.CrmSiteMap.Communication.Message;
import com.propertyvista.crm.rpc.services.MessageCrudService;
import com.propertyvista.crm.rpc.services.MessageCrudService.MessageInitializationData;
import com.propertyvista.domain.communication.MessageCategory;
import com.propertyvista.dto.MessageDTO;

public class MessageEditorActivity extends CrmEditorActivity<MessageDTO> implements MessageEditorView.Presenter {

    private final CrudAppPlace place;

    private MessageCategory mc;

    public MessageEditorActivity(CrudAppPlace place) {
        super(MessageDTO.class, place, CrmSite.getViewFactory().getView(MessageEditorView.class), GWT.<MessageCrudService> create(MessageCrudService.class));
        this.place = place;
        InitializationData data = place.getInitializationData();
        if (data != null && data instanceof MessageInitializationData) {
            mc = ((MessageInitializationData) data).messageCategory();
        }
    }

    @Override
    public void saveMessage(AsyncCallback<MessageDTO> callback, MessageDTO message) {
        ((MessageCrudService) getService()).saveMessage(callback, message, null);

    }

    @Override
    protected void obtainInitializationData(AsyncCallback<InitializationData> callback) {
        MessageInitializationData initData = EntityFactory.create(MessageInitializationData.class);
        if (place instanceof Message) {
            Message p = (Message) place;
            initData.forwardedMessage().set(p.getForwardedMessage());
        }

        if (mc != null) {
            initData.messageCategory().set(mc);
        }
        callback.onSuccess(initData);
    }

    @Override
    public MessageCategory getCategory() {

        return mc;
    }
}
