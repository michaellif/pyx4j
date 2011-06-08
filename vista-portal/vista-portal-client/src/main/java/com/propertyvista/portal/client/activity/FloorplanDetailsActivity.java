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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.client.PortalSite;
import com.propertyvista.portal.client.ui.searchapt.FloorplanDetailsView;
import com.propertyvista.portal.domain.dto.FloorplanDetailsDTO;
import com.propertyvista.portal.rpc.DeploymentConsts;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.ptapp.PtSiteMap;

public class FloorplanDetailsActivity extends AbstractActivity implements FloorplanDetailsView.Presenter {

    private final FloorplanDetailsView view;

    private String floorplanId;

    @Inject
    public FloorplanDetailsActivity(FloorplanDetailsView view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    public FloorplanDetailsActivity withPlace(Place place) {
        floorplanId = ((AppPlace) place).getArgs().get(PortalSiteMap.ARG_FLOORPLAN_ID);
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);
        PortalSite.getPortalSiteServices().retrieveFloorplanDetails(new DefaultAsyncCallback<FloorplanDetailsDTO>() {

            @Override
            public void onSuccess(FloorplanDetailsDTO floorplan) {
                view.populate(floorplan);
            }

        }, new Key(floorplanId));
    }

    @Override
    public void apply() {
        String url = Window.Location.getPath() + "/" + DeploymentConsts.PTAPP_URL + "?" + PtSiteMap.ARG_FLOORPLAN_ID + "=" + floorplanId;
        Window.Location.replace(url);
    }

    @Override
    public void inquiry() {
        // TODO Auto-generated method stub

    }

}
