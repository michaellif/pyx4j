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
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.ReadOnly;
import com.pyx4j.entity.annotations.Table;
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
    @NotNull
    @JoinColumn
    @Detached
    BillingAccount billingAccount();

    @Detached
    @Versioned
    Lease lease();

    IPrimitive<Integer> billSequenceNumber();

    /**
     * If draft is true no need to verify it. Next bill will run on the same billing cycle.
     */
    IPrimitive<Boolean> draft();

    IPrimitiveSet<String> warnings();

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
    @OrderBy(InvoiceLineItem.OrderId.class)
    IList<InvoiceLineItem> lineItems();

    /**
     * The total amount due from the previous bill.
     */
    IPrimitive<BigDecimal> balanceForwardAmount();

    /**
     * The total amount of payments received since the previous bill, up to the current
     * Bill day.
     */
    IPrimitive<BigDecimal> paymentReceivedAmount();

    IPrimitive<BigDecimal> depositRefundAmount();

    IPrimitive<BigDecimal> immediateLeaseAdjustments();

    /**
     * pastDueAmount = previousBalanceAmount + paymentReceivedAmount + depositRefundAmount +
     * immediateAdjustments
     */
    IPrimitive<BigDecimal> pastDueAmount();

    IPrimitive<BigDecimal> serviceCharge();

    IPrimitive<BigDecimal> recurringFeatureCharges();

    IPrimitive<BigDecimal> oneTimeFeatureCharges();

    IPrimitive<BigDecimal> pendingLeaseAdjustments();

    IPrimitive<BigDecimal> depositAmount();

    IPrimitive<BigDecimal> credits();

    /**
     * currentAmount = pastDueAmount + serviceCharge + recurringFeatureCharges +
     * oneTimeFeatureCharges + totalAdjustments + depositPaidAmount
     */
    IPrimitive<BigDecimal> currentAmount();

    IPrimitive<BigDecimal> taxes();

    /**
     * totalDueAmount = currentAmount + taxes
     */
    IPrimitive<BigDecimal> totalDueAmount();

}
