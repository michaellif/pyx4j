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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

import com.propertyvista.domain.DemoData;
import com.propertyvista.oapi.XmlFormatter;

public class LeaseServiceRSClient {

    private static boolean isLocal = true;

    public static void main(String[] args) throws MalformedURLException {

        String leaseId = String.valueOf(System.currentTimeMillis());
        leaseId = leaseId.substring(leaseId.length() - 8, leaseId.length());

        String buildingCode = "B0";
        String unitNumber = "#100";
        String tenantId = "100";

        createLease(leaseId, buildingCode, tenantId, unitNumber);
        //getLease(leaseId);
    }

    public static void createLease(String leaseId, String buildingCode, String tenantId, String unitNumber) throws MalformedURLException {

        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("o001@pyx4j.com:vista", "o001@pyx4j.com".toCharArray());
            }
        });

        URL url = null;
        if (isLocal) {
            url = new URL("http://localhost:8888/vista/interfaces/oapi/v1/rs/leases/updateLease");
        } else {
            url = new URL("http://static-11.birchwoodsoftwaregroup.com/interfaces/oapi/v1/rs/leases/updateLease");
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
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><lease propertyCode=\""
                    + buildingCode
                    + "\" unitNumber=\""
                    + unitNumber
                    + "\" leaseId=\""
                    + leaseId
                    + "\"><price>1234.00</price><status>Active</status><leaseType>residentialUnit</leaseType><leaseTerm>months12</leaseTerm><leaseFrom>2010-01-01</leaseFrom><leaseTo>2014-01-01</leaseTo><tenants><tenant leaseId=\"+leaseId+\" tenantId=\"+tenantId+\"><firstName>Bob</firstName><lastName>Smith</lastName><birthDate>1965-01-01</birthDate><sex>Male</sex><email>bob.smith@gmail.com</email><phone>123-123-1234</phone></tenant></tenants></lease>");

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

    public static void getLease(String leaseId) throws MalformedURLException {

        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("o001@pyx4j.com:vista", "o001@pyx4j.com".toCharArray());
            }
        });

        URL url = null;
        if (isLocal) {
            url = new URL("http://localhost:8888/vista/interfaces/oapi/rs/leases/" + leaseId);
        } else {
            url = new URL("http://static-11.birchwoodsoftwaregroup.com/interfaces/oapi/rs/leases/" + leaseId);
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
