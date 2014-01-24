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
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.policy.framework.Policy;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.CustomerScreening;

public class PolicyFacadeImpl implements PolicyFacade {

    @Override
    public <POLICY extends Policy> POLICY obtainEffectivePolicy(PolicyNode node, final Class<POLICY> policyClass) {
        return PolicyManager.obtainEffectivePolicy(node, policyClass);
    }

    @Override
    public <POLICY extends Policy> POLICY obtainHierarchicalEffectivePolicy(IEntity entity, Class<POLICY> policyClass) {
        // Find Object hierarchy, Like in BreadcrumbsHelper
        PolicyNode node = null;
        // Special case for not business owned
        if (entity instanceof CustomerScreening) {
            // TODO Find  LeaseTerm that have application
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().units().$()._Leases().$().leaseParticipants().$().customer().personScreening(), entity));
// TODO VladS : (DEV:)Sort by collections is unsupported, Throwable class java.lang.Error
//            criteria.desc(criteria.proto().units().$()._Leases().$().updated());
            node = Persistence.service().retrieve(criteria);
        } else {
            // TODO use the same code as in BreadcrumbsHelper
            throw new IllegalArgumentException("TODO take a code from BreadcrumbsHelper and find fist PolicyNode in object hierarchy");
        }

        return PolicyManager.obtainEffectivePolicy(node, policyClass);
    }

    @Override
    public <T extends PolicyNode> List<T> getGovernedNodesOfType(Policy policy, Class<T> nodeType) {
        return PolicyManager.descendantsOf(policy.node(), nodeType);
    }

    @Override
    public void resetPolicyCache() {
        PolicyManager.resetPolicyCache();
    }

}
