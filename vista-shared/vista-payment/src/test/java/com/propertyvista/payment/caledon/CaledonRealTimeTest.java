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

import java.math.BigDecimal;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.payment.CCInformation;
import com.propertyvista.payment.CreditCardPaymentProcessorFacade;
import com.propertyvista.payment.Merchant;
import com.propertyvista.payment.PaymentProcessingException;
import com.propertyvista.payment.PaymentRequest;
import com.propertyvista.payment.PaymentResponse;

public class CaledonRealTimeTest extends CaledonTestBase {

    static PaymentRequest createRequest(String creditCardNumber, String exp, double amount) {
        return createRequest(creditCardNumber, exp, null, amount);
    }

    static PaymentRequest createRequest(String creditCardNumber, String exp, String securityCode, double amount) {
        PaymentRequest request = EntityFactory.create(PaymentRequest.class);
        request.referenceNumber().setValue("Test1");
        request.amount().setValue(new BigDecimal(amount));
        CCInformation ccInfo = createCCInformation(creditCardNumber, exp);
        request.paymentInstrument().set(ccInfo);
        return request;
    }

    static PaymentResponse assertRealTimeSale(Merchant merchant, PaymentRequest request, String responseCode) {
        CreditCardPaymentProcessorFacade proc = new CaledonPaymentProcessor();
        PaymentResponse response = proc.realTimeSale(merchant, request);
        assertEquals(responseCode, response.code().getValue());
        return response;
    }

    public void testSampleTransactions() {
        PaymentRequest request = createRequest(TestData.CARD_MC1, "2015-01", 10.0);
        //System.out.print("CCNUMBER=" + ((CCInformation) (request.paymentInstrument().getValue())).creditCardNumber().getValue());
        assertRealTimeSale(testMerchant, createRequest(TestData.CARD_MC1, "2015-01", 10.0), "1285");
        assertRealTimeSale(testMerchant, createRequest(TestData.CARD_MC1, "2017-09", 10.0), "0000");
        assertRealTimeSale(testMerchant, createRequest(TestData.CARD_MC1, "2017-09", "234", 10.0), "0000");
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
