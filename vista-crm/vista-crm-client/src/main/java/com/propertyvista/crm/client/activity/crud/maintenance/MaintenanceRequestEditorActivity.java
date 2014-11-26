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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.rpc.AbstractCrudService.InitializationData;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.activity.crud.CrmEditorActivity;
import com.propertyvista.crm.client.ui.crud.maintenance.MaintenanceRequestEditorView;
import com.propertyvista.crm.rpc.services.maintenance.MaintenanceCrudService;
import com.propertyvista.domain.maintenance.MaintenanceRequestMetadata;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestEditorActivity extends CrmEditorActivity<MaintenanceRequestDTO> implements MaintenanceRequestEditorView.Presenter {

    public MaintenanceRequestEditorActivity(CrudAppPlace place) {
        super(MaintenanceRequestDTO.class, place, CrmSite.getViewFactory().getView(MaintenanceRequestEditorView.class), GWT
                .<MaintenanceCrudService> create(MaintenanceCrudService.class));
    }

    @Override
    protected void obtainInitializationData(final AsyncCallback<InitializationData> callback) {
        if (getPlace().getInitializationData() != null) {
            callback.onSuccess(getPlace().getInitializationData());
        } else if (getParentId() != null) {
            MaintenanceCrudService.MaintenanceInitializationData id = EntityFactory.create(MaintenanceCrudService.MaintenanceInitializationData.class);
            id.building().set(EntityFactory.createIdentityStub(Building.class, getParentId()));
            callback.onSuccess(id);
        } else {
            super.obtainInitializationData(callback);
        }
    }

    @Override
    public void getCategoryMeta(final AsyncCallback<MaintenanceRequestMetadata> callback, Key buildingId) {
        ((MaintenanceCrudService) getService()).getCategoryMeta(callback, false, buildingId);
    }
}
