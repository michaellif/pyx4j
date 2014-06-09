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

import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.communication.MessageEditorView;
import com.propertyvista.crm.rpc.services.MessageCrudService;
import com.propertyvista.dto.MessageDTO;

public class MessageEditorActivity extends CrmEditorActivity<MessageDTO> implements MessageEditorView.Presenter {

    public MessageEditorActivity(CrudAppPlace place) {
        super(place, CrmSite.getViewFactory().getView(MessageEditorView.class), GWT.<MessageCrudService> create(MessageCrudService.class), MessageDTO.class);
    }

    @Override
    public void saveMessage(AsyncCallback<MessageDTO> callback, MessageDTO message) {
        ((MessageCrudService) getService()).saveMessage(callback, message, null);

    }
}
