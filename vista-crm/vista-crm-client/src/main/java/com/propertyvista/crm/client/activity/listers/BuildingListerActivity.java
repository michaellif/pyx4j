/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-03
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.listers;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;

import com.propertyvista.crm.client.ui.listers.IBuildingListerView;
import com.propertyvista.crm.client.ui.listers.IListerView;
import com.propertyvista.crm.rpc.services.BuildingCrudService;
import com.propertyvista.domain.property.asset.Building;

public class BuildingListerActivity extends AbstractActivity implements IListerView.Presenter {

    private final IBuildingListerView view;

    @Inject
    public BuildingListerActivity(IBuildingListerView view) {
        this.view = view;
        view.setPresenter(this);
        populateData(0);
    }

    public BuildingListerActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);
    }

    @Override
    public void populateData(final int pageNumber) {
        BuildingCrudService service = GWT.create(BuildingCrudService.class);
        if (service != null) {
            EntitySearchCriteria<Building> criteria = new EntitySearchCriteria<Building>(Building.class);
            criteria.setPageSize(view.getPageSize());
            criteria.setPageNumber(pageNumber);

            service.search(new AsyncCallback<EntitySearchResult<Building>>() {
                @Override
                public void onFailure(Throwable caught) {
                }

                @Override
                public void onSuccess(EntitySearchResult<Building> result) {
                    view.populateData(result.getData(), pageNumber, result.hasMoreData());
                }
            }, criteria);
        }
    }
}
