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

import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.gwt.geo.GoogleAPI;

public abstract class AbstractMapWidget extends SimplePanel {

    private MapWidget map;

    private boolean mapLoadComplete = false;

    private final String width;

    private final String height;

    AbstractMapWidget(String width, String height) {
        this.width = width;
        this.height = height;

        setSize(width, height);

        getElement().getStyle().setMarginBottom(10, Unit.PX);
        GoogleAPI.ensureInitialized();

    }

    private void loadMaps() {
        AjaxLoader.loadApi("maps", "2", new Runnable() {
            @Override
            public void run() {
                mapsLoaded();
            }
        }, null);
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        if (isVisible() && !mapLoadComplete) {
            loadMaps();
        }
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        mapLoadComplete = false;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (isAttached() && !mapLoadComplete) {
            loadMaps();
        }
    }

    protected void mapsLoaded() {

        LatLng pos = LatLng.newInstance(43.7571145, -79.5082499);

        map = new MapWidget(pos, 10);
        map.setScrollWheelZoomEnabled(true);
        map.setSize(width, height);

        //TODO
        //map.setStyleName();

        map.addControl(new LargeMapControl());

        setWidget(map);

        mapLoadComplete = true;

    }

    protected boolean isMapLoadComplete() {
        return mapLoadComplete;
    }

    protected MapWidget getMap() {
        return map;
    }

}
