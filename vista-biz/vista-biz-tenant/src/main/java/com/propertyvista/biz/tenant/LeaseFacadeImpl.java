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
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.deposit.DepositFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.occupancy.OccupancyOperationException;
import com.propertyvista.biz.occupancy.UnitTurnoverAnalysisFacade;
import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.biz.validation.framework.ValidationFailure;
import com.propertyvista.biz.validation.validators.lease.LeaseApprovalValidator;
import com.propertyvista.biz.validation.validators.lease.ScreeningValidator;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Bill.BillType;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.Service.ServiceType;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.note.NotesAndAttachments;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.security.CrmUser;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTerm.Type;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTermTenant;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.shared.NotesParentId;

public class LeaseFacadeImpl implements LeaseFacade {

    private static final Logger log = LoggerFactory.getLogger(LeaseFacadeImpl.class);

    private static final I18n i18n = I18n.get(LeaseFacadeImpl.class);

    private static final long ONE_DAY_IN_MSEC = 1000L * 60L * 60L * 24L;

    @Override
    public Lease create(Status status) {
        Lease lease = EntityFactory.create(Lease.class);
        lease.status().setValue(status);
        return init(lease);
    }

    @Override
    public Lease init(Lease lease) {
        // check client supplied initial status value:
        assert !lease.status().isNull();
        switch (lease.status().getValue()) {
        case Application:
            lease.leaseApplication().status().setValue(LeaseApplication.Status.Created);
            break; // ok, allowed value...
        case ExistingLease:
            lease.leaseApplication().setValue(null);
            break; // ok, allowed value...
        default:
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }

        ServerSideFactory.create(IdAssignmentFacade.class).assignId(lease);

        // TODO could be more variants in the future:
        lease.paymentFrequency().setValue(PaymentFrequency.Monthly);
        lease.type().setValue(Service.ServiceType.residentialUnit);

        if (lease.currentTerm().isNull()) {
            lease.currentTerm().set(EntityFactory.create(LeaseTerm.class));
            lease.currentTerm().type().setValue(LeaseTerm.Type.FixedEx);
            lease.currentTerm().status().setValue(LeaseTerm.Status.Current);
        }
        lease.currentTerm().lease().set(lease);

        lease.billingAccount().accountNumber().setValue(ServerSideFactory.create(IdAssignmentFacade.class).createAccountNumber());
        lease.billingAccount().billCounter().setValue(0);

        return lease;
    }

    @Override
    public Lease setUnit(Lease lease, AptUnit unitId) {
        assert !lease.currentTerm().isNull();
        return setUnit(lease, lease.currentTerm(), unitId);
    }

    @Override
    public Lease setService(Lease lease, ProductItem serviceId) {
        assert !lease.currentTerm().isNull();
        setService(lease, lease.currentTerm(), serviceId);
        return lease;
    }

    @Override
    public Lease persist(Lease lease) {
        return persist(lease, false);
    }

    @Override
    public Lease finalize(Lease lease) {
        return persist(lease, true);
    }

    @Override
    public Lease load(Lease leaseId, boolean editingTerm) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId.getPrimaryKey());
        if (lease == null) {
            throw new IllegalArgumentException("lease " + leaseId.getPrimaryKey() + " was not found");
        }

        // load current Term
        assert !lease.currentTerm().isNull();
        Persistence.service().retrieve(lease.currentTerm());
        if (editingTerm || lease.currentTerm().version().isNull()) {
            lease.currentTerm().set(Persistence.retrieveDraftForEdit(LeaseTerm.class, lease.currentTerm().getPrimaryKey()));
        }

        // Load participants:
