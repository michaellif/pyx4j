/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 11, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.jaxws;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.handler.MessageContext;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.propertyvista.oapi.PortAllocator;

public class GreetingTest extends TestCase {

    private static int port;

    private static Endpoint endpoint;

    private static String getAddress(int port) {
        return "http://localhost:" + port + "/WS/Greeting";
    }

    public void testContext() throws Exception {
        assertEquals(HttpURLConnection.HTTP_OK, getHttpStatusCode(getAddress(port)));
    }

    public void testMessage() throws Exception {

        GreetingStub stub = new GreetingStub(new URL(getAddress(port)));

        Greeting greeting = stub.getGreetingImplPort();

        Map<String, Object> requestContext = ((BindingProvider) greeting).getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getAddress(port) + "?wsdl");

        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.put("Username", Collections.singletonList("user"));
        headers.put("Password", Collections.singletonList("password"));
        requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, headers);

        String say = "Hi!";
        assertEquals("User says: " + say, greeting.say(say));

    }

    private int getHttpStatusCode(String address) throws Exception {
        URL url = new URL(address + "?wsdl");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.connect();
        int code = con.getResponseCode();
        return code;
    }

    public static Test suite() {
        TestSetup setup = new TestSetup(new TestSuite(GreetingTest.class)) {
            @Override
            protected void setUp() throws Exception {
                port = PortAllocator.allocatePort();
                int monitorPort = port;
                //For TCP/IP monitor
                if (false) {
                    port = 8888;
                    monitorPort = 8080;
                }
                endpoint = Endpoint.publish(getAddress(monitorPort), new GreetingImpl());
                super.setUp();
            }

            @Override
            protected void tearDown() throws Exception {
                super.tearDown();
                if (false) {
                    endpoint.stop();
                }
            }
        };
        return setup;
    }
}