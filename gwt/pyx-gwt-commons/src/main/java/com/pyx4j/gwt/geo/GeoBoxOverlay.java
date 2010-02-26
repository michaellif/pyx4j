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
 * Created on Feb 26, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.gwt.geo;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Polygon;

import com.pyx4j.geo.GeoBox;
import com.pyx4j.geo.GeoCell;

public class GeoBoxOverlay extends Polygon {

    public static final double KM_IN_LAT_DEGREE = 111.325;

    public GeoBoxOverlay(String geoBoxId) {
        super(constructGeoBoxPoints(geoBoxId), "red", 2, 0.4, "red", 0.1);

    }

    private static LatLng[] constructGeoBoxPoints(String geoBoxId) {
        GeoBox geoBox = GeoCell.computeBox(geoBoxId);
        LatLng[] geoBoxLatLngs = new LatLng[5];
        geoBoxLatLngs[0] = LatLng.newInstance(geoBox.getNorth(), geoBox.getEast());
        geoBoxLatLngs[1] = LatLng.newInstance(geoBox.getNorth(), geoBox.getWest());
        geoBoxLatLngs[2] = LatLng.newInstance(geoBox.getSouth(), geoBox.getWest());
        geoBoxLatLngs[3] = LatLng.newInstance(geoBox.getSouth(), geoBox.getEast());
        geoBoxLatLngs[4] = LatLng.newInstance(geoBox.getNorth(), geoBox.getEast());
        return geoBoxLatLngs;
    }
}