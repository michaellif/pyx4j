/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 20, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.test.mock.models;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdAssignmentType;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.test.mock.MockDataModel;

public class IdAssignmentPolicyDataModel extends MockDataModel<IdAssignmentPolicy> {

    public IdAssignmentPolicyDataModel() {
    }

    @Override
    protected void generate() {
        IdAssignmentPolicy policy = EntityFactory.create(IdAssignmentPolicy.class);

        policy.items().clear();
        for (IdTarget target : IdTarget.values()) {
            if (target == IdTarget.accountNumber) {
                continue;
            }

            IdAssignmentItem item = EntityFactory.create(IdAssignmentItem.class);
            item.target().setValue(target);
            item.type().setValue(IdAssignmentType.generatedNumber);

            policy.items().add(item);
        }

        policy.node().set(getDataModel(PmcDataModel.class).getOrgNode());

        Persistence.service().persist(policy);
        addItem(policy);
    }

}
