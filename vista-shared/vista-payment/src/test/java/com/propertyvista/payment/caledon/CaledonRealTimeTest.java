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
import com.propertyvista.payment.PaymentRequest;

import com.pyx4j.entity.shared.EntityFactory;

public class CaledonRealTimeTest extends TestCase {

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

    public void testSampleTransactions() {
        IPaymentProcessor proc = new CaledonPaymentProcessor();
        proc.realTimeSale(createRequest("5555555555554444", "2015-01", 10.0));
    }
}
