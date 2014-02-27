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
package com.propertyvista.yardi.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.mits.Customerinfo;
import com.yardi.entity.mits.Unitleasestatusinfo;
import com.yardi.entity.mits.YardiCustomer;
import com.yardi.entity.mits.YardiLease;
import com.yardi.entity.resident.ChargeDetail;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.Transactions;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.Validate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.utils.EntityGraph;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.financial.ar.yardi.YardiARIntegrationAgent;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.tenant.lease.LeaseFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.ARCode.ActionType;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.policy.policies.YardiInterfacePolicy;
import com.propertyvista.domain.policy.policies.domain.YardiInterfacePolicyChargeCodeIgnore;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.CompletionType;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.portal.rpc.shared.PolicyNotFoundException;
import com.propertyvista.yardi.mergers.LeaseMerger;
import com.propertyvista.yardi.mergers.LeaseMerger.LeaseChargesMergeStatus;
import com.propertyvista.yardi.mergers.TenantMerger;
import com.propertyvista.yardi.services.ARCodeAdapter;

public class YardiLeaseProcessor {

    private final static Logger log = LoggerFactory.getLogger(YardiLeaseProcessor.class);

    private final ExecutionMonitor executionMonitor;

    public YardiLeaseProcessor() {
        this(null);
    }

    public YardiLeaseProcessor(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
    }

