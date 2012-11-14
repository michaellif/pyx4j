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

import com.propertyvista.oapi.model.ChargeRS;
import com.propertyvista.oapi.model.PaymentRS;
import com.propertyvista.oapi.model.ServiceRS;
import com.propertyvista.oapi.model.TransactionRS;
import com.propertyvista.oapi.ws.ReceivableService;

public class ReceivableServiceTest extends OapiWsTest {

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
        for (int i = 1; i <= 3; i++) {
            ChargeRS charge = new ChargeRS();
            charge.description = "tr" + i;
            charge.amount = new BigDecimal("" + i + i);
            ServiceRS chargeService = new ServiceRS();
            chargeService.chargeCode = "ch" + i;
            charge.service = chargeService;
            charge.fromDate = new LogicalDate(112, 1, 1);
            charge.toDate = new LogicalDate(112, 2, 2);
            transactions.add(charge);
        }
        for (int i = 4; i <= 5; i++) {
            PaymentRS payment = new PaymentRS();
            payment.description = "tr" + i;
            payment.amount = new BigDecimal("" + i + i);
            transactions.add(payment);
        }

        service.postTransactions(transactions);

    }

}