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
package com.propertyvista.crm.client.activity.crud.tenant.lead;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView.Presenter;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.tenant.lead.ShowingEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.TenantViewFactory;
import com.propertyvista.crm.rpc.services.SelectBuildingCrudService;
import com.propertyvista.crm.rpc.services.SelectUnitCrudService;
import com.propertyvista.crm.rpc.services.ShowingCrudService;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Showing;

public class ShowingEditorActivity extends EditorActivityBase<Showing> implements ShowingEditorView.Presenter {

    private final IListerView.Presenter buildingsLister;

    private final IListerView.Presenter unitsLister;

    @SuppressWarnings("unchecked")
    public ShowingEditorActivity(Place place) {
        super((ShowingEditorView) TenantViewFactory.instance(ShowingEditorView.class), (AbstractCrudService<Showing>) GWT.create(ShowingCrudService.class),
                Showing.class);

        buildingsLister = new ListerActivityBase<Building>(((ShowingEditorView) view).getBuildingListerView(),
                (AbstractCrudService<Building>) GWT.create(SelectBuildingCrudService.class), Building.class);

        unitsLister = new ListerActivityBase<AptUnit>(((ShowingEditorView) view).getUnitListerView(),
                (AbstractCrudService<AptUnit>) GWT.create(SelectUnitCrudService.class), AptUnit.class);

        setPlace(place);
    }

    @Override
    public Presenter getBuildingPresenter() {
        return buildingsLister;
    }

    @Override
    public Presenter getUnitPresenter() {
        return unitsLister;
    }

    @Override
    public void onPopulateSuccess(Showing result) {
        super.onPopulateSuccess(result);

        buildingsLister.populate(0);
        unitsLister.populate(0);
    }

    @Override
    public void setSelectedBuilding(Building selected) {
        // update unit lister with building units:
        unitsLister.setParentFiltering(selected.getPrimaryKey());
        unitsLister.populate(0);
    }

    @Override
    public void setSelectedUnit(AptUnit selected) {
        ((ShowingCrudService) service).updateValue(new AsyncCallback<AptUnit>() {

            @Override
            public void onSuccess(AptUnit result) {
                Showing current = view.getValue();

                current.unit().set(result);
                current.building().set(result.belongsTo());

                view.populate(current);
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        }, selected.getPrimaryKey());
    }
}
