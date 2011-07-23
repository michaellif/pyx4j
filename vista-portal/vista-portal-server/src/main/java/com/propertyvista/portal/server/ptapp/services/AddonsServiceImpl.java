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
package com.propertyvista.portal.server.ptapp.services;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.Pet;
import com.propertyvista.domain.PetChargeRule;
import com.propertyvista.domain.charges.ChargeType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.PetsDTO;
import com.propertyvista.portal.domain.ptapp.dto.AddOnsDTO;
import com.propertyvista.portal.rpc.ptapp.ChargesSharedCalculation;
import com.propertyvista.portal.rpc.ptapp.services.AddonsService;
import com.propertyvista.portal.server.ptapp.ChargesServerCalculation;
import com.propertyvista.portal.server.ptapp.PtAppContext;

public class AddonsServiceImpl extends ApplicationEntityServiceImpl implements AddonsService {

    private final static Logger log = LoggerFactory.getLogger(AddonsServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<AddOnsDTO> callback, Key tenantId) {
        log.info("Retrieving pets");
        Lease lease = PersistenceServicesFactory.getPersistenceService().retrieve(Lease.class, PtAppContext.getCurrentUserApplicationPrimaryKey());
        PersistenceServicesFactory.getPersistenceService().retrieve(lease.pets());

        AddOnsDTO addOns = EntityFactory.create(AddOnsDTO.class);
        addOns.pets().pets().addAll(lease.pets());
        addOns.vehicles().addAll(lease.vehicles());

        loadTransientData(addOns.pets());

        callback.onSuccess(addOns);
    }

    @Override
    public void save(AsyncCallback<AddOnsDTO> callback, AddOnsDTO addOns) {
        log.info("Saving pets {}", addOns);

        Lease lease = PersistenceServicesFactory.getPersistenceService().retrieve(Lease.class, PtAppContext.getCurrentUserApplicationPrimaryKey());
        PersistenceServicesFactory.getPersistenceService().retrieve(lease.pets());

        // This value will never be null, since we are always creating it at retrieve
        List<Pet> existingPets = new Vector<Pet>();
        existingPets.addAll(lease.pets());

        // Calculate charges on server to avoid Front End API Hackers.
        PetChargeRule petChargeRule = loadPetChargeRule();
        for (Pet pet : addOns.pets().pets()) {
            ChargesSharedCalculation.calculatePetCharges(petChargeRule, pet);
        }

        lease.pets().clear();
        lease.pets().addAll(addOns.pets().pets());

        lease.vehicles().clear();
        lease.vehicles().addAll(addOns.vehicles());

        //TODO use merge
        PersistenceServicesFactory.getPersistenceService().persist(lease.pets());
        PersistenceServicesFactory.getPersistenceService().persist(lease.vehicles());
        PersistenceServicesFactory.getPersistenceService().persist(lease);

        if (ChargesServerCalculation.needToUpdateChargesForPets(lease.pets(), existingPets)) {
            ApplicationProgressMgr.invalidateChargesStep();
        }

        loadTransientData(addOns.pets());

        callback.onSuccess(addOns);
    }

    /*
     * We can load the data required for pets validation in additional RPC call. But we
     * want to save on number of requests to make application work faster for users, each
     * request may take 10 seconds on slow network connection.
     */
    private static void loadTransientData(PetsDTO pets) {
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
