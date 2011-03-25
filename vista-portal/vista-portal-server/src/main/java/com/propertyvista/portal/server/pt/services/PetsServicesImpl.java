/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 10, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.pt.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertyvista.portal.domain.ChargeType;
import com.propertyvista.portal.domain.pt.Pet;
import com.propertyvista.portal.domain.pt.PetChargeRule;
import com.propertyvista.portal.domain.pt.Pets;
import com.propertyvista.portal.rpc.pt.ChargesSharedCalculation;
import com.propertyvista.portal.rpc.pt.services.PetsServices;
import com.propertyvista.portal.server.pt.ChargesServerCalculation;
import com.propertyvista.portal.server.pt.PtAppContext;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

public class PetsServicesImpl extends ApplicationEntityServicesImpl implements PetsServices {
    private final static Logger log = LoggerFactory.getLogger(PetsServicesImpl.class);

    @Override
    public void retrieve(AsyncCallback<Pets> callback, Long tenantId) {
        log.info("Retrieving pets for tenant {}", tenantId);
        EntityQueryCriteria<Pets> criteria = EntityQueryCriteria.create(Pets.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), PtAppContext.getCurrentUserApplication()));
        Pets pets = secureRetrieve(criteria);
        if (pets == null) {
            pets = EntityFactory.create(Pets.class);
        }
        loadTransientData(pets);

        callback.onSuccess(pets);
    }

    @Override
    public void save(AsyncCallback<Pets> callback, Pets pets) {
        log.info("Saving pets {}", pets);

        // Calculate charges on server to avoid Front End API Hackers.
        PetChargeRule petCharge = loadPetChargeRule();
        for (Pet pet : pets.pets()) {
            ChargesSharedCalculation.calculatePetCharges(petCharge, pet);
        }

        saveApplicationEntity(pets);

        if (ChargesServerCalculation.updateChargesForPets(pets)) {
            ApplicationServicesImpl.invalidateChargesStep(pets.application());
        }

        loadTransientData(pets);

        callback.onSuccess(pets);
    }

    /*
     * We can load the data required for pets validation in additional RPC call. But we
     * want to save on number of requests to make application work faster for users, each
     * request may take 10 seconds on slow network connection.
     */
    private static void loadTransientData(Pets pets) {
        // TODO get it from building
        PetChargeRule petCharge = loadPetChargeRule();
        pets.petChargeRule().set(petCharge);
        pets.petWeightMaximum().setValue(25);
        pets.petsMaximum().setValue(3);
    }

    private static PetChargeRule loadPetChargeRule() {
        PetChargeRule petChargeRule = EntityFactory.create(PetChargeRule.class);
        petChargeRule.chargeType().setValue(ChargeType.monthly);
        petChargeRule.value().setValue(20);
        return petChargeRule;
    }

}
