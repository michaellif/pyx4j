/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-20
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;

import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.payment.caledon.CaledonPaymentProcessor;

public class CreditCardFacadeImpl implements CreditCardFacade {

    @Override
    public void persistToken(String merchantTerminalId, CreditCardInfo cc) {
        CreditCardProcessor.persistToken(merchantTerminalId, cc);
    }

    @Override
    public String getTransactionreferenceNumber(ReferenceNumberPrefix uniquePrefix, String referenceNumber) {
        return uniquePrefix.getValue() + referenceNumber;
    }

    @Override
    public CreditCardTransactionResponse realTimeSale(BigDecimal amount, String merchantTerminalId, ReferenceNumberPrefix uniquePrefix, String referenceNumber,
            CreditCardInfo cc) {
        return CreditCardProcessor.realTimeSale(amount, merchantTerminalId, uniquePrefix.getValue() + referenceNumber, cc);
    }

    @Override
    public String preAuthorization(BigDecimal amount, String merchantTerminalId, ReferenceNumberPrefix uniquePrefix, String referenceNumber, CreditCardInfo cc) {
        return CreditCardProcessor.preAuthorization(amount, merchantTerminalId, uniquePrefix.getValue() + referenceNumber, cc);
    }

    @Override
    public void preAuthorizationReversal(String merchantTerminalId, ReferenceNumberPrefix uniquePrefix, String referenceNumber, CreditCardInfo cc) {
        CreditCardProcessor.preAuthorizationReversal(merchantTerminalId, uniquePrefix.getValue() + referenceNumber, cc);
    }

    @Override
    public String completion(BigDecimal amount, String merchantTerminalId, ReferenceNumberPrefix uniquePrefix, String referenceNumber, CreditCardInfo cc) {
        return CreditCardProcessor.completion(amount, merchantTerminalId, uniquePrefix.getValue() + referenceNumber, cc);
    }

    @Override
    public boolean isNetworkError(String responseCode) {
        return new CaledonPaymentProcessor().isNetworkError(responseCode);
    }

    @Override
    public boolean validateVisaDebit(CreditCardInfo creditCardInfo) {
        return CreditCardProcessor.validateVisaDebit(creditCardInfo);
    }
}
