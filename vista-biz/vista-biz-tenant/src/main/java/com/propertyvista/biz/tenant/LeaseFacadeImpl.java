/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-04-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;

public class LeaseFacadeImpl implements LeaseFacade {

    @Override
    public void createLease(Lease lease) {
        lease.version().status().setValue(Lease.Status.Created);
        lease.paymentFrequency().setValue(PaymentFrequency.Monthly);

        // Manage customer 
        for (Tenant tenant : lease.version().tenants()) {
            Persistence.service().merge(tenant.customer());
        }

        Persistence.service().merge(lease);

        if (lease.unit().getPrimaryKey() != null) {
            // occupancyManager(lease.unit().getPrimaryKey()).reserve(lease);
        }
    }

    @Override
    public void persistLease(Lease lease) {
        // TODO Auto-generated method stub

    }

    @Override
    public void createMasterOnlineApplication(Key leaseId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void approveApplication(Key leaseId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void declineApplication(Key leaseId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void cancelApplication(Key leaseId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void activate(Key leaseId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void createCompletionEvent(Key leaseId, CompletionType completionType, LogicalDate noticeDay, LogicalDate moveOutDay) {
        // TODO Auto-generated method stub

    }

    @Override
    public void cancelCompletionEvent(Key leaseId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void complete(Key leaseId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void close(Key leaseId) {
        // TODO Auto-generated method stub

    }

}
