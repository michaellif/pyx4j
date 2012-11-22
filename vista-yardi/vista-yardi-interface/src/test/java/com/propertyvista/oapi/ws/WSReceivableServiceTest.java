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

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.oapi.model.ChargeIO;
import com.propertyvista.oapi.model.PaymentIO;
import com.propertyvista.oapi.model.ServiceIO;
import com.propertyvista.oapi.model.TransactionIO;
import com.propertyvista.oapi.ws.WSReceivableService;

public class WSReceivableServiceTest extends WSOapiTest {

    @Before
    public void init() throws Exception {
        publish(WSReceivableService.class);
    }

    @Test
    public void testContext() throws Exception {
        assertEquals(HttpURLConnection.HTTP_OK, getHttpStatusCode(getAddress()));
    }

    @Test
    public void testMessage() throws Exception {

        WSReceivableServiceStub stub = new WSReceivableServiceStub(new URL(getAddress()));

        WSReceivableService service = stub.getReceivableServicePort();

        Map<String, Object> requestContext = ((BindingProvider) service).getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getAddress() + "?wsdl");

        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.put("Username", Collections.singletonList("user"));
        headers.put("Password", Collections.singletonList("password"));
        requestContext.put(MessageContext.HTTP_REQUEST_HEADERS, headers);

        List<TransactionIO> transactions = new ArrayList<TransactionIO>();
        for (int i = 1; i <= 3; i++) {
            ChargeIO charge = new ChargeIO();
            charge.description = "tr" + i;
            charge.amount = new BigDecimal("" + i + i);
            ServiceIO chargeService = new ServiceIO();
            chargeService.chargeCode = "ch" + i;
            charge.service = chargeService;
            charge.fromDate = new LogicalDate(112, 1, 1);
            charge.toDate = new LogicalDate(112, 2, 2);
            transactions.add(charge);
        }
        for (int i = 4; i <= 5; i++) {
            PaymentIO payment = new PaymentIO();
            payment.description = "tr" + i;
            payment.amount = new BigDecimal("" + i + i);
            transactions.add(payment);
        }

        service.postTransactions(transactions);

    }

}