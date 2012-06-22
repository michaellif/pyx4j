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

import java.util.EnumSet;
import java.util.List;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
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
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.financial.offering.ServiceItemType;
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

public class LeaseFacadeImpl implements LeaseFacade {

    private static final I18n i18n = I18n.get(LeaseFacadeImpl.class);

    final boolean bugNo1549 = true;

    @Override
    public Lease init(Lease lease) {
        // let client supply initial status value:
        if (lease.version().status().isNull()) {
            lease.version().status().setValue(Lease.Status.Created);
        } else {
            switch (lease.version().status().getValue()) {
            case Created:
            case Application:
                break; // ok, allowed values...
            default:
                lease.version().status().setValue(Lease.Status.Created);
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
    public Lease initAndSave(Lease lease) {

        init(lease);

        saveCustomers(lease);
        Persistence.service().merge(lease.billingAccount());
        Persistence.service().merge(lease);

        if (lease.unit().getPrimaryKey() != null) {
            lease = setUnit(lease, lease.unit());
            if (bugNo1549) {
                DataDump.dumpToDirectory("lease-bug", "saving", lease);
            }
            persist(lease);
        }

        return lease;
    }

    @Override
    public Lease setUnit(Lease lease, AptUnit unitId) {
        if (!Lease.Status.draft().contains(lease.version().status().getValue())) {
            throw new UserRuntimeException(i18n.tr("Invalid Lease State"));
        }

        AptUnit unit = Persistence.secureRetrieve(AptUnit.class, unitId.getPrimaryKey());
        Persistence.service().retrieve(unit.building());

        assert !lease.isValueDetached();

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
        if (!Lease.Status.draft().contains(lease.version().status().getValue())) {
            throw new UserRuntimeException(i18n.tr("Invalid Lease State"));
        }

        ProductItem serviceItem = Persistence.secureRetrieve(ProductItem.class, serviceId.getPrimaryKey());

        assert serviceItem != null;
        assert !lease.isValueDetached();
        assert !lease.unit().isNull();

        // set selected service:
        lease.version().leaseProducts().serviceItem().set(createBillableItem(serviceItem, lease));
        if (bugNo1549) {
            DataDump.dumpToDirectory("lease-bug", "serviceItem", lease);
        }

        // find/load all necessary ingredients:
        if (lease.unit().isValueDetached()) {
            Persistence.service().retrieve(lease.unit());
        }
        if (lease.unit().building().isValueDetached()) {
            Persistence.service().retrieve(lease.unit().building());
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
                        lease.version().leaseProducts().featureItems().add(createBillableItem(item, lease));
                    }
                }
            }
        }

        return lease;
    }

    @Override
    public BillableItem createBillableItem(ProductItem item, Lease lease) {
        BillableItem newItem = EntityFactory.create(BillableItem.class);
        newItem.item().set(item);
        newItem.agreedPrice().setValue(item.price().getValue());
        newItem._currentPrice().setValue(item.price().getValue());

        // Add Deposits if present for the product::
        List<Deposit> deposits = ServerSideFactory.create(DepositFacade.class).createRequiredDeposits(item.type(), lease);
        if (deposits != null) {
            newItem.deposits().addAll(deposits);
        }

        return newItem;
    }

    @Override
    public Lease persist(Lease lease) {
        boolean doReserve = false;
        boolean doUnreserve = false;
        Lease origLease = null;

        // existing lease - check if unit reservation has changed:
        if (lease.getPrimaryKey() != null) {
            origLease = Persistence.secureRetrieve(Lease.class, lease.getPrimaryKey().asCurrentKey());
            if (!EqualsHelper.equals(origLease.unit().getPrimaryKey(), lease.unit().getPrimaryKey())) {
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
        } else { // newly created lease - reserve unit if set:
            doReserve = !lease.unit().isNull();
        }

        // actual persist:
        saveCustomers(lease);
        Persistence.secureSave(lease.billingAccount());
        Persistence.secureSave(lease);

        // update reservation if necessary:
        if (doUnreserve) {
            ServerSideFactory.create(OccupancyFacade.class).unreserve(origLease.unit().getPrimaryKey());
        }
        if (doReserve) {
            ServerSideFactory.create(OccupancyFacade.class).reserve(lease.unit().getPrimaryKey(), lease);
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

    private void saveCustomers(Lease lease) {
        for (Tenant tenant : lease.version().tenants()) {
            if (!tenant.customer().isValueDetached()) {
                ServerSideFactory.create(CustomerFacade.class).persistCustomer(tenant.customer());
            }
        }
        for (Guarantor guarantor : lease.version().guarantors()) {
            if (!guarantor.customer().isValueDetached()) {
                ServerSideFactory.create(CustomerFacade.class).persistCustomer(guarantor.customer());
            }
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
                if (!tenant.application().isNull()) { //co-applicants have no dedicated application
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
                if (!tenant.application().isNull()) { //co-applicants have no dedicated application
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

        ServerSideFactory.create(OccupancyFacade.class).approveLease(lease.unit().getPrimaryKey());
        ServerSideFactory.create(ProductCatalogFacade.class).updateUnitRentPrice(lease);

        lease.saveAction().setValue(SaveAction.saveAsFinal);
        Persistence.secureSave(lease);

        ServerSideFactory.create(BillingFacade.class).runBilling(lease);
    }

    // TODO review code here
    @Override
    public void activate(Key leaseId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId);
        // set lease status to active ONLY if first (latest till now) bill is confirmed: 
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

        //TODO move to LeadFacad
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
