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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.JoinColumn;
import com.pyx4j.entity.annotations.Length;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;
import com.pyx4j.i18n.shared.I18nEnum;

import com.propertyvista.domain.financial.BillingAccount;

public interface Bill extends IEntity {

    @I18n
    enum BillStatus {

        Running,

        Erred,

        Finished,

        Confirmed,

        Rejected;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    };

    @I18n
    enum BillType {

        First,

        Regular,

        Final;

        @Override
        public String toString() {
            return I18nEnum.toString(this);
        }
    };

    @Owner
    @JoinColumn
    BillingAccount billingAccount();

    IPrimitive<Integer> billSequenceNumber();

    /**
     * If draft is true no need to verify it. Next bill will run on the same billing cycle.
     */
    IPrimitive<Boolean> draft();

    Bill previousBill();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> billingPeriodStartDate();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> billingPeriodEndDate();

    IPrimitive<BillStatus> billStatus();

    IPrimitive<BillType> billType();

    @Length(50)
    @Editor(type = Editor.EditorType.textarea)
    IPrimitive<String> rejectReason();

    @ReadOnly
    BillingRun billingRun();

    @Detached
    @Owned
    @OrderBy(BillPayment.OrderId.class)
    IList<BillPayment> billPayments();

    @Detached
    @Owned
    @OrderBy(BillCharge.OrderId.class)
    IList<BillCharge> charges();

    @Detached
    @Owned
    @OrderBy(BillChargeAdjustment.OrderId.class)
    IList<BillChargeAdjustment> chargeAdjustments();

    @Detached
    @Owned
    @OrderBy(BillLeaseAdjustment.OrderId.class)
    IList<BillLeaseAdjustment> leaseAdjustments();

    @Detached
    @Owned
    @OrderBy(BillEntryAdjustment.OrderId.class)
    IList<BillEntryAdjustment> billEntryAdjustments();

    /**
     * The total amount due from the previous bill.
     */
    IPrimitive<BigDecimal> previousBalanceAmount();

    /**
     * The total amount of payments received since the previous bill, up to the current
     * Bill day.
     */
    IPrimitive<BigDecimal> paymentReceivedAmount();

    IPrimitive<BigDecimal> depositRefundAmount();

    IPrimitive<BigDecimal> immediateAdjustments();

    /**
     * pastDueAmount = previousBalanceAmount - paymentReceivedAmount - depositRefundAmount -
     * immediateAdjustments
     */
    IPrimitive<BigDecimal> pastDueAmount();

    IPrimitive<BigDecimal> serviceCharge();

    IPrimitive<BigDecimal> recurringFeatureCharges();

    IPrimitive<BigDecimal> oneTimeFeatureCharges();

    /**
     * 
     * It includes all feature/service adjustments as well as Product Item adjustments
     * 
     */
    IPrimitive<BigDecimal> totalAdjustments();

    IPrimitive<BigDecimal> depositPaidAmount();

    IPrimitive<BigDecimal> latePaymentCharges();

    /**
     * currentAmount = pastDueAmount + serviceCharge + recurringFeatureCharges +
     * oneTimeFeatureCharges + totalAdjustments - depositPaidAmount +
     * latePaymentCharges
     */
    IPrimitive<BigDecimal> currentAmount();

    IPrimitive<BigDecimal> taxes();

    /**
     * totalDueAmount = currentAmount + taxes
     */
    IPrimitive<BigDecimal> totalDueAmount();

}