    public Lease findLease(Key yardiInterfaceId, String propertyCode, String customerId) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.eq(criteria.proto().leaseId(), customerId);
        criteria.eq(criteria.proto().integrationSystemId(), yardiInterfaceId);
        criteria.eq(criteria.proto().unit().building().propertyCode(), propertyCode);
        criteria.eq(criteria.proto().unit().building().integrationSystemId(), yardiInterfaceId);
        return Persistence.service().retrieve(criteria);
    }

    public Lease processLease(RTCustomer rtCustomer, Key yardiInterfaceId, String propertyCode) {
        Lease existingLease = findLease(yardiInterfaceId, propertyCode, getLeaseId(rtCustomer));
        if (existingLease != null) {
            log.info("      = Updating lease {} {}", yardiInterfaceId, getLeaseId(rtCustomer));
            return updateLease(rtCustomer, yardiInterfaceId, propertyCode, existingLease);
        } else {
            log.info("      = Creating new lease {} {}", yardiInterfaceId, getLeaseId(rtCustomer));
            return createLease(yardiInterfaceId, propertyCode, rtCustomer);
        }
    }

    private AptUnit retrieveUnit(Key yardiInterfaceId, String propertyCode, String unitNumber) {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.eq(criteria.proto().building().propertyCode(), propertyCode);
        criteria.eq(criteria.proto().building().integrationSystemId(), yardiInterfaceId);
        criteria.eq(criteria.proto().info().number(), unitNumber);
        AptUnit unit = Persistence.service().retrieve(criteria);
        if (unit == null) {
            throw new Error("Unit " + unitNumber + " not found in building " + propertyCode);
        } else {
            return unit;
        }
    }

    private Lease createLease(Key yardiInterfaceId, String propertyCode, RTCustomer rtCustomer) {
        List<YardiCustomer> yardiCustomers = rtCustomer.getCustomers().getCustomer();
        YardiLease yardiLease = yardiCustomers.get(0).getLease();

        Validate.isTrue(CommonsStringUtils.isStringSet(propertyCode), "Property Code required");
        String unitNumber = YardiARIntegrationAgent.getUnitId(rtCustomer);
        Validate.isTrue(CommonsStringUtils.isStringSet(unitNumber), "Unit number required");

        AptUnit unit = retrieveUnit(yardiInterfaceId, propertyCode, unitNumber);
        log.debug("creating lease {} for unit {}", getLeaseId(rtCustomer), unit.getStringView());

        LeaseFacade leaseFacade = ServerSideFactory.create(LeaseFacade.class);

        Lease lease = leaseFacade.create(Lease.Status.ExistingLease);
        lease.leaseId().setValue(getLeaseId(rtCustomer));
        lease.type().setValue(ARCode.Type.Residential);
        lease.integrationSystemId().setValue(yardiInterfaceId);

        // unit:
        if (unit.getPrimaryKey() != null) {
            leaseFacade.setPackage(lease.currentTerm(), unit, null, Collections.<BillableItem> emptyList());
            leaseFacade.setLeaseAgreedPrice(lease, yardiLease.getCurrentRent());
        }

        //  dates:
        lease.currentTerm().termFrom().setValue(guessFromDate(yardiLease));

        if (yardiLease.getLeaseToDate() != null) {
            lease.currentTerm().termTo().setValue(getLogicalDate(yardiLease.getLeaseToDate()));
        } else {
            lease.currentTerm().type().setValue(LeaseTerm.Type.Periodic);
        }

        if (yardiLease.getExpectedMoveInDate() != null) {
            lease.expectedMoveIn().setValue(getLogicalDate(yardiLease.getExpectedMoveInDate()));
        }
        if (yardiLease.getActualMoveIn() != null) {
            lease.actualMoveIn().setValue(getLogicalDate(yardiLease.getActualMoveIn()));
        }

        if (yardiLease.getExpectedMoveOutDate() != null) {
            lease.expectedMoveOut().setValue(getLogicalDate(yardiLease.getExpectedMoveOutDate()));
        }

        // misc.
        lease.billingAccount().paymentAccepted().setValue(BillingAccount.PaymentAccepted.getPaymentType(rtCustomer.getPaymentAccepted()));

        lease.currentTerm().yardiLeasePk().setValue(getYardiLeasePk(yardiCustomers));
        // TODO Need to find another lease to merge with

        // tenants:
        new TenantMerger(executionMonitor).createTenants(yardiCustomers, lease.currentTerm());

        lease = leaseFacade.persist(lease);
        leaseFacade.activate(lease);
        Persistence.service().retrieve(lease);

        log.debug("lease {} created for unit {}", lease.leaseId().getValue(), lease.unit().getStringView());

        // manage state:

        if (isOnNotice(rtCustomer)) {
            lease = markLeaseOnNotice(lease, yardiLease);
        }
        if (isFormerLease(rtCustomer)) {
            lease = completeLease(lease, yardiLease);
        }

        return lease;
    }

    /**
     * The <MITS:Description> node is the primary key of the person record. The <CustomerID> node is the tenant code related to the person. When a Unit Transfer
     * happens, a new person record (MITS:Description node) will be created with the new property info, unit info, etc., but it will still point to that same
     * tenant code
     */
    private String getYardiLeasePk(List<YardiCustomer> yardiCustomers) {
        return yardiCustomers.get(0).getDescription();
    }

    private Lease updateLease(RTCustomer rtCustomer, Key yardiInterfaceId, String propertyCode, Lease leaseId) {
        List<YardiCustomer> yardiCustomers = rtCustomer.getCustomers().getCustomer();
        YardiLease yardiLease = yardiCustomers.get(0).getLease();

        Lease lease = ServerSideFactory.create(LeaseFacade.class).load(leaseId, true);
        Persistence.ensureRetrieve(lease.currentTerm().version().tenants(), AttachLevel.Attached);

        boolean leaseMove = false;
        if (!lease.currentTerm().yardiLeasePk().isNull()
                && !EqualsHelper.equals(lease.currentTerm().yardiLeasePk().getValue(), getYardiLeasePk(yardiCustomers))) {
            // TODO Need to find another lease to merge with
            leaseMove = true;
        }

        boolean toPersist = false;

        // if unit update is occurred:
        String unitNumber = YardiARIntegrationAgent.getUnitId(rtCustomer);
        Validate.isTrue(CommonsStringUtils.isStringSet(unitNumber), "Unit number required");
        if (leaseMove || (!unitNumber.equals(lease.unit().info().number().getValue()))) {
            Validate.isTrue(CommonsStringUtils.isStringSet(propertyCode), "Property Code required");
            AptUnit unit = retrieveUnit(yardiInterfaceId, propertyCode, unitNumber);
            log.debug("updating unit {} for lease {}", unit.getStringView(), getLeaseId(rtCustomer));

            lease = new LeaseMerger().updateUnit(unit, lease);
            toPersist = true;
            log.debug("        - LeaseUnitChanged...");
        }

        if (LeaseMerger.isLeaseDatesChanged(yardiLease, lease)) {
            lease = new LeaseMerger().mergeLeaseDates(yardiLease, lease);
            toPersist = true;
            log.debug("        - LeaseDatesChanged...");
        }

        if (LeaseMerger.isTermDatesChanged(yardiLease, lease.currentTerm())) {
            lease.currentTerm().set(new LeaseMerger().mergeTermDates(yardiLease, lease.currentTerm()));
            toPersist = true;

            log.debug("        - TermDatesChanged...");
        }

        if (new LeaseMerger().isPaymentTypeChanged(rtCustomer, lease)) {
            new LeaseMerger().mergePaymentType(rtCustomer, lease);
            toPersist = true;
            log.debug("        - PaymentTypeChanged...");
        }

        Persistence.ensureRetrieve(lease.currentTerm().version().tenants(), AttachLevel.Attached);
        Persistence.ensureRetrieve(lease.currentTerm().version().guarantors(), AttachLevel.Attached);
        if (new TenantMerger().isChanged(yardiCustomers, lease.currentTerm().version().tenants(), lease.currentTerm().version().guarantors())) {
            lease.currentTerm().set(new TenantMerger(executionMonitor).updateTenants(yardiCustomers, lease.currentTerm()));
            toPersist = true;
            log.debug("        - TenantsChanged...");
        }

        lease.currentTerm().yardiLeasePk().setValue(getYardiLeasePk(yardiCustomers));
        if (new TenantMerger(executionMonitor).updateTenantsData(yardiCustomers, lease.currentTerm())) {
            toPersist = true;
            log.debug("        - TenantDataChanged...");
        }

        // persist: 

        if (toPersist) {
            lease = ServerSideFactory.create(LeaseFacade.class).finalize(lease);
            log.debug("        >> Persisting/Finalizing lease! <<");
        }

        // manage state:

        if (lease.status().getValue().isActive()) {
            if (isOnNotice(rtCustomer)) {
                if (lease.completion().getValue() != CompletionType.Notice) {
                    lease = markLeaseOnNotice(lease, yardiLease);
                }
            } else if (lease.completion().getValue() == CompletionType.Notice) {
                lease = cancelMarkLeaseOnNotice(lease, yardiLease);
            }
            if (isFormerLease(rtCustomer)) { // active -> past transition:
                lease = completeLease(lease, yardiLease);
            }
        } else { // past -> active transition (cancel Move Out in Yardi!):
            if (isCurrentLease(rtCustomer) || isFutureLease(rtCustomer)) {
                lease = cancelLeaseCompletion(lease, yardiLease);
            }
        }

        return lease;
    }

    public void setLeaseChargesComaptibleIds(Lease lease) {

        List<BillableItem> allBillableItems = new ArrayList<>();
        allBillableItems.add(lease.currentTerm().version().leaseProducts().serviceItem());
        for (BillableItem bi : lease.currentTerm().version().leaseProducts().featureItems()) {
            allBillableItems.add(bi);
        }

        Map<String, Integer> chargeCodeItemsCount = new HashMap<>();

        for (BillableItem bi : allBillableItems) {
            String chargeCode;

            Validate.isTrue(bi.item().product().holder().code().yardiChargeCodes().size() > 0, "yardiChargeCodes are not mapped to product {0}", bi.item()
                    .product().holder());

            chargeCode = bi.item().product().holder().code().yardiChargeCodes().get(0).yardiChargeCode().getValue();

            Integer chargeCodeItemNo = chargeCodeItemsCount.get(chargeCode);
            if (chargeCodeItemNo == null) {
                chargeCodeItemNo = 1;
            } else {
                chargeCodeItemNo = chargeCodeItemNo + 1;
            }
            chargeCodeItemsCount.put(chargeCode, chargeCodeItemNo);

            bi.uid().setValue(billableItemUid(chargeCode, chargeCodeItemNo));
        }
    }

    public Lease updateLeaseProducts(List<Transactions> transactions, Lease leaseId) {
        Lease lease = ServerSideFactory.create(LeaseFacade.class).load(leaseId, true);
        log.info("        = Updating billable items for lease: {} ", lease.leaseId().getStringView());
        List<BillableItem> newItems = new ArrayList<BillableItem>();

        // Ensure all items are uniquely identified by the order in YArdi
        /**
         * rrent -> rrent:1
         * rpark -> rpark:1
         * rpark -> rpark:2
         * routpark -> routpark:1
         */
        Map<String, Integer> chargeCodeItemsCount = new HashMap<String, Integer>();

        // build ignored ChargeCode set
        Set<String> ignoredCodes = new HashSet<String>();
        try {
            YardiInterfacePolicy yardiIfacePolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(lease.unit().building(),
                    YardiInterfacePolicy.class);
            for (YardiInterfacePolicyChargeCodeIgnore policyItem : yardiIfacePolicy.ignoreChargeCodes()) {
                log.debug("    Processing ChargeCodeIgnore entry: {}", policyItem.yardiChargeCode().getValue());
                ignoredCodes.add(policyItem.yardiChargeCode().getValue());
            }
        } catch (PolicyNotFoundException e) {
            // ignore
        }

        List<BillableItem> uidLookupList = new ArrayList<BillableItem>(lease.currentTerm().version().leaseProducts().featureItems());
        uidLookupList.add(lease.currentTerm().version().leaseProducts().serviceItem());

        for (Transactions tr : transactions) {
            if (tr == null || tr.getCharge() == null) {
                continue;
            }

            String chargeCode = tr.getCharge().getDetail().getChargeCode();

            // consult ignored codes
            if (ignoredCodes.contains(chargeCode)) {
                log.debug("    charge code ignored: {}", chargeCode);
                continue;
            }

            // update charge code item counter
            Integer chargeCodeItemNo = chargeCodeItemsCount.get(chargeCode);
            chargeCodeItemNo = chargeCodeItemNo == null ? 1 : chargeCodeItemNo + 1;
            chargeCodeItemsCount.put(chargeCode, chargeCodeItemNo);

            // create new item from an empty stub or from a copy if we find item with the same uid
            String uid = billableItemUid(tr.getCharge().getDetail().getChargeCode(), chargeCodeItemNo);
            BillableItem newItem = null;
            for (BillableItem leaseItem : uidLookupList) {
                if (uid.compareTo(leaseItem.uid().getValue()) == 0) {
                    newItem = EntityGraph.businessDuplicate(leaseItem);
                }
            }
            if (newItem == null) {
                newItem = EntityFactory.create(BillableItem.class);
                newItem.uid().setValue(uid);
            }

            newItems.add(createBillableItem(tr.getCharge().getDetail(), newItem));
        }

        LeaseChargesMergeStatus mergeStatus = new LeaseMerger().mergeBillableItems(newItems, lease, executionMonitor);
        if (!LeaseChargesMergeStatus.NoChange.equals(mergeStatus)) {
            ServerSideFactory.create(LeaseFacade.class).finalize(lease);
            log.debug("        >> Finalizing lease! <<");

            if (LeaseChargesMergeStatus.TotalAmount.equals(mergeStatus)) {
                String msg = SimpleMessageFormat.format("charges changed for lease {0}", leaseId.leaseId());
                log.info(msg);
                if (executionMonitor != null) {
                    executionMonitor.addInfoEvent("chargesChanged", msg);
                }
            }
        }

        return lease;
    }

    public boolean expireLeaseProducts(Lease leaseId) {
        Lease lease = ServerSideFactory.create(LeaseFacade.class).load(leaseId, true);
        if (BigDecimal.ZERO.compareTo(lease.currentTerm().version().leaseProducts().serviceItem().agreedPrice().getValue(BigDecimal.ZERO)) < 0) {
            log.info("      Terminating billable items for lease {} ", lease.leaseId().getStringView());

            // set service charge to zero
            lease.currentTerm().version().leaseProducts().serviceItem().agreedPrice().setValue(BigDecimal.ZERO);
            // remove features
            lease.currentTerm().version().leaseProducts().featureItems().clear();
            // finalize
            ServerSideFactory.create(LeaseFacade.class).finalize(lease);
            log.debug("        >> Finalizing lease! <<");

            return true;
        }
        return false;
    }

    //
    // Some public utils:
    //
    public static boolean isEligibleForProcessing(RTCustomer rtCustomer) {
        Customerinfo info = rtCustomer.getCustomers().getCustomer().get(0).getType();
        // @formatter:off
        // list eligible for processing types here:
        return info.equals(Customerinfo.CURRENT_RESIDENT) ||
               info.equals(Customerinfo.FORMER_RESIDENT)  ||
               info.equals(Customerinfo.FUTURE_RESIDENT);
        // @formatter:on
    }

    public static String getLeaseId(RTCustomer rtCustomer) {
        return rtCustomer.getCustomerID();
    }

    public static boolean isCurrentLease(RTCustomer rtCustomer) {
        Customerinfo info = rtCustomer.getCustomers().getCustomer().get(0).getType();
        return Customerinfo.CURRENT_RESIDENT.equals(info);
    }

    public static boolean isFormerLease(RTCustomer rtCustomer) {
        Customerinfo info = rtCustomer.getCustomers().getCustomer().get(0).getType();
        return Customerinfo.FORMER_RESIDENT.equals(info);
    }

    public static boolean isFutureLease(RTCustomer rtCustomer) {
        Customerinfo info = rtCustomer.getCustomers().getCustomer().get(0).getType();
        return Customerinfo.FUTURE_RESIDENT.equals(info);
    }

    public static boolean isOnNotice(RTCustomer rtCustomer) {
        Unitleasestatusinfo info = rtCustomer.getRTUnit().getUnit().getInformation().get(0).getUnitLeasedStatus();
        return (Unitleasestatusinfo.ON_NOTICE.equals(info) || Unitleasestatusinfo.LEASED_ON_NOTICE.equals(info));
    }

    /**
     * We badly depends on this termFrom/leaseFrom date - so try to deduct as much as possible in the cases where it absent in Yardi!
     */
    public static LogicalDate guessFromDate(YardiLease yardiLease) {
        LogicalDate date;

        if (yardiLease.getLeaseFromDate() != null) {
            date = getLogicalDate(yardiLease.getLeaseFromDate());
        } else if (yardiLease.getActualMoveIn() != null) {
            date = getLogicalDate(yardiLease.getActualMoveIn());
        } else if (yardiLease.getExpectedMoveInDate() != null) {
            date = getLogicalDate(yardiLease.getExpectedMoveInDate());
        } else if (yardiLease.getLeaseSignDate() != null) {
            date = getLogicalDate(yardiLease.getLeaseSignDate());
        } else {
            date = new LogicalDate(SystemDateManager.getDate());
            log.warn("Empty Yardi 'Lease From' date - substitute with current date!");
        }

        return date;
    }

    /**
     * Sort list of Yardi leases (RTCustomer-s) by lease status: former -> current -> future
     * 
     * @param rtCustomers
     *            - list to sort
     * @return - sorted input list
     */

    public static List<RTCustomer> sortRtCustomers(List<RTCustomer> rtCustomers) {
        Collections.sort(rtCustomers, new Comparator<RTCustomer>() {
            @Override
            public int compare(RTCustomer c1, RTCustomer c2) {

                int s1 = (isFormerLease(c1) ? -1 : (isFutureLease(c1) ? 1 : 0));
                int s2 = (isFormerLease(c2) ? -1 : (isFutureLease(c2) ? 1 : 0));

                return (s1 < s2 ? -1 : (s1 > s2 ? 1 : 0));
            }
        });

        return rtCustomers;
    }

    // @see function in migration PadProcessorInformation.billableItemId  that use the same value
    public String billableItemUid(String chargeCode, int chargeCodeItemNo) {
        return chargeCode + ":" + chargeCodeItemNo;
    }

    private BillableItem createBillableItem(ChargeDetail detail, BillableItem newItem) {

        newItem.agreedPrice().setValue(new BigDecimal(detail.getAmount()));
        newItem.updated().setValue(getLogicalDate(SystemDateManager.getDate()));
        newItem.effectiveDate().setValue(getLogicalDate(detail.getServiceFromDate()));
        newItem.expirationDate().setValue(getLogicalDate(detail.getServiceToDate()));
        newItem.description().setValue(getLeaseChargeDescription(detail));
        newItem.yardiChargeCode().setValue(detail.getChargeCode());

        return newItem;
    }

    private static LogicalDate getLogicalDate(Date date) {
        return date == null ? null : new LogicalDate(date);
    }

    private static String getLeaseChargeDescription(ChargeDetail detail) {
        ARCode arCode = new ARCodeAdapter().retrieveARCode(ActionType.Debit, detail.getChargeCode());
        return arCode == null ? detail.getDescription() : arCode.name().getValue();
    }

    // Lease state management: 

    private static Lease markLeaseOnNotice(Lease lease, YardiLease yardiLease) {
        ServerSideFactory.create(LeaseFacade.class).createCompletionEvent(lease, CompletionType.Notice, new LogicalDate(SystemDateManager.getDate()),
                getLogicalDate(yardiLease.getExpectedMoveOutDate()), null);

        Persistence.service().retrieve(lease);
        return lease;
    }

    private static Lease cancelMarkLeaseOnNotice(Lease lease, YardiLease yardiLease) {
        ServerSideFactory.create(LeaseFacade.class).cancelCompletionEvent(lease, null, "Yardi notice rollback!");

        Persistence.service().retrieve(lease);
        return lease;
    }

    public static void completeLease(Lease lease) {
        YardiLease yardiLease = new YardiLease();
        yardiLease.setExpectedMoveOutDate(SystemDateManager.getDate());
        yardiLease.setActualMoveOut(SystemDateManager.getDate());

        Persistence.ensureRetrieve(lease, AttachLevel.Attached);

        completeLease(lease, yardiLease);
    }

    private static Lease completeLease(Lease lease, YardiLease yardiLease) {
        if (lease.completion().isNull()) {
            ServerSideFactory.create(LeaseFacade.class).createCompletionEvent(lease, CompletionType.Termination, new LogicalDate(SystemDateManager.getDate()),
                    getLogicalDate(yardiLease.getExpectedMoveOutDate()), getLogicalDate(yardiLease.getActualMoveOut()));
        }

        ServerSideFactory.create(LeaseFacade.class).moveOut(lease, getLogicalDate(yardiLease.getActualMoveOut()));

        Persistence.service().retrieve(lease);
        return lease;
    }

    private static Lease cancelLeaseCompletion(Lease lease, YardiLease yardiLease) {
        ServerSideFactory.create(LeaseFacade.class).cancelCompletionEvent(lease, null, "Yardi move out rollback!");

        Persistence.service().retrieve(lease);
        return lease;
    }
}