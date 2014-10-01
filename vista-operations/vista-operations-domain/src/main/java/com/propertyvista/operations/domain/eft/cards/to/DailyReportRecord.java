/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 15, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.eft.cards.to;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.essentials.rpc.ImportColumn;
import com.pyx4j.i18n.annotations.I18n;

/**
 * Caledon interface CSV record structure for CardsClearanceRecord
 */
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@Transient
public interface DailyReportRecord extends IEntity {

    public enum DailyReportRecordType {

        /**
         * the transactions will be included in the daily reconciliation reports based on the time of the "SALE" or "PRCO" transaction.
         *
         * "PRCO" transactions are only used for convenience fee transactions.
         */
        PREA, PRCO, BALR, SETT, SALE, AUTH, VOID, POST, RETU, MRVD;

        /**
         * "SALE" - Sale transaction
         *
         * "POST" - Force Post is a transaction which has not been authorized online; this transaction type is not expected to be included in Property Vista
         * processing
         *
         * "RETU" - Return/refund transaction
         *
         * "PREA" - Pre-Authorization transaction
         *
         * "VOID" - Void of Sale transaction
         *
         * "MRVD" - Void of Return transaction
         *
         * "PRCO" - Completion transaction
         *
         * "AUTH" - Authorize Only; this transaction type is not expected to be included in Property Vista processing
         *
         * "BALR" - Balance Request
         *
         * "SETT" - Settlement
         */

        public static Collection<DailyReportRecordType> vistaProcessing() {
            return EnumSet.of(SALE, PRCO, RETU);
        }
    }

    public enum DailyReportCardType {

        VISA, MCRD, CREDIT;

    }

    @NotNull
    @ImportColumn
    IPrimitive<String> terminalID();

    IPrimitive<String> oper();

    @NotNull
    @ImportColumn(format = "dd-MMM-yyyy HH:mm:ss")
    @Caption(name = "DATE/TIME")
    @Format("dd-MMM-yyyy HH:mm:ss")
    IPrimitive<Date> date();

    @NotNull
    @Caption(name = "TRAN TYPE")
    IPrimitive<DailyReportRecordType> transactionType();

    @ImportColumn(names = "CARD TYPE")
    IPrimitive<DailyReportCardType> cardType();

    IPrimitive<String> cardNumber();

    IPrimitive<String> expiry();

    IPrimitive<String> referenceNumber();

    IPrimitive<BigDecimal> amount();

    IPrimitive<String> response();

    IPrimitive<String> authNumber();

    IPrimitive<String> pinpadId();

    @NotNull
    @Format(messageFormat = true, value = "{0,choice,0#N|1#Y}")
    IPrimitive<Boolean> voided();

    @NotNull
    @Format(messageFormat = true, value = "{0,choice,0#N|1#Y}")
    IPrimitive<Boolean> approved();

}
