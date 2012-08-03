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
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.dev.DataDump;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.deposit.DepositFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Guarantor2;
import com.propertyvista.domain.tenant.Tenant2;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.Lease2;
import com.propertyvista.domain.tenant.lease.Lease2.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease2.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.Lease2.Status;
import com.propertyvista.domain.tenant.lease.LeaseApplication2;
import com.propertyvista.domain.tenant.lease.LeaseTerm;

public class LeaseFacade2Impl implements LeaseFacade2 {

    private static final Logger log = LoggerFactory.getLogger(LeaseFacade2Impl.class);

    private static final I18n i18n = I18n.get(LeaseFacade2Impl.class);

    final boolean bugNo1549 = true;

    @Override
    public Lease2 init(Lease2 lease) {
        // check client supplied initial status value:
        if (lease.status().isNull()) {
            throw new IllegalStateException(i18n.tr("Invalid Lease2 State"));
        } else {
            switch (lease.status().getValue()) {
            case Created:
            case Application:
                break; // ok, allowed values...
            default:
                throw new IllegalStateException(i18n.tr("Invalid Lease2 State"));
            }
        }

        // TODO could be more variants in the future:
        lease.paymentFrequency().setValue(PaymentFrequency.Monthly);

        if (lease.currentLeaseTerm().isEmpty()) {
            lease.currentLeaseTerm().set(EntityFactory.create(LeaseTerm.class));
            lease.currentLeaseTerm().type().setValue(LeaseTerm.Type.FixedEx);
        }
        lease.currentLeaseTerm().lease().set(lease);

        // Create Application by default
        lease.leaseApplication().status().setValue(LeaseApplication2.Status.Created);
        lease.leaseApplication().leaseOnApplication().set(lease);

// TODO 2 uncomment then
//        ServerSideFactory.create(IdAssignmentFacade.class).assignId(lease);

//        lease.billingAccount().accountNumber().setValue(ServerSideFactory.create(IdAssignmentFacade.class).createAccountNumber());
//        lease.billingAccount().billCounter().setValue(0);

        return lease;
    }

    @Override
    public Lease2 setUnit(Lease2 lease, AptUnit unitId) {
        assert !lease.isValueDetached();
        if (!Lease2.Status.draft().contains(lease.status().getValue())) {
            throw new UserRuntimeException(i18n.tr("Invalid Lease2 State"));
        }

        AptUnit unit = Persistence.secureRetrieve(AptUnit.class, unitId.getPrimaryKey());
        Persistence.service().retrieve(unit.building());

        boolean succeeded = false;
        lease.unit().set(unit);

        EntityQueryCriteria<Service> criteria = new EntityQueryCriteria<Service>(Service.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().catalog(), unit.building().productCatalog()));
        criteria.add(PropertyCriterion.eq(criteria.proto().version().type(), lease.type()));
        servicesLoop: for (Service service : Persistence.service().query(criteria)) {
            EntityQueryCriteria<ProductItem> serviceCriteria = EntityQueryCriteria.create(ProductItem.class);
            serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().type(), ServiceItemType.class));
            serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().product(), service.version()));
            serviceCriteria.add(PropertyCriterion.eq(serviceCriteria.proto().element(), lease.unit()));
            ProductItem serviceItem = Persistence.service().retrieve(serviceCriteria);
            if (serviceItem != null) {
                assert (!lease.currentLeaseTerm().isNull());
                setService(lease.currentLeaseTerm(), serviceItem);
                succeeded = true;
                break servicesLoop;
            }
        }

        if (!succeeded) {
            if (bugNo1549) {
                DataDump.dumpToDirectory("lease-bug", "error", lease);
                DataDump.dumpToDirectory("lease-bug", "error", unit);
            }
            throw new UserRuntimeException(i18n.tr("There no service ''{0}'' for selected unit: {1} from Building: {2}", lease.type().getStringView(),
                    unit.getStringView(), unit.building().getStringView()));
        }

        return lease;
    }

    @Override
    public Lease2 persist(Lease2 lease) {
        boolean isNewLease = lease.getPrimaryKey() == null;

        Lease2 previousLeaseEdition = null;
        boolean doReserve = false;
        boolean doUnreserve = false;

        if (lease.status().getValue() == Status.Application | lease.status().getValue() == Status.Created) {
            if (isNewLease) {
                doReserve = !lease.unit().isNull();
            } else {
                previousLeaseEdition = Persistence.secureRetrieve(Lease2.class, lease.getPrimaryKey().asCurrentKey());

                if (!EqualsHelper.equals(previousLeaseEdition.unit().getPrimaryKey(), lease.unit().getPrimaryKey())) {
                    doUnreserve = previousLeaseEdition.unit().getPrimaryKey() != null;
                    doReserve = lease.unit().getPrimaryKey() != null;
                }
            }
        }

        // actual persist:
        if (lease.currentLeaseTerm().getPrimaryKey() == null) {
            LeaseTerm term = lease.currentLeaseTerm().duplicate();

            lease.currentLeaseTerm().set(null);
            Persistence.secureSave(lease);
            lease.currentLeaseTerm().set(term);

            lease.currentLeaseTerm().lease().set(lease);
            persist(lease.currentLeaseTerm());
        }
        Persistence.secureSave(lease);

        // update reservation if necessary:
        if (doUnreserve) {
            switch (lease.status().getValue()) {
            case Application:
                ServerSideFactory.create(OccupancyFacade.class).unreserve(previousLeaseEdition.unit().getPrimaryKey());
                break;
            case Created:
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
// TODO 2 uncomment then
//                ServerSideFactory.create(OccupancyFacade.class).reserve(lease.unit().getPrimaryKey(), lease);
                break;
            case Created:
// TODO 2 uncomment then
//                ServerSideFactory.create(OccupancyFacade.class).migrateStart(lease.unit().<AptUnit> createIdentityStub(), lease);
                break;
            default:
                throw new IllegalStateException(SimpleMessageFormat.format("it's not allowed to set unit while lease's state is \"{0}\"", lease.status()
                        .getValue()));
            }
        }

        return lease;
    }

    @Override
    public LeaseTerm setService(LeaseTerm leaseTerm, ProductItem serviceId) {
        assert !leaseTerm.isValueDetached();

        assert !leaseTerm.lease().isNull();
        if (leaseTerm.lease().isValueDetached()) {
            Persistence.service().retrieve(leaseTerm.lease());
        }

        Lease2 lease = leaseTerm.lease();

        if (!Lease2.Status.draft().contains(lease.status().getValue())) {
            throw new UserRuntimeException(i18n.tr("Invalid Lease State"));
        }

        // find/load all necessary ingredients:
        ProductItem serviceItem = Persistence.secureRetrieve(ProductItem.class, serviceId.getPrimaryKey());
        assert serviceItem != null;
        if (serviceItem.element().isValueDetached()) {
            Persistence.service().retrieve(serviceItem.element());
        }

        assert !lease.unit().isNull();
        if (lease.unit().isValueDetached()) {
            Persistence.service().retrieve(lease.unit());
        }
        assert !lease.unit().building().isNull();
        if (lease.unit().building().isValueDetached()) {
            Persistence.service().retrieve(lease.unit().building());
        }

        // double check:
        if (!lease.unit().equals(serviceItem.element())) {
            throw new UserRuntimeException(i18n.tr("Invalid Unit/Service combination"));
        }

        PolicyNode node = lease.unit().building();

        // set selected service:
        BillableItem billableItem = createBillableItem(serviceItem, node);
        leaseTerm.version().leaseProducts().serviceItem().set(billableItem);
// TODO 2 uncomment then
//        Persistence.service().retrieve(leaseTerm.billingAccount().deposits());
//        leaseTerm.billingAccount().deposits().clear();

        if (bugNo1549) {
            DataDump.dumpToDirectory("lease-bug", "serviceItem", leaseTerm);
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

        // update unit rent price here:
// TODO 2 uncomment then
//        ServerSideFactory.create(ProductCatalogFacade.class).updateUnitRentPrice(leaseTerm);

        return leaseTerm;
    }

    private void persistCustomers(LeaseTerm leaseTerm) {
        for (Tenant2 tenant : leaseTerm.version().tenants()) {
            if (!tenant.isValueDetached()) {
                if (tenant.id().isNull()) {
// TODO 2 uncomment then
//                    ServerSideFactory.create(IdAssignmentFacade.class).assignId(tenant);
                }
            }
            if (!tenant.customer().isValueDetached()) {
                ServerSideFactory.create(CustomerFacade.class).persistCustomer(tenant.customer());
            }
        }
        for (Guarantor2 guarantor : leaseTerm.version().guarantors()) {
            if (!guarantor.isValueDetached()) {
                if (guarantor.id().isNull()) {
// TODO 2 uncomment then
//                    ServerSideFactory.create(IdAssignmentFacade.class).assignId(guarantor);
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
        for (Deposit deposit : currentDeposits) {
            if (deposit.lifecycle().isNull()) {
//                Persistence.service().persist(ServerSideFactory.create(DepositFacade.class).createDepositLifecycle(deposit, leaseTerm.billingAccount()));
                Persistence.service().merge(deposit);
            }
        }
    }

    private void updateApplicationReferencesToFinalVersionOfLease(Lease2 lease) {
        lease.leaseApplication().leaseOnApplication().set(lease);
        Persistence.service().persist(lease.leaseApplication());
    }

    @Override
    public void setCurrentTerm(Lease2 leaseId, LeaseTerm leaseTermId) {
        Lease2 lease = Persistence.secureRetrieve(Lease2.class, leaseId.getPrimaryKey());
        if (lease.leaseTerms().contains(leaseTermId)) {
            lease.currentLeaseTerm().set(leaseTermId);
            Persistence.secureSave(lease);
        } else {
            throw new UserRuntimeException(i18n.tr("Invalid LeaseTerm supplied"));
        }
    }

    @Override
    public void createMasterOnlineApplication(Key leaseId) {
        Lease2 lease = Persistence.secureRetrieve(Lease2.class, leaseId);

        // Verify the status
        if (!Lease2.Status.draft().contains(lease.status().getValue())) {
            throw new UserRuntimeException(i18n.tr("Invalid Lease2 State"));
        }
        if (LeaseApplication2.Status.Created != lease.leaseApplication().status().getValue()) {
            throw new UserRuntimeException(i18n.tr("Invalid Application State"));
        }

// TODO 2 uncomment then
//        ServerSideFactory.create(OnlineApplicationFacade.class).createMasterOnlineApplication(lease.leaseApplication().onlineApplication());

        lease.leaseApplication().status().setValue(LeaseApplication2.Status.OnlineApplication);
        lease.status().setValue(Lease2.Status.Application);
        Persistence.service().persist(lease);
    }

    @Override
    public void approveApplication(Lease2 leaseId, Employee decidedBy, String decisionReason) {
        Lease2 lease = Persistence.secureRetrieve(Lease2.class, leaseId.getPrimaryKey());

// TODO 2 uncomment then
//        Set<ValidationFailure> validationFailures = new LeaseApprovalValidator().validate(lease);
//        if (!validationFailures.isEmpty()) {
//            List<String> errorMessages = new ArrayList<String>();
//            for (ValidationFailure failure : validationFailures) {
//                errorMessages.add(failure.getMessage());
//            }
//            String errorsRoster = StringUtils.join(errorMessages, ",\n");
//            throw new UserRuntimeException(i18n.tr("This lease cannot be approved due to following validation errors:\n{0}", errorsRoster));
//        }

        lease.status().setValue(Lease2.Status.Approved);
        lease.leaseApplication().status().setValue(LeaseApplication2.Status.Approved);
        lease.approvalDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        lease.leaseApplication().decidedBy().set(decidedBy);
        lease.leaseApplication().decisionReason().setValue(decisionReason);
        lease.leaseApplication().decisionDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));

        finalize(lease.currentLeaseTerm());
        persist(lease);

        updateApplicationReferencesToFinalVersionOfLease(lease);

        ServerSideFactory.create(OccupancyFacade.class).approveLease(lease.unit().getPrimaryKey());
// TODO 2 uncomment then
//        ServerSideFactory.create(ProductCatalogFacade.class).updateUnitRentPrice(lease);
//        Bill bill = ServerSideFactory.create(BillingFacade.class).runBilling(lease);
//        if (bill.billStatus().getValue() != BillStatus.Failed) {
//            ServerSideFactory.create(BillingFacade.class).confirmBill(bill);
//        } else {
//            throw new UserRuntimeException(i18n.tr("This lease cannot be approved due to failed first time bill"));
//        }

// TODO 2 uncomment then
//        if (!lease.leaseApplication().onlineApplication().isNull()) {
//        Persistence.service().retrieve(lease.version().tenants());
//            for (Tenant tenant : lease.version().tenants()) {
//                if (!tenant.application().isNull()) { // co-applicants have no
//                                                      // dedicated application
//                    ServerSideFactory.create(CommunicationFacade.class).sendApplicationStatus(tenant);
//                }
//            }
//        }
    }

    @Override
    public void declineApplication(Lease2 leaseId, Employee decidedBy, String decisionReason) {
        Lease2 lease = Persistence.secureRetrieve(Lease2.class, leaseId.getPrimaryKey());
        // TODO Review the status
        lease.status().setValue(Lease2.Status.Closed);
        lease.leaseApplication().status().setValue(LeaseApplication2.Status.Declined);
        lease.leaseApplication().decidedBy().set(decidedBy);
        lease.leaseApplication().decisionReason().setValue(decisionReason);
        lease.leaseApplication().decisionDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));

        Persistence.secureSave(lease);

        updateApplicationReferencesToFinalVersionOfLease(lease);

        ServerSideFactory.create(OccupancyFacade.class).unreserve(lease.unit().getPrimaryKey());

// TODO 2 uncomment then
//        if (!lease.leaseApplication().onlineApplication().isNull()) {
//            for (Tenant tenant : lease.version().tenants()) {
//                if (!tenant.application().isNull()) { // co-applicants have no
//                                                      // dedicated application
//                    ServerSideFactory.create(CommunicationFacade.class).sendApplicationStatus(tenant);
//                }
//            }
//        }
    }

    @Override
    public void cancelApplication(Lease2 leaseId, Employee decidedBy, String decisionReason) {
        Lease2 lease = Persistence.secureRetrieve(Lease2.class, leaseId.getPrimaryKey());
        // TODO Review the status
        lease.status().setValue(Lease2.Status.Closed);
        lease.leaseApplication().status().setValue(LeaseApplication2.Status.Cancelled);
        lease.leaseApplication().decidedBy().set(decidedBy);
        lease.leaseApplication().decisionReason().setValue(decisionReason);
        lease.leaseApplication().decisionDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));

        Persistence.secureSave(lease);

        updateApplicationReferencesToFinalVersionOfLease(lease);

        ServerSideFactory.create(OccupancyFacade.class).unreserve(lease.unit().getPrimaryKey());
    }

    @Override
    public void approveExistingLease(Lease2 leaseId) {
        Lease2 lease = Persistence.secureRetrieve(Lease2.class, leaseId.getPrimaryKey());
        lease.status().setValue(Lease2.Status.Approved);

        ServerSideFactory.create(OccupancyFacade.class).migratedApprove(lease.unit().<AptUnit> createIdentityStub());
// TODO 2 uncomment then
//        ServerSideFactory.create(ProductCatalogFacade.class).updateUnitRentPrice(lease);

        finalize(lease.currentLeaseTerm());
        persist(lease);

// TODO 2 uncomment then
//        Bill bill = ServerSideFactory.create(BillingFacade.class).runBilling(lease);
//        if (bill.billStatus().getValue() != BillStatus.Failed) {
//            ServerSideFactory.create(BillingFacade.class).confirmBill(bill);
//        } else {
//            throw new UserRuntimeException(i18n.tr("This lease cannot be approved due to failed first time bill"));
//        }
    }

    // TODO review code here
    @Override
    public void activate(Key leaseId) {
        Lease2 lease = Persistence.secureRetrieve(Lease2.class, leaseId);
        // set lease status to active ONLY if first (latest till now) bill is
        // confirmed:
        // TODO
//        if (lease.billingAccount().carryforwardBalance().isNull()
//                && ServerSideFactory.create(BillingFacade.class).getLatestBill(lease).billStatus().getValue() != Bill.BillStatus.Confirmed) {
//            throw new UserRuntimeException(i18n.tr("Please run and confirm first bill in order to activate the lease."));
//        }
        if (!EnumSet.of(Lease2.Status.Created, Lease2.Status.Approved).contains(lease.status().getValue())) {
            throw new UserRuntimeException(i18n.tr("Impossible to activate lease with status: {0}", lease.status().getStringView()));
        }

        lease.status().setValue(Status.Active);
        lease.activationDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        Persistence.secureSave(lease);

// TODO 2 uncomment then
//        ServerSideFactory.create(UnitTurnoverAnalysisFacade.class).propagateLeaseActivationToTurnoverReport(lease);

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
    public void createCompletionEvent(Key leaseId, CompletionType completionType, LogicalDate noticeDay, LogicalDate moveOutDay) {
        Lease2 lease = Persistence.secureRetrieve(Lease2.class, leaseId);
        if (lease == null) {
            throw new IllegalStateException("lease " + leaseId + " was not found");
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
        Lease2 lease = Persistence.secureRetrieve(Lease2.class, leaseId);
        if (lease == null) {
            throw new IllegalStateException("lease " + leaseId + " was not found");
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
        Lease2 lease = Persistence.secureRetrieve(Lease2.class, leaseId);

        lease.actualLeaseTo().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        lease.status().setValue(Status.Completed);

        Persistence.secureSave(lease);
    }

    @Override
    public void close(Key leaseId) {
        Lease2 lease = Persistence.secureRetrieve(Lease2.class, leaseId);

        lease.status().setValue(Status.Closed);

        Persistence.secureSave(lease);
    }
}
