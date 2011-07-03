/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.crud.unit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.propertyvista.crm.client.ui.crud.unit.UnitEditorView;
import com.propertyvista.crm.client.ui.crud.unit.UnitView;
import com.propertyvista.crm.client.ui.crud.viewfactories.UnitViewFactory;
import com.propertyvista.crm.rpc.services.UnitCrudService;
import com.propertyvista.dto.AptUnitDTO;

import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.client.ui.crud.IListerView.Presenter;
import com.pyx4j.site.rpc.services.AbstractCrudService;

public class UnitEditorActivity extends EditorActivityBase<AptUnitDTO> implements UnitEditorView.Presenter {

    private final UnitActivityDelegate delegate;

    @SuppressWarnings("unchecked")
    public UnitEditorActivity(Place place) {
        super((UnitEditorView) UnitViewFactory.instance(UnitEditorView.class), (AbstractCrudService<AptUnitDTO>) GWT.create(UnitCrudService.class),
                AptUnitDTO.class);
        delegate = new UnitActivityDelegate((UnitView) view);
        withPlace(place);
    }

    @Override
    public Presenter getDetailsPresenter() {
        return delegate.getDetailsPresenter();
    }

    @Override
    public Presenter getOccupanciesPresenter() {
        return delegate.getOccupanciesPresenter();
    }

    @Override
    public void onPopulateSuccess(AptUnitDTO result) {
        super.onPopulateSuccess(result);
        delegate.populate(result.getPrimaryKey());
    }
}
