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
package com.propertyvista.portal.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.gwt.geo.CircleOverlay;
import com.pyx4j.gwt.geo.GoogleAPI;
import com.pyx4j.gwt.geo.MapUtils;

import com.propertyvista.portal.domain.site.Property;

public class PropertyMapWidget extends SimplePanel {

    private MapWidget map;

    private boolean mapLoadComplete = false;

    private final List<Marker> markers = new ArrayList<Marker>();

    private List<Property> properties;

    private LatLng latLng;

    private double distance;

    private CircleOverlay distanceOverlay;

    public PropertyMapWidget() {
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
        map.setSize("300px", "300px");
        //TODO
        //map.setStyleName();

        map.addControl(new LargeMapControl());

        setWidget(map);

        mapLoadComplete = true;

        if (properties != null) {
            populate(properties);
        }

        if (latLng != null) {
            setDistanceOverlay(latLng, distance);
        }

    }

    public void populate(List<Property> properties) {
        this.properties = properties;
        if (mapLoadComplete) {
            for (Marker marker : markers) {
                map.removeOverlay(marker);
            }
            markers.clear();
            for (Property property : properties) {
                Marker marker = createMarker(property);
                if (marker != null) {
                    map.addOverlay(marker);
                    markers.add(marker);
                }
            }
        }
    }

    public void setDistanceOverlay(LatLng latLng, final double distance) {
        this.latLng = latLng;
        this.distance = distance;
        if (mapLoadComplete) {
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

    private Marker createMarker(final Property property) {
        MarkerOptions markerOptions = MarkerOptions.newInstance();
        markerOptions.setTitle(property.address().getValue());

//        Icon icon = Icon.newInstance(FmRiaResources.INSTANCE.mapMarkerHouse().getURL());
//        icon.setShadowURL(FmRiaResources.INSTANCE.mapMarkerHouseShadow().getURL());
//        icon.setIconSize(Size.newInstance(30, 30));
//        icon.setShadowSize(Size.newInstance(44, 35));
//        icon.setIconAnchor(Point.newInstance(15, 20));
//        icon.setInfoWindowAnchor(Point.newInstance(15, 5));
//        markerOptions.setIcon(icon);

        if (!property.location().isNull()) {
            final Marker marker = new Marker(MapUtils.newLatLngInstance(property.location().getValue()), markerOptions);

            marker.addMarkerClickHandler(new MarkerClickHandler() {

                @Override
                public void onClick(MarkerClickEvent event) {
//                    map.getInfoWindow().open(
//                            marker,
//                            new InfoWindowContent("<div style='text-align:center; font-size:14px;background-color:white; padding:2px;'><a href='#"
//                                    + NavigUtils.getPageUri(FmRiaSiteMap.Crm.Customers.Edit.class) + "?" + NavigUtils.ENTITY_ID + "="
//                                    + customer.getPrimaryKey() + "'><b>" + customer.name().getValue() + "</b></a><br>"
//
//                                    + customer.address().street().getValue() + ", " + customer.address().city().getValue() + "<br/>"
//                                    + customer.address().province().getValue() + ", " + customer.address().zip().getValue()
//
//                                    + "</div>"));

                }
            });
            return marker;
        } else {
            return null;
        }
    }
}
