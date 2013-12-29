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

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.financial.ar.ARFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.ARCode.Type;
import com.propertyvista.domain.policy.policies.LeaseAdjustmentPolicy;
import com.propertyvista.domain.policy.policies.domain.LeaseAdjustmentPolicyItem;
import com.propertyvista.test.mock.MockDataModel;

public class LeaseAdjustmentPolicyDataModel extends MockDataModel<LeaseAdjustmentPolicy> {

    public LeaseAdjustmentPolicyDataModel() {
    }

    @Override
    protected void generate() {

        LeaseAdjustmentPolicy policy = EntityFactory.create(LeaseAdjustmentPolicy.class);

        {
            ARCode creditCode;
            creditCode = ServerSideFactory.create(ARFacade.class).getReservedARCode(Type.AccountCredit);
            LeaseAdjustmentPolicyItem item = EntityFactory.create(LeaseAdjustmentPolicyItem.class);
            item.code().set(creditCode);
            policy.policyItems().add(item);
        }

        {
            ARCode chargeCode;
            chargeCode = ServerSideFactory.create(ARFacade.class).getReservedARCode(Type.AccountCharge);
            LeaseAdjustmentPolicyItem item = EntityFactory.create(LeaseAdjustmentPolicyItem.class);
            item.code().set(chargeCode);
            item.taxes().add(getDataModel(TaxesDataModel.class).getAllItems().get(0));
            policy.policyItems().add(item);
        }

        policy.node().set(getDataModel(PmcDataModel.class).getOrgNode());

        Persistence.service().persist(policy);
        addItem(policy);
    }

}
