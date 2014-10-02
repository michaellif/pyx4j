/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 11, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.eft.cards;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.PmcMerchantAccountIndex;
import com.propertyvista.operations.domain.eft.cards.to.DailyReportRecord.DailyReportCardType;

@Table(namespace = VistaNamespace.operationsNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface CardsClearanceRecord extends IEntity {

    public enum CardsClearanceRecordType {

        Completion,

        Sale,

        Return,

    }

    @Owner
    @JoinColumn
    @Indexed
    @MemberColumn(notNull = true)
    CardsClearanceFile clearanceFile();

    //ID Copied from the file not used in Vista
    IPrimitive<String> merchantID();

    // found based on merchantTerminalId
    @Indexed
    PmcMerchantAccountIndex merchantAccount();

    IPrimitive<CardsClearanceRecordProcessingStatus> status();

    IPrimitive<Boolean> convenienceFeeAccount();

    IPrimitive<DailyReportCardType> cardType();

    @MemberColumn(notNull = true)
    IPrimitive<CardsClearanceRecordType> transactionType();

    IPrimitive<String> referenceNumber();

    IPrimitive<String> responseMessage();

    IPrimitive<String> transactionAuthorizationNumber();

    @NotNull
    IPrimitive<Boolean> voided();

    @NotNull
    IPrimitive<Boolean> approved();

    @Editor(type = EditorType.label)
    @Format("yyyy-MM-dd HH:mm:ss")
    IPrimitive<Date> clearanceDate();

    IPrimitive<BigDecimal> amount();

    @Timestamp(Timestamp.Update.Created)
    @Editor(type = EditorType.label)
    @Format("yyyy-MM-dd HH:mm:ss")
    IPrimitive<Date> recordReceivedDate();

}
