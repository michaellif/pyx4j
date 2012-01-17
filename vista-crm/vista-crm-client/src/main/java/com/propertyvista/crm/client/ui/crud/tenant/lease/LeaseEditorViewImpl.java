/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.tenant.lease;

import java.util.EnumSet;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerBase.ItemSelectionHandler;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;

import com.propertyvista.common.client.ui.components.SelectTypeDialog;
import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.client.ui.crud.building.SelectedBuildingLister;
import com.propertyvista.crm.client.ui.crud.unit.SelectedUnitLister;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.dto.LeaseDTO;

public class LeaseEditorViewImpl extends CrmEditorViewImplBase<LeaseDTO> implements LeaseEditorView {

    private final IListerView<Building> buildingLister;

    private final IListerView<AptUnit> unitLister;

//    private final IListerView<Tenant> tenantLister;

    public LeaseEditorViewImpl() {
        super(CrmSiteMap.Tenants.Lease.class);

        buildingLister = new ListerInternalViewImplBase<Building>(new SelectedBuildingLister(/* readOnly */));
        buildingLister.getLister().addItemSelectionHandler(new ItemSelectionHandler<Building>() {
            @Override
            public void onSelect(Building selectedItem) {
                ((LeaseEditorView.Presenter) getPresenter()).setSelectedBuilding(selectedItem);
            }
        });
        unitLister = new ListerInternalViewImplBase<AptUnit>(new SelectedUnitLister(/* readOnly */));
//        tenantLister = new ListerInternalViewImplBase<Tenant>(new SelectTenantLister(/* readOnly */));
//        tenantLister.getLister().setHasCheckboxColumn(true);

        // set main form here: 
        setForm(new LeaseEditorForm());
    }

    @Override
    public IListerView<Building> getBuildingListerView() {
        return buildingLister;
    }

    @Override
    public IListerView<AptUnit> getUnitListerView() {
        return unitLister;
    }

//    @Override
//    public IListerView<Tenant> getTenantListerView() {
//        return tenantLister;
//    }

    @Override
    public void showSelectTypePopUp(final AsyncCallback<Service.Type> callback) {
        new SelectTypeDialog<Service.Type>(i18n.tr("Select Lease Type"), EnumSet.allOf(Service.Type.class)) {
            @Override
            public boolean onClickOk() {
                callback.onSuccess(getSelectedType());
                return true;
            }

            @Override
            public String defineHeight() {
                return "100px";
            };

            @Override
            public String defineWidth() {
                return "300px";
            };
        }.show();
    }
}
