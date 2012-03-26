/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 25, 2012
 * @author igor
 * @version $Id$
 */
package com.propertyvista.crm.server.util;

import java.security.InvalidParameterException;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdAssignmentType;
import com.propertyvista.server.common.policy.PolicyManager;
import com.propertyvista.server.domain.IdAssignmentSequence;

public class IdAssignmentSequenceUtil {

    private static char[] codes = { '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

    public static boolean needsGeneratedId(IdAssignmentItem.IdTarget target) {
        IdAssignmentPolicy policy = PolicyManager.obtainEffectivePolicy(EntityFactory.create(OrganizationPoliciesNode.class), IdAssignmentPolicy.class);

        if (policy == null) {
            throw new InvalidParameterException("Node OrganizationPoliciesNode has no : " + IdAssignmentPolicy.TO_STRING_ATTR + " assigned");
        }

        IdAssignmentItem targetItem = null;
        for (IdAssignmentItem item : policy.itmes()) {
            if (item.target().getValue() == target) {
                targetItem = item;

                break;
            }
        }

        if (targetItem == null) {
            throw new InvalidParameterException("No item for target: " + target.toString());
        }

        return targetItem.type().getValue() == IdAssignmentType.generatedNumber || targetItem.type().getValue() == IdAssignmentType.generatedAlphaNumeric;
    }

    public static String getId(IdAssignmentItem.IdTarget target) {
        IdAssignmentPolicy policy = PolicyManager.obtainEffectivePolicy(EntityFactory.create(OrganizationPoliciesNode.class), IdAssignmentPolicy.class);

        if (policy == null) {
            throw new InvalidParameterException("Node OrganizationPoliciesNode has no : " + IdAssignmentPolicy.TO_STRING_ATTR + " assigned");
        }

        IdAssignmentItem targetItem = null;
        for (IdAssignmentItem item : policy.itmes()) {
            if (item.target().getValue() == target) {
                targetItem = item;

                break;
            }
        }

        if (targetItem == null) {
            throw new InvalidParameterException("No item for target: " + target.toString());
        }

        EntityQueryCriteria<IdAssignmentSequence> criteria = EntityQueryCriteria.create(IdAssignmentSequence.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().target(), target));
        IdAssignmentSequence sequence = Persistence.service().retrieve(criteria);

        if (sequence == null) {
            sequence = EntityFactory.create(IdAssignmentSequence.class);
            sequence.target().setValue(target);
            sequence.number().setValue(new Long(0));
        }

        long id = sequence.number().getValue() + 1;
        sequence.number().setValue(id);
        Persistence.service().persist(sequence);

        String res = "";

        if (targetItem.type().getValue() == IdAssignmentType.generatedNumber) {
            res = new Long(id).toString();
        } else if (targetItem.type().getValue() == IdAssignmentType.generatedAlphaNumeric) {
            StringBuffer buf = new StringBuffer();
            generatedAlphabetical(buf, id);

            res = buf.toString();
        }

        return res;
    }

    private static void generatedAlphabetical(final StringBuffer res, long val) {
        long div = val / codes.length;
        long mod = val % codes.length;

        if (val > codes.length) {
            generatedAlphabetical(res, mod == 0 ? div - 1 : div);
        }

        if (val == codes.length || mod == 0) {
            res.append(codes[codes.length - 1]);
        } else {
            res.append(codes[(int) mod - 1]);
        }
    }

}
