/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 25, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.domain.financial;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.ColumnId;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Timestamp;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;

/**
 * 
 * Actual payment record. {@link com.propertyvista.domain.financial.billing.InvoicePayment} captures payment portion for particular charge (in future version)
 * Deposit is considered as payment and presented by this class (this statement requires
 * review, Alex S, see deposit properties in
 * com.propertyvista.domain.financial.payment.java)
 * 
 * @author Alexs
 */
public interface PaymentRecord extends IEntity {

    @I18n
    enum PaymentStatus {

        Submitted,

        Scheduled,

        Queued,

        // This state is skipped in current implementation and may be enabled by Policy in future
        Processing,

        Received,

        Rejected,

        Canceled,

        Cleared,

        Returned;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

        // state sets:

        public static Collection<PaymentStatus> processed() {
            return EnumSet.of(Rejected, Cleared, Returned);
        }

        // states:

        public boolean isProcessed() {
            return processed().contains(this);
        }
    };

    @Owner
    @MemberColumn(notNull = true)
    @ReadOnly
    @Detached
    @JoinColumn
    BillingAccount billingAccount();

    @SuppressWarnings("rawtypes")
    @NotNull
    @Detached
    LeaseTermParticipant leaseTermParticipant();

    @Override
    @Indexed
    @ToString
    IPrimitive<Key> id();

    IPrimitive<LogicalDate> createdDate();

    IPrimitive<LogicalDate> receivedDate();

    IPrimitive<LogicalDate> finalizeDate();

    // Do not show in UI. May be used for reporting
    IPrimitive<LogicalDate> lastStatusChangeDate();

    /**
     * Do not post before that date, Date taken from Cheque. If present and in future record will become Scheduled
     */
    IPrimitive<LogicalDate> targetDate();

    @NotNull
    @Format("#,##0.00")
    @Editor(type = EditorType.money)
    IPrimitive<BigDecimal> amount();

    LeasePaymentMethod paymentMethod();

    IPrimitive<PaymentStatus> paymentStatus();

    public static boolean merchantAccountIsRequedForPayments = false;

    /**
     * This value set when record we Processing record. e.g. when we set status to Processing or Received
     */
    @Detached
    MerchantAccount merchantAccount();

    IPrimitive<String> transactionErrorMessage();

    @Caption(name = "Transaction Authorization #")
    IPrimitive<String> transactionAuthorizationNumber();

    interface PaidRejectedAggregatedTransferId extends ColumnId {
    }

    @JoinColumn(PaidRejectedAggregatedTransferId.class)
    @ReadOnly(allowOverrideNull = true)
    AggregatedTransfer aggregatedTransfer();

    interface ReturnAggregatedTransferId extends ColumnId {
    }

    @JoinColumn(ReturnAggregatedTransferId.class)
    @ReadOnly(allowOverrideNull = true)
    AggregatedTransfer aggregatedTransferReturn();

    /**
     * Key of record PadReconciliationDebitRecord from shared namespace.
     */
    @ReadOnly(allowOverrideNull = true)
    IPrimitive<Key> padReconciliationDebitRecordKey();

    @ReadOnly(allowOverrideNull = true)
    IPrimitive<Key> padReconciliationReturnRecordKey();

    @Editor(type = EditorType.textarea)
    IPrimitive<String> notes();

    @Timestamp(Timestamp.Update.Updated)
    IPrimitive<Date> updated();

    @Owned(cascade = {})
    ISet<PaymentRecordProcessing> processing();
}
