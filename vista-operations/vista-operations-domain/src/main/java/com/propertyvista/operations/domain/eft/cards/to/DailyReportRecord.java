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
import java.util.Date;

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

        PREA, PRCO, BALR, SETT, SALE, AUTH, VOID;
    }

    public enum DailyReportCardType {

        VISA, MCRD, CREDIT;

    }

    @NotNull
    @ImportColumn
    IPrimitive<String> terminalID();

    IPrimitive<String> oper();

    @NotNull
    @ImportColumn(names = "DATE/TIME", format = "dd-MMM-yyyy HH:mm:ss")
    IPrimitive<Date> date();

    @ImportColumn(names = "TRAN TYPE")
    @NotNull
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
    IPrimitive<Boolean> voided();

    @NotNull
    IPrimitive<Boolean> approved();

}
