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
package com.propertyvista.portal.resident.activity.maintenance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.site.shared.domain.Notification.NotificationType;

import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.portal.resident.ResidentPortalSite;
import com.propertyvista.portal.resident.ui.maintenance.MaintenanceRequestWizardView;
import com.propertyvista.portal.resident.ui.maintenance.MaintenanceRequestWizardView.MaintenanceRequestWizardPresenter;
import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.rpc.portal.resident.dto.maintenance.MaintenanceRequestDTO;
import com.propertyvista.portal.rpc.portal.resident.services.maintenance.MaintenanceRequestCrudService;
import com.propertyvista.portal.shared.activity.AbstractWizardFormCrudActivity;

public class MaintenanceRequestWizardActivity extends AbstractWizardFormCrudActivity<MaintenanceRequestDTO> implements MaintenanceRequestWizardPresenter {

    private static final I18n i18n = I18n.get(MaintenanceRequestWizardActivity.class);

    public MaintenanceRequestWizardActivity(AppPlace place) {
        super(MaintenanceRequestWizardView.class, GWT.<MaintenanceRequestCrudService> create(MaintenanceRequestCrudService.class), MaintenanceRequestDTO.class);
    }

    @Override
    public void getCategoryMeta(final AsyncCallback<MaintenanceRequestMetadata> callback) {
        ((MaintenanceRequestCrudService) getService()).getCategoryMeta(new DefaultAsyncCallback<MaintenanceRequestMetadata>() {
            @Override
            public void onSuccess(MaintenanceRequestMetadata result) {
                callback.onSuccess(result);
            }
        }, false);
    }

    @Override
    protected void onFinish(Key result) {
        Notification message = new Notification(null, i18n.tr("Maintenance Request submitted Successfully!"), NotificationType.INFO);
        ResidentPortalSite.getPlaceController().showNotification(message, new ResidentPortalSiteMap.Maintenance());
    }
}
