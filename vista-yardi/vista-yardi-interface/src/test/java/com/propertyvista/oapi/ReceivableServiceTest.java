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

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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

import com.propertyvista.oapi.model.Charge;
import com.propertyvista.oapi.model.Payment;
import com.propertyvista.oapi.model.Transaction;

public class ReceivableServiceTest extends TestCase {

    private static int port;

    private static String getAddress(int port) {
        return "http://localhost:" + port + "/WS/ReceivableService";
    }

    public void testContext() throws Exception {
        assertEquals(HttpURLConnection.HTTP_OK, getHttpStatusCode(getAddress(port)));
    }

    public void testMessage() throws Exception {

        ReceivableServiceStub stub = new ReceivableServiceStub(new URL(getAddress(port)));

        ReceivableService service = stub.getReceivableServicePort();

        Map<String, Object> requestContext = ((BindingProvider) service).getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getAddress(port) + "?wsdl");

        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.put("Username", Collections.singletonList("user"));
        headers.put("Password", Collections.singletonList("password"));
        requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, headers);

        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(new Charge("tr1", new BigDecimal("11")));
        transactions.add(new Charge("tr2", new BigDecimal("22")));
        transactions.add(new Charge("tr3", new BigDecimal("33")));
        transactions.add(new Payment("tr2", new BigDecimal("22")));
        transactions.add(new Payment("tr3", new BigDecimal("33")));

        service.postTransactions(transactions);

        System.err.println("+++++++++++");
    }

    private int getHttpStatusCode(String address) throws Exception {
        URL url = new URL(address + "?wsdl");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.connect();
        int code = con.getResponseCode();
        return code;
    }

    public static Test suite() {
        TestSetup setup = new TestSetup(new TestSuite(ReceivableServiceTest.class)) {
            @Override
            protected void setUp() throws Exception {
                port = PortAllocator.allocatePort();
                int monitorPort = port;
                //For TCP/IP monitor
                if (true) {
                    port = 8888;
                    monitorPort = 8080;
                }
                Endpoint.publish(getAddress(monitorPort), new ReceivableServiceImpl());
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