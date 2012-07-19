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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.essentials.server.dev.DataDump;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.UserRuntimeException;

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
import com.propertyvista.domain.financial.offering.ServiceItemType;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.Guarantor;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.domain.tenant.lead.Lead;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Deposit;
import com.propertyvista.domain.tenant.lease.DepositLifecycle;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.Lease.PaymentFrequency;
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseApplication;

public class LeaseFacadeImpl implements LeaseFacade {

    private static final I18n i18n = I18n.get(LeaseFacadeImpl.class);

    final boolean bugNo1549 = true;

    @Override
    public Lease init(Lease lease) {
        // check client supplied initial status value:
        if (lease.version().status().isNull()) {
            throw new IllegalStateException(i18n.tr("Invalid Lease State"));
        } else {
            switch (lease.version().status().getValue()) {
            case Created:
            case Application:
                break; // ok, allowed values...
            default:
                throw new IllegalStateException(i18n.tr("Invalid Lease State"));
            }
        }

        // TODO could be more variants in the future:
        lease.paymentFrequency().setValue(PaymentFrequency.Monthly);

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
        assert !lease.isValueDetached();
        if (!Lease.Status.draft().contains(lease.version().status().getValue())) {
            throw new UserRuntimeException(i18n.tr("Invalid Lease State"));
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
                setService(lease, serviceItem);
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
    public Lease setService(Lease lease, ProductItem serviceId) {
        assert !lease.isValueDetached();
        if (!Lease.Status.draft().contains(lease.version().status().getValue())) {
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
        BillableItem billableItem = createBillableItem(serviceItem);
        lease.version().leaseProducts().serviceItem().set(billableItem);
        Persistence.service().retrieve(lease.billingAccount().deposits());
        lease.billingAccount().deposits().clear();
        List<Deposit> deposits = createBillableItemDeposits(billableItem, node);
        if (deposits != null) {
            billableItem.deposits().addAll(deposits);
        }

        if (bugNo1549) {
            DataDump.dumpToDirectory("lease-bug", "serviceItem", lease);
        }

        // Service by Service item:
        Service.ServiceV service = null;
        Persistence.service().retrieve(serviceItem.product());
        if (serviceItem.product().getInstanceValueClass().equals(Service.ServiceV.class)) {
            service = serviceItem.product().cast();
        }
        assert service != null;

        // clear current dependable data:
        lease.version().leaseProducts().featureItems().clear();
        lease.version().leaseProducts().concessions().clear();

        // pre-populate mandatory features for the new service:
        Persistence.service().retrieve(service.features());
        for (Feature feature : service.features()) {
            if (feature.version().mandatory().isBooleanTrue()) {
                Persistence.service().retrieve(feature.version().items());
                for (ProductItem item : feature.version().items()) {
                    if (item.isDefault().isBooleanTrue()) {
                        billableItem = createBillableItem(item);
                        lease.version().leaseProducts().featureItems().add(billableItem);
                        deposits = createBillableItemDeposits(billableItem, node);
                        if (deposits != null) {
                            billableItem.deposits().addAll(deposits);
                        }
                    }
                }
            }
        }

        return lease;
    }

    @Override
    public BillableItem createBillableItem(ProductItem itemId) {
        ProductItem item = Persistence.secureRetrieve(ProductItem.class, itemId.getPrimaryKey());
        assert item != null;

        BillableItem newItem = EntityFactory.create(BillableItem.class);
        newItem.item().set(item);
        newItem.agreedPrice().setValue(item.price().getValue());

        return newItem;
    }

    @Override
    public List<Deposit> createBillableItemDeposits(BillableItem item, PolicyNode node) {
        return ServerSideFactory.create(DepositFacade.class).createRequiredDeposits(item, node);
    }

    @Override
    public Lease persist(Lease lease) {
        boolean isNewLease = lease.getPrimaryKey() == null;

        Lease previousLeaseEdition = null;
        boolean doReserve = false;
        boolean doUnreserve = false;

        if (lease.version().status().getValue() == Status.Application | lease.version().status().getValue() == Status.Created) {
            if (isNewLease) {
                doReserve = !lease.unit().isNull();
            } else {
                previousLeaseEdition = Persistence.secureRetrieve(Lease.class, lease.getPrimaryKey().asCurrentKey());

                if (!EqualsHelper.equals(previousLeaseEdition.unit().getPrimaryKey(), lease.unit().getPrimaryKey())) {
                    doUnreserve = previousLeaseEdition.unit().getPrimaryKey() != null;
                    doReserve = lease.unit().getPrimaryKey() != null;
                }
            }
        }

        // actual persist:
        persistCustomers(lease);
        Persistence.secureSave(lease);
        persistDeposits(lease);

        // update reservation if necessary:
        if (doUnreserve) {
            switch (lease.version().status().getValue()) {
            case Application:
                ServerSideFactory.create(OccupancyFacade.class).unreserve(previousLeaseEdition.unit().getPrimaryKey());
                break;
            case Created:
                ServerSideFactory.create(OccupancyFacade.class).migratedCancel(previousLeaseEdition.unit().<AptUnit> createIdentityStub());
                break;
            default:
                throw new IllegalStateException(SimpleMessageFormat.format("it's not allowed to unset unit while lease's state is \"{0}\"", lease.version()
                        .status().getValue()));
            }

        }
        if (doReserve) {
            switch (lease.version().status().getValue()) {
            case Application:
                ServerSideFactory.create(OccupancyFacade.class).reserve(lease.unit().getPrimaryKey(), lease);
                break;
            case Created:
                ServerSideFactory.create(OccupancyFacade.class).migrateStart(lease.unit().<AptUnit> createIdentityStub(), lease);
                break;
            default:
                throw new IllegalStateException(SimpleMessageFormat.format("it's not allowed to set unit while lease's state is \"{0}\"", lease.version()
                        .status().getValue()));
            }
        }

        return lease;
    }

    @Override
    public Lease saveAsFinal(Lease lease) {
        lease.saveAction().setValue(SaveAction.saveAsFinal);
        lease = persist(lease);

        // update unit rent price here:
        ServerSideFactory.create(ProductCatalogFacade.class).updateUnitRentPrice(lease);
        return lease;
    }

    private void persistCustomers(Lease lease) {
        for (Tenant tenant : lease.version().tenants()) {
            if (!tenant.isValueDetached()) {
                if (tenant.id().isNull()) {
                    ServerSideFactory.create(IdAssignmentFacade.class).assignId(tenant);
                }
            }
            if (!tenant.customer().isValueDetached()) {
                ServerSideFactory.create(CustomerFacade.class).persistCustomer(tenant.customer());
            }
        }
        for (Guarantor guarantor : lease.version().guarantors()) {
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

    private void persistDeposits(Lease lease) {
        List<Deposit> currentDeposits = new ArrayList<Deposit>();
        currentDeposits.addAll(lease.version().leaseProducts().serviceItem().deposits());
        for (BillableItem item : lease.version().leaseProducts().featureItems()) {
            currentDeposits.addAll(item.deposits());
        }

        List<Deposit> wrappedDeposits = new ArrayList<Deposit>();
        Persistence.service().retrieve(lease.billingAccount().deposits());
        for (DepositLifecycle dlc : lease.billingAccount().deposits()) {
            wrappedDeposits.add(dlc.deposit());
        }

        // clean current deposits from already wrapped ones:
        Iterator<Deposit> it = currentDeposits.iterator();
        while (it.hasNext()) {
            Deposit current = it.next();
            for (Deposit wrrapped : wrappedDeposits) {
                if (current.uid().equals(wrrapped.uid())) {
                    it.remove();
                }
            }
        }

        // wrap newly added deposits in DepositLifecycle:
        for (Deposit deposit : currentDeposits) {
            Persistence.service().persist(ServerSideFactory.create(DepositFacade.class).createDepositLifecycle(deposit, lease.billingAccount()));
        }
    }

    private void updateApplicationReferencesToFinalVersionOfLease(Lease lease) {
        lease.leaseApplication().leaseOnApplication().set(lease);
        Persistence.service().persist(lease.leaseApplication());
    }

    @Override
    public void createMasterOnlineApplication(Key leaseId) {
        Lease lease = Persistence.retrieveDraftForEdit(Lease.class, leaseId.asDraftKey());

        // Verify the status
        if (!Lease.Status.draft().contains(lease.version().status().getValue())) {
            throw new UserRuntimeException(i18n.tr("Invalid Lease State"));
        }
        if (LeaseApplication.Status.Created != lease.leaseApplication().status().getValue()) {
            throw new UserRuntimeException(i18n.tr("Invalid Application State"));
        }

        ServerSideFactory.create(OnlineApplicationFacade.class).createMasterOnlineApplication(lease.leaseApplication().onlineApplication());

        lease.leaseApplication().status().setValue(LeaseApplication.Status.OnlineApplication);
        lease.version().status().setValue(Lease.Status.Application);
        Persistence.service().persist(lease);
    }

    @Override
    public void approveApplication(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId.getPrimaryKey());
        Persistence.service().retrieve(lease.version().tenants());

        Set<ValidationFailure> validationFailures = new LeaseApprovalValidator().validate(lease);
        if (!validationFailures.isEmpty()) {
            List<String> errorMessages = new ArrayList<String>();
            for (ValidationFailure failure : validationFailures) {
                errorMessages.add(failure.getMessage());
            }
            String errorsRoster = StringUtils.join(errorMessages, ",\n");
            throw new UserRuntimeException(i18n.tr("This lease cannot be approved due to following validation errors:\n{0}", errorsRoster));
        }

        lease.version().status().setValue(Lease.Status.Approved);
        lease.leaseApplication().status().setValue(LeaseApplication.Status.Approved);
        lease.approvalDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        lease.leaseApplication().decidedBy().set(decidedBy);
        lease.leaseApplication().decisionReason().setValue(decisionReason);
        lease.leaseApplication().decisionDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));

        // finalize approved leases while saving:
        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.secureSave(lease);

        updateApplicationReferencesToFinalVersionOfLease(lease);

        ServerSideFactory.create(OccupancyFacade.class).approveLease(lease.unit().getPrimaryKey());
        ServerSideFactory.create(ProductCatalogFacade.class).updateUnitRentPrice(lease);
        ServerSideFactory.create(BillingFacade.class).runBilling(lease);

        if (!lease.leaseApplication().onlineApplication().isNull()) {
            for (Tenant tenant : lease.version().tenants()) {
                if (!tenant.application().isNull()) { // co-applicants have no
                                                      // dedicated application
                    ServerSideFactory.create(CommunicationFacade.class).sendApplicationStatus(tenant);
                }
            }
        }
    }

    @Override
    public void declineApplication(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId.getPrimaryKey());
        // TODO Review the status
        lease.version().status().setValue(Lease.Status.Closed);
        lease.leaseApplication().status().setValue(LeaseApplication.Status.Declined);
        lease.leaseApplication().decidedBy().set(decidedBy);
        lease.leaseApplication().decisionReason().setValue(decisionReason);
        lease.leaseApplication().decisionDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.secureSave(lease);

        updateApplicationReferencesToFinalVersionOfLease(lease);

        ServerSideFactory.create(OccupancyFacade.class).unreserve(lease.unit().getPrimaryKey());

        if (!lease.leaseApplication().onlineApplication().isNull()) {
            for (Tenant tenant : lease.version().tenants()) {
                if (!tenant.application().isNull()) { // co-applicants have no
                                                      // dedicated application
                    ServerSideFactory.create(CommunicationFacade.class).sendApplicationStatus(tenant);
                }
            }
        }
    }

    @Override
    public void cancelApplication(Lease leaseId, Employee decidedBy, String decisionReason) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId.getPrimaryKey());
        // TODO Review the status
        lease.version().status().setValue(Lease.Status.Closed);
        lease.leaseApplication().status().setValue(LeaseApplication.Status.Cancelled);
        lease.leaseApplication().decidedBy().set(decidedBy);
        lease.leaseApplication().decisionReason().setValue(decisionReason);
        lease.leaseApplication().decisionDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.secureSave(lease);

