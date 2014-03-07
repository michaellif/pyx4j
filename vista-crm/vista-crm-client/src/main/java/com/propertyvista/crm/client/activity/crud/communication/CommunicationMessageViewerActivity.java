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
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.communication.CommunicationMessageViewerView;
import com.propertyvista.crm.rpc.services.CommunicationMessageCrudService;
import com.propertyvista.domain.communication.CommunicationMessage;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.CommunicationMessageDTO;

public class CommunicationMessageViewerActivity extends CrmViewerActivity<CommunicationMessageDTO> implements CommunicationMessageViewerView.Presenter {

    public CommunicationMessageViewerActivity(CrudAppPlace place) {

        super(place, CrmSite.getViewFactory().getView(CommunicationMessageViewerView.class), (AbstractCrudService<CommunicationMessageDTO>) GWT
                .create(CommunicationMessageCrudService.class));
        // TODO Auto-generated constructor stub
    }

    @Override
    public void saveMessage(AsyncCallback<CommunicationMessage> callback, CommunicationMessage message) {
        ((CommunicationMessageCrudService) getService()).saveMessage(callback, message);

    }

    @Override
    public boolean canEdit() {
        return super.canEdit() & SecurityController.checkBehavior(VistaCrmBehavior.Maintenance);
    }

    @Override
    public void takeOwnership(AsyncCallback<CommunicationMessage> callback, CommunicationMessage message) {
        ((CommunicationMessageCrudService) getService()).takeOwnership(callback, message);
    }
}
