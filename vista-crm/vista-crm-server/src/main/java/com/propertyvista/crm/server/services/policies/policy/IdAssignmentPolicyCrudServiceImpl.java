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

import java.util.ArrayList;
import java.util.List;

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
    protected void enhanceRetrieved(IdAssignmentPolicy entity, IdAssignmentPolicyDTO dto, RetrieveTarget retrieveTarget ) {
        // tune up UI items in case of YardyInegration mode:
        if (VistaFeatures.instance().yardiIntegration()) {
            for (IdAssignmentItem item : entity.items()) {
                // filter out these IDs!..
                if (!IdTarget.nonEditableWhenYardyIntergation().contains(item.target().getValue())) {
                    dto.editableItems().add(item);
                }
            }
        } else {
            dto.editableItems().addAll(entity.items());
        }
    }

    @Override
    protected void persist(IdAssignmentPolicy dbo, IdAssignmentPolicyDTO in) {
        List<IdAssignmentItem> newItemsList = new ArrayList<IdAssignmentItem>();
        newItemsList.addAll(in.editableItems());

        // Append to list items removed for UI editing
        if (VistaFeatures.instance().yardiIntegration()) {
            for (IdAssignmentItem item : dbo.items()) {
                // filter out these IDs!..
                if (IdTarget.nonEditableWhenYardyIntergation().contains(item.target().getValue())) {
                    newItemsList.add(item);
                }
            }
        }

        dbo.items().clear();
        dbo.items().addAll(newItemsList);

        super.persist(dbo, in);
    }
}
