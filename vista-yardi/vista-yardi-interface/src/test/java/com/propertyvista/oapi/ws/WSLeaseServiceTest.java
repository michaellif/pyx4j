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
package com.propertyvista.oapi.ws;

import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import org.junit.Before;
import org.junit.Test;

import com.propertyvista.oapi.model.LeaseIO;
import com.propertyvista.oapi.ws.WSLeaseService;

public class WSLeaseServiceTest extends WSOapiTestBase {

    @Before
    public void init() throws Exception {
        publish(WSLeaseService.class);
    }

    @Test
    public void testContext() throws Exception {
        assertEquals(HttpURLConnection.HTTP_OK, getHttpStatusCode(getAddress()));
    }

    @Test
    public void testMessage() throws Exception {

        WSLeaseServiceStub stub = new WSLeaseServiceStub(new URL(getAddress()));

        WSLeaseService service = stub.getLeaseServicePort();

        Map<String, Object> requestContext = ((BindingProvider) service).getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getAddress() + "?wsdl");

        service.createLease(new LeaseIO("l1"));

        LeaseIO lease = service.getLeaseByLeaseId("l1");

        assertEquals("l1", lease.leaseId);

    }

}