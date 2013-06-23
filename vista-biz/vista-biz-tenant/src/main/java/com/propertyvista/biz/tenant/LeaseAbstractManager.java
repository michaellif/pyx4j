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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.shared.utils.VersionedEntityUtils;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.biz.financial.deposit.DepositFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.occupancy.OccupancyOperationException;
import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.validation.framework.ValidationFailure;
import com.propertyvista.biz.validation.validators.lease.LeaseApprovalValidator;
import com.propertyvista.biz.validation.validators.lease.ScreeningValidator;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.note.NotesAndAttachments;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.domain.LeaseBillingTypePolicyItem;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTerm.Type;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant.Role;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.shared.NotesParentId;
import com.propertyvista.shared.config.VistaFeatures;

public abstract class LeaseAbstractManager {

    private static final Logger log = LoggerFactory.getLogger(LeaseAbstractManager.class);

    private static final I18n i18n = I18n.get(LeaseAbstractManager.class);

    protected abstract BillingAccount createBillingAccount();

    protected abstract void onLeaseApprovalError(Lease lease, String error);

    protected abstract void onLeaseApprovalSuccess(Lease lease, Lease.Status leaseStatus);

    protected abstract void ensureLeaseUniqness(Lease lease);

    public Lease create(Status status) {
        Lease lease = EntityFactory.create(Lease.class);
        lease.status().setValue(status);
        return init(lease);
    }

    public Lease init(Lease lease) {
        // check client supplied initial status value:
        assert !lease.status().isNull();
        switch (lease.status().getValue()) {
        case Application:
            lease.leaseApplication().status().setValue(LeaseApplication.Status.Created);
            break; // ok, allowed value...
        case NewLease:
        case ExistingLease:
            lease.leaseApplication().setValue(null);
            break; // ok, allowed value...
        default:
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }

        ServerSideFactory.create(IdAssignmentFacade.class).assignId(lease);

        if (lease.type().isNull()) {
            lease.type().setValue(ARCode.Type.Residential);
        } else if (!ARCode.Type.unitRelatedServices().contains(lease.type().getValue())) {
            throw new IllegalStateException(SimpleMessageFormat.format("Unsupported Lease Type (\"{0}\")", lease.type().getValue()));
        }

        if (lease.currentTerm().isNull()) {
            lease.currentTerm().set(EntityFactory.create(LeaseTerm.class));
            lease.currentTerm().type().setValue(LeaseTerm.Type.FixedEx);
            lease.currentTerm().status().setValue(LeaseTerm.Status.Current);
        }
        lease.currentTerm().lease().set(lease);

        if (lease.billingAccount().isNull()) {
            lease.billingAccount().set(createBillingAccount());
        }
        lease.billingAccount().accountNumber().setValue(ServerSideFactory.create(IdAssignmentFacade.class).createAccountNumber());
        lease.billingAccount().paymentAccepted().setValue(BillingAccount.PaymentAccepted.Any);

        return lease;
    }

    public Lease setUnit(Lease lease, AptUnit unitId) {
        assert !lease.currentTerm().isNull();
        return setUnit(lease, lease.currentTerm(), unitId);
    }

    public Lease setService(Lease lease, ProductItem serviceId) {
        assert !lease.currentTerm().isNull();
        setService(lease, lease.currentTerm(), serviceId);
        return lease;
    }

    public Lease persist(Lease lease) {
        return persist(lease, false);
    }

    public Lease finalize(Lease lease) {
        return persist(lease, true);
    }

    public Lease load(Lease leaseId, boolean editingTerm) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
        if (lease == null) {
            throw new IllegalArgumentException("lease " + leaseId.getPrimaryKey() + " was not found");
        }

        // load current Term
        assert !lease.currentTerm().isNull();
        if (editingTerm || lease.currentTerm().version().isNull()) {
            lease.currentTerm().set(Persistence.retrieveDraftForEdit(LeaseTerm.class, lease.currentTerm().getPrimaryKey()));
        }

        // Load participants:
//        Persistence.service().retrieve(lease.currentTerm().version().tenants());
//        Persistence.service().retrieve(lease.currentTerm().version().guarantors());

