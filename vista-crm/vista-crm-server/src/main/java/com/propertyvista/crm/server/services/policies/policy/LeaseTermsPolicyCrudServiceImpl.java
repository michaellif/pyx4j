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

import com.pyx4j.entity.server.Persistence;

import com.propertyvista.crm.server.services.policies.GenericPolicyCrudService;
import com.propertyvista.domain.policy.dto.LeaseTermsPolicyDTO;
import com.propertyvista.domain.policy.policies.LegalTermsPolicy;
import com.propertyvista.domain.policy.policies.specials.LegalTermsContent;
import com.propertyvista.domain.policy.policies.specials.LegalTermsDescriptor;

public class LeaseTermsPolicyCrudServiceImpl extends GenericPolicyCrudService<LegalTermsPolicy, LeaseTermsPolicyDTO> {

    public LeaseTermsPolicyCrudServiceImpl() {
        super(LegalTermsPolicy.class, LeaseTermsPolicyDTO.class);
    }

    @Override
    protected void persistDBO(LegalTermsPolicy dbo, LeaseTermsPolicyDTO in) {
//        for (LegalTermsDescriptor descriptor : dbo.summaryTerms()) {
//            persistDescriptor(descriptor);
//        }
//        persistDescriptor(dbo.paymentTerms1());
//        persistDescriptor(dbo.paymentTerms2());

        super.persistDBO(dbo, in);
    }

    private void persistDescriptor(LegalTermsDescriptor descriptor) {
        if (!descriptor.isNull()) {
            for (LegalTermsContent content : descriptor.content()) {
                Persistence.service().persist(content);
            }
            Persistence.service().persist(descriptor);
        }
    }

}
