/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-04
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.policy;

import java.util.List;

import com.pyx4j.entity.core.IEntity;

import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.framework.PolicyNode;

public interface PolicyFacade {

    <POLICY extends Policy> POLICY obtainEffectivePolicy(PolicyNode node, final Class<POLICY> policyClass);

    <POLICY extends Policy> POLICY obtainHierarchicalEffectivePolicy(IEntity entity, final Class<POLICY> policyClass);

    <T extends PolicyNode> List<T> getGovernedNodesOfType(Policy policy, Class<T> nodeType);

    public void resetPolicyCache();

}