//        Persistence.service().retrieve(lease.currentTerm().version().tenants());
//        Persistence.service().retrieve(lease.currentTerm().version().guarantors());

        return lease;
    }

    // Lease term operations:

    @Override
    public LeaseTerm setUnit(LeaseTerm leaseTerm, AptUnit unitId) {
        assert !leaseTerm.lease().isNull();
        setUnit(leaseTerm.lease(), leaseTerm, unitId);
        return leaseTerm;
    }

    @Override
    public LeaseTerm setService(LeaseTerm leaseTerm, ProductItem serviceId) {
        assert !leaseTerm.lease().isNull();
        return setService(leaseTerm.lease(), leaseTerm, serviceId);
    }

    @Override
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

        Persistence.secureSave(leaseTerm);

        return leaseTerm;
    }

    @Override
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
        if (leaseTerm.lease().isValueDetached()) {
            Persistence.service().retrieve(leaseTerm.lease());
        }
        if (leaseTerm.equals(leaseTerm.lease().currentTerm())) {
            updateLeaseDeposits(leaseTerm.lease());
        }

        return leaseTerm;
    }

    @Override
    public BillableItem createBillableItem(ProductItem itemId, PolicyNode node) {
        ProductItem item = Persistence.secureRetrieve(ProductItem.class, itemId.getPrimaryKey());
        assert item != null;

        BillableItem newItem = EntityFactory.create(BillableItem.class);
        newItem.item().set(item);
        newItem.agreedPrice().setValue(item.price().getValue());

        // set policed deposits:
        List<Deposit> deposits = ServerSideFactory.create(DepositFacade.class).createRequiredDeposits(newItem, node);
        if (deposits != null) {
            newItem.deposits().addAll(deposits);
        }

        return newItem;
    }

    // Operations:

    @Override
    public void createMasterOnlineApplication(Lease leaseId) {
        Lease lease = load(leaseId, false);

        // Verify the status
        if (!Lease.Status.draft().contains(lease.status().getValue())) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }
        if (LeaseApplication.Status.Created != lease.leaseApplication().status().getValue()) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Applicatino Status (\"{0}\")", lease.leaseApplication().status().getValue()));
        }

        ServerSideFactory.create(OnlineApplicationFacade.class).createMasterOnlineApplication(lease.leaseApplication().onlineApplication());

        lease.leaseApplication().status().setValue(LeaseApplication.Status.OnlineApplication);
        lease.status().setValue(Lease.Status.Application);
        Persistence.secureSave(lease);
    }

    @Override
    public void approveApplication(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = load(leaseId, false);

        Set<ValidationFailure> validationFailures = new LeaseApprovalValidator().validate(lease);
        if (!validationFailures.isEmpty()) {
            List<String> errorMessages = new ArrayList<String>();
            for (ValidationFailure failure : validationFailures) {
                errorMessages.add(failure.getMessage());
            }
            String errorsRoster = StringUtils.join(errorMessages, ",\n");
            throw new UserRuntimeException(i18n.tr("This lease cannot be approved due to following validation errors:\n{0}", errorsRoster));
        }

        lease.status().setValue(Lease.Status.Approved);
        lease.approvalDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        lease.leaseApplication().status().setValue(LeaseApplication.Status.Approved);
        lease.leaseApplication().decidedBy().set(decidedBy);
        lease.leaseApplication().decisionReason().setValue(decisionReason);
        lease.leaseApplication().decisionDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));

        recordApplicationData(lease.currentTerm());

        finalize(lease);

        ServerSideFactory.create(OccupancyFacade.class).approveLease(lease.unit().getPrimaryKey());

        updateUnitRentPrice(lease);

        Bill bill = ServerSideFactory.create(BillingFacade.class).runBilling(lease);

        if (bill.billStatus().getValue() == Bill.BillStatus.Failed) {
            throw new UserRuntimeException(i18n.tr("This lease cannot be approved due to failed first time bill"));
        }

        if (bill.billStatus().getValue() != Bill.BillStatus.Confirmed) {
            ServerSideFactory.create(BillingFacade.class).confirmBill(bill);
        }

        if (!lease.leaseApplication().onlineApplication().isNull()) {
            Persistence.service().retrieve(lease.currentTerm().version().tenants());
            for (LeaseTermTenant tenant : lease.currentTerm().version().tenants()) {
                if (!tenant.application().isNull()) { // co-applicants have no
                                                      // dedicated application
                    ServerSideFactory.create(CommunicationFacade.class).sendApplicationStatus(tenant);
                }
            }
        }
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

    @Override
    public void declineApplication(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = load(leaseId, false);

        // TODO Review the status
        lease.status().setValue(Lease.Status.Cancelled);
        lease.leaseApplication().status().setValue(LeaseApplication.Status.Declined);
        lease.leaseApplication().decidedBy().set(decidedBy);
        lease.leaseApplication().decisionReason().setValue(decisionReason);
        lease.leaseApplication().decisionDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));

        recordApplicationData(lease.currentTerm());

        Persistence.secureSave(lease);

        ServerSideFactory.create(OccupancyFacade.class).unreserve(lease.unit().getPrimaryKey());

        if (!lease.leaseApplication().onlineApplication().isNull()) {
            for (LeaseTermTenant tenant : lease.currentTerm().version().tenants()) {
                if (!tenant.application().isNull()) { // co-applicants have no dedicated application
                    ServerSideFactory.create(CommunicationFacade.class).sendApplicationStatus(tenant);
                }
            }
        }
    }

    @Override
    public void cancelApplication(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = load(leaseId, false);

        // TODO Review the status
        lease.status().setValue(Lease.Status.Cancelled);
        lease.leaseApplication().status().setValue(LeaseApplication.Status.Cancelled);
        lease.leaseApplication().decidedBy().set(decidedBy);
        lease.leaseApplication().decisionReason().setValue(decisionReason);
        lease.leaseApplication().decisionDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));

        Persistence.secureSave(lease);

        ServerSideFactory.create(OccupancyFacade.class).unreserve(lease.unit().getPrimaryKey());
    }

    @Override
    public void approveExistingLease(Lease leaseId) {
        Lease lease = load(leaseId, false);

        Set<ValidationFailure> validationFailures = new LeaseApprovalValidator().validate(lease);
        if (!validationFailures.isEmpty()) {
            List<String> errorMessages = new ArrayList<String>();
            for (ValidationFailure failure : validationFailures) {
                errorMessages.add(failure.getMessage());
            }
            String errorsRoster = StringUtils.join(errorMessages, ",\n");
            throw new UserRuntimeException(i18n.tr("This lease cannot be approved due to following validation errors:\n{0}", errorsRoster));
        }

        lease.status().setValue(Lease.Status.Approved);
        lease.approvalDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));

        finalize(lease);

        ServerSideFactory.create(OccupancyFacade.class).migratedApprove(lease.unit().<AptUnit> createIdentityStub());

        updateUnitRentPrice(lease);

        BillingFacade billingFacade = ServerSideFactory.create(BillingFacade.class);
        Bill bill = billingFacade.runBilling(lease);
        if (bill.billStatus().getValue() != Bill.BillStatus.Failed) {
            if (bill.billStatus().getValue() != Bill.BillStatus.Confirmed) {
                billingFacade.confirmBill(bill);
            }
            // for zero cycle bill also create the next bill if we are past the executionTargetDate of the cycle
            Date curDate = Persistence.service().getTransactionSystemTime();
            LogicalDate nextExecDate = billingFacade.getNextCycleExecutionDate(bill.billingCycle());
            if (BillType.ZeroCycle.equals(bill.billType().getValue()) && !curDate.before(nextExecDate)) {
                billingFacade.runBilling(lease);
            }
        } else {
            throw new UserRuntimeException(i18n.tr("This lease cannot be approved due to failed first time bill"));
        }
    }

    // TODO review code here
    @Override
    public void activate(Lease leaseId) {
        Lease lease = load(leaseId, false);

        if (!EnumSet.of(Lease.Status.ExistingLease, Lease.Status.Approved).contains(lease.status().getValue())) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }

        // set lease status to active ONLY if first (latest till now) bill is confirmed:
