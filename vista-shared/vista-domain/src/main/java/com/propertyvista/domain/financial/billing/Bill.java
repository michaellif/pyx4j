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
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Versioned;
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

    };

    @I18n
    enum BillType {

        ZeroCycle,

        Estimate,

        First,

        Regular,

        Final;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    };

    @Owner
    @NotNull
    @JoinColumn
    @Detached
    BillingAccount billingAccount();

    @Detached
    @Versioned
    Lease lease();

    IPrimitive<Integer> billSequenceNumber();

    IPrimitiveSet<String> warnings();

    Bill previousBill();

    @Format("MM/dd/yyyy")
    @ToString(index = 0)
    IPrimitive<LogicalDate> dueDate();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> billingPeriodStartDate();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> billingPeriodEndDate();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> executionDate();

    @ToString(index = 2)
    IPrimitive<BillStatus> billStatus();

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
    IPrimitive<BigDecimal> balanceForwardAmount();

    /**
     * The total amount of payments received since the previous bill, up to the current
     * Bill day.
     */
    IPrimitive<BigDecimal> paymentReceivedAmount();

    IPrimitive<BigDecimal> paymentRejectedAmount();

    IPrimitive<BigDecimal> depositRefundAmount();

    //immediateAccountAdjustments should include taxes
    IPrimitive<BigDecimal> immediateAccountAdjustments();

    IPrimitive<BigDecimal> nsfCharges();

    IPrimitive<BigDecimal> withdrawalAmount();

    /**
     * pastDueAmount = previousBalanceAmount + paymentReceivedAmount + depositRefundAmount +
     * immediateAdjustments
     */

    @Caption(name = "Balance from last bill")
    IPrimitive<BigDecimal> pastDueAmount();

    IPrimitive<BigDecimal> serviceCharge();

    IPrimitive<BigDecimal> recurringFeatureCharges();

    IPrimitive<BigDecimal> oneTimeFeatureCharges();

    IPrimitive<BigDecimal> pendingAccountAdjustments();

    IPrimitive<BigDecimal> latePaymentFees();

    IPrimitive<BigDecimal> depositAmount();

    IPrimitive<BigDecimal> productCreditAmount();

    /**
     * currentAmount = pastDueAmount + serviceCharge + recurringFeatureCharges +
     * oneTimeFeatureCharges + totalAdjustments + depositPaidAmount
     */

    @Caption(name = "Total (before taxes)")
    IPrimitive<BigDecimal> currentAmount();

    IPrimitive<BigDecimal> taxes();

    /**
     * totalDueAmount = currentAmount + taxes
     */
    @ToString(index = 1)
    IPrimitive<BigDecimal> totalDueAmount();

}
