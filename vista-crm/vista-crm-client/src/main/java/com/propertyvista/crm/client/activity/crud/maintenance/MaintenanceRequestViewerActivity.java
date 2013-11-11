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
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmViewerActivity;
import com.propertyvista.crm.client.ui.crud.maintenance.MaintenanceRequestViewerView;
import com.propertyvista.crm.rpc.services.MaintenanceCrudService;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.maintenance.MaintenanceRequestSchedule;
import com.propertyvista.domain.maintenance.SurveyResponse;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.dto.MaintenanceRequestScheduleDTO;

public class MaintenanceRequestViewerActivity extends CrmViewerActivity<MaintenanceRequestDTO> implements MaintenanceRequestViewerView.Presenter {

    @SuppressWarnings("unchecked")
    public MaintenanceRequestViewerActivity(CrudAppPlace place) {
        super(place, CrmSite.getViewFactory().getView(MaintenanceRequestViewerView.class), (AbstractCrudService<MaintenanceRequestDTO>) GWT
                .create(MaintenanceCrudService.class));
    }

    @Override
    public boolean canEdit() {
        return super.canEdit() & SecurityController.checkBehavior(VistaCrmBehavior.Maintenance);
    }

    @Override
    public void scheduleAction(MaintenanceRequestScheduleDTO schedule) {
        ((MaintenanceCrudService) getService()).sheduleAction(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, schedule, getEntityId());
    }

    @Override
    public void updateProgressAction(MaintenanceRequestSchedule schedule) {
        ((MaintenanceCrudService) getService()).updateProgressAction(new DefaultAsyncCallback<VoidSerializable>() {
            @Override
            public void onSuccess(VoidSerializable result) {
                populate();
            }
        }, schedule.progressNote().getValue(), schedule.getPrimaryKey());
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
        }, (SurveyResponse) rate.detach(), getEntityId());
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

    @Override
    public void getCategoryMeta(final AsyncCallback<MaintenanceRequestMetadata> callback, Key buildingId) {
        ((MaintenanceCrudService) getService()).getCategoryMeta(new DefaultAsyncCallback<MaintenanceRequestMetadata>() {
            @Override
            public void onSuccess(MaintenanceRequestMetadata result) {
                callback.onSuccess(result);
            }
        }, true, buildingId);
    }
}
