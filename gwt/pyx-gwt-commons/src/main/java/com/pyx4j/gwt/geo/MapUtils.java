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
 */
package com.pyx4j.gwt.geo;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.maps.client.LoadApi;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.services.Geocoder;
import com.google.gwt.maps.client.services.GeocoderRequest;
import com.google.gwt.maps.client.services.GeocoderRequestHandler;
import com.google.gwt.maps.client.services.GeocoderResult;
import com.google.gwt.maps.client.services.GeocoderStatus;

import com.pyx4j.geo.GeoPoint;

public class MapUtils {

    public static void obtainLatLang(final String address, final LatLngCallback callback) {
        GoogleAPI.ensureInitialized();

        final GeocoderRequest request = GeocoderRequest.newInstance();
        request.setAddress(address);

        final GeocoderRequestHandler handler = new GeocoderRequestHandler() {

            @Override
            public void onCallback(JsArray<GeocoderResult> results, GeocoderStatus status) {
                switch (status) {
                case ZERO_RESULTS:
                    callback.onSuccess(null);
                    break;
                case OK:
                    if (results.length() > 0) {
                        GeocoderResult result = results.get(0);
                        callback.onSuccess(result.getGeometry().getLocation());
                    } else {
                        callback.onSuccess(null);
                    }
                    break;
                default:
                    callback.onFailure(new Error(status.name()));
                }
            }
        };

        Runnable onLoad = new Runnable() {
            @Override
            public void run() {
                Geocoder.newInstance().geocode(request, handler);
            }
        };

        LoadApi.go(onLoad, false);
    }

    public static LatLng newLatLngInstance(GeoPoint geoPoint) {
        return LatLng.newInstance(geoPoint.getLat(), geoPoint.getLng());
    }

    public static GeoPoint newGeoPointInstance(LatLng latLng) {
        return new GeoPoint(latLng.getLatitude(), latLng.getLongitude());
    }
}
