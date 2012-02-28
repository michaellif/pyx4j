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
import com.propertyvista.domain.policy.dto.LeaseTermsPolicyDTO;
import com.propertyvista.domain.policy.policies.LeaseTermsPolicy;
import com.propertyvista.domain.policy.policies.domain.LegalTermsDescriptor;

public class LeaseTermsPolicyCrudServiceImpl extends GenericPolicyCrudService<LeaseTermsPolicy, LeaseTermsPolicyDTO> {

    public LeaseTermsPolicyCrudServiceImpl() {
        super(LeaseTermsPolicy.class, LeaseTermsPolicyDTO.class);
    }

    @Override
    protected void persistDBO(LeaseTermsPolicy dbo, LeaseTermsPolicyDTO in) {
        StringBuffer errors = new StringBuffer();
        if (!isValid(in, errors)) {
            throw new Error(errors.toString());
        }
        super.persistDBO(dbo, in);
    }

    private static boolean isValid(LeaseTermsPolicyDTO in, StringBuffer errors) {
        boolean isValid = true;
        if (!(isValid &= !in.tenantSummaryTerms().isEmpty())) {
            errors.append("Summary terms list must not be empty; ");
        } else {
            for (LegalTermsDescriptor terms : in.tenantSummaryTerms()) {
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
