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

import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;

import com.pyx4j.gwt.geo.MapUtils;

import com.propertyvista.portal.client.resources.PortalImages;
import com.propertyvista.portal.domain.dto.PropertyDTO;

public class PropertyMapWidget extends AbstractMapWidget {

    private Marker marker;

    private PropertyDTO property;

    public PropertyMapWidget() {
        super("300px", "300px");
    }

    @Override
    protected void mapsLoaded() {

        super.mapsLoaded();

        if (property != null) {
            populate(property);
        }

    }

    public void populate(PropertyDTO property) {
        this.property = property;

        if (isMapLoadComplete()) {
            if (marker != null) {
                getMap().removeOverlay(marker);
            }

            marker = createMarker(property);
            if (marker != null) {
                getMap().addOverlay(marker);
            }
            //TODO calc base on  markers
            getMap().setCenter(MapUtils.newLatLngInstance(property.location().getValue()));
            getMap().setZoomLevel(15);
        }
    }

    private Marker createMarker(final PropertyDTO property) {
        if (!property.location().isNull()) {
            MarkerOptions markerOptions = MarkerOptions.newInstance();
            markerOptions.setTitle(property.address().getStringView());

            Icon icon = Icon.newInstance(PortalImages.INSTANCE.mapMarker().getURL());
            //TODO get shadow URL
            //icon.setShadowURL(FmRiaResources.INSTANCE.mapMarkerHouseShadow().getURL());
            icon.setIconSize(Size.newInstance(38, 41));
            icon.setShadowSize(Size.newInstance(44, 35));
            icon.setIconAnchor(Point.newInstance(15, 20));
            icon.setInfoWindowAnchor(Point.newInstance(15, 5));
            markerOptions.setIcon(icon);
            return new Marker(MapUtils.newLatLngInstance(property.location().getValue()), markerOptions);
        } else {
            return null;
        }
    }

}