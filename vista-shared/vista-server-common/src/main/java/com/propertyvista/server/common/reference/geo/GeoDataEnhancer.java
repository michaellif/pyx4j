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
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pyx4j.geo.GeoPoint;

public class GeoDataEnhancer {

    //How to find panoid - http://diddling.blogspot.com/2008/01/hacking-google-street-view.html
    //http://maps.google.com/cbk?output=xml&ll=37.4451,-122.125577 - see xml

    // Address to LatLng - http://maps.google.com/maps/geo?q={address}&output=csv

    public static GeoPoint getLatLng(String address) throws Exception {
        GeoPoint point = null;
        URL url = new URL("http://maps.google.com/maps/geo?q=" + URLEncoder.encode(String.valueOf(address), "utf8") + "&output=csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String line = reader.readLine();
        String[] s = line.split(",");
        point = new GeoPoint(Double.parseDouble(s[2]), Double.parseDouble(s[3]));
        reader.close();
        return point;
    }

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
