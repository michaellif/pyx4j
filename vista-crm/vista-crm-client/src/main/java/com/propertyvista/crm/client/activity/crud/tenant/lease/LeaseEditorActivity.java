/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.tenant.lease;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.client.ui.crud.IListerView.Presenter;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.tenant.lease.LeaseEditorView;
import com.propertyvista.crm.client.ui.crud.tenant.lease.LeaseView;
import com.propertyvista.crm.client.ui.crud.viewfactories.TenantViewFactory;
import com.propertyvista.crm.rpc.services.LeaseCrudService;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.dto.LeaseDTO;

public class LeaseEditorActivity extends EditorActivityBase<LeaseDTO> implements LeaseEditorView.Presenter {

    private final LeaseActivityDelegate delegate;

    @SuppressWarnings("unchecked")
    public LeaseEditorActivity(Place place) {
        super((LeaseEditorView) TenantViewFactory.instance(LeaseEditorView.class), (AbstractCrudService<LeaseDTO>) GWT.create(LeaseCrudService.class),
                LeaseDTO.class);
        delegate = new LeaseActivityDelegate((LeaseView) view);
        withPlace(place);
    }

    @Override
    public Presenter getBuildingPresenter() {
        return delegate.getBuildingPresenter();
    }

    @Override
    public Presenter getUnitPresenter() {
        return delegate.getUnitPresenter();
    }

    @Override
    public Presenter getTenantPresenter() {
        return delegate.getTenantPresenter();
    }

    @Override
    public void onPopulateSuccess(LeaseDTO result) {
        super.onPopulateSuccess(result);
        delegate.populate(result);
    }

    @Override
    public void setSelectedBuilding(Building selected) {
        LeaseDTO current = view.getValue();
        current.selectedBuilding().set(selected);
        onPopulateSuccess(current);
    }

    @Override
    public void setSelectedUnit(AptUnit selected) {
        LeaseDTO current = view.getValue();
        current.unit().set(selected);
        onPopulateSuccess(current);
    }
}
