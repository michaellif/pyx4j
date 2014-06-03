/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-02
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.resident.activity.communication;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.rpc.AbstractCrudService.InitializationData;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.MessageWizardAppPlace;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.site.shared.domain.Notification.NotificationType;

import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.communication.MessageWizardView;
import com.propertyvista.portal.resident.ui.communication.MessageWizardView.MessageWizardPresenter;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.communication.MessageDTO;
import com.propertyvista.portal.rpc.portal.resident.services.MessagePortalCrudService;
import com.propertyvista.portal.shared.activity.AbstractWizardCrudActivity;

public class CommunicationMessageWizardActivity extends AbstractWizardCrudActivity<MessageDTO, MessageWizardView> implements MessageWizardPresenter {

    private static final I18n i18n = I18n.get(CommunicationMessageWizardActivity.class);

    private MessageWizardAppPlace place;

    public CommunicationMessageWizardActivity(AppPlace place) {
        super(MessageWizardView.class, GWT.<MessagePortalCrudService> create(MessagePortalCrudService.class), MessageDTO.class);
        if (place instanceof MessageWizardAppPlace) {
            this.place = (MessageWizardAppPlace) place;
        } else {
            place = null;
        }
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);

        obtainInitializationData(new DefaultAsyncCallback<AbstractCrudService.InitializationData>() {
            @Override
            public void onSuccess(InitializationData result) {
                ((MessagePortalCrudService) getService()).init(new DefaultAsyncCallback<MessageDTO>() {
                    @Override
                    public void onSuccess(MessageDTO result) {
                        if (result != null && place != null && place.getForwardText() != null) {
                            result.text().setValue(place.getForwardText());
                        }
                        getView().populate(result);
                    }
                }, result);
            }
        });
    }

    @Override
    protected void onFinish(Key result) {
        Notification message = new Notification(null, i18n.tr("Message submitted Successfully!"), NotificationType.INFO);
        ResidentPortalSite.getPlaceController().showNotification(message, new ResidentPortalSiteMap.Message.MessageView());
    }
}
