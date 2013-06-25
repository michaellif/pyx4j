/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.financial;

import java.math.BigDecimal;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.PaymentRecord.PaidOrRejectedAggregatedTransferId;
import com.propertyvista.domain.financial.PaymentRecord.ReturnAggregatedTransferId;

public interface AggregatedTransfer extends IEntity {

    @I18n
    enum AggregatedTransferStatus {

        Rejected,

        Canceled,

        Paid,

        Hold;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

    };

    IPrimitive<LogicalDate> paymentDate();

    IPrimitive<AggregatedTransferStatus> status();

    MerchantAccount merchantAccount();

    IPrimitive<Key> padReconciliationSummaryKey();

    @NotNull
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> grossPaymentAmount();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> grossPaymentFee();

    IPrimitive<Integer> grossPaymentCount();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> rejectItemsAmount();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> rejectItemsFee();

    IPrimitive<Integer> rejectItemsCount();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> returnItemsAmount();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> returnItemsFee();

    IPrimitive<Integer> returnItemsCount();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> netAmount();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> adjustments();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> previousBalance();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> merchantBalance();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> fundsReleased();

    @Detached(level = AttachLevel.Detached)
    @JoinTable(value = PaymentRecord.class, mappedBy = PaidOrRejectedAggregatedTransferId.class)
    ISet<PaymentRecord> payments();

    @Detached(level = AttachLevel.Detached)
    @JoinTable(value = PaymentRecord.class, mappedBy = ReturnAggregatedTransferId.class)
    ISet<PaymentRecord> returnedPayments();

    @Detached(level = AttachLevel.Detached)
    @JoinTable(value = PaymentRecordProcessing.class, mappedBy = PaymentRecordProcessing.RejectedBatchAggregatedTransferId.class)
    ISet<PaymentRecord> rejectedBatchPayments();

    IPrimitive<String> transactionErrorMessage();
}
