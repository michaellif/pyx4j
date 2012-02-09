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

import java.util.ArrayList;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.client.ReportDialog;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.ui.board.BoardView;
import com.propertyvista.crm.client.ui.crud.building.BuildingSelectorDialog;
import com.propertyvista.crm.rpc.services.dashboard.BoardMetadataServiceBase;
import com.propertyvista.crm.rpc.services.reports.DashboardReportService;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.GadgetMetadata;
import com.propertyvista.domain.property.asset.building.Building;

public abstract class BoardViewActivity<V extends BoardView> extends AbstractActivity implements BoardView.Presenter {

    protected final V view;

    protected Key entityId;

    protected Place place;

    public BoardViewActivity(V view, Place place) {
        this.view = view;
        assert (view != null);

        view.setPresenter(this);
        view.setStatusDate(new LogicalDate());
        view.setBuildings(new ArrayList<Building>());
        if (place != null) {
            setPlace(place);
        }
    }

    public void setPlace(Place place) {
        this.place = place;
        entityId = null;

        String val;
        assert (place instanceof AppPlace);
        if ((val = ((AppPlace) place).getFirstArg(CrudAppPlace.ARG_NAME_ID)) != null) {
            entityId = new Key(val);
        }
    }

    public Place getPlace() {
        return place;
    }

    protected abstract BoardMetadataServiceBase getService();

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);
        populate();
    }

    @Override
    public void populate() {
        if (entityId != null) {
            populate(entityId);
        }
    }

    @Override
    public void populate(Key boardId) {
        getService().retrieveMetadata(new AsyncCallback<DashboardMetadata>() {
            @Override
            public void onSuccess(DashboardMetadata result) {
                onPopulateSuccess(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }
        }, boardId);
    }

    public void onPopulateSuccess(DashboardMetadata result) {
        view.populate(result);
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
        }, view.getDashboardMetadata());
    }

    protected void onSaveSuccess(DashboardMetadata result) {
        view.onSaveSuccess();
        onPopulateSuccess(result);
    }

    protected void onSaveFail(Throwable caught) {
        if (!view.onSaveFail(caught)) {
            throw new UnrecoverableClientError(caught);
        }
    }

    @Override
    public void print() {
        EntityQueryCriteria<DashboardMetadata> criteria = new EntityQueryCriteria<DashboardMetadata>(DashboardMetadata.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().id(), view.getDashboardMetadata().getPrimaryKey()));
        ReportDialog.start(GWT.<DashboardReportService> create(DashboardReportService.class), criteria);
    }

    @Override
    public void selectBuildings() {
        new BuildingSelectorDialog(new ArrayList<Building>(1)) {
            @Override
            public boolean onClickOk() {
                view.setBuildings(getSelectedItems());
                return true;
            }
        }.show();
    }

    @Override
    public void selectAllBuildings() {
        view.setBuildings(new ArrayList<Building>(1));
    }

    @Override
    public void onSelectStatusDate() {

    }

// GadgetPresenter:

    @Override
    public void save(final GadgetMetadata gadgetMetadata) {
        getService().saveSettings(new AsyncCallback<GadgetMetadata>() {
            @Override
            public void onSuccess(GadgetMetadata result) {
                gadgetMetadata.set(result);
                view.onSaveSuccess();
            }

            @Override
            public void onFailure(Throwable caught) {
                onSaveFail(caught);
            }
        }, gadgetMetadata);
    }

    @Override
    public void retrieve(Key gadgetId, AsyncCallback<GadgetMetadata> callback) {
        getService().retrieveSettings(callback, gadgetId);
    }

    protected boolean isTypedDashboard() {
        return (entityId == null);
    }

    @Override
    public void onStop() {
        super.onStop();
        view.stop();
    }
}