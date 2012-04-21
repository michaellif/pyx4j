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
package com.propertyvista.biz.financial.preload;

import java.math.BigDecimal;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.BillingAccount.ProrationMethod;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem;
import com.propertyvista.domain.policy.policies.domain.LateFeeItem.BaseFeeType;

public class LeaseBillingPolicyDataModel {
    private final BuildingDataModel buildingDataModel;

    private LeaseBillingPolicy policy;

    public LeaseBillingPolicyDataModel(BuildingDataModel buildingDataModel) {
        this.buildingDataModel = buildingDataModel;
        generate(true);
    }

    public void generate(boolean persist) {
        policy = EntityFactory.create(LeaseBillingPolicy.class);

        policy.billingPeriodStartDay().setValue(1);
        policy.useBillingPeriodSartDay().setValue(false);
        policy.prorationMethod().setValue(ProrationMethod.Standard);

        LateFeeItem item = EntityFactory.create(LateFeeItem.class);

        item.baseFee().setValue(new BigDecimal(50.00));
        item.baseFeeType().setValue(BaseFeeType.FlatAmount);
        item.maxTotalFee().setValue(new BigDecimal(1000.00));
        item.maxTotalFeeType().setValue(LateFeeItem.MaxTotalFeeType.FlatAmount);

        policy.lateFee().set(item);

        policy.node().set(buildingDataModel.getBuilding());

        if (persist) {
            Persistence.service().persist(policy);
        }
    }

    LeaseBillingPolicy getPolicy() {
        return policy;
    }
}