        updateApplicationReferencesToFinalVersionOfLease(lease);

        ServerSideFactory.create(OccupancyFacade.class).unreserve(lease.unit().getPrimaryKey());
    }

    @Override
    public void approveExistingLease(Lease leaseId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId.getPrimaryKey());
        lease.version().status().setValue(Lease.Status.Approved);

        ServerSideFactory.create(OccupancyFacade.class).migratedApprove(lease.unit().<AptUnit> createIdentityStub());
        ServerSideFactory.create(ProductCatalogFacade.class).updateUnitRentPrice(lease);

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.secureSave(lease);

        ServerSideFactory.create(BillingFacade.class).runBilling(lease);
    }

    // TODO review code here
    @Override
    public void activate(Key leaseId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        // set lease status to active ONLY if first (latest till now) bill is
        // confirmed:
        // TODO
        if (lease.billingAccount().carryforwardBalance().isNull()
                && ServerSideFactory.create(BillingFacade.class).getLatestBill(lease).billStatus().getValue() != Bill.BillStatus.Confirmed) {
            throw new UserRuntimeException(i18n.tr("Please run and confirm first bill in order to activate the lease."));
        }
        if (!EnumSet.of(Lease.Status.Created, Lease.Status.Approved).contains(lease.version().status().getValue())) {
            throw new UserRuntimeException(i18n.tr("Impossible to activate lease with status: {0}", lease.version().status().getStringView()));
        }

        lease.version().status().setValue(Status.Active);
        lease.saveAction().setValue(SaveAction.saveAsFinal);
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
