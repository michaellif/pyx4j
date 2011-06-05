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

import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.geo.GeoPoint;
import com.pyx4j.gwt.geo.CircleOverlay;
import com.pyx4j.gwt.geo.MapUtils;

import com.propertyvista.portal.client.resources.PortalImages;
import com.propertyvista.portal.domain.dto.PropertyDTO;
import com.propertyvista.portal.domain.dto.PropertyListDTO;

public class PropertiesMapWidget extends AbstractMapWidget {

    private final HashMap<PropertyDTO, Marker> markers = new HashMap<PropertyDTO, Marker>();

    private PropertyListDTO propertyList;

    private GeoPoint geoPoint;

    private double distance;

    private CircleOverlay distanceOverlay;

    public PropertiesMapWidget() {
        super("100%", "500px");
    }

    @Override
    protected void mapsLoaded() {

        super.mapsLoaded();

        if (propertyList != null && !propertyList.properties().isNull()) {
            populate(propertyList);
        }

        if (geoPoint != null) {
            setDistanceOverlay(geoPoint, distance);
        }

    }

    public void populate(PropertyListDTO propertyList) {
        this.propertyList = propertyList;

        if (isMapLoadComplete()) {
            for (Marker marker : markers.values()) {
                getMap().removeOverlay(marker);
            }
            markers.clear();
            for (PropertyDTO property : propertyList.properties()) {
                Marker marker = createMarker(property);
                if (marker != null) {
                    getMap().addOverlay(marker);
                    markers.put(property, marker);
                }
            }
            //TODO calc base on  markers
            getMap().setCenter(LatLng.newInstance(43.7571145, -79.5082499));
            getMap().setZoomLevel(10);
        }
    }

    public void setDistanceOverlay(GeoPoint geoPoint, final double distance) {
        this.geoPoint = geoPoint;
        this.distance = distance;
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
        }
    }

    private Marker createMarker(final PropertyDTO property) {
        MarkerOptions markerOptions = MarkerOptions.newInstance();
        markerOptions.setTitle(property.address().getStringView());

        Icon icon = Icon.newInstance(PortalImages.INSTANCE.mapMarker().getURL());
        //TODO get shadow URL
        //icon.setShadowURL(FmRiaResources.INSTANCE.mapMarkerHouseShadow().getURL());
        icon.setIconSize(Size.newInstance(38, 41));
        icon.setShadowSize(Size.newInstance(44, 35));
        icon.setIconAnchor(Point.newInstance(15, 20));
        icon.setInfoWindowAnchor(Point.newInstance(15, 5));
        markerOptions.setIcon(icon);

        if (!property.location().isNull()) {
            final Marker marker = new Marker(MapUtils.newLatLngInstance(property.location().getValue()), markerOptions);

            marker.addMarkerClickHandler(new MarkerClickHandler() {

                @Override
                public void onClick(MarkerClickEvent event) {
                    showMarker(property);

                }
            });
            return marker;
        } else {
            return null;
        }
    }

    public void showMarker(PropertyDTO property) {
        getMap().getInfoWindow().open(markers.get(property), new InfoWindowContent(new PropertyInfo(property)));
    }

    class PropertyInfo extends DockPanel {

        PropertyInfo(PropertyDTO property) {
            super();
            add(new Label("[Image]"), DockPanel.WEST);

            add(new Button("Details"), DockPanel.SOUTH);

            add(new HTML("[Property Descr]"), DockPanel.CENTER);
        }

    }
}
