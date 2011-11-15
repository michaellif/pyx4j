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
package com.propertyvista.crm.client.activity.crud.marketing.inquiry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.IListerView.Presenter;

import com.propertyvista.crm.client.ui.crud.marketing.inquiry.InquiryEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.MarketingViewFactory;
import com.propertyvista.crm.rpc.services.InquiryCrudService;
import com.propertyvista.crm.rpc.services.SelectBuildingCrudService;
import com.propertyvista.crm.rpc.services.SelectFloorplanCrudService;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.dto.InquiryDTO;

public class InquiryEditorActivity extends EditorActivityBase<InquiryDTO> implements InquiryEditorView.Presenter {

    private final IListerView.Presenter buildingsLister;

    private final IListerView.Presenter unitsLister;

    @SuppressWarnings("unchecked")
    public InquiryEditorActivity(Place place) {
        super((InquiryEditorView) MarketingViewFactory.instance(InquiryEditorView.class), (AbstractCrudService<InquiryDTO>) GWT
                .create(InquiryCrudService.class), InquiryDTO.class);

        buildingsLister = new ListerActivityBase<Building>(((InquiryEditorView) view).getBuildingListerView(),
                (AbstractCrudService<Building>) GWT.create(SelectBuildingCrudService.class), Building.class);

        unitsLister = new ListerActivityBase<Floorplan>(((InquiryEditorView) view).getFloorplanListerView(),
                (AbstractCrudService<Floorplan>) GWT.create(SelectFloorplanCrudService.class), Floorplan.class);

        setPlace(place);
    }

    @Override
    public Presenter getBuildingPresenter() {
        return buildingsLister;
    }

    @Override
    public Presenter getFloorplanPresenter() {
        return unitsLister;
    }

    @Override
    public void setSelectedBuilding(Building selected) {
        populateUnitLister(selected);
    }

    @Override
    public void setSelectedFloorplan(Floorplan selected) {
        ((InquiryCrudService) service).setSelectedFloorplan(new AsyncCallback<Floorplan>() {

            @Override
            public void onSuccess(Floorplan item) {
                InquiryDTO currentValue = view.getValue();

                currentValue.building().set(item.building());
                currentValue.floorplan().set(item);

                view.populate(currentValue);
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        }, selected.getPrimaryKey());
    }

    @Override
    public void onPopulateSuccess(InquiryDTO result) {
        buildingsLister.populate(0);

        populateUnitLister(result.building());

        super.onPopulateSuccess(result);
    }

    public void populateUnitLister(Building selected) {
        if (!selected.isEmpty()) {
            unitsLister.setParentFiltering(selected.getPrimaryKey());
        }
        unitsLister.populate(0);
    }
}
