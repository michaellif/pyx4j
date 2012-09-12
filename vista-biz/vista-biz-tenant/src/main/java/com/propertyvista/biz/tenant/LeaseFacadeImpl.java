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
import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
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
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.Service.ServiceType;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseApplication;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.LeaseTerm.Type;

public class LeaseFacadeImpl implements LeaseFacade {

    private static final Logger log = LoggerFactory.getLogger(LeaseFacadeImpl.class);

    private static final I18n i18n = I18n.get(LeaseFacadeImpl.class);

    @Override
    public Lease create(Status status) {
        Lease lease = EntityFactory.create(Lease.class);
        lease.status().setValue(status);
        return init(lease);
    }

    @Override
    public Lease init(Lease lease) {
        // check client supplied initial status value:
        if (lease.status().isNull()) {
            throw new IllegalStateException(i18n.tr("Invalid Lease State"));
        } else {
            switch (lease.status().getValue()) {
            case ExistingLease:
            case Application:
                break; // ok, allowed values...
            default:
                throw new IllegalStateException(i18n.tr("Invalid Lease State"));
            }
        }

        // TODO could be more variants in the future:
        lease.paymentFrequency().setValue(PaymentFrequency.Monthly);
        lease.type().setValue(Service.ServiceType.residentialUnit);

        if (lease.currentTerm().isNull()) {
            lease.currentTerm().set(EntityFactory.create(LeaseTerm.class));
            lease.currentTerm().type().setValue(LeaseTerm.Type.FixedEx);
            lease.currentTerm().status().setValue(LeaseTerm.Status.Working);
        }
        lease.currentTerm().lease().set(lease);

        // Create Application by default
        lease.leaseApplication().status().setValue(LeaseApplication.Status.Created);
        lease.leaseApplication().leaseOnApplication().set(lease);

        ServerSideFactory.create(IdAssignmentFacade.class).assignId(lease);

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
    public Lease load(Lease lease, boolean editingTerm) {
        if (lease.isValueDetached()) {
            Key leaseKey = lease.getPrimaryKey();
            lease = Persistence.secureRetrieve(Lease.class, leaseKey);
            if (lease == null) {
                throw new IllegalArgumentException("lease " + leaseKey + " was not found");
            }
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
        if (leaseTerm.lease().isValueDetached()) {
            Persistence.service().retrieve(leaseTerm.lease());
        }
        setUnit(leaseTerm.lease(), leaseTerm, unitId);
        return leaseTerm;
    }

    @Override
    public LeaseTerm setService(LeaseTerm leaseTerm, ProductItem serviceId) {
        assert !leaseTerm.lease().isNull();
        if (leaseTerm.lease().isValueDetached()) {
            Persistence.service().retrieve(leaseTerm.lease());
        }
        return setService(leaseTerm.lease(), leaseTerm, serviceId);
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

    @Override
    public LeaseTerm persist(LeaseTerm leaseTerm) {
        persistCustomers(leaseTerm);

        Persistence.secureSave(leaseTerm);

        return leaseTerm;
    }

    @Override
    public LeaseTerm finalize(LeaseTerm leaseTerm) {
        finalizeBillableItems(leaseTerm);

        leaseTerm.saveAction().setValue(SaveAction.saveAsFinal);
        leaseTerm = persist(leaseTerm);

        // update deposit mechanics:
        finalizeDeposits(leaseTerm);

        return leaseTerm;
    }

    @Override
    public void setCurrentTerm(Lease leaseId, LeaseTerm leaseTermId) {
        Lease lease = load(leaseId, false);

        Persistence.service().retrieveMember(lease.leaseTerms());
        if (lease.leaseTerms().contains(leaseTermId)) {
            lease.currentTerm().set(leaseTermId);

            Persistence.service().retrieve(lease.currentTerm());
            lease.currentTerm().status().setValue(LeaseTerm.Status.Working);
            lease.currentTerm().version().setValueDetached();
            persist(lease);
        } else {
            throw new UserRuntimeException(i18n.tr("Invalid LeaseTerm supplied"));
        }
    }

    @Override
    public void createMasterOnlineApplication(Lease leaseId) {
        Lease lease = load(leaseId, false);

        // Verify the status
        if (!Lease.Status.draft().contains(lease.status().getValue())) {
            throw new UserRuntimeException(i18n.tr("Invalid Lease State"));
        }
        if (LeaseApplication.Status.Created != lease.leaseApplication().status().getValue()) {
            throw new UserRuntimeException(i18n.tr("Invalid Application State"));
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
        lease.leaseApplication().status().setValue(LeaseApplication.Status.Approved);
        lease.approvalDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        lease.leaseApplication().decidedBy().set(decidedBy);
        lease.leaseApplication().decisionReason().setValue(decisionReason);
        lease.leaseApplication().decisionDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));

        finalize(lease);

        updateApplicationReferencesToFinalVersionOfLease(lease);

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

        updateApplicationReferencesToFinalVersionOfLease(lease);

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

        updateApplicationReferencesToFinalVersionOfLease(lease);

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

        // set lease status to active ONLY if first (latest till now) bill is
        // confirmed:
        // TODO
//        if (lease.billingAccount().carryforwardBalance().isNull()
//                && ServerSideFactory.create(BillingFacade.class).getLatestBill(lease).billStatus().getValue() != Bill.BillStatus.Confirmed) {
//            throw new UserRuntimeException(i18n.tr("Please run and confirm first bill in order to activate the lease."));
//        }
        if (!EnumSet.of(Lease.Status.ExistingLease, Lease.Status.Approved).contains(lease.status().getValue())) {
            throw new UserRuntimeException(i18n.tr("Impossible to activate lease with status: {0}", lease.status().getStringView()));
        }

        lease.status().setValue(Status.Active);
        lease.activationDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        Persistence.secureSave(lease);

        ServerSideFactory.create(UnitTurnoverAnalysisFacade.class).propagateLeaseActivationToTurnoverReport(lease);

        // TODO move to LeadFacad
        // update Lead state (if present)
        EntityQueryCriteria<Lead> criteria = new EntityQueryCriteria<Lead>(Lead.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().lease(), leaseId));
        Lead lead = Persistence.service().retrieve(criteria);
        if (lead != null) {
            lead.status().setValue(Lead.Status.rented);
            Persistence.service().persist(lead);
        }
    }

    @Override
    public LeaseTerm createOffer(Lease leaseId, Type type) {
        Lease lease = load(leaseId, false);

        LeaseTerm term = EntityFactory.create(LeaseTerm.class);
        term.status().setValue(LeaseTerm.Status.Offer);

        term.type().setValue(type);
        term.lease().set(lease);

        term.termFrom().setValue(lease.currentTerm().termTo().getValue());

        updateTermWithUnit(term, lease.unit(), lease.type().getValue());

        // migrate participants:
        Persistence.service().retrieve(lease.currentTerm().version().tenants());
        for (Tenant tenant : lease.currentTerm().version().tenants()) {
            Tenant copy = (Tenant) tenant.duplicate();
            copy.id().setValue(null);
            term.version().tenants().add(copy);
        }
        Persistence.service().retrieve(lease.currentTerm().version().guarantors());
        for (Guarantor guarantor : lease.currentTerm().version().guarantors()) {
            Guarantor copy = (Guarantor) guarantor.duplicate();
            copy.id().setValue(null);
            term.version().guarantors().add(copy);
        }

        return term;
    }

    @Override
    public void createCompletionEvent(Key leaseId, CompletionType completionType, LogicalDate noticeDay, LogicalDate moveOutDay) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
        if (lease == null) {
            throw new IllegalArgumentException("lease " + leaseId + " was not found");
        }
        if (lease.status().getValue() != Status.Active) {
            throw new IllegalStateException("lease " + leaseId + " must be " + Status.Active + " in order to perform Completion");
        }

        lease.completion().setValue(completionType);
        lease.moveOutNotice().setValue(noticeDay);
        lease.expectedMoveOut().setValue(moveOutDay);

        Persistence.secureSave(lease);

        ServerSideFactory.create(OccupancyFacade.class).endLease(lease.unit().getPrimaryKey());
    }

