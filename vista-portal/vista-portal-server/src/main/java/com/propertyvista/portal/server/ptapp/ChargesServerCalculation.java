/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 20, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.ptapp;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.Pet;
import com.propertyvista.domain.Vehicle;
import com.propertyvista.domain.charges.ChargeLine.ChargeType;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.dto.PetsDTO;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.PotentialTenant.Status;
import com.propertyvista.portal.domain.ptapp.PotentialTenantInfo;
import com.propertyvista.portal.domain.ptapp.Tenant;
import com.propertyvista.portal.domain.ptapp.TenantCharge;
import com.propertyvista.portal.domain.ptapp.UnitSelection;
import com.propertyvista.portal.rpc.ptapp.ChargesSharedCalculation;

public class ChargesServerCalculation extends ChargesSharedCalculation {

    private final static Logger log = LoggerFactory.getLogger(ChargesServerCalculation.class);

    public static void updateChargesFromApplication(Charges charges) {
        if (charges.application().isNull()) {
            throw new Error("Data error, charges are not associated with an application");
        }
        // find unit selection
        EntityQueryCriteria<UnitSelection> aptUnitCriteria = EntityQueryCriteria.create(UnitSelection.class);
        aptUnitCriteria.add(PropertyCriterion.eq(aptUnitCriteria.proto().application(), charges.application()));
        UnitSelection unitSelection = PersistenceServicesFactory.getPersistenceService().retrieve(aptUnitCriteria);
        if (unitSelection == null) {
            log.warn("Could not find unit selection for charges {}", charges);
            return;
        }
        AptUnit selectedUnit = null;
        if (!unitSelection.selectedUnitId().isNull()) {
            selectedUnit = PersistenceServicesFactory.getPersistenceService().retrieve(AptUnit.class, unitSelection.selectedUnitId().getValue());
        }

        EntityQueryCriteria<Tenant> tenantCriteria = EntityQueryCriteria.create(Tenant.class);
        tenantCriteria.add(PropertyCriterion.eq(tenantCriteria.proto().application(), charges.application()));
        Tenant tenantList = PersistenceServicesFactory.getPersistenceService().retrieve(tenantCriteria);

        // find appropriate pet charges
        EntityQueryCriteria<PetsDTO> petCriteria = EntityQueryCriteria.create(PetsDTO.class);
        //TODO petCriteria.add(PropertyCriterion.eq(petCriteria.proto().application(), charges.application()));
        PetsDTO pets = null;//PersistenceServicesFactory.getPersistenceService().retrieve(petCriteria);
        // TODO retrieve vehicles list here from lease:
        IList<Vehicle> vehicles = null;

        updateChargesFromObjects(charges, unitSelection, selectedUnit, tenantList, pets, vehicles);
    }

    public static void updateChargesFromObjects(Charges charges, UnitSelection unitSelection, AptUnit selectedUnit, Tenant tenantList,
            PetsDTO pets, IList<Vehicle> vehicles) {
        double rentAmount = 0;
        double depositAmount = 0;

        if (selectedUnit != null) {
            rentAmount = selectedUnit.financial().unitRent().getValue();
// TODO: there is no requiredDeposit in Unit now!?
//            depositAmount = selectedUnit.requiredDeposit().amount().getValue();
        }

        charges.rentStart().setValue(unitSelection.rentStart().getValue());
        charges.monthlyCharges().charges().clear();
        charges.monthlyCharges().charges().add(DomainUtil.createChargeLine(ChargeType.monthlyRent, rentAmount));
        charges.monthlyCharges().charges().add(DomainUtil.createChargeLine(ChargeType.locker, 25)); // TODO make this dynamic

        int carsCount = vehicles.size();

        if (carsCount > 0) {
            double parkingChargeAmount = carsCount * 50;
            if (carsCount == 1) {
                charges.monthlyCharges().charges().add(DomainUtil.createChargeLine(ChargeType.parking, parkingChargeAmount));
            } else {
                String label = "Parking " + carsCount + " Cars";
                charges.monthlyCharges().charges().add(DomainUtil.createChargeLine(label, ChargeType.parking, parkingChargeAmount));
            }
        }

        double petChargeAmount = 0d;
        for (Pet pet : pets.list()) {
            petChargeAmount += pet.chargeLine().charge().amount().getValue();
        }
        if (petChargeAmount > 0) {
            charges.monthlyCharges().charges().add(DomainUtil.createChargeLine(ChargeType.petCharge, petChargeAmount));
        }

        // available upgrades
        if (charges.monthlyCharges().upgradeCharges().size() == 0) {
            charges.monthlyCharges().upgradeCharges().add(DomainUtil.createChargeLine(ChargeType.parking2, 75, false));
            charges.monthlyCharges().upgradeCharges().add(DomainUtil.createChargeLine(ChargeType.locker, 50, false));
        }

        // application charges
        //TODO use update.
        charges.applicationCharges().charges().clear();
        if (depositAmount > 0) {
            charges.applicationCharges().charges().add(DomainUtil.createChargeLine(ChargeType.deposit, depositAmount));
        }
        //charges.applicationCharges().charges().add(DomainUtil.createChargeLine(ChargeType.petDeposit, 100));
        charges.applicationCharges().charges().add(DomainUtil.createChargeLine(ChargeType.applicationFee, 29));

        // payment splits
        updatePaymentSplitCharges(charges, tenantList);

        // make sure to calculate charges
        calculateCharges(charges);
    }

