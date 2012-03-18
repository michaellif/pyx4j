/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 10, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.domain.policy.policies;

import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.policy.framework.BuildingPolicy;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem;

@DiscriminatorValue("LeaseBillingPolicy")
public interface LeaseBillingPolicy extends BuildingPolicy {

    @NotNull
    IPrimitive<BillingAccount.ProrationMethod> prorationMethod();

    @NotNull
    IPrimitive<Boolean> useBillingPeriodSartDay();

    @NotNull
    IPrimitive<Integer> billingPeriodStartDay();

    @Owned
    LateFeeItem lateFee();
}
