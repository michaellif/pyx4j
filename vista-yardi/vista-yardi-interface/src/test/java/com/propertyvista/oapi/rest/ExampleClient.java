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
package com.propertyvista.oapi.rest;

public class ExampleClient {

    public static String serverUrl = "http://localhost:8888/vista/interfaces/onboarding";

    public static String interfaceEntity = "romans@rossul.com";

    public static String interfaceEntityPassword = "secret";

//    public static ResponseMessage execute(RequestMessage requestMessage) throws Exception {
//
//        System.out.println("--sending to :" + serverUrl);
//        MarshallUtil.marshal(requestMessage, System.out);
//
//        HttpURLConnection conn = null;
//        OutputStream out = null;
//        InputStream in = null;
//        try {
//            URL u = new URL(serverUrl);
//            conn = (HttpURLConnection) u.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//            conn.setUseCaches(false);
//
//            conn.setRequestProperty("Content-Type", "text/xml");
//
//            out = conn.getOutputStream();
//            MarshallUtil.marshal(requestMessage, out);
//
//            out.flush();
//            out.close();
//            out = null;
//
//            int responseCode = conn.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                System.out.println("--received:");
//                in = conn.getInputStream();
//                JAXBContext context = JAXBContext.newInstance(ResponseMessage.class);
//                Unmarshaller um = context.createUnmarshaller();
//                ResponseMessage message = (ResponseMessage) um.unmarshal(in);
//                MarshallUtil.marshal(message, System.out);
//                in.close();
//                return message;
//            } else {
//                throw new RuntimeException(responseCode + ":" + conn.getResponseMessage());
//            }
//        } finally {
//            if (out != null) {
//                try {
//                    out.close();
//                } catch (IOException ignore) {
//                }
//            }
//            if (in != null) {
//                try {
//                    in.close();
//                } catch (IOException ignore) {
//                }
//            }
//            if (conn != null) {
//                conn.disconnect();
//            }
//        }
//    }
}
