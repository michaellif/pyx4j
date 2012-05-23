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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.policy.policies.ARPolicy;

public class ARPolicyDataModel {
    private final BuildingDataModel buildingDataModel;

    private ARPolicy policy;

    public ARPolicyDataModel(BuildingDataModel buildingDataModel) {
        this.buildingDataModel = buildingDataModel;
    }

    public void generate(boolean persist) {
        policy = EntityFactory.create(ARPolicy.class);

        policy.creditDebitRule().setValue(ARPolicy.CreditDebitRule.byDueDate);

        policy.node().set(buildingDataModel.getBuilding());

        if (persist) {
            Persistence.service().persist(policy);
        }
    }

    public ARPolicy getPolicy() {
        return policy;
    }
}
