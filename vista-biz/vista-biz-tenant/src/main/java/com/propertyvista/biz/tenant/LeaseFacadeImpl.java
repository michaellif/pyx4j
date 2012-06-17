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

import com.propertyvista.biz.communication.CommunicationFacade;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.productcatalog.ProductCatalogFacade;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.occupancy.UnitTurnoverAnalysisFacade;
import com.propertyvista.biz.policy.IdAssignmentFacade;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.offering.Feature;
import com.propertyvista.domain.financial.offering.FeatureItemType;
import com.propertyvista.domain.financial.offering.ProductCatalog;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.policy.policies.DepositPolicy;
import com.propertyvista.domain.policy.policies.domain.DepositPolicyItem;
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
import com.propertyvista.misc.VistaTODO;

public class LeaseFacadeImpl implements LeaseFacade {

    private static final I18n i18n = I18n.get(LeaseFacadeImpl.class);

    @Override
    public Lease initLease(Lease leaseDraft) {
        // let client supply initial status value:
        if (leaseDraft.version().status().isNull()) {
            leaseDraft.version().status().setValue(Lease.Status.Created);
        } else {
            switch (leaseDraft.version().status().getValue()) {
            case Created:
            case ApplicationInProgress:
                break; // ok, allowed values...
            default:
                leaseDraft.version().status().setValue(Lease.Status.Created);
            }
        }
        //TODO
        leaseDraft.paymentFrequency().setValue(PaymentFrequency.Monthly);

        // Create Application by default
        leaseDraft.leaseApplication().status().setValue(LeaseApplication.Status.Created);
        leaseDraft.leaseApplication().leaseOnApplication().set(leaseDraft);

        saveCustomers(leaseDraft);

        ServerSideFactory.create(IdAssignmentFacade.class).assignId(leaseDraft);

        leaseDraft.billingAccount().accountNumber().setValue(ServerSideFactory.create(IdAssignmentFacade.class).createAccountNumber());
        leaseDraft.billingAccount().billCounter().setValue(0);

        Persistence.service().merge(leaseDraft.billingAccount());

        Persistence.service().merge(leaseDraft);

        if (leaseDraft.unit().getPrimaryKey() != null) {
            ServerSideFactory.create(OccupancyFacade.class).reserve(leaseDraft.unit().getPrimaryKey(), leaseDraft);
            leaseDraft = setUnit(leaseDraft, leaseDraft.unit());
        }
        return leaseDraft;
    }

    @Override
    public Lease setUnit(Lease leaseId, AptUnit unitId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId.getPrimaryKey());
        if (!Lease.Status.draft().contains(lease.version().status().getValue())) {
            throw new UserRuntimeException(i18n.tr("Invalid Lease State"));
        }

        boolean succeeded = false;
        AptUnit unit = Persistence.secureRetrieve(AptUnit.class, unitId.getPrimaryKey());
        Persistence.service().retrieve(unit.belongsTo());

        assert !lease.isValueDetached();

        EntityQueryCriteria<Service> criteria = new EntityQueryCriteria<Service>(Service.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().catalog(), unit.belongsTo().productCatalog()));
        criteria.add(PropertyCriterion.eq(criteria.proto().version().type(), lease.type()));
        Service service = Persistence.service().retrieve(criteria);
        if (service != null) {
            Persistence.service().retrieve(service.version().items());
            for (ProductItem item : service.version().items()) {
                if (item.element().equals(unit)) {
                    lease = setService(lease, unit.belongsTo().productCatalog(), service, item);
                    succeeded = true;
                    break;
                }
            }
        }

