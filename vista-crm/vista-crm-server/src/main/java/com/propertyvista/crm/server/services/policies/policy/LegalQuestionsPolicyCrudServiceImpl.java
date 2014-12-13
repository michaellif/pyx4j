/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 12, 2014
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.server.services.policies.policy;

import com.propertyvista.crm.rpc.services.policies.policy.LegalQuestionsPolicyCrudService;
import com.propertyvista.crm.server.services.policies.GenericPolicyCrudService;
import com.propertyvista.domain.policy.dto.LegalQuestionsPolicyDTO;
import com.propertyvista.domain.policy.policies.domain.LegalQuestionsPolicy;

public class LegalQuestionsPolicyCrudServiceImpl extends GenericPolicyCrudService<LegalQuestionsPolicy, LegalQuestionsPolicyDTO> implements
        LegalQuestionsPolicyCrudService {

    public LegalQuestionsPolicyCrudServiceImpl() {
        super(LegalQuestionsPolicy.class, LegalQuestionsPolicyDTO.class);
    }
}
