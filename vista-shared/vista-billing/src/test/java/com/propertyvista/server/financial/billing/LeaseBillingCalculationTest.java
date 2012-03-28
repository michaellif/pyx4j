/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 25, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.server.financial.billing;

import java.math.BigDecimal;
import java.math.RoundingMode;

import junit.framework.TestCase;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem.BaseFeeType;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem.MaxTotalFeeType;
import com.propertyvista.server.financial.billing.LeaseBillingUtils;

public class LeaseBillingCalculationTest extends TestCase {

    public void testZeroAmountOwning() {
        LeaseBillingPolicy policy = EntityFactory.create(LeaseBillingPolicy.class);

        BigDecimal projectedFee = new BigDecimal(0).setScale(2, RoundingMode.HALF_UP);
        BigDecimal lateFee = LeaseBillingUtils.calculateLatePaymentFee(policy, new BigDecimal("0.00"), new BigDecimal("500.00")).setScale(2,
                RoundingMode.HALF_UP);

        assertEquals(projectedFee, lateFee);
    }

    public void testBaseFeeType() {
        LeaseBillingPolicy policy = EntityFactory.create(LeaseBillingPolicy.class);

        BigDecimal baseFee = new BigDecimal("100.00");
        BigDecimal ownedTotal = new BigDecimal("205.55");
        BigDecimal montlyRent = new BigDecimal("1500.00");
        BigDecimal maxTotalFee = new BigDecimal("500.00");

        policy.lateFee().baseFee().setValue(baseFee);
        policy.lateFee().baseFeeType().setValue(BaseFeeType.FlatAmount);
        policy.lateFee().maxTotalFee().setValue(maxTotalFee);
        policy.lateFee().maxTotalFeeType().setValue(MaxTotalFeeType.FlatAmount);

        BigDecimal calculatedLateFee = LeaseBillingUtils.calculateLatePaymentFee(policy, ownedTotal, montlyRent);
        BigDecimal projectedFee = baseFee;
        assertEquals(projectedFee, calculatedLateFee);

        baseFee = new BigDecimal("0.1");

        policy.lateFee().baseFee().setValue(baseFee);
        policy.lateFee().baseFeeType().setValue(BaseFeeType.PercentOwedTotal);

        projectedFee = ownedTotal.multiply(baseFee);
        calculatedLateFee = LeaseBillingUtils.calculateLatePaymentFee(policy, ownedTotal, montlyRent);
        assertEquals(projectedFee, calculatedLateFee);

        baseFee = new BigDecimal("0.1");

        policy.lateFee().baseFee().setValue(baseFee);
        policy.lateFee().baseFeeType().setValue(BaseFeeType.PercentMonthlyRent);

        projectedFee = montlyRent.multiply(baseFee);
        calculatedLateFee = LeaseBillingUtils.calculateLatePaymentFee(policy, ownedTotal, montlyRent);
        assertEquals(projectedFee, calculatedLateFee);
    }

    public void testMaxFeeType() {
        LeaseBillingPolicy policy = EntityFactory.create(LeaseBillingPolicy.class);

        BigDecimal baseFee = new BigDecimal("200.00");
        BigDecimal ownedTotal = new BigDecimal("205.55");
        BigDecimal montlyRent = new BigDecimal("1500.00");
        BigDecimal maxTotalFee = new BigDecimal("0.1");

        policy.lateFee().baseFee().setValue(baseFee);
        policy.lateFee().baseFeeType().setValue(BaseFeeType.FlatAmount);
        policy.lateFee().maxTotalFee().setValue(maxTotalFee);
        policy.lateFee().maxTotalFeeType().setValue(MaxTotalFeeType.PercentMonthlyRent);

        BigDecimal calculatedLateFee = LeaseBillingUtils.calculateLatePaymentFee(policy, ownedTotal, montlyRent);
        BigDecimal projectedFee = baseFee.min(montlyRent.multiply(maxTotalFee));
        assertEquals(projectedFee, calculatedLateFee);

        policy.lateFee().maxTotalFeeType().setValue(MaxTotalFeeType.Unlimited);
        calculatedLateFee = LeaseBillingUtils.calculateLatePaymentFee(policy, ownedTotal, montlyRent);
        projectedFee = baseFee;
        assertEquals(projectedFee, calculatedLateFee);
    }

    public void testCombined() {
        LeaseBillingPolicy policy = EntityFactory.create(LeaseBillingPolicy.class);

        BigDecimal baseFee = new BigDecimal("0.1");
        BigDecimal ownedTotal = new BigDecimal("3000.55");
        BigDecimal montlyRent = new BigDecimal("1500.00");
        BigDecimal maxTotalFee = new BigDecimal("0.1");

        policy.lateFee().baseFee().setValue(baseFee);
        policy.lateFee().baseFeeType().setValue(BaseFeeType.PercentOwedTotal);
        policy.lateFee().maxTotalFee().setValue(maxTotalFee);
        policy.lateFee().maxTotalFeeType().setValue(MaxTotalFeeType.PercentMonthlyRent);

        BigDecimal calculatedLateFee = LeaseBillingUtils.calculateLatePaymentFee(policy, ownedTotal, montlyRent);

        BigDecimal projectedFee = montlyRent.multiply(maxTotalFee).min(ownedTotal.multiply(baseFee));
        assertEquals(projectedFee, calculatedLateFee);

    }
}
