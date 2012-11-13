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

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

import org.junit.Before;
import org.junit.Test;

import com.propertyvista.oapi.model.ChargeRS;
import com.propertyvista.oapi.model.PaymentRS;
import com.propertyvista.oapi.model.TransactionRS;

public class ReceivableServiceTest extends OAPITest {

    @Before
    public void init() throws Exception {
        publish(ReceivableService.class);
    }

    @Test
    public void testContext() throws Exception {
        assertEquals(HttpURLConnection.HTTP_OK, getHttpStatusCode(getAddress()));
    }

    @Test
    public void testMessage() throws Exception {

        ReceivableServiceStub stub = new ReceivableServiceStub(new URL(getAddress()));

        ReceivableService service = stub.getReceivableServicePort();

        Map<String, Object> requestContext = ((BindingProvider) service).getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getAddress() + "?wsdl");

        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.put("Username", Collections.singletonList("user"));
        headers.put("Password", Collections.singletonList("password"));
        requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, headers);

        List<TransactionRS> transactions = new ArrayList<TransactionRS>();
        transactions.add(new ChargeRS("tr1", new BigDecimal("11")));
        transactions.add(new ChargeRS("tr2", new BigDecimal("22")));
        transactions.add(new ChargeRS("tr3", new BigDecimal("33")));
        transactions.add(new PaymentRS("tr2", new BigDecimal("22")));
        transactions.add(new PaymentRS("tr3", new BigDecimal("33")));

        service.postTransactions(transactions);

    }

}