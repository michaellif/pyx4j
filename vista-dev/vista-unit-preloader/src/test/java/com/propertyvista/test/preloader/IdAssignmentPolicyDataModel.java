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
package com.propertyvista.test.preloader;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdAssignmentType;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;

public class IdAssignmentPolicyDataModel {

    final private IdAssignmentPolicy policy;

    private final PmcDataModel pmcDataModel;

    public IdAssignmentPolicyDataModel(PreloadConfig config, PmcDataModel pmcDataModel) {
        policy = EntityFactory.create(IdAssignmentPolicy.class);
        this.pmcDataModel = pmcDataModel;
    }

    public void generate() {
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

        policy.node().set(pmcDataModel.getOrgNode());

        Persistence.service().persist(policy);
    }

    IdAssignmentPolicy getPolicy() {
        return policy;
    }
}
