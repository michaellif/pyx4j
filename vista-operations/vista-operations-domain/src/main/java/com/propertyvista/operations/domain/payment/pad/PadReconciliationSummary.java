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
package com.propertyvista.operations.domain.payment.pad;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.GwtBlacklist;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.VistaNamespace;
import com.propertyvista.domain.pmc.PmcMerchantAccountIndex;

@Table(namespace = VistaNamespace.operationsNamespace)
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
@GwtBlacklist
public interface PadReconciliationSummary extends IEntity {

    @Owner
    @MemberColumn(notNull = true)
    @JoinColumn
    @Indexed
    PadReconciliationFile reconciliationFile();

    @OrderColumn
    IPrimitive<Integer> odr();

    IPrimitive<LogicalDate> paymentDate();

    IPrimitive<String> merchantTerminalId();

    // found based on merchantTerminalId
    PmcMerchantAccountIndex merchantAccount();

    IPrimitive<MerchantReconciliationStatus> reconciliationStatus();

    @NotNull
    @Format("#0.00")
    IPrimitive<BigDecimal> grossPaymentAmount();

    @Format("#0.00")
    IPrimitive<BigDecimal> grossPaymentFee();

    IPrimitive<Integer> grossPaymentCount();

    @Format("#0.00")
    IPrimitive<BigDecimal> rejectItemsAmount();

    @Format("#0.00")
    IPrimitive<BigDecimal> rejectItemsFee();

    IPrimitive<Integer> rejectItemsCount();

    @Format("#0.00")
    IPrimitive<BigDecimal> returnItemsAmount();

    @Format("#0.00")
    IPrimitive<BigDecimal> returnItemsFee();

    IPrimitive<Integer> returnItemsCount();

    @Format("#0.00")
    IPrimitive<BigDecimal> netAmount();

    @Format("#0.00")
    IPrimitive<BigDecimal> adjustments();

    @Format("#0.00")
    IPrimitive<BigDecimal> previousBalance();

    @Format("#0.00")
    IPrimitive<BigDecimal> merchantBalance();

    @Format("#0.00")
    IPrimitive<BigDecimal> fundsReleased();

    @Owned
    IList<PadReconciliationDebitRecord> records();

}
