/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 5, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.rs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;

import org.junit.Assert;
import org.junit.Test;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.oapi.model.ChargeIO;
import com.propertyvista.oapi.model.PaymentIO;
import com.propertyvista.oapi.model.PaymentRecordIO;
import com.propertyvista.oapi.model.ServiceIO;
import com.propertyvista.oapi.model.TransactionIO;
import com.propertyvista.oapi.xml.BigDecimalIO;
import com.propertyvista.oapi.xml.LogicalDateIO;
import com.propertyvista.oapi.xml.StringIO;

public class RSReceivableServiceTest extends RSOapiTestBase {

    public RSReceivableServiceTest() throws Exception {
        super("com.propertyvista.oapi.rs");
    }

    @Test
    public void testGetNonProcessedPaymentRecords() {
        WebResource webResource = resource();
        GenericType<List<PaymentRecordIO>> genericType = new GenericType<List<PaymentRecordIO>>() {
        };
        List<PaymentRecordIO> payments = webResource.path("payments/nonProcessed").get(genericType);
        Assert.assertTrue(payments.isEmpty());
    }

    @Test
    public void testRunBilling_NonExistingPropertyCode() {
        WebResource webResource = resource();

        ClientResponse response = webResource.path("payments/MockCode/runBilling").accept(MediaType.APPLICATION_XML).post(ClientResponse.class);
        Assert.assertEquals(ClientResponse.Status.OK, response.getClientResponseStatus());
    }

    @Test
    public void testReconcilePaymentRecords_EmptyPaymentRecordsList() {
        WebResource webResource = resource();

        List<PaymentRecordIO> records = new ArrayList<PaymentRecordIO>();

        ClientResponse response = webResource.path("payments/reconcile").accept(MediaType.APPLICATION_XML)
                .post(ClientResponse.class, new GenericEntity<List<PaymentRecordIO>>(records) {
                });
        Assert.assertEquals(ClientResponse.Status.OK, response.getClientResponseStatus());
    }

    @Test
    public void testReconcilePaymentRecords_MockPaymentRecordsList() {
        WebResource webResource = resource();

        List<PaymentRecordIO> records = new ArrayList<PaymentRecordIO>();
        PaymentRecordIO record1 = new PaymentRecordIO();
        record1.transactionId = "111";
        record1.externalTransactionId = new StringIO("222");
        records.add(record1);
        ClientResponse response = webResource.path("payments/reconcile").accept(MediaType.APPLICATION_XML)
                .post(ClientResponse.class, new GenericEntity<List<PaymentRecordIO>>(records) {
                });
        Assert.assertEquals(ClientResponse.Status.INTERNAL_SERVER_ERROR, response.getClientResponseStatus());
    }

    @Test
    public void testPostTransactions() {
        WebResource webResource = resource();

        List<TransactionIO> transactions = new ArrayList<TransactionIO>();
        for (int i = 1; i <= 3; i++) {
            ChargeIO charge = new ChargeIO();
            charge.description = new StringIO("tr" + i);
            charge.amount = new BigDecimalIO(new BigDecimal("" + i + i));
            ServiceIO chargeService = new ServiceIO();
            chargeService.chargeCode = "ch" + i;
            charge.service = chargeService;
            charge.fromDate = new LogicalDateIO(new LogicalDate(112, 1, 1));
            charge.toDate = new LogicalDateIO(new LogicalDate(112, 2, 2));
            transactions.add(charge);
        }
        for (int i = 4; i <= 5; i++) {
            PaymentIO payment = new PaymentIO();
            payment.description = new StringIO("tr" + i);
            payment.amount = new BigDecimalIO(new BigDecimal("" + i + i));
            payment.paymentType = new StringIO("Check");
            transactions.add(payment);
        }

        ClientResponse response = webResource.path("payments/transactions").accept(MediaType.APPLICATION_XML)
                .post(ClientResponse.class, new GenericEntity<List<TransactionIO>>(transactions) {
                });

    }

    @Test
    public void testPostTransactions_EmptyTransactionsList() {
        WebResource webResource = resource();

        List<TransactionIO> transactions = new ArrayList<TransactionIO>();

        ClientResponse response = webResource.path("payments/transactions").accept(MediaType.APPLICATION_XML)
                .post(ClientResponse.class, new GenericEntity<List<TransactionIO>>(transactions) {
                });
        Assert.assertEquals(ClientResponse.Status.OK, response.getClientResponseStatus());
    }
}
