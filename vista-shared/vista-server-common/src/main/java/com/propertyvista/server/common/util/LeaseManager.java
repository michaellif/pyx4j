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
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.policy.policies.domain.IdAssignmentItem.IdTarget;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.ptapp.Application;
import com.propertyvista.domain.tenant.ptapp.MasterApplication;
import com.propertyvista.server.common.ptapp.ApplicationManager;
import com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManager;
import com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerImpl;
import com.propertyvista.server.common.util.occupancy.UnitTurnoverAnalysisManager;
import com.propertyvista.server.common.util.occupancy.UnitTurnoverAnalysisManagerImpl;
import com.propertyvista.server.financial.billing.BillingFacade;
import com.propertyvista.server.financial.productcatalog.ProductCatalogFacade;

public class LeaseManager {

    private static final I18n i18n = I18n.get(LeaseManager.class);

    private final TimeContextProvider timeContextProvider;

    private final UnitTurnoverAnalysisManager turnoverAnalysisManager;

    public LeaseManager() {
        this(new TimeContextProvider() {
            @Override
            public LogicalDate getTimeContext() {
                return new LogicalDate();
            }
        });
    }

    public LeaseManager(TimeContextProvider timeContextProvider) {
        this(timeContextProvider, new UnitTurnoverAnalysisManagerImpl());
    }

    public LeaseManager(TimeContextProvider timeContextProvider, UnitTurnoverAnalysisManager turnoverAnalysisManager) {
        assert timeContextProvider != null;
        assert turnoverAnalysisManager != null;
        this.timeContextProvider = timeContextProvider;
        this.turnoverAnalysisManager = turnoverAnalysisManager;
    }

    public Lease create(String leaseId, Service.Type type, AptUnit unit, LogicalDate leaseFrom, LogicalDate leaseTo) {
        Lease lease = EntityFactory.create(Lease.class);

        lease.leaseId().setValue(leaseId);
        lease.type().setValue(type);
        lease.paymentFrequency().setValue(PaymentFrequency.Monthly);
        lease.unit().set(unit);
        lease.leaseFrom().setValue(leaseFrom);
        lease.leaseTo().setValue(leaseTo);

        lease.version().status().setValue(Lease.Status.Created);

        return lease;
    }

    public Lease save(Lease lease) {
        boolean isNewLease = (lease.getPrimaryKey() == null);
        boolean isUnitChanged = isNewLease;
        boolean doReserve = false;
        boolean doUnreserve = false;

        Lease oldLease = null;
        if (isNewLease) {
            doReserve = (lease.unit().getPrimaryKey() != null);
        } else {
            oldLease = Persistence.secureRetrieve(Lease.class, lease.getPrimaryKey().asCurrentKey());

            // check if unit reservation has changed
            Persistence.service().retrieve(oldLease.unit());
            Persistence.service().retrieve(lease.unit());
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

        if (lease.id().isNull() && IdAssignmentSequenceUtil.needsGeneratedId(IdTarget.lease)) {
            lease.leaseId().setValue(IdAssignmentSequenceUtil.getId(IdTarget.lease));
        }

        Persistence.secureSave(lease);

        // always create application for new lease (check if exist for others): 
        if (isNewLease || lease.application().isNull()) {
            try {
                ApplicationManager.createMasterApplication(lease);
            } catch (Exception e) {
                // ok, it seems there is no main applicant still...
                if (!isNewLease)
                    throw new Error(e);
            }
        }

        if (isUnitChanged) {
            if (doUnreserve) {
                occupancyManager(oldLease.unit().getPrimaryKey()).unreserve();
            }
            if (doReserve) {
                occupancyManager(lease.unit().getPrimaryKey()).reserve(lease);
            }
        }

        ServerSideFactory.create(ProductCatalogFacade.class).updateUnitRentPrice(lease);

        return lease;
    }

    public Lease notice(Key leaseId, LogicalDate noticeDay, LogicalDate moveOutDay) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        if (lease == null) {
            throw new IllegalStateException("lease " + leaseId + " was not found");
        }
        if (lease.version().status().getValue() != Status.Active) {
            throw new IllegalStateException("lease " + leaseId + " must be " + Status.Active + " in order to perform 'Notice'");
        }

        lease.version().completion().setValue(CompletionType.Notice);
        lease.version().moveOutNotice().setValue(noticeDay);
        lease.version().expectedMoveOut().setValue(moveOutDay);

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.secureSave(lease);

        occupancyManager(lease.unit().getPrimaryKey()).endLease();
        return lease;
    }

    public Lease cancelNotice(Key leaseId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        if (lease == null) {
            throw new IllegalStateException("lease " + leaseId + " was not found");
        }
        if (lease.version().completion().getValue() != CompletionType.Notice | lease.version().moveOutNotice().isNull()) {
            throw new IllegalStateException("lease " + leaseId + " must have notice in order to perform 'cancelNotice'");
        }
        lease.version().completion().setValue(null);
        lease.version().moveOutNotice().setValue(null);
        lease.version().expectedMoveOut().setValue(null);

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.secureSave(lease);

        occupancyManager(lease.unit().getPrimaryKey()).cancelEndLease();
        return lease;
    }

