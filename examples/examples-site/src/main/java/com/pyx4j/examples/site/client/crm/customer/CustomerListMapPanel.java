/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Dec 29, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.examples.site.client.crm.customer;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.ajaxloader.client.AjaxLoader;
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
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.examples.domain.crm.Customer;
import com.pyx4j.examples.site.client.ExamplesSiteMap;
import com.pyx4j.examples.site.client.crm.CrmSiteResources;
import com.pyx4j.geo.GeoCell;
import com.pyx4j.geo.GeoCircle;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.gwt.geo.CircleOverlay;
import com.pyx4j.gwt.geo.GeoBoxOverlay;
import com.pyx4j.gwt.geo.GoogleAPI;
import com.pyx4j.gwt.geo.MapUtils;
import com.pyx4j.site.client.themes.SiteCSSClass;
import com.pyx4j.site.shared.meta.NavigUtils;

public class CustomerListMapPanel extends SimplePanel {

    private MapWidget map;

    private boolean mapLoadComplete = false;

    private List<Customer> entities;

    private LatLng latLng;

    private double distance;

    private CircleOverlay distanceOverlay;

    private final List<GeoBoxOverlay> geoBoxOverlayList = new ArrayList<GeoBoxOverlay>();

    private final List<Marker> markers = new ArrayList<Marker>();

    public CustomerListMapPanel() {

        GoogleAPI.ensureInitialized();
        AjaxLoader.loadApi("maps", "2", new Runnable() {
            public void run() {
                mapsLoaded();
            }
        }, null);
    }

    private void mapsLoaded() {

        LatLng pos = LatLng.newInstance(43.7571145, -79.5082499);

        map = new MapWidget(pos, 10);
        map.setSize("280px", "350px");
        map.setStyleName(SiteCSSClass.pyx4j_Site_Map.name());

        map.addControl(new LargeMapControl());

        setWidget(map);

        mapLoadComplete = true;

        if (entities != null) {
            populateData(entities);
        }

        if (latLng != null) {
            setDistanceOverlay(latLng, distance);
        }

    }

    public void populateData(List<Customer> entities) {
        this.entities = entities;
        if (mapLoadComplete) {
            for (Marker marker : markers) {
                map.removeOverlay(marker);
            }
            markers.clear();
            for (Customer entity : entities) {
                Marker marker = createMarker(entity);
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
            for (GeoBoxOverlay geoBoxOverlay : geoBoxOverlayList) {
                map.removeOverlay(geoBoxOverlay);
            }
            geoBoxOverlayList.clear();
            if (latLng != null && distance != 0) {
                distanceOverlay = new CircleOverlay(latLng, distance, "green", 2, 0.4, "green", 0.1);
                map.addOverlay(distanceOverlay);
                List<String> keys = GeoCell.getBestCoveringSet(new GeoCircle(new GeoPoint(latLng.getLatitude(), latLng.getLongitude()), distance));
                for (String geoBox : keys) {
                    if (false) {
                        GeoBoxOverlay geoBoxOverlay = new GeoBoxOverlay(geoBox);
                        map.addOverlay(geoBoxOverlay);
                        geoBoxOverlayList.add(geoBoxOverlay);
                    }
                }
            }
            if (latLng != null) {
                map.setCenter(latLng, 14 - (int) Math.ceil(Math.log(distance) / Math.log(2)));
            } else {
                LatLng pos = LatLng.newInstance(43.7571145, -79.5082499);
                map.setCenter(pos, 10);
            }
        }
    }

    private Marker createMarker(final Customer customer) {
        MarkerOptions markerOptions = MarkerOptions.newInstance();
        markerOptions.setTitle(customer.name().getValue());

        Icon icon = Icon.newInstance(CrmSiteResources.INSTANCE.mapMarkerHouse().getURL());
        icon.setShadowURL(CrmSiteResources.INSTANCE.mapMarkerHouseShadow().getURL());
        icon.setIconSize(Size.newInstance(30, 30));
        icon.setShadowSize(Size.newInstance(44, 35));
        icon.setIconAnchor(Point.newInstance(15, 20));
        icon.setInfoWindowAnchor(Point.newInstance(15, 5));
        markerOptions.setIcon(icon);

        if (!customer.location().isNull()) {
            final Marker marker = new Marker(MapUtils.newLatLngInstance(customer.location().getValue()), markerOptions);

            marker.addMarkerClickHandler(new MarkerClickHandler() {

                public void onClick(MarkerClickEvent event) {
                    map.getInfoWindow().open(
                            marker,
                            new InfoWindowContent("<div style='text-align:center; font-size:14px;background-color:white; padding:2px;'><a href='#"
                                    + NavigUtils.getPageUri(ExamplesSiteMap.Crm.Customers.Edit.class) + "?entity_id=" + customer.getPrimaryKey() + "'><b>"
                                    + customer.name().getValue() + "</b></a><br>"

                                    + customer.address().street().getValue() + ", " + customer.address().city().getValue() + "<br/>"
                                    + customer.address().province().getValue() + ", " + customer.address().zip().getValue()

                                    + "</div>"));

                }
            });
            return marker;
        } else {
            return null;
        }
    }

    public void clearData() {
        if (mapLoadComplete) {
            for (Marker marker : markers) {
                map.removeOverlay(marker);
            }
            markers.clear();
            if (distanceOverlay != null) {
                map.removeOverlay(distanceOverlay);
                distanceOverlay = null;
            }
            for (GeoBoxOverlay geoBoxOverlay : geoBoxOverlayList) {
                map.removeOverlay(geoBoxOverlay);
            }
            geoBoxOverlayList.clear();

        }
    }
}
