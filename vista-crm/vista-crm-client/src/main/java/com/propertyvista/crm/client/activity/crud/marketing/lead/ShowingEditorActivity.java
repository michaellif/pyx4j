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
package com.propertyvista.crm.client.activity.crud.marketing.lead;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;

import com.propertyvista.crm.client.ui.crud.marketing.lead.ShowingEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.MarketingViewFactory;
import com.propertyvista.crm.rpc.services.tenant.lead.ShowingCrudService;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Showing;

public class ShowingEditorActivity extends EditorActivityBase<Showing> implements ShowingEditorView.Presenter {

    @SuppressWarnings("unchecked")
    public ShowingEditorActivity(Place place) {
        super(place, MarketingViewFactory.instance(ShowingEditorView.class), (AbstractCrudService<Showing>) GWT.create(ShowingCrudService.class), Showing.class);
    }

    @Override
    public void onPopulateSuccess(Showing result) {
        super.onPopulateSuccess(result);
    }

    @Override
    public void setSelectedUnit(AptUnit selected) {
        ((ShowingCrudService) getService()).updateValue(new AsyncCallback<AptUnit>() {

            @Override
            public void onSuccess(AptUnit result) {
                Showing current = getView().getValue().duplicate();

                current.unit().set(result);
                current.building().set(result.belongsTo());

                getView().populate(current);
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        }, selected.getPrimaryKey());
    }
}
