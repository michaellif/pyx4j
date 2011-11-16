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

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.TimeUtils;

import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.TenantCharge;
import com.propertyvista.portal.rpc.ptapp.ChargesSharedCalculation;

public class ChargesServerCalculation extends ChargesSharedCalculation {

    private final static Logger log = LoggerFactory.getLogger(ChargesServerCalculation.class);

    public static boolean isEligibleForPaymentSplit(TenantInLease tenant) {
        if (tenant.isNull()) {
            log.info("Received a null tenant when checking for eligibility");
            return false;
        }

        //@see http://propertyvista.jira.com/browse/VISTA-235?focusedCommentId=10332
        if (tenant.status().getValue() == TenantInLease.Status.Applicant) {
            return true;
        } else {
            return TimeUtils.isOlderThen(tenant.tenant().person().birthDate().getValue(), 18);
        }
    }

    public static void updatePaymentSplitCharges(Charges charges, List<TenantInLease> tenants) {
        //        // find all potential tenants 
        //        EntityQueryCriteria<PotentialTenantList> criteria = EntityQueryCriteria.create(PotentialTenantList.class);
        //        criteria.add(PropertyCriterion.eq(criteria.proto().application(), application));
        //        PotentialTenantList tenantList = Persistence.service().retrieve(criteria);
        //        log.info("Found {} tenants", tenantList.tenants().size());

        // compare current tenant list with what we have on the form
        boolean dirty = charges.paymentSplitCharges().charges().isEmpty(); // if there a no charges let's create them
        List<TenantInLease> chargedTenants = new ArrayList<TenantInLease>();
        for (TenantCharge tenantCharge : charges.paymentSplitCharges().charges()) {
            TenantInLease tenant = tenantCharge.tenant();
            log.info("Tenant from charge: {}", tenant);
            chargedTenants.add(tenant);
        }

        for (TenantInLease tenant : tenants) {
            log.debug("Tenant from master tenant list: {}", tenant);
            if (!isEligibleForPaymentSplit(tenant)) {
                log.info("Charges contained tenant {} who should be removed", tenant);
                dirty = true;
                break;
            }
        }

// TODO update the algorithm:
        // go through both lists and make sure that they match
        if (!dirty) {
            dirty = !areTwoTenantListsTheSame(chargedTenants, tenants);
        }
        if (!dirty) {
            dirty = !areTwoTenantListsTheSame(tenants, chargedTenants);
        }

        if (dirty) {
            log.info("Tenants have changed, we need to reset payment split charges");
            resetPaymentSplitCharges(charges, tenants);
        } else {
            log.info("Tenants are the same as before, no need to reset payment split charges");
        }
    }

    private static boolean areTwoTenantListsTheSame(List<TenantInLease> tenants1, List<TenantInLease> tenants2) {

        log.info("Exam");
        for (TenantInLease tenant1 : tenants1) {
            // first, find the tenant (matching by first name and last name)
            TenantInLease tenant2 = null;
            for (TenantInLease currTenant : tenants2) {
                if (EqualsHelper.equals(currTenant.getPrimaryKey(), tenant1.getPrimaryKey())) {
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

    private static void resetPaymentSplitCharges(Charges charges, List<TenantInLease> tenants) {
        charges.paymentSplitCharges().charges().clear();
        for (TenantInLease tenant : tenants) {
            TenantInLease.Status status = tenant.status().getValue();
            log.debug("Going to reset payment splits for tenant {} of age {}", tenant.relationship().getValue(), tenant.tenant().person().birthDate()
                    .getValue());

            if (!isEligibleForPaymentSplit(tenant)) { // make sure that it is eligible
                log.info("This tenant was not eligible");
                continue;
            }

            int percentage = 0;
            if (status == TenantInLease.Status.Applicant) {
                percentage = 100;
            }
            TenantCharge tenantCharge = com.propertyvista.portal.domain.util.DomainUtil.createTenantCharge(percentage, 0);
            tenantCharge.tenant().set(tenant);
            //            Persistence.service().persist(tenant);
            charges.paymentSplitCharges().charges().add(tenantCharge);
        }
    }
}
