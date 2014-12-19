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
 */
package com.propertyvista.crm.server.services.policies.policy;

import com.propertyvista.crm.rpc.services.policies.policy.DepositPolicyCrudService;
import com.propertyvista.crm.server.services.policies.GenericPolicyCrudService;
import com.propertyvista.domain.policy.dto.DepositPolicyDTO;
import com.propertyvista.domain.policy.policies.DepositPolicy;

public class DepositPolicyCrudServiceImpl extends GenericPolicyCrudService<DepositPolicy, DepositPolicyDTO> implements DepositPolicyCrudService {

    public DepositPolicyCrudServiceImpl() {
        super(DepositPolicy.class, DepositPolicyDTO.class);
    }

}
