/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 3, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.policies.policy;

import com.pyx4j.commons.UserRuntimeException;

import com.propertyvista.crm.rpc.services.policies.policy.ApplicationDocumentationPolicyCrudService;
import com.propertyvista.crm.server.services.policies.GenericPolicyCrudService;
import com.propertyvista.domain.policy.dto.ApplicationDocumentationPolicyDTO;
import com.propertyvista.domain.policy.policies.ApplicationDocumentationPolicy;

public class ApplicationDocumentationPolicyCrudServiceImpl extends GenericPolicyCrudService<ApplicationDocumentationPolicy, ApplicationDocumentationPolicyDTO>
        implements ApplicationDocumentationPolicyCrudService {

    public ApplicationDocumentationPolicyCrudServiceImpl() {
        super(ApplicationDocumentationPolicy.class, ApplicationDocumentationPolicyDTO.class);
    }

    @Override
    protected void persist(ApplicationDocumentationPolicy dbo, ApplicationDocumentationPolicyDTO in) {
        if (dbo.allowedIDs().isNull() || dbo.allowedIDs().isEmpty()) {
            throw new UserRuntimeException("At least one kind of allowed ID is required");
        }
        if (dbo.numberOfRequiredIDs().isNull() || dbo.numberOfRequiredIDs().getValue() < 1) {
            throw new UserRuntimeException("The number of required IDs must be a positive integer");
        }
        if (dbo.numberOfRequiredIDs().getValue() > dbo.allowedIDs().size()) {
            throw new UserRuntimeException("The number of required IDs must not exceed the number of allowed IDs");
        }
        super.persist(dbo, in);
    }

}
