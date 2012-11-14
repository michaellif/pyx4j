/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 13, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.ws;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.ws.Endpoint;

import com.propertyvista.oapi.PortAllocator;

public class OapiWsTest {

    private int port;

    private String serviceName;

    public int getPort() {
        return port;
    }

    private String getAddress(int port) {
        return "http://localhost:" + port + "/WS/" + serviceName;
    }

    String getAddress() {
        return getAddress(port);
    }

    void publish(Class<?> serviceClass) throws Exception {
        serviceName = serviceClass.getSimpleName();
        port = PortAllocator.allocatePort();
        int monitorPort = port;
        //For TCP/IP monitor
        if (false) {
            port = 8888;
            monitorPort = 8080;
        }
        Endpoint.publish(getAddress(monitorPort), Class.forName(serviceClass.getName() + "Impl").newInstance());
    }

    int getHttpStatusCode(String address) throws Exception {
        URL url = new URL(address + "?wsdl");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.connect();
        int code = con.getResponseCode();
        return code;
    }
}