    /**
     * @param pets
     * @return true if pets are changed
     */
    public static boolean needToUpdateChargesForPets(List<Pet> pets, List<Pet> existingPets) {
        if (existingPets == null) {
            return true;
        }
        if (pets.size() != existingPets.size()) {
            log.info("Number of pets has changed from {} to {}", existingPets.size(), pets.size());
            return true;
        }

        for (int i = 0; i < pets.size(); i++) {
            Pet pet = pets.get(i);
            Pet existingPet = existingPets.get(i);
            if (pet.chargeLine().charge().amount().getValue() != existingPet.chargeLine().charge().amount().getValue()) {
                log.info("Pet's charge has changed from {} to {}", existingPet.chargeLine().charge(), pet.chargeLine().charge());
                return true;
            }
        }

        //TODO  See ChargeType.petCharge line will be changed and update it if required
        return false;
    }

    public static boolean isEligibleForPaymentSplit(PotentialTenantInfo tenant) {
        if (tenant.isNull()) {
            log.info("Received a null tenant when checking for eligibility");
            return false;
        }

        //@see http://propertyvista.jira.com/browse/VISTA-235?focusedCommentId=10332
        if (tenant.status().getValue() == Status.Applicant) {
            return true;
        } else {
            return TimeUtils.isOlderThen(tenant.person().birthDate().getValue(), 18);
        }
    }

    public static void updatePaymentSplitCharges(Charges charges, Tenant tenantList) {
        //        // find all potential tenants 
        //        EntityQueryCriteria<PotentialTenantList> criteria = EntityQueryCriteria.create(PotentialTenantList.class);
        //        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
        //        PotentialTenantList tenantList = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);
        //        log.info("Found {} tenants", tenantList.tenants().size());

        // compare current tenant list with what we have on the form
        boolean dirty = charges.paymentSplitCharges().charges().isEmpty(); // if there a no charges let's create them
        List<PotentialTenantInfo> chargedTenants = new ArrayList<PotentialTenantInfo>();
        for (TenantCharge tenantCharge : charges.paymentSplitCharges().charges()) {
            PotentialTenantInfo tenant = tenantCharge.tenant();
            log.info("Tenant from charge: {}", tenant);
            chargedTenants.add(tenant);
        }

        for (PotentialTenantInfo tenant : tenantList.tenants()) {
            log.debug("Tenant from master tenant list: {}", tenant);
            if (!isEligibleForPaymentSplit(tenant)) {
                log.info("Charges contained tenant {} who should be removed", tenant);
                dirty = true;
                break;
            }
        }

        // go through both lists and make sure that they match
        if (!dirty) {
            dirty = !areTwoTenantListsTheSame(chargedTenants, tenantList.tenants());
        }
        if (!dirty) {
            dirty = !areTwoTenantListsTheSame(tenantList.tenants(), chargedTenants);
        }

        if (dirty) {
            log.info("Tenants have changed, we need to reset payment split charges");
            resetPaymentSplitCharges(charges, tenantList);
        } else {
            log.info("Tenants are the same as before, no need to reset payment split charges");
        }
    }

    private static boolean areTwoTenantListsTheSame(List<PotentialTenantInfo> tenants1, List<PotentialTenantInfo> tenants2) {

        log.info("Exam");
        for (PotentialTenantInfo tenant1 : tenants1) {
            // first, find the tenant (matching by first name and last name)
            PotentialTenantInfo tenant2 = null;
            for (PotentialTenantInfo currTenant : tenants2) {
                if (currTenant.id().equals(tenant1.id())) {
                    tenant2 = currTenant;
                    continue;
                }
            }

            //            log.info("Comparing {} and {}", tenant1.id(), tenant2.id());

            if (tenant2 == null) {
                return false; // this means that we have not found corresponding tenant in the other list by id
            }

            //            // second, change their roles
            //            if (!tenant1.relationship().getValue().equals(tenant2.relationship().getValue())) {
            //                return false;
            //            }
        }

        return true;
    }

    private static void resetPaymentSplitCharges(Charges charges, Tenant tenantList) {
        charges.paymentSplitCharges().charges().clear();
        for (PotentialTenantInfo tenant : tenantList.tenants()) {
            Status status = tenant.status().getValue();
            log.debug("Going to reset payment splits for tenant {} of age {}", tenant.relationship().getValue(), tenant.person().birthDate().getValue());

            if (!isEligibleForPaymentSplit(tenant)) { // make sure that it is eligible
                log.info("This tenant was not eligible");
                continue;
            }

            int percentage = 0;
            if (status == Status.Applicant) {
                percentage = 100;
            }
            TenantCharge tenantCharge = com.propertyvista.portal.domain.util.DomainUtil.createTenantCharge(percentage, 0);
            tenantCharge.tenant().set(tenant);
            //            PersistenceServicesFactory.getPersistenceService().persist(tenant);
            charges.paymentSplitCharges().charges().add(tenantCharge);
        }
    }
}
