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

import com.propertyvista.crm.server.services.policies.GenericPolicyCrudService;
import com.propertyvista.domain.policy.dto.LegalTermsPolicyDTO;
import com.propertyvista.domain.policy.policies.LegalTermsPolicy;

public class LegalDocumentationPolicyCrudServiceImpl extends GenericPolicyCrudService<LegalTermsPolicy, LegalTermsPolicyDTO> {

    public LegalDocumentationPolicyCrudServiceImpl() {
        super(LegalTermsPolicy.class, LegalTermsPolicyDTO.class);
    }

    @Override
    protected LegalTermsPolicyDTO init(InitializationData initializationData) {
        throw new IllegalArgumentException();
    }

}
