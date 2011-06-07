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

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.client.PortalSite;
import com.propertyvista.portal.client.ui.searchapt.ApartmentDetailsView;
import com.propertyvista.portal.domain.dto.FloorplanDTO;
import com.propertyvista.portal.domain.dto.PropertyDetailsDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class ApartmentDetailsActivity extends AbstractActivity implements ApartmentDetailsView.Presenter {

    private static final Logger log = LoggerFactory.getLogger(ApartmentDetailsActivity.class);

    private final ApartmentDetailsView view;

    private String propertyId;

    @Inject
    public ApartmentDetailsActivity(ApartmentDetailsView view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    public ApartmentDetailsActivity withPlace(Place place) {
        propertyId = ((AppPlace) place).getArgs().get(PortalSiteMap.ARG_PROPERTY_ID);
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);
        PortalSite.getPortalSiteServices().retrievePropertyDetails(new DefaultAsyncCallback<PropertyDetailsDTO>() {

            @Override
            public void onSuccess(PropertyDetailsDTO property) {
                view.populate(property);
            }

        }, new Key(propertyId));

    }

    @Override
    public void showFloorplan(FloorplanDTO floorplan) {
        AppPlace place = new PortalSiteMap.FindApartment.FloorplanDetails();
        HashMap<String, String> args = new HashMap<String, String>();
        //TODO floorplan id is null. fix this
        args.put(PortalSiteMap.ARG_FLOORPLAN_ID, floorplan.id().getValue().toString());
        place.setArgs(args);
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public void navigTo(Place place) {
        AppSite.getPlaceController().goTo(place);
    }

}
