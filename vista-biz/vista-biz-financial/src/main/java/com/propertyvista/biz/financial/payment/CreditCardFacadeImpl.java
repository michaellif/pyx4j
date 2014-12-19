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
 */
package com.propertyvista.biz.financial.payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.Validate;
import com.pyx4j.entity.core.IPrimitive;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.payment.CreditCardInfo;
import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;
import com.propertyvista.dto.payment.ConvenienceFeeCalculationResponseTO;
import com.propertyvista.shared.rpc.CreditCardValidationResponce;

public class CreditCardFacadeImpl implements CreditCardFacade {

    @Override
    public void persistToken(String merchantTerminalId, CreditCardInfo cc) {
        CreditCardProcessor.persistToken(merchantTerminalId, cc);
    }

    @Override
    public String getTransactionreferenceNumber(ReferenceNumberPrefix uniquePrefix, IPrimitive<Key> referenceNumber) {
        return uniquePrefix.getValue() + PadTransactionUtils.toCaldeonTransactionId(referenceNumber, VistaDeployment.isVistaProduction());
    }

    @Override
    public List<String> getProdAndTestTransactionreferenceNumbers(ReferenceNumberPrefix uniquePrefix, IPrimitive<Key> referenceNumber) {
        List<String> numbers = new ArrayList<>();
        if (VistaDeployment.isVistaProduction()) {
            numbers.add(getTransactionreferenceNumber(uniquePrefix, referenceNumber));
        } else {
            numbers.add(uniquePrefix.getValue() + PadTransactionUtils.toCaldeonTransactionId(referenceNumber, true));
            numbers.add(getTransactionreferenceNumber(uniquePrefix, referenceNumber));
        }
        return numbers;
    }

    @Override
    public boolean isVistaRecordId(String transactionreferenceNumber) {
        for (ReferenceNumberPrefix uniquePrefix : EnumSet.allOf(ReferenceNumberPrefix.class)) {
            if (transactionreferenceNumber.startsWith(uniquePrefix.getValue())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Key getVistaRecordId(ReferenceNumberPrefix uniquePrefix, String transactionreferenceNumber) {
        Validate.isTrue(transactionreferenceNumber.startsWith(uniquePrefix.getValue()), "Transaction Id {0} Has Unexpected Prefix, Expected {1}", uniquePrefix,
                transactionreferenceNumber);
        return PadTransactionUtils.toVistaPaymentRecordId(transactionreferenceNumber.substring(uniquePrefix.getValue().length()), true);
    }

    @Override
    public Key getProdAndTestVistaRecordId(ReferenceNumberPrefix uniquePrefix, String transactionreferenceNumber) {
        Validate.isTrue(transactionreferenceNumber.startsWith(uniquePrefix.getValue()), "Transaction Id {0} Has Unexpected Prefix, Expected {1}",
                transactionreferenceNumber, uniquePrefix.getValue());
        return PadTransactionUtils.toVistaPaymentRecordId(transactionreferenceNumber.substring(uniquePrefix.getValue().length()), false);
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
    public CreditCardTransactionResponse preAuthorization(String merchantTerminalId, BigDecimal amount, ReferenceNumberPrefix uniquePrefix,
            IPrimitive<Key> referenceNumber, CreditCardInfo cc) {
        return CreditCardProcessor.preAuthorization(merchantTerminalId, amount, getTransactionreferenceNumber(uniquePrefix, referenceNumber), cc);
    }

    @Override
    @Deprecated
    public String preAuthorization2(String merchantTerminalId, BigDecimal amount, ReferenceNumberPrefix uniquePrefix, IPrimitive<Key> referenceNumber,
            CreditCardInfo cc) {
        return CreditCardProcessor.preAuthorization2(merchantTerminalId, amount, getTransactionreferenceNumber(uniquePrefix, referenceNumber), cc);
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
    public CreditCardValidationResponce validateCard(CreditCardInfo creditCardInfo) {
        return CreditCardProcessor.validateCard(creditCardInfo);
    }

    @Override
    public boolean validateCreditCard(CreditCardInfo creditCardInfo) {
        return CreditCardProcessor.validateCreditCard(creditCardInfo, true);
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
