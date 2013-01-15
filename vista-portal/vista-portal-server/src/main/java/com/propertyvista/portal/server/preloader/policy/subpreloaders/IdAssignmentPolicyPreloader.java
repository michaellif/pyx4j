/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader.policy.subpreloaders;

import java.util.EnumSet;
import java.util.Set;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdAssignmentType;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;
import com.propertyvista.shared.config.VistaFeatures;

public class IdAssignmentPolicyPreloader extends AbstractPolicyPreloader<IdAssignmentPolicy> {

    private final Set<IdTarget> yardiIntegrationAssignedId = EnumSet.of(IdTarget.propertyCode, IdTarget.lease);

    public IdAssignmentPolicyPreloader() {
        super(IdAssignmentPolicy.class);
    }

    @Override
    protected IdAssignmentPolicy createPolicy(StringBuilder log) {
        IdAssignmentPolicy policy = EntityFactory.create(IdAssignmentPolicy.class);
        for (IdTarget target : IdTarget.values()) {
            if (target == IdTarget.accountNumber) {
                continue;
            }

            IdAssignmentItem item = EntityFactory.create(IdAssignmentItem.class);
            item.target().setValue(target);

            if (VistaFeatures.instance().yardiIntegration() && (yardiIntegrationAssignedId.contains(target))) {
                item.type().setValue(IdAssignmentType.userAssigned);
            } else {
                item.type().setValue(IdAssignmentType.generatedNumber);
            }

            policy.items().add(item);
        }

        log.append(policy.getStringView());

        return policy;
    }
}
