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
package com.propertyvista.server.common.util;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.TenantInLease;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.server.billing.BillingFacade;
import com.propertyvista.server.common.util.LeaseManager.TimeContextProvider;
import com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerImpl;
import com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerImpl.NowSource;

public class LeaseLifecycleSim {

    public Lease newLease(final LogicalDate eventDate, String leaseId, AptUnit unit, LogicalDate leaseFrom, LogicalDate leaseTo, LogicalDate expectedMoveIn,
            PaymentFrequency paymentFrequency, Tenant tenant) {
        final Lease lease = EntityFactory.create(Lease.class);
        lease.version().status().setValue(Lease.Status.Created);
        lease.paymentFrequency().setValue(PaymentFrequency.Monthly);
        lease.leaseID().setValue(leaseId);
        lease.unit().set(unit);

        // TODO fix this to match unit type
        lease.type().setValue(Service.Type.residentialUnit);

        lease.createDate().setValue(eventDate);
        lease.leaseFrom().setValue(leaseFrom);
        lease.leaseTo().setValue(leaseTo);
        lease.version().expectedMoveIn().setValue(expectedMoveIn);
        lease.paymentFrequency().setValue(PaymentFrequency.Monthly);

        if (tenant != null) {
            TenantInLease tenantInLease = EntityFactory.create(TenantInLease.class);
            tenantInLease.lease().set(lease.version());
            tenantInLease.tenant().set(tenant);
            tenantInLease.orderInLease().setValue(1);
            tenantInLease.role().setValue(TenantInLease.Role.Applicant);
            lease.version().tenants().add(tenantInLease);
        }
        leaseManager(eventDate).save(lease);

        return lease;
    }

    public Lease createApplication(Key leaseId, LogicalDate eventDate) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        lease.version().status().setValue(Status.ApplicationInProgress);
        leaseManager(eventDate).save(lease);
        return lease;
    }

    public Lease approveApplication(Key leaseId, LogicalDate eventDate) {
        return leaseManager(eventDate).approveApplication(leaseId);
    }

    public Lease declineApplication(Key leaseId, LogicalDate eventDate) {
        return leaseManager(eventDate).declineApplication(leaseId);
    }

    public Lease cancelApplication(Key leaseId, LogicalDate eventDate) {
        return leaseManager(eventDate).cancelApplication(leaseId);
    }

    public Lease activate(Key leaseId, LogicalDate eventDate) {
        // confirm latest bill before activation :
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
        BillingFacade billing = ServerSideFactory.create(BillingFacade.class);
        billing.confirmBill(billing.getLatestBill(lease.billingAccount()));

        return leaseManager(eventDate).activate(leaseId);
    }

    public Lease notice(Key leaseId, LogicalDate noticeDay, LogicalDate moveOutDay) {
        return leaseManager(noticeDay).notice(leaseId, noticeDay, moveOutDay);
    }

    public Lease cancelNotice(Key leaseId, LogicalDate cancelDay) {
        return leaseManager(cancelDay).cancelNotice(leaseId);
    }

    public Lease evict(Key leaseId, LogicalDate evictionDay, LogicalDate moveOutDay) {
        return leaseManager(evictionDay).evict(leaseId, evictionDay, moveOutDay);
    }

    public Lease cancelEvict(Key leaseId, LogicalDate cancellationDay) {
        return leaseManager(cancellationDay).cancelEvict(leaseId);
    }

    /** completes the lease and makes the unit "available" */
    public Lease complete(Key leaseId, final LogicalDate completionDay) {
        Lease lease = leaseManager(completionDay).complete(leaseId);

        AptUnitOccupancyManagerImpl.get(lease.unit().getPrimaryKey(), new NowSource() {
            @Override
            public LogicalDate getNow() {
                return completionDay;
            }
        }).scopeAvailable();

        return lease;
    }

    public Lease close(Key leaseId, LogicalDate closingDay) {
        return leaseManager(closingDay).close(leaseId);
    }

    private LeaseManager leaseManager(final LogicalDate eventDate) {
        return new LeaseManager(new TimeContextProvider() {
            @Override
            public LogicalDate getTimeContext() {
                return eventDate;
            }
        });
    }

}
