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

import com.propertyvista.crm.rpc.services.policies.policy.LeaseAdjustmentPolicyCrudService;
import com.propertyvista.crm.server.services.policies.GenericPolicyCrudService;
import com.propertyvista.domain.policy.dto.LeaseAdjustmentPolicyDTO;
import com.propertyvista.domain.policy.policies.LeaseAdjustmentPolicy;

public class LeaseAdjustmentPolicyCrudServiceImpl extends GenericPolicyCrudService<LeaseAdjustmentPolicy, LeaseAdjustmentPolicyDTO> implements
        LeaseAdjustmentPolicyCrudService {

    public LeaseAdjustmentPolicyCrudServiceImpl() {
        super(LeaseAdjustmentPolicy.class, LeaseAdjustmentPolicyDTO.class);
    }

    @Override
    protected void enhanceRetrieved(LeaseAdjustmentPolicy in, LeaseAdjustmentPolicyDTO dto, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(in, dto, retrieveTarget);
        Persistence.service().retrieveMember(in.policyItems());
        dto.policyItems().set(in.policyItems());
    }

}
