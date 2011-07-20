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
package com.propertyvista.crm.client.activity.board;

import java.util.Vector;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.gwt.commons.UnrecoverableClientError;

import com.propertyvista.crm.client.ui.board.BoardView;
import com.propertyvista.crm.rpc.services.dashboard.BoardMetadataServiceBase;
import com.propertyvista.domain.dashboard.AbstractGadgetSettings;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.DashboardMetadata.DashboardType;

public abstract class BoardViewActivity<V extends BoardView> extends AbstractActivity implements BoardView.Presenter {

    protected final V view;

    protected Key entityId;

    protected DashboardType dashboardType;

    public BoardViewActivity(V view, Place place) {
        this.view = view;
        assert (view != null);
        view.setPresenter(this);
        withPlace(place);
    }

    public abstract BoardViewActivity<V> withPlace(Place place);

    protected abstract BoardMetadataServiceBase getService();

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);
        populate();
    }

    @Override
    public void populate() {
        if (isTypedDashboard()) {
            getService().listMetadata(new AsyncCallback<Vector<DashboardMetadata>>() {
                @Override
                public void onSuccess(Vector<DashboardMetadata> result) {
                    for (DashboardMetadata dmd : result) {
                        if (dmd.type().getValue().equals(dashboardType)) {
                            view.fill(dmd);
                            break;
                        }
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    throw new UnrecoverableClientError(caught);
                }
            });
        } else {

            getService().retrieveMetadata(new AsyncCallback<DashboardMetadata>() {
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
        getService().saveMetadata(new AsyncCallback<DashboardMetadata>() {
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
        getService().saveSettings(new AsyncCallback<AbstractGadgetSettings>() {
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
        getService().retrieveSettings(callback, gadgetId);
    }

    protected boolean isTypedDashboard() {
        return (entityId == null);
    }
}