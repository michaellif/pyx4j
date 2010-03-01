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

import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.pyx4j.examples.domain.crm.Customer;
import com.pyx4j.gwt.geo.GoogleAPI;
import com.pyx4j.gwt.geo.MapUtils;
import com.pyx4j.site.client.themes.SiteCSSClass;

public class CustomerEditorMapPanel extends HorizontalPanel {

    private MapWidget map;

    private Marker marker;

    private MarkerOptions markerOptions;

    private SimplePanel streetViewHolder;

    private Customer customer;

    private boolean mapLoadComplete = false;

    public CustomerEditorMapPanel() {

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
        map.setSize("400px", "400px");
        map.setStyleName(SiteCSSClass.pyx4j_Site_Map.name());
        map.setCenter(pos, 15);

        map.addControl(new LargeMapControl());

        markerOptions = MarkerOptions.newInstance();

        Icon icon = Icon.newInstance("images/house.png");
        icon.setShadowURL("images/house_shadow.png");
        icon.setIconSize(Size.newInstance(30, 30));
        icon.setShadowSize(Size.newInstance(44, 35));
        icon.setIconAnchor(Point.newInstance(6, 20));
        icon.setInfoWindowAnchor(Point.newInstance(15, 5));
        markerOptions.setIcon(icon);

        marker = new Marker(pos, markerOptions);

        add(map);

        //How to find panoid - http://diddling.blogspot.com/2008/01/hacking-google-street-view.html
        //http://maps.google.com/cbk?output=xml&ll=37.4451,-122.125577 - see xml

        // Address to LatLng - http://maps.google.com/maps/geo?q={address}&output=csv

        streetViewHolder = new SimplePanel();
        streetViewHolder.getElement().getStyle().setProperty("padding", "0px 10px 0px 10px");
        add(streetViewHolder);

        mapLoadComplete = true;

        if (customer != null) {
            populate(customer);
        }
    }

    void populate(Customer customer) {
        this.customer = customer;
        if (mapLoadComplete) {
            map.removeOverlay(marker);
            if (!customer.location().isNull()) {
                markerOptions.setTitle(customer.name().getValue());
                LatLng latLng = MapUtils.newLatLngInstance(customer.location().getValue());
                marker.setLatLng(latLng);
                map.setCenter(latLng);
                map.addOverlay(marker);
            }

            streetViewHolder.clear();
            if (customer.panoId().getValue() != null) {
                HTML streetView = new HTML(
                        "<object type='application/x-shockwave-flash' name='panoflash1' id='panoflash1' align='middle' style='position: relative; visibility: visible; ' data='http://maps.gstatic.com/intl/en_ALL/mapfiles/cb/googlepano.104.swf' width='400' height='400'><param name='allowscriptaccess' value='always'><param name='scale' value='noScale'><param name='salign' value='lt'><param name='allowfullscreen' value='true'><param name='swliveconnect' value='false'><param name='wmode' value=''><param name='quality' value='high'><param name='bgcolor' value='#000000'><param name='flashvars' value='panoId="
                                + customer.panoId().getValue()
                                + "&amp;directionMap=N:N,W:W,S:S,E:E,NW:NW,NE:NE,SW:SW,SE:SE&amp;yaw=150.67939649880157&amp;zoom=0&amp;pitch=-10&amp;viewerId=1&amp;context=api&amp;useSsl=false&amp;csiCallback=&amp;userPhotoRepositories=all&amp;rtfArgs=hl:en,gl:,fs:1,sv:1,ph:0'></object>");
                streetViewHolder.setWidget(streetView);
            }
        }

    }
}
