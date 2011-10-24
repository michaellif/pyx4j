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
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.maps;

import java.util.HashMap;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.overlay.Marker;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.gwt.geo.CircleOverlay;
import com.pyx4j.gwt.geo.MapUtils;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

import com.propertyvista.portal.domain.dto.PropertyDTO;
import com.propertyvista.portal.domain.dto.PropertyListDTO;

public class PropertiesMapWidget extends AbstractMapWidget {

    public static String PROPERTY_CARD_STYLE_PREFIX = "PropertyCard";

    private final HashMap<PropertyDTO, PropertyMarker> markers = new HashMap<PropertyDTO, PropertyMarker>();

    private PropertyListDTO propertyList;

    private GeoPoint geoPoint;

    private double distance;

    private CircleOverlay distanceOverlay;

    private DefaultAsyncCallback<LatLngBounds> callback;

    public static enum StyleSuffix implements IStyleName {
        CardLeft, CardLeftItem, CardLeftLabel, CardContent, CardContentItem, CardImage, CardMenuItem
    }

    public static enum MarkerType {
        inbound, outbound
    }

    public PropertiesMapWidget() {
        super("100%", "100%");
    }

    @Override
    protected void mapsLoaded() {
        markers.clear();

        super.mapsLoaded();

        if (propertyList != null && !propertyList.properties().isNull()) {
            setBounds(propertyList, callback);
        } else if (geoPoint != null) {
            setDistanceOverlay(geoPoint, distance, callback);
        }

    }

    public void populateMarkers(PropertyListDTO inboundPropertyList, PropertyListDTO outboundPropertyList) {

        if (isMapLoadComplete()) {

            HashMap<PropertyDTO, PropertyMarker> origMarkers = new HashMap<PropertyDTO, PropertyMarker>(markers);
            markers.clear();

            PropertyMarker marker = null;
            for (PropertyDTO property : inboundPropertyList.properties()) {
                if (origMarkers.containsKey(property)) {
                    marker = origMarkers.remove(property);
                } else {
                    marker = createMarker(property);
                    getMap().addOverlay(marker);
                }
                marker.setMarkerType(MarkerType.inbound);
                markers.put(property, marker);
            }
            for (PropertyDTO property : outboundPropertyList.properties()) {
                if (origMarkers.containsKey(property)) {
                    marker = origMarkers.remove(property);
                } else {
                    marker = createMarker(property);
                    getMap().addOverlay(marker);
                }
                marker.setMarkerType(MarkerType.outbound);
                markers.put(property, marker);
            }

            for (Marker oldMarker : origMarkers.values()) {
                getMap().removeOverlay(oldMarker);
            }
        }
    }

    public void setBounds(PropertyListDTO propertyList, DefaultAsyncCallback<LatLngBounds> callback) {
        this.propertyList = propertyList;
        this.callback = callback;
        this.geoPoint = null;
        this.distance = 0;
        if (isMapLoadComplete()) {
            LatLngBounds bounds = LatLngBounds.newInstance();
            for (PropertyDTO property : propertyList.properties()) {
                bounds.extend(MapUtils.newLatLngInstance(property.location().getValue()));
            }
            getMap().setCenter(bounds.getCenter());
            int zoomLevel = getMap().getBoundsZoomLevel(bounds) - 1;
            if (zoomLevel > 12) {
                zoomLevel = 12;
            }
            getMap().setZoomLevel(zoomLevel);
            callback.onSuccess(getMap().getBounds());
        }
    }

    public void setDistanceOverlay(GeoPoint geoPoint, final double distance, DefaultAsyncCallback<LatLngBounds> callback) {
        this.geoPoint = geoPoint;
        this.distance = distance;
        this.callback = callback;
        this.propertyList = null;
        if (isMapLoadComplete()) {
            LatLng latLng = MapUtils.newLatLngInstance(geoPoint);
            if (distanceOverlay != null) {
                getMap().removeOverlay(distanceOverlay);
                distanceOverlay = null;
            }
            if (latLng != null && distance != 0) {
                distanceOverlay = new CircleOverlay(latLng, distance, "green", 2, 0.4, "green", 0.1);
                getMap().addOverlay(distanceOverlay);
            }
            if (latLng != null) {
                getMap().setCenter(latLng, 14 - (int) Math.ceil(Math.log(distance) / Math.log(2)));
            } else {
                LatLng pos = LatLng.newInstance(43.7571145, -79.5082499);
                getMap().setCenter(pos, 10);
            }
            callback.onSuccess(getMap().getBounds());
        }
    }

    public void removeDistanceOverlay() {
        if (distanceOverlay != null) {
            getMap().removeOverlay(distanceOverlay);
            distanceOverlay = null;
        }
    }

    private PropertyMarker createMarker(final PropertyDTO property) {
        if (!property.location().isNull()) {
            final PropertyMarker marker = new PropertyMarker(property, getMap());
            return marker;
        } else {
            return null;
        }
    }

}
