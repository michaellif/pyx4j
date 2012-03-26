/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 9, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.server.billing;

import java.math.BigDecimal;

import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.server.common.policy.PolicyManager;

public class LeaseBillingUtils {

    public static BigDecimal calculateLatePayment(BigDecimal amount, BigDecimal monthlyRent, Building building) {
        LeaseBillingPolicy leaseBillingPolicy = PolicyManager.obtainEffectivePolicy(building, LeaseBillingPolicy.class);

        return calculateFee(leaseBillingPolicy, amount, monthlyRent);
    }

    private static BigDecimal calculateFee(LeaseBillingPolicy policy, BigDecimal amount, BigDecimal monthlyRent) {
        BigDecimal fee = new BigDecimal(0.0);

        switch (policy.lateFee().baseFeeType().getValue()) {
        case FlatAmount:
            fee = policy.lateFee().baseFee().getValue();
            break;

        case PercentOwedTotal:
            fee = amount.multiply(policy.lateFee().baseFee().getValue());
            break;

        case PercentMonthlyRent:
            fee = monthlyRent.multiply(policy.lateFee().baseFee().getValue());
            break;
        }

        switch (policy.lateFee().maxTotalFeeType().getValue()) {
        case FlatAmount:
            fee = fee.min(policy.lateFee().maxTotalFee().getValue());
            break;

        case PercentMonthlyRent:
            fee = fee.min(monthlyRent.multiply(policy.lateFee().maxTotalFee().getValue()));
            break;

        case Unlimited:
            break;
        }

        return fee;
    }
}
