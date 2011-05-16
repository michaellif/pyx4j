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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.LargeMapControl;
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
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.geo.GeoPoint;
import com.pyx4j.gwt.geo.CircleOverlay;
import com.pyx4j.gwt.geo.GoogleAPI;
import com.pyx4j.gwt.geo.MapUtils;

import com.propertyvista.portal.client.resources.PortalImages;
import com.propertyvista.portal.rpc.dto.PropertyDTO;

public class PropertiesMapWidget extends SimplePanel {

    private MapWidget map;

    private boolean mapLoadComplete = false;

    private final List<Marker> markers = new ArrayList<Marker>();

    private List<PropertyDTO> properties;

    private GeoPoint geoPoint;

    private double distance;

    private CircleOverlay distanceOverlay;

    public PropertiesMapWidget() {
        setWidth("100%");
        getElement().getStyle().setMarginTop(10, Unit.PX);
        getElement().getStyle().setMarginBottom(10, Unit.PX);
        GoogleAPI.ensureInitialized();
        AjaxLoader.loadApi("maps", "2", new Runnable() {
            @Override
            public void run() {
                mapsLoaded();
            }
        }, null);
    }

    private void mapsLoaded() {

        LatLng pos = LatLng.newInstance(43.7571145, -79.5082499);

        map = new MapWidget(pos, 10);
        map.setSize("100%", "500px");
        //TODO
        //map.setStyleName();

        map.addControl(new LargeMapControl());

        setWidget(map);

        mapLoadComplete = true;

        if (properties != null) {
            populate(properties);
        }

        if (geoPoint != null) {
            setDistanceOverlay(geoPoint, distance);
        }

    }

    public void populate(List<PropertyDTO> properties) {
        this.properties = properties;
        if (mapLoadComplete) {
            for (Marker marker : markers) {
                map.removeOverlay(marker);
            }
            markers.clear();
            for (PropertyDTO property : properties) {
                Marker marker = createMarker(property);
                if (marker != null) {
                    map.addOverlay(marker);
                    markers.add(marker);
                }
            }
        }
    }

    public void setDistanceOverlay(GeoPoint geoPoint, final double distance) {
        this.geoPoint = geoPoint;
        this.distance = distance;
        if (mapLoadComplete) {
            LatLng latLng = MapUtils.newLatLngInstance(geoPoint);
            if (distanceOverlay != null) {
                map.removeOverlay(distanceOverlay);
                distanceOverlay = null;
            }
            if (latLng != null && distance != 0) {
                distanceOverlay = new CircleOverlay(latLng, distance, "green", 2, 0.4, "green", 0.1);
                map.addOverlay(distanceOverlay);
            }
            if (latLng != null) {
                map.setCenter(latLng, 14 - (int) Math.ceil(Math.log(distance) / Math.log(2)));
            } else {
                LatLng pos = LatLng.newInstance(43.7571145, -79.5082499);
                map.setCenter(pos, 10);
            }
        }
    }

    private Marker createMarker(final PropertyDTO property) {
        MarkerOptions markerOptions = MarkerOptions.newInstance();
        markerOptions.setTitle(property.address().getValue());

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
                    map.getInfoWindow().open(marker, new InfoWindowContent(new PropertyInfo(property)));

                }
            });
            return marker;
        } else {
            return null;
        }
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
