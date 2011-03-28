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

import com.propertyvista.payment.IPaymentProcessor;
import com.propertyvista.payment.PaymentRequest;
import com.propertyvista.payment.PaymentResponse;
import com.propertyvista.payment.Token;

import com.pyx4j.entity.shared.EntityFactory;

public class CaledonTokenTest extends CaledonTestCase {

    public void testNothing() {

    }

    public void OOO_testCreateToken() {
        IPaymentProcessor proc = new CaledonPaymentProcessor();
        Token token = EntityFactory.create(Token.class);
        token.code().setValue("" + System.currentTimeMillis());
        PaymentResponse pr = proc.createToken(testMerchant, super.createCCInformation(TestData.CARD_MC1, "2015-01"), token);
        assertEquals(CaledonTokenResponse.TOKEN_SUCCESS.getValue(), pr.code().getValue());
    }

    public void testTokenTransaction() {
        IPaymentProcessor proc = new CaledonPaymentProcessor();
        Token token = EntityFactory.create(Token.class);
        token.code().setValue("" + System.currentTimeMillis());

        System.out.println("Token value=" + token.code().getValue());
        PaymentResponse pr = proc.createToken(testMerchant, super.createCCInformation(TestData.CARD_MC1, "2015-01"), token);

        System.out.println(pr.code().getValue());

        assertEquals(CaledonTokenResponse.TOKEN_SUCCESS.getValue(), pr.code().getValue());

        PaymentRequest request = EntityFactory.create(PaymentRequest.class);
        request.paymentInstrument().setValue(token);

        PaymentResponse pr1 = proc.realTimeSale(testMerchant, request);

        System.out.println(pr1.code().getValue());

    }
}
