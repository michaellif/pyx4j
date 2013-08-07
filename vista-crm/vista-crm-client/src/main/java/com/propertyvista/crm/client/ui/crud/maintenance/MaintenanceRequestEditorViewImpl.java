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
package com.propertyvista.crm.client.ui.crud.maintenance;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.dto.MaintenanceRequestDTO;
import com.propertyvista.dto.MaintenanceRequestMetadataDTO;
import com.propertyvista.shared.config.VistaFeatures;

public class MaintenanceRequestEditorViewImpl extends CrmEditorViewImplBase<MaintenanceRequestDTO> implements MaintenanceRequestEditorView {

    private MaintenanceRequestMetadataDTO categoryMeta;

    public MaintenanceRequestEditorViewImpl() {
        setForm(new MaintenanceRequestForm(this));
    }

    @Override
    public void setPresenter(IForm.Presenter presenter) {
        super.setPresenter(presenter);
        if (categoryMeta != null) {
            ((MaintenanceRequestForm) getForm()).setMaintenanceRequestCategoryMeta(categoryMeta);
        } else if (presenter != null) {
            if (VistaFeatures.instance().yardiInterfaces() > 1) {
                // for multiple yardi interfaces ask to select building first
                new BuildingSelectorDialog(false) {
                    @Override
                    public boolean onClickOk() {
                        if (getSelectedItems().isEmpty()) {
                            return false;
                        }
                        ((MaintenanceRequestEditorView.Presenter) getPresenter()).getCategoryMeta(new DefaultAsyncCallback<MaintenanceRequestMetadataDTO>() {
                            @Override
                            public void onSuccess(MaintenanceRequestMetadataDTO meta) {
                                MaintenanceRequestEditorViewImpl.this.categoryMeta = meta;
                                ((MaintenanceRequestForm) getForm()).setMaintenanceRequestCategoryMeta(meta);
                            }
                        }, getSelectedItems().get(0));
                        return true;
                    }
                }.show();
            } else {
                // just use null for building
                ((MaintenanceRequestEditorView.Presenter) getPresenter()).getCategoryMeta(new DefaultAsyncCallback<MaintenanceRequestMetadataDTO>() {
                    @Override
                    public void onSuccess(MaintenanceRequestMetadataDTO meta) {
                        MaintenanceRequestEditorViewImpl.this.categoryMeta = meta;
                        ((MaintenanceRequestForm) getForm()).setMaintenanceRequestCategoryMeta(meta);
                    }
                }, null);
            }
        }
    }

    @Override
    public MaintenanceRequestDTO getValue() {
        // don't want all the attached info back over the wire
        MaintenanceRequestDTO value = super.getValue();
        MaintenanceRequestCategory newCat = EntityFactory.createIdentityStub(MaintenanceRequestCategory.class, value.category().getPrimaryKey());
        value.category().set(newCat);
        return value;
    }
}
