/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.preloader.policy.subpreloaders;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.preloader.policy.AbstractPolicyPreloader;
import com.propertyvista.domain.policy.policies.AutoPayPolicy;

public class AutoPayPolicyPreloader extends AbstractPolicyPreloader<AutoPayPolicy> {

    public AutoPayPolicyPreloader() {
        super(AutoPayPolicy.class);
    }

    @Override
    protected AutoPayPolicy createPolicy(StringBuilder log) {
        AutoPayPolicy policy = EntityFactory.create(AutoPayPolicy.class);

        policy.onLeaseChargeChangeRule().setValue(AutoPayPolicy.ChangeRule.keepPercentage);
        policy.excludeFirstBillingPeriodCharge().setValue(Boolean.FALSE);
        policy.excludeLastBillingPeriodCharge().setValue(Boolean.TRUE);
        policy.allowCancelationByResident().setValue(Boolean.TRUE);

        return policy;
    }
}
