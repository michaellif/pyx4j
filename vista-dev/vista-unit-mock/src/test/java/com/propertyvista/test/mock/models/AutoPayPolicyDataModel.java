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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.policy.policies.AutoPayPolicy;
import com.propertyvista.test.mock.MockDataModel;

public class AutoPayPolicyDataModel extends MockDataModel<AutoPayPolicy> {

    public AutoPayPolicyDataModel() {
    }

    @Override
    protected void generate() {
        AutoPayPolicy policy = EntityFactory.create(AutoPayPolicy.class);

        policy.onLeaseChargeChangeRule().setValue(AutoPayPolicy.ChangeRule.keepPercentage);
        policy.excludeFirstBillingPeriodCharge().setValue(Boolean.FALSE);
        policy.excludeLastBillingPeriodCharge().setValue(Boolean.TRUE);

        policy.node().set(getDataModel(PmcDataModel.class).getOrgNode());
        Persistence.service().persist(policy);
        addItem(policy);
    }
}
