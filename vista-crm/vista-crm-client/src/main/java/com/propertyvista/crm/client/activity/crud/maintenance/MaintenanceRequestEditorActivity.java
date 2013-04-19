/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 23, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.maintenance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.maintenance.MaintenanceRequestEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.MaintenanceViewFactory;
import com.propertyvista.crm.rpc.services.MaintenanceCrudService;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategoryMeta;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestEditorActivity extends CrmEditorActivity<MaintenanceRequestDTO> implements MaintenanceRequestEditorView.Presenter {

    private MaintenanceRequestCategoryMeta meta;

    public MaintenanceRequestEditorActivity(CrudAppPlace place) {
        super(place, MaintenanceViewFactory.instance(MaintenanceRequestEditorView.class), GWT.<MaintenanceCrudService> create(MaintenanceCrudService.class),
                MaintenanceRequestDTO.class);
    }

    @Override
    protected void createNewEntity(final AsyncCallback<MaintenanceRequestDTO> callback) {
        ((MaintenanceCrudService) getService()).createNewRequest(callback, getParentId());
    }

    @Override
    public void getCategoryMeta(final AsyncCallback<MaintenanceRequestCategoryMeta> callback) {
        if (meta == null) {
            ((MaintenanceCrudService) getService()).getCategoryMeta(new DefaultAsyncCallback<MaintenanceRequestCategoryMeta>() {
                @Override
                public void onSuccess(MaintenanceRequestCategoryMeta result) {
                    meta = result;
                    callback.onSuccess(result);
                }
            }, false);
        } else {
            callback.onSuccess(meta);
        }
    }
}
