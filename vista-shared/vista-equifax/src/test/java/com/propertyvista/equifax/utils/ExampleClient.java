/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-23
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.equifax.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import ca.equifax.uat.to.CNConsAndCommRequestType;

import com.pyx4j.essentials.j2se.HostConfig.ProxyConfig;

import com.propertyvista.config.SystemConfig;

public class ExampleClient {

    public static String serverUrl = "https://uat.equifax.ca/sts";

    public static Object execute(CNConsAndCommRequestType requestMessage) throws Exception {
        HttpURLConnection conn = null;
        OutputStream out = null;
        InputStream in = null;

        System.out.println("Connect to " + serverUrl);
        ProxyConfig proxy = SystemConfig.instance().getCaledonProxy();
        if (proxy != null) {
            System.out.println("Connect via " + proxy.getHost());
            System.setProperty("http.proxyHost", proxy.getHost());
            System.setProperty("http.proxyPort", String.valueOf(proxy.getPort()));
            if (proxy.getUser() != null) {
                System.setProperty("http.proxyUser", proxy.getUser());
                System.setProperty("http.proxyPassword", proxy.getPassword());
            }
        }

        try {
            URL u = new URL(serverUrl);
            conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            conn.setRequestProperty("Content-Type", "text/xml");

            out = conn.getOutputStream();

            QName qname = new QName("http://www.equifax.ca/XMLSchemas/CustToEfx", "CNCustTransmitToEfx");
            JAXBElement<CNConsAndCommRequestType> element = new JAXBElement<CNConsAndCommRequestType>(qname, CNConsAndCommRequestType.class, requestMessage);

            JAXBContext context = JAXBContext.newInstance(CNConsAndCommRequestType.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(element, out);

            out.flush();
            out.close();
            out = null;

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                in = conn.getInputStream();
                //JAXBContext context = JAXBContext.newInstance(ResponseMessage.class);
                //Unmarshaller um = context.createUnmarshaller();
                //ResponseMessage message = (ResponseMessage) um.unmarshal(in);
                in.close();
                //return message;
                return null;
            } else {
                throw new RuntimeException(responseCode + ":" + conn.getResponseMessage());
            }
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignore) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignore) {
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
