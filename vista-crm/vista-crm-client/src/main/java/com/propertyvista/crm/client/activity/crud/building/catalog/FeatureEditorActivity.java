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
package com.propertyvista.crm.client.activity.crud.building.catalog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.building.catalog.FeatureEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.MarketingViewFactory;
import com.propertyvista.crm.rpc.services.FeatureCrudService;
import com.propertyvista.domain.financial.offering.Feature;

public class FeatureEditorActivity extends EditorActivityBase<Feature> {

    private Feature.Type itemType;

    @SuppressWarnings("unchecked")
    public FeatureEditorActivity(Place place) {
        super((FeatureEditorView) MarketingViewFactory.instance(FeatureEditorView.class), (AbstractCrudService<Feature>) GWT.create(FeatureCrudService.class),
                Feature.class);
        withPlace(place);
    }

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
        if (isNewItem()) {
            ((FeatureEditorView) view).showSelectTypePopUp(new AsyncCallback<Feature.Type>() {
                @Override
                public void onSuccess(Feature.Type result) {
                    itemType = result;
                    FeatureEditorActivity.super.start(panel, eventBus);
                }

                @Override
                public void onFailure(Throwable caught) {
                    throw new UnrecoverableClientError(caught);
                }
            });
        } else {
            super.start(panel, eventBus);
        }
    }

    @Override
    protected void initNewItem(Feature entity) {
        super.initNewItem(entity);
        entity.type().setValue(itemType);
    }
}
