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
import java.util.ArrayList;
import java.util.List;

public class GeoPoint implements Serializable {

    private static final long serialVersionUID = -5427495876750723533L;

    private double lat;

    private double lng;

    private transient List<String> cells;

    public GeoPoint(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;

    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public List<String> getCells() {
        if (cells == null)
            computeCells();
        return cells;
    }

    private void computeCells() {
        cells = new ArrayList<String>();
        for (int i = 1; i <= GeoCell.MAX_GEOCELL_RESOLUTION; i++) {
            cells.add(GeoCell.compute(this, i));
        }
    }

    @Override
    public String toString() {
        return lat + "/" + lng;
    }

    public GeoPoint normalize() {
        if (lat >= -90 && lat <= 90 && lng >= -180 && lng >= -180)
            return this;
        double la = lat;
        double lo = lng;
        if (la > 90)
            la = -90 + (la - 90);
        if (la < -90)
            la = 90 - (-90 - la);
        if (lo > 180)
            lo = -180 + (lo - 180);
        if (lo < -180)
            lo = 180 - (-180 - lo);
        return new GeoPoint(la, lo);
    }
}
