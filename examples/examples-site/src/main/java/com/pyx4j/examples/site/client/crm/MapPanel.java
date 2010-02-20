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
package com.pyx4j.examples.site.client.crm;

import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.StreetviewOverlayChangedHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.StreetviewOverlay;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.site.client.InlineWidget;

public class MapPanel extends InlineWidget {

    private MapWidget map;

    private StreetviewOverlay svOverlay;

    private boolean svShown;

    public MapPanel() {
        final VerticalPanel contentPanel = new VerticalPanel();
        setWidget(contentPanel);

        AjaxLoader.loadApi("maps", "2", new Runnable() {
            public void run() {
                mapsLoaded(contentPanel);
            }
        }, null);
    }

    private void mapsLoaded(VerticalPanel contentPanel) {

        LatLng pos = LatLng.newInstance(55, -93);

        map = new MapWidget(pos, 2);
        map.setSize("400px", "400px");
        map.setCenter(pos, 4);

        map.addControl(new LargeMapControl());

        contentPanel.add(map);
        contentPanel.setCellHorizontalAlignment(map, DockPanel.ALIGN_CENTER);

        //How to find panoid - http://diddling.blogspot.com/2008/01/hacking-google-street-view.html
        //http://maps.google.com/cbk?output=xml&ll=37.4451,-122.125577 - see xml

        // Address to LogLat - http://maps.google.com/maps/geo?q={address}&output=csv

        contentPanel
                .add(new HTML(
                        "<object type='application/x-shockwave-flash' name='panoflash1' id='panoflash1' align='middle' style='position: relative; visibility: visible; ' data='http://maps.gstatic.com/intl/en_ALL/mapfiles/cb/googlepano.104.swf' width='400' height='400'><param name='allowscriptaccess' value='always'><param name='scale' value='noScale'><param name='salign' value='lt'><param name='allowfullscreen' value='true'><param name='swliveconnect' value='false'><param name='wmode' value=''><param name='quality' value='high'><param name='bgcolor' value='#000000'><param name='flashvars' value='panoId=l3-YA_-GMcjy2yTsYx-GOg&amp;directionMap=N:N,W:W,S:S,E:E,NW:NW,NE:NE,SW:SW,SE:SE&amp;yaw=150.67939649880157&amp;zoom=0&amp;pitch=-10&amp;viewerId=1&amp;context=api&amp;useSsl=false&amp;csiCallback=&amp;userPhotoRepositories=all&amp;rtfArgs=hl:en,gl:,fs:1,sv:1,ph:0'></object>"));
    }
}
