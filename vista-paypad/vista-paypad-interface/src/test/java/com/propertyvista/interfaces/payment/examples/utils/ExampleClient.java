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
package com.propertyvista.interfaces.payment.examples.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.propertyvista.interfaces.payment.RequestMessage;
import com.propertyvista.interfaces.payment.ResponseMessage;

public class ExampleClient {

    public static String serverUrl = "https://secure.paymentpad.com/payment";

    public static ResponseMessage execute(RequestMessage requestMessage) throws Exception {
        HttpURLConnection conn = null;
        OutputStream out = null;
        InputStream in = null;
        try {
            URL u = new URL(serverUrl);
            conn = (HttpURLConnection) u.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            conn.setRequestProperty("Content-Type", "text/xml");

            out = conn.getOutputStream();
            MarshallUtil.marshal(requestMessage, out);

            out.flush();
            out.close();
            out = null;

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                in = conn.getInputStream();
                JAXBContext context = JAXBContext.newInstance(ResponseMessage.class);
                Unmarshaller um = context.createUnmarshaller();
                ResponseMessage message = (ResponseMessage) um.unmarshal(in);
                in.close();
                return message;
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
