/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 16, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.tenant.yardi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.tenant.LeaseAbstractManager;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.Status;

public class LeaseYardiManager extends LeaseAbstractManager {

    private static final Logger log = LoggerFactory.getLogger(LeaseYardiManager.class);

    @Override
    protected BillingAccount createBillingAccount() {
        BillingAccount billingAccount = EntityFactory.create(BillingAccount.class);
        billingAccount.billingPeriod().setValue(BillingPeriod.Monthly);
        return billingAccount;
    }

    @Override
    protected void onLeaseApprovalError(Lease lease, String error) {
        log.warn("Lease approval validation for lease pk='" + lease.getPrimaryKey() + "', id='" + lease.leaseId().getValue()
                + "' has discovered the following errors: " + error);
    }

    @Override
    protected void onLeaseApprovalSuccess(Lease lease, Status leaseStatus) {
        // N/A - internal impl only
    }

    @Override
    protected void ensureLeaseUniqness(Lease lease) {
        // N/A - internal impl only
    }

    @Override
    public void activate(Lease leaseId) {
        // approve, newly created leases:
        super.approve(leaseId, null, null);
        super.activate(leaseId);
    }

    @Override
    public void approve(Lease leaseId, Employee decidedBy, String decisionReason) {
        super.approve(leaseId, decidedBy, decisionReason);
        // activate, newly created leases:
        activate(leaseId);
    }

    @Override
    public void moveOut(Lease leaseId, LogicalDate actualMoveOut) {
        super.moveOut(leaseId, actualMoveOut);
        // complete former leases:
        complete(leaseId);
    }

    @Override
    public void cancelCompletionEvent(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId.getPrimaryKey());

        // Verify the status
        if (lease.status().getValue() != Lease.Status.Completed) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }

        lease.actualMoveOut().setValue(null);
        lease.status().setValue(Status.Active);
        Persistence.secureSave(lease);

        LogicalDate expectedMoveOut = lease.expectedMoveOut().getValue();

        super.cancelCompletionEvent(leaseId, decidedBy, decisionReason);

        lease.expectedMoveOut().setValue(expectedMoveOut);
        Persistence.secureSave(lease);
    }

    @Override
    public void updateLeaseDates(Lease lease) {
        LogicalDate expectedMoveOut = lease.expectedMoveOut().getValue();

        super.updateLeaseDates(lease);

        lease.expectedMoveOut().setValue(expectedMoveOut);
    }
}
