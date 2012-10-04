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

import java.util.ArrayList;
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
import com.propertyvista.biz.financial.productcatalog.ProductCatalogFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.occupancy.UnitTurnoverAnalysisFacade;
import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.biz.validation.framework.ValidationFailure;
import com.propertyvista.biz.validation.validators.lease.LeaseApprovalValidator;
import com.propertyvista.domain.PublicVisibilityType;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.Product;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.Service.ServiceType;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Customer;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.PersonScreening;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.domain.tenant.lease.LeaseCustomer;
import com.propertyvista.domain.tenant.lease.LeaseCustomerGuarantor;
import com.propertyvista.domain.tenant.lease.LeaseCustomerTenant;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTerm.Type;

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
            lease.leaseApplication().status().setValue(null);
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

        Persistence.secureSave(leaseTerm);

        return leaseTerm;
    }

    @Override
    public LeaseTerm finalize(LeaseTerm leaseTerm) {
        finalizeBillableItems(leaseTerm);

        // migrate participants:
        Persistence.ensureRetrieve(leaseTerm.version().tenants(), AttachLevel.Attached);
        for (Tenant tenant : leaseTerm.version().tenants()) {
            tenant.screening().set(retrivePersonScreeningId(tenant.leaseCustomer().customer()));
        }

        Persistence.ensureRetrieve(leaseTerm.version().guarantors(), AttachLevel.Attached);
        for (Guarantor guarantor : leaseTerm.version().guarantors()) {
            guarantor.screening().set(retrivePersonScreeningId(guarantor.leaseCustomer().customer()));
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

        finalize(lease);

        ServerSideFactory.create(OccupancyFacade.class).approveLease(lease.unit().getPrimaryKey());
        ServerSideFactory.create(ProductCatalogFacade.class).updateUnitRentPrice(lease);
        Bill bill = ServerSideFactory.create(BillingFacade.class).runBilling(lease);
        if (bill.billStatus().getValue() != Bill.BillStatus.Failed) {
            ServerSideFactory.create(BillingFacade.class).confirmBill(bill);
        } else {
            throw new UserRuntimeException(i18n.tr("This lease cannot be approved due to failed first time bill"));
        }

        if (!lease.leaseApplication().onlineApplication().isNull()) {
            Persistence.service().retrieve(lease.currentTerm().version().tenants());
            for (Tenant tenant : lease.currentTerm().version().tenants()) {
                if (!tenant.application().isNull()) { // co-applicants have no
                                                      // dedicated application
                    ServerSideFactory.create(CommunicationFacade.class).sendApplicationStatus(tenant);
                }
            }
        }
    }

    public PersonScreening retrivePersonScreeningId(Customer customer) {
        if (customer.personScreening().getAttachLevel() == AttachLevel.Detached) {
            Persistence.service().retrieveMember(customer.personScreening(), AttachLevel.IdOnly);
        }
        if (customer.personScreening().isNull()) {
            return null;
        } else {
            return customer.personScreening();
        }
    }

    @Override
    public void declineApplication(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = load(leaseId, false);

        // TODO Review the status
        lease.status().setValue(Lease.Status.Closed);
        lease.leaseApplication().status().setValue(LeaseApplication.Status.Declined);
        lease.leaseApplication().decidedBy().set(decidedBy);
        lease.leaseApplication().decisionReason().setValue(decisionReason);
        lease.leaseApplication().decisionDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));

        Persistence.secureSave(lease);

        ServerSideFactory.create(OccupancyFacade.class).unreserve(lease.unit().getPrimaryKey());

        if (!lease.leaseApplication().onlineApplication().isNull()) {
            for (Tenant tenant : lease.currentTerm().version().tenants()) {
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
        lease.status().setValue(Lease.Status.Closed);
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

        ServerSideFactory.create(OccupancyFacade.class).migratedApprove(lease.unit().<AptUnit> createIdentityStub());
        ServerSideFactory.create(ProductCatalogFacade.class).updateUnitRentPrice(lease);

        finalize(lease);

        Bill bill = ServerSideFactory.create(BillingFacade.class).runBilling(lease);
        if (bill.billStatus().getValue() != Bill.BillStatus.Failed) {
            ServerSideFactory.create(BillingFacade.class).confirmBill(bill);
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

        lease.actualLeaseTo().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
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
        for (Tenant tenant : lease.currentTerm().version().tenants()) {
            term.version().tenants().add(businessDuplicate(tenant));
        }
        Persistence.service().retrieve(lease.currentTerm().version().guarantors());
        for (Guarantor guarantor : lease.currentTerm().version().guarantors()) {
            term.version().guarantors().add(businessDuplicate(guarantor));
        }

        return term;
    }

    private <P extends LeaseParticipant<?>> P businessDuplicate(P leaseParticipant) {
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
    public void createCompletionEvent(Lease leaseId, CompletionType completionType, LogicalDate noticeDay, LogicalDate moveOutDay) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId.getPrimaryKey());
        if (lease.status().getValue() != Status.Active) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }

        lease.completion().setValue(completionType);
        lease.moveOutNotice().setValue(noticeDay);
        lease.expectedMoveOut().setValue(moveOutDay);

        Persistence.secureSave(lease);

        ServerSideFactory.create(OccupancyFacade.class).endLease(lease.unit().getPrimaryKey());
    }

    @Override
    public void cancelCompletionEvent(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId.getPrimaryKey());
        if (lease.status().getValue() != Status.Active) {
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }

        lease.completion().setValue(null);
        lease.moveOutNotice().setValue(null);
        lease.expectedMoveOut().setValue(null);

        Persistence.secureSave(lease);

        ServerSideFactory.create(OccupancyFacade.class).cancelEndLease(lease.unit().getPrimaryKey());
    }

    @Override
    public void cancelLease(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId.getPrimaryKey());

        Status status = lease.status().getValue();
        lease.status().setValue(Status.Cancelled);

        if (status != Status.ExistingLease) {
            lease.actualLeaseTo().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        }

        Persistence.secureSave(lease);

        switch (status) {
        case ExistingLease:
            ServerSideFactory.create(OccupancyFacade.class).migratedCancel(lease.unit().<AptUnit> createIdentityStub());
            break;

        case Approved:
            ServerSideFactory.create(OccupancyFacade.class).unreserve(lease.unit().getPrimaryKey());
            break;

        case Active:
            ServerSideFactory.create(OccupancyFacade.class).endLease(lease.unit().getPrimaryKey());
            break;

        default:
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }
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

        if (!isProductAvailable(lease, service.holder())) {
            throw new IllegalArgumentException(i18n.tr("Unavailable Service"));
        }

        // clear current dependable data:
        leaseTerm.version().leaseProducts().featureItems().clear();
        leaseTerm.version().leaseProducts().concessions().clear();

        // pre-populate mandatory features for the new service:
        Persistence.service().retrieve(service.features());
        for (Feature feature : service.features()) {
            if (feature.version().mandatory().isBooleanTrue()) {
                if (isProductAvailable(lease, feature)) {
                    Persistence.service().retrieve(feature.version().items());
                    for (ProductItem item : feature.version().items()) {
                        if (item.isDefault().isBooleanTrue()) {
                            leaseTerm.version().leaseProducts().featureItems().add(createBillableItem(item, node));
                            break;
                        }
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
        for (Tenant tenant : leaseTerm.version().tenants()) {
            if (!tenant.isValueDetached()) {
                persistLeaseCustomer(leaseTerm, tenant, LeaseCustomerTenant.class);
            }
        }
        for (Guarantor guarantor : leaseTerm.version().guarantors()) {
            if (!guarantor.isValueDetached()) {
                persistLeaseCustomer(leaseTerm, guarantor, LeaseCustomerGuarantor.class);
            }
        }
    }

    private <E extends LeaseCustomer, P extends LeaseParticipant<?>> void persistLeaseCustomer(LeaseTerm leaseTerm, P leaseParticipant,
            Class<E> leaseCustomerClass) {
        boolean newCustomer = leaseParticipant.leaseCustomer().customer().id().isNull();
        if (!leaseParticipant.leaseCustomer().customer().isValueDetached()) {
            ServerSideFactory.create(CustomerFacade.class).persistCustomer(leaseParticipant.leaseCustomer().customer());
        }
        // Is new LeaseCustomer find or create new
        if (leaseParticipant.leaseCustomer().id().isNull()) {
            E leaseCustomer = null;
            if (!newCustomer) {
                EntityQueryCriteria<E> criteria = EntityQueryCriteria.create(leaseCustomerClass);
                criteria.add(PropertyCriterion.eq(criteria.proto().lease(), leaseTerm.lease()));
                criteria.add(PropertyCriterion.eq(criteria.proto().customer(), leaseParticipant.leaseCustomer().customer()));
                leaseCustomer = Persistence.service().retrieve(criteria);
            }
            if (leaseCustomer == null) {
                Customer customer = leaseParticipant.leaseCustomer().customer();
                leaseCustomer = EntityFactory.create(leaseCustomerClass);
                leaseCustomer.lease().set(leaseTerm.lease());
                leaseCustomer.customer().set(customer);
                ServerSideFactory.create(IdAssignmentFacade.class).assignId(leaseCustomer);
                Persistence.service().persist(leaseCustomer);
            }
            leaseParticipant.leaseCustomer().set(leaseCustomer);
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

    private void updateLeaseDates(Lease lease) {
        if (lease.status().getValue().isDraft()) {
            assert (!lease.currentTerm().isEmpty());

            lease.leaseFrom().set(lease.currentTerm().termFrom());
            lease.leaseTo().set(lease.currentTerm().termTo());
        } else {
            EntityQueryCriteria<LeaseTerm> criteria = new EntityQueryCriteria<LeaseTerm>(LeaseTerm.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().lease(), lease));
            criteria.add(PropertyCriterion.ne(criteria.proto().status(), LeaseTerm.Status.Offer));

            // set sorting by 'from date':
            Vector<Sort> sorts = new Vector<Sort>();
            sorts.add(new Sort(criteria.proto().termFrom().getPath().toString(), true));
            criteria.setSorts(sorts);

            List<LeaseTerm> terms = Persistence.service().query(criteria);
            assert (!terms.isEmpty());

            lease.leaseFrom().set(terms.get(0).termFrom());
            lease.leaseTo().set(terms.get(terms.size() - 1).termTo());
        }

        // some common checks/corrections: 
        if (lease.expectedMoveIn().isNull()) {
            lease.expectedMoveIn().setValue(lease.leaseFrom().getValue());
        }
        // term type corrections:
        switch (lease.currentTerm().type().getValue()) {
        case Fixed:
            lease.expectedMoveOut().setValue(lease.currentTerm().termTo().getValue());
            break;
        case FixedEx:
            lease.leaseTo().set(null); // special case for automatically renewed leases
            break;
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
        serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().version().serviceType(), leaseType));
        serviceCriteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
        for (Service service : Persistence.service().query(serviceCriteria)) {
            if (isProductAvailable(leaseTerm.lease(), service)) {
                EntityQueryCriteria<ProductItem> productCriteria = EntityQueryCriteria.create(ProductItem.class);
                productCriteria.add(PropertyCriterion.eq(productCriteria.proto().type(), ServiceItemType.class));
                productCriteria.add(PropertyCriterion.eq(productCriteria.proto().product(), service.version()));
                productCriteria.add(PropertyCriterion.eq(productCriteria.proto().element(), unit));
                ProductItem serviceItem = Persistence.service().retrieve(productCriteria);
                if (serviceItem != null) {
                    setService(leaseTerm, serviceItem);
                    succeeded = true;
                    break;
                }
            }
        }

        if (!succeeded) {
            throw new UserRuntimeException(i18n.tr("There no service ''{0}'' for selected unit: {1} from Building: {2}", leaseType.toString(),
                    unit.getStringView(), unit.building().getStringView()));
        }

        return leaseTerm;
    }

    @Override
    public boolean isProductAvailable(Lease lease, Product<? extends Product.ProductV<?>> product) {
        // calculate visibility:
        boolean visible = false;
        switch (lease.status().getValue()) {
        case Active:
        case Approved:
        case ExistingLease:
            visible = PublicVisibilityType.visibleToExistingTenant().contains(product.version().visibility().getValue());
            break;
        case Application:
            visible = PublicVisibilityType.visibleToTenant().contains(product.version().visibility().getValue());
            break;
        default:
            throw new IllegalStateException(SimpleMessageFormat.format("Invalid Lease Status (\"{0}\")", lease.status().getValue()));
        }

        return visible;
    }
}