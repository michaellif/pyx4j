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
package com.propertyvista.biz.tenant.lease.yardi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.system.YardiARFacade;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.biz.system.yardi.YardiLeaseApplicationFacade;
import com.propertyvista.biz.tenant.lease.LeaseAbstractManager;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.Status;

public class LeaseYardiApplicationManager extends LeaseAbstractManager {

    private static final Logger log = LoggerFactory.getLogger(LeaseYardiApplicationManager.class);

    private final static I18n i18n = I18n.get(LeaseYardiApplicationManager.class);

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
        // N/A - internal implementation only
    }

    @Override
    protected void ensureLeaseUniqness(Lease lease) {
        // N/A - internal implementation only
    }

    @Override
    public Lease persist(Lease lease) {
        ServerSideFactory.create(YardiARFacade.class).setLeaseChargesComaptibleIds(lease);
        return super.persist(lease);
    }

    @Override
    public Lease activate(Lease leaseId) {
        // N/A - internal implementation only
        return Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
    }

    @Override
    public Lease approve(Lease leaseId, Employee decidedBy, String decisionReason) {
        try {
            ServerSideFactory.create(YardiLeaseApplicationFacade.class).createApplication(leaseId);
        } catch (YardiServiceException e) {
            throw new UserRuntimeException(i18n.tr("Posting Application to Yardi failed") + "\n" + e.getMessage(), e);
        }

        Lease lease = super.approve(leaseId, decidedBy, decisionReason);

        // Send Participants to yardi after activation validation if we have not done so in previous attempt
        Persistence.ensureRetrieve(lease._applicant(), AttachLevel.Attached);
        if (lease._applicant().yardiApplicantId().isNull()) {
            try {
                ServerSideFactory.create(YardiLeaseApplicationFacade.class).addLeaseParticipants(lease);
            } catch (YardiServiceException e) {
                throw new UserRuntimeException(i18n.tr("Posting Applicants to Yardi failed") + "\n" + e.getMessage(), e);
            }
        }

        try {
            lease = ServerSideFactory.create(YardiLeaseApplicationFacade.class).approveApplication(lease);
        } catch (YardiServiceException e) {
            throw new UserRuntimeException(i18n.tr("Posting Application to Yardi failed") + "\n" + e.getMessage(), e);
        }

        // Unit occupancy state managed by purely by Import procedure.
        try {
            ServerSideFactory.create(YardiARFacade.class).updateUnitAvailability(lease.unit());
        } catch (Throwable e) {
            log.error("unable to update unit availability", e);
        }

        return lease;
    }

    @Override
    public void createCompletionEvent(Lease leaseId, CompletionType completionType, LogicalDate eventDate, LogicalDate expectedMoveOut, LogicalDate leaseEndDate) {
        if (eventDate == null) {
            eventDate = SystemDateManager.getLogicalDate();
        }
        if (expectedMoveOut == null) {
            expectedMoveOut = SystemDateManager.getLogicalDate();
        }
        if (leaseEndDate == null) {
            leaseEndDate = SystemDateManager.getLogicalDate();
        }

        super.createCompletionEvent(leaseId, completionType, eventDate, expectedMoveOut, leaseEndDate);
    }

    @Override
    public void cancelCompletionEvent(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());

        if (lease.status().getValue() == Lease.Status.Completed) {
            lease.actualMoveOut().setValue(null);
            lease.status().setValue(Status.Active);
            if (lease.completion().isNull()) {
                lease.completion().setValue(CompletionType.Termination);
            }
            Persistence.service().merge(lease);
        }

        LogicalDate expectedMoveOut = lease.expectedMoveOut().getValue();

        super.cancelCompletionEvent(leaseId, decidedBy, decisionReason);
        Persistence.service().retrieve(lease);

        lease.expectedMoveOut().setValue(expectedMoveOut);
        Persistence.service().merge(lease);
    }

    @Override
    public void moveOut(Lease leaseId, LogicalDate actualMoveOut) {
        if (actualMoveOut == null) {
            actualMoveOut = SystemDateManager.getLogicalDate();
        }

        super.moveOut(leaseId, actualMoveOut);
        // complete former leases:
        complete(leaseId);
    }

    @Override
    public void complete(Lease leaseId) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());

        // Verify the status
        if (lease.status().getValue() != Lease.Status.Active) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }
        // if renewed and not moving out:
        if (!lease.nextTerm().isNull() && lease.completion().isNull()) {
            throw new IllegalStateException("Lease has next term ready");
        }

        lease.status().setValue(Status.Completed);

        Persistence.service().merge(lease);
    }

    @Override
    public void updateLeaseDates(Lease lease) {
        LogicalDate expectedMoveOut = lease.expectedMoveOut().getValue();

        super.updateLeaseDates(lease);

        lease.expectedMoveOut().setValue(expectedMoveOut);
    }

    //
    // Functionality, removed in Yardi mode:
    //

    // Unit occupancy management:

    @Override
    protected void reserveUnit(Lease lease) {
        // Do nothing in Yardi mode - unit occupancy state managed by purely by Import procedure!
    }

    @Override
    protected void releaseUnit(Lease lease, Status previousStatus) {
        ServerSideFactory.create(OccupancyFacade.class).unreserveIfReservered(lease);
    }

    @Override
    protected void markUnitOccupied(Lease lease, Status previousStatus) {
        ServerSideFactory.create(OccupancyFacade.class).unreserveIfReservered(lease);
    }

    @Override
    protected void moveOutUnit(Lease lease) {
        // Do nothing in Yardi mode - unit occupancy state managed by purely by Import procedure!
    }

    @Override
    protected void cancelMoveOutUnit(Lease lease) {
        // Do nothing in Yardi mode - unit occupancy state managed by purely by Import procedure!
    }

    @Override
    protected void approveMoveOutUnit(Lease lease) {
        // Do nothing in Yardi mode - unit occupancy state managed by purely by Import procedure!
    }
}
