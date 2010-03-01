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
 * Created on Feb 23, 2010
 * @author kaushansky
 * @version $Id$
 */
package com.pyx4j.geo;

import java.io.Serializable;

public class GeoCircle implements Serializable {

    private static final long serialVersionUID = 8531522974408382863L;

    public static final double KM_IN_LAT_DEGREE = 111.325;

    private GeoPoint center;

    private double radiusKm;

    protected GeoCircle() {

    }

    public GeoCircle(GeoPoint center, double radiusKm) {
        super();
        this.center = center;
        this.radiusKm = radiusKm;
    }

    public GeoPoint getCenter() {
        return center;
    }

    public void setCenter(GeoPoint center) {
        this.center = center;
    }

    public double getRadius() {
        return radiusKm;
    }

    public void setRadius(double radiusKm) {
        this.radiusKm = radiusKm;
    }

    public boolean contains(GeoPoint point) {
        return GeoUtils.distance(center, point) <= radiusKm;
    }

    public GeoBox getMinBox() {
        double radiusInDegreesLat = radiusKm / KM_IN_LAT_DEGREE;
        double latInRadians = (Math.PI / 180) * center.getLat();
        double radiusInDegreesLng = radiusInDegreesLat / Math.cos(latInRadians);
        GeoPoint ne = new GeoPoint(center.getLat() + radiusInDegreesLat, center.getLng() + radiusInDegreesLng);
        GeoPoint sw = new GeoPoint(center.getLat() - radiusInDegreesLat, center.getLng() - radiusInDegreesLng);
        GeoBox box = new GeoBox(ne, sw);
        box.normalize();
        return box;
    }
}
