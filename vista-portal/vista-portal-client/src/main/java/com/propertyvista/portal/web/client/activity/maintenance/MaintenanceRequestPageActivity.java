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
package com.propertyvista.portal.web.client.activity.maintenance;

import com.google.gwt.core.client.GWT;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.web.dto.maintenance.MaintenanceRequestDTO;
import com.propertyvista.portal.rpc.portal.web.services.maintenance.MaintenanceRequestCrudService;
import com.propertyvista.portal.web.client.activity.AbstractEditorActivity;
import com.propertyvista.portal.web.client.ui.maintenance.MaintenanceRequestPageView;
import com.propertyvista.portal.web.client.ui.maintenance.MaintenanceRequestPageView.MaintenanceRequestPagePresenter;

public class MaintenanceRequestPageActivity extends AbstractEditorActivity<MaintenanceRequestDTO> implements MaintenanceRequestPagePresenter {

    public MaintenanceRequestPageActivity(AppPlace place) {
        super(MaintenanceRequestPageView.class, GWT.<MaintenanceRequestCrudService> create(MaintenanceRequestCrudService.class), place);
    }

    @Override
    public void rateRequest(SurveyResponse rate) {
        ((MaintenanceRequestCrudService) getService()).rateMaintenanceRequest(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                edit();
            }
        }, getEntityId(), rate.rating().getValue());
    }

    @Override
    public void cancelRequest() {
        ((MaintenanceRequestCrudService) getService()).cancelMaintenanceRequest(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                AppSite.getPlaceController().goTo(new PortalSiteMap.Resident.Maintenance());
            }
        }, getEntityId());
    }

}
