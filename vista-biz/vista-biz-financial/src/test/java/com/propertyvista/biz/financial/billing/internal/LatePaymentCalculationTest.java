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
package com.propertyvista.biz.financial.billing.internal;

import java.math.BigDecimal;

import org.junit.experimental.categories.Category;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.financial.billing.internal.LatePaymentUtils;
import com.propertyvista.config.tests.VistaDBTestBase;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem.BaseFeeType;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem.MaxTotalFeeType;
import com.propertyvista.test.integration.IntegrationTestBase.FunctionalTests;

@Category(FunctionalTests.class)
public class LatePaymentCalculationTest extends VistaDBTestBase {

    public void testZeroAmountOwning() {
        LeaseBillingPolicy policy = EntityFactory.create(LeaseBillingPolicy.class);
        BigDecimal lateFee = LatePaymentUtils.calculateLatePaymentFee(new BigDecimal("0.00"), new BigDecimal("500.00"), policy);
        assertEquals(new BigDecimal("0.00"), lateFee);
    }

    public void testBaseFeeType() {
        LeaseBillingPolicy policy = EntityFactory.create(LeaseBillingPolicy.class);

        BigDecimal ownedTotal = new BigDecimal("205.55");
        BigDecimal monthlyRent = new BigDecimal("1500.00");

        policy.lateFee().baseFee().setValue(new BigDecimal("100.00"));
        policy.lateFee().baseFeeType().setValue(BaseFeeType.FlatAmount);
        policy.lateFee().maxTotalFee().setValue(new BigDecimal("500.00"));
        policy.lateFee().maxTotalFeeType().setValue(MaxTotalFeeType.FlatAmount);

        BigDecimal calculatedLateFee = LatePaymentUtils.calculateLatePaymentFee(ownedTotal, monthlyRent, policy);
        assertEquals(new BigDecimal("100.00"), calculatedLateFee);

        policy.lateFee().baseFee().setValue(new BigDecimal("0.1"));
        policy.lateFee().baseFeeType().setValue(BaseFeeType.PercentOwedTotal);

        calculatedLateFee = LatePaymentUtils.calculateLatePaymentFee(ownedTotal, monthlyRent, policy);
        assertEquals(new BigDecimal("20.56"), calculatedLateFee);

        policy.lateFee().baseFee().setValue(new BigDecimal("0.1"));
        policy.lateFee().baseFeeType().setValue(BaseFeeType.PercentMonthlyRent);

        calculatedLateFee = LatePaymentUtils.calculateLatePaymentFee(ownedTotal, monthlyRent, policy);
        assertEquals(new BigDecimal("150.00"), calculatedLateFee);
    }

    public void testMaxFeeType() {
        LeaseBillingPolicy policy = EntityFactory.create(LeaseBillingPolicy.class);

        BigDecimal ownedTotal = new BigDecimal("205.55");
        BigDecimal monthlyRent = new BigDecimal("1500.00");
        BigDecimal maxTotalFee = new BigDecimal("0.1");

        policy.lateFee().baseFee().setValue(new BigDecimal("200.00"));
        policy.lateFee().baseFeeType().setValue(BaseFeeType.FlatAmount);
        policy.lateFee().maxTotalFee().setValue(maxTotalFee);
        policy.lateFee().maxTotalFeeType().setValue(MaxTotalFeeType.PercentMonthlyRent);

        BigDecimal calculatedLateFee = LatePaymentUtils.calculateLatePaymentFee(ownedTotal, monthlyRent, policy);
        assertEquals(new BigDecimal("150.00"), calculatedLateFee);

        policy.lateFee().maxTotalFeeType().setValue(MaxTotalFeeType.Unlimited);
        calculatedLateFee = LatePaymentUtils.calculateLatePaymentFee(ownedTotal, monthlyRent, policy);
        assertEquals(new BigDecimal("200.00"), calculatedLateFee);
    }

    public void testCombined() {
        LeaseBillingPolicy policy = EntityFactory.create(LeaseBillingPolicy.class);

        BigDecimal ownedTotal = new BigDecimal("3000.55");
        BigDecimal montlyRent = new BigDecimal("1500.00");
        BigDecimal maxTotalFee = new BigDecimal("0.1");

        policy.lateFee().baseFee().setValue(new BigDecimal("0.1"));
        policy.lateFee().baseFeeType().setValue(BaseFeeType.PercentOwedTotal);
        policy.lateFee().maxTotalFee().setValue(maxTotalFee);
        policy.lateFee().maxTotalFeeType().setValue(MaxTotalFeeType.PercentMonthlyRent);

        BigDecimal calculatedLateFee = LatePaymentUtils.calculateLatePaymentFee(ownedTotal, montlyRent, policy);

        assertEquals(new BigDecimal("150.00"), calculatedLateFee);

    }
}
