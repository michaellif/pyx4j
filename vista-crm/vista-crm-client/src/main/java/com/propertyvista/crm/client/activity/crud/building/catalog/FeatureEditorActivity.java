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
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;

import com.propertyvista.crm.client.ui.crud.building.catalog.feature.FeatureEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.MarketingViewFactory;
import com.propertyvista.crm.rpc.services.building.catalog.FeatureCrudService;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductCatalog;

public class FeatureEditorActivity extends EditorActivityBase<Feature> {

    @SuppressWarnings("unchecked")
    public FeatureEditorActivity(Place place) {
        super(place, MarketingViewFactory.instance(FeatureEditorView.class), (AbstractCrudService<Feature>) GWT.create(FeatureCrudService.class), Feature.class);
    }

    @Override
    protected void createNewEntity(final AsyncCallback<Feature> callback) {
        ((FeatureEditorView) getView()).showSelectTypePopUp(new DefaultAsyncCallback<Feature.Type>() {
            @Override
            public void onSuccess(final Feature.Type type) {
                ((FeatureCrudService) getService()).retrieveCatalog(new DefaultAsyncCallback<ProductCatalog>() {
                    @Override
                    public void onSuccess(ProductCatalog catalog) {
                        Feature entity = EntityFactory.create(getEntityClass());
                        entity.version().type().setValue(type);
                        entity.catalog().set(catalog);

                        callback.onSuccess(entity);
                    }
                }, parentID);
            }
        });
    }
}
