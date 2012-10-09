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

import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseCustomerTenant;
import com.propertyvista.domain.tenant.lease.LeaseTerm;

public class LightWeightLeaseManagement {

    static long uniqueId = 0;

    synchronized static String uniqueId() {
        return String.valueOf(uniqueId++);
    }

    public static Lease create(Status status) {
        Lease lease = EntityFactory.create(Lease.class);

        lease.status().setValue(status);

        lease.paymentFrequency().setValue(PaymentFrequency.Monthly);
        lease.type().setValue(Service.ServiceType.residentialUnit);

        lease.currentTerm().set(EntityFactory.create(LeaseTerm.class));
        lease.currentTerm().type().setValue(LeaseTerm.Type.FixedEx);
        lease.currentTerm().status().setValue(LeaseTerm.Status.Current);

        return lease;
    }

    public static Lease persist(Lease lease, boolean finalize) {
        if (lease.currentTerm().getPrimaryKey() == null) {
            LeaseTerm term = lease.currentTerm().detach();

            lease.currentTerm().set(null);
            Persistence.service().persist(lease);
            lease.currentTerm().set(term);

            lease.currentTerm().lease().set(lease);

            for (Tenant tenantInLease : term.version().tenants()) {
                Customer customer = tenantInLease.leaseCustomer().customer();
                LeaseCustomerTenant leaseCustomer = EntityFactory.create(LeaseCustomerTenant.class);
                leaseCustomer.participantId().setValue(uniqueId());
                leaseCustomer.lease().set(lease);
                leaseCustomer.customer().set(customer);
                Persistence.service().persist(leaseCustomer);
                tenantInLease.leaseCustomer().set(leaseCustomer);
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
