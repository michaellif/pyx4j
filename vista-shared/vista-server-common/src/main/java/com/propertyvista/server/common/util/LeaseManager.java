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

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerImpl;

public class LeaseManager {

    public void save(Lease lease) {
        boolean isNewLease = lease.getPrimaryKey() == null;
        boolean isUnitChanged = isNewLease;
        boolean doReserve = false;
        boolean doUnreserve = false;

        Lease oldLease = null;

        if (isNewLease) {
            doReserve = lease.unit().getPrimaryKey() != null;
        } else {
            oldLease = Persistence.secureRetrieve(Lease.class, lease.getPrimaryKey());

            // check if unit reservation has changed
            Persistence.service().retrieve(oldLease.unit());
            if (!EqualsHelper.equals(oldLease.unit().id().getValue(), lease.unit().id().getValue())) {
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
                doUnreserve = oldLease.unit().getPrimaryKey() != null;
                doReserve = lease.unit().getPrimaryKey() != null;
            }
        }

        Persistence.secureSave(lease);

        if (isUnitChanged) {
            if (doUnreserve) {
                new AptUnitOccupancyManagerImpl(oldLease.unit().getPrimaryKey()).unreserve();
            }
            if (doReserve) {
                new AptUnitOccupancyManagerImpl(lease.unit().getPrimaryKey()).reserve(lease);
            }
        }
    }

    public void notice(Key leaseId, LogicalDate noticeDay, LogicalDate moveOutDay) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
        if (lease == null) {
            throw new IllegalStateException("lease " + leaseId + " was not found");
        }
        if (lease.status().getValue() != Status.Active) {
            throw new IllegalStateException("lease " + leaseId + " must be " + Status.Active + " in order to perform 'Notice'");
        }

        lease.completion().setValue(CompletionType.Notice);
        lease.moveOutNotice().setValue(noticeDay);
        lease.expectedMoveOut().setValue(moveOutDay);
        Persistence.secureSave(lease);

        new AptUnitOccupancyManagerImpl(lease.unit().getPrimaryKey()).endLease();
    }

    public void cancelNotice(Key leaseId) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
        if (lease == null) {
            throw new IllegalStateException("lease " + leaseId + " was not found");
        }
        if (lease.completion().getValue() != CompletionType.Notice | lease.moveOutNotice().isNull()) {
            throw new IllegalStateException("lease " + leaseId + " must have notice in order to perform 'cancelNotice'");
        }
        lease.completion().setValue(null);
        lease.moveOutNotice().setValue(null);
        lease.expectedMoveOut().setValue(null);
        Persistence.secureSave(lease);
        new AptUnitOccupancyManagerImpl(lease.unit().getPrimaryKey()).cancelEndLease();
    }

    public void evict(Key leaseId, LogicalDate evictionDay, LogicalDate moveOutDay) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
        if (lease == null) {
            throw new IllegalStateException("lease " + leaseId + " was not found");
        }
        if (lease.status().getValue() != Status.Active) {
            throw new IllegalStateException("lease " + leaseId + " must be " + Status.Active + " in order to perform 'Evict'");
        }
        lease.completion().setValue(CompletionType.Eviction);
        lease.moveOutNotice().setValue(evictionDay);
        lease.expectedMoveOut().setValue(moveOutDay);
        Persistence.secureSave(lease);
        new AptUnitOccupancyManagerImpl(lease.unit().getPrimaryKey()).endLease();
    }

    public void cancelEvict(Key leaseId) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
        if (lease == null) {
            throw new IllegalStateException("lease " + leaseId + " was not found");
        }
        if (lease.completion().getValue() != CompletionType.Notice | lease.moveOutNotice().isNull()) {
            throw new IllegalStateException("lease " + leaseId + " must have eviction in order to perform 'cancelEvict'");
        }
        lease.completion().setValue(null);
        lease.moveOutNotice().setValue(null);
        lease.expectedMoveOut().setValue(null);
        Persistence.secureSave(lease);
        new AptUnitOccupancyManagerImpl(lease.unit().getPrimaryKey()).cancelEndLease();
    }

    public void approveApplication(Key leaseId) {
        // TODO
    }

    public void declineApplication(Key leaseId) {
        // TODO 
    }

    public void cancelApplication(Key leaseId) {
        // TODO
    }

}
