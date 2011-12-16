/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 16, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.policy;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.policy.PolicyManagerService;
import com.propertyvista.domain.policy.Policy;
import com.propertyvista.domain.policy.PolicyToNodeMap;
import com.propertyvista.domain.policy.PolicyToNodeMap.NodeType;

public class PolicyManagerServiceImpl implements PolicyManagerService {

    @Override
    public void getUnitPolicy(AsyncCallback<Policy> callback, Key unitPk, Policy policyProto) {
//        Class<? extends IEntity> clazz = policyProto.cast().getInstanceValueClass();
//        Policy foo = (Policy) EntityFactory.create(clazz);
//        foo.foo().setValue(foo.getInstanceValueClass().equals(FooPolicy.class) ? "foo" : "moo");
//        callback.onSuccess(foo);
        Policy policy = getPolicy(unitPk, NodeType.unit, policyProto);
    }

    private Policy getPolicy(Key pk, PolicyToNodeMap.NodeType nodeType, Policy policyProto) {
        EntityQueryCriteria<PolicyToNodeMap> criteria = new EntityQueryCriteria<PolicyToNodeMap>(PolicyToNodeMap.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo(), pk));
        criteria.add(PropertyCriterion.eq(criteria.proto().nodeType(), nodeType));
        List<PolicyToNodeMap> policyToNodeMap = Persistence.service().query(criteria);
        if (policyToNodeMap.isEmpty()) {
            // TODO throw exception here;
        }
        if (policyToNodeMap.size() > 1) {
            // TODO throw exception here: database is broken
        }
        if (!policyToNodeMap.get(0).policyPreset().policies().isNull()) {
            Class<? extends IEntity> requestedPolicyClass = policyProto.cast().getInstanceValueClass();
            for (Policy policy : policyToNodeMap.get(0).policyPreset().policies()) {
                if (policy.cast().getInstanceValueClass().equals(requestedPolicyClass)) {
                    return policy;
                }
            }
        }
        return null;
    }
}
