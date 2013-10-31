/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-10-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.policies.policy;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.crm.rpc.services.policies.policy.N4PolicyCrudService;
import com.propertyvista.crm.server.services.policies.GenericPolicyCrudService;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.policy.dto.N4PolicyDTO;
import com.propertyvista.domain.policy.dto.N4PolicyDTOARCodeHolderDTO;
import com.propertyvista.domain.policy.policies.N4Policy;

public class N4PolicyCrudServiceImpl extends GenericPolicyCrudService<N4Policy, N4PolicyDTO> implements N4PolicyCrudService {

    public N4PolicyCrudServiceImpl() {
        super(N4Policy.class, N4PolicyDTO.class);
    }

    @Override
    protected void save(N4Policy bo, N4PolicyDTO to) {
        bo.relevantARCodes().clear();
        for (N4PolicyDTOARCodeHolderDTO arCodeHolder : to.arCodes()) {
            bo.relevantARCodes().add(arCodeHolder.arCode());
        }
        super.save(bo, to);
    }

    @Override
    protected void enhanceRetrieved(N4Policy bo, N4PolicyDTO to, RetrieveTarget retrieveTarget) {
        to.arCodes().clear();
        for (ARCode arCode : bo.relevantARCodes()) {
            N4PolicyDTOARCodeHolderDTO codeHolder = EntityFactory.create(N4PolicyDTOARCodeHolderDTO.class);
            codeHolder.arCode().set(arCode.duplicate());
            to.arCodes().add(codeHolder);
        }
    }

}
