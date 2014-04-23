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

import com.pyx4j.commons.Key;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.site.shared.domain.Notification.NotificationType;

import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.communication.CommunicationMessageWizardView;
import com.propertyvista.portal.resident.ui.communication.CommunicationMessageWizardView.CommunicationMessageWizardPresenter;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.communication.CommunicationMessageDTO;
import com.propertyvista.portal.rpc.portal.resident.services.CommunicationMessagePortalCrudService;
import com.propertyvista.portal.shared.activity.AbstractWizardCrudActivity;

public class CommunicationMessageWizardActivity extends AbstractWizardCrudActivity<CommunicationMessageDTO, CommunicationMessageWizardView> implements
        CommunicationMessageWizardPresenter {

    private static final I18n i18n = I18n.get(CommunicationMessageWizardActivity.class);

    public CommunicationMessageWizardActivity(AppPlace place) {
        super(CommunicationMessageWizardView.class, GWT.<CommunicationMessagePortalCrudService> create(CommunicationMessagePortalCrudService.class),
                CommunicationMessageDTO.class);
    }

    @Override
    protected void onFinish(Key result) {
        Notification message = new Notification(null, i18n.tr("Message submitted Successfully!"), NotificationType.INFO);
        ResidentPortalSite.getPlaceController().showNotification(message, new ResidentPortalSiteMap.CommunicationMessage.CommunicationMessageView());
    }
}