        return lease;
    }

    // Lease term operations: -----------------------------------------------------------------------------------------

    public LeaseTerm setUnit(LeaseTerm leaseTerm, AptUnit unitId) {
        assert !leaseTerm.lease().isNull();
        setUnit(leaseTerm.lease(), leaseTerm, unitId);
        return leaseTerm;
    }

    public LeaseTerm setService(LeaseTerm leaseTerm, ProductItem serviceId) {
        assert !leaseTerm.lease().isNull();
        return setService(leaseTerm.lease(), leaseTerm, serviceId);
    }

    public LeaseTerm persist(LeaseTerm leaseTerm) {
        persistCustomers(leaseTerm);

        switch (leaseTerm.type().getValue()) {
        case Fixed:
            break;
        case FixedEx:
            break;
        case Periodic:
            leaseTerm.termTo().set(null); // open end term!..
            break;
        default:
            break;
        }

        Persistence.service().merge(leaseTerm);

        return leaseTerm;
    }

    public LeaseTerm finalize(LeaseTerm leaseTerm) {
        finalizeBillableItems(leaseTerm);

        // migrate participants:
        Persistence.ensureRetrieve(leaseTerm.version().tenants(), AttachLevel.Attached);
        for (LeaseTermTenant tenant : leaseTerm.version().tenants()) {
            tenant.screening().set(retrivePersonScreeningId(tenant.leaseParticipant().customer()));
        }

        Persistence.ensureRetrieve(leaseTerm.version().guarantors(), AttachLevel.Attached);
        for (LeaseTermGuarantor guarantor : leaseTerm.version().guarantors()) {
            guarantor.screening().set(retrivePersonScreeningId(guarantor.leaseParticipant().customer()));
        }

        leaseTerm.saveAction().setValue(SaveAction.saveAsFinal);
        leaseTerm = persist(leaseTerm);

        // update lease deposits if current term:
        Persistence.ensureRetrieve(leaseTerm.lease(), AttachLevel.Attached);
        if (leaseTerm.equals(leaseTerm.lease().currentTerm())) {
            updateLeaseDeposits(leaseTerm.lease());

            ServerSideFactory.create(PaymentMethodFacade.class).renewPreauthorizedPayments(leaseTerm.lease());
        }

        return leaseTerm;
    }

    // Operations: ----------------------------------------------------------------------------------------------------

    public void createMasterOnlineApplication(Lease leaseId) {
        Lease lease = load(leaseId, false);

        // Verify the status
        if (!Lease.Status.draft().contains(lease.status().getValue())) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }
        if (LeaseApplication.Status.Created != lease.leaseApplication().status().getValue()) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Application Status (\"{0}\")", lease.leaseApplication().status().getValue()));
        }

        ServerSideFactory.create(OnlineApplicationFacade.class).createMasterOnlineApplication(lease.leaseApplication().onlineApplication());

        lease.leaseApplication().status().setValue(LeaseApplication.Status.OnlineApplication);
        lease.status().setValue(Lease.Status.Application);
        Persistence.service().merge(lease);
    }

    void recordApplicationData(LeaseTerm leaseTerm) {
        // Confider adding screening().set() to this function
        Persistence.ensureRetrieve(leaseTerm.version().tenants(), AttachLevel.Attached);
        for (LeaseTermTenant leaseParticipant : leaseTerm.version().tenants()) {
            leaseParticipant.creditCheck().set(
                    ServerSideFactory.create(ScreeningFacade.class).retrivePersonCreditCheck(leaseParticipant.leaseParticipant().customer()));
        }

        Persistence.ensureRetrieve(leaseTerm.version().guarantors(), AttachLevel.Attached);
        for (LeaseTermGuarantor leaseParticipant : leaseTerm.version().guarantors()) {
            leaseParticipant.creditCheck().set(
                    ServerSideFactory.create(ScreeningFacade.class).retrivePersonCreditCheck(leaseParticipant.leaseParticipant().customer()));
        }
    }

    public CustomerScreening retrivePersonScreeningId(Customer customer) {
        if (ScreeningValidator.screeningIsAutomaticallyFinalized) {
            EntityQueryCriteria<CustomerScreening> criteria = EntityQueryCriteria.create(CustomerScreening.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().screene(), customer));
            criteria.setVersionedCriteria(VersionedCriteria.onlyDraft);
            CustomerScreening screening = Persistence.service().retrieve(criteria);
            if ((screening != null) && (!screening.version().isNull())) {
                screening.saveAction().setValue(SaveAction.saveAsFinal);
                Persistence.service().persist(screening);
            }
            return screening;
        } else {
            if (customer.personScreening().getAttachLevel() == AttachLevel.Detached) {
                Persistence.service().retrieveMember(customer.personScreening(), AttachLevel.IdOnly);
            }
            if (customer.personScreening().isNull()) {
                return null;
            } else {
                return customer.personScreening();
            }
        }
    }

    public void declineApplication(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = load(leaseId, false);

        // TODO Review the status
        lease.status().setValue(Lease.Status.Cancelled);
        lease.leaseApplication().status().setValue(LeaseApplication.Status.Declined);
        lease.leaseApplication().decidedBy().set(decidedBy);
        lease.leaseApplication().decisionReason().setValue(decisionReason);
        lease.leaseApplication().decisionDate().setValue(new LogicalDate(SystemDateManager.getDate()));

        recordApplicationData(lease.currentTerm());

        Persistence.service().merge(lease);

        ServerSideFactory.create(OccupancyFacade.class).unreserve(lease.unit().getPrimaryKey());

        if (!lease.leaseApplication().onlineApplication().isNull()) {
            Persistence.ensureRetrieve(lease.currentTerm().version().tenants(), AttachLevel.Attached);
            for (LeaseTermTenant tenant : lease.currentTerm().version().tenants()) {
                if (!tenant.application().isNull()) { // co-applicants have no dedicated application
                    ServerSideFactory.create(CommunicationFacade.class).sendApplicationStatus(tenant);
                }
            }
        }
    }

    public void cancelApplication(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = load(leaseId, false);

        lease.status().setValue(Lease.Status.Cancelled);
        lease.leaseApplication().status().setValue(LeaseApplication.Status.Cancelled);
        lease.leaseApplication().decidedBy().set(decidedBy);
        lease.leaseApplication().decisionReason().setValue(decisionReason);
        lease.leaseApplication().decisionDate().setValue(new LogicalDate(SystemDateManager.getDate()));

        Persistence.service().merge(lease);

        ServerSideFactory.create(OccupancyFacade.class).unreserve(lease.unit().getPrimaryKey());
    }

    public void approve(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = load(leaseId, false);

        Set<ValidationFailure> validationFailures = new LeaseApprovalValidator().validate(lease);
        if (!validationFailures.isEmpty()) {
            List<String> errorMessages = new ArrayList<String>();
            for (ValidationFailure failure : validationFailures) {
                errorMessages.add(failure.getMessage());
            }
            String jointErrors = StringUtils.join(errorMessages, ",\n");
            onLeaseApprovalError(lease, jointErrors);
        }

        // memorize entry LeaseStatus:
        Status leaseStatus = lease.status().getValue();

        lease.status().setValue(Lease.Status.Approved);
        lease.approvalDate().setValue(new LogicalDate(SystemDateManager.getDate()));

        if (leaseStatus == Status.Application) {
            lease.leaseApplication().status().setValue(LeaseApplication.Status.Approved);
            lease.leaseApplication().decidedBy().set(decidedBy);
            lease.leaseApplication().decisionReason().setValue(decisionReason);
            lease.leaseApplication().decisionDate().setValue(new LogicalDate(SystemDateManager.getDate()));

            recordApplicationData(lease.currentTerm());
        }

        finalize(lease);

        // Billing-related stuff:
        onLeaseApprovalSuccess(lease, leaseStatus);

        switch (leaseStatus) {
        case Application:
            if (!lease.leaseApplication().onlineApplication().isNull()) {
                Persistence.ensureRetrieve(lease.currentTerm().version().tenants(), AttachLevel.Attached);
                for (LeaseTermTenant tenant : lease.currentTerm().version().tenants()) {
                    if (!tenant.application().isNull()) { // co-applicants have no
                                                          // dedicated application
                        ServerSideFactory.create(CommunicationFacade.class).sendApplicationStatus(tenant);
                    }
                }
            }

            ServerSideFactory.create(OccupancyFacade.class).approveLease(lease.unit().getPrimaryKey());
            break;

        case NewLease:
            ServerSideFactory.create(OccupancyFacade.class).approveLease(lease.unit().getPrimaryKey());
            break;

        case ExistingLease:
            ServerSideFactory.create(OccupancyFacade.class).migratedApprove(lease.unit().<AptUnit> createIdentityStub());
            break;

        }

        updateUnitRentPrice(lease);

        // create historical billing cycles for imported leases
        BillingCycle cycle = ServerSideFactory.create(BillingCycleFacade.class).getLeaseFirstBillingCycle(lease);
        Date now = SystemDateManager.getDate();
        while (cycle.billingCycleStartDate().getValue().before(now)) {
            cycle = ServerSideFactory.create(BillingCycleFacade.class).getSubsequentBillingCycle(cycle);
        }
    }

    // TODO review code here
    public void activate(Lease leaseId) {
        Lease lease = load(leaseId, false);

        if (!EnumSet.of(Lease.Status.ExistingLease, Lease.Status.NewLease, Lease.Status.Approved).contains(lease.status().getValue())) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }

        // set lease status to active ONLY if first (latest till now) bill is confirmed:
