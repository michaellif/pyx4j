/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 24, 2013
 * @author yuriyl
 * @version $Id$
 */
package com.propertyvista.yardi.merger;

import java.util.List;

import com.yardi.entity.mits.YardiCustomer;

import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;

public class TenantMerger {

    public boolean validateChanges(List<YardiCustomer> yardiCustomers, List<LeaseTermTenant> tenants) {
        for (YardiCustomer customer : yardiCustomers) {
            boolean isNew = true;
            for (LeaseTermTenant tenant : tenants) {
                if (compare(customer, tenant) && checkRole(customer, tenant)) {
                    isNew = false;
                }
            }
            if (isNew) {
                return true;
            }
        }
        return false;
    }

    private boolean checkRole(YardiCustomer customer, LeaseTermTenant tenant) {
        if (customer.getLease().isResponsibleForLease().equals(tenant.role().getValue().equals(Role.Applicant))) {
            return true;
        }
        return false;
    }

    public LeaseTerm updateTenants(List<YardiCustomer> yardiCustomers, IList<LeaseTermTenant> tenants) {
        // TODO
        return null;
    }

    private boolean compare(YardiCustomer customer, LeaseTermTenant tenant) {
        if (customer.getName().getFirstName().equals(tenant.leaseParticipant().customer().person().name().firstName().getValue())
                && customer.getName().getLastName().equals(tenant.leaseParticipant().customer().person().name().lastName().getValue())) {
            return true;
        }
        return false;
    }
}
