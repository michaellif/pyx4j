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

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.propertyvista.crm.rpc.services.policy.PolicyManagerService;
import com.propertyvista.domain.policy.Policy;
import com.propertyvista.domain.policy.PolicyAtNode;
import com.propertyvista.domain.policy.PolicyNode;
import com.propertyvista.domain.policy.dto.EffectivePoliciesDTO;
import com.propertyvista.server.common.policy.PolicyManager;

public class PolicyManagerServiceImpl implements PolicyManagerService {

    @Override
    public void effectivePolicies(AsyncCallback<EffectivePoliciesDTO> callback, PolicyNode node) {
        callback.onSuccess(PolicyManager.computeEffectivePolicyPreset(node));
    }

    @Override
    public void effectivePolicy(AsyncCallback<Policy> callback, PolicyNode node, Policy policyProto) {
        assert policyProto != null : "A policyProto must be provided";

        EffectivePoliciesDTO effectivePolicies = PolicyManager.computeEffectivePolicyPreset(node);
        for (PolicyAtNode policyAtNode : effectivePolicies.policies()) {
            policyAtNode.policy().getInstanceValueClass().equals(policyProto.getInstanceValueClass());
            callback.onSuccess(policyAtNode.policy());
        }

        callback.onFailure(new Error("Unfortunately the requested policy (" + policyProto.getInstanceValueClass().getClass().getName()
                + ") was not found in the Default Policy Preset"));
    }
}
