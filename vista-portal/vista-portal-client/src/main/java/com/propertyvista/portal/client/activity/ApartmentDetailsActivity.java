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

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.portal.client.ui.ApartmentDetailsView;
import com.propertyvista.portal.domain.dto.AmenityDTO;
import com.propertyvista.portal.domain.dto.AptUnitDTO;
import com.propertyvista.portal.domain.dto.FloorplanDTO;
import com.propertyvista.portal.domain.dto.PropertyDetailsDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

public class ApartmentDetailsActivity extends AbstractActivity implements ApartmentDetailsView.Presenter {

    private final ApartmentDetailsView view;

    @Inject
    public ApartmentDetailsActivity(ApartmentDetailsView view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    public ApartmentDetailsActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);
        //TODO get property details passed from the PropertyMap page

        PropertyDetailsDTO property = EntityFactory.create(PropertyDetailsDTO.class);
        property.address().street1().setValue("320 Avenue Road");
        property.address().city().setValue("Toronto");
        property.location().setValue(new GeoPoint(43.697665, -79.402313));
        property.price().setValue(1700.00);

        AmenityDTO amenity = EntityFactory.create(AmenityDTO.class);
        amenity.name().setValue("Pool");
        property.amenities().add(amenity);
        amenity = EntityFactory.create(AmenityDTO.class);
        amenity.name().setValue("Somthing else");
        property.amenities().add(amenity);

        FloorplanDTO floorplan = EntityFactory.create(FloorplanDTO.class);
        floorplan.name().setValue("floorplan1");
        floorplan.area().setValue(22);
        property.floorplans().add(floorplan);
        floorplan = EntityFactory.create(FloorplanDTO.class);
        floorplan.name().setValue("floorplan2");
        floorplan.area().setValue(33);
        property.floorplans().add(floorplan);

        view.populate(property);
    }

    @Override
    public void goToUnitDetails(AptUnitDTO unit) {
        AppSite.getPlaceController().goTo(new PortalSiteMap.FindApartment.UnitDetails());
    }

    @Override
    public void navigTo(Place place) {
        AppSite.getPlaceController().goTo(place);
    }

}
