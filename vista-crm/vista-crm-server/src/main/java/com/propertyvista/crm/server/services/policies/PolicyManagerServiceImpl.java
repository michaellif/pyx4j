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
package com.propertyvista.crm.server.services.policies;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.crm.rpc.services.policies.PolicyManagerService;
import com.propertyvista.domain.policy.framework.EffectivePoliciesDTO;
import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.server.common.policy.PolicyManager;

public class PolicyManagerServiceImpl implements PolicyManagerService {

    @Override
    public void effectivePolicies(AsyncCallback<EffectivePoliciesDTO> callback, PolicyNode node) {
        EffectivePoliciesDTO dto = EntityFactory.create(EffectivePoliciesDTO.class);
        dto.policies().addAll(PolicyManager.effectivePolicies(node));
        callback.onSuccess(dto);
    }

    @Override
    public void effectivePolicy(AsyncCallback<Policy> callback, PolicyNode node, Policy policyProto) {
        if (policyProto == null) {
            throw new Error("A policy prototype was not provided");
        }

        @SuppressWarnings("unchecked")
        Policy policy = PolicyManager.effectivePolicy(node, (Class<? extends Policy>) policyProto.getInstanceValueClass());
        if (policy != null) {
            callback.onSuccess(policy);
        } else {
            callback.onFailure(new Error("Unfortunately the requested policy (" + policyProto.getInstanceValueClass().getClass().getName()
                    + ") was not found in the Default Policy Preset"));
        }
    }
}
