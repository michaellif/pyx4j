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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.gwt.geo.GoogleAPI;

import com.propertyvista.portal.client.ui.maps.PropertyMapController;

public class PortalEntryPoint implements EntryPoint {

    public static final String APT_MAP_INSERTION_ID = "vista.aptmap";

    @Override
    public void onModuleLoad() {
        if (Window.Location.getHost().endsWith("birchwoodsoftwaregroup.com")) {
            // Key for .birchwoodsoftwaregroup.com
            GoogleAPI.setGoogleAPIKey("ABQIAAAAfWHWzhfYNuypHiKXdxVi1hQNAqXoqeDSmjSd0LqmyIBhhU5npBSrKP1emJkpH44tWO17lL5gHAI_vg");
        } else {
            // Key for .residentportalsite.com
            GoogleAPI.setGoogleAPIKey("ABQIAAAAfWHWzhfYNuypHiKXdxVi1hT_reCJphII0xq04pEBPin6xLE3_xTP25TFN5BRmIeHnTU_tgz_y1HAZg");
        }

        if (RootPanel.get(APT_MAP_INSERTION_ID) != null) {
            RootPanel.get(APT_MAP_INSERTION_ID).add(PropertyMapController.getMapWidget());
        } else {
            new PortalSite().onModuleLoad();
        }

    }
}
