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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.crm.rpc.services.policy.PolicyManagerService;
import com.propertyvista.crm.server.util.PolicyManager;
import com.propertyvista.domain.policy.NodeType;
import com.propertyvista.domain.policy.Policy;
import com.propertyvista.domain.policy.dto.EffectivePolicyDTO;
import com.propertyvista.domain.policy.dto.EffectivePolicyPresetDTO;

public class PolicyManagerServiceImpl implements PolicyManagerService {

    @Override
    public void effectivePolicies(AsyncCallback<EffectivePolicyPresetDTO> callback, IEntity node) {
        NodeType nodeType = PolicyManager.entityToNodeType(node);
        Key pk = node != null ? node.getPrimaryKey() : null;

        callback.onSuccess(PolicyManager.computeEffectivePolicyPreset(pk, nodeType));
    }

    @Override
    public void effectivePolicy(AsyncCallback<Policy> callback, IEntity node, Policy policyProto) {
        assert node != null : "A valid node of the Oranization Hierarchy must be provided";
        assert policyProto != null : "A policyProto must be provided";

        Key pk = node.getPrimaryKey();
        NodeType nodeType = PolicyManager.entityToNodeType(node);
        if (nodeType == null) {
            callback.onFailure(new Error("Unknown node or the provided entity does not represent a node in policy hierarchy: " + node.getClass().getName()));
        }

        EffectivePolicyPresetDTO preset = PolicyManager.computeEffectivePolicyPreset(pk, nodeType);
        for (EffectivePolicyDTO policy : preset.effectivePolicies()) {
            policy.policy().getInstanceValueClass().equals(policyProto.getInstanceValueClass());
            callback.onSuccess(policy.policy());
        }

        callback.onFailure(new Error("Unfortunately the requested policy (" + policyProto.getInstanceValueClass().getClass().getName()
                + ") is not available in the Default Policy Preset"));
    }
}
