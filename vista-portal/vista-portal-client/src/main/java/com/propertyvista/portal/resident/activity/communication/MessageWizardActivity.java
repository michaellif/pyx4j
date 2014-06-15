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
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.rpc.AbstractCrudService.InitializationData;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.site.shared.domain.Notification.NotificationType;

import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.communication.MessageWizardView;
import com.propertyvista.portal.resident.ui.communication.MessageWizardView.MessageWizardPresenter;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap.Message.MessageWizard;
import com.propertyvista.portal.rpc.portal.resident.communication.MessageDTO;
import com.propertyvista.portal.rpc.portal.resident.services.MessagePortalCrudService;
import com.propertyvista.portal.rpc.portal.resident.services.MessagePortalCrudService.MessageInitializationData;
import com.propertyvista.portal.shared.activity.AbstractWizardCrudActivity;

public class MessageWizardActivity extends AbstractWizardCrudActivity<MessageDTO, MessageWizardView> implements MessageWizardPresenter {

    private static final I18n i18n = I18n.get(MessageWizardActivity.class);

    private final MessageWizard place;

    public MessageWizardActivity(AppPlace place) {
        super(MessageWizardView.class, GWT.<MessagePortalCrudService> create(MessagePortalCrudService.class), MessageDTO.class);
        this.place = (MessageWizard) place;
    }

    @Override
    protected void obtainInitializationData(AsyncCallback<InitializationData> callback) {
        MessageInitializationData initData = EntityFactory.create(MessageInitializationData.class);
        initData.initalizedText().setValue(place.getForwardText());
        callback.onSuccess(initData);
    }

    @Override
    protected void onFinish(Key result) {
        Notification message = new Notification(null, i18n.tr("Message submitted Successfully!"), NotificationType.INFO);
        ResidentPortalSite.getPlaceController().showNotification(message, new ResidentPortalSiteMap.Message.MessageView());
    }
}
