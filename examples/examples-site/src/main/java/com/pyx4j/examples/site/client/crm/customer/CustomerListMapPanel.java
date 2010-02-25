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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geocode.GeocodeCache;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.examples.domain.crm.Customer;
import com.pyx4j.examples.rpc.PageType;
import com.pyx4j.examples.site.client.GoogleAPI;

public class CustomerListMapPanel extends SimplePanel {

    private static Logger log = LoggerFactory.getLogger(CustomerListMapPanel.class);

    private MapWidget map;

    private boolean mapLoadComplete = false;

    private List<Customer> entities;

    private String zip;

    private double distance;

    private CircleOverlay distanceOverlay;

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

        map = new MapWidget(pos, 2);
        map.setSize("350px", "400px");
        map.setCenter(pos, 10);

        map.addControl(new LargeMapControl());

        setWidget(map);

        mapLoadComplete = true;

        if (entities != null) {
            populateData(entities);
        }

        if (zip != null) {
            setDistanceOverlay(zip, distance);
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

    public void setDistanceOverlay(String zip, final double distance) {
        this.zip = zip;
        this.distance = distance;
        if (mapLoadComplete) {
            if (distanceOverlay != null) {
                map.removeOverlay(distanceOverlay);
                distanceOverlay = null;
            }
            if (zip != null && distance != 0) {
                new Geocoder().getLatLng(zip, new LatLngCallback() {

                    @Override
                    public void onSuccess(LatLng point) {
                        distanceOverlay = new CircleOverlay(point, distance, "green", 2, 0.4, "green", 0.1);
                        map.addOverlay(distanceOverlay);
                    }

                    @Override
                    public void onFailure() {
                        log.warn("Can't find LatLng for distanceOverlay");
                    }
                });

            }
        }
    }

    private Marker createMarker(final Customer customer) {
        MarkerOptions markerOptions = MarkerOptions.newInstance();
        markerOptions.setTitle(customer.name().getValue());

        Icon icon = Icon.newInstance("images/house.png");
        icon.setShadowURL("images/house_shadow.png");
        icon.setIconSize(Size.newInstance(30, 30));
        icon.setShadowSize(Size.newInstance(44, 35));
        icon.setIconAnchor(Point.newInstance(6, 20));
        icon.setInfoWindowAnchor(Point.newInstance(15, 5));
        markerOptions.setIcon(icon);

        if (customer.latitude().getValue() != null && customer.longitude().getValue() != null) {

            final Marker marker = new Marker(LatLng.newInstance(customer.latitude().getValue(), customer.longitude().getValue()), markerOptions);

            marker.addMarkerClickHandler(new MarkerClickHandler() {

                public void onClick(MarkerClickEvent event) {
                    map.getInfoWindow().open(
                            marker,
                            new InfoWindowContent("<div style='text-align:center; font-size:14px;background-color:white; padding:2px;'><a href='#"
                                    + PageType.crm$customers$editor.getUri().uri().getValue() + "?entity_id=" + customer.getPrimaryKey() + "'><b>"
                                    + customer.name().getValue() + "</b></a><br>"

                                    + customer.street().getValue()

                                    + "</div>"));
                }
            });
            return marker;
        } else {
            return null;
        }
    }
}
