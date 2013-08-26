/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-08-13
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.test.helper;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;

public class LightWeightLeaseManagement {

    static long uniqueId = 0;

    synchronized static String uniqueId() {
        return String.valueOf(uniqueId++);
    }

    public static Lease create(Status status) {
        Lease lease = EntityFactory.create(Lease.class);

        lease.status().setValue(status);

        lease.type().setValue(ARCode.Type.Residential);

        BillingAccount billingAccount = EntityFactory.create(BillingAccount.class);
        lease.billingAccount().set(billingAccount);
        lease.billingAccount().billingPeriod().setValue(BillingPeriod.Monthly);

        lease.currentTerm().set(EntityFactory.create(LeaseTerm.class));
        lease.currentTerm().type().setValue(LeaseTerm.Type.FixedEx);
        lease.currentTerm().status().setValue(LeaseTerm.Status.Current);

        return lease;
    }

    // no product catalog support; no unit availability support 
    public static void setUnit(Lease lease, AptUnit unit) {
        lease.unit().set(unit);
        lease.currentTerm().unit().set(unit);
    }

    public static Lease persist(Lease lease, boolean finalize) {
        if (lease.currentTerm().getPrimaryKey() == null) {
            LeaseTerm term = lease.currentTerm().detach();

            lease.currentTerm().set(null);

            Persistence.service().persist(lease);
            lease.currentTerm().set(term);

            lease.currentTerm().lease().set(lease);

            // just one tenant per customer supported:
            for (LeaseTermTenant tenantInLease : term.version().tenants()) {
                Customer customer = tenantInLease.leaseParticipant().customer();
                Tenant tenant = EntityFactory.create(Tenant.class);
                tenant.participantId().setValue(uniqueId());
                tenant.lease().set(lease);
                tenant.customer().set(customer);
                Persistence.service().persist(customer);
                Persistence.service().persist(tenant);
                tenantInLease.leaseParticipant().set(tenant);
            }
        }
        if (finalize) {
            lease.currentTerm().saveAction().setValue(SaveAction.saveAsFinal);
        }

        Persistence.service().persist(lease.currentTerm());
        Persistence.service().persist(lease);

        return lease;
    }
}
