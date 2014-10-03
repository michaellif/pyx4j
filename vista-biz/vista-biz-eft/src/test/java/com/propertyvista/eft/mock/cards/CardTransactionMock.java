/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 29, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.mock.cards;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;

class CardTransactionMock {

    public enum TransactionStatus {

        PreAuthorization,

        Compleated,

        Voided,

        Return;

    }

    String referenceNumber;

    LogicalDate date;

    String terminalID;

    BigDecimal amount;

    TransactionStatus status;

    String authorizationNumber;

    boolean clearenceSent;

    boolean reconciliationSent;

    CardTransactionMock() {
        date = SystemDateManager.getLogicalDate();
    }

    @Override
    public String toString() {
        return "CardTransactionMock " + status + "; amount=" + amount;
    }
}
