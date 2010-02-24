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
 * Created on Feb 24, 2010
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.examples.site.client.crm.customer;

import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Polygon;

public class CircleOverlay extends Polygon {

    public static final double KM_IN_LAT_DEGREE = 111.325;

    public CircleOverlay(LatLng center, double distance, String strokeColor, int strokeWeight, double strokeOpacity, String fillColor, double fillOpacity) {
        super(constructCirclePoints(center, distance), strokeColor, strokeWeight, strokeOpacity, fillColor, fillOpacity);

    }

    private static LatLng[] constructCirclePoints(LatLng center, double radius) {
        int numPoints = 40;
        LatLng[] circleLatLngs = new LatLng[numPoints + 1];
        double circleLat = radius / KM_IN_LAT_DEGREE;
        double circleLng = circleLat / Math.cos(center.getLatitudeRadians());

        // 2PI = 360 degrees, +1 so that the end points meet
        for (int i = 0; i < numPoints + 1; i++) {
            double theta = Math.PI * ((double) i * 2 / numPoints);
            double vertexLat = center.getLatitude() + circleLat * Math.sin(theta);
            double vertexLng = center.getLongitude() + circleLng * Math.cos(theta);
            LatLng vertextLatLng = LatLng.newInstance(vertexLat, vertexLng);
            circleLatLngs[i] = vertextLatLng;
        }
        return circleLatLngs;
    }
}
