/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 27, 2011
 * @author kostya
 * @version $Id$
 */
package com.propertyvista.payment.caledon;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.payment.IPaymentProcessor;
import com.propertyvista.payment.PaymentRequest;
import com.propertyvista.payment.PaymentResponse;
import com.propertyvista.payment.Token;

public class CaledonTokenTest extends CaledonTestCase {

    public void testNothing() {

    }

    public void WORK_IN_PROGRESS_testCreateToken() {
        IPaymentProcessor proc = new CaledonPaymentProcessor();
        Token token = EntityFactory.create(Token.class);
        token.code().setValue("" + System.currentTimeMillis());
        PaymentResponse pr = proc.createToken(testMerchant, super.createCCInformation(TestData.CARD_MC1, "2015-01"), token);
        String rCode = pr.code().getValue();
        //System.out.println("rCode=" + rCode);
        assertEquals(CaledonTokenResponse.TOKEN_SUCCESS.getValue(), rCode);

    }

    public void WORK_IN_PROGRESS_testTokenTransaction() {
        IPaymentProcessor proc = new CaledonPaymentProcessor();
        Token token = EntityFactory.create(Token.class);
        token.code().setValue("" + System.currentTimeMillis());

        //System.out.println("Token value=" + token.code().getValue());
        PaymentResponse pr = proc.createToken(testMerchant, super.createCCInformation(TestData.CARD_MC1, "2015-01"), token);

        //System.out.println(pr.code().getValue());

        assertEquals(CaledonTokenResponse.TOKEN_SUCCESS.getValue(), pr.code().getValue());

        PaymentRequest request = EntityFactory.create(PaymentRequest.class);
        request.paymentInstrument().setValue(token);
        PaymentResponse pr1 = proc.realTimeSale(testMerchant, request);

        //System.out.println(pr1.code().getValue());
        assertEquals(CaledonTokenResponse.TOKEN_SUCCESS.getValue(), pr1.code().getValue());

    }
}
