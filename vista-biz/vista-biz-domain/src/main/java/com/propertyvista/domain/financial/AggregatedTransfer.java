/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 27, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.financial;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.AbstractEntity;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Inheritance;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.PaymentRecord.PaidOrRejectedAggregatedTransferId;
import com.propertyvista.domain.financial.PaymentRecord.ReturnAggregatedTransferId;
import com.propertyvista.domain.note.HasNotesAndAttachments;

@AbstractEntity
@Inheritance(strategy = Inheritance.InheritanceStrategy.SINGLE_TABLE)
public interface AggregatedTransfer extends IEntity, HasNotesAndAttachments {

    @I18n
    public enum AggregatedTransferStatus {

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

    @MemberColumn(notNull = true)
    IPrimitive<FundsTransferType> fundsTransferType();

    @NotNull
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> grossPaymentAmount();

    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> grossPaymentFee();

    IPrimitive<Integer> grossPaymentCount();

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
