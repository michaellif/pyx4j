/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.customer;

import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.crm.rpc.services.customer.GuarantorCrudService;
import com.propertyvista.domain.policy.policies.RestrictionsPolicy;
import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.dto.GuarantorDTO;

public class GuarantorCrudServiceImpl extends LeaseParticipantCrudServiceBaseImpl<Guarantor, GuarantorDTO> implements GuarantorCrudService {

    public GuarantorCrudServiceImpl() {
        super(Guarantor.class, GuarantorDTO.class);
    }

    @Override
    protected void enhanceRetrieved(Guarantor bo, GuarantorDTO to, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(bo, to, retrieveTarget);

        if (retrieveTarget == RetrieveTarget.Edit) {
            RestrictionsPolicy restrictionsPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(bo.lease().unit(),
                    RestrictionsPolicy.class);
            if (restrictionsPolicy.enforceAgeOfMajority().isBooleanTrue()) {
                to.ageOfMajority().setValue(restrictionsPolicy.ageOfMajority().getValue());
            }
        }

    }
}
