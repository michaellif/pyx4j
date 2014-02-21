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
package com.propertyvista.biz.tenant.lease;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IVersionedEntity.SaveAction;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
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
import com.propertyvista.biz.tenant.CustomerFacade;
import com.propertyvista.biz.tenant.LeadFacade;
import com.propertyvista.biz.tenant.OnlineApplicationFacade;
import com.propertyvista.biz.tenant.ScreeningFacade;
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
import com.propertyvista.domain.policy.policies.AutoPayPolicy;
import com.propertyvista.domain.policy.policies.LeaseAgreementLegalPolicy;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.domain.LeaseBillingTypePolicyItem;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingUtility;
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
            ServerSideFactory.create(IdAssignmentFacade.class).assignId(lease.leaseApplication());
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
        return setUnit(lease, lease.currentTerm(), unitId, true);
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

//        // Load participants:
//        Persistence.service().retrieveMember(lease.currentTerm().version().tenants());
//        Persistence.service().retrieveMember(lease.currentTerm().version().guarantors());

        return lease;
    }

    // Lease term operations: -----------------------------------------------------------------------------------------

    public LeaseTerm setUnit(LeaseTerm leaseTerm, AptUnit unitId) {
        assert !leaseTerm.lease().isNull();
        setUnit(leaseTerm.lease(), leaseTerm, unitId, true);
        return leaseTerm;
    }

    public LeaseTerm setService(LeaseTerm leaseTerm, ProductItem serviceId) {
        assert !leaseTerm.lease().isNull();
        return setService(leaseTerm.lease(), leaseTerm, serviceId);
    }

    public LeaseTerm setPackage(LeaseTerm leaseTerm, AptUnit unitId, BillableItem serviceItem, List<BillableItem> featureItems) {
        assert !leaseTerm.lease().isNull();
        assert !unitId.isNull();

        setUnit(leaseTerm.lease(), leaseTerm, unitId, false);

        // update service/features:
        if (serviceItem != null) {
            leaseTerm.version().leaseProducts().serviceItem().set(serviceItem);
        }

        leaseTerm.version().leaseProducts().featureItems().clear();
        if (featureItems != null) {
            leaseTerm.version().leaseProducts().featureItems().addAll(featureItems);
        }

        leaseTerm.version().leaseProducts().concessions().clear();

        return leaseTerm;
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

        // update lease deposits/unit rent if current term:
        Persistence.ensureRetrieve(leaseTerm.lease(), AttachLevel.Attached);
        if (VersionedEntityUtils.equalsIgnoreVersion(leaseTerm.lease().currentTerm(), leaseTerm)) {
            updateLeaseDeposits(leaseTerm.lease());
            updateUnitRentPrice(leaseTerm.lease());

            ServerSideFactory.create(PaymentMethodFacade.class).renewAutopayAgreements(leaseTerm.lease());
        }

        return leaseTerm;
    }

    // Operations: ----------------------------------------------------------------------------------------------------

    public void createMasterOnlineApplication(Lease leaseId, Building building, Floorplan floorplan) {
        Lease lease = load(leaseId, false);

        // Verify the status
        if (!lease.status().getValue().isDraft()) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }
        if (lease.leaseApplication().status().getValue() != LeaseApplication.Status.Created) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Application Status (\"{0}\")", lease.leaseApplication().status().getValue()));
        }

        ServerSideFactory.create(OnlineApplicationFacade.class)
                .createMasterOnlineApplication(lease.leaseApplication().onlineApplication(), building, floorplan);

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

        releaseUnit(lease);

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

        releaseUnit(lease);
    }

    public Lease approve(Lease leaseId, Employee decidedBy, String decisionReason) {
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
        lease.approvalDate().setValue(SystemDateManager.getDate());

        if (leaseStatus == Status.Application) {
            lease.leaseApplication().status().setValue(LeaseApplication.Status.Approved);
            lease.leaseApplication().decidedBy().set(decidedBy);
            lease.leaseApplication().decisionReason().setValue(decisionReason);
            lease.leaseApplication().decisionDate().setValue(new LogicalDate(SystemDateManager.getDate()));

            ServerSideFactory.create(OnlineApplicationFacade.class).approveMasterOnlineApplication(lease.leaseApplication().onlineApplication());

            recordApplicationData(lease.currentTerm());
        }

        finalize(lease);

        // Billing-related stuff:
        onLeaseApprovalSuccess(lease, leaseStatus);

        markUnitOccupied(lease, leaseStatus);

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
        }

        // create historical billing cycles for imported leases
        BillingCycle cycle = ServerSideFactory.create(BillingCycleFacade.class).getLeaseFirstBillingCycle(lease);
        Date now = SystemDateManager.getDate();
        while (cycle.billingCycleStartDate().getValue().before(now)) {
            cycle = ServerSideFactory.create(BillingCycleFacade.class).getSubsequentBillingCycle(cycle);
        }
        return lease;
    }

    // TODO review code here
    public Lease activate(Lease leaseId) {
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

        return lease;
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
            lease.unit().set(lease.currentTerm().unit());
            updateLeaseDeposits(lease);
            updateUnitRentPrice(lease);

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

    public LeaseTerm createOffer(Lease leaseId, AptUnit unitId, Type type) {
        Lease lease = load(leaseId, false);
        AptUnit unit = (unitId != null ? Persistence.service().retrieve(AptUnit.class, unitId.getPrimaryKey()) : lease.unit());

        LeaseTerm term = EntityFactory.create(LeaseTerm.class);
        term.status().setValue(LeaseTerm.Status.Offer);

        term.type().setValue(type);
        term.lease().set(lease);
        term.unit().set(unit);

        // set from date to next day after current term (or today if termTo is not set):
        if (lease.currentTerm().termTo().isNull()) {
            term.termFrom().setValue(new LogicalDate(SystemDateManager.getDate()));
        } else {
            term.termFrom().setValue(DateUtils.daysAdd(lease.currentTerm().termTo().getValue(), 1));
        }

        updateTermUnitRelatedData(term);

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

        moveOutUnit(lease);
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

        cancelMoveOutUnit(lease);
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

        persist(lease);

        approveMoveOutUnit(lease);
    }

    public void cancelLease(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());

        // Verify the status
        if (!lease.status().getValue().isDraft() && lease.status().getValue() != Status.Approved) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }

        releaseUnit(lease);

        lease.status().setValue(Status.Cancelled);
        Persistence.service().merge(lease);
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

    public boolean isMoveOutWithinNextBillingCycle(Lease leaseId) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
        if (Lease.Status.isApplicationWithoutUnit(lease)) {
            return false;
        }
        BillingCycle nextCycle = ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayBillingCycle(lease);
        AutoPayPolicy autoPayPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit().building(), AutoPayPolicy.class);

        return (autoPayPolicy.excludeLastBillingPeriodCharge().getValue(Boolean.TRUE) && (beforeOrEqual(lease.expectedMoveOut(),
                nextCycle.billingCycleEndDate()) || beforeOrEqual(lease.actualMoveOut(), nextCycle.billingCycleEndDate())));
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

    private Lease setUnit(Lease lease, LeaseTerm leaseTerm, AptUnit unitId, boolean updateTermData) {
        Persistence.ensureRetrieve(lease, AttachLevel.Attached);
        Persistence.ensureRetrieve(leaseTerm, AttachLevel.Attached);

        if (!lease.status().getValue().isDraft()) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }

        AptUnit unit = Persistence.service().retrieve(AptUnit.class, unitId.getPrimaryKey());
        Persistence.ensureRetrieve(unit.building(), AttachLevel.Attached);

        leaseTerm.unit().set(unit);
        setBuildingUtilities(leaseTerm);

        if (VersionedEntityUtils.equalsIgnoreVersion(lease.currentTerm(), leaseTerm)) {
            lease.unit().set(unit);
            leaseTerm.lease().set(lease);

            // set LeaseBillingPolicy offsets
            boolean policyFound = false;
            LeaseBillingPolicy billingPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(unit.building(), LeaseBillingPolicy.class);
            for (LeaseBillingTypePolicyItem policyItem : billingPolicy.availableBillingTypes()) {
                if (policyItem.billingPeriod().getValue().equals(lease.billingAccount().billingPeriod().getValue())) {
                    lease.billingAccount().paymentDueDayOffset().set(policyItem.paymentDueDayOffset());
                    lease.billingAccount().finalDueDayOffset().set(policyItem.finalDueDayOffset());
                    policyFound = true;
                    break;
                }
            }
            if (!policyFound) {
                throw new IllegalArgumentException(i18n.tr("No Billing policy found for: {0}", lease.billingAccount().billingPeriod().getValue()));
            }
        }

        LeaseAgreementLegalPolicy agreementLegalPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(leaseTerm.unit().building(),
                LeaseAgreementLegalPolicy.class);
        leaseTerm.version().agreementLegalTerms().set(agreementLegalPolicy.legal());
        leaseTerm.version().agreementConfirmationTerm().set(agreementLegalPolicy.confirmation());

        if (updateTermData) {
            updateTermUnitRelatedData(leaseTerm);
        }

        return lease;
    }

    private LeaseTerm setService(Lease lease, LeaseTerm leaseTerm, ProductItem serviceId) {
        Persistence.ensureRetrieve(lease, AttachLevel.Attached);
        Persistence.ensureRetrieve(leaseTerm, AttachLevel.Attached);

        // find/load all necessary ingredients:
        assert !lease.unit().isNull();
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

        if (VersionedEntityUtils.equalsIgnoreVersion(lease.currentTerm(), leaseTerm)) {
            if (!lease.status().getValue().isDraft()) {
                throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
            }
// TODO - review deposits lifecycle management!
//            // clear current deposits: 
//            EntityQueryCriteria<DepositLifecycle> criteria = EntityQueryCriteria.create(DepositLifecycle.class);
//            criteria.eq(criteria.proto().billingAccount(), lease.billingAccount());
//            Persistence.service().delete(criteria);
        }

        // Service by Service item:
        Service.ServiceV service = null;
        Persistence.ensureRetrieve(serviceItem.product(), AttachLevel.Attached);
        if (serviceItem.product().getInstanceValueClass().equals(Service.ServiceV.class)) {
            service = serviceItem.product().cast();
        }
        assert service != null;

        // clear current dependable data:
        leaseTerm.version().leaseProducts().featureItems().clear();
        leaseTerm.version().leaseProducts().concessions().clear();

        LogicalDate termFrom = (leaseTerm.termFrom().isNull() ? new LogicalDate(SystemDateManager.getDate()) : leaseTerm.termFrom().getValue());

        // pre-populate mandatory features for the new service:
        Persistence.ensureRetrieve(service.features(), AttachLevel.Attached);
        for (Feature feature : service.features()) {
            if (!VistaFeatures.instance().yardiIntegration() || VistaFeatures.instance().yardiIntegration()
                    && feature.version().availableOnline().isBooleanTrue()) {
                if (feature.expiredFrom().isNull() || feature.expiredFrom().getValue().before(termFrom)) {
                    if (feature.version().mandatory().isBooleanTrue()) {
                        Persistence.ensureRetrieve(feature.version().items(), AttachLevel.Attached);
                        if (!feature.version().items().isEmpty()) {
                            leaseTerm.version().leaseProducts().featureItems().add(createBillableItem(lease, feature.version().items().get(0), node));
                        }
                    }
                }
            }
        }

        return leaseTerm;
    }

    private void setBuildingUtilities(LeaseTerm term) {
        assert (!term.unit().isNull());
        assert (!term.unit().isValueDetached());

        Persistence.ensureRetrieve(term.version().utilities(), AttachLevel.Attached);
        term.version().utilities().clear();

        EntityQueryCriteria<BuildingUtility> criteria = EntityQueryCriteria.create(BuildingUtility.class);
        criteria.eq(criteria.proto().building(), term.unit().building());
        criteria.eq(criteria.proto().isDeleted(), Boolean.FALSE);
        term.version().utilities().addAll(Persistence.service().query(criteria));
    }

    protected void finalizeBillableItems(LeaseTerm leaseTerm) {
        leaseTerm.version().leaseProducts().serviceItem().finalized().setValue(Boolean.TRUE);
        for (BillableItem item : leaseTerm.version().leaseProducts().featureItems()) {
            item.finalized().setValue(Boolean.TRUE);
        }
    }

    private Lease persist(Lease lease, boolean finalize) {
        boolean doReserve = false;

        if (lease.status().getValue().isDraft()) {
            doReserve = !lease.unit().isNull();
            if (lease.getPrimaryKey() != null) {
                Lease previousLeaseEdition = Persistence.service().retrieve(Lease.class, lease.getPrimaryKey());
                if (!EqualsHelper.equals(previousLeaseEdition.unit().getPrimaryKey(), lease.unit().getPrimaryKey())) {
                    if (!previousLeaseEdition.unit().isNull()) {
                        releaseUnit(previousLeaseEdition);
                    }
                }
            }
        }

        // actual persist mechanics:
        if (lease.currentTerm().getPrimaryKey() == null) {
            LeaseTerm term = lease.currentTerm().detach();

            lease.currentTerm().set(null);
            Persistence.service().merge(lease);
            lease.currentTerm().set(term);

            lease.currentTerm().lease().set(lease);
        }

        // sync. unit:
        if (!lease.currentTerm().unit().isNull()) {
            lease.unit().set(lease.currentTerm().unit());
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

        ServerSideFactory.create(PaymentMethodFacade.class).terminateAutopayAgreements(lease);

        // update reservation if necessary:
        if (doReserve) {
            reserveUnit(lease);
        }

        return lease;
    }

    // Unit occupancy management:

    protected void reserveUnit(Lease lease) {
        switch (lease.status().getValue()) {
        case NewLease:
        case Application:
            break;

        case ExistingLease:
            if (ServerSideFactory.create(OccupancyFacade.class).isMigrateStartAvailable(lease.unit().<AptUnit> createIdentityStub())) {
                ServerSideFactory.create(OccupancyFacade.class).migrateStart(lease.unit().<AptUnit> createIdentityStub(), lease);
            }
            break;

        default:
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }
    }

    protected void releaseUnit(Lease lease) {
        switch (lease.status().getValue()) {
        case NewLease:
        case Application:
            ServerSideFactory.create(OccupancyFacade.class).unreserveIfReservered(lease);
            break;

        case Approved:
            ServerSideFactory.create(OccupancyFacade.class).unreserveIfReservered(lease);
            ServerSideFactory.create(OccupancyFacade.class).unoccupy(lease);
            break;

        case ExistingLease:
            if (ServerSideFactory.create(OccupancyFacade.class).isMigratedCancelAvailable(lease.unit().<AptUnit> createIdentityStub())) {
                ServerSideFactory.create(OccupancyFacade.class).migratedCancel(lease.unit().<AptUnit> createIdentityStub());
            }
            break;

        default:
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }
    }

    protected void markUnitOccupied(Lease lease, Status previousStatus) {
        switch (previousStatus) {
        case Application:
            ServerSideFactory.create(OccupancyFacade.class).occupy(lease.<Lease> createIdentityStub());
            break;

        case NewLease:
            ServerSideFactory.create(OccupancyFacade.class).occupy(lease.<Lease> createIdentityStub());
            break;

        case ExistingLease:
            ServerSideFactory.create(OccupancyFacade.class).migratedApprove(lease.unit().<AptUnit> createIdentityStub());
            break;

        default:
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", previousStatus));
        }
    }

    protected void moveOutUnit(Lease lease) {
        try {
            ServerSideFactory.create(OccupancyFacade.class).moveOut(lease.unit().getPrimaryKey(), lease.expectedMoveOut().getValue(), lease);
        } catch (OccupancyOperationException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    protected void cancelMoveOutUnit(Lease lease) {
        try {
            ServerSideFactory.create(OccupancyFacade.class).cancelMoveOut(lease.unit().getPrimaryKey());
        } catch (OccupancyOperationException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    protected void approveMoveOutUnit(Lease lease) {
        AptUnitOccupancySegment segment = ServerSideFactory.create(OccupancyFacade.class).getOccupancySegment(lease.unit(), lease.actualMoveOut().getValue());
        if (segment.lease().equals(lease)) {
            try {
                ServerSideFactory.create(OccupancyFacade.class).moveOut(lease.unit().getPrimaryKey(), lease.actualMoveOut().getValue(), lease);
            } catch (OccupancyOperationException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
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

    private CustomerScreening retrivePersonScreeningId(Customer customer) {
        if (ScreeningValidator.screeningIsAutomaticallyFinalized) {
            return ServerSideFactory.create(ScreeningFacade.class).retriveAndFinalizePersonScreening(customer, AttachLevel.Attached);
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

    private NotesAndAttachments creteLeaseNote(Lease leaseId, String subject, String note, CrmUser user) {
        NotesAndAttachments naa = EntityFactory.create(NotesAndAttachments.class);
        naa.owner().set(leaseId);

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
            criteria.asc(criteria.proto().termFrom());

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

    protected LeaseTerm updateTermUnitRelatedData(LeaseTerm leaseTerm) {
        Persistence.ensureRetrieve(leaseTerm.unit().building(), AttachLevel.Attached);
        Persistence.ensureRetrieve(leaseTerm.lease(), AttachLevel.Attached);

        boolean succeeded = false;

        LogicalDate termFrom = (leaseTerm.termFrom().isNull() ? new LogicalDate(SystemDateManager.getDate()) : leaseTerm.termFrom().getValue());

        // use default product catalog items for specific cases:
        boolean useDefaultCatalog = (leaseTerm.unit().building().defaultProductCatalog().isBooleanTrue() || leaseTerm.lease().status().getValue() == Lease.Status.ExistingLease);
        if (VistaFeatures.instance().yardiIntegration()) {
            useDefaultCatalog = false;
        }

        EntityQueryCriteria<Service> serviceCriteria = new EntityQueryCriteria<Service>(Service.class);
        serviceCriteria.eq(serviceCriteria.proto().catalog(), leaseTerm.unit().building().productCatalog());
        serviceCriteria.eq(serviceCriteria.proto().code().type(), leaseTerm.lease().type());
        serviceCriteria.eq(serviceCriteria.proto().defaultCatalogItem(), useDefaultCatalog);
        serviceCriteria.or(PropertyCriterion.isNull(serviceCriteria.proto().expiredFrom()),
                PropertyCriterion.lt(serviceCriteria.proto().expiredFrom(), termFrom));
        serviceCriteria.isCurrent(serviceCriteria.proto().version());

        if (VistaFeatures.instance().yardiIntegration()) {
            serviceCriteria.eq(serviceCriteria.proto().version().availableOnline(), Boolean.TRUE);
        }

        for (Service service : Persistence.service().query(serviceCriteria)) {
            EntityQueryCriteria<ProductItem> productCriteria = EntityQueryCriteria.create(ProductItem.class);
            productCriteria.add(PropertyCriterion.eq(productCriteria.proto().product(), service.version()));
            productCriteria.add(PropertyCriterion.eq(productCriteria.proto().element(), leaseTerm.unit()));

            ProductItem serviceItem = Persistence.service().retrieve(productCriteria);
            if (serviceItem != null) {
                setService(leaseTerm, serviceItem);
                succeeded = true;
                break; // use first found service/item
            }
        }

        if (!succeeded) {
            throw new UserRuntimeException(i18n.tr("There is no Service type of ''{0}'' for selected unit {1}, building {2}", leaseTerm.lease().type()
                    .getValue().toString(), leaseTerm.unit().getStringView(), leaseTerm.unit().building().getStringView()));
        }

        return leaseTerm;
    }

    private void updateUnitRentPrice(Lease lease) {
        Persistence.ensureRetrieve(lease.unit(), AttachLevel.Attached);

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
        if (lease.status().getValue().isDraft() && !lease.leaseFrom().isNull() && !lease.unit().isNull()) {
            lease.billingAccount().billingType().set(ServerSideFactory.create(BillingCycleFacade.class).getBillingType(lease));
        } else {
            log.debug("Can't retrieve Billing Type!..");
        }
    }

    protected <P extends LeaseTermParticipant<?>> P businessDuplicate(P leaseParticipant) {
        // There are no own entities for now,
        Persistence.retrieveOwned(leaseParticipant);
        P copy = EntityGraph.businessDuplicate(leaseParticipant);
        copy.screening().set(null);
        return copy;
    }

    private boolean beforeOrEqual(IPrimitive<LogicalDate> one, IPrimitive<LogicalDate> two) {
        if (!one.isNull() && !two.isNull()) {
            return !one.getValue().after(two.getValue());
        }
        return false;
    }

    public Building getLeasePolicyNode(Lease leaseId) {
        if (!leaseId.unit().building().isValueDetached()) {
            return leaseId.unit().building();
        } else {
            Building building;
            {
                EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
                criteria.eq(criteria.proto().units().$().leases(), leaseId);
                building = Persistence.service().retrieve(criteria, AttachLevel.IdOnly);
            }
            if (building != null) {
                return building;
            } else {
                throw new Error();
            }
        }
    }
}