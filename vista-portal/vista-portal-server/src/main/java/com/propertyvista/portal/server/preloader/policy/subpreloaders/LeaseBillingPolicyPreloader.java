/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.policy.subpreloaders;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.InternalBillingAccount.ProrationMethod;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy.BillConfirmationMethod;
import com.propertyvista.domain.policy.policies.domain.LeaseBillingTypePolicyItem;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class LeaseBillingPolicyPreloader extends AbstractPolicyPreloader<LeaseBillingPolicy> {

    public LeaseBillingPolicyPreloader() {
        super(LeaseBillingPolicy.class);
    }

    @Override
    protected LeaseBillingPolicy createPolicy(StringBuilder log) {
        LeaseBillingPolicy policy = EntityFactory.create(LeaseBillingPolicy.class);
        policy.prorationMethod().setValue(ProrationMethod.Actual);
        policy.confirmationMethod().setValue(BillConfirmationMethod.manual);

        LeaseBillingTypePolicyItem billingType = EntityFactory.create(LeaseBillingTypePolicyItem.class);
        billingType.paymentFrequency().setValue(PaymentFrequency.Monthly);
        billingType.billingCycleStartDay().setValue(1);
        billingType.offsetExecutionTargetDay().setValue(-15);
        policy.availableBillingTypes().add(billingType);

        log.append(policy.getStringView());
        return policy;
    }

}
