/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 23, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.server.common.reference.geo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.geo.GeoPoint;

import com.propertyvista.server.common.reference.geo.googleapis.GeocodeResponse;
import com.propertyvista.server.common.reference.geo.googleapis.GoogleMapRestService;

public class GeoDataEnhancer {

    private static final Logger log = LoggerFactory.getLogger(GeoDataEnhancer.class);

    public static GeoPoint getLatLng(String address) {
        GeocodeResponse result = GoogleMapRestService.getGeocode(address);
        if ("OK".equals(result.status) && (result.result.geometry.location.lat != null) && (result.result.geometry.location.lng != null)) {
            GeoPoint gp = new GeoPoint(Double.parseDouble(result.result.geometry.location.lat), Double.parseDouble(result.result.geometry.location.lng));
            if (gp.getLat() == 0) {
                return null;
            } else {
                return gp;
            }
        } else {
            log.warn("Unexpected GEO response {}", result);
            return null;
        }
    }

    //How to find panoid - http://diddling.blogspot.com/2008/01/hacking-google-street-view.html
    //http://maps.google.com/cbk?output=xml&ll=37.4451,-122.125577 - see xml
    public static String getPanoId(GeoPoint point) throws Exception {
        String panoId = "";
        URL url = new URL("http://maps.google.com/cbk?output=xml&ll=" + point.getLat() + "," + point.getLng());
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String line = reader.readLine();
        Pattern pattern = Pattern.compile("\\spano_id=\"(\\S*)\"\\s");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            panoId = matcher.group(1);
        }
        reader.close();
        return panoId;
    }
}