// TODO
//        if (lease.billingAccount().carryforwardBalance().isNull()
//                && ServerSideFactory.create(BillingFacade.class).getLatestBill(lease).billStatus().getValue() != Bill.BillStatus.Confirmed) {
//            throw new UserRuntimeException(i18n.tr("Please run and confirm first bill in order to activate the lease."));
//        }

        lease.status().setValue(Status.Active);
        lease.activationDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        Persistence.secureSave(lease);

        ServerSideFactory.create(UnitTurnoverAnalysisFacade.class).propagateLeaseActivationToTurnoverReport(lease);
        ServerSideFactory.create(LeadFacade.class).setLeadRentedState(lease);
    }

    @Override
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

    @Override
    public void complete(Lease leaseId) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId.getPrimaryKey());

        // Verify the status
        if (lease.status().getValue() != Lease.Status.Active) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }
        // if renewed and or forcibly moving out:  
        if (!lease.nextTerm().isNull() && lease.completion().isNull()) {
            throw new IllegalStateException(SimpleMessageFormat.format("Lease has next term ready"));
        }

        lease.status().setValue(Status.Completed);

        Persistence.secureSave(lease);
    }

    @Override
    public void close(Lease leaseId) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId.getPrimaryKey());

        lease.status().setValue(Status.Closed);

        Persistence.secureSave(lease);
    }

    @Override
    public LeaseTerm createOffer(Lease leaseId, Type type) {
        Lease lease = load(leaseId, false);

        LeaseTerm term = EntityFactory.create(LeaseTerm.class);
        term.status().setValue(LeaseTerm.Status.Offer);

        term.type().setValue(type);
        term.lease().set(lease);

        // set from date to next day after current term:
        term.termFrom().setValue(new LogicalDate(lease.currentTerm().termTo().getValue().getTime() + ONE_DAY_IN_MSEC));

        updateTermUnitRelatedData(term, lease.unit(), lease.type().getValue());

        // migrate participants:
        Persistence.service().retrieve(lease.currentTerm().version().tenants());
        for (LeaseTermTenant tenant : lease.currentTerm().version().tenants()) {
            term.version().tenants().add(businessDuplicate(tenant));
        }
        Persistence.service().retrieve(lease.currentTerm().version().guarantors());
        for (LeaseTermGuarantor guarantor : lease.currentTerm().version().guarantors()) {
            term.version().guarantors().add(businessDuplicate(guarantor));
        }

        return term;
    }

    private <P extends LeaseTermParticipant<?>> P businessDuplicate(P leaseParticipant) {
        // There are no own entities for now, 
        Persistence.retrieveOwned(leaseParticipant);
        P copy = EntityGraph.businessDuplicate(leaseParticipant);
        copy.screening().set(null);
        return copy;
    }

    @Override
    public void acceptOffer(Lease leaseId, LeaseTerm leaseTermId) {
        Lease lease = load(leaseId, false);
        LeaseTerm leaseTerm = Persistence.secureRetrieve(LeaseTerm.class, leaseTermId.getPrimaryKey());

        Persistence.service().retrieveMember(lease.leaseTerms());
        if (leaseTerm.status().getValue() == LeaseTerm.Status.Offer && lease.leaseTerms().contains(leaseTermId)) {
            lease.nextTerm().set(leaseTerm);
            lease.nextTerm().status().setValue(LeaseTerm.Status.AcceptedOffer);
            lease.nextTerm().version().setValueDetached(); // TRICK (saving just non-versioned part)!..
            persist(lease.nextTerm());

            // save lease:
            Persistence.secureSave(lease);
        } else {
            throw new IllegalArgumentException(i18n.tr("Invalid LeaseTerm supplied"));
        }
    }

    @Override
    public void createCompletionEvent(Lease leaseId, CompletionType completionType, LogicalDate eventDate, LogicalDate moveOutDay, LogicalDate leseEndDate) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId.getPrimaryKey());
        if (lease.status().getValue() != Status.Active) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }

        lease.completion().setValue(completionType);
        lease.moveOutSubmissionDate().setValue(eventDate);
        lease.expectedMoveOut().setValue(moveOutDay);

        switch (completionType) {
        case Eviction:
            break;
        case Notice:
            break;
        case Skip:
            break;
        case Termination:
            lease.terminationLeaseTo().setValue(leseEndDate);
            break;
        default:
            break;
        }

        updateLeaseDates(lease);

        Persistence.secureSave(lease);

        try {
            ServerSideFactory.create(OccupancyFacade.class).moveOut(lease.unit().getPrimaryKey(), lease.expectedMoveOut().getValue());
        } catch (OccupancyOperationException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    @Override
    public void cancelCompletionEvent(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId.getPrimaryKey());
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

        Persistence.secureSave(lease);

        Persistence.secureSave(creteLeaseNote(leaseId, "Cancel " + completionType.toString(), decisionReason, decidedBy.user()));

        try {
            ServerSideFactory.create(OccupancyFacade.class).cancelMoveOut(lease.unit().getPrimaryKey());
        } catch (OccupancyOperationException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    // internals:

    @Override
    public void moveOut(Lease leaseId) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId.getPrimaryKey());

        // Verify the status
        if (lease.status().getValue() != Lease.Status.Active) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }
        // if renewed and or forcibly moving out:  
        if (lease.completion().isNull()) {
            throw new IllegalStateException(SimpleMessageFormat.format("Lease has next term ready"));
        }

        lease.actualMoveOut().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));

        updateLeaseDates(lease);

        Persistence.secureSave(lease);

        try {
            ServerSideFactory.create(OccupancyFacade.class).moveOut(lease.unit().getPrimaryKey(), lease.actualMoveOut().getValue());
        } catch (OccupancyOperationException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    @Override
    public void cancelLease(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId.getPrimaryKey());

        Status status = lease.status().getValue();
        lease.status().setValue(Status.Cancelled);

        Persistence.secureSave(lease);

        switch (status) {
        case ExistingLease:
            ServerSideFactory.create(OccupancyFacade.class).migratedCancel(lease.unit().<AptUnit> createIdentityStub());
            break;

        case Approved:
            ServerSideFactory.create(OccupancyFacade.class).unreserve(lease.unit().getPrimaryKey());
            break;

        default:
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }

        Persistence.secureSave(creteLeaseNote(leaseId, "Cancel Lease", decisionReason, decidedBy.user()));
    }

    // internals:

    private Lease setUnit(Lease lease, LeaseTerm leaseTerm, AptUnit unitId) {
        if (lease.isValueDetached()) {
            Persistence.service().retrieve(lease);
        }

        if (!Lease.Status.draft().contains(lease.status().getValue())) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }

        if (VersionedEntityUtils.equalsIgnoreVersion(lease.currentTerm(), leaseTerm)) {
            AptUnit unit = Persistence.secureRetrieve(AptUnit.class, unitId.getPrimaryKey());
            if (unit.building().isValueDetached()) {
                Persistence.service().retrieve(unit.building());
            }

            lease.unit().set(unit);

            lease.billingAccount().billingType().set(ServerSideFactory.create(BillingFacade.class).ensureBillingType(lease));

            updateTermUnitRelatedData(leaseTerm, lease.unit(), lease.type().getValue());
        } else {
            throw new IllegalArgumentException(i18n.tr("Invalid Lease/Term pair supplied"));
        }

        return lease;
    }

    private LeaseTerm setService(Lease lease, LeaseTerm leaseTerm, ProductItem serviceId) {
        if (lease.isValueDetached()) {
            Persistence.service().retrieve(lease);
        }
        if (leaseTerm.isValueDetached()) {
            Persistence.service().retrieve(leaseTerm);
        }

        // find/load all necessary ingredients:
        assert !lease.unit().isNull();
        if (lease.unit().isValueDetached()) {
            Persistence.service().retrieve(lease.unit());
        }
        assert !lease.unit().building().isNull();
        if (lease.unit().building().isValueDetached()) {
            Persistence.service().retrieve(lease.unit().building());
        }

        ProductItem serviceItem = Persistence.secureRetrieve(ProductItem.class, serviceId.getPrimaryKey());
        assert serviceItem != null;
        if (serviceItem.element().isValueDetached()) {
            Persistence.service().retrieve(serviceItem.element());
        }

        // double check:
        if (!lease.unit().equals(serviceItem.element())) {
            throw new IllegalArgumentException(i18n.tr("Invalid Unit/Service combination"));
        }

        PolicyNode node = lease.unit().building();

        // set selected service:
        BillableItem billableItem = createBillableItem(serviceItem, node);
        leaseTerm.version().leaseProducts().serviceItem().set(billableItem);

        if (leaseTerm.equals(lease.currentTerm())) {
            if (!Lease.Status.draft().contains(lease.status().getValue())) {
                throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
            }
            Persistence.service().retrieve(lease.billingAccount().deposits());
            lease.billingAccount().deposits().clear();
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
                        leaseTerm.version().leaseProducts().featureItems().add(createBillableItem(item, node));
                        break;
                    }
                }
            }
        }

        return leaseTerm;
    }

    private Lease persist(Lease lease, boolean finalize) {
        boolean doReserve = false;
        boolean doUnreserve = false;
        Lease previousLeaseEdition = null;

        if (lease.status().getValue().isDraft()) {
            if (lease.getPrimaryKey() == null) {
                doReserve = !lease.unit().isNull();
            } else {
                previousLeaseEdition = Persistence.secureRetrieve(Lease.class, lease.getPrimaryKey());
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
            Persistence.secureSave(lease);
            lease.currentTerm().set(term);

            lease.currentTerm().lease().set(lease);
        }

        if (finalize) {
            finalize(lease.currentTerm());
        } else {
            persist(lease.currentTerm());
        }

        updateLeaseDates(lease);

        Persistence.secureSave(lease);

        // update reservation if necessary:
        if (doUnreserve) {
            switch (lease.status().getValue()) {
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
                Persistence.service().persist(leaseCustomer);
            }
            // Copy value to member and update other references in graph
            leaseParticipant.leaseParticipant().id().set(leaseCustomer.id());
            leaseParticipant.leaseParticipant().lease().set(leaseTerm.lease());
            leaseParticipant.leaseParticipant().participantId().set(leaseCustomer.participantId());
        }
    }

    private void finalizeBillableItems(LeaseTerm leaseTerm) {
        leaseTerm.version().leaseProducts().serviceItem().finalized().setValue(Boolean.TRUE);
        for (BillableItem item : leaseTerm.version().leaseProducts().featureItems()) {
            item.finalized().setValue(Boolean.TRUE);
        }
    }

    private void updateLeaseDeposits(Lease lease) {
        if (lease.currentTerm().isValueDetached()) {
            Persistence.service().retrieve(lease.currentTerm());
        }

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

    @Override
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
            criteria.setSorts(new Vector<Sort>(Arrays.asList(new Sort(criteria.proto().termFrom().getPath().toString(), true))));

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

    private LeaseTerm updateTermUnitRelatedData(LeaseTerm leaseTerm, AptUnit unit, ServiceType leaseType) {
        if (unit.isValueDetached()) {
            Persistence.service().retrieve(unit);
        }
        if (unit.building().isValueDetached()) {
            Persistence.service().retrieve(unit.building());
        }
        if (leaseTerm.lease().isValueDetached()) {
            Persistence.service().retrieve(leaseTerm.lease());
        }

        boolean succeeded = false;

        EntityQueryCriteria<Service> serviceCriteria = new EntityQueryCriteria<Service>(Service.class);
        serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().catalog(), unit.building().productCatalog()));
        serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().serviceType(), leaseType));
        serviceCriteria.isCurrent(serviceCriteria.proto().version());

        Service service = Persistence.service().retrieve(serviceCriteria);
        if (service != null) {
            EntityQueryCriteria<ProductItem> productCriteria = EntityQueryCriteria.create(ProductItem.class);
            productCriteria.add(PropertyCriterion.eq(productCriteria.proto().type(), ServiceItemType.class));
            productCriteria.add(PropertyCriterion.eq(productCriteria.proto().product(), service.version()));
            productCriteria.add(PropertyCriterion.eq(productCriteria.proto().element(), unit));

            ProductItem serviceItem = Persistence.service().retrieve(productCriteria);
            if (serviceItem != null) {
                setService(leaseTerm, serviceItem);
                succeeded = true;
            }
        }

        if (!succeeded) {
            throw new UserRuntimeException(i18n.tr("There no service ''{0}'' for selected unit: {1} from Building: {2}", leaseType.toString(),
                    unit.getStringView(), unit.building().getStringView()));
        }

        return leaseTerm;
    }

    private void updateUnitRentPrice(Lease lease) {
        if (lease.unit().isValueDetached()) {
            Persistence.service().retrieve(lease.unit());
        }

        BigDecimal origPrice = lease.unit().financial()._unitRent().getValue();
        BigDecimal currentPrice = null;
        if (!lease.currentTerm().version().isNull() && !lease.currentTerm().version().leaseProducts().serviceItem().isNull()) {
            currentPrice = lease.currentTerm().version().leaseProducts().serviceItem().agreedPrice().getValue();
        }

        if ((origPrice != null && !origPrice.equals(currentPrice)) || (origPrice == null && currentPrice != null)) {
            lease.unit().financial()._unitRent().setValue(currentPrice);
            Persistence.service().merge(lease.unit());
            Persistence.service().commit();
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

    /**
     * This is a temporary solution for lease renewal (see VISTA-1789 and VISTA-2245)
     */
    @Override
    public void simpleLeaseRenew(Lease leaseId, LogicalDate leaseEndDate) {
        Lease lease = load(leaseId, true);

        // Verify the status
        if (lease.status().getValue() != Lease.Status.Active) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }

        lease.currentTerm().termTo().setValue(leaseEndDate);

        finalize(lease);
    }
}