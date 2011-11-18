/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 16, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.maps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;

import com.pyx4j.geo.GeoPoint;
import com.pyx4j.gwt.geo.MapUtils;

import com.propertyvista.portal.client.resources.PortalImages;
import com.propertyvista.portal.domain.dto.PropertyDTO;

public class PropertyMapWidget extends AbstractMapWidget {

    private static Logger log = LoggerFactory.getLogger(PropertyMapWidget.class);

    private Marker marker;

    private PropertyDTO property;

    private GeoPoint location;

    private static PropertyMapWidget instance;

    private PropertyMapWidget() {
        super("300px", "300px");
        addJSHook();
    }

    public static PropertyMapWidget get() {
        if (instance == null) {
            instance = new PropertyMapWidget();
        }
        return instance;
    }

    @Override
    protected void onMapLoaded() {
        super.onMapLoaded();

        if (property != null) {
            populate(property);
        } else if (location != null) {
            populate(location);
        }
    }

    public void populate(PropertyDTO property) {
        this.property = property;
        setMarker(property.location().getValue(), property.address().getStringView());
    }

    public void populate(GeoPoint point) {
        this.location = point;
        setMarker(point, "Marker Title");
    }

    private void setMarker(final GeoPoint point, final String title) {
        if (isMapLoadComplete()) {
            if (marker != null) {
                getMap().removeOverlay(marker);
            }

            marker = createMarker(point, title);
            if (marker != null) {
                getMap().addOverlay(marker);
            }
            if (point != null) {
                getMap().setCenter(MapUtils.newLatLngInstance(point));
            }
            getMap().setZoomLevel(15);
        }
    }

    private Marker createMarker(final GeoPoint point, final String title) {
        MarkerOptions markerOptions = MarkerOptions.newInstance();
        markerOptions.setTitle(title);
        Icon icon = Icon.newInstance(PortalImages.INSTANCE.mapMarker().getSafeUri().asString());
        //TODO get shadow URL
        //icon.setShadowURL(FmRiaResources.INSTANCE.mapMarkerHouseShadow().getURL());
        icon.setIconSize(Size.newInstance(38, 41));
        icon.setShadowSize(Size.newInstance(44, 35));
        icon.setIconAnchor(Point.newInstance(15, 20));
        icon.setInfoWindowAnchor(Point.newInstance(15, 5));
        markerOptions.setIcon(icon);
        return new Marker(MapUtils.newLatLngInstance(point), markerOptions);
    }

    public static void populate(double lat, double lng) {
        instance.populate(new GeoPoint(lat, lng));
    }

    private static native void addJSHook() /*-{
		$wnd.locateOnMap = @com.propertyvista.portal.client.ui.maps.PropertyMapWidget::populate(DD);
    }-*/;
}