/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-13
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment;

/**
 * Actual real time credit cards transactions interface to Bank
 */
//TODO Move to vista-biz-common com.propertyvista.payment.cards
public interface CreditCardPaymentProcessorFacade {

    PaymentResponse realTimeSale(Merchant merchant, PaymentRequest request);

    PaymentResponse realTimePreAuthorization(Merchant merchant, PaymentRequest request);

    PaymentResponse realTimePreAuthorizationReversal(Merchant merchant, PaymentRequest request);

    PaymentResponse realTimePreAuthorizationCompletion(Merchant merchant, PaymentRequest request);

    PaymentResponse validateVisaDebit(CCInformation ccinfo);

    FeeCalulationResponse getConvenienceFee(Merchant merchant, FeeCalulationRequest request);

    PaymentResponse createToken(Merchant merchant, CCInformation ccinfo, Token token);

    PaymentResponse updateToken(Merchant merchant, CCInformation ccinfo, Token token);

    PaymentResponse deactivateToken(Merchant merchant, Token token);

    PaymentResponse reactivateToken(Merchant merchant, Token token);

    PaymentResponse voidTransaction(Merchant merchant, PaymentRequest request);

    boolean isNetworkError(String responseCode);
}