    @Override
    public void cancelCompletionEvent(Key leaseId) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
        if (lease == null) {
            throw new IllegalArgumentException("lease " + leaseId + " was not found");
        }
        if (lease.completion().isNull()) {
            throw new IllegalStateException("lease " + leaseId + " must have notice in order to perform 'cancelNotice'");
        }

        lease.completion().setValue(null);
        lease.moveOutNotice().setValue(null);
        lease.expectedMoveOut().setValue(null);

        Persistence.secureSave(lease);

        ServerSideFactory.create(OccupancyFacade.class).cancelEndLease(lease.unit().getPrimaryKey());
    }

    @Override
    public void complete(Key leaseId) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
        if (lease == null) {
            throw new IllegalArgumentException("lease " + leaseId + " was not found");
        }

        lease.actualLeaseTo().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        lease.status().setValue(Status.Completed);

        Persistence.secureSave(lease);
    }

    @Override
    public void close(Key leaseId) {
        Lease lease = Persistence.secureRetrieve(Lease.class, leaseId);
        if (lease == null) {
            throw new IllegalArgumentException("lease " + leaseId + " was not found");
        }

        lease.status().setValue(Status.Closed);

        Persistence.secureSave(lease);
    }

    // internals:

    private Lease setUnit(Lease lease, LeaseTerm leaseTerm, AptUnit unitId) {
        assert !lease.isValueDetached();

        AptUnit unit = Persistence.secureRetrieve(AptUnit.class, unitId.getPrimaryKey());
        if (unit.building().isValueDetached()) {
            Persistence.service().retrieve(unit.building());
        }

        if (leaseTerm.equals(lease.currentTerm())) {
            if (!Lease.Status.draft().contains(lease.status().getValue())) {
                throw new UserRuntimeException(i18n.tr("Invalid Lease State"));
            }
            lease.unit().set(unit);
        }

        updateTermWithUnit(leaseTerm, lease.unit(), lease.type().getValue());

        return lease;
    }

    private LeaseTerm updateTermWithUnit(LeaseTerm leaseTerm, AptUnit unit, ServiceType leaseType) {
        assert !unit.isValueDetached();
        if (unit.building().isValueDetached()) {
            Persistence.service().retrieve(unit.building());
        }

        boolean succeeded = false;

        EntityQueryCriteria<Service> criteria = new EntityQueryCriteria<Service>(Service.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().catalog(), unit.building().productCatalog()));
        criteria.add(PropertyCriterion.eq(criteria.proto().version().type(), leaseType));
        servicesLoop: for (Service service : Persistence.service().query(criteria)) {
            EntityQueryCriteria<ProductItem> serviceCriteria = EntityQueryCriteria.create(ProductItem.class);
            serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().type(), ServiceItemType.class));
            serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().product(), service.version()));
            serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().element(), unit));
            ProductItem serviceItem = Persistence.service().retrieve(serviceCriteria);
            if (serviceItem != null) {
                setService(leaseTerm, serviceItem);
                succeeded = true;
                break servicesLoop;
            }
        }

        if (!succeeded) {
            throw new UserRuntimeException(i18n.tr("There no service ''{0}'' for selected unit: {1} from Building: {2}", leaseType.toString(),
                    unit.getStringView(), unit.building().getStringView()));
        }

        return leaseTerm;
    }

    private LeaseTerm setService(Lease lease, LeaseTerm leaseTerm, ProductItem serviceId) {
        assert !leaseTerm.isValueDetached();

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
            throw new UserRuntimeException(i18n.tr("Invalid Unit/Service combination"));
        }

        PolicyNode node = lease.unit().building();

        // set selected service:
        BillableItem billableItem = createBillableItem(serviceItem, node);
        leaseTerm.version().leaseProducts().serviceItem().set(billableItem);

        if (leaseTerm.equals(lease.currentTerm())) {
            if (!Lease.Status.draft().contains(lease.status().getValue())) {
                throw new UserRuntimeException(i18n.tr("Invalid Lease State"));
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
                throw new IllegalStateException(SimpleMessageFormat.format("it's not allowed to unset unit while lease's state is \"{0}\"", lease.status()
                        .getValue()));
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
                throw new IllegalStateException(SimpleMessageFormat.format("it's not allowed to set unit while lease's state is \"{0}\"", lease.status()
                        .getValue()));
            }
        }

        return lease;
    }

    private void persistCustomers(LeaseTerm leaseTerm) {
        for (Tenant tenant : leaseTerm.version().tenants()) {
            if (!tenant.isValueDetached()) {
                if (tenant.id().isNull()) {
                    ServerSideFactory.create(IdAssignmentFacade.class).assignId(tenant);
                }
            }
            if (!tenant.customer().isValueDetached()) {
                ServerSideFactory.create(CustomerFacade.class).persistCustomer(tenant.customer());
            }
        }
        for (Guarantor guarantor : leaseTerm.version().guarantors()) {
            if (!guarantor.isValueDetached()) {
                if (guarantor.id().isNull()) {
                    ServerSideFactory.create(IdAssignmentFacade.class).assignId(guarantor);
                }
            }
            if (!guarantor.customer().isValueDetached()) {
                ServerSideFactory.create(CustomerFacade.class).persistCustomer(guarantor.customer());
            }
        }
    }

    private void finalizeBillableItems(LeaseTerm leaseTerm) {
        leaseTerm.version().leaseProducts().serviceItem().finalized().setValue(Boolean.TRUE);
        for (BillableItem item : leaseTerm.version().leaseProducts().featureItems()) {
            item.finalized().setValue(Boolean.TRUE);
        }
    }

    private void finalizeDeposits(LeaseTerm leaseTerm) {
        List<Deposit> currentDeposits = new ArrayList<Deposit>();
        currentDeposits.addAll(leaseTerm.version().leaseProducts().serviceItem().deposits());
        for (BillableItem item : leaseTerm.version().leaseProducts().featureItems()) {
            currentDeposits.addAll(item.deposits());
        }

        // wrap newly added deposits in DepositLifecycle:
        if (leaseTerm.lease().isValueDetached()) {
            Persistence.service().retrieve(leaseTerm.lease());
        }
        for (Deposit deposit : currentDeposits) {
            if (deposit.lifecycle().isNull()) {
                Persistence.service()
                        .persist(ServerSideFactory.create(DepositFacade.class).createDepositLifecycle(deposit, leaseTerm.lease().billingAccount()));
                Persistence.service().merge(deposit);
            }
        }
    }

    private void updateApplicationReferencesToFinalVersionOfLease(Lease lease) {
        lease.leaseApplication().leaseOnApplication().set(lease);
        Persistence.service().persist(lease.leaseApplication());
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

        // special case of automatically renewed leases:
        if (lease.currentTerm().type().getValue() == LeaseTerm.Type.FixedEx) {
            lease.leaseTo().set(null);
        }
    }
}