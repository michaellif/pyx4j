/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 25, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.MapMoveEndHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.gwt.geo.GoogleAPI;

public abstract class AbstractMapWidget extends SimplePanel {

    private static Logger log = LoggerFactory.getLogger(PropertiesMapWidget.class);

    private MapWidget map;

    private boolean mapLoadComplete = false;

    private final String width;

    private final String height;

    AbstractMapWidget(String width, String height) {
        this.width = width;
        this.height = height;
        setSize(width, height);
        GoogleAPI.ensureInitialized();
    }

    public void loadMap() {
        log.info("loadMap() called, mapLoadComplete=" + mapLoadComplete);
        if (!mapLoadComplete) {
            AjaxLoader.loadApi("maps", "2", new Runnable() {
                @Override
                public void run() {
                    mapLoadComplete = true;
                    onMapLoaded();
                }
            }, null);
            Timer t = new Timer() {
                @Override
                public void run() {
                    onMapLoaded();
                }
            };
            t.schedule(3000);
        } else {
            onMapLoaded();
        }
    }

    protected void onMapLoaded() {
        if (!mapLoadComplete) {
            log.info("Gmaps not available");
            Label failMsg = new Label("Sorry, Google maps not available.");
            failMsg.setStyleName("googleMapsTimeout-error");
            setWidget(failMsg);
            return;
        }

        log.info("call onMapLoaded()");

        LatLng pos = LatLng.newInstance(43.7571145, -79.5082499);

        map = new MapWidget(pos, 10);

        map.setScrollWheelZoomEnabled(true);
        map.setSize(width, height);

        //TODO
        //map.setStyleName();

        map.addControl(new LargeMapControl());
        setWidget(map);

    }

    public void addMapMoveEndHandler(final MapMoveEndHandler handler) {
        map.addMapMoveEndHandler(handler);
    }

    protected boolean isMapLoadComplete() {
        return mapLoadComplete;
    }

    protected MapWidget getMap() {
        return map;
    }

}
