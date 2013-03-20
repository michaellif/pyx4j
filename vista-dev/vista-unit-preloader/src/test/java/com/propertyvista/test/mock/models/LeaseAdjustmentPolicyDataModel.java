/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 6, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.test.mock.models;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.policy.policies.LeaseAdjustmentPolicy;
import com.propertyvista.domain.policy.policies.domain.LeaseAdjustmentPolicyItem;
import com.propertyvista.domain.tenant.lease.LeaseAdjustmentReason;
import com.propertyvista.test.mock.MockDataModel;

public class LeaseAdjustmentPolicyDataModel extends MockDataModel<LeaseAdjustmentPolicy> {

    private LeaseAdjustmentPolicy policy;

    public LeaseAdjustmentPolicyDataModel() {
    }

    @Override
    protected void generate() {

        policy = EntityFactory.create(LeaseAdjustmentPolicy.class);

        for (LeaseAdjustmentReasonDataModel.Reason reason : LeaseAdjustmentReasonDataModel.Reason.values()) {
            LeaseAdjustmentReason lar = getDataModel(LeaseAdjustmentReasonDataModel.class).getReason(reason);
            LeaseAdjustmentPolicyItem item = EntityFactory.create(LeaseAdjustmentPolicyItem.class);
            item.leaseAdjustmentReason().set(lar);
            item.taxes().add(getDataModel(TaxesDataModel.class).getTaxes().get(0));
            policy.policyItems().add(item);
        }

        policy.node().set(getDataModel(BuildingDataModel.class).getCurrentItem());

        Persistence.service().persist(policy);
    }

    LeaseAdjustmentPolicy getPolicy() {
        return policy;
    }
}
