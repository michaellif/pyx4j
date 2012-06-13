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
package com.propertyvista.biz.financial.preload;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdAssignmentType;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;

public class IdAssignmentPolicyDataModel {

    private IdAssignmentPolicy policy;

    public IdAssignmentPolicyDataModel(PreloadConfig config) {
    }

    public void generate(boolean persist) {
        policy = EntityFactory.create(IdAssignmentPolicy.class);

        IdAssignmentItem item = null;

        for (IdTarget target : IdTarget.values()) {
            if (target == IdTarget.accountNumber) {
                continue;
            }
            item = EntityFactory.create(IdAssignmentItem.class);

            item.target().setValue(target);
            item.type().setValue(target == IdTarget.propertyCode ? IdAssignmentType.userAssigned : IdAssignmentType.generatedNumber);

            policy.itmes().add(item);

        }

        OrganizationPoliciesNode orgNode = EntityFactory.create(OrganizationPoliciesNode.class);
        Persistence.service().persist(orgNode);

        policy.node().set(orgNode);

        if (persist) {
            Persistence.service().persist(policy);
        }
    }

    IdAssignmentPolicy getPolicy() {
        return policy;
    }
}
