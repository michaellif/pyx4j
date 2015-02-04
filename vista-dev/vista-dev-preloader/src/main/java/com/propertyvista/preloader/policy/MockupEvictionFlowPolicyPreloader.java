/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 3, 2015
 * @author stanp
 */
package com.propertyvista.preloader.policy;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.preloader.policy.AbstractPolicyPreloader;
import com.propertyvista.domain.policy.policies.EvictionFlowPolicy;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep;
import com.propertyvista.domain.policy.policies.domain.EvictionFlowStep.EvictionStepType;

public class MockupEvictionFlowPolicyPreloader extends AbstractPolicyPreloader<EvictionFlowPolicy> {

    public MockupEvictionFlowPolicyPreloader() {
        super(EvictionFlowPolicy.class);
    }

    @Override
    protected EvictionFlowPolicy createPolicy(StringBuilder log) {
        EvictionFlowPolicy policy = EntityFactory.create(EvictionFlowPolicy.class);

        addStep(policy, EvictionFlowStep.EvictionStepType.N4);
        addStep(policy, EvictionFlowStep.EvictionStepType.L1);
        addStep(policy, EvictionFlowStep.EvictionStepType.HearingDate);
        addStep(policy, EvictionFlowStep.EvictionStepType.Order);

        log.append(policy.getStringView());

        return policy;
    }

    private void addStep(EvictionFlowPolicy policy, EvictionStepType type) {
        EvictionFlowStep step = policy.evictionFlow().$();
        step.stepType().setValue(type);
        step.name().setValue(type.toString());
        policy.evictionFlow().add(step);
    }
}
