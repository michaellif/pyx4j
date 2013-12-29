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
package com.propertyvista.yardi.ws;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.ws.Endpoint;

import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSTestBase {

    private static final Logger log = LoggerFactory.getLogger(WSTestBase.class);

    Endpoint endpoint = null;

    @After
    public void stop() {
        if (endpoint != null) {
            endpoint.stop();
        }
        endpoint = null;
    }

    private String getAddress(int port) {
        return "http://localhost:" + port + "/vista/interfaces/ws/ApplicantScreening";
    }

    protected String getAddress() {
        return getAddress(7771);
    }

    void publish(Class<?> serviceClass) throws Exception {
        assert endpoint == null;
        String address = getAddress();
        log.info("Address: {}", address);
        endpoint = Endpoint.publish(address, Class.forName(serviceClass.getName()).newInstance());
    }

    int getHttpStatusCode(String address) throws Exception {
        URL url = new URL(address + "?wsdl");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.connect();
        int code = con.getResponseCode();
        return code;
    }

}
