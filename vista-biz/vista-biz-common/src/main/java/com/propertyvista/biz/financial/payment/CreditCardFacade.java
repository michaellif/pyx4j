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

public interface CreditCardFacade {

    /**
     * the reference number MUST be unique for each type of transaction we make from vista.
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

    public void persistToken(String merchantTerminalId, CreditCardInfo cc);

    public CreditCardTransactionResponse realTimeSale(BigDecimal amount, String merchantTerminalId, ReferenceNumberPrefix uniquePrefix, String referenceNumber,
            CreditCardInfo cc);

    /**
     * @return authorizationNumber
     */
    public String authorization(BigDecimal amount, String merchantTerminalId, ReferenceNumberPrefix uniquePrefix, String referenceNumber, CreditCardInfo cc);

    public void authorizationReversal(String merchantTerminalId, ReferenceNumberPrefix uniquePrefix, String referenceNumber, CreditCardInfo cc);

    public String completion(BigDecimal amount, String merchantTerminalId, ReferenceNumberPrefix uniquePrefix, String referenceNumber, CreditCardInfo cc);

}
