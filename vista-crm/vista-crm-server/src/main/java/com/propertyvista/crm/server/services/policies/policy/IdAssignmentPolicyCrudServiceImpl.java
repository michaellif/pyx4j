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

import java.util.Iterator;

import com.propertyvista.crm.rpc.services.policies.policy.IdAssignmentPolicyCrudService;
import com.propertyvista.crm.server.services.policies.GenericPolicyCrudService;
import com.propertyvista.domain.policy.dto.IdAssignmentPolicyDTO;
import com.propertyvista.domain.policy.policies.IdAssignmentPolicy;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.shared.config.VistaFeatures;

public class IdAssignmentPolicyCrudServiceImpl extends GenericPolicyCrudService<IdAssignmentPolicy, IdAssignmentPolicyDTO> implements
        IdAssignmentPolicyCrudService {

    public IdAssignmentPolicyCrudServiceImpl() {
        super(IdAssignmentPolicy.class, IdAssignmentPolicyDTO.class);
    }

    @Override
    protected void enhanceRetrieved(IdAssignmentPolicy entity, IdAssignmentPolicyDTO dto, RetrieveTraget retrieveTraget) {

        // tune up UI items in case of YardyInegration mode:
        if (VistaFeatures.instance().yardiIntegration()) {
            Iterator<IdAssignmentItem> it = dto.items().iterator();
            while (it.hasNext()) {
                if (IdTarget.nonEditableWhenYardyIntergation().contains(it.next().target().getValue())) {
                    it.remove(); // filter out these IDs!..
                }
            }
        }
    }
}
