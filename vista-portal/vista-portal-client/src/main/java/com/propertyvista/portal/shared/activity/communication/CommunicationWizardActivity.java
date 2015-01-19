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
 */
package com.propertyvista.portal.shared.activity.communication;

import com.google.gwt.core.client.GWT;

import com.pyx4j.commons.Key;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.site.shared.domain.Notification.NotificationType;

import com.propertyvista.dto.communication.CommunicationThreadDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.services.CommunicationPortalCrudService;
import com.propertyvista.portal.shared.activity.AbstractWizardCrudActivity;
import com.propertyvista.portal.shared.ui.communication.CommunicationWizardView;
import com.propertyvista.portal.shared.ui.communication.CommunicationWizardView.MessageWizardPresenter;

public class CommunicationWizardActivity extends AbstractWizardCrudActivity<CommunicationThreadDTO, CommunicationWizardView> implements MessageWizardPresenter {

    private static final I18n i18n = I18n.get(CommunicationWizardActivity.class);

    public CommunicationWizardActivity(AppPlace place) {
        super(CommunicationWizardView.class, GWT.<CommunicationPortalCrudService> create(CommunicationPortalCrudService.class), CommunicationThreadDTO.class);
    }

    @Override
    protected void onFinish(Key result) {
        Notification message = new Notification(null, i18n.tr("Message submitted Successfully!"), NotificationType.INFO);
        AppSite.getPlaceController().showNotification(message, new PortalSiteMap.Message.MessageView());
    }
}