    public Lease evict(Key leaseId, LogicalDate evictionDay, LogicalDate moveOutDay) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        if (lease == null) {
            throw new IllegalStateException("lease " + leaseId + " was not found");
        }
        if (lease.version().status().getValue() != Status.Active) {
            throw new IllegalStateException("lease " + leaseId + " must be " + Status.Active + " in order to perform 'Evict'");
        }
        lease.version().completion().setValue(CompletionType.Eviction);
        lease.version().moveOutNotice().setValue(evictionDay);
        lease.version().expectedMoveOut().setValue(moveOutDay);

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.secureSave(lease);

        occupancyManager(lease.unit().getPrimaryKey()).endLease();
        return lease;
    }

    public Lease cancelEvict(Key leaseId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        if (lease == null) {
            throw new IllegalStateException("lease " + leaseId + " was not found");
        }
        if (lease.version().completion().getValue() != CompletionType.Notice | lease.version().moveOutNotice().isNull()) {
            throw new IllegalStateException("lease " + leaseId + " must have eviction in order to perform 'cancelEvict'");
        }
        lease.version().completion().setValue(null);
        lease.version().moveOutNotice().setValue(null);
        lease.version().expectedMoveOut().setValue(null);

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.secureSave(lease);

        occupancyManager(lease.unit().getPrimaryKey()).cancelEndLease();
        return lease;
    }

    public Lease approveApplication(Key leaseId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        lease.version().status().setValue(Status.Approved);
        lease.approvalDate().setValue(timeContextProvider.getTimeContext());

        // finalize approved leases while saving:
        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.secureSave(lease);

        updateApplicationReferencesToFinalVersionOfLase(lease);

        occupancyManager(lease.unit().getPrimaryKey()).approveLease();

        ServerSideFactory.create(BillingFacade.class).runBilling(lease);
        return lease;
    }

    public Lease declineApplication(Key leaseId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        lease.version().status().setValue(Status.Declined);
        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.secureSave(lease);

        updateApplicationReferencesToFinalVersionOfLase(lease);

        occupancyManager(lease.unit().getPrimaryKey()).unreserve();
        return lease;
    }

    public Lease cancelApplication(Key leaseId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        lease.version().status().setValue(Status.ApplicationCancelled);

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.secureSave(lease);

        updateApplicationReferencesToFinalVersionOfLase(lease);

        occupancyManager(lease.unit().getPrimaryKey()).unreserve();
        return lease;
    }

    private void updateApplicationReferencesToFinalVersionOfLase(Lease lease) {
        // update reference to first version of Lease in MasterApplication and Applications
        EntityQueryCriteria<MasterApplication> criteria = EntityQueryCriteria.create(MasterApplication.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().lease(), lease.getPrimaryKey().asDraftKey()));
        MasterApplication ma = Persistence.service().retrieve(criteria);
        if (ma != null) {
            ma.lease().set(lease);
            Persistence.service().retrieve(ma.applications());
            for (Application app : ma.applications()) {
                app.lease().set(lease);
            }
            Persistence.service().persist(ma);
        }
    }

    public Lease activate(Key leaseId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        // set lease status to active ONLY if first (latest till now) bill is confirmed: 
        if (ServerSideFactory.create(BillingFacade.class).getLatestBill(lease).billStatus().getValue() == Bill.BillStatus.Confirmed) {
            lease.version().status().setValue(Status.Active);

            lease.saveAction().setValue(SaveAction.saveAsFinal);
            Persistence.secureSave(lease);

            turnoverAnalysisManager.propagateLeaseActivationToTurnoverReport(lease);

            // update Lead state (if present)
            EntityQueryCriteria<Lead> criteria = new EntityQueryCriteria<Lead>(Lead.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().lease(), leaseId));
            Lead lead = Persistence.secureRetrieve(criteria);
            if (lead != null) {
                lead.status().setValue(Lead.Status.rented);
            }
        } else {
            throw new UserRuntimeException(i18n.tr("Please run and confirm first bill in order to activate the lease."));
        }
        return lease;
    }

    public Lease complete(Key leaseId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        lease.version().actualLeaseTo().setValue(timeContextProvider.getTimeContext());
        lease.version().status().setValue(Status.Completed);

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.secureSave(lease);
        return lease;
    }

    public Lease close(Key leaseId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        lease.version().status().setValue(Status.Closed);

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.secureSave(lease);
        return lease;
    }

    private AptUnitOccupancyManager occupancyManager(Key unitId) {
        return AptUnitOccupancyManagerImpl.get(unitId, new AptUnitOccupancyManagerImpl.NowSource() {
            @Override
            public LogicalDate getNow() {
                return timeContextProvider.getTimeContext();
            }
        });
    }

    public interface TimeContextProvider {
        LogicalDate getTimeContext();
    }
}
