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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.policy.NumberOfIDsPolicyCrudService;
import com.propertyvista.crm.server.util.GenericCrudServiceDtoImpl;
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
}
