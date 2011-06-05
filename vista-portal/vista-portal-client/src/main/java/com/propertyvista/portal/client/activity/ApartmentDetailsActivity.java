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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.propertyvista.portal.client.PortalSite;
import com.propertyvista.portal.client.ui.searchapt.ApartmentDetailsView;
import com.propertyvista.portal.domain.dto.AmenityDTO;
import com.propertyvista.portal.domain.dto.AptUnitDTO;
import com.propertyvista.portal.domain.dto.FloorplanDTO;
import com.propertyvista.portal.domain.dto.PropertyDetailsDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

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
                AmenityDTO amenity = EntityFactory.create(AmenityDTO.class);
                amenity.name().setValue("Pool");
                property.amenities().add(amenity);
                amenity = EntityFactory.create(AmenityDTO.class);
                amenity.name().setValue("Tennis court");
                property.amenities().add(amenity);
                amenity = EntityFactory.create(AmenityDTO.class);
                amenity.name().setValue("Game room");
                property.amenities().add(amenity);

                FloorplanDTO fp = EntityFactory.create(FloorplanDTO.class);
                fp.area().setValue(700);
                fp.description().setValue("Nice, clean, south side. Freshly painted");
                fp.name().setValue("one bedroom");
                property.floorplans().add(fp);

                view.populate(property);
            }

        }, new Key(propertyId));

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
