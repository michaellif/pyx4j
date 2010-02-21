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
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CustomerListMapPanel extends SimplePanel {

    private MapWidget map;

    public CustomerListMapPanel() {
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

    }

}
