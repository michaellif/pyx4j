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

public class LeaseTermsPolicyCrudServiceImpl extends GenericPolicyCrudService<LeaseTermsPolicy, LeaseTermsPolicyDTO> {

    public LeaseTermsPolicyCrudServiceImpl() {
        super(LeaseTermsPolicy.class, LeaseTermsPolicyDTO.class);
    }

    @Override
    protected void persistDBO(LeaseTermsPolicy dbo, LeaseTermsPolicyDTO in) {
        // FIXME remove these printlns after its clear what's wrong
        System.out.println(dbo.summaryTerms().get(0).content());
        System.out.println(dbo.oneTimePaymentTerms().content());
        System.out.println(dbo.recurrentPaymentTerms().content());
        super.persistDBO(dbo, in);
    }

}
