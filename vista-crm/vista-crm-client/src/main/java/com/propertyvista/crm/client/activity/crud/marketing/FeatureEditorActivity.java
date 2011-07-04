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
package com.propertyvista.crm.client.activity.crud.marketing;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.client.activity.crud.EditorActivityBase;
import com.pyx4j.site.client.ui.crud.IListerView.Presenter;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.client.ui.crud.marketing.FeatureEditorView;
import com.propertyvista.crm.client.ui.crud.marketing.FeatureView;
import com.propertyvista.crm.client.ui.crud.viewfactories.MarketingViewFactory;
import com.propertyvista.crm.rpc.services.FeatureCrudService;
import com.propertyvista.domain.financial.offering.Feature;

public class FeatureEditorActivity extends EditorActivityBase<Feature> implements FeatureEditorView.Presenter {

    private final FeatureActivityDelegate delegate;

    private Class<? extends Feature> featureClass;

    @SuppressWarnings("unchecked")
    public FeatureEditorActivity(Place place) {
        super((FeatureEditorView) MarketingViewFactory.instance(FeatureEditorView.class), (AbstractCrudService<Feature>) GWT.create(FeatureCrudService.class),
                Feature.class);
        delegate = new FeatureActivityDelegate((FeatureView) view);
        withPlace(place);
    }

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
        if (isNewItem()) {
            ((FeatureEditorView) view).showSelectTypePopUp(new AsyncCallback<Class<? extends Feature>>() {
                @Override
                public void onSuccess(Class<? extends Feature> result) {
                    featureClass = result;
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
    public Presenter getConcessionsPresenter() {
        return delegate.getConcessionsPresenter();
    }

    @Override
    public void onPopulateSuccess(Feature result) {
        super.onPopulateSuccess(result);
        delegate.populate(result.getPrimaryKey());
    }

    @Override
    protected void createNewEntity(AsyncCallback<Feature> callback) {
        assert (featureClass != null);
        callback.onSuccess(EntityFactory.create(featureClass));
    }
}
