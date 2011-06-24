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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.payment.IPaymentProcessor;
import com.propertyvista.payment.PaymentRequest;
import com.propertyvista.payment.PaymentResponse;
import com.propertyvista.payment.Token;

public class CaledonTokenTest extends CaledonTestBase {

    private final static Logger log = LoggerFactory.getLogger(CaledonTokenTest.class);

    public void testNothing() {

    }

    public void testCreateToken() {
        IPaymentProcessor proc = new CaledonPaymentProcessor();
        Token token = EntityFactory.create(Token.class);
        token.code().setValue(String.valueOf(System.currentTimeMillis()));
        PaymentResponse pr = proc.createToken(testMerchant, super.createCCInformation(TestData.CARD_MC1, "2015-01"), token);
        log.debug("responce code {}", pr.code().getValue());
        assertEquals(CaledonTokenResponse.TOKEN_SUCCESS.getValue(), pr.code().getValue());

    }

    public void testTokenTransaction() {
        IPaymentProcessor proc = new CaledonPaymentProcessor();
        Token token = EntityFactory.create(Token.class);
        token.code().setValue(String.valueOf(System.currentTimeMillis()));

        log.debug("Token value", token.code().getValue());
        PaymentResponse pr = proc.createToken(testMerchant, super.createCCInformation(TestData.CARD_MC1, "2015-01"), token);

        log.debug("responce code {}", pr.code().getValue());

        assertEquals(CaledonTokenResponse.TOKEN_SUCCESS.getValue(), pr.code().getValue());

        PaymentRequest request = EntityFactory.create(PaymentRequest.class);
        request.paymentInstrument().setValue(token);
        PaymentResponse pr1 = proc.realTimeSale(testMerchant, request);

        log.debug("responce code {}", pr1.code().getValue());
        assertEquals(CaledonTokenResponse.TOKEN_SUCCESS.getValue(), pr1.code().getValue());

    }
}
