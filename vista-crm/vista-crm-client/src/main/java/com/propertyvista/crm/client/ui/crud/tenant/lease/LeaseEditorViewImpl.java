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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerBase.ItemSelectionHandler;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;

import com.propertyvista.common.client.ui.components.OkCancelBox;
import com.propertyvista.common.client.ui.components.ShowPopUpBox;
import com.propertyvista.crm.client.ui.crud.CrmEditorViewImplBase;
import com.propertyvista.crm.client.ui.crud.building.SelectedBuildingLister;
import com.propertyvista.crm.client.ui.crud.tenant.SelectTenantLister;
import com.propertyvista.crm.client.ui.crud.unit.SelectedUnitLister;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.dto.LeaseDTO;

public class LeaseEditorViewImpl extends CrmEditorViewImplBase<LeaseDTO> implements LeaseEditorView {

    private final IListerView<Building> buildingLister;

    private final IListerView<AptUnit> unitLister;

    private final IListerView<Tenant> tenantLister;

    public LeaseEditorViewImpl() {
        super(CrmSiteMap.Tenants.Lease.class);

        buildingLister = new ListerInternalViewImplBase<Building>(new SelectedBuildingLister(/* readOnly */));
        buildingLister.getLister().addItemSelectionHandler(new ItemSelectionHandler<Building>() {
            @Override
            public void onSelect(Building selectedItem) {
                ((LeaseEditorView.Presenter) presenter).setSelectedBuilding(selectedItem);
            }
        });
        unitLister = new ListerInternalViewImplBase<AptUnit>(new SelectedUnitLister(/* readOnly */));
        tenantLister = new ListerInternalViewImplBase<Tenant>(new SelectTenantLister(/* readOnly */));

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

    @Override
    public IListerView<Tenant> getTenantListerView() {
        return tenantLister;
    }

    @Override
    public void showSelectTypePopUp(final AsyncCallback<Service.Type> callback) {
        new ShowPopUpBox<SelectTypeBox>(new SelectTypeBox()) {
            @Override
            protected void onClose(SelectTypeBox box) {
                callback.onSuccess(box.getSelectedType());
            }
        };
    }

    private class SelectTypeBox extends OkCancelBox {

        private final CComboBox<Service.Type> type = new CComboBox<Service.Type>(i18n.tr("Types"), true);

        public SelectTypeBox() {
            super(i18n.tr("Select Lease Type"), true);
            setContent(createContent());
        }

        protected Widget createContent() {
            type.setOptions(EnumSet.allOf(Service.Type.class));
            type.setValue(Service.Type.residentialUnit);
            type.setWidth("100%");

            HorizontalPanel main = new HorizontalPanel();
            main.add(type);
            main.setSpacing(4);
            main.setWidth("100%");
            return main;
        }

        @Override
        protected void setSize() {
            setSize("300px", "100px");
        }

        public Service.Type getSelectedType() {
            return type.getValue();
        }
    }
}
