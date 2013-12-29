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

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.policy.policies.ARPolicy;
import com.propertyvista.test.mock.MockDataModel;

public class ARPolicyDataModel extends MockDataModel<ARPolicy> {

    public ARPolicyDataModel() {
    }

    @Override
    protected void generate() {
        ARPolicy policy = EntityFactory.create(ARPolicy.class);
        policy.creditDebitRule().setValue(ARPolicy.CreditDebitRule.oldestDebtFirst);
        policy.node().set(getDataModel(PmcDataModel.class).getOrgNode());
        Persistence.service().persist(policy);
        addItem(policy);
    }

}
