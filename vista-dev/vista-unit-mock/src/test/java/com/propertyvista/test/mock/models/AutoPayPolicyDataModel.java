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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.policy.policies.AutoPayPolicy;
import com.propertyvista.test.mock.MockDataModel;

public class AutoPayPolicyDataModel extends MockDataModel<AutoPayPolicy> {

    private AutoPayPolicy policy;

    public AutoPayPolicyDataModel() {
    }

    @Override
    protected void generate() {
        policy = EntityFactory.create(AutoPayPolicy.class);

        policy.onLeaseChargeChangeRule().setValue(AutoPayPolicy.ChangeRule.keepPercentage);
        policy.excludeFirstBillingPeriodCharge().setValue(Boolean.FALSE);
        policy.excludeLastBillingPeriodCharge().setValue(Boolean.TRUE);

        policy.node().set(getDataModel(PmcDataModel.class).getOrgNode());
        Persistence.service().persist(policy);
        addItem(policy);
    }

    // modifiers:
    public void setOnLeaseChargeChangeRule(AutoPayPolicy.ChangeRule rule) {
        policy.onLeaseChargeChangeRule().setValue(rule);
        ensureSetEffective();
    }

    public void setExcludeFirstBillingPeriodCharge(Boolean exclude) {
        policy.excludeFirstBillingPeriodCharge().setValue(exclude);
        ensureSetEffective();
    }

    public void setExcludeLastBillingPeriodCharge(Boolean exclude) {
        policy.excludeLastBillingPeriodCharge().setValue(exclude);
        ensureSetEffective();
    }

    private void ensureSetEffective() {
        Persistence.service().merge(policy);
        ServerSideFactory.create(PolicyFacade.class).resetPolicyCache();
    }
}
