/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 27, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.mock.cards;

import com.propertyvista.biz.system.eft.CreditCardPaymentProcessorFacade;
import com.propertyvista.operations.domain.eft.cards.to.CreditCardPaymentInstrument;
import com.propertyvista.operations.domain.eft.cards.to.FeeCalulationRequest;
import com.propertyvista.operations.domain.eft.cards.to.FeeCalulationResponse;
import com.propertyvista.operations.domain.eft.cards.to.Merchant;
import com.propertyvista.operations.domain.eft.cards.to.PaymentRequest;
import com.propertyvista.operations.domain.eft.cards.to.PaymentResponse;
import com.propertyvista.operations.domain.eft.cards.to.TokenPaymentInstrument;

public class CreditCardPaymentProcessorFacadeMock implements CreditCardPaymentProcessorFacade {

    public CreditCardPaymentProcessorFacadeMock() {
    }

    public static void init() {
        // Initialize listeners
        PCIMock.instance();
    }

    @Override
    public PaymentResponse realTimeSale(Merchant merchant, PaymentRequest request) {
        return PCIMock.instance().realTimeSale(merchant, request);
    }

    @Override
    public PaymentResponse realTimePreAuthorization(Merchant merchant, PaymentRequest request) {
        // TODO implement Mock
        throw new Error("Mock not implemented");
    }

    @Override
    public PaymentResponse realTimePreAuthorizationReversal(Merchant merchant, PaymentRequest request) {
        // TODO implement Mock
        throw new Error("Mock not implemented");
    }

    @Override
    public PaymentResponse realTimePreAuthorizationCompletion(Merchant merchant, PaymentRequest request) {
        // TODO implement Mock
        throw new Error("Mock not implemented");
    }

    @Override
    public PaymentResponse validateVisaDebit(CreditCardPaymentInstrument ccinfo) {
        // TODO Auto-generated method stub
        throw new Error("Mock not implemented");
    }

    @Override
    public FeeCalulationResponse getConvenienceFee(Merchant merchant, FeeCalulationRequest request) {
        return ConvenienceFeeMock.instance().getConvenienceFee(merchant, request);
    }

    @Override
    public PaymentResponse createToken(Merchant merchant, CreditCardPaymentInstrument ccinfo, TokenPaymentInstrument token) {
        return PCIMock.instance().createToken(ccinfo, token);
    }

    @Override
    public PaymentResponse updateToken(Merchant merchant, CreditCardPaymentInstrument ccinfo, TokenPaymentInstrument token) {
        // TODO implement Mock
        throw new Error("Mock not implemented");
    }

    @Override
    public PaymentResponse deactivateToken(Merchant merchant, TokenPaymentInstrument token) {
        // TODO implement Mock
        throw new Error("Mock not implemented");
    }

    @Override
    public PaymentResponse reactivateToken(Merchant merchant, TokenPaymentInstrument token) {
        // TODO implement Mock
        throw new Error("Mock not implemented");
    }

    @Override
    public PaymentResponse voidTransaction(Merchant merchant, PaymentRequest request) {
        return PCIMock.instance().voidTransaction(merchant, request);
    }

    @Override
    public boolean isNetworkError(String responseCode) {
        return false;
    }

}
