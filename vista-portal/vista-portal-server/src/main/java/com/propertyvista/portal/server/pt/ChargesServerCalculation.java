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
package com.propertyvista.portal.server.pt;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.ChargeLine.ChargeType;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.Pet;
import com.propertyvista.portal.domain.pt.Pets;
import com.propertyvista.portal.domain.pt.PotentialTenant.Status;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.PotentialTenantList;
import com.propertyvista.portal.domain.pt.TenantCharge;
import com.propertyvista.portal.domain.pt.UnitSelection;
import com.propertyvista.portal.domain.util.DomainUtil;
import com.propertyvista.portal.rpc.pt.ChargesSharedCalculation;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

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
        charges.rentStart().setValue(unitSelection.rentStart().getValue());

        // calculate things that we can
        double rentAmount = unitSelection.markerRent().rent().amount().getValue();
        double depositAmount = unitSelection.selectedUnit().requiredDeposit().amount().getValue();

        // monthly charges
        //TODO use update.
        charges.monthlyCharges().charges().clear();
        charges.monthlyCharges().charges().add(DomainUtil.createChargeLine(ChargeType.rent, rentAmount));

        EntityQueryCriteria<PotentialTenantList> tenantCriteria = EntityQueryCriteria.create(PotentialTenantList.class);
        tenantCriteria.add(PropertyCriterion.eq(tenantCriteria.proto().application(), charges.application()));
        PotentialTenantList tenantList = PersistenceServicesFactory.getPersistenceService().retrieve(tenantCriteria);
        int carsCount = 0;
        for (PotentialTenantInfo pti : tenantList.tenants()) {
            carsCount += pti.vehicles().size();
        }

        if (carsCount > 0) {
            double parkingChargeAmount = carsCount * 50;
            if (carsCount == 1) {
                charges.monthlyCharges().charges().add(DomainUtil.createChargeLine(ChargeType.parking, parkingChargeAmount));
            } else {
                String label = "Parking " + carsCount + " Cars";
                charges.monthlyCharges().charges().add(DomainUtil.createChargeLine(label, ChargeType.parking, parkingChargeAmount));
            }
        }

        // TODO 
        charges.monthlyCharges().charges().add(DomainUtil.createChargeLine(ChargeType.locker, 25));

        // find appropriate pet charges
        EntityQueryCriteria<Pets> petCriteria = EntityQueryCriteria.create(Pets.class);
        petCriteria.add(PropertyCriterion.eq(aptUnitCriteria.proto().application(), charges.application()));
        Pets pets = PersistenceServicesFactory.getPersistenceService().retrieve(petCriteria);

        double petChargeAmount = 0d;
        for (Pet pet : pets.pets()) {
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
        updatePaymentSplitCharges(charges, charges.application());

        // make sure to calculate charges
        calculateCharges(charges);
    }

    public static boolean isEligibleForPaymentSplit(PotentialTenantInfo tenant) {
        //@see http://propertyvista.jira.com/browse/VISTA-235?focusedCommentId=10332
        if (tenant.status().getValue() == Status.Applicant) {
            return true;
        } else {
            return TimeUtils.isOlderThen(tenant.birthDate().getValue(), 18);
        }
    }

    public static void updatePaymentSplitCharges(Charges charges, Application application) {
        // find all potential tenants 
        EntityQueryCriteria<PotentialTenantList> criteria = EntityQueryCriteria.create(PotentialTenantList.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
        PotentialTenantList tenantList = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);

        log.info("Found {} tenants", tenantList.tenants().size());

        // compare current tenant list with what we have on the form
        boolean dirty = charges.paymentSplitCharges().charges().isEmpty(); // if there a no charges let's create them
        List<PotentialTenantInfo> chargedTenants = new ArrayList<PotentialTenantInfo>();
        for (TenantCharge tenantCharge : charges.paymentSplitCharges().charges()) {

            PotentialTenantInfo tenant = tenantCharge.tenant();
            log.info("Checking tenant {} of age {}", tenant.relationship().getValue(), tenant.birthDate().getValue());
            if (isEligibleForPaymentSplit(tenant)) {
                log.info("Charges contained tenant {} who should be removed", tenantCharge.tenant());
                dirty = true;
                break;
            }
            chargedTenants.add(tenantCharge.tenant());
        }

        // go through both lists and make sure that they match
        if (!dirty) {
            dirty = examineTenantLists(chargedTenants, tenantList.tenants());
        }
        if (!dirty) {
            dirty = examineTenantLists(tenantList.tenants(), chargedTenants);
        }

        if (dirty) {
            log.info("Tenants have changed, we need to reset payment split charges");
            resetPaymentSplitCharges(charges, tenantList);
        } else {
            log.info("Tenants are the same as before, no need to reset payment split charges");
        }
    }

    private static boolean examineTenantLists(List<PotentialTenantInfo> tenants1, List<PotentialTenantInfo> tenants2) {

        log.info("Exam");
        for (PotentialTenantInfo tenant1 : tenants1) {
            // first, find the tenant (matching by first name and last name)
            PotentialTenantInfo tenant2 = null;
            for (PotentialTenantInfo currTenant : tenants2) {
                if (currTenant.firstName().equals(tenant1.firstName()) && currTenant.lastName().equals(tenant1.lastName())) {
                    tenant2 = currTenant;
                    continue;
                }
            }

            log.info("Comparing \n\t{} \nand \n\t{}", tenant1, tenant2);

            if (tenant2 == null) {
                return false;
            }

            // second, change their roles
            if (!tenant1.relationship().getValue().equals(tenant2.relationship().getValue())) {
                return false;
            }
        }

        return true;
    }

    private static void resetPaymentSplitCharges(Charges charges, PotentialTenantList tenantList) {
        charges.paymentSplitCharges().charges().clear();
        for (PotentialTenantInfo tenant : tenantList.tenants()) {
            Status status = tenant.status().getValue();
            log.info("Going to reset payment splits for tenant {} of age {}", tenant.relationship().getValue(), tenant.birthDate().getValue());

            if (!isEligibleForPaymentSplit(tenant)) { // make sure that it is eligible
                log.info("This tenant was not eligible");
                continue;
            }

            int percentage = 0;
            if (status == Status.Applicant) {
                percentage = 100;
            }
            TenantCharge tenantCharge = DomainUtil.createTenantCharge(percentage, 0);
            tenantCharge.tenant().set(tenant);
            PersistenceServicesFactory.getPersistenceService().persist(tenant);
            charges.paymentSplitCharges().charges().add(tenantCharge);
        }
    }
}
