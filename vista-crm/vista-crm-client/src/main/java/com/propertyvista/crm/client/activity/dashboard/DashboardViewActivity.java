/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.dashboard;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.ui.dashboard.DashboardView;
import com.propertyvista.crm.client.ui.viewfactories.DashboardVeiwFactory;
import com.propertyvista.crm.rpc.services.DashboardMetadataService;
import com.propertyvista.domain.dashboard.AbstractGadgetSettings;
import com.propertyvista.domain.dashboard.DashboardMetadata;

public class DashboardViewActivity extends AbstractActivity implements DashboardView.Presenter {

    private final DashboardView view;

    private final DashboardMetadataService service = GWT.create(DashboardMetadataService.class);

    private Key entityId;

    public DashboardViewActivity(Place place) {
        view = (DashboardView) DashboardVeiwFactory.instance(DashboardView.class);
        assert (view != null);
        view.setPresenter(this);
        withPlace(place);
    }

    public DashboardViewActivity(DashboardView view, Place place) {
        this.view = view;
        assert (view != null);
        view.setPresenter(this);
        withPlace(place);
    }

    public DashboardViewActivity withPlace(Place place) {
        entityId = null;
        String id;
        if ((id = ((AppPlace) place).getArg(CrudAppPlace.ARG_NAME_ITEM_ID)) != null) {
            entityId = new Key(id);
        } else { // building dashboard?
            entityId = new Key(2);
        }

        assert (entityId != null);
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);
        populate();
    }

    @Override
    public void populate() {
        if (isNewItem()) {
            view.fill(EntityFactory.create(DashboardMetadata.class));
        } else {

            service.retrieveMetadata(new AsyncCallback<DashboardMetadata>() {
                @Override
                public void onSuccess(DashboardMetadata result) {
                    view.fill(result);
                }

                @Override
                public void onFailure(Throwable caught) {
                    throw new UnrecoverableClientError(caught);
                }
            }, entityId);
        }
    }

    @Override
    public void save() {
        service.saveMetadata(new AsyncCallback<DashboardMetadata>() {
            @Override
            public void onSuccess(DashboardMetadata result) {
                onSaveSuccess(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                onSaveFail(caught);
            }
        }, view.getData());
    }

    protected void onSaveSuccess(DashboardMetadata result) {
        view.onSaveSuccess();
    }

    protected void onSaveFail(Throwable caught) {
        if (!view.onSaveFail(caught)) {
            throw new UnrecoverableClientError(caught);
        }
    }

// GadgetPresenter:

    @Override
    public void save(Key gadgetId, AbstractGadgetSettings settings) {
        service.saveSettings(new AsyncCallback<AbstractGadgetSettings>() {
            @Override
            public void onSuccess(AbstractGadgetSettings result) {
                view.onSaveSuccess();
            }

            @Override
            public void onFailure(Throwable caught) {
                onSaveFail(caught);
            }
        }, gadgetId, settings);
    }

    @Override
    public void retrieve(Key gadgetId, AsyncCallback<AbstractGadgetSettings> callback) {
        service.retrieveSettings(callback, gadgetId);
    }

    protected boolean isNewItem() {
        assert (entityId != null);
        return (entityId.toString().equals(CrudAppPlace.ARG_VALUE_NEW_ITEM));
    }
}