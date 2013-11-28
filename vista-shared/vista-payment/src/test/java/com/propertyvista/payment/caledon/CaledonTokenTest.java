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

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.payment.CreditCardPaymentProcessorFacade;
import com.propertyvista.payment.PaymentRequest;
import com.propertyvista.payment.PaymentResponse;
import com.propertyvista.payment.Token;

public class CaledonTokenTest extends CaledonTestBase {

    private final static Logger log = LoggerFactory.getLogger(CaledonTokenTest.class);

    public void testCreateToken() {
        CreditCardPaymentProcessorFacade proc = new CaledonPaymentProcessor();
        Token token = EntityFactory.create(Token.class);
        token.code().setValue(String.valueOf(System.currentTimeMillis()));
        PaymentResponse pr = proc.createToken(testMerchant, super.createCCInformation(TestData.CARD_MC1, "2015-01"), token);
        log.debug("responce code {}", pr.code().getValue());
        assertEquals(CaledonTokenResponse.TOKEN_SUCCESS.getValue(), pr.code().getValue());

        proc.deactivateToken(testMerchant, token);
    }

    public void testCreateTokenWithSecurityCode() {
        CreditCardPaymentProcessorFacade proc = new CaledonPaymentProcessor();
        Token token = EntityFactory.create(Token.class);
        token.code().setValue(String.valueOf(System.currentTimeMillis()));
        PaymentResponse pr = proc.createToken(testMerchant, super.createCCInformation(TestData.CARD_MC1, "2015-01", "1234"), token);
        log.debug("responce code {}", pr.code().getValue());
        assertEquals(CaledonTokenResponse.TOKEN_SUCCESS.getValue(), pr.code().getValue());

        proc.deactivateToken(testMerchant, token);
    }

    public void testTokenTransaction() {
        CreditCardPaymentProcessorFacade proc = new CaledonPaymentProcessor();
        Token token = EntityFactory.create(Token.class);
        token.code().setValue(String.valueOf(System.currentTimeMillis()));

        log.debug("Token value", token.code().getValue());
        PaymentResponse pr = proc.createToken(testMerchant, super.createCCInformation(TestData.CARD_MC1, "2017-09"), token);

        log.debug("responce code {}", pr.code().getValue());

        assertEquals(CaledonTokenResponse.TOKEN_SUCCESS.getValue(), pr.code().getValue());

        PaymentRequest request = EntityFactory.create(PaymentRequest.class);
        request.paymentInstrument().set(token);
        request.amount().setValue(new BigDecimal(10f));
        request.referenceNumber().setValue("Test212");
        try {
            PaymentResponse pr1 = proc.realTimeSale(testMerchant, request);

            log.debug("responce code {}", pr1.code().getValue());
            assertEquals(CaledonTokenResponse.TOKEN_SUCCESS.getValue(), pr1.code().getValue());
        } finally {
            proc.deactivateToken(testMerchant, token);
        }
    }
}
