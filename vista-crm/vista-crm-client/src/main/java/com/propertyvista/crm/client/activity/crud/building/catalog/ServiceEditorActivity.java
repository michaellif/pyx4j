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
import com.pyx4j.site.client.activity.crud.EditorActivityBase;

import com.propertyvista.crm.client.ui.crud.building.catalog.service.ServiceEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.MarketingViewFactory;
import com.propertyvista.crm.rpc.services.building.catalog.ServiceCrudService;
import com.propertyvista.domain.financial.offering.Service;

public class ServiceEditorActivity extends EditorActivityBase<Service> implements ServiceEditorView.Presenter {

    @SuppressWarnings("unchecked")
    public ServiceEditorActivity(Place place) {
        super(place, MarketingViewFactory.instance(ServiceEditorView.class), (AbstractCrudService<Service>) GWT.create(ServiceCrudService.class), Service.class);
    }

    @Override
    protected void createNewEntity(final AsyncCallback<Service> callback) {
        ((ServiceEditorView) view).showSelectTypePopUp(new AsyncCallback<Service.Type>() {
            @Override
            public void onSuccess(Service.Type type) {
                Service entity = EntityFactory.create(entityClass);
                entity.type().setValue(type);

                callback.onSuccess(entity);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }
}