// TODO
//        if (lease.billingAccount().carryforwardBalance().isNull()
//                && ServerSideFactory.create(BillingFacade.class).getLatestBill(lease).billStatus().getValue() != Bill.BillStatus.Confirmed) {
//            throw new UserRuntimeException(i18n.tr("Please run and confirm first bill in order to activate the lease."));
//        }

        lease.status().setValue(Status.Active);
        lease.activationDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        Persistence.service().merge(lease);

        ensureLeaseUniqness(lease);

        ServerSideFactory.create(LeadFacade.class).setLeadRentedState(lease);
    }

    public void renew(Lease leaseId) {
        Lease lease = load(leaseId, false);

        if (!lease.nextTerm().isNull()) {
            // update old:
            lease.previousTerm().set(lease.currentTerm());
            lease.previousTerm().status().setValue(LeaseTerm.Status.Historic);
            lease.previousTerm().version().setValueDetached(); // TRICK (saving just non-versioned part)!..
            persist(lease.previousTerm());

            // set new:
            lease.currentTerm().set(lease.nextTerm());
            Persistence.service().retrieve(lease.currentTerm());
            lease.currentTerm().status().setValue(LeaseTerm.Status.Current);
            updateLeaseDeposits(lease);

            // clear next reference:
            lease.nextTerm().set(null);

            // save lease with new current term:
            lease.currentTerm().version().setValueDetached(); // TRICK (saving just non-versioned part)!..
            persist(lease);
        } else {
            throw new IllegalArgumentException(i18n.tr("There is no term for renewal"));
        }
    }

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
        // if still has time to go:
        if (!lease.leaseTo().isNull() && !lease.leaseTo().getValue().before(new LogicalDate(SystemDateManager.getDate()))) {
            throw new IllegalStateException("Lease is not ended yet");
        }

        lease.status().setValue(Status.Completed);

        Persistence.service().merge(lease);
    }

    public void close(Lease leaseId) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());

        lease.status().setValue(Status.Closed);

        Persistence.service().merge(lease);
    }

    public LeaseTerm createOffer(Lease leaseId, Type type) {
        Lease lease = load(leaseId, false);

        LeaseTerm term = EntityFactory.create(LeaseTerm.class);
        term.status().setValue(LeaseTerm.Status.Offer);

        term.type().setValue(type);
        term.lease().set(lease);

        // set from date to next day after current term:
        term.termFrom().setValue(DateUtils.daysAdd(lease.currentTerm().termTo().getValue(), 1));

        updateTermUnitRelatedData(term, lease.unit(), lease.type().getValue());

        // migrate participants:
        Persistence.ensureRetrieve(lease.currentTerm().version().tenants(), AttachLevel.Attached);
        for (LeaseTermTenant tenant : lease.currentTerm().version().tenants()) {
            term.version().tenants().add(businessDuplicate(tenant));
        }
        Persistence.ensureRetrieve(lease.currentTerm().version().guarantors(), AttachLevel.Attached);
        for (LeaseTermGuarantor guarantor : lease.currentTerm().version().guarantors()) {
            term.version().guarantors().add(businessDuplicate(guarantor));
        }

        return term;
    }

    public void acceptOffer(Lease leaseId, LeaseTerm leaseTermId) {
        Lease lease = load(leaseId, false);
        LeaseTerm leaseTerm = Persistence.service().retrieve(LeaseTerm.class, leaseTermId.getPrimaryKey());

        Persistence.service().retrieveMember(lease.leaseTerms());
        if (leaseTerm.status().getValue() == LeaseTerm.Status.Offer && lease.leaseTerms().contains(leaseTermId)) {
            lease.nextTerm().set(leaseTerm);
            lease.nextTerm().status().setValue(LeaseTerm.Status.AcceptedOffer);
            lease.nextTerm().version().setValueDetached(); // TRICK (saving just non-versioned part)!..
            persist(lease.nextTerm());

            // save lease:
            Persistence.service().merge(lease);
        } else {
            throw new IllegalArgumentException(i18n.tr("Invalid LeaseTerm supplied"));
        }
    }

    public void createCompletionEvent(Lease leaseId, CompletionType completionType, LogicalDate eventDate, LogicalDate expectedMoveOut, LogicalDate leaseEndDate) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
        if (lease.status().getValue() != Status.Active) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }

        lease.completion().setValue(completionType);
        lease.moveOutSubmissionDate().setValue(eventDate);
        lease.expectedMoveOut().setValue(expectedMoveOut);

        switch (completionType) {
        case Eviction:
            break;
        case Notice:
            break;
        case Skip:
            break;
        case Termination:
            lease.terminationLeaseTo().setValue(leaseEndDate);
            break;
        default:
            break;
        }

        updateLeaseDates(lease);

        Persistence.service().merge(lease);

        try {
            ServerSideFactory.create(OccupancyFacade.class).moveOut(lease.unit().getPrimaryKey(), lease.expectedMoveOut().getValue(), lease);
        } catch (OccupancyOperationException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public void cancelCompletionEvent(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
        if (lease.status().getValue() != Status.Active) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }

        CompletionType completionType = lease.completion().getValue();

        lease.completion().setValue(null);
        lease.moveOutSubmissionDate().setValue(null);
        lease.expectedMoveOut().setValue(null);

        switch (completionType) {
        case Eviction:
            break;
        case Notice:
            break;
        case Skip:
            break;
        case Termination:
            lease.terminationLeaseTo().setValue(null);
            break;
        default:
            break;
        }

        updateLeaseDates(lease);

        Persistence.service().merge(lease);

        Persistence.service().merge(
                creteLeaseNote(leaseId, "Cancel " + completionType.toString(), decisionReason, (decidedBy != null ? decidedBy.user() : null)));

        try {
            ServerSideFactory.create(OccupancyFacade.class).cancelMoveOut(lease.unit().getPrimaryKey());
        } catch (OccupancyOperationException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public void moveOut(Lease leaseId, LogicalDate actualMoveOut) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());

        // Verify the status
        if (!lease.status().getValue().isOperative()) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }
        // if not moving out:
        if (lease.completion().isNull()) {
            throw new IllegalStateException(SimpleMessageFormat.format("Lease " + leaseId.getPrimaryKey() + " has no completion event"));
        }

        lease.actualMoveOut().setValue(actualMoveOut);

        updateLeaseDates(lease);

        Persistence.service().merge(lease);

        AptUnitOccupancySegment segment = ServerSideFactory.create(OccupancyFacade.class).getOccupancySegment(lease.unit(), lease.actualMoveOut().getValue());
        // if unit is not reserved/leased for new application/lease yet - correct the move out date:
        if (segment.lease().equals(lease)) {
            try {
                ServerSideFactory.create(OccupancyFacade.class).moveOut(lease.unit().getPrimaryKey(), lease.actualMoveOut().getValue(), lease);
            } catch (OccupancyOperationException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
    }

    public void cancelLease(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());

        Status status = lease.status().getValue();
        lease.status().setValue(Status.Cancelled);

        Persistence.service().merge(lease);

        switch (status) {
        case ExistingLease:
            ServerSideFactory.create(OccupancyFacade.class).migratedCancel(lease.unit().<AptUnit> createIdentityStub());
            break;

        case NewLease:
        case Approved:
            ServerSideFactory.create(OccupancyFacade.class).unreserve(lease.unit().getPrimaryKey());
            break;

        default:
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }

        Persistence.service().merge(creteLeaseNote(leaseId, "Cancel Lease", decisionReason, decidedBy.user()));
    }

    // Utils : --------------------------------------------------------------------------------------------------------

    public BillableItem createBillableItem(Lease lease, ProductItem productItemId, PolicyNode node) {
        Persistence.ensureRetrieve(lease, AttachLevel.Attached);

        ProductItem productItem = Persistence.service().retrieve(ProductItem.class, productItemId.getPrimaryKey());
        assert productItem != null;
        Persistence.ensureRetrieve(productItem.product(), AttachLevel.Attached);

        BillableItem newItem = EntityFactory.create(BillableItem.class);
        newItem.item().set(productItem);
        newItem.agreedPrice().setValue(productItem.price().getValue());

        // avoid policed deposits for existing Leases:
        if (lease.status().getValue() != Lease.Status.ExistingLease) {
            // set policed deposits:
            List<Deposit> deposits = ServerSideFactory.create(DepositFacade.class).createRequiredDeposits(newItem, node);
            if (deposits != null) {
                newItem.deposits().addAll(deposits);
            }
        }

        return newItem;
    }

    public void setLeaseAgreedPrice(Lease lease, BigDecimal price) {
        Persistence.ensureRetrieve(lease, AttachLevel.Attached);

        lease.currentTerm().version().leaseProducts().serviceItem().agreedPrice().setValue(price);
    }

    /**
     * This is a temporary solution for lease renewal (see VISTA-1789 and VISTA-2245)
     */
    public void simpleLeaseRenew(Lease leaseId, LogicalDate leaseEndDate) {
        Lease lease = load(leaseId, true);

        // Verify the status
        if (lease.status().getValue() != Lease.Status.Active) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }

        lease.currentTerm().termTo().setValue(leaseEndDate);

        finalize(lease);
    }

    // Internals: -----------------------------------------------------------------------------------------------------

    private Lease setUnit(Lease lease, LeaseTerm leaseTerm, AptUnit unitId) {
        Persistence.ensureRetrieve(lease, AttachLevel.Attached);

        if (!Lease.Status.draft().contains(lease.status().getValue())) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }

        if (VersionedEntityUtils.equalsIgnoreVersion(lease.currentTerm(), leaseTerm)) {
            AptUnit unit = Persistence.service().retrieve(AptUnit.class, unitId.getPrimaryKey());
            Persistence.ensureRetrieve(unit.building(), AttachLevel.Attached);

            lease.unit().set(unit);

            // set LeaseBillingPolicy offsets
            LeaseBillingPolicy billingPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(unit.building(), LeaseBillingPolicy.class);
            boolean policyFound = false;
            for (LeaseBillingTypePolicyItem billingType : billingPolicy.availableBillingTypes()) {
                if (billingType.billingPeriod().getValue().equals(lease.billingAccount().billingPeriod().getValue())) {
                    lease.billingAccount().billingCycleStartDay().set(billingType.billingCycleStartDay());
                    lease.billingAccount().paymentDueDayOffset().set(billingType.paymentDueDayOffset());
                    lease.billingAccount().finalDueDayOffset().set(billingType.finalDueDayOffset());
                    policyFound = true;
                    break;
                }
            }
            if (!policyFound) {
                throw new IllegalArgumentException(i18n.tr("No Billing policy found for: {0}", lease.billingAccount().billingPeriod().getValue()));
            }

            updateTermUnitRelatedData(leaseTerm, lease.unit(), lease.type().getValue());
        } else {
            throw new IllegalArgumentException(i18n.tr("Invalid Lease/Term pair supplied"));
        }

        return lease;
    }

    private LeaseTerm setService(Lease lease, LeaseTerm leaseTerm, ProductItem serviceId) {
        Persistence.ensureRetrieve(lease, AttachLevel.Attached);
        Persistence.ensureRetrieve(leaseTerm, AttachLevel.Attached);

        // find/load all necessary ingredients:
        assert !lease.unit().isNull();
        Persistence.ensureRetrieve(lease.unit(), AttachLevel.Attached);

        assert !lease.unit().building().isNull();
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);

        ProductItem serviceItem = Persistence.service().retrieve(ProductItem.class, serviceId.getPrimaryKey());
        assert serviceItem != null;
        Persistence.ensureRetrieve(serviceItem.element(), AttachLevel.Attached);

        // double check:
        if (!lease.unit().equals(serviceItem.element())) {
            throw new IllegalArgumentException(i18n.tr("Invalid Unit/Service combination"));
        }

        PolicyNode node = lease.unit().building();

        // set selected service:
        BillableItem billableItem = createBillableItem(lease, serviceItem, node);
        leaseTerm.version().leaseProducts().serviceItem().set(billableItem);

        if (leaseTerm.equals(lease.currentTerm())) {
            if (!Lease.Status.draft().contains(lease.status().getValue())) {
                throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
            }
            if (false) {
                // TODO This code never worked propely because deposits() are @Owned(cascade = {})
                Persistence.service().retrieve(lease.billingAccount().deposits());
                lease.billingAccount().deposits().clear();
            }
        }

        // Service by Service item:
        Service.ServiceV service = null;
        Persistence.service().retrieve(serviceItem.product());
        if (serviceItem.product().getInstanceValueClass().equals(Service.ServiceV.class)) {
            service = serviceItem.product().cast();
        }
        assert service != null;

        // clear current dependable data:
        leaseTerm.version().leaseProducts().featureItems().clear();
        leaseTerm.version().leaseProducts().concessions().clear();

        // pre-populate mandatory features for the new service:
        Persistence.service().retrieve(service.features());
        for (Feature feature : service.features()) {
            if (feature.version().mandatory().isBooleanTrue()) {
                Persistence.service().retrieve(feature.version().items());
                for (ProductItem item : feature.version().items()) {
                    if (item.isDefault().isBooleanTrue()) {
                        leaseTerm.version().leaseProducts().featureItems().add(createBillableItem(lease, item, node));
                        break;
                    }
                }
            }
        }

        return leaseTerm;
    }

    protected void finalizeBillableItems(LeaseTerm leaseTerm) {
        leaseTerm.version().leaseProducts().serviceItem().finalized().setValue(Boolean.TRUE);
        for (BillableItem item : leaseTerm.version().leaseProducts().featureItems()) {
            item.finalized().setValue(Boolean.TRUE);
        }
    }

    private Lease persist(Lease lease, boolean finalize) {
        boolean doReserve = false;
        boolean doUnreserve = false;
        Lease previousLeaseEdition = null;

        if (lease.status().getValue().isDraft()) {
            if (lease.getPrimaryKey() == null) {
                doReserve = !lease.unit().isNull();
            } else {
                previousLeaseEdition = Persistence.service().retrieve(Lease.class, lease.getPrimaryKey());
                if (!EqualsHelper.equals(previousLeaseEdition.unit().getPrimaryKey(), lease.unit().getPrimaryKey())) {
                    doUnreserve = previousLeaseEdition.unit().getPrimaryKey() != null;
                    doReserve = lease.unit().getPrimaryKey() != null;
                }
            }
        }

        // actual persist mechanics::
        if (lease.currentTerm().getPrimaryKey() == null) {
            LeaseTerm term = lease.currentTerm().detach();

            lease.currentTerm().set(null);
            Persistence.service().merge(lease);
            lease.currentTerm().set(term);

            lease.currentTerm().lease().set(lease);
        }

        if (finalize) {
            finalize(lease.currentTerm());
        } else {
            persist(lease.currentTerm());
        }

        updateLeaseApplicantReference(lease);
        updateLeaseDates(lease);
        updateBillingType(lease);

        // ensure non-null member(s):
        if (lease.billingAccount().paymentAccepted().isNull()) {
            lease.billingAccount().paymentAccepted().setValue(BillingAccount.PaymentAccepted.Any);
        }

        Persistence.service().merge(lease);
        Persistence.service().merge(lease.billingAccount());

        // update reservation if necessary:
        if (doUnreserve) {
            switch (lease.status().getValue()) {
            case NewLease:
            case Application:
                ServerSideFactory.create(OccupancyFacade.class).unreserve(previousLeaseEdition.unit().getPrimaryKey());
                break;
            case ExistingLease:
                ServerSideFactory.create(OccupancyFacade.class).migratedCancel(previousLeaseEdition.unit().<AptUnit> createIdentityStub());
                break;
            default:
                throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
            }
        }
        if (doReserve) {
            switch (lease.status().getValue()) {
            case NewLease:
            case Application:
                ServerSideFactory.create(OccupancyFacade.class).reserve(lease.unit().getPrimaryKey(), lease);
                break;
            case ExistingLease:
                ServerSideFactory.create(OccupancyFacade.class).migrateStart(lease.unit().<AptUnit> createIdentityStub(), lease);
                break;
            default:
                throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
            }
        }

        return lease;
    }

    // Internals: -----------------------------------------------------------------------------------------------------

    private void persistCustomers(LeaseTerm leaseTerm) {
        for (LeaseTermTenant tenant : leaseTerm.version().tenants()) {
            if (!tenant.isValueDetached()) {
                persistLeaseCustomer(leaseTerm, tenant, Tenant.class);
            }
        }
        for (LeaseTermGuarantor guarantor : leaseTerm.version().guarantors()) {
            if (!guarantor.isValueDetached()) {
                persistLeaseCustomer(leaseTerm, guarantor, Guarantor.class);
            }
        }
    }

    private <E extends LeaseParticipant<?>, P extends LeaseTermParticipant<?>> void persistLeaseCustomer(LeaseTerm leaseTerm, P leaseParticipant,
            Class<E> leaseCustomerClass) {
        boolean newCustomer = leaseParticipant.leaseParticipant().customer().id().isNull();

        if (!leaseParticipant.leaseParticipant().customer().isValueDetached()) {
            ServerSideFactory.create(CustomerFacade.class).persistCustomer(leaseParticipant.leaseParticipant().customer());
        }

        // Is new LeaseCustomer find or create new
        if (leaseParticipant.leaseParticipant().id().isNull()) {
            E leaseCustomer = null;

            if (!newCustomer) {
                EntityQueryCriteria<E> criteria = EntityQueryCriteria.create(leaseCustomerClass);
                criteria.add(PropertyCriterion.eq(criteria.proto().lease(), leaseTerm.lease()));
                criteria.add(PropertyCriterion.eq(criteria.proto().customer(), leaseParticipant.leaseParticipant().customer()));
                leaseCustomer = Persistence.service().retrieve(criteria);
            }

            if (leaseCustomer == null) {
                Customer customer = leaseParticipant.leaseParticipant().customer();
                leaseCustomer = EntityFactory.create(leaseCustomerClass);
                leaseCustomer.lease().set(leaseTerm.lease());
                leaseCustomer.customer().set(customer);
                ServerSideFactory.create(IdAssignmentFacade.class).assignId(leaseCustomer);
                // case of user-assignable ID:
                if (leaseCustomer.participantId().isNull() && !leaseParticipant.leaseParticipant().participantId().isNull()) {
                    leaseCustomer.participantId().set(leaseParticipant.leaseParticipant().participantId());
                }

                Persistence.service().persist(leaseCustomer);
            }

            // Copy value to member and update other references in graph
            leaseParticipant.leaseParticipant().id().set(leaseCustomer.id());
            leaseParticipant.leaseParticipant().lease().set(leaseTerm.lease());
            leaseParticipant.leaseParticipant().participantId().set(leaseCustomer.participantId());
        }
    }

    private NotesAndAttachments creteLeaseNote(Lease leaseId, String subject, String note, CrmUser user) {
        NotesAndAttachments naa = EntityFactory.create(NotesAndAttachments.class);

        new NotesParentId(leaseId).setOwner(naa);

        naa.subject().setValue(subject);
        naa.note().setValue(note);

        naa.user().set(user);

        return naa;
    }

    public void updateLeaseDates(Lease lease) {
        if (lease.status().getValue().isDraft()) {
            assert (!lease.currentTerm().isEmpty());

            lease.leaseFrom().set(lease.currentTerm().termFrom());
            lease.leaseTo().set(lease.currentTerm().termTo());
        } else {
            EntityQueryCriteria<LeaseTerm> criteria = new EntityQueryCriteria<LeaseTerm>(LeaseTerm.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().lease(), lease));
            criteria.add(PropertyCriterion.ne(criteria.proto().status(), LeaseTerm.Status.Offer));
            // set sorting by 'from date':
            criteria.setSorts(new Vector<Sort>(Arrays.asList(new Sort(criteria.proto().termFrom().getPath().toString(), false))));

            List<LeaseTerm> terms = Persistence.service().query(criteria);
            assert (!terms.isEmpty());

            lease.leaseFrom().set(terms.get(0).termFrom());
            lease.leaseTo().set(terms.get(terms.size() - 1).termTo());
        }

        // some common checks/corrections:
        if (lease.expectedMoveIn().isNull()) {
            lease.expectedMoveIn().setValue(lease.leaseFrom().getValue());
        }
        if (lease.completion().isNull()) {
            lease.expectedMoveOut().setValue(null);
        }

        // current term type-related corrections:
        switch (lease.currentTerm().type().getValue()) {
        case Fixed:
        case FixedEx:
            if (lease.completion().isNull()) {
                lease.expectedMoveOut().setValue(lease.currentTerm().termTo().getValue());
            }
            break;
        case Periodic:
            break;
        default:
            break;
        }

        // next term type-related corrections:
        if (lease.completion().isNull() && !lease.nextTerm().isNull()) {
            switch (lease.nextTerm().type().getValue()) {
            case Fixed:
            case FixedEx:
                lease.expectedMoveOut().setValue(lease.nextTerm().termTo().getValue());
                break;
            case Periodic:
                break;
            default:
                break;
            }
        }

        // if lease to be terminated:
        if (!lease.terminationLeaseTo().isNull()) {
            lease.leaseTo().setValue(lease.terminationLeaseTo().getValue());
        }
    }

    private void updateLeaseApplicantReference(Lease lease) {
        Persistence.ensureRetrieve(lease.currentTerm().version().tenants(), AttachLevel.Attached);
        for (LeaseTermTenant leaseTermTenant : lease.currentTerm().version().tenants()) {
            if (leaseTermTenant.role().getValue() == Role.Applicant) {
                lease._applicant().set(leaseTermTenant.leaseParticipant());
                break;
            }
        }
    }

    private void updateLeaseDeposits(Lease lease) {
        List<Deposit> currentDeposits = new ArrayList<Deposit>();
        currentDeposits.addAll(lease.currentTerm().version().leaseProducts().serviceItem().deposits());
        for (BillableItem item : lease.currentTerm().version().leaseProducts().featureItems()) {
            currentDeposits.addAll(item.deposits());
        }

        // wrap newly added deposits in DepositLifecycle:
        for (Deposit deposit : currentDeposits) {
            if (deposit.lifecycle().isNull()) {
                Persistence.service().persist(ServerSideFactory.create(DepositFacade.class).createDepositLifecycle(deposit, lease.billingAccount()));
                Persistence.service().merge(deposit);
            }
        }
    }

    private LeaseTerm updateTermUnitRelatedData(LeaseTerm leaseTerm, AptUnit unit, ARCode.Type leaseType) {
        Persistence.ensureRetrieve(unit, AttachLevel.Attached);
        Persistence.ensureRetrieve(unit.building(), AttachLevel.Attached);
        Persistence.ensureRetrieve(leaseTerm.lease(), AttachLevel.Attached);

        boolean succeeded = false;

        // use default product catalog items for specific cases:
        boolean useDefaultCatalog = (VistaFeatures.instance().defaultProductCatalog() || leaseTerm.lease().status().getValue() == Lease.Status.ExistingLease);

        EntityQueryCriteria<Service> serviceCriteria = new EntityQueryCriteria<Service>(Service.class);
        serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().catalog(), unit.building().productCatalog()));
        serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().type(), leaseType));
        serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().isDefaultCatalogItem(), useDefaultCatalog));
        serviceCriteria.isCurrent(serviceCriteria.proto().version());

        for (Service service : Persistence.service().query(serviceCriteria)) {
            EntityQueryCriteria<ProductItem> productCriteria = EntityQueryCriteria.create(ProductItem.class);
            productCriteria.add(PropertyCriterion.eq(productCriteria.proto().product(), service.version()));
            productCriteria.add(PropertyCriterion.eq(productCriteria.proto().element(), unit));

            ProductItem serviceItem = Persistence.service().retrieve(productCriteria);
            if (serviceItem != null) {
                setService(leaseTerm, serviceItem);
                succeeded = true;
                break; // use first found service/item
            }
        }

        if (!succeeded) {
            throw new UserRuntimeException(i18n.tr("There no service ''{0}'' for selected unit: {1} from Building: {2}", leaseType.toString(),
                    unit.getStringView(), unit.building().getStringView()));
        }

        return leaseTerm;
    }

    private void updateUnitRentPrice(Lease lease) {
        Persistence.service().retrieve(lease.unit());

        BigDecimal origPrice = lease.unit().financial()._unitRent().getValue();
        BigDecimal currentPrice = null;
        if (!lease.currentTerm().version().isNull() && !lease.currentTerm().version().leaseProducts().serviceItem().isNull()) {
            currentPrice = lease.currentTerm().version().leaseProducts().serviceItem().agreedPrice().getValue();
        }

        if ((origPrice != null && !origPrice.equals(currentPrice)) || (origPrice == null && currentPrice != null)) {
            lease.unit().financial()._unitRent().setValue(currentPrice);
            Persistence.service().merge(lease.unit());
        }
    }

    private void updateBillingType(Lease lease) {
        if (!lease.leaseFrom().isNull() && !lease.unit().isNull()) {
            lease.billingAccount().billingType().set(ServerSideFactory.create(BillingCycleFacade.class).getBillingType(lease));
        } else {
            log.debug("Can't retrieve Billing Type!..");
        }
    }

    private <P extends LeaseTermParticipant<?>> P businessDuplicate(P leaseParticipant) {
        // There are no own entities for now,
        Persistence.retrieveOwned(leaseParticipant);
        P copy = EntityGraph.businessDuplicate(leaseParticipant);
        copy.screening().set(null);
        return copy;
    }
}