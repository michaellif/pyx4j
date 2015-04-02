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
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.shared.utils.VersionedEntityUtils;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.communication.NotificationFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.biz.financial.deposit.DepositFacade;
import com.propertyvista.biz.financial.payment.PaymentMethodFacade;
import com.propertyvista.biz.financial.productcatalog.ProductCatalogFacade;
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
import com.propertyvista.domain.company.Notification.AlertType;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.note.NotesAndAttachments;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.policy.policies.ApplicationApprovalChecklistPolicy;
import com.propertyvista.domain.policy.policies.AutoPayPolicy;
import com.propertyvista.domain.policy.policies.LeaseAgreementLegalPolicy;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.domain.ApplicationApprovalChecklistPolicyItem;
import com.propertyvista.domain.policy.policies.domain.LeaseBillingTypePolicyItem;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingUtility;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.CustomerScreening;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.domain.tenant.lease.LeaseApplication.ApprovalChecklistItem;
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
            lease.leaseApplication().status().setValue(LeaseApplication.Status.InProgress);
            fireLeaseApplicationNotification(lease, AlertType.ApplicationInProgress);
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
            lease.currentTerm().version().leaseProducts().serviceItem().agreedPrice().setValue(BigDecimal.ZERO);
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
        return setUnitInternal(lease.currentTerm(), unitId, true).lease();
    }

    public Lease setService(Lease lease, ProductItem serviceId) {
        assert !lease.currentTerm().isNull();
        return setServiceInternal(lease.currentTerm(), serviceId).lease();
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
        if (editingTerm) {
            lease.currentTerm().set(Persistence.retrieveDraftForEdit(LeaseTerm.class, lease.currentTerm().getPrimaryKey()));
        } else {
            lease.currentTerm().set(Persistence.retriveFinalOrDraft(LeaseTerm.class, lease.currentTerm().getPrimaryKey(), AttachLevel.Attached));
        }

        // Load participants:
        Persistence.service().retrieveMember(lease.currentTerm().version().tenants());
        Persistence.service().retrieveMember(lease.currentTerm().version().guarantors());

        return lease;
    }

    // Lease term operations: -----------------------------------------------------------------------------------------

    public LeaseTerm setUnit(LeaseTerm leaseTerm, AptUnit unitId) {
        return setUnitInternal(leaseTerm, unitId, true);
    }

    public LeaseTerm setService(LeaseTerm leaseTerm, ProductItem serviceId) {
        return setServiceInternal(leaseTerm, serviceId);
    }

    public LeaseTerm setPackage(LeaseTerm leaseTerm, AptUnit unitId, BillableItem serviceItem, List<BillableItem> featureItems) {
        assert !unitId.isNull();

        setUnitInternal(leaseTerm, unitId, false);

        // update service/features:
        if (serviceItem != null) {
            leaseTerm.version().leaseProducts().serviceItem().set(serviceItem);
        } else {
            leaseTerm.version().leaseProducts().serviceItem().clear();
            leaseTerm.version().leaseProducts().serviceItem().agreedPrice().setValue(BigDecimal.ZERO);
        }

        leaseTerm.version().leaseProducts().featureItems().clear();
        if (featureItems != null) {
            leaseTerm.version().leaseProducts().featureItems().addAll(featureItems);
        }

        leaseTerm.version().leaseProducts().concessions().clear();

        return leaseTerm;
    }

    public LeaseTerm persist(LeaseTerm leaseTerm) {
        persistLeaseParticipants(leaseTerm);

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
        if (lease.leaseApplication().status().getValue() != LeaseApplication.Status.InProgress) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Application Status (\"{0}\")", lease.leaseApplication().status().getValue()));
        }

        ServerSideFactory.create(OnlineApplicationFacade.class)
                .createMasterOnlineApplication(lease.leaseApplication().onlineApplication(), building, floorplan);

        Persistence.service().merge(lease);
    }

    public void cancelMasterOnlineApplication(Lease leaseId) {
        Lease lease = load(leaseId, false);

        // Verify the status
        if (!lease.status().getValue().isDraft()) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }
        if (!LeaseApplication.Status.isOnlineApplication(lease.leaseApplication())) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Application Status (\"{0}\")", lease.leaseApplication().status().getValue()));
        }

        ServerSideFactory.create(OnlineApplicationFacade.class).cancelMasterOnlineApplication(lease.leaseApplication().onlineApplication());

        lease.leaseApplication().status().setValue(LeaseApplication.Status.InProgress);
        fireLeaseApplicationNotification(lease, AlertType.ApplicationInProgress);

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

    public void requestForMoreInformation(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = load(leaseId, false);

        lease.leaseApplication().status().setValue(LeaseApplication.Status.PendingFurtherInformation);
        fireLeaseApplicationNotification(lease, AlertType.ApplicationInformationRequired);

        Persistence.service().merge(lease);
        addLeaseNote(lease, "Pending Further Information on Application", decisionReason, decidedBy);
    }

    public void submitApplication(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = load(leaseId, false);

        lease.leaseApplication().status().setValue(LeaseApplication.Status.Submitted);
        fireLeaseApplicationNotification(lease, AlertType.ApplicationSubmitted);

        initializeApprovalChecklist(lease.leaseApplication());

        if (decidedBy != null) {
            lease.leaseApplication().submission().decidedBy().set(decidedBy);
            lease.leaseApplication().submission().decisionReason().setValue(decisionReason);
            lease.leaseApplication().submission().decisionDate().setValue(SystemDateManager.getLogicalDate());
            addLeaseNote(lease, "Submit Application", decisionReason, decidedBy);
        }

        Persistence.service().merge(lease);
    }

    public void completeApplication(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = load(leaseId, false);

        lease.leaseApplication().status().setValue(LeaseApplication.Status.PendingDecision);
        fireLeaseApplicationNotification(lease, AlertType.ApplicationPendingDecision);
        if (decidedBy != null) {
            lease.leaseApplication().validation().decidedBy().set(decidedBy);
            lease.leaseApplication().validation().decisionReason().setValue(decisionReason);
            lease.leaseApplication().validation().decisionDate().setValue(SystemDateManager.getLogicalDate());
            addLeaseNote(lease, "Complete Application", decisionReason, decidedBy);
        }

        Persistence.service().merge(lease);
    }

    public void declineApplication(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = load(leaseId, false);
        Status status = lease.status().getValue();

        // TODO Review the status
        lease.status().setValue(Lease.Status.Cancelled);
        lease.leaseApplication().status().setValue(LeaseApplication.Status.Declined);
        fireLeaseApplicationNotification(lease, AlertType.ApplicationDeclined);

        if (decidedBy != null) {
            lease.leaseApplication().approval().decidedBy().set(decidedBy);
            lease.leaseApplication().approval().decisionReason().setValue(decisionReason);
            lease.leaseApplication().approval().decisionDate().setValue(SystemDateManager.getLogicalDate());
            addLeaseNote(lease, "Decline Application", decisionReason, decidedBy);
        }
        recordApplicationData(lease.currentTerm());

        Persistence.service().merge(lease);

        releaseUnit(lease, status);

        Persistence.ensureRetrieve(lease.currentTerm().version().tenants(), AttachLevel.Attached);
        for (LeaseTermTenant participant : lease.currentTerm().version().tenants()) {
            // co-applicants have no dedicated application
            if (participant.role().getValue() != LeaseTermParticipant.Role.Dependent && !participant.leaseParticipant().customer().person().email().isNull()) {
                ServerSideFactory.create(CommunicationFacade.class).sendApplicationDeclined(participant);
            }
        }
        Persistence.ensureRetrieve(lease.currentTerm().version().guarantors(), AttachLevel.Attached);
        for (LeaseTermGuarantor participant : lease.currentTerm().version().guarantors()) {
            if (!participant.leaseParticipant().customer().person().email().isNull()) {
                ServerSideFactory.create(CommunicationFacade.class).sendApplicationDeclined(participant);
            }
        }
    }

    public void cancelApplication(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = load(leaseId, false);
        Status status = lease.status().getValue();

        lease.status().setValue(Lease.Status.Cancelled);
        lease.leaseApplication().status().setValue(LeaseApplication.Status.Cancelled);
        if (decidedBy != null) {
            lease.leaseApplication().approval().decidedBy().set(decidedBy);
            lease.leaseApplication().approval().decisionReason().setValue(decisionReason);
            lease.leaseApplication().approval().decisionDate().setValue(SystemDateManager.getLogicalDate());
            addLeaseNote(lease, "Cancel Application", decisionReason, decidedBy);
        }

        Persistence.service().merge(lease);

        releaseUnit(lease, status);
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
        lease.approvalDate().setValue(Persistence.service().getTransactionTime());

        if (leaseStatus == Status.Application) {
            lease.leaseApplication().status().setValue(LeaseApplication.Status.Approved);
            fireLeaseApplicationNotification(lease, AlertType.ApplicationApproved);

            if (decidedBy != null) {
                lease.leaseApplication().approval().decidedBy().set(decidedBy);
                lease.leaseApplication().approval().decisionReason().setValue(decisionReason);
                lease.leaseApplication().approval().decisionDate().setValue(SystemDateManager.getLogicalDate());
                addLeaseNote(lease, "Approve Application", decisionReason, decidedBy);
            }

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
                for (LeaseTermTenant participant : lease.currentTerm().version().tenants()) {
                    // co-applicants have no dedicated application
                    if (participant.role().getValue() != LeaseTermParticipant.Role.Dependent
                            && !participant.leaseParticipant().customer().person().email().isNull()) {
                        ServerSideFactory.create(CommunicationFacade.class).sendApplicationApproved(participant);
                    }
                }
                Persistence.ensureRetrieve(lease.currentTerm().version().guarantors(), AttachLevel.Attached);
                for (LeaseTermGuarantor participant : lease.currentTerm().version().guarantors()) {
                    if (!participant.leaseParticipant().customer().person().email().isNull()) {
                        ServerSideFactory.create(CommunicationFacade.class).sendApplicationApproved(participant);
                    }
                }
            }
        default:
            break;
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
        lease.activationDate().setValue(SystemDateManager.getLogicalDate());
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
        if (!lease.leaseTo().isNull() && !lease.leaseTo().getValue().before(SystemDateManager.getLogicalDate())) {
            throw new IllegalStateException("Lease is not ended yet");
        }

        lease.status().setValue(Status.Completed);

        Persistence.service().merge(lease);
    }

    public void close(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());

        lease.status().setValue(Status.Closed);

        Persistence.service().merge(lease);
        addLeaseNote(lease, "Close Lease", decisionReason, decidedBy);
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
            term.termFrom().setValue(SystemDateManager.getLogicalDate());
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
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease {0} Status (\"{1}\")", lease.id(), lease.status()));
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
        addLeaseNote(lease, "Cancel " + completionType.toString(), decisionReason, decidedBy);

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
        Status status = lease.status().getValue();

        // Verify the status
        if (!status.isDraft() && status != Status.Approved) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }

        releaseUnit(lease, status);

        lease.status().setValue(Status.Cancelled);
        Persistence.service().merge(lease);
        addLeaseNote(lease, "Cancel Lease", decisionReason, decidedBy);
    }

    // Utils : --------------------------------------------------------------------------------------------------------

    public BillableItem createBillableItem(Lease lease, ProductItem productItemId) {
        Persistence.ensureRetrieve(lease, AttachLevel.Attached);

        ProductItem productItem = Persistence.service().retrieve(ProductItem.class, productItemId.getPrimaryKey());
        assert productItem != null;
        Persistence.ensureRetrieve(productItem.product(), AttachLevel.Attached);

        BillableItem newItem = EntityFactory.create(BillableItem.class);
        newItem.item().set(productItem);
        newItem.agreedPrice().setValue(ServerSideFactory.create(ProductCatalogFacade.class).calculateItemPrice(productItem));

        // avoid policed deposits for existing Leases:
        if (lease.status().getValue() != Lease.Status.ExistingLease) {
            // set policed deposits:
            newItem.deposits().addAll(ServerSideFactory.create(DepositFacade.class).createRequiredDeposits(newItem));
        }

        return newItem;
    }

    public boolean isMoveOutWithinNextBillingCycle(Lease leaseId) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
        if (!Lease.Status.isApplicationUnitSelected(lease)) {
            return false;
        }
        BillingCycle nextCycle = ServerSideFactory.create(PaymentMethodFacade.class).getNextAutopayBillingCycle(lease);
        AutoPayPolicy autoPayPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit().building(), AutoPayPolicy.class);

        return (autoPayPolicy.excludeLastBillingPeriodCharge().getValue(false) && (beforeOrEqual(lease.expectedMoveOut(), nextCycle.billingCycleEndDate()) || beforeOrEqual(
                lease.actualMoveOut(), nextCycle.billingCycleEndDate())));
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

    private LeaseTerm setUnitInternal(LeaseTerm leaseTerm, AptUnit unitId, boolean updateTermData) {
        assert !leaseTerm.lease().isNull();
        Persistence.ensureRetrieve(leaseTerm.lease(), AttachLevel.Attached);

        if (!VistaFeatures.instance().yardiIntegration() && !leaseTerm.lease().status().getValue().isDraft()) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", leaseTerm.lease().status().getValue()));
        }

        AptUnit unit = Persistence.service().retrieve(AptUnit.class, unitId.getPrimaryKey());
        Persistence.ensureRetrieve(unit.building(), AttachLevel.Attached);

        leaseTerm.unit().set(unit);
        setBuildingUtilities(leaseTerm);

        if (VersionedEntityUtils.equalsIgnoreVersion(leaseTerm.lease().currentTerm(), leaseTerm)) {
            leaseTerm.lease().unit().set(unit);

            // set LeaseBillingPolicy offsets
            boolean policyFound = false;
            LeaseBillingPolicy billingPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(unit.building(), LeaseBillingPolicy.class);
            for (LeaseBillingTypePolicyItem policyItem : billingPolicy.availableBillingTypes()) {
                if (policyItem.billingPeriod().getValue().equals(leaseTerm.lease().billingAccount().billingPeriod().getValue())) {
                    leaseTerm.lease().billingAccount().paymentDueDayOffset().set(policyItem.paymentDueDayOffset());
                    leaseTerm.lease().billingAccount().finalDueDayOffset().set(policyItem.finalDueDayOffset());
                    policyFound = true;
                    break;
                }
            }
            if (!policyFound) {
                throw new IllegalArgumentException(i18n.tr("No Billing policy found for: {0}", leaseTerm.lease().billingAccount().billingPeriod().getValue()));
            }
        }

        if (updateTermData) {
            updateTermUnitRelatedData(leaseTerm);
        }

        return leaseTerm;
    }

    private LeaseTerm setServiceInternal(LeaseTerm leaseTerm, ProductItem serviceId) {
        assert !leaseTerm.lease().isNull();
        Persistence.ensureRetrieve(leaseTerm.lease(), AttachLevel.Attached);

        // find/load all necessary ingredients:
        assert !leaseTerm.unit().isNull();
        Persistence.ensureRetrieve(leaseTerm.unit().building(), AttachLevel.Attached);

        ProductItem serviceItem = Persistence.service().retrieve(ProductItem.class, serviceId.getPrimaryKey());
        assert serviceItem != null;
        Persistence.ensureRetrieve(serviceItem.element(), AttachLevel.Attached);

        // double check:
        if (!leaseTerm.unit().equals(serviceItem.element())) {
            throw new IllegalArgumentException(i18n.tr("Invalid Unit/Service combination"));
        }

        // set selected service:
        BillableItem billableItem = createBillableItem(leaseTerm.lease(), serviceItem);
        leaseTerm.version().leaseProducts().serviceItem().set(billableItem);

        if (VersionedEntityUtils.equalsIgnoreVersion(leaseTerm.lease().currentTerm(), leaseTerm)) {
            if (!leaseTerm.lease().status().getValue().isDraft()) {
                throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", leaseTerm.lease().status().getValue()));
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

        LogicalDate termFrom = (leaseTerm.termFrom().isNull() ? SystemDateManager.getLogicalDate() : leaseTerm.termFrom().getValue());

        // pre-populate mandatory features for the new service:
        Persistence.ensureRetrieve(service.features(), AttachLevel.Attached);
        for (Feature feature : service.features()) {
            if (!VistaFeatures.instance().yardiIntegration() || VistaFeatures.instance().yardiIntegration()
                    && feature.version().availableOnline().getValue(false)) {
                if (feature.expiredFrom().isNull() || feature.expiredFrom().getValue().after(termFrom)) {
                    if (feature.version().mandatory().getValue(false)) {
                        Persistence.ensureRetrieve(feature.version().items(), AttachLevel.Attached);
                        if (!feature.version().items().isEmpty()) {
                            leaseTerm.version().leaseProducts().featureItems().add(createBillableItem(leaseTerm.lease(), feature.version().items().get(0)));
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
        if (!leaseTerm.version().leaseProducts().serviceItem().isNull()) {
            leaseTerm.version().leaseProducts().serviceItem().finalized().setValue(Boolean.TRUE);
        }
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
                        releaseUnit(previousLeaseEdition, previousLeaseEdition.status().getValue());
                    }
                }
            }
        }

        // actual persist mechanics:
        if (lease.getPrimaryKey() == null) {
            lease = lease.detach();
            LeaseTerm term = lease.currentTerm().detach();

            lease.currentTerm().set(null);
            Persistence.service().merge(lease);
            lease.currentTerm().set(term);

            lease.currentTerm().lease().set(lease);
        }

        // sync. unit:
        if (!lease.currentTerm().unit().isNull()) {
            lease.unit().set(lease.currentTerm().unit());
            // update legal policies
            if (lease.status().getValue().isDraft()) {
                LeaseAgreementLegalPolicy agreementLegalPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(
                        lease.currentTerm().unit().building(), LeaseAgreementLegalPolicy.class);
                lease.currentTerm().agreementLegalTerms().set(agreementLegalPolicy.legal());
                lease.currentTerm().agreementConfirmationTerms().set(agreementLegalPolicy.confirmation());
            }
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

    protected void releaseUnit(Lease lease, Status previousStatus) {
        switch (previousStatus) {
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

    private void persistLeaseParticipants(LeaseTerm leaseTerm) {
        for (LeaseTermTenant tenant : leaseTerm.version().tenants()) {
            if (!tenant.isValueDetached()) {
                persistLeaseParticipant(leaseTerm, tenant, Tenant.class);
            }
        }
        for (LeaseTermGuarantor guarantor : leaseTerm.version().guarantors()) {
            if (!guarantor.isValueDetached()) {
                persistLeaseParticipant(leaseTerm, guarantor, Guarantor.class);
            }
        }
    }

    private <E extends LeaseParticipant<?>, P extends LeaseTermParticipant<?>> void persistLeaseParticipant(LeaseTerm leaseTerm, P leaseTermParticipant,
            Class<E> leaseParticipantClass) {
        boolean newCustomer = leaseTermParticipant.leaseParticipant().customer().id().isNull();

        if (!leaseTermParticipant.leaseParticipant().customer().isValueDetached()) {
            ServerSideFactory.create(CustomerFacade.class).persistCustomer(leaseTermParticipant.leaseParticipant().customer());
        }

        // Is new LeaseCustomer find or create new
        if (leaseTermParticipant.leaseParticipant().id().isNull()) {
            E leaseParticipant = null;

            if (!newCustomer) {
                // Find returning Lease Participant (if exist):
                EntityQueryCriteria<E> criteria = EntityQueryCriteria.create(leaseParticipantClass);
                criteria.eq(criteria.proto().lease(), leaseTerm.lease());
                criteria.eq(criteria.proto().customer(), leaseTermParticipant.leaseParticipant().customer());
                leaseParticipant = Persistence.service().retrieve(criteria);
            }

            if (leaseParticipant == null) {
                Customer customer = leaseTermParticipant.leaseParticipant().customer();
                leaseParticipant = EntityFactory.create(leaseParticipantClass);
                leaseParticipant.lease().set(leaseTerm.lease());
                leaseParticipant.customer().set(customer);
                ServerSideFactory.create(IdAssignmentFacade.class).assignId(leaseParticipant);
                // case of user-assignable ID:
                if (leaseParticipant.participantId().isNull() && !leaseTermParticipant.leaseParticipant().participantId().isNull()) {
                    leaseParticipant.participantId().set(leaseTermParticipant.leaseParticipant().participantId());
                }

                Persistence.service().persist(leaseParticipant);
            }

            // Copy value to member and update other references in graph
            leaseTermParticipant.leaseParticipant().id().set(leaseParticipant.id());
            leaseTermParticipant.leaseParticipant().lease().set(leaseTerm.lease());
            leaseTermParticipant.leaseParticipant().participantId().set(leaseParticipant.participantId());
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

    protected NotesAndAttachments createLeaseNote(Lease lease, String subject, String note, Employee employee) {
        NotesAndAttachments naa = EntityFactory.create(NotesAndAttachments.class);
        naa.owner().set(lease);

        naa.subject().setValue(subject);
        naa.note().setValue(note);
        if (employee != null) {
            naa.user().set(employee.user());
        }

        return naa;
    }

    protected void addLeaseNote(Lease lease, String subject, String note, Employee employee) {
        Persistence.service().merge(createLeaseNote(lease, subject, note, employee));
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

        LogicalDate termFrom = (leaseTerm.termFrom().isNull() ? SystemDateManager.getLogicalDate() : leaseTerm.termFrom().getValue());

        // use default product catalog items for specific cases:
        boolean useDefaultCatalog = (leaseTerm.unit().building().defaultProductCatalog().getValue(false) || leaseTerm.lease().status().getValue() == Lease.Status.ExistingLease);
        if (VistaFeatures.instance().yardiIntegration()) {
            useDefaultCatalog = false;
        }

        EntityQueryCriteria<Service> serviceCriteria = new EntityQueryCriteria<Service>(Service.class);
        serviceCriteria.eq(serviceCriteria.proto().catalog(), leaseTerm.unit().building().productCatalog());
        serviceCriteria.eq(serviceCriteria.proto().code().type(), leaseTerm.lease().type());
        serviceCriteria.eq(serviceCriteria.proto().defaultCatalogItem(), useDefaultCatalog);
        serviceCriteria.or(PropertyCriterion.isNull(serviceCriteria.proto().expiredFrom()),
                PropertyCriterion.gt(serviceCriteria.proto().expiredFrom(), termFrom));
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
            currentPrice = ServerSideFactory.create(BillingFacade.class).getActualPrice(lease.currentTerm().version().leaseProducts().serviceItem());
        }

        if ((origPrice != null && !origPrice.equals(currentPrice)) || (origPrice == null && currentPrice != null)) {
            lease.unit().financial()._unitRent().setValue(currentPrice);
            Persistence.service().merge(lease.unit());
        }
    }

    private void updateBillingType(Lease lease) {
        if (lease.status().getValue().isDraft()) {
            if (!lease.leaseFrom().isNull() && !lease.unit().isNull()) {
                lease.billingAccount().billingType().set(ServerSideFactory.create(BillingCycleFacade.class).getBillingType(lease));
            } else {
                log.debug("Can't retrieve Billing Type!..");
            }
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

    public PolicyNode getLeasePolicyNode(Lease leaseId) {
        PolicyNode node = getLeaseBuilding(leaseId);
        if (node == null) {
            // return organization node for policy queries in case of empty building/unit selection:
            node = Persistence.service().retrieve(EntityQueryCriteria.create(OrganizationPoliciesNode.class));
        }
        return node;
    }

    public Building getLeaseBuilding(Lease leaseId) {
        Lease lease = Persistence.service().retrieve(Lease.class, leaseId.getPrimaryKey());
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.IdOnly);

        if (lease.unit().isNull()) {
            if (LeaseApplication.Status.isOnlineApplication(lease.leaseApplication())) {
                // OnlineApplication, Case of ILS link, see  @link: OnlineApplicationFacadeImpl.getOnlineApplicationPolicyNode
                Persistence.ensureRetrieve(lease.leaseApplication().onlineApplication().ilsBuilding(), AttachLevel.IdOnly);
                return lease.leaseApplication().onlineApplication().ilsBuilding();
            }
            return null; // no unit selected yet!..
        }

        return lease.unit().building();
    }

    private void initializeApprovalChecklist(LeaseApplication leaseApplication) {
        ApplicationApprovalChecklistPolicy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(
                getLeasePolicyNode(leaseApplication.lease()), ApplicationApprovalChecklistPolicy.class);

        leaseApplication.approvalChecklist().clear();
        for (ApplicationApprovalChecklistPolicyItem item : policy.itemsToCheck()) {
            ApprovalChecklistItem checklistItem = EntityFactory.create(ApprovalChecklistItem.class);

            checklistItem.task().setValue(item.itemToCheck().getValue());

            for (ApplicationApprovalChecklistPolicyItem.StatusSelectionPolicyItem status : item.statusesToSelect()) {
                ApprovalChecklistItem.StatusSelectionItem statusItem = EntityFactory.create(ApprovalChecklistItem.StatusSelectionItem.class);

                statusItem.statusSelection().setValue(status.statusSelection().getValue());

                checklistItem.statusesToSelect().add(statusItem);
            }

            checklistItem.leaseApplication().set(leaseApplication);
            Persistence.service().persist(checklistItem);

            leaseApplication.approvalChecklist().add(checklistItem);
        }
    }

    private void fireLeaseApplicationNotification(final Lease leaseId, final AlertType type) {
        Persistence.service().addTransactionCompletionHandler(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() {
                ServerSideFactory.create(NotificationFacade.class).leaseApplicationNotification(leaseId, type);
                return null;
            }
        });
    }
}