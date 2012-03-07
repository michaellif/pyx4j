/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 7, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertvista.generator;

import com.propertvista.generator.util.RandomUtil;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.server.common.util.LeaseManager;
import com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerImpl;

public class LeaseLifecycleSim {

    public Lease newLease(LogicalDate eventDate, AptUnit unit, LogicalDate leaseFrom, LogicalDate leaseTo, LogicalDate expectedMoveIn,
            PaymentFrequency paymentFrequency, Tenant tenant) {
        final Lease lease = EntityFactory.create(Lease.class);
        lease.status().setValue(Lease.Status.New);
        lease.leaseID().setValue(RandomUtil.randomLetters(8));
        lease.unit().set(unit);

        //TODO :lease.type().setValue(Service.Type.residentialUnit);         // This is actually updated during save to match real unit data

        lease.createDate().setValue(RandomUtil.randomLogicalDate(2010, 2011));
        lease.leaseFrom().setValue(lease.createDate().getValue());
        lease.leaseTo().setValue(RandomUtil.randomLogicalDate(2012, 2014));
        lease.expectedMoveIn().setValue(lease.leaseFrom().getValue());
        lease.paymentFrequency().setValue(PaymentFrequency.Monthly);

        TenantInLease tenantInLease = EntityFactory.create(TenantInLease.class);
        tenantInLease.lease().set(lease);
        tenantInLease.tenant().set(tenant);
        tenantInLease.orderInLease().setValue(1);
        tenantInLease.role().setValue(TenantInLease.Role.Applicant);
        lease.tenants().add(tenantInLease);

        new LeaseManager().save(lease);
        return lease;
    }

    public Lease createApplication(Key leaseId) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
        lease.status().setValue(Status.ApplicationInProgress);
        return lease;
    }

    public Lease approveApplication(Key leaseId) {
        new LeaseManager().approveApplication(leaseId);
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
        return lease;
    }

    public Lease declineApplication(Key leaseId) {
        new LeaseManager().declineApplication(leaseId);
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
        return lease;
    }

    public Lease cancelApplication(Key leaseId) {
        new LeaseManager().cancelApplication(leaseId);
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
        return lease;
    }

    public Lease activate(Key leaseId) {
        new LeaseManager().approveApplication(leaseId);
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
        return lease;
    }

//    public Lease moveIn(Key leaseId, LogicalDate moveInDate) {
//        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
//        lease.actualMoveIn().setValue(moveInDate);
//        new LeaseManager().save(lease);
//        return lease;
//    }

    public Lease notice(LogicalDate eventDay, Key leaseId, LogicalDate moveOutDay) {
        new LeaseManager().notice(leaseId, eventDay, moveOutDay);
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
        return lease;
    }

    public Lease cancelNotice(Key leaseId) {
        new LeaseManager().cancelNotice(leaseId);
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
        return lease;
    }

    public Lease evict(LogicalDate eventDay, Key leaseId, LogicalDate moveOutDay) {
        new LeaseManager().evict(leaseId, eventDay, moveOutDay);
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
        return lease;
    }

    public Lease cancelEvict(Key leaseId) {
        new LeaseManager().cancelEvict(leaseId);
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
        return lease;
    }

    public Lease complete(final LogicalDate eventDay, Key leaseId) {
        final Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
        lease.actualLeaseTo().setValue(eventDay);
        lease.status().setValue(Status.Completed);
        new LeaseManager().save(lease);

        new AptUnitOccupancyManagerImpl(lease.unit(), new AptUnitOccupancyManagerImpl.NowSource() {

            @Override
            public LogicalDate getNow() {

                return eventDay;
            }
        }).scopeAvailable();
        return lease;
    }

    public Lease close(Key leaseId) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
        lease.status().setValue(Status.Closed);
        return lease;
    }
}
