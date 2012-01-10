/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 23, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.policies;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityGraph;

import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.policy.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.Policy;
import com.propertyvista.domain.policy.PolicyAtNode;
import com.propertyvista.domain.policy.PolicyNode;
import com.propertyvista.domain.policy.dto.PolicyDTOBase;

public abstract class GenericPolicyCrudService<POLICY extends Policy, POLICY_DTO extends POLICY> extends GenericCrudServiceDtoImpl<POLICY, POLICY_DTO> {

    public GenericPolicyCrudService(Class<POLICY> dboClass, Class<POLICY_DTO> dtoClass) {
        super(dboClass, dtoClass);

        // TODO check how multiple parametrized inheritance may be acheived (i need "POLICY_DTO extends POLICY & PolicyDTOBase (maybe PolicyDTOBase should extend Policy") 
        if (!PolicyDTOBase.class.isAssignableFrom(dtoClass)) {
            throw new Error("POLICY_DTO must extends PolicyDTOBase");
        }
    }

    @Override
    protected void enhanceDTO(POLICY in, POLICY_DTO dto, boolean fromList) {
        super.enhanceDTO(in, dto, fromList);

        EntityQueryCriteria<PolicyAtNode> criteria = new EntityQueryCriteria<PolicyAtNode>(PolicyAtNode.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().policy(), in));
        PolicyAtNode policyAtNode = Persistence.service().retrieve(criteria);

        if (policyAtNode == null) {
            throw new Error("DB integrity error: policy instance doesn't have associated node");
        }

        ((PolicyDTOBase) dto).node().set(policyAtNode.node().cast());

        if (fromList) {
            PolicyNode castedNode = ((PolicyDTOBase) dto).node().cast();
            ((PolicyDTOBase) dto).nodeType().setValue(castedNode.getEntityMeta().getCaption());
            ((PolicyDTOBase) dto).nodeRepresentation().setValue(castedNode.getStringView());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void persistDBO(POLICY dbo, POLICY_DTO in) {
        PolicyNode node = ((PolicyDTOBase) in).node().cast();

        if (node.getInstanceValueClass().equals(OrganizationPoliciesNode.class)) {
            // since there should be only one organizational policy node, we don't expect from the client to give us a persisted entity with a PK
            // and do it ourselves
            node = Persistence.service().retrieve(new EntityQueryCriteria<OrganizationPoliciesNode>(OrganizationPoliciesNode.class));
            if (node == null) {
                throw new Error("failed to retrieve instance of " + OrganizationPoliciesNode.class.getSimpleName() + " from the DB");
            }
        }
        if (node.isNull() || node.id().isNull()) {
            throw new Error("unable to persist policy, the scope (a node in organizatonal hierarchy) was not set");
        }

        POLICY policy = null;
        if (!isNewPolicy(dbo)) {
            policy = EntityGraph.businessDuplicate(dbo);
        } else {
            policy = dbo;
        }

        EntityQueryCriteria<PolicyAtNode> criteria = new EntityQueryCriteria<PolicyAtNode>(PolicyAtNode.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().node(), node));
        criteria.add(PropertyCriterion.eq(criteria.proto().policy(), dboClass));
        PolicyAtNode policyAtNode = Persistence.service().retrieve(criteria);

        if (policyAtNode != null) {
            // override, i.e. delete the old policy that was at that node
            // TODO review this: maybe it's better to forbid such an action on server and populate the form with the existing policy when the user selects a scope with a policy that actually exists            
            Persistence.service().delete(policyAtNode.policy().cast());
        } else {
            policyAtNode = EntityFactory.create(PolicyAtNode.class);
            policyAtNode.node().set(node);
        }
        policyAtNode.policy().set(policy);

        super.persistDBO(policy, in);
        Persistence.service().merge(policyAtNode);

        // A hack to return the correct result back to the client (because java has no C++ references)
        if (policy != dbo) {
            dbo.id().set(policy.id());
            for (String member : policy.getEntityMeta().getMemberNames()) {
                dbo.set(dbo.getMember(member), policy.getMember(member));
            }
        }
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        EntityQueryCriteria<PolicyAtNode> criteria = new EntityQueryCriteria<PolicyAtNode>(PolicyAtNode.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().policy(), dboClass));

        Policy deletedNodeGhost = EntityFactory.create(dboClass);
        deletedNodeGhost.id().setValue(entityId);
        criteria.add(PropertyCriterion.eq(criteria.proto().policy(), deletedNodeGhost));
        Persistence.service().delete(criteria);

        super.delete(callback, entityId);
    }

    private boolean isNewPolicy(POLICY policy) {
        return policy.id().isNull();
    }
}
