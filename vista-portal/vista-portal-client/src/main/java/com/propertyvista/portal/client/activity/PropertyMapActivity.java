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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.utils.EntityArgsConverter;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.gwt.geo.MapUtils;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.portal.client.PortalSite;
import com.propertyvista.portal.client.ui.searchapt.PropertyMapView;
import com.propertyvista.portal.client.ui.viewfactories.PropertySearchViewFactory;
import com.propertyvista.portal.domain.dto.PropertyDTO;
import com.propertyvista.portal.domain.dto.PropertyListDTO;
import com.propertyvista.portal.rpc.portal.PortalSiteMap;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;

public class PropertyMapActivity extends AbstractActivity implements PropertyMapView.Presenter {

    private static Logger log = LoggerFactory.getLogger(PropertyMapActivity.class);

    private static PropertyListDTO allProperties;

    private PropertyListDTO inboundProperties;

    private PropertyListDTO shownProperties;

    private final PropertyMapView view;

    private PropertySearchCriteria criteria;

    private GeoPoint geoPoint;

    public PropertyMapActivity(Place place) {
        this.view = (PropertyMapView) PropertySearchViewFactory.instance(PropertyMapView.class);
        this.view.setPresenter(this);
        withPlace(place);
    }

    public PropertyMapActivity withPlace(Place place) {
        Map<String, String> args = ((AppPlace) place).getArgs();
        criteria = EntityArgsConverter.createFromArgs(PropertySearchCriteria.class, args);
        return this;
    }

    @Override
    public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
        containerWidget.setWidget(view);

        if (PropertyMapActivity.allProperties == null) {
            PortalSite.getPortalSiteServices().retrievePropertyList(new DefaultAsyncCallback<PropertyListDTO>() {
                @Override
                public void onSuccess(PropertyListDTO properties) {
                    PropertyMapActivity.allProperties = properties;
                    obtainGeopoint();
                }
            });
        } else {
            obtainGeopoint();
        }

    }

    private void obtainGeopoint() {

        if (PropertySearchCriteria.SearchType.proximity.equals(criteria.searchType().getValue())) {
            MapUtils.obtainLatLang(criteria.location().getValue(), new LatLngCallback() {

                @Override
                public void onSuccess(LatLng fromCoordinates) {

                    geoPoint = MapUtils.newGeoPointInstance(fromCoordinates);
                    populateView();

                }

                @Override
                public void onFailure() {
                    log.warn("Can't find LatLng for distanceOverlay");
                }
            });
        } else {
            populateView();
        }

    }

    private void populateView() {
        inboundProperties = filterForList();
        view.populate(criteria, geoPoint, inboundProperties);
    }

    @Override
    public void updateMap(LatLngBounds latLngBounds) {
        shownProperties = filterByBounds(latLngBounds);
        PropertyListDTO outboundProperties = EntityFactory.create(PropertyListDTO.class);
        for (PropertyDTO property : shownProperties.properties()) {
            if (!inboundProperties.properties().contains(property)) {
                outboundProperties.properties().add(property);

            }
        }
        view.updateMarkers(inboundProperties, outboundProperties);
    }

    private PropertyListDTO filterForList() {
        PropertyListDTO filteredProperties = EntityFactory.create(PropertyListDTO.class);
        for (PropertyDTO property : allProperties.properties()) {
            if (criteria.city().isNull() || !criteria.city().name().equals(property.address().city())
                    || !criteria.city().province().name().equals(property.address().province().name())) {
                continue;
            }
            filteredProperties.properties().add(property);
        }
        return filteredProperties;
    }

    private PropertyListDTO filterByBounds(LatLngBounds latLngBounds) {
        return allProperties;
    }

    @Override
    public void showPropertyDetails(PropertyDTO property) {
        AppPlace place = new PortalSiteMap.FindApartment.ApartmentDetails();
        place.putArg(PortalSiteMap.ARG_PROPERTY_ID, property.id().getValue().toString());
        AppSite.getPlaceController().goTo(place);
    }

    @Override
    public void refineSearch() {
        criteria = view.getValue();
        obtainGeopoint();
    }

}
