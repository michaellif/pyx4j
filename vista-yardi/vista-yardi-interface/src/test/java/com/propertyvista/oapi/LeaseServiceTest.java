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
package com.propertyvista.oapi;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.propertyvista.oapi.model.Lease;

public class LeaseServiceTest extends TestCase {

    private static int port;

    private static String getAddress(int port) {
        return "http://localhost:" + port + "/WS/LeaseService";
    }

    public void testContext() throws Exception {
        assertEquals(HttpURLConnection.HTTP_OK, getHttpStatusCode(getAddress(port)));
    }

    public void testMessage() throws Exception {

        LeaseServiceStub stub = new LeaseServiceStub(new URL(getAddress(port)));

        LeaseService service = stub.getLeaseServicePort();

        Map<String, Object> requestContext = ((BindingProvider) service).getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getAddress(port) + "?wsdl");

        service.createLease(new Lease("l1"));

        Lease lease = service.getLeaseByLeaseId("l1");

        assertEquals("l1", lease.leaseId);

    }

    private int getHttpStatusCode(String address) throws Exception {
        URL url = new URL(address + "?wsdl");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.connect();
        int code = con.getResponseCode();
        return code;
    }

    public static Test suite() {
        TestSetup setup = new TestSetup(new TestSuite(LeaseServiceTest.class)) {
            @Override
            protected void setUp() throws Exception {
                port = PortAllocator.allocatePort();
                int monitorPort = port;
                //For TCP/IP monitor
                if (false) {
                    port = 8888;
                    monitorPort = 8080;
                }
                Endpoint.publish(getAddress(monitorPort), new LeaseServiceImpl());
                super.setUp();
            }

            @Override
            protected void tearDown() throws Exception {
                super.tearDown();
            }
        };
        return setup;
    }
}