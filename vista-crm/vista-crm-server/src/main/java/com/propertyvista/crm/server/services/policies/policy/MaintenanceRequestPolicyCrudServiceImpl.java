/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 23, 2014
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.server.services.policies.policy;

import com.propertyvista.crm.rpc.services.policies.policy.MaintenanceRequestPolicyCrudService;
import com.propertyvista.crm.server.services.policies.GenericPolicyCrudService;
import com.propertyvista.domain.policy.dto.MaintenanceRequestPolicyDTO;
import com.propertyvista.domain.policy.policies.MaintenanceRequestPolicy;

public class MaintenanceRequestPolicyCrudServiceImpl extends GenericPolicyCrudService<MaintenanceRequestPolicy, MaintenanceRequestPolicyDTO> implements
        MaintenanceRequestPolicyCrudService {

    public MaintenanceRequestPolicyCrudServiceImpl() {
        super(MaintenanceRequestPolicy.class, MaintenanceRequestPolicyDTO.class);
    }

}
