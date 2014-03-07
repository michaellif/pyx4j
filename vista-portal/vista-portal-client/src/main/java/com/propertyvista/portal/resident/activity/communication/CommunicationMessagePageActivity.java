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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.communication.CommunicationMessage;
import com.propertyvista.dto.CommunicationMessageDTO;
import com.propertyvista.portal.resident.ui.communication.CommunicationMessagePageView;
import com.propertyvista.portal.resident.ui.communication.CommunicationMessagePageView.CommunicationMessagePagePresenter;
import com.propertyvista.portal.rpc.portal.resident.services.CommunicationMessagePortalCrudService;
import com.propertyvista.portal.shared.activity.AbstractEditorActivity;

public class CommunicationMessagePageActivity extends AbstractEditorActivity<CommunicationMessageDTO> implements CommunicationMessagePagePresenter {

    public CommunicationMessagePageActivity(AppPlace place) {
        super(CommunicationMessagePageView.class, GWT.<CommunicationMessagePortalCrudService> create(CommunicationMessagePortalCrudService.class), place);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        getView().setPresenter(this);
    }

    @Override
    public void saveMessage(AsyncCallback<CommunicationMessage> callback, CommunicationMessage message) {

        ((CommunicationMessagePortalCrudService) getService()).saveMessage(callback, message);
    }

}
