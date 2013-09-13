/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 13, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.server.services.policies.policy;

import com.propertyvista.crm.rpc.services.policies.policy.ILSPolicyCrudService;
import com.propertyvista.crm.server.services.policies.GenericPolicyCrudService;
import com.propertyvista.domain.policy.dto.ILSPolicyDTO;
import com.propertyvista.domain.policy.policies.ILSPolicy;

public class ILSPolicyCrudServiceImpl extends GenericPolicyCrudService<ILSPolicy, ILSPolicyDTO> implements ILSPolicyCrudService {

    public ILSPolicyCrudServiceImpl() {
        super(ILSPolicy.class, ILSPolicyDTO.class);
    }

}
