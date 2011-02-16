/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-14
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.caledon;

import java.text.SimpleDateFormat;

import junit.framework.TestCase;

import com.propertyvista.payment.IPaymentProcessor;
import com.propertyvista.payment.Merchant;
import com.propertyvista.payment.PaymentProcessingException;
import com.propertyvista.payment.PaymentRequest;
import com.propertyvista.payment.PaymentResponse;

import com.pyx4j.entity.shared.EntityFactory;

public class CaledonRealTimeTest extends TestCase {

    private static Merchant testMerchant = createTestCaledonMerchant(TestData.TEST_TERMID);

    private static Merchant testMerchantError = createTestCaledonMerchant(TestData.TEST_TERMID_ERROR);

    private static PaymentRequest createRequest(String creditCardNumber, String exp, double amount) {
        PaymentRequest request = EntityFactory.create(PaymentRequest.class);

        request.referenceNumber().setValue("Test1");
        request.amount().setValue((float) amount);
        request.creditCardNumber().setValue(creditCardNumber);

        try {
            request.creditCardExpiryDate().setValue(new SimpleDateFormat("yyyy-MM").parse(exp));
        } catch (Throwable e) {
            throw new Error("Invalid data");
        }

        return request;
    }

    private static Merchant createTestCaledonMerchant(String terminalID) {
        Merchant m = EntityFactory.create(Merchant.class);
        m.terminalID().setValue(terminalID);
        return m;
    }

    private static PaymentResponse assertRealTimeSale(Merchant merchant, PaymentRequest request, String responseCode) {
        IPaymentProcessor proc = new CaledonPaymentProcessor();
        PaymentResponse response = proc.realTimeSale(merchant, request);
        assertEquals(responseCode, response.code().getValue());
        return response;
    }

    public void testSampleTransactions() {
        assertRealTimeSale(testMerchant, createRequest(TestData.CARD_MC1, "2015-01", 10.0), "1285");
        assertRealTimeSale(testMerchant, createRequest(TestData.CARD_MC1, "2017-09", 10.0), "0000");
        assertRealTimeSale(testMerchantError, createRequest(TestData.CARD_MC1, "2017-09", 10.0), "1001");
    }

    public void testServerDropConnection() {
        try {
            new CaledonPaymentProcessor().realTimeSale(testMerchant, createRequest(TestData.CARD_MC1, "2009-09", 10.0));
            fail("no return code expected");
        } catch (PaymentProcessingException e) {
            // OK
        }
    }
}
