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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.charges.ChargeType;
import com.propertyvista.domain.financial.offering.extradata.Pet;
import com.propertyvista.domain.financial.offering.extradata.PetChargeRule;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.dto.PetsDTO;
import com.propertyvista.dto.VehiclesDTO;
import com.propertyvista.portal.rpc.ptapp.ChargesSharedCalculation;
import com.propertyvista.portal.rpc.ptapp.dto.AddOnsDTO;
import com.propertyvista.portal.rpc.ptapp.services.AddonsService;
import com.propertyvista.portal.server.ptapp.ChargesServerCalculation;
import com.propertyvista.portal.server.ptapp.PtAppContext;

public class AddonsServiceImpl extends ApplicationEntityServiceImpl implements AddonsService {

    private final static Logger log = LoggerFactory.getLogger(AddonsServiceImpl.class);

    @Override
    public void retrieve(AsyncCallback<AddOnsDTO> callback, Key tenantId) {
        log.info("Retrieving addons");

        Lease lease = PtAppContext.getCurrentUserLease();
        Persistence.service().retrieve(lease.pets());
        Persistence.service().retrieve(lease.vehicles());

        AddOnsDTO addOns = EntityFactory.create(AddOnsDTO.class);
        addOns.pets().list().addAll(lease.pets());
        addOns.vehicles().list().addAll(lease.vehicles());

        loadTransientData(addOns.pets());
        loadTransientData(addOns.vehicles());

        callback.onSuccess(addOns);
    }

    @Override
    public void save(AsyncCallback<AddOnsDTO> callback, AddOnsDTO addOns) {
        log.info("Saving addons {}", addOns);

        Lease lease = PtAppContext.getCurrentUserLease();
        Persistence.service().retrieve(lease.pets());
        Persistence.service().retrieve(lease.vehicles());

        // This value will never be null, since we are always creating it at retrieve
        List<Pet> existingPets = new Vector<Pet>();
        existingPets.addAll(lease.pets());

        // Calculate charges on server to avoid Front End API Hackers.
        PetChargeRule petChargeRule = loadPetChargeRule();
        for (Pet pet : addOns.pets().list()) {
            ChargesSharedCalculation.calculatePetCharges(petChargeRule, pet);
        }

        // update lists:
        lease.pets().clear();
        lease.pets().addAll(addOns.pets().list());

        lease.vehicles().clear();
        lease.vehicles().addAll(addOns.vehicles().list());

        // actual save:
        Persistence.service().merge(lease.vehicles());
        Persistence.service().merge(lease.pets());
        Persistence.service().merge(lease);

        if (ChargesServerCalculation.needToUpdateChargesForPets(lease.pets(), existingPets)) {
            ApplicationProgressMgr.invalidateChargesStep();
        }

        // update current addons:
        loadTransientData(addOns.pets());
        loadTransientData(addOns.vehicles());

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
        pets.chargeRule().set(petCharge);
        pets.maxPetWeight().setValue(25);
        pets.maxTotal().setValue(3);
    }

    private static void loadTransientData(VehiclesDTO vehicles) {
        // TODO get it from building
//        vehicles.chargeRule().set(vehicleCharge);
        vehicles.maxTotal().setValue(3);
    }

    private static PetChargeRule loadPetChargeRule() {
        // TODO get it from building
        PetChargeRule petChargeRule = EntityFactory.create(PetChargeRule.class);
        petChargeRule.chargeType().setValue(ChargeType.monthly);
        petChargeRule.value().setValue(20);
        return petChargeRule;
    }
}
