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
package com.propertyvista.portal.web.client.activity.maintenance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceRequestDTO;
import com.propertyvista.portal.rpc.portal.web.services.maintenance.MaintenanceRequestCrudService;
import com.propertyvista.portal.web.client.activity.AbstractWizardCrudActivity;
import com.propertyvista.portal.web.client.ui.maintenance.MaintenanceRequestWizardView;
import com.propertyvista.portal.web.client.ui.maintenance.MaintenanceRequestWizardView.MaintenanceRequestWizardPresenter;

public class MaintenanceRequestWizardActivity extends AbstractWizardCrudActivity<MaintenanceRequestDTO> implements MaintenanceRequestWizardPresenter {

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
        AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Maintenance.MaintenanceRequestConfirmation(result));
    }

}
