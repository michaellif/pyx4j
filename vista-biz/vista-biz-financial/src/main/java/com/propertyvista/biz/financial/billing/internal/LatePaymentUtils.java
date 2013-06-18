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
package com.propertyvista.biz.financial.billing.internal;

import java.math.BigDecimal;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.util.DomainUtil;

public class LatePaymentUtils {

    public static BigDecimal calculateLatePaymentFee(BigDecimal amount, BigDecimal monthlyRent, Building building) {
        LeaseBillingPolicy leaseBillingPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(building, LeaseBillingPolicy.class);

        return calculateLatePaymentFee(amount, monthlyRent, leaseBillingPolicy);
    }

    public static BigDecimal calculateLatePaymentFee(BigDecimal amount, BigDecimal monthlyRent, LeaseBillingPolicy policy) {
        BigDecimal fee = new BigDecimal("0.00");

        if (amount.compareTo(fee) == 0) {
            return fee;
        }

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

        return DomainUtil.roundMoney(fee);
    }
}
