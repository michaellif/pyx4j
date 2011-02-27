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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.portal.domain.pt.Application;
import com.propertyvista.portal.domain.pt.ChargeLine.ChargeType;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.PotentialTenant.Relationship;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.PotentialTenantList;
import com.propertyvista.portal.domain.pt.TenantCharge;
import com.propertyvista.portal.domain.util.DomainUtil;
import com.propertyvista.portal.rpc.pt.ChargesSharedCalculation;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.server.PersistenceServicesFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

public class ChargesServerCalculation extends ChargesSharedCalculation {

    private final static Logger log = LoggerFactory.getLogger(ChargesServerCalculation.class);

    public static void dummyPopulate(Charges charges, Application application) {

        charges.rentStart().setValue(TimeUtils.createDate(2011, 4, 7)); // dummy date

        // monthly charges
        charges.monthlyCharges().charges().add(DomainUtil.createChargeLine(ChargeType.rent, 1500));
        charges.monthlyCharges().charges().add(DomainUtil.createChargeLine(ChargeType.parking, 100));
        charges.monthlyCharges().charges().add(DomainUtil.createChargeLine(ChargeType.locker, 25));
        charges.monthlyCharges().charges().add(DomainUtil.createChargeLine(ChargeType.petCharge, 75));

        // available upgrades
        charges.monthlyCharges().upgradeCharges().add(DomainUtil.createChargeLine(ChargeType.parking2, 100, true));
        charges.monthlyCharges().upgradeCharges().add(DomainUtil.createChargeLine(ChargeType.locker, 50, true));

        // application charges
        charges.applicationCharges().charges().add(DomainUtil.createChargeLine(ChargeType.deposit, 1500));
        charges.applicationCharges().charges().add(DomainUtil.createChargeLine(ChargeType.petDeposit, 100));
        charges.applicationCharges().charges().add(DomainUtil.createChargeLine(ChargeType.applicationFee, 29));

        // make sure to calculate charges
        calculateCharges(charges);

        // payment splits
        updatePaymentSplitCharges(charges, application);
    }

    public static void updatePaymentSplitCharges(Charges charges, Application application) {
        // find all potential tenants 
        EntityQueryCriteria<PotentialTenantList> criteria = EntityQueryCriteria.create(PotentialTenantList.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
        PotentialTenantList tenantList = PersistenceServicesFactory.getPersistenceService().retrieve(criteria);

        log.info("Found {} tenants", tenantList.tenants().size());

        // compare current tenant list with what we have on the form
        boolean dirty = charges.paymentSplitCharges().charges().isEmpty(); // if there a no charges let's create them
        for (TenantCharge tenantCharge : charges.paymentSplitCharges().charges()) {

            // first, find the tenant (matching by first name and last name)
            PotentialTenantInfo tenant = null;
            for (PotentialTenantInfo existingTenant : tenantList.tenants()) {
                if (tenantCharge.tenant().firstName().equals(existingTenant.firstName()) && tenantCharge.tenant().lastName().equals(existingTenant.lastName())) {
                    tenant = existingTenant;
                }
            }
            if (tenant == null) {
                dirty = true;
                break;
            }

            // second, change their roles
            if (!tenant.relationship().getValue().equals(tenantCharge.tenant().relationship().getValue())) {
                dirty = true;
                break;
            }

            //            // first find the corresponding tenant
            //            int index = tenantList.tenants().indexOf(tenantCharge.tenant());
            //            if (index == -1) {
            //                dirty = true;
            //                break;
            //            }
            //            PotentialTenantInfo tenant = tenantList.tenants().get(index);

            //            // now compare their relationships
            //            if (tenant.relationship()
        }

        if (dirty) {
            resetPaymentSplitCharges(charges, tenantList);
        }
    }

    private static void resetPaymentSplitCharges(Charges charges, PotentialTenantList tenantList) {
        charges.paymentSplitCharges().charges().clear();
        for (PotentialTenantInfo tenant : tenantList.tenants()) {
            Relationship relationship = tenant.relationship().getValue();
            // only applicant or co-applicant can pay
            if (relationship != Relationship.Applicant && relationship != Relationship.CoApplicant) {
                continue;
            }

            int percentage = 0;
            if (relationship == Relationship.Applicant) {
                percentage = 100;
            }
            TenantCharge tenantCharge = DomainUtil.createTenantCharge(percentage, 0);
            tenantCharge.tenant().set(tenant);
            charges.paymentSplitCharges().charges().add(tenantCharge);
        }
    }
}
