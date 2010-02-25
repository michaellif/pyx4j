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

public class GeoBox implements Serializable {

    private static final long serialVersionUID = 1546352713191347593L;

    private GeoPoint northEast;

    private GeoPoint southWest;

    public GeoBox(double north, double east, double south, double west) {
        if (south > north) {
            double tmp = north;
            north = south;
            south = tmp;
        }
        northEast = new GeoPoint(north, east);
        southWest = new GeoPoint(south, west);
    }

    public GeoBox(GeoPoint northEast, GeoPoint southWest) {
        this(northEast.getLat(), northEast.getLng(), southWest.getLat(), southWest.getLng());
    }

    public GeoPoint getNorthEast() {
        return northEast;
    }

    public void setNorthEast(GeoPoint northEast) {
        this.northEast = northEast;
    }

    public GeoPoint getSouthWest() {
        return southWest;
    }

    public void setSouthWest(GeoPoint southWest) {
        this.southWest = southWest;
    }

    public double getNorth() {
        return this.northEast.getLat();
    }

    public double getEast() {
        return this.northEast.getLng();
    }

    public double getSouth() {
        return this.southWest.getLat();
    }

    public double getWest() {
        return this.southWest.getLng();
    }

    public boolean contains(GeoPoint point) {
        double lat = point.getLat();
        double lng = point.getLng();
        boolean between_w_e = southWest.getLng() <= lng && lng <= northEast.getLng();
        boolean between_n_s = southWest.getLat() <= lat && lat <= northEast.getLat();
        return between_w_e && between_n_s;
    }

    @Override
    public String toString() {
        return northEast + "," + southWest;
    }

    public void normalize() {
        southWest = southWest.normalize();
        northEast = northEast.normalize();
    }
}
