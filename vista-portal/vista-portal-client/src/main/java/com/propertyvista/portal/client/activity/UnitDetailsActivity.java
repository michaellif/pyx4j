/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 18, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.portal.client.PortalSite;
import com.propertyvista.portal.client.ui.searchapt.UnitDetailsView;
import com.propertyvista.portal.domain.dto.FloorplanDetailsDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

public class UnitDetailsActivity extends AbstractActivity implements UnitDetailsView.Presenter {

    private final UnitDetailsView view;

    private String unitId;

    @Inject
    public UnitDetailsActivity(UnitDetailsView view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    public UnitDetailsActivity withPlace(Place place) {
        unitId = ((AppPlace) place).getArgs().get(PortalSiteMap.ARG_FLOORPLAN_ID);
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);
        PortalSite.getPortalSiteServices().retrieveFloorplanDetails(new DefaultAsyncCallback<FloorplanDetailsDTO>() {

            @Override
            public void onSuccess(FloorplanDetailsDTO unit) {
                view.populate(unit);
            }

        }, new Key(unitId));
    }

    @Override
    public void apply() {
        // TODO Auto-generated method stub

    }

    @Override
    public void inquiry() {
        // TODO Auto-generated method stub

    }

}
