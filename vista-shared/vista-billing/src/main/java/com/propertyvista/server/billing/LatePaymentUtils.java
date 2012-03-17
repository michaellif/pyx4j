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

import com.propertyvista.domain.policy.policies.LateFeePolicy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.server.common.policy.PolicyManager;

public class LatePaymentUtils {

    public static BigDecimal latePayment(BigDecimal amount, BigDecimal monthlyRent, Building building) {
        LateFeePolicy lateFeePolicy = PolicyManager.obtainEffectivePolicy(building, LateFeePolicy.class);

        if (amount.compareTo(lateFeePolicy.minimumAmounDue().getValue()) <= 0)
            return new BigDecimal(0.0); // Don't bother with small amount...

        return calculateFee(lateFeePolicy, amount, monthlyRent);
    }

    private static BigDecimal calculateFee(LateFeePolicy policy, BigDecimal amount, BigDecimal monthlyRent) {
        BigDecimal fee = new BigDecimal(0.0);

        switch (policy.baseFeeType().getValue()) {
        case FlatAmount:
            fee = policy.baseFee().getValue();
            break;

        case PercentOwedTotal:
            fee = amount.multiply(policy.baseFee().getValue());
            break;

        case PercentMonthlyRent:
            fee = monthlyRent.multiply(policy.baseFee().getValue());
            break;
        }

        switch (policy.maxTotalFeeType().getValue()) {
        case FlatAmount:
            fee = fee.min(policy.maxTotalFee().getValue());
            break;

        case PercentMonthlyRent:
            fee = fee.min(monthlyRent.multiply(policy.maxTotalFee().getValue()));
            break;

        case Unlimited:
            break;
        }

        return fee;
    }
}
