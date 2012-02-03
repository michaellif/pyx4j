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

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.JoinTable;
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

    @ReadOnly
    BillingAccount billingAccount();

    @ReadOnly
    BillingRun billingRun();

    @Detached
    @JoinTable(value = BillCharge.class, orderColumn = BillCharge.OrderId.class, cascade = false)
    IList<BillCharge> charges();

    @Detached
    @JoinTable(value = BillChargeAdjustment.class, orderColumn = BillChargeAdjustment.OrderId.class, cascade = false)
    IList<BillChargeAdjustment> adjustments();

    //TODO BillLeaseAdjustment

    /**
     * The total amount due from the previous bill.
     */
    IPrimitive<Double> previousBalanceAmount();

    /**
     * The total amount of payments received since the previous bill, up to the current Bill day.
     */
    IPrimitive<Double> paymentReceivedAmount();

    IPrimitive<Double> totalImmediateAdjustments();

    /**
     * pastDueAmount = previousBalanceAmount - paymentReceivedAmount - totalImmidiateAdjustments
     */
    IPrimitive<Double> pastDueAmount();

    IPrimitive<Double> serviceCharge();

    IPrimitive<Double> totalRecurringFeatureCharges();

    IPrimitive<Double> totalOneTimeFeatureCharges();

    /**
     * 
     * It includes all feature/service adjustments as well as Product Item adjustments
     * 
     */
    IPrimitive<Double> totalAdjustments();

    IPrimitive<Double> depositPaidAmount();

    IPrimitive<Double> latePaymentCharges();

    /**
     * totalDueAmount = pastDueAmount + serviceCharge + totalRecurringFeatureCharges + totalOneTimeFeatureCharges + totalAdjustments - depositPaidAmount +
     * latePaimantCharges
     */
    IPrimitive<Double> totalCurrentAmount();

    IPrimitive<Double> totalTaxes();

    /**
     * totalDueAmount = totalCurrentAmount + totalTaxes
     */
    IPrimitive<Double> totalDueAmount();

}
