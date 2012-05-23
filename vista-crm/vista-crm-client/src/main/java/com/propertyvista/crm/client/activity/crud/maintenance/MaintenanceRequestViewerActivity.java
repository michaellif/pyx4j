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
package com.propertyvista.crm.client.activity.crud.maintenance;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.maintenance.MaintenanceRequestViewerView;
import com.propertyvista.crm.client.ui.crud.viewfactories.MaintenanceViewFactory;
import com.propertyvista.crm.rpc.dto.ScheduleDataDTO;
import com.propertyvista.crm.rpc.services.MaintenanceCrudService;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestViewerActivity extends CrmViewerActivity<MaintenanceRequestDTO> implements MaintenanceRequestViewerView.Presenter {

    @SuppressWarnings("unchecked")
    public MaintenanceRequestViewerActivity(CrudAppPlace place) {
        super(place, MaintenanceViewFactory.instance(MaintenanceRequestViewerView.class), (AbstractCrudService<MaintenanceRequestDTO>) GWT
                .create(MaintenanceCrudService.class));
    }

    @Override
    public boolean canEdit() {
        return super.canEdit() & SecurityController.checkBehavior(VistaCrmBehavior.Maintenance);
    }

    @Override
    public void scheduleAction(ScheduleDataDTO data) {
        ((MaintenanceCrudService) getService()).sheduleAction(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, data, getEntityId());
    }

    @Override
    public void resolveAction() {
        ((MaintenanceCrudService) getService()).resolveAction(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId());
    }

    @Override
    public void rateAction(SurveyResponse rate) {
        ((MaintenanceCrudService) getService()).rateAction(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, rate, getEntityId());
    }

    @Override
    public void cancelAction() {
        ((MaintenanceCrudService) getService()).cancelAction(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, getEntityId());
    }

}
