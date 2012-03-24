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

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdAssignmentType;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.portal.server.preloader.policy.util.AbstractPolicyPreloader;

public class IdAssignmentPolicyPreloader extends AbstractPolicyPreloader<IdAssignmentPolicy> {

    private final static I18n i18n = I18n.get(IdAssignmentPolicyPreloader.class);

    public IdAssignmentPolicyPreloader() {
        super(IdAssignmentPolicy.class);
    }

    @Override
    protected IdAssignmentPolicy createPolicy(StringBuilder log) {
        IdAssignmentPolicy policy = EntityFactory.create(IdAssignmentPolicy.class);

        IdAssignmentItem item = null;

        for (IdTarget target : IdTarget.values()) {
            item = EntityFactory.create(IdAssignmentItem.class);

            item.target().setValue(target);
            item.type().setValue(IdAssignmentType.generatedNumber);

            policy.itmes().add(item);

        }

        log.append(policy.getStringView());

        return policy;
    }
}
