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

import com.propertyvista.crm.rpc.services.policies.policy.LeaseBillingPolicyCrudService;
import com.propertyvista.crm.server.services.policies.GenericPolicyCrudService;
import com.propertyvista.domain.policy.dto.LeaseBillingPolicyDTO;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;

public class LeaseBillingPolicyCrudServiceImpl extends GenericPolicyCrudService<LeaseBillingPolicy, LeaseBillingPolicyDTO> implements
        LeaseBillingPolicyCrudService {

    public LeaseBillingPolicyCrudServiceImpl() {
        super(LeaseBillingPolicy.class, LeaseBillingPolicyDTO.class);
    }
}
