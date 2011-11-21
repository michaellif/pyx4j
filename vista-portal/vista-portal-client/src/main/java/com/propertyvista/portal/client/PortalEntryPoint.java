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
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.gwt.geo.GoogleAPI;

import com.propertyvista.portal.client.ui.maps.PropertyMapController;
import com.propertyvista.portal.client.ui.maps.PropertyMapWidget;

public class PortalEntryPoint implements EntryPoint {

    public static final String APTLIST_MAP_INSERTION_ID = "vista.aptlistmap";

    public static final String APTINFO_MAP_INSERTION_ID = "vista.aptinfomap";

    @Override
    public void onModuleLoad() {
        if (Window.Location.getHost().endsWith("residentportalsite.com")) {
            // Key for .residentportalsite.com
            GoogleAPI.setGoogleAPIKey("ABQIAAAAfWHWzhfYNuypHiKXdxVi1hShXpG0z1GYh8qtGf1RDMPw_eX7gBQVD9gTYQZV0bRTbYFAMAnoaOWTqQ");
        } else if (Window.Location.getHost().endsWith("propertyvista.com")) {
            // Key for propertyvista.com
            GoogleAPI.setGoogleAPIKey("ABQIAAAAfWHWzhfYNuypHiKXdxVi1hSyeUldaL5RpJeho94okp3eVm_TSRReYYuX9Yx5PqVq2CO9w36_0whAoQ");
        } else {
            // Key for .birchwoodsoftwaregroup.com
            GoogleAPI.setGoogleAPIKey("ABQIAAAAfWHWzhfYNuypHiKXdxVi1hQNAqXoqeDSmjSd0LqmyIBhhU5npBSrKP1emJkpH44tWO17lL5gHAI_vg");
        }

        if (RootPanel.get(APTLIST_MAP_INSERTION_ID) != null) {
            GWT.runAsync(new RunAsyncCallback() {
                @Override
                public void onFailure(Throwable caught) {
                }

                @Override
                public void onSuccess() {
                    RootPanel.get(APTLIST_MAP_INSERTION_ID).add(PropertyMapController.getMapWidget());
                }
            });
        } else if (RootPanel.get(APTINFO_MAP_INSERTION_ID) != null) {
            GWT.runAsync(new RunAsyncCallback() {
                @Override
                public void onFailure(Throwable caught) {
                }

                @Override
                public void onSuccess() {
                    PropertyMapWidget map = PropertyMapWidget.get();
                    RootPanel.get(APTINFO_MAP_INSERTION_ID).add(map);
                    map.setSize("300px", "300px");
                    map.loadMap();
                }
            });

        } else {
            GWT.runAsync(new RunAsyncCallback() {
                @Override
                public void onFailure(Throwable caught) {
                }

                @Override
                public void onSuccess() {
                    new PortalSite().onModuleLoad();
                }
            });

        }

    }
}
