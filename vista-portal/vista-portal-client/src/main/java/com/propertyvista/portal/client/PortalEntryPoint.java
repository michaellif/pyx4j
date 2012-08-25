/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 8, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.gwt.geo.GoogleAPI;

import com.propertyvista.portal.client.ui.maps.PropertyMapController;
import com.propertyvista.portal.client.ui.maps.PropertyMapWidget;

public class PortalEntryPoint implements EntryPoint {

    public static final String APTLIST_MAP_INSERTION_ID = "vista.aptlistmap";

    public static final String APTINFO_MAP_INSERTION_ID = "vista.aptinfomap";

    @Override
    public void onModuleLoad() {
        String customKey = getPortalGoogleAPIKey();
        if ((customKey != null) && (customKey.length() > 0)) {
            GoogleAPI.setGoogleAPIKey(customKey);
        } else {
            GoogleAPI.setGoogleAPIKey("AIzaSyAa7EDFOm17VLX7tLJqCZfn2SX8qT5kYcE");
        }

        if (RootPanel.get(APTLIST_MAP_INSERTION_ID) != null) {
            RootPanel.get(APTLIST_MAP_INSERTION_ID).add(PropertyMapController.getMapWidget());
            if (RootPanel.get(APTINFO_MAP_INSERTION_ID) != null) {
                UncaughtHandler.setUnrecoverableErrorHandler(new VistaPortalWicketFragmentUnrecoverableErrorHandler());
                PropertyMapWidget map = PropertyMapWidget.get();
                RootPanel.get(APTINFO_MAP_INSERTION_ID).add(map);
                map.setSize("300px", "300px");
                map.loadMap();
            }
        } else {
            new PortalSite().onModuleLoad();
        }

    }

    public final native String getPortalGoogleAPIKey() /*-{
		return $wnd.gwtPortalGoogleAPIKey();
    }-*/;
}
