/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 14, 2015
 * @author VladL
 */
package com.propertyvista.crm.server.services.policies.policy;

import com.propertyvista.crm.rpc.services.policies.policy.ApplicationApprovalChecklistPolicyCrudService;
import com.propertyvista.crm.server.services.policies.GenericPolicyCrudService;
import com.propertyvista.domain.policy.dto.ApplicationApprovalChecklistPolicyDTO;
import com.propertyvista.domain.policy.policies.ApplicationApprovalChecklistPolicy;

public class ApplicationApprovalChecklistPolicyCrudServiceImpl extends
        GenericPolicyCrudService<ApplicationApprovalChecklistPolicy, ApplicationApprovalChecklistPolicyDTO> implements
        ApplicationApprovalChecklistPolicyCrudService {

    public ApplicationApprovalChecklistPolicyCrudServiceImpl() {
        super(ApplicationApprovalChecklistPolicy.class, ApplicationApprovalChecklistPolicyDTO.class);
    }
}
