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
package com.propertyvista.domain.financial.billing;

import java.math.BigDecimal;
import java.util.EnumSet;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.MemberColumn;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.tenant.lease.Lease;

@ToStringFormat("{0}, ${1}, {2}")
@Table(prefix = "billing")
public interface Bill extends IEntity {

    @I18n
    enum BillStatus {

        Running,

        Failed,

        Finished,

        Confirmed,

        Rejected;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }

        public static boolean notConfirmed(BillStatus status) {
            return EnumSet.of(Running, Finished).contains(status);
        }

        public static boolean discarded(BillStatus status) {
            return EnumSet.of(Failed, Rejected).contains(status);
        }

    };

    @I18n
    enum BillType {

        ZeroCycle,

        First,

        Regular,

        Final,

        External;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    };

    @Owner
    @NotNull
    @MemberColumn(notNull = true)
    @JoinColumn
    @Detached
    BillingAccount billingAccount();

    @Detached
    Lease lease();

    IPrimitive<Integer> billSequenceNumber();

    IPrimitiveSet<String> warnings();

    @ToString(index = 0)
    IPrimitive<LogicalDate> dueDate();

    IPrimitive<LogicalDate> billingPeriodStartDate();

    IPrimitive<LogicalDate> billingPeriodEndDate();

    IPrimitive<LogicalDate> executionDate();

    @ToString(index = 2)
    IPrimitive<BillStatus> billStatus();

    IPrimitive<String> billCreationError();

    IPrimitive<BillType> billType();

    @Length(50)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> rejectReason();

    @ReadOnly
    BillingCycle billingCycle();

    @Detached
    @OrderBy(InvoiceLineItem.OrderId.class)
    IList<InvoiceLineItem> lineItems();

    /**
     * The total amount due from the previous bill.
     */
    @Caption(name = "Previous Balance")
    @Format("#,##0.00")
    IPrimitive<BigDecimal> balanceForwardAmount();

    /**
     * The total amount of payments received since the previous bill, up to the current
     * Bill day.
     */
    @Format("#,##0.00")
    IPrimitive<BigDecimal> paymentReceivedAmount();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> paymentRejectedAmount();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> depositRefundAmount();

    //immediateAccountAdjustments should include taxes
    @Format("#,##0.00")
    IPrimitive<BigDecimal> immediateAccountAdjustments();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> nsfCharges();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> withdrawalAmount();

    /**
     * pastDueAmount = previousBalanceAmount + paymentReceivedAmount + depositRefundAmount +
     * immediateAdjustments
     */
    @Editor(type = EditorType.money)
    @Format("#,##0.00")
    IPrimitive<BigDecimal> pastDueAmount();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> serviceCharge();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> recurringFeatureCharges();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> oneTimeFeatureCharges();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> pendingAccountAdjustments();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> previousChargeRefunds();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> latePaymentFees();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> depositAmount();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> productCreditAmount();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> carryForwardCredit();

    /**
     * currentAmount = serviceCharge + recurringFeatureCharges +
     * oneTimeFeatureCharges + totalAdjustments + depositPaidAmount
     */

    @Caption(name = "Total (before taxes)")
    @Format("#,##0.00")
    IPrimitive<BigDecimal> currentAmount();

    @Format("#,##0.00")
    IPrimitive<BigDecimal> taxes();

    /**
     * totalDueAmount = pastDueAmount + currentAmount + taxes
     */
    @ToString(index = 1)
    @Editor(type = EditorType.money)
    @Format("#,##0.00")
    IPrimitive<BigDecimal> totalDueAmount();

}