// TODO : ensure preloaders do work with this!!        
//        if (!succeeded) {
//            throw new UserRuntimeException(i18n.tr("There no service for selected unit: {0} from Building: {1}", unit.getStringView(), unit.belongsTo()
//                    .getStringView()));
//        }

        lease.unit().set(unit);
        persistLease(lease);

        return lease;
    }

    private Lease setService(Lease lease, ProductCatalog catalog, Service service, ProductItem serviceItem) {
        // set selected service:
        lease.version().leaseProducts().serviceItem().set(createBillableItem(serviceItem));

        DepositPolicy depositPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit().belongsTo(), DepositPolicy.class);

        for (DepositPolicyItem item : depositPolicy.policyItems()) {
            if (item.productType().equals(serviceItem.type())) {
                Deposit deposit = EntityFactory.create(Deposit.class);
                deposit.initialAmount().setValue(item.value().getValue());
                deposit.valueType().setValue(item.valueType().getValue());
                deposit.repaymentMode().setValue(item.repaymentMode().getValue());
                lease.version().leaseProducts().serviceItem().deposits().add(deposit);
            }
        }

        // clear current dependable data:
        lease.version().leaseProducts().featureItems().clear();
        lease.version().leaseProducts().concessions().clear();

        List<FeatureItemType> utilitiesToExclude = new ArrayList<FeatureItemType>(catalog.includedUtilities().size() + catalog.externalUtilities().size());
        utilitiesToExclude.addAll(catalog.includedUtilities());
        utilitiesToExclude.addAll(catalog.externalUtilities());

        // pre-populate utilities for the new service:
        for (Feature feature : service.version().features()) {
            for (ProductItem item : feature.version().items()) {
                switch (feature.version().type().getValue()) {
                case utility:
                    // filter out utilities included in price for selected building:
                    if (!utilitiesToExclude.contains(item.type())) {
                        lease.version().leaseProducts().featureItems().add(createBillableItem(item));
                    }
                    break;
                }
            }
        }
        return lease;
    }

    private BillableItem createBillableItem(ProductItem item) {
        BillableItem newItem = EntityFactory.create(BillableItem.class);
        newItem.item().set(item);
        newItem.agreedPrice().setValue(item.price().getValue());
        newItem._currentPrice().setValue(item.price().getValue());
        return newItem;
    }

    @Override
    public void persistLease(Lease lease) {
        boolean isUnitChanged = false;
        boolean doReserve = false;
        boolean doUnreserve = false;

        Lease origLease = Persistence.secureRetrieve(Lease.class, lease.getPrimaryKey().asCurrentKey());

        // check if unit reservation has changed
        if (!EqualsHelper.equals(origLease.unit().getPrimaryKey(), lease.unit().getPrimaryKey())) {
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
            doUnreserve = origLease.unit().getPrimaryKey() != null;
            doReserve = lease.unit().getPrimaryKey() != null;
        }

        saveCustomers(lease);
        Persistence.secureSave(lease);

        if (isUnitChanged) {
            if (doUnreserve) {
                ServerSideFactory.create(OccupancyFacade.class).unreserve(origLease.unit().getPrimaryKey());
            }
            if (doReserve) {
                ServerSideFactory.create(OccupancyFacade.class).reserve(lease.unit().getPrimaryKey(), lease);
            }
        }
    }

    @Override
    public void saveAsFinal(Lease lease) {
        lease.saveAction().setValue(SaveAction.saveAsFinal);
        persistLease(lease);
        // update unit rent price here:
        ServerSideFactory.create(ProductCatalogFacade.class).updateUnitRentPrice(lease);
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

    private void updateApplicationReferencesToFinalVersionOfLase(Lease lease) {
        lease.leaseApplication().leaseOnApplication().set(lease);
        Persistence.service().persist(lease.leaseApplication());
    }

    @Override
    public void createMasterOnlineApplication(Key leaseId) {
        Lease lease = Persistence.retrieveDraft(Lease.class, leaseId.asDraftKey());

        // Verify the status
        if (!Lease.Status.draft().contains(lease.version().status().getValue())) {
            throw new UserRuntimeException(i18n.tr("Invalid Lease State"));
        }
        if (LeaseApplication.Status.Created != lease.leaseApplication().status().getValue()) {
            throw new UserRuntimeException(i18n.tr("Invalid Application State"));
        }

        ServerSideFactory.create(OnlineApplicationFacade.class).createMasterOnlineApplication(lease.leaseApplication().onlineApplication());

        lease.leaseApplication().status().setValue(LeaseApplication.Status.OnlineApplicationInProgress);
        lease.version().status().setValue(Lease.Status.ApplicationInProgress);
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

        updateApplicationReferencesToFinalVersionOfLase(lease);

        ServerSideFactory.create(OccupancyFacade.class).approveLease(lease.unit().getPrimaryKey());

        ServerSideFactory.create(ProductCatalogFacade.class).updateUnitRentPrice(lease);

        if (!VistaTODO.removedForProduction) {
            ServerSideFactory.create(BillingFacade.class).runBilling(lease);
        }

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

        updateApplicationReferencesToFinalVersionOfLase(lease);

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

        updateApplicationReferencesToFinalVersionOfLase(lease);

        ServerSideFactory.create(OccupancyFacade.class).unreserve(lease.unit().getPrimaryKey());
    }

    @Override
    public void verifyExistingLease(Lease leaseId) {
        Lease lease = Persistence.secureRetrieveDraft(Lease.class, leaseId.getPrimaryKey());

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
        if (!VistaTODO.removedForProduction && lease.billingAccount().carryforwardBalance().isNull()
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
