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

import com.propertyvista.crm.client.ui.crud.building.catalog.ServiceEditorView;
import com.propertyvista.crm.client.ui.crud.viewfactories.MarketingViewFactory;
import com.propertyvista.crm.rpc.services.ServiceCrudService;
import com.propertyvista.domain.financial.offering.Service;

public class ServiceEditorActivity extends EditorActivityBase<Service> {

    private Service.Type itemType;

    @SuppressWarnings("unchecked")
    public ServiceEditorActivity(Place place) {
        super((ServiceEditorView) MarketingViewFactory.instance(ServiceEditorView.class), (AbstractCrudService<Service>) GWT.create(ServiceCrudService.class),
                Service.class);
        withPlace(place);
    }

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
        if (isNewItem()) {
            ((ServiceEditorView) view).showSelectTypePopUp(new AsyncCallback<Service.Type>() {
                @Override
                public void onSuccess(Service.Type result) {
                    itemType = result;
                    ServiceEditorActivity.super.start(panel, eventBus);
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
    protected void initNewItem(Service entity) {
        super.initNewItem(entity);
        entity.type().setValue(itemType);
    }
}
