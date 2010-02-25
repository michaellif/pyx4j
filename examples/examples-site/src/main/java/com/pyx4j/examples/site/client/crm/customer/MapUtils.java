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
 * Created on Feb 25, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.examples.site.client.crm.customer;

import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LatLngCallback;
import com.google.gwt.maps.client.geom.LatLng;
import com.pyx4j.examples.site.client.GoogleAPI;
import com.pyx4j.geo.GeoPoint;

public class MapUtils {

    public static void obtainLatLang(final String address, final LatLngCallback callback) {

        GoogleAPI.ensureInitialized();
        AjaxLoader.loadApi("maps", "2", new Runnable() {
            public void run() {
                new Geocoder().getLatLng(address, callback);

            }
        }, null);
    }

    public static LatLng newLatLngInstance(GeoPoint geoPoint) {
        return LatLng.newInstance(geoPoint.getLat(), geoPoint.getLng());
    }

    public static GeoPoint newGeoPointInstance(LatLng latLng) {
        return new GeoPoint(latLng.getLatitude(), latLng.getLongitude());
    }
}
