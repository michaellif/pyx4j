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

public interface CreditCardFacade {

    /**
     * The reference number MUST be unique for each type of transaction we make from vista.
     * 
     * the value should also be letters [A-Z]
     */
    public enum ReferenceNumberPrefix {

        TenantSure("TS"),

        EquifaxScreening("EFX"),

        RentPayments("R");

        private final String value;

        private ReferenceNumberPrefix(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public boolean isNetworkError(String responseCode);

    public String getTransactionreferenceNumber(ReferenceNumberPrefix uniquePrefix, IPrimitive<Key> referenceNumber);

    public Key getVistaRecordId(ReferenceNumberPrefix uniquePrefix, String transactionreferenceNumber);

    public void persistToken(String merchantTerminalId, CreditCardInfo cc);

    public CreditCardTransactionResponse realTimeSale(String merchantTerminalId, BigDecimal amount, BigDecimal convenienceFee,
            ReferenceNumberPrefix uniquePrefix, IPrimitive<Key> referenceNumber, String convenienceFeeReferenceNumber, CreditCardInfo cc);

    public CreditCardTransactionResponse voidTransaction(String merchantTerminalId, BigDecimal amount, ReferenceNumberPrefix uniquePrefix,
            IPrimitive<Key> referenceNumber);

    /**
     * @return authorizationNumber
     */
    public String preAuthorization(String merchantTerminalId, BigDecimal amount, ReferenceNumberPrefix uniquePrefix, IPrimitive<Key> referenceNumber,
            CreditCardInfo cc);

    public void preAuthorizationReversal(String merchantTerminalId, ReferenceNumberPrefix uniquePrefix, IPrimitive<Key> referenceNumber, CreditCardInfo cc);

    public String completion(String merchantTerminalId, BigDecimal amount, ReferenceNumberPrefix uniquePrefix, IPrimitive<Key> referenceNumber,
            CreditCardInfo cc);

    public boolean validateVisaDebit(CreditCardInfo creditCardInfo);

    public ConvenienceFeeCalculationResponseTO getConvenienceFee(String merchantTerminalId, ReferenceNumberPrefix uniquePrefix, CreditCardType cardType,
            BigDecimal amount);

}
