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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Consts;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.gwt.server.IOUtils;

public class GeoDataEnhancer {

    private static final Logger log = LoggerFactory.getLogger(GeoDataEnhancer.class);

    //How to find panoid - http://diddling.blogspot.com/2008/01/hacking-google-street-view.html
    //http://maps.google.com/cbk?output=xml&ll=37.4451,-122.125577 - see xml

    // Address to LatLng - http://maps.google.com/maps/geo?q={address}&output=csv

    public static GeoPoint getLatLng(String address) {
        BufferedReader reader = null;
        try {
            URL url = new URL("http://maps.google.com/maps/geo?q=" + URLEncoder.encode(address, "utf8") + "&output=csv");
            String line = null;
            for (int retry = 0; retry < 3; retry++) {
                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                line = reader.readLine();
                String[] s = line.split(",");
                if ("200".equals(s[0])) {
                    GeoPoint gp = new GeoPoint(Double.parseDouble(s[2]), Double.parseDouble(s[3]));
                    if (gp.getLat() == 0) {
                        return null;
                    } else {
                        return gp;
                    }
                } else if ("620".equals(s[0])) {
                    // retry, google do not allow more then 10 QPS
                    IOUtils.closeQuietly(reader);
                    try {
                        Thread.sleep(Consts.SEC2MILLISECONDS);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    log.warn("Unexpected GEO responce '{}'", line);
                    return null;
                }
            }
            log.warn("Failed to retry, last responce '{}'", line);
            return null;
        } catch (IOException e) {
            throw new RuntimeException("Communication error", e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
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
