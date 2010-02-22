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

import java.util.List;

import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.examples.domain.crm.Customer;
import com.pyx4j.examples.site.client.GoogleAPI;

public class CustomerListMapPanel extends SimplePanel {

    private MapWidget map;

    private boolean mapLoadComplete = false;

    private List<Customer> entities;

    public CustomerListMapPanel() {
        final VerticalPanel contentPanel = new VerticalPanel();
        setWidget(contentPanel);

        GoogleAPI.ensureInitialized();
        AjaxLoader.loadApi("maps", "2", new Runnable() {
            public void run() {
                mapsLoaded(contentPanel);
            }
        }, null);
    }

    private void mapsLoaded(VerticalPanel contentPanel) {

        LatLng pos = LatLng.newInstance(43, -79);

        map = new MapWidget(pos, 2);
        map.setSize("400px", "400px");
        map.setCenter(pos, 10);

        map.addControl(new LargeMapControl());

        contentPanel.add(map);
        contentPanel.setCellHorizontalAlignment(map, DockPanel.ALIGN_CENTER);

        mapLoadComplete = true;

        if (entities != null) {
            populateData(entities);
        }

    }

    public void populateData(List<Customer> entities) {
        this.entities = entities;
        if (mapLoadComplete) {
            for (Customer entity : entities) {
                Marker marker = createMarker(entity);
                if (marker != null) {
                    map.addOverlay(marker);
                }
            }
        }
    }

    private Marker createMarker(final Customer customer) {
        MarkerOptions markerOptions = MarkerOptions.newInstance();
        markerOptions.setTitle(customer.name().getValue());

        if (customer.latitude().getValue() != null && customer.longitude().getValue() != null) {

            final Marker marker = new Marker(LatLng.newInstance(customer.latitude().getValue(), customer.longitude().getValue()), markerOptions);

            marker.addMarkerClickHandler(new MarkerClickHandler() {

                public void onClick(MarkerClickEvent event) {
                    map.getInfoWindow().open(
                            marker,
                            new InfoWindowContent("<div style='font-size:12pt;background-color:white; padding:2px; height:160px;'><a href=''>"
                                    + customer.name().getValue() + "</a><br>"

                                    + "<b>" + customer.street().getValue()

                                    + "</b><br>" + "</div>"));
                }
            });
            return marker;
        } else {
            return null;
        }
    }
}
