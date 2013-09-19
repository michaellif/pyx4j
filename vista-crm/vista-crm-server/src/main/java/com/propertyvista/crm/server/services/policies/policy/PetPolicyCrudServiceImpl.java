/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 9, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.policies.policy;

import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.crm.rpc.services.policies.policy.PetPolicyCrudService;
import com.propertyvista.crm.server.services.policies.GenericPolicyCrudService;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.policy.dto.PetPolicyDTO;
import com.propertyvista.domain.policy.policies.PetPolicy;
import com.propertyvista.domain.policy.policies.domain.PetConstraints;

public class PetPolicyCrudServiceImpl extends GenericPolicyCrudService<PetPolicy, PetPolicyDTO> implements PetPolicyCrudService {

    public PetPolicyCrudServiceImpl() {
        super(PetPolicy.class, PetPolicyDTO.class);
    }

    @Override
    protected PetPolicyDTO init(InitializationData initializationData) {
        PetPolicyDTO dto = super.init(initializationData);
        attachNewPets(dto);
        return dto;
    }

    @Override
    protected void enhanceRetrieved(PetPolicy in, PetPolicyDTO dto, RetrieveTarget retrieveTarget) {
        super.enhanceRetrieved(in, dto, retrieveTarget);
        attachNewPets(dto);
    }

    private void attachNewPets(PetPolicyDTO policyDTO) {
        EntityQueryCriteria<ARCode> criteria = new EntityQueryCriteria<ARCode>(ARCode.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().type(), ARCode.Type.Pet));
        List<ARCode> availablePets = Persistence.service().query(criteria);

        for (ARCode availablePet : availablePets) {
            if (isNewPet(availablePet, policyDTO)) {
                policyDTO.constraints().add(createNewPetConstraints(availablePet));
            }
        }

    }

    private boolean isNewPet(ARCode pet, PetPolicyDTO dto) {
        for (PetConstraints constraints : dto.constraints()) {
            if (constraints.pet().equals(pet)) {
                return false;
            }
        }
        return true;
    }

    private PetConstraints createNewPetConstraints(ARCode pet) {
        PetConstraints constraints = EntityFactory.create(PetConstraints.class);
        constraints.pet().set(pet);
        constraints.maxNumber().setValue(0);
        constraints.maxWeight().setValue(.0);
        return constraints;
    }

}
