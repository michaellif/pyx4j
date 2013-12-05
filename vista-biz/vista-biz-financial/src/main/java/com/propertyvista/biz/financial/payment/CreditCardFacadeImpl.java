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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.dto.payment.ConvenienceFeeCalculationResponseTO;

public class CreditCardFacadeImpl implements CreditCardFacade {

    @Override
    public void persistToken(String merchantTerminalId, CreditCardInfo cc) {
        CreditCardProcessor.persistToken(merchantTerminalId, cc);
    }

    //TODO temporary caledon testing Hack
    @Deprecated
    final boolean caldeonHack = true;

    @Override
    public String getTransactionreferenceNumber(ReferenceNumberPrefix uniquePrefix, IPrimitive<Key> referenceNumber) {
        if (caldeonHack) {
            return referenceNumber.getStringView();
        }
        return uniquePrefix.getValue() + PadTransactionUtils.toCaldeonTransactionId(referenceNumber);
    }

    @Override
    public Key getVistaRecordId(ReferenceNumberPrefix uniquePrefix, String transactionreferenceNumber) {
        if (caldeonHack) {
            return new Key(transactionreferenceNumber);
        }
        return PadTransactionUtils.toVistaPaymentRecordId(transactionreferenceNumber.substring(uniquePrefix.getValue().length()));
    }

    @Override
    public CreditCardTransactionResponse realTimeSale(String merchantTerminalId, //
            BigDecimal amount, BigDecimal convenienceFee,//
            ReferenceNumberPrefix uniquePrefix, IPrimitive<Key> referenceNumber, String convenienceFeeReferenceNumber, //
            CreditCardInfo cc) {
        return CreditCardProcessor.realTimeSale(merchantTerminalId, amount, convenienceFee, getTransactionreferenceNumber(uniquePrefix, referenceNumber),
                convenienceFeeReferenceNumber, cc);
    }

    @Override
    public CreditCardTransactionResponse voidTransaction(String merchantTerminalId,//
            BigDecimal amount, BigDecimal convenienceFee, //
            ReferenceNumberPrefix uniquePrefix, IPrimitive<Key> referenceNumber, String convenienceFeeReferenceNumber) {
        return CreditCardProcessor.voidTransaction(merchantTerminalId, amount, convenienceFee, getTransactionreferenceNumber(uniquePrefix, referenceNumber),
                convenienceFeeReferenceNumber);
    }

    @Override
    public String preAuthorization(String merchantTerminalId, BigDecimal amount, ReferenceNumberPrefix uniquePrefix, IPrimitive<Key> referenceNumber,
            CreditCardInfo cc) {
        return CreditCardProcessor.preAuthorization(merchantTerminalId, amount, getTransactionreferenceNumber(uniquePrefix, referenceNumber), cc);
    }

    @Override
    public void preAuthorizationReversal(String merchantTerminalId, ReferenceNumberPrefix uniquePrefix, IPrimitive<Key> referenceNumber, CreditCardInfo cc) {
        CreditCardProcessor.preAuthorizationReversal(merchantTerminalId, getTransactionreferenceNumber(uniquePrefix, referenceNumber), cc);
    }

    @Override
    public String completion(String merchantTerminalId, BigDecimal amount, ReferenceNumberPrefix uniquePrefix, IPrimitive<Key> referenceNumber,
            CreditCardInfo cc) {
        return CreditCardProcessor.completion(merchantTerminalId, amount, getTransactionreferenceNumber(uniquePrefix, referenceNumber), cc);
    }

    @Override
    public boolean isNetworkError(String responseCode) {
        return CreditCardProcessor.getPaymentProcessor().isNetworkError(responseCode);
    }

    @Override
    public boolean validateVisaDebit(CreditCardInfo creditCardInfo) {
        return CreditCardProcessor.validateVisaDebit(creditCardInfo);
    }

    @Override
    public ConvenienceFeeCalculationResponseTO getConvenienceFee(String merchantTerminalId, ReferenceNumberPrefix uniquePrefix, CreditCardType cardType,
            BigDecimal amount) {
        return CreditCardProcessor.getConvenienceFee(merchantTerminalId, uniquePrefix, cardType, amount);
    }

}
