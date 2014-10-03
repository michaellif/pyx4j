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
package com.propertyvista.eft.caledoncards;

import java.math.BigDecimal;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.unit.shared.UniqueInteger;

import com.propertyvista.biz.system.eft.PaymentProcessingException;
import com.propertyvista.operations.domain.eft.cards.to.CreditCardPaymentInstrument;
import com.propertyvista.operations.domain.eft.cards.to.Merchant;
import com.propertyvista.operations.domain.eft.cards.to.PaymentRequest;
import com.propertyvista.operations.domain.eft.cards.to.PaymentResponse;

public class CaledonRealTimeTest extends CaledonTestBase {

    static PaymentRequest createRequest(String creditCardNumber, String exp, double amount) {
        return createRequest(creditCardNumber, exp, null, amount);
    }

    static PaymentRequest createRequest(String creditCardNumber, String exp, String securityCode, double amount) {
        PaymentRequest request = EntityFactory.create(PaymentRequest.class);
        request.referenceNumber().setValue("Test" + UniqueInteger.getInstance("cardTransaction"));
        request.amount().setValue(new BigDecimal(amount));
        CreditCardPaymentInstrument ccInfo = createCCInformation(creditCardNumber, exp);
        request.paymentInstrument().set(ccInfo);
        return request;
    }

    static PaymentResponse assertRealTimeSale(Merchant merchant, PaymentRequest request, String responseCode) {
        PaymentResponse response = new CaledonPaymentProcessor().realTimeSale(merchant, request);
        assertEquals(responseCode, response.code().getValue());
        return response;
    }

    public void testSampleTransactions() {
        assertRealTimeSale(testMerchant, createRequest(TestData.CARD_MC1, "2018-01", 10.0), "1285");
        assertRealTimeSale(testMerchant, createRequest(TestData.CARD_MC1, "2020-09", 10.0), "0000");
        assertRealTimeSale(testMerchant, createRequest(TestData.CARD_MC1, "2020-09", "234", 10.0), "0000");
        assertRealTimeSale(testMerchantError, createRequest(TestData.CARD_MC1, "2020-09", 10.0), "1001");
    }

    public void testVoidTransaction() {
        PaymentRequest request1 = createRequest(TestData.CARD_MC1, "2020-09", 25.0);
        assertRealTimeSale(testMerchant, request1, "0000");

        PaymentRequest request2 = createRequest(TestData.CARD_MC1, "2020-09", 25.0);
        request2.referenceNumber().setValue(request1.referenceNumber().getValue());
        PaymentResponse response2 = new CaledonPaymentProcessor().voidTransaction(testMerchant, request2);
        assertEquals("0000", response2.code().getValue());
    }

    public void testReturnTransaction() {
        PaymentRequest request1 = createRequest(TestData.CARD_MC1, "2020-09", 21.0);
        assertRealTimeSale(testMerchant, request1, "0000");

        // Test system not validating transaction numbers :(
        PaymentRequest request2 = createRequest(TestData.CARD_MC1, "2020-09", 22.0);
        request2.referenceNumber().setValue(request1.referenceNumber().getValue());
        assertRealTimeSale(testMerchant, request2, "0000");

        PaymentRequest request3 = createRequest(TestData.CARD_MC1, "2020-09", 23.0);
        PaymentResponse response2 = new CaledonPaymentProcessor().returnTransaction(testMerchant, request3);
        assertEquals("0000", response2.code().getValue());
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
