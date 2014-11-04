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

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.rpc.AbstractListCrudService;
import com.pyx4j.forms.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.crm.client.ui.components.boxes.BuildingSelectionDialog;
import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.rpc.services.selections.SelectBuildingListService;
import com.propertyvista.domain.TimeWindow;
import com.propertyvista.domain.maintenance.MaintenanceRequestCategory;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestEditorViewImpl extends CrmEditorViewImplBase<MaintenanceRequestDTO> implements MaintenanceRequestEditorView {

    public MaintenanceRequestEditorViewImpl() {
        setForm(new MaintenanceRequestForm(this));
    }

    @Override
    public void populate(MaintenanceRequestDTO value) {
        super.populate(value);
        // ensure building
        if (value != null && value.building().isNull()) {
            BuildingSelectionDialog buildingDialog = new BuildingSelectionDialog() {
                @Override
                public boolean onClickOk() {
                    if (getSelectedItems().size() == 1) {
                        Building building = getSelectedItems().iterator().next();
                        getValue().building().set(building);
                        // set window options
                        ((MaintenanceRequestEditorView.Presenter) getPresenter()).getPreferredWindowOptions(new DefaultAsyncCallback<Vector<TimeWindow>>() {
                            @Override
                            public void onSuccess(Vector<TimeWindow> result) {
                                getValue().preferredWindowOptions().addAll(result);
                                getForm().refresh(true);
                            }
                        }, building.getPrimaryKey());
                    }
                    return true;
                }

                @Override
                protected List<ColumnDescriptor> defineColumnDescriptors() {
                    return Arrays.asList( //                    
                            new MemberColumnDescriptor.Builder(proto().propertyCode()).build(), //
                            new MemberColumnDescriptor.Builder(proto().info().name()).build(), //
                            new MemberColumnDescriptor.Builder(proto().info().address()).width("50%").build(), //
                            new MemberColumnDescriptor.Builder(proto().info().address().streetNumber()).searchableOnly().build(), //
                            new MemberColumnDescriptor.Builder(proto().info().address().streetName()).searchableOnly().build(), //
                            new MemberColumnDescriptor.Builder(proto().info().address().city()).searchableOnly().build(), //
                            new MemberColumnDescriptor.Builder(proto().info().address().province()).searchableOnly().build(), //
                            new MemberColumnDescriptor.Builder(proto().info().address().country()).searchableOnly().build() //
                            );
                }

                @Override
                protected AbstractListCrudService<Building> getSelectService() {
                    return GWT.<AbstractListCrudService<Building>> create(SelectBuildingListService.class);
                }
            };
            buildingDialog.getCancelButton().setVisible(false);
            buildingDialog.show();
        }
    }

    @Override
    public MaintenanceRequestDTO getValue() {
        // don't want all the attached info back over the wire
        MaintenanceRequestDTO value = super.getValue();
        if (value != null && !value.category().isNull()) {
            MaintenanceRequestCategory newCat = EntityFactory.createIdentityStub(MaintenanceRequestCategory.class, value.category().getPrimaryKey());
            value.category().set(newCat);
        }
        return value;
    }
}
