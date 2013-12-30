/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-26
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.domain.eft.caledoneft;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.GwtBlacklist;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;

@Table(namespace = VistaNamespace.operationsNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@GwtBlacklist
public interface FundsReconciliationRecordRecord extends IEntity {

    @Owner
    @MemberColumn(notNull = true)
    @JoinColumn
    @Indexed
    FundsReconciliationSummary reconciliationSummary();

    IPrimitive<LogicalDate> paymentDate();

    IPrimitive<String> merchantTerminalId();

    @Length(29)
    IPrimitive<String> clientId();

    @Length(15)
    IPrimitive<String> transactionId();

    @Format("#0.00")
    @Editor(type = EditorType.moneylabel)
    IPrimitive<BigDecimal> amount();

    IPrimitive<TransactionReconciliationStatus> reconciliationStatus();

    IPrimitive<String> reasonCode();

    IPrimitive<String> reasonText();

    @Caption(description = "Reject/Return Item Fee; This field will contain the fee for the reject or returned item.")
    @Editor(type = EditorType.moneylabel)
    IPrimitive<BigDecimal> fee();

    // Not coming from Caledon, this is our processing flag
    @NotNull
    IPrimitive<Boolean> processingStatus();

}
