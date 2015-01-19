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
import com.propertyvista.crm.client.ui.crud.communication.CommunicationViewerView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.CommunicationCrudService;
import com.propertyvista.domain.communication.CommunicationThread.ThreadStatus;
import com.propertyvista.dto.communication.CommunicationThreadDTO;
import com.propertyvista.dto.communication.MessageDTO;

public class CommunicationViewerActivity extends CrmViewerActivity<CommunicationThreadDTO> implements CommunicationViewerView.Presenter {

    public CommunicationViewerActivity(CrudAppPlace place) {
        super(CommunicationThreadDTO.class, place, CrmSite.getViewFactory().getView(CommunicationViewerView.class), GWT
                .<AbstractCrudService<CommunicationThreadDTO>> create(CommunicationCrudService.class));
    }

    @Override
    public void saveMessage(MessageDTO message, final ThreadStatus threadStatus, final boolean rePopulate) {
        ((CommunicationCrudService) getService()).saveMessage(new DefaultAsyncCallback<CommunicationThreadDTO>() {
            @Override
            public void onSuccess(CommunicationThreadDTO result) {
                if (rePopulate) {
                    getView().populate(result);
                }
                ClientEventBus.fireEvent(new CommunicationStatusUpdateEvent(CrmClientCommunicationManager.instance().getLatestCommunicationNotification()));
            }
        }, message, threadStatus);

    }

    @Override
    public void assignOwnership(CommunicationThreadDTO message, String additionalComment, IEntity empoyee) {
        ((CommunicationCrudService) getService()).assignOwnership(new DefaultAsyncCallback<CommunicationThreadDTO>() {
            @Override
            public void onSuccess(CommunicationThreadDTO result) {
                getView().populate(result);
                ClientEventBus.fireEvent(new CommunicationStatusUpdateEvent(CrmClientCommunicationManager.instance().getLatestCommunicationNotification()));
            }
        }, message, additionalComment, empoyee);
    }

    @Override
    public void hideUnhide(CommunicationThreadDTO source) {
        ((CommunicationCrudService) getService()).hideUnhide(new DefaultAsyncCallback<CommunicationThreadDTO>() {
            @Override
            public void onSuccess(CommunicationThreadDTO result) {
                boolean gotoLister = result.hidden().getValue(false);
                if (gotoLister) {
                    CrudAppPlace place = new CrmSiteMap.Communication.Message();
                    place.queryArg(CommunicationThreadDTO.ViewScope.class.getSimpleName(), CommunicationThreadDTO.ViewScope.Messages.toString());
                    place.setType(Type.lister);
                    AppSite.getPlaceController().goTo(place);
                } else {
                    getView().populate(result);
                }
            }
        }, source);
    }

}
