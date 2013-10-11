/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 18, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.test.mock.models;

import java.math.BigDecimal;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.payment.PaymentType;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem.BaseFeeType;
import com.propertyvista.domain.policy.policies.domain.LeaseBillingTypePolicyItem;
import com.propertyvista.domain.policy.policies.domain.NsfFeeItem;
import com.propertyvista.test.mock.MockDataModel;

public class LeaseBillingPolicyDataModel extends MockDataModel<LeaseBillingPolicy> {

    private LeaseBillingPolicy policy;

    public LeaseBillingPolicyDataModel() {
    }

    @Override
    protected void generate() {

        policy = EntityFactory.create(LeaseBillingPolicy.class);

        policy.prorationMethod().setValue(BillingAccount.ProrationMethod.Actual);

        LateFeeItem lateFee = EntityFactory.create(LateFeeItem.class);
        lateFee.baseFee().setValue(new BigDecimal(50.00));
        lateFee.baseFeeType().setValue(BaseFeeType.FlatAmount);
        lateFee.maxTotalFee().setValue(new BigDecimal(1000.00));
        lateFee.maxTotalFeeType().setValue(LateFeeItem.MaxTotalFeeType.FlatAmount);
        policy.lateFee().set(lateFee);

        NsfFeeItem nsfItem = EntityFactory.create(NsfFeeItem.class);
        nsfItem.paymentType().setValue(PaymentType.Check);
        nsfItem.fee().setValue(new BigDecimal(30.00));
        policy.nsfFees().add(nsfItem);

        nsfItem = EntityFactory.create(NsfFeeItem.class);
        nsfItem.paymentType().setValue(PaymentType.Echeck);
        nsfItem.fee().setValue(new BigDecimal(100.00));
        policy.nsfFees().add(nsfItem);

        nsfItem = EntityFactory.create(NsfFeeItem.class);
        nsfItem.paymentType().setValue(PaymentType.CreditCard);
        nsfItem.fee().setValue(new BigDecimal(30.00));
        policy.nsfFees().add(nsfItem);

        policy.confirmationMethod().setValue(getConfig().billConfirmationMethod);

        policy.node().set(getDataModel(PmcDataModel.class).getOrgNode());

        {
            LeaseBillingTypePolicyItem billingType = EntityFactory.create(LeaseBillingTypePolicyItem.class);
            billingType.billingPeriod().setValue(BillingPeriod.Monthly);
            billingType.billingCycleStartDay().setValue(getConfig().defaultBillingCycleSartDay);
            billingType.paymentDueDayOffset().setValue(0);
            billingType.finalDueDayOffset().setValue(15);
            billingType.billExecutionDayOffset().setValue(-15);
            billingType.autopayExecutionDayOffset().setValue(0);
            policy.availableBillingTypes().add(billingType);
        }

        {
            LeaseBillingTypePolicyItem billingType = EntityFactory.create(LeaseBillingTypePolicyItem.class);
            billingType.billingPeriod().setValue(BillingPeriod.SemiMonthly);
            billingType.billingCycleStartDay().setValue(getConfig().defaultBillingCycleSartDay);
            billingType.paymentDueDayOffset().setValue(0);
            billingType.finalDueDayOffset().setValue(15);
            billingType.billExecutionDayOffset().setValue(-7);
            billingType.autopayExecutionDayOffset().setValue(0);
            policy.availableBillingTypes().add(billingType);
        }

        {
            LeaseBillingTypePolicyItem billingType = EntityFactory.create(LeaseBillingTypePolicyItem.class);
            billingType.billingPeriod().setValue(BillingPeriod.BiWeekly);
            billingType.billingCycleStartDay().setValue(getConfig().defaultBillingCycleSartDay);
            billingType.paymentDueDayOffset().setValue(0);
            billingType.finalDueDayOffset().setValue(15);
            billingType.billExecutionDayOffset().setValue(-7);
            billingType.autopayExecutionDayOffset().setValue(0);
            policy.availableBillingTypes().add(billingType);
        }

        {
            LeaseBillingTypePolicyItem billingType = EntityFactory.create(LeaseBillingTypePolicyItem.class);
            billingType.billingPeriod().setValue(BillingPeriod.Weekly);
            billingType.billingCycleStartDay().setValue(getConfig().defaultBillingCycleSartDay);
            billingType.paymentDueDayOffset().setValue(0);
            billingType.finalDueDayOffset().setValue(15);
            billingType.billExecutionDayOffset().setValue(-3);
            billingType.autopayExecutionDayOffset().setValue(0);
            policy.availableBillingTypes().add(billingType);
        }

        Persistence.service().persist(policy);
        addItem(policy);
    }

}
