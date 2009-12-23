/*
 * Copyright 2007 Google Inc.
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
 */
package com.pyx4j.examples.site.client.pages;


import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.SmallMapControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.examples.site.client.Page;

/**
 * Introduction page.
 */
public class ContactUs extends Page {

    public static PageInfo init() {
        return new PageInfo("Contact Us") {

            @Override
            public Page createInstance() {
                return new ContactUs();
            }
        };
    }

    public ContactUs() {
    }

    @Override
    public Widget getContent() {

        DockPanel content = new DockPanel();
        content.setWidth("100%");

        MapWidget map = createMap();

        content.add(map, DockPanel.CENTER);
        content.setCellHorizontalAlignment(map, DockPanel.ALIGN_CENTER);

        HTML contactInfo = new HTML(StringUtils.unescapeHTML(DOM.getInnerHTML(RootPanel.get("ContactUs").getElement()), 0));

        DOM.setStyleAttribute(contactInfo.getElement(), "fontSize", "12pt");
        content.add(contactInfo, DockPanel.NORTH);
        content.setCellHorizontalAlignment(contactInfo, DockPanel.ALIGN_CENTER);
        DOM.setStyleAttribute(contactInfo.getElement(), "margin", "10px");
        DOM.setStyleAttribute(contactInfo.getElement(), "marginBottom", "20px");
        DOM.setStyleAttribute(contactInfo.getElement(), "whiteSpace", "nowrap");

        return content;
    }

    private MapWidget createMap() {

        LatLng pos = LatLng.newInstance(43.879428, -79.438062);

        final MapWidget map = new MapWidget(pos, 2);
        map.setSize("500px", "300px");
        map.setCenter(pos, 14);

        map.addControl(new SmallMapControl());

        Icon icon = Icon.newInstance("images/house.png");
        icon.setShadowURL("images/house_shadow.png");
        icon.setIconSize(Size.newInstance(30, 30));
        icon.setShadowSize(Size.newInstance(44, 35));
        icon.setIconAnchor(Point.newInstance(6, 20));
        icon.setInfoWindowAnchor(Point.newInstance(15, 5));

        MarkerOptions markerOptions = MarkerOptions.newInstance();
        markerOptions.setTitle("21 Bedford Park Ave, Richmond Hill, ON, Canada");
        markerOptions.setIcon(icon);

        final Marker marker = new Marker(pos, markerOptions);

        marker.addMarkerClickHandler(new MarkerClickHandler() {

            public void onClick(MarkerClickEvent event) {
                map
                        .getInfoWindow()
                        .open(
                                marker,
                                new InfoWindowContent(
                                        "<div style=\"fontSize:16pt;background-color:white;width=220px;height=60px\"><b>\"Curious Kids\"</b><p>21 Bedford Park Ave,<br>Richmond Hill, ON, Canada<br></div>"));
            }
        });

        map.addOverlay(marker);

        return map;
    }

    @Override
    public Widget getContentAdditions() {
        return new HTML("");
    }

    @Override
    public Widget getNavigPanel() {
        return new HTML("");
    }

}
