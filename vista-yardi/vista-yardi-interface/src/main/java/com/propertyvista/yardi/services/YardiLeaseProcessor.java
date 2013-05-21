/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 17, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.mits.Customerinfo;
import com.yardi.entity.mits.YardiCustomer;
import com.yardi.entity.mits.YardiLease;
import com.yardi.entity.resident.ChargeDetail;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.Transactions;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.financial.ar.yardi.YardiARIntegrationAgent;
import com.propertyvista.biz.tenant.LeaseFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.domain.tenant.lease.extradata.YardiLeaseChargeData;
import com.propertyvista.yardi.mapper.TenantMapper;
import com.propertyvista.yardi.merger.LeaseMerger;
import com.propertyvista.yardi.merger.TenantMerger;

public class YardiLeaseProcessor {
    private final static Logger log = LoggerFactory.getLogger(YardiLeaseProcessor.class);

    public Lease findLease(String customerId, String propertyCode) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.eq(criteria.proto().leaseId(), customerId);
        return Persistence.service().retrieve(criteria);
    }

    public Lease processLease(RTCustomer rtCustomer, String propertyCode) {
        Lease existingLease = findLease(rtCustomer.getCustomerID(), propertyCode);
        if (existingLease != null) {
            log.info("      Updating lease {}", rtCustomer.getCustomerID());
            return updateLease(rtCustomer, existingLease);
        } else {
            log.info("      Creating new lease {}", rtCustomer.getCustomerID());
            return createLease(rtCustomer, propertyCode);
        }
    }

    private Lease updateLease(RTCustomer rtCustomer, Lease lease) {
        List<YardiCustomer> yardiCustomers = rtCustomer.getCustomers().getCustomer();
        Persistence.ensureRetrieve(lease.currentTerm().version().tenants(), AttachLevel.Attached);
        YardiLease yardiLease = yardiCustomers.get(0).getLease();

        if (new LeaseMerger().isLeaseChanged(yardiLease, lease)) {
            lease = new LeaseMerger().mergeLease(yardiLease, lease);
        }

        if (new LeaseMerger().isTermChanged(yardiLease, lease.currentTerm())) {
            lease.currentTerm().set(new LeaseMerger().mergeTerm(yardiLease, lease.currentTerm()));
        }

        if (new LeaseMerger().isPaymentTypeChanged(rtCustomer, lease)) {
            new LeaseMerger().mergePaymentType(rtCustomer, lease);
        }

        Persistence.ensureRetrieve(lease.currentTerm().version().tenants(), AttachLevel.Attached);
        if (new TenantMerger().checkChanges(yardiCustomers, lease.currentTerm().version().tenants())) {
            lease.currentTerm().set(new TenantMerger().updateTenants(yardiCustomers, lease.currentTerm()));
        }

        Persistence.ensureRetrieve(lease.currentTerm().version().tenants(), AttachLevel.Attached);
        if (new TenantMerger().changedNames(yardiCustomers, lease.currentTerm().version().tenants())) {
            new TenantMerger().updateTenantNames(rtCustomer, lease);
        }

        return ServerSideFactory.create(LeaseFacade.class).finalize(lease);
    }

    private Lease createLease(RTCustomer rtCustomer, String propertyCode) {
        List<YardiCustomer> yardiCustomers = rtCustomer.getCustomers().getCustomer();
        YardiLease yardiLease = yardiCustomers.get(0).getLease();

        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.eq(criteria.proto().building().propertyCode(), propertyCode);
        criteria.eq(criteria.proto().info().number(), YardiARIntegrationAgent.getUnitId(rtCustomer));
        AptUnit unit = Persistence.service().query(criteria).get(0);

        LeaseFacade leaseFacade = ServerSideFactory.create(LeaseFacade.class);

        Lease lease = leaseFacade.create(Lease.Status.ExistingLease);
        lease.leaseId().setValue(rtCustomer.getCustomerID());
        lease.type().setValue(ARCode.Type.Residential);

        // unit:
        if (unit.getPrimaryKey() != null) {
            leaseFacade.setUnit(lease, unit);
            leaseFacade.setLeaseAgreedPrice(lease, yardiLease.getCurrentRent());
        }

        //  dates:
        lease.currentTerm().termFrom().setValue(guessFromDate(yardiLease));

        if (yardiLease.getLeaseToDate() != null) {
            lease.currentTerm().termTo().setValue(new LogicalDate(yardiLease.getLeaseToDate()));
        } else {
            lease.currentTerm().type().setValue(LeaseTerm.Type.Periodic);
        }

        if (yardiLease.getExpectedMoveInDate() != null) {
            lease.expectedMoveIn().setValue(new LogicalDate(yardiLease.getExpectedMoveInDate()));
        }
        if (yardiLease.getActualMoveIn() != null) {
            lease.actualMoveIn().setValue(new LogicalDate(yardiLease.getActualMoveIn()));
        }

//        if (yardiLease.getExpectedMoveOutDate() != null) {
//            lease.expectedMoveOut().setValue(new LogicalDate(yardiLease.getExpectedMoveOutDate()));
//        }
//        if (yardiLease.getActualMoveOut() != null) {
//            lease.actualMoveOut().setValue(new LogicalDate(yardiLease.getActualMoveOut()));
//        }

        // misc.
        lease.billingAccount().paymentAccepted().setValue(BillingAccount.PaymentAccepted.getPaymentType(rtCustomer.getPaymentAccepted()));

        // tenants:
        for (YardiCustomer yardiCustomer : yardiCustomers) {
            lease.currentTerm().version().tenants().add(new TenantMapper().map(yardiCustomer, lease.currentTerm().version().tenants()));
        }

        lease = leaseFacade.persist(lease);
        leaseFacade.approve(lease, null, null);
        leaseFacade.activate(lease);

        return lease;
    }

    public Lease updateLeaseProducts(List<Transactions> transactions, Lease lease) {
        // TODO YardiLeaseIntegrationAgent.updateBillabelItem(lease, billableItem);
        log.info("      Updating billable item");
        List<BillableItem> newItems = new ArrayList<BillableItem>();
        boolean modified = false;
        for (Transactions tr : transactions) {
            if (tr == null || tr.getCharge() == null) {
                continue;
            }
            BillableItem item = createBillableItem(tr.getCharge().getDetail());
            if (new LeaseMerger().mergeBillableItem(item, lease)) {
                modified = true;
            }
            newItems.add(item);
        }
        if (modified) {
            // remove unmatched features from new version
            LeaseMerger merger = new LeaseMerger();
            Iterator<BillableItem> leaseItems = lease.currentTerm().version().leaseProducts().featureItems().iterator();
            while (leaseItems.hasNext()) {
                BillableItem item = leaseItems.next();
                if (merger.findBillableItem(item, newItems) == null) {
                    leaseItems.remove();
                }
            }
            Persistence.service().persist(lease);
        }
        return lease;
    }

    //
    // Some public utils:
    //
    public static boolean isSkipped(RTCustomer rtCustomer) {
        Customerinfo info = rtCustomer.getCustomers().getCustomer().get(0).getType();
        return !info.equals(Customerinfo.CURRENT_RESIDENT);
    }

    /**
     * We badly depends on this termFrom/leaseFrom date - so try to deduct as much as possible in the cases where it absent in Yardi!
     */
    public static LogicalDate guessFromDate(YardiLease yardiLease) {
        LogicalDate date;

        if (yardiLease.getLeaseFromDate() != null) {
            date = new LogicalDate(yardiLease.getLeaseFromDate());
        } else if (yardiLease.getActualMoveIn() != null) {
            date = new LogicalDate(yardiLease.getActualMoveIn());
        } else if (yardiLease.getLeaseSignDate() != null) {
            date = new LogicalDate(yardiLease.getLeaseSignDate());
        } else {
            throw new IllegalArgumentException("Can't deduct leaseFrom date!!!");
        }

        return date;
    }

    public static LogicalDate guessFromDateNoThrow(YardiLease yardiLease) {
        LogicalDate date;

        try {
            date = guessFromDate(yardiLease);
        } catch (IllegalArgumentException e) {
            log.error("Error", e);
            date = null;
        }

        return date;
    }

    private BillableItem createBillableItem(ChargeDetail charge) {
        BillableItem billableItem = EntityFactory.create(BillableItem.class);
        billableItem.uid().setValue(charge.getChargeCode());
        billableItem.agreedPrice().setValue(new BigDecimal(charge.getAmount()));
        billableItem.updated().setValue(new LogicalDate(SystemDateManager.getDate()));
        billableItem.effectiveDate().setValue(new LogicalDate(charge.getServiceFromDate()));
        billableItem.expirationDate().setValue(new LogicalDate(charge.getServiceToDate()));

        YardiLeaseChargeData extraData = EntityFactory.create(YardiLeaseChargeData.class);
        extraData.chargeCode().setValue(charge.getChargeCode());
        extraData.description().setValue(charge.getDescription());
        billableItem.extraData().set(extraData);

        return billableItem;
    }

}