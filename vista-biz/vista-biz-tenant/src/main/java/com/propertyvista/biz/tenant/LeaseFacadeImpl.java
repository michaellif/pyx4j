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

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.server.financial.productcatalog.ProductCatalogFacade;

public class LeaseFacadeImpl implements LeaseFacade {

    @Override
    public void createLease(Lease lease) {
        lease.version().status().setValue(Lease.Status.Created);
        lease.paymentFrequency().setValue(PaymentFrequency.Monthly);

        // Create Application by default
        lease.leaseApplication().status().setValue(LeaseApplication.Status.Draft);

        saveCustomers(lease);

        //TODO
//      if (lease.id().isNull() && IdAssignmentSequenceUtil.needsGeneratedId(IdTarget.lease)) {
//          lease.leaseId().setValue(IdAssignmentSequenceUtil.getId(IdTarget.lease));
//      }

        Persistence.service().merge(lease);

        if (lease.unit().getPrimaryKey() != null) {
            ServerSideFactory.create(OccupancyFacade.class).reserve(lease.unit().getPrimaryKey(), lease);
        }
    }

    @Override
    public void persistLease(Lease lease) {
        boolean isUnitChanged = false;
        boolean doReserve = false;
        boolean doUnreserve = false;

        Lease origLease = Persistence.secureRetrieve(Lease.class, lease.getPrimaryKey().asCurrentKey());

        // check if unit reservation has changed
        if (!EqualsHelper.equals(origLease.unit().getPrimaryKey(), lease.unit().getPrimaryKey())) {
            isUnitChanged = true;
            // old lease has unit: o
            // new lease has a unit: n
            // !o & !n is impossible here, then we have:
            // !o & n -> reserve
            // o & n -> reserve           
            //  o & !n -> unreserve
            // o & n -> unreserve                
            // hence:
            // o -> unreserve                
            // n -> reserve
            doUnreserve = origLease.unit().getPrimaryKey() != null;
            doReserve = lease.unit().getPrimaryKey() != null;
        }

        saveCustomers(lease);
        Persistence.secureSave(lease);

        if (isUnitChanged) {
            if (doUnreserve) {
                ServerSideFactory.create(OccupancyFacade.class).unreserve(origLease.unit().getPrimaryKey());
            }
            if (doReserve) {
                ServerSideFactory.create(OccupancyFacade.class).reserve(lease.unit().getPrimaryKey(), lease);
            }
        }
    }

    @Override
    public void saveAsFinal(Lease lease) {
        persistLease(lease);
        // update unit rent price here:
        ServerSideFactory.create(ProductCatalogFacade.class).updateUnitRentPrice(lease);
    }

    private void saveCustomers(Lease lease) {
        // TODO Manage customer for PTAPP 
        for (Tenant tenant : lease.version().tenants()) {
            Persistence.service().merge(tenant.customer());
        }
        for (Guarantor guarantor : lease.version().guarantors()) {
            Persistence.service().merge(guarantor.customer());
        }
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
