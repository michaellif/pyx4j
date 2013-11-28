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
package com.propertyvista.payment.cards;

import static com.propertyvista.payment.cards.PaymentResponseHelper.createResponse;

import com.propertyvista.payment.CCInformation;
import com.propertyvista.payment.CreditCardPaymentProcessorFacade;
import com.propertyvista.payment.FeeCalulationRequest;
import com.propertyvista.payment.FeeCalulationResponse;
import com.propertyvista.payment.Merchant;
import com.propertyvista.payment.PaymentRequest;
import com.propertyvista.payment.PaymentResponse;
import com.propertyvista.payment.Token;

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
        return createResponse("1000", "Mock not implemented");
    }

    @Override
    public PaymentResponse realTimePreAuthorizationReversal(Merchant merchant, PaymentRequest request) {
        // TODO implement Mock
        return createResponse("1000", "Mock not implemented");
    }

    @Override
    public PaymentResponse realTimePreAuthorizationCompletion(Merchant merchant, PaymentRequest request) {
        // TODO implement Mock
        return createResponse("1000", "Mock not implemented");
    }

    @Override
    public PaymentResponse validateVisaDebit(CCInformation ccinfo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public FeeCalulationResponse getConvenienceFee(Merchant merchant, FeeCalulationRequest request) {
        return ConvenienceFeeMock.instance().getConvenienceFee(merchant, request);
    }

    @Override
    public PaymentResponse createToken(Merchant merchant, CCInformation ccinfo, Token token) {
        return PCIMock.instance().createToken(ccinfo, token);
    }

    @Override
    public PaymentResponse updateToken(Merchant merchant, CCInformation ccinfo, Token token) {
        // TODO implement Mock
        return createResponse("1000", "Mock not implemented");
    }

    @Override
    public PaymentResponse deactivateToken(Merchant merchant, Token token) {
        // TODO implement Mock
        return createResponse("1000", "Mock not implemented");
    }

    @Override
    public PaymentResponse reactivateToken(Merchant merchant, Token token) {
        // TODO implement Mock
        return createResponse("1000", "Mock not implemented");
    }

    @Override
    public PaymentResponse voidTransaction(Merchant merchant, PaymentRequest request) {
        // TODO implement Mock
        return createResponse("1000", "Mock not implemented");
    }

    @Override
    public boolean isNetworkError(String responseCode) {
        return false;
    }

}
