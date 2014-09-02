/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 28, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.v1.rs.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

import com.propertyvista.oapi.XmlFormatter;
import com.propertyvista.oapi.v1.Version;

public class BuildingServiceRSClient {

    private static boolean isLocal = true;

    public static void main(String[] args) throws MalformedURLException {

        String buildingCode = String.valueOf(System.currentTimeMillis());
        buildingCode = buildingCode.substring(buildingCode.length() - 8, buildingCode.length());

        CookieHandler.setDefault(new CookieManager());

        createBuilding(buildingCode);
        getBuilding(buildingCode);
    }

    public static void createBuilding(String buildingCode) throws MalformedURLException {

        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("o001@pyx4j.com:vista", "o001@pyx4j.com".toCharArray());
            }
        });

        URL url = null;
        if (isLocal) {
            url = new URL("http://localhost:8888/vista/interfaces/oapi/" + Version.VERSION_NAME + "/rs/buildings/createBuilding");
        } else {
            url = new URL("http://static-66.birchwoodsoftwaregroup.com/interfaces/oapi/" + Version.VERSION_NAME + "/rs/buildings/createBuilding");
        }
        HttpURLConnection conn = null;
        OutputStreamWriter out = null;
        try {
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            conn.setAllowUserInteraction(false);
            conn.setRequestProperty("Content-type", "application/xml");
            conn.setRequestProperty("Accept", "application/*");

            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");

            //out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><building propertyCode=\"" + buildingCode + "\"></building>");
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><building propertyCode=\""
                    + buildingCode
                    + "\"><info><buildingType>mixed_residential</buildingType><address><streetNumber>1206</streetNumber><streetName>Emerson</streetName><streetType>avenue</streetType><city>Saskatoon</city><province>Saskatchewan</province><postalCode>S7H 2X1</postalCode><country>Canada</country></address></info><contacts><contact name=\"Cathern Petters\"><email>cathern.petters@yahoo.ca</email><phone>905-306-0112</phone></contact></contacts><marketing><name>emerson1206 mktRG</name><description>Curabitur sem velit, ullamcorper nec sagittis et, fringilla at risus. Donec eleifend convallis massa, ac commodo odio condimentum eu.</description><blurbs/></marketing><units/><medias><media><mediaType>youTube</mediaType><caption>A emerson1206 video</caption><youTubeVideoID>rDZR0RglALI</youTubeVideoID><url/></media><media><mediaType>file</mediaType><caption>building6</caption><youTubeVideoID/><url/></media><media><mediaType>file</mediaType><caption>building6-1</caption><youTubeVideoID/><url/></media></medias><amenities><amenity><name>Elevator JI</name><description>Libero consectetur pharetra</description></amenity><amenity><name>Play Ground CL</name><description>Pellentesque et enim a eros rutrum dapibus</description></amenity><amenity><name>Business Center SH</name><description>Non fringilla diam</description></amenity><amenity><name>House Sitting TL</name><description>Pellentesque vitae turpis vitae</description></amenity><amenity><name>On-Site Maintenance EG</name><description>Neque porro quisquam</description></amenity></amenities><parkings><parking name=\"Parking1\"><description>3-level parking1 at emerson1206</description><type>garageLot</type><levels>3.0</levels></parking></parkings></building>");
            //      + "\"><info><buildingType>mixed_residential</buildingType><address><streetNumber>1206</streetNumber><streetName>Emerson</streetName><streetType>avenue</streetType><city>Saskatoon</city><province>Saskatchewan</province><postalCode>S7H 2X1</postalCode><country>Canada</country></address></info><contacts><contact name=\"Cathern Petters\"><email>cathern.petters@yahoo.ca</email><phone>905-306-0112</phone></contact></contacts><marketing><name>emerson1206 mktRG</name><description>Curabitur sem velit, ullamcorper nec sagittis et, fringilla at risus. Donec eleifend convallis massa, ac commodo odio condimentum eu.</description><blurbs/></marketing><units/><medias><media><mediaType>youTube</mediaType><caption>A emerson1206 video</caption><youTubeVideoID>rDZR0RglALI</youTubeVideoID><url/></media><media><mediaType>file</mediaType><caption>building6</caption><youTubeVideoID/><url/></media><media><mediaType>file</mediaType><caption>building6-1</caption><youTubeVideoID/><url/></media></medias><amenities><amenity><name>Elevator JI</name><description>Libero consectetur pharetra</description></amenity><amenity><name>Play Ground CL</name><description>Pellentesque et enim a eros rutrum dapibus</description></amenity><amenity><name>Business Center SH</name><description>Non fringilla diam</description></amenity><amenity><name>House Sitting TL</name><description>Pellentesque vitae turpis vitae</description></amenity><amenity><name>On-Site Maintenance EG</name><description>Neque porro quisquam</description></amenity></amenities><parkings><parking name=\"Parking1\"><description>3-level parking1 at emerson1206</description><type>garageLot</type><levels>3.0</levels></parking></parkings><includedUtilities><utility><name>Hydro</name></utility></includedUtilities></building>");

            out.flush();
            out.close();

            conn.getInputStream();

            InputStream inputStream = null;
            inputStream = conn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }

            System.out.println(response.toString());
            bufferedReader.close();
            bufferedReader = null;

            inputStream.close();
            inputStream = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

    }

    public static void getBuilding(String buildingCode) throws MalformedURLException {

        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("o001@pyx4j.com:vista", "o001@pyx4j.com".toCharArray());
            }
        });

        URL url = null;
        if (isLocal) {
            url = new URL("http://localhost:8888/vista/interfaces/oapi/" + Version.VERSION_NAME + "/rs/buildings/" + buildingCode);
        } else {
            url = new URL("http://static-66.birchwoodsoftwaregroup.com/interfaces/oapi/" + Version.VERSION_NAME + "/rs/buildings/" + buildingCode);
        }

        HttpURLConnection conn = null;
        BufferedReader in = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder builder = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                builder.append(inputLine);
            }

            System.out.println(new XmlFormatter().format(builder.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

    }
}
