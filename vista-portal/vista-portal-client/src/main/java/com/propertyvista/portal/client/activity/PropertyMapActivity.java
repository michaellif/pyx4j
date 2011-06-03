/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.client.ui.PropertyMapView;
import com.propertyvista.portal.domain.dto.PropertyDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;

public class PropertyMapActivity extends AbstractActivity implements PropertyMapView.Presenter {
    PropertyMapView view;

    @Inject
    public PropertyMapActivity(PropertyMapView view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    public PropertyMapActivity withPlace(Place place) {
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);
        List<PropertyDTO> properties = new ArrayList<PropertyDTO>();
        {
            PropertyDTO property = EntityFactory.create(PropertyDTO.class);
            property.address().street1().setValue("320 Avenue Road");
            property.location().setValue(new GeoPoint(43.697665, -79.402313));
            property.id().setValue(new Key(11));
            properties.add(property);

            property = EntityFactory.create(PropertyDTO.class);
            property.address().street1().setValue("1000 Yonge Street");
            property.location().setValue(new GeoPoint(43.675599, -79.389042));
            property.id().setValue(new Key(12));
            properties.add(property);

        }
        view.populate(properties);
    }

    @Override
    public void showPropertyDetails(PropertyDTO property) {
        AppPlace place = new PortalSiteMap.FindApartment.ApartmentDetails();
        HashMap<String, String> args = new HashMap<String, String>();
        args.put(PortalSiteMap.ARG_PROPERTY_ID, property.id().getValue().toString());
        place.setArgs(args);
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public void refineSearch(PropertySearchCriteria searchCriteria) {
        AppSite.getPlaceController().goTo(new PortalSiteMap.FindApartment.PropertyMap());
    }

}
