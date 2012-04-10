/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
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
