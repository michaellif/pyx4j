/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.policies.policy;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;

import com.propertyvista.crm.rpc.services.policies.policy.ProductTaxPolicyCrudService;
import com.propertyvista.crm.server.services.policies.GenericPolicyCrudService;
import com.propertyvista.domain.policy.dto.ProductTaxPolicyDTO;
import com.propertyvista.domain.policy.policies.ProductTaxPolicy;

public class ProductTaxPolicyCrudServiceImpl extends GenericPolicyCrudService<ProductTaxPolicy, ProductTaxPolicyDTO> implements ProductTaxPolicyCrudService {

    public ProductTaxPolicyCrudServiceImpl() {
        super(ProductTaxPolicy.class, ProductTaxPolicyDTO.class);
    }

    @Override
    protected void enhanceDTO(ProductTaxPolicy in, ProductTaxPolicyDTO dto, boolean fromList) {
        super.enhanceDTO(in, dto, fromList);

        if (!fromList) {
            Persistence.service().retrieveMember(in.policyItems());
            dto.policyItems().setAttachLevel(AttachLevel.Attached);
            dto.policyItems().addAll(in.policyItems());
        }
    }

    @Override
    protected void persistDBO(ProductTaxPolicy dbo, ProductTaxPolicyDTO in) {
        // TODO Auto-generated method stub
        super.persistDBO(dbo, in);
    }
}
