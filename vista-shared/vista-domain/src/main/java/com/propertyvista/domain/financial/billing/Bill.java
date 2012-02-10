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

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinTable;
import com.pyx4j.entity.annotations.OrderBy;
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

    IPrimitive<BillStatus> billStatus();

    IPrimitive<Integer> billSequenceNumber();

    IPrimitive<String> rejectReason();

    @ReadOnly
    BillingAccount billingAccount();

    @ReadOnly
    BillingRun billingRun();

    @Detached
    @JoinTable(value = BillCharge.class, cascade = false)
    @OrderBy(BillCharge.OrderId.class)
    IList<BillCharge> charges();

    @Detached
    @JoinTable(value = BillChargeAdjustment.class, cascade = false)
    @OrderBy(BillChargeAdjustment.OrderId.class)
    IList<BillChargeAdjustment> adjustments();

    //TODO BillLeaseAdjustment

    /**
     * The total amount due from the previous bill.
     */
    IPrimitive<BigDecimal> previousBalanceAmount();

    /**
     * The total amount of payments received since the previous bill, up to the current Bill day.
     */
    IPrimitive<BigDecimal> paymentReceivedAmount();

    IPrimitive<BigDecimal> totalImmediateAdjustments();

    /**
     * pastDueAmount = previousBalanceAmount - paymentReceivedAmount - totalImmidiateAdjustments
     */
    IPrimitive<BigDecimal> pastDueAmount();

    IPrimitive<BigDecimal> serviceCharge();

    IPrimitive<BigDecimal> totalRecurringFeatureCharges();

    IPrimitive<BigDecimal> totalOneTimeFeatureCharges();

    /**
     * 
     * It includes all feature/service adjustments as well as Product Item adjustments
     * 
     */
    IPrimitive<BigDecimal> totalAdjustments();

    IPrimitive<BigDecimal> depositPaidAmount();

    IPrimitive<BigDecimal> latePaymentCharges();

    /**
     * totalDueAmount = pastDueAmount + serviceCharge + totalRecurringFeatureCharges + totalOneTimeFeatureCharges + totalAdjustments - depositPaidAmount +
     * latePaimantCharges
     */
    IPrimitive<BigDecimal> totalCurrentAmount();

    IPrimitive<BigDecimal> totalTaxes();

    /**
     * totalDueAmount = totalCurrentAmount + totalTaxes
     */
    IPrimitive<BigDecimal> totalDueAmount();

}
