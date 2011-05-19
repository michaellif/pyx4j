/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-17
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.viewers;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.ui.vewers.IBuildingViewerView;
import com.propertyvista.crm.client.ui.vewers.IViewerView;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.BuildingCrudService;
import com.propertyvista.domain.property.asset.Building;

public class BuildingViewerActivity extends AbstractActivity implements IViewerView.Presenter {

    private final IBuildingViewerView view;

    private final BuildingCrudService service = GWT.create(BuildingCrudService.class);

    private long entityId = -1;

    @Inject
    public BuildingViewerActivity(IBuildingViewerView view) {
        this.view = view;
        view.setPresenter(this);
    }

    public BuildingViewerActivity withPlace(Place place) {
        String stepArg = ((AppPlace) place).getArgs().get(CrmSiteMap.ARG_NAME_ITEM_ID);
        if (stepArg != null) {
            entityId = Long.valueOf(stepArg);
        }

        return this;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        panel.setWidget(view);
        populate();
    }

    @Override
    public void populate() {
        if (service != null) {
            service.retrieve(new AsyncCallback<Building>() {

                @Override
                public void onSuccess(Building result) {
                    view.populate(result);
                }

                @Override
                public void onFailure(Throwable caught) {
                }
            }, entityId);
        }
    }
}
