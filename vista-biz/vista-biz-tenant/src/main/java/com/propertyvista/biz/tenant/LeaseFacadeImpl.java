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
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.occupancy.UnitTurnoverAnalysisFacade;
import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.domain.tenant.ptapp.MasterOnlineApplication;
import com.propertyvista.domain.tenant.ptapp.OnlineApplication;
import com.propertyvista.misc.VistaTODO;
import com.propertyvista.server.financial.billing.BillingFacade;
import com.propertyvista.server.financial.productcatalog.ProductCatalogFacade;

public class LeaseFacadeImpl implements LeaseFacade {

    private static final I18n i18n = I18n.get(LeaseFacadeImpl.class);

    @Override
    public void createLease(Lease lease) {
        lease.version().status().setValue(Lease.Status.Created);
        lease.paymentFrequency().setValue(PaymentFrequency.Monthly);

        // Create Application by default
        lease.leaseApplication().status().setValue(LeaseApplication.Status.Draft);

        saveCustomers(lease);

        ServerSideFactory.create(IdAssignmentFacade.class).assignId(lease);

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
            if (!tenant.customer().isValueDetached()) {
                ServerSideFactory.create(IdAssignmentFacade.class).assignId(tenant.customer());
                Persistence.service().merge(tenant.customer());
            }
        }
        for (Guarantor guarantor : lease.version().guarantors()) {
            if (!guarantor.customer().isValueDetached()) {
                ServerSideFactory.create(IdAssignmentFacade.class).assignId(guarantor.customer());
                Persistence.service().merge(guarantor.customer());
            }
        }
    }

    private void updateApplicationReferencesToFinalVersionOfLase(Lease lease) {
        // update reference to first version of Lease in MasterApplication and Applications
        EntityQueryCriteria<MasterOnlineApplication> criteria = EntityQueryCriteria.create(MasterOnlineApplication.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().lease(), lease.getPrimaryKey().asDraftKey()));
        MasterOnlineApplication ma = Persistence.service().retrieve(criteria);
        if (ma != null) {
            ma.lease().set(lease);
            Persistence.service().retrieve(ma.applications());
            for (OnlineApplication app : ma.applications()) {
                app.lease().set(lease);
            }
            Persistence.service().persist(ma);
        }
    }

    @Override
    public void createMasterOnlineApplication(Key leaseId) {
        Lease lease = Persistence.retrieveDraft(Lease.class, leaseId.asDraftKey());

        // Verify the status
        if (!Lease.Status.draft().contains(lease.version().status().getValue())) {
            throw new UserRuntimeException("Invalid Lease State");
        }
        if (LeaseApplication.Status.Draft != lease.leaseApplication().status().getValue()) {
            throw new UserRuntimeException("Invalid Application State");
        }

        ServerSideFactory.create(OnlineApplicationFacade.class).createMasterOnlineApplication(lease.leaseApplication().onlineApplication());

        lease.leaseApplication().status().setValue(LeaseApplication.Status.OnlineApplicationInProgress);
        lease.version().status().setValue(Lease.Status.ApplicationInProgress);
        Persistence.service().persist(lease);
    }

    @Override
    public void approveApplication(Key leaseId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);

        lease.version().status().setValue(Lease.Status.Approved);
        lease.leaseApplication().status().setValue(LeaseApplication.Status.Approved);
        lease.approvalDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));

        // finalize approved leases while saving:
        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.secureSave(lease);

        updateApplicationReferencesToFinalVersionOfLase(lease);

        ServerSideFactory.create(OccupancyFacade.class).approveLease(lease.unit().getPrimaryKey());

        ServerSideFactory.create(ProductCatalogFacade.class).updateUnitRentPrice(lease);

        if (false && !VistaTODO.removedForProduction) {
            ServerSideFactory.create(BillingFacade.class).runBilling(lease);
        }
    }

    @Override
    public void declineApplication(Key leaseId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        // TODO Review the status
        lease.version().status().setValue(Lease.Status.Closed);
        lease.leaseApplication().status().setValue(LeaseApplication.Status.Declined);

        lease.saveAction().setValue(SaveAction.saveAsFinal);

        Persistence.secureSave(lease);

        updateApplicationReferencesToFinalVersionOfLase(lease);

        ServerSideFactory.create(OccupancyFacade.class).unreserve(lease.unit().getPrimaryKey());

    }

    @Override
    public void cancelApplication(Key leaseId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        // TODO Review the status
        lease.version().status().setValue(Lease.Status.Closed);
        lease.leaseApplication().status().setValue(LeaseApplication.Status.Cancelled);

        lease.saveAction().setValue(SaveAction.saveAsFinal);

        Persistence.secureSave(lease);

        updateApplicationReferencesToFinalVersionOfLase(lease);

        ServerSideFactory.create(OccupancyFacade.class).unreserve(lease.unit().getPrimaryKey());

    }

    // TODO review code here
    @Override
    public void activate(Key leaseId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        // set lease status to active ONLY if first (latest till now) bill is confirmed: 
        if (VistaTODO.removedForProduction
                || ServerSideFactory.create(BillingFacade.class).getLatestBill(lease).billStatus().getValue() == Bill.BillStatus.Confirmed) {

            lease.version().status().setValue(Status.Active);

            lease.saveAction().setValue(SaveAction.saveAsFinal);
            Persistence.secureSave(lease);

            ServerSideFactory.create(UnitTurnoverAnalysisFacade.class).propagateLeaseActivationToTurnoverReport(lease);

            //TODO move to LeadFacad
            // update Lead state (if present)
            EntityQueryCriteria<Lead> criteria = new EntityQueryCriteria<Lead>(Lead.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().lease(), leaseId));

            Lead lead = Persistence.service().retrieve(criteria);
            if (lead != null) {
                lead.status().setValue(Lead.Status.rented);
                Persistence.service().persist(lead);
            }
        } else {
            throw new UserRuntimeException(i18n.tr("Please run and confirm first bill in order to activate the lease."));
        }

    }

    @Override
    public void createCompletionEvent(Key leaseId, CompletionType completionType, LogicalDate noticeDay, LogicalDate moveOutDay) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        if (lease == null) {
            throw new IllegalStateException("lease " + leaseId + " was not found");
        }
        if (lease.version().status().getValue() != Status.Active) {
            throw new IllegalStateException("lease " + leaseId + " must be " + Status.Active + " in order to perform Completion");
        }

        lease.version().completion().setValue(completionType);
        lease.version().moveOutNotice().setValue(noticeDay);
        lease.version().expectedMoveOut().setValue(moveOutDay);

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.secureSave(lease);

        ServerSideFactory.create(OccupancyFacade.class).endLease(lease.unit().getPrimaryKey());

    }

    @Override
    public void cancelCompletionEvent(Key leaseId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        if (lease == null) {
            throw new IllegalStateException("lease " + leaseId + " was not found");
        }
        if (lease.version().completion().isNull()) {
            throw new IllegalStateException("lease " + leaseId + " must have notice in order to perform 'cancelNotice'");
        }
        lease.version().completion().setValue(null);
        lease.version().moveOutNotice().setValue(null);
        lease.version().expectedMoveOut().setValue(null);

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.secureSave(lease);

        ServerSideFactory.create(OccupancyFacade.class).cancelEndLease(lease.unit().getPrimaryKey());
    }

    @Override
    public void complete(Key leaseId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        lease.version().actualLeaseTo().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        lease.version().status().setValue(Status.Completed);

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.secureSave(lease);

    }

    @Override
    public void close(Key leaseId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        lease.version().status().setValue(Status.Closed);

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.secureSave(lease);
    }

}
