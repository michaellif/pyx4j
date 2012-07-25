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

import com.propertyvista.crm.server.services.policies.GenericPolicyCrudService;
import com.propertyvista.domain.policy.dto.LegalDocumentationPolicyDTO;
import com.propertyvista.domain.policy.policies.LegalDocumentation;
import com.propertyvista.domain.policy.policies.domain.LegalTermsDescriptor;

public class LegalDocumentationPolicyCrudServiceImpl extends GenericPolicyCrudService<LegalDocumentation, LegalDocumentationPolicyDTO> {

    public LegalDocumentationPolicyCrudServiceImpl() {
        super(LegalDocumentation.class, LegalDocumentationPolicyDTO.class);
    }

    @Override
    protected void persist(LegalDocumentation dbo, LegalDocumentationPolicyDTO in) {
        StringBuffer errors = new StringBuffer();
        if (!isValid(in, errors)) {
            throw new UserRuntimeException(errors.toString());
        }
        super.persist(dbo, in);
    }

    private static boolean isValid(LegalDocumentationPolicyDTO in, StringBuffer errors) {
        boolean isValid = true;
        if (!(isValid &= !in.mainApplication().isEmpty())) {
            errors.append("Summary terms list must not be empty; ");
        } else {
            for (LegalTermsDescriptor terms : in.mainApplication()) {
                if (!(isValid &= isValid(terms))) {
                    // TODO add message
                }
            }
        }

        // FIXME finish validation for lease terms
        return isValid;
    }

    private static boolean isValid(LegalTermsDescriptor terms) {
        // FIXME finish this
        return true;
    }
}
