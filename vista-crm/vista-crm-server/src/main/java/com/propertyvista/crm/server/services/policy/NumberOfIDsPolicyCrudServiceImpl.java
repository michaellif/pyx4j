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
package com.propertyvista.crm.server.services.policy;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.policy.NumberOfIDsPolicyCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
import com.propertyvista.domain.policy.DefaultPoliciesNode;
import com.propertyvista.domain.policy.PolicyAtNode;
import com.propertyvista.domain.policy.PolicyNode;
import com.propertyvista.domain.policy.dto.NumberOfIDsPolicyDTO;
import com.propertyvista.domain.policy.policies.NumberOfIDsPolicy;

public class NumberOfIDsPolicyCrudServiceImpl extends GenericCrudServiceDtoImpl<NumberOfIDsPolicy, NumberOfIDsPolicyDTO> implements
        NumberOfIDsPolicyCrudService {

    public NumberOfIDsPolicyCrudServiceImpl() {
        super(NumberOfIDsPolicy.class, NumberOfIDsPolicyDTO.class);
    }

    @Override
    protected void enhanceDTO(NumberOfIDsPolicy in, NumberOfIDsPolicyDTO dto, boolean fromList) {
        super.enhanceDTO(in, dto, fromList);

        EntityQueryCriteria<PolicyAtNode> criteria = new EntityQueryCriteria<PolicyAtNode>(PolicyAtNode.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().policy(), in));
        PolicyAtNode policyAtNode = Persistence.service().retrieve(criteria);
        assert policyAtNode != null : "Each policy instance has to have one and only one associated node";
        dto.node().set(policyAtNode.node());

        if (fromList) {
            PolicyNode castedNode = dto.node().cast();
            dto.nodeType().setValue(castedNode.getEntityMeta().getCaption());
            dto.nodeRepresentation().setValue(castedNode.getStringView());
        }
    }
    
    @Override
    protected void persistDBO(NumberOfIDsPolicy dbo, NumberOfIDsPolicyDTO in) {    	
    	if (in.node().isNull() | in.node().getPrimaryKey() == null) {
    		throw new Error("failed to persist policy! policy scope (a node in organizaton structure) was not set");
    	}
    	if (in.node().getInstanceValueClass().equals(DefaultPoliciesNode.class)) {
    		throw new Error("overriding default policies is strictly forbidden");
    	}

    	
    	EntityQueryCriteria<PolicyAtNode> policyAtNodeCriteria = new EntityQueryCriteria<PolicyAtNode>(PolicyAtNode.class);
    	policyAtNodeCriteria.add(PropertyCriterion.eq(policyAtNodeCriteria.proto().node(), in.node()));
    	PolicyAtNode policyAtNode = Persistence.service().retrieve(policyAtNodeCriteria);
    	if (policyAtNode == null) {
    		policyAtNode = EntityFactory.create(PolicyAtNode.class);
    		policyAtNode.node().set(in.node());
    		
    		// if someone has reassigned an existing policy to another node,
    		// we have to remove the old policyAtNode record unless it's a default policy
    		// if it is a default policy we just make a copy of that policy.
        	if (dbo.getPrimaryKey() != null) {
        		EntityQueryCriteria<PolicyAtNode> criteria = new EntityQueryCriteria<PolicyAtNode>(PolicyAtNode.class);
        		criteria.add(PropertyCriterion.eq(criteria.proto().policy(), dbo.getPrimaryKey()));
        		PolicyAtNode oldPolicyAtNode = Persistence.service().retrieve(criteria);
        		if (oldPolicyAtNode == null) {
        			throw new Error("Old policyAtNode was not found, something bad happened!");
        		}
        		if (oldPolicyAtNode.policy().getInstanceValueClass().equals(DefaultPoliciesNode.class)) {
        			dbo.setPrimaryKey(null); // make a copy
        		} else {
        			Persistence.service().delete(oldPolicyAtNode);
        		}
        	}
    	} else {
    		// this node already had a policy, we have to remove it because if we are not updating a policy,
    		// but creating a new one, otherwise we could make a policy that does not belong anywhere
    		Persistence.service().delete(NumberOfIDsPolicy.class, policyAtNode.policy().getPrimaryKey());
    	}
    
    	policyAtNode.policy().set(dbo);
    	super.persistDBO(dbo, in);    	
    	Persistence.service().merge(policyAtNode);
    }
}
