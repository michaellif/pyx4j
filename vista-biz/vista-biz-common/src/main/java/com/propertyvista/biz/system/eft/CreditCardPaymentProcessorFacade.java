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
package com.propertyvista.biz.system.eft;

import com.propertyvista.operations.domain.eft.cards.to.CreditCardPaymentInstrument;
import com.propertyvista.operations.domain.eft.cards.to.FeeCalulationRequest;
import com.propertyvista.operations.domain.eft.cards.to.FeeCalulationResponse;
import com.propertyvista.operations.domain.eft.cards.to.Merchant;
import com.propertyvista.operations.domain.eft.cards.to.PaymentRequest;
import com.propertyvista.operations.domain.eft.cards.to.PaymentResponse;
import com.propertyvista.operations.domain.eft.cards.to.TokenPaymentInstrument;

/**
 * Actual real time credit cards transactions interface to Bank
 */
public interface CreditCardPaymentProcessorFacade {

    PaymentResponse realTimeSale(Merchant merchant, PaymentRequest request);

    PaymentResponse realTimePreAuthorization(Merchant merchant, PaymentRequest request);

    PaymentResponse realTimePreAuthorizationReversal(Merchant merchant, PaymentRequest request);

    PaymentResponse realTimePreAuthorizationCompletion(Merchant merchant, PaymentRequest request);

    PaymentResponse validateVisaDebit(CreditCardPaymentInstrument ccinfo);

    FeeCalulationResponse getConvenienceFee(Merchant merchant, FeeCalulationRequest request);

    PaymentResponse createToken(Merchant merchant, CreditCardPaymentInstrument ccinfo, TokenPaymentInstrument token);

    PaymentResponse updateToken(Merchant merchant, CreditCardPaymentInstrument ccinfo, TokenPaymentInstrument token);

    PaymentResponse deactivateToken(Merchant merchant, TokenPaymentInstrument token);

    PaymentResponse reactivateToken(Merchant merchant, TokenPaymentInstrument token);

    PaymentResponse voidTransaction(Merchant merchant, PaymentRequest request);

    boolean isNetworkError(String responseCode);
}
