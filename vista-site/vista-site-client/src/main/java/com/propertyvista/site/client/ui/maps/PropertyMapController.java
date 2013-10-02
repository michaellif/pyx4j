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
package com.propertyvista.site.client.ui.maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.user.client.Window;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.utils.EntityArgsConverter;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.gwt.geo.MapUtils;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.site.rpc.dto.PropertyDTO;
import com.propertyvista.site.rpc.dto.PropertyListDTO;
import com.propertyvista.site.rpc.dto.PropertySearchCriteria;
import com.propertyvista.site.rpc.dto.PropertySearchCriteria.SearchType;
import com.propertyvista.site.rpc.services.PortalSiteServices;

//http://localhost:8888/vista/portal/aptmap.html?gwt.codesvr=127.0.0.1:9997&city.name=Halifax&city.province.name=Nova+Scotia&searchType=city
//
public enum PropertyMapController {

    instance;

    private static Logger log = LoggerFactory.getLogger(PropertyMapController.class);

    private final PropertiesMapWidget map;

    private PropertyListDTO allProperties;

    private final PropertySearchCriteria criteria;

    private PropertyListDTO inboundProperties;

    private PropertyListDTO shownProperties;

    private GeoPoint proximityCenter;

    private PropertyMapController() {

        criteria = EntityArgsConverter.createFromArgs(PropertySearchCriteria.class, Window.Location.getParameterMap());

        map = new PropertiesMapWidget();

        publishJs();
    }

    public static PropertiesMapWidget getMapWidget() {
        return instance.map;
    }

    public static void loadMap() {
        instance.map.loadMap();
        instance.loadProperties();
    }

    private native void publishJs() /*-{
		$wnd.loadMap = @com.propertyvista.site.client.ui.maps.PropertyMapController::loadMap();
    }-*/;

    private void loadProperties() {
        if (allProperties == null) {
            GWT.<PortalSiteServices> create(PortalSiteServices.class).retrievePropertyList(new DefaultAsyncCallback<PropertyListDTO>() {
                @Override
                public void onSuccess(PropertyListDTO properties) {
                    allProperties = properties;
                    obtainGeopoint();
                }
            }, criteria);
        } else {
            obtainGeopoint();
        }
    }

    private void obtainGeopoint() {
        if (PropertySearchCriteria.SearchType.proximity.equals(criteria.searchType().getValue())) {
            MapUtils.obtainLatLang(criteria.location().getValue(), new LatLngCallback() {

                @Override
                public void onSuccess(LatLng fromCoordinates) {

                    proximityCenter = MapUtils.newGeoPointInstance(fromCoordinates);
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

    private PropertyListDTO filterInboundProperties() {
        PropertyListDTO filteredProperties = EntityFactory.create(PropertyListDTO.class);
        for (PropertyDTO property : allProperties.properties()) {
            if (allProperties.filterIds().contains(property.getPrimaryKey().asLong())) {
                filteredProperties.properties().add(property);
            }
        }
        return filteredProperties;
    }

    private void populateView() {
        inboundProperties = filterInboundProperties();

        DefaultAsyncCallback<LatLngBounds> callback = new DefaultAsyncCallback<LatLngBounds>() {
            @Override
            public void onSuccess(LatLngBounds result) {
                updateMap(result);
            }
        };
        if (SearchType.proximity.equals(criteria.searchType().getValue()) && proximityCenter != null && !criteria.distance().isNull()
                && criteria.distance().getValue() > 0) {
            map.setDistanceOverlay(proximityCenter, criteria.distance().getValue(), callback);
        } else {
            map.removeDistanceOverlay();
            if (!inboundProperties.isEmpty()) {
                map.setBounds(inboundProperties, callback);
            } else if (!criteria.city().isNull()) {
                // TODO set bounds using city location
            } else {
                // TODO set bounds using a list of province cities
            }
        }

    }

    public void updateMap(LatLngBounds latLngBounds) {
        if (inboundProperties == null) {
            return;
        }
        shownProperties = filterByBounds(latLngBounds);
        PropertyListDTO outboundProperties = EntityFactory.create(PropertyListDTO.class);
        for (PropertyDTO property : shownProperties.properties()) {
            if (!inboundProperties.properties().contains(property)) {
                outboundProperties.properties().add(property);
            }
        }
        map.populateMarkers(inboundProperties, outboundProperties);
    }

    private PropertyListDTO filterByBounds(LatLngBounds latLngBounds) {
        //TODO load only the properties that fit into map (optimisation may not be needed at all)
        return allProperties;
    }
}
