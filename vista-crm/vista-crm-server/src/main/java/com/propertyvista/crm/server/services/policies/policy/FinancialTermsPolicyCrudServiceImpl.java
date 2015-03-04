/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 3, 2015
 * @author VladL
 */
package com.propertyvista.crm.server.services.policies.policy;

import com.propertyvista.crm.server.services.policies.GenericPolicyCrudService;
import com.propertyvista.domain.policy.dto.FinancialTermsPolicyDTO;
import com.propertyvista.domain.policy.policies.FinancialTermsPolicy;

public class FinancialTermsPolicyCrudServiceImpl extends GenericPolicyCrudService<FinancialTermsPolicy, FinancialTermsPolicyDTO> {

    public FinancialTermsPolicyCrudServiceImpl() {
        super(FinancialTermsPolicy.class, FinancialTermsPolicyDTO.class);
    }

    @Override
    protected FinancialTermsPolicyDTO init(InitializationData initializationData) {
        throw new IllegalArgumentException();
    }

}
