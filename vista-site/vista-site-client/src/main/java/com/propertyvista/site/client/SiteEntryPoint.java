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
package com.propertyvista.site.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.gwt.commons.BrowserType;
import com.pyx4j.gwt.commons.UncaughtHandler;
import com.pyx4j.gwt.geo.GoogleAPI;
import com.pyx4j.log4gwt.client.ClientLogger;
import com.pyx4j.site.client.NavigationUri;

import com.propertyvista.shared.config.VistaSettings;
import com.propertyvista.site.client.ui.maps.PropertyMapController;
import com.propertyvista.site.client.ui.maps.PropertyMapWidget;

public class SiteEntryPoint implements EntryPoint {

    private static final Logger log = LoggerFactory.getLogger(SiteEntryPoint.class);

    public static final String APTLIST_MAP_INSERTION_ID = "vista.aptlistmap";

    public static final String APTINFO_MAP_INSERTION_ID = "vista.aptinfomap";

    @Override
    public void onModuleLoad() {
        String customKey = getPortalGoogleAPIKey();
        if ((customKey != null) && (customKey.length() > 0)) {
            GoogleAPI.setGoogleAPIKey(customKey);
        } else {
            GoogleAPI.setGoogleAPIKey(VistaSettings.googleAPIKey);
        }
        GoogleAPI.setGoogleAPIKey(""); // Maps V2 Hack - Google won't validate empty key
        GoogleAPI.setMapApiVersion(VistaSettings.googleMapApiVersion);
        UncaughtHandler.setUnrecoverableErrorHandler(new VistaPortalWicketFragmentUnrecoverableErrorHandler());

        if (RootPanel.get(APTLIST_MAP_INSERTION_ID) != null) {
            RootPanel.get(APTLIST_MAP_INSERTION_ID).add(PropertyMapController.getMapWidget());
            if (RootPanel.get(APTINFO_MAP_INSERTION_ID) != null) {
                PropertyMapWidget map = PropertyMapWidget.get();
                RootPanel.get(APTINFO_MAP_INSERTION_ID).add(map);
                map.setSize("300px", "300px");
                map.loadMap();
            }
        }

        if (ApplicationMode.isDevelopment()) {
            ClientLogger.setDebugOn(true);
            log.debug("{}", BrowserType.getCompiledType());
            log.debug("GWT.getPermutationStrongName       {}", GWT.getPermutationStrongName());
            log.debug("GWT.getModuleName                  {}", GWT.getModuleName());
            log.debug("GWT.getModuleBaseURL               {}", GWT.getModuleBaseURL());
            log.debug("GWT.getHostPageBaseURL             {}", GWT.getHostPageBaseURL());
            log.debug("GWT.getModuleBaseForStaticFiles    {}", GWT.getModuleBaseForStaticFiles());
            log.debug("NavigationUri.getDeploymentBaseURL {}", NavigationUri.getDeploymentBaseURL());
            log.debug("NavigationUri.getHostPageURL       {}", NavigationUri.getHostPageURL());
        }
    }

    public final native String getPortalGoogleAPIKey() /*-{
		return $wnd.gwtPortalGoogleAPIKey();
    }-*/;
}
