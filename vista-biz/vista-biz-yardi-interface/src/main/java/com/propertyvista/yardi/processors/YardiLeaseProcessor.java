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
 */
package com.propertyvista.yardi.processors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang.StringUtils;
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
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.utils.EntityGraph;

import com.propertyvista.biz.financial.ar.yardi.YardiARIntegrationAgent;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.biz.system.yardi.YardiServiceException;
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
import com.propertyvista.domain.tenant.lease.Lease.Status;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.portal.rpc.shared.PolicyNotFoundException;
import com.propertyvista.yardi.YardiTrace;
import com.propertyvista.yardi.mergers.LeaseMerger;
import com.propertyvista.yardi.mergers.TenantMerger;
import com.propertyvista.yardi.services.ARCodeAdapter;
import com.propertyvista.yardi.services.YardiResidentTransactionsData;
import com.propertyvista.yardi.services.YardiResidentTransactionsData.LeaseTransactionData;

public class YardiLeaseProcessor {

    private final static boolean useDatesToCalculateLeaseStatus = false;

    private final static Logger log = LoggerFactory.getLogger(YardiLeaseProcessor.class);

    private final YardiResidentTransactionsData rtd;

    public YardiLeaseProcessor(YardiResidentTransactionsData rtd) {
        assert (rtd != null);
        this.rtd = rtd;
    }

    // Public interface:

    public void process() throws YardiServiceException {
        Map<String, Lease> nonProcessedLeases = new CaseInsensitiveMap<>();
        if (rtd.isCloseNonProcessedLeases()) {
            for (Lease lease : retrieveActiveLeases(rtd.getYardiInterfaceId())) {
                nonProcessedLeases.put(lease.leaseId().getValue(), lease);
            }
        }

        for (final String propertyCode : rtd.getKeySet()) {
            for (final String leaseId : rtd.getData(propertyCode).getKeySet()) {
                if (rtd.isCloseNonProcessedLeases()) {
                    nonProcessedLeases.remove(leaseId);
                }

                try {
                    new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, YardiServiceException>() {
                        @Override
                        public Void execute() throws YardiServiceException {
                            processLease(propertyCode, leaseId, rtd.getData(propertyCode).getData(leaseId));
                            return null;
                        }
                    });
                } catch (Throwable e) {
                    log.error("Lease {} Processing error {}", leaseId, e);
                    rtd.getExecutionMonitor().addErredEvent("Lease", SimpleMessageFormat.format("lease {0}", leaseId), e);
                }

                if (rtd.getExecutionMonitor().isTerminationRequested()) {
                    break;
                }
            }

            if (rtd.getExecutionMonitor().isTerminationRequested()) {
                break;
            }
        }

        if (!rtd.getExecutionMonitor().isTerminationRequested()) {
            if (rtd.isCloseNonProcessedLeases()) {
                closeLeases(nonProcessedLeases.values());
            }
        }
    }

    public Lease processLease(String propertyCode, String leaseId, LeaseTransactionData ltd) throws YardiServiceException {
        Lease existingLease = retriveLease(rtd.getYardiInterfaceId(), leaseId);
        if (existingLease != null) {
            return updateLease(propertyCode, leaseId, ltd, existingLease);
        } else {
            return createLease(propertyCode, leaseId, ltd);
        }
    }

    private Lease createLease(String propertyCode, String leaseId, LeaseTransactionData ltd) throws YardiServiceException {
        Validate.notNull(ltd.getResident(), "createLease: Resident data for lease {0} is null!", leaseId);
        Validate.isFalse(StringUtils.isEmpty(ltd.getResident().getCustomerID()), "createLease: Resident data for lease {0} has null/empty CustomerID!", leaseId);

        String unitNumber = YardiARIntegrationAgent.getUnitId(ltd.getResident());
        Validate.isTrue(CommonsStringUtils.isStringSet(unitNumber), "Unit number required");
        Validate.isTrue(CommonsStringUtils.isStringSet(propertyCode), "Property Code required");

        AptUnit unit = retrieveUnit(rtd.getYardiInterfaceId(), propertyCode, unitNumber);
        log.debug("Creating lease {} for unit {}", leaseId, unit.getStringView());

        Lease lease = ServerSideFactory.create(LeaseFacade.class).create(Lease.Status.ExistingLease);
        lease.leaseId().setValue(getLeaseID(ltd.getResident()));
        lease.type().setValue(ARCode.Type.Residential);
        lease.integrationSystemId().setValue(rtd.getYardiInterfaceId());

        // unit:
        if (unit.getPrimaryKey() != null) {
            ServerSideFactory.create(LeaseFacade.class).setPackage(lease.currentTerm(), unit, null, Collections.<BillableItem> emptyList());
        }

        List<YardiCustomer> yardiCustomers = ltd.getResident().getCustomers().getCustomer();
        YardiLease yardiLease = yardiCustomers.get(0).getLease();

        //  dates:
        LogicalDate date = guessFromDate(yardiLease);
        if (date == null) {
            date = SystemDateManager.getLogicalDate();
            log.warn("Unable to guess 'Lease From' date - substitute with current one!");
        }
        lease.currentTerm().termFrom().setValue(date);

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
        lease.billingAccount().paymentAccepted().setValue(BillingAccount.PaymentAccepted.getPaymentType(ltd.getResident().getPaymentAccepted()));

        lease.currentTerm().yardiLeasePk().setValue(getYardiLeasePk(yardiCustomers));
        LeaseTerm previousTerm = retrieveLeaseTermByYardiLeasePk(rtd.getYardiInterfaceId(), getYardiLeasePk(yardiCustomers));

        // tenants:
        new TenantMerger(rtd.getExecutionMonitor()).createTenants(yardiCustomers, lease, previousTerm);

        updateLeaseProducts(lease, ltd);

        lease = ServerSideFactory.create(LeaseFacade.class).persist(lease);
        ServerSideFactory.create(LeaseFacade.class).approve(lease, null, null);
        lease = ServerSideFactory.create(LeaseFacade.class).load(lease, false);

        rtd.getExecutionMonitor().addInfoEvent("Lease",
                SimpleMessageFormat.format("lease {0} created for unit {1}", lease.leaseId().getValue(), lease.unit().getStringView()));

        new YardiLeaseFinancialProcessor(rtd.getExecutionMonitor()).processLease(leaseId, ltd.getResident(), rtd.getYardiInterfaceId());

        return manageLeaseState(lease, ltd.getResident(), yardiLease);
    }

    private Lease updateLease(String propertyCode, String leaseId, LeaseTransactionData ltd, Lease existingLease) throws YardiServiceException {
        Lease lease = ServerSideFactory.create(LeaseFacade.class).load(existingLease, true);
        if (lease.status().getValue().isDraft()) {
            log.warn("lease {} in propertyCode {} is in Draft state - can not update!", leaseId, propertyCode);
            rtd.getExecutionMonitor().addFailedEvent("Lease",
                    SimpleMessageFormat.format("lease {0} - is in Draft state - can not update!", lease.leaseId().getStringView()));
            return lease;
        }

        Persistence.ensureRetrieve(lease.currentTerm().version().tenants(), AttachLevel.Attached);
        log.debug("Updating lease {} in propertyCode {}", leaseId, propertyCode);

        if (ltd.getResident() == null) {
            // there are no resident data - update just products:
            if (updateLeaseProducts(lease, ltd)) {
                lease = ServerSideFactory.create(LeaseFacade.class).finalize(lease);
                rtd.getExecutionMonitor().addInfoEvent("Lease",
                        SimpleMessageFormat.format("lease {0} - products only updated (new version)", lease.leaseId().getStringView()));
            }
            return lease;
        }

        List<YardiCustomer> yardiCustomers = ltd.getResident().getCustomers().getCustomer();
        YardiLease yardiLease = yardiCustomers.get(0).getLease();

        LeaseTerm previousTerm = null;

        boolean toPersist = false;
        boolean toFinalize = false;
        boolean yardiUnitTransfer = false;

        if (lease.currentTerm().yardiLeasePk().isNull()) {
            lease.currentTerm().yardiLeasePk().setValue(getYardiLeasePk(yardiCustomers));
            Persistence.service().merge(lease.currentTerm()); // just memorize yardiLeasePk for older leases (before feature was introduced)
        } else if (!EqualsHelper.equals(lease.currentTerm().yardiLeasePk().getValue(), getYardiLeasePk(yardiCustomers))) {
            // it seems we are moving by Yardi Unit Transfer method!..
            previousTerm = lease.currentTerm().duplicate();
            yardiUnitTransfer = true;
            log.info("- Lease {} Moving...", lease.leaseId().getStringView());
        }

        // if unit update is occurred:
        String unitNumber = YardiARIntegrationAgent.getUnitId(ltd.getResident());
        Validate.isTrue(CommonsStringUtils.isStringSet(unitNumber), "Unit number required");
        if (yardiUnitTransfer || !unitNumber.equals(lease.unit().info().number().getValue())) {
            Validate.isTrue(CommonsStringUtils.isStringSet(propertyCode), "Property Code required");
            AptUnit unit = retrieveUnit(rtd.getYardiInterfaceId(), propertyCode, unitNumber);
            rtd.getExecutionMonitor().addInfoEvent(
                    "Lease",
                    SimpleMessageFormat.format("lease {0}: updating unit {1} to unit {2}", lease.leaseId().getStringView(), lease.unit().getStringView(),
                            unit.getStringView()));

            lease = new LeaseMerger().updateUnit(unit, lease, yardiUnitTransfer);
            lease.currentTerm().yardiLeasePk().setValue(getYardiLeasePk(yardiCustomers));
            toFinalize = true;
            log.debug("- Lease Unit Changed...");
        }

        if (LeaseMerger.isLeaseDatesChanged(yardiLease, lease)) {
            lease = new LeaseMerger().mergeLeaseDates(yardiLease, lease);
            toPersist = true;
            log.debug("- Lease Dates Changed...");
        }

        if (LeaseMerger.isTermDatesChanged(yardiLease, lease.currentTerm())) {
            lease.currentTerm().set(new LeaseMerger().mergeTermDates(yardiLease, lease.currentTerm()));
            toPersist = true;
            log.debug("- Term Dates Changed...");
        }

        if (new LeaseMerger().isPaymentTypeChanged(ltd.getResident(), lease)) {
            new LeaseMerger().mergePaymentType(ltd.getResident(), lease);
            toPersist = true;
            log.debug("- Payment Type Changed...");
        }

        Persistence.ensureRetrieve(lease.currentTerm().version().tenants(), AttachLevel.Attached);
        Persistence.ensureRetrieve(lease.currentTerm().version().guarantors(), AttachLevel.Attached);
        if (new TenantMerger(rtd.getExecutionMonitor()).isChanged(yardiCustomers, lease)) {
            new TenantMerger(rtd.getExecutionMonitor()).updateTenants(yardiCustomers, lease, previousTerm);
            if (yardiUnitTransfer) {
                // TODO: hack to update newly created tenants with old customers:
                ServerSideFactory.create(LeaseFacade.class).persist(lease.currentTerm());
                new TenantMerger(rtd.getExecutionMonitor()).updateTenants(yardiCustomers, lease, previousTerm);
            }
            toFinalize = true;
            log.debug("- Tenants Changed...");
        }

        if (new TenantMerger(rtd.getExecutionMonitor()).updateTenantsData(yardiCustomers, lease.currentTerm())) {
            toFinalize = true;
            log.debug("- Tenant Data Changed...");
        }

        toFinalize |= updateLeaseProducts(lease, ltd);

        // persisting logic:
        if (toFinalize) {
            lease = ServerSideFactory.create(LeaseFacade.class).finalize(lease);
            rtd.getExecutionMonitor().addInfoEvent("Lease", SimpleMessageFormat.format("lease {0} updated (new version)", lease.leaseId().getStringView()));
        } else if (toPersist) {
            lease = ServerSideFactory.create(LeaseFacade.class).persist(lease);
            rtd.getExecutionMonitor().addInfoEvent("Lease",
                    SimpleMessageFormat.format("lease {0} updated (no version changes)", lease.leaseId().getStringView()));
        } else {
            log.debug("= No lease changes detected");
        }

        new YardiLeaseFinancialProcessor(rtd.getExecutionMonitor()).processLease(leaseId, ltd.getResident(), rtd.getYardiInterfaceId());

        return manageLeaseState(lease, ltd.getResident(), yardiLease);
    }

    private boolean updateLeaseProducts(Lease lease, LeaseTransactionData ltd) {
        if (ltd.getCharges() == null) {
            // lease has no charges - clear products:
            return expireLeaseProducts(lease);
        }

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
                ignoredCodes.add(policyItem.yardiChargeCode().getValue());
            }
        } catch (PolicyNotFoundException e) {
            // ignore
        }

        List<BillableItem> currentItems = new ArrayList<>();
        currentItems.add(lease.currentTerm().version().leaseProducts().serviceItem());
        currentItems.addAll(lease.currentTerm().version().leaseProducts().featureItems());

        List<BillableItem> newItems = new ArrayList<BillableItem>();
        for (Transactions tr : ltd.getCharges().getRTServiceTransactions().getTransactions()) {
            if (tr == null || tr.getCharge() == null) {
                continue;
            }

            String chargeCode = tr.getCharge().getDetail().getChargeCode();

            // consult ignored codes
            if (ignoredCodes.contains(chargeCode)) {
                continue;
            }

            // update charge code item counter
            Integer chargeCodeItemNo = chargeCodeItemsCount.get(chargeCode);
            chargeCodeItemNo = chargeCodeItemNo == null ? 1 : chargeCodeItemNo + 1;
            chargeCodeItemsCount.put(chargeCode, chargeCodeItemNo);

            // create new item from an empty stub or from a copy if we find item with the same uid
            String uid = billableItemUid(tr.getCharge().getDetail().getChargeCode(), chargeCodeItemNo);
            BillableItem newItem = null;
            for (BillableItem leaseItem : currentItems) {
                if (!leaseItem.uuid().isNull() && uid.compareTo(leaseItem.uuid().getValue()) == 0) {
                    newItem = EntityGraph.businessDuplicate(leaseItem);
                }
            }
            if (newItem == null) {
                newItem = EntityFactory.create(BillableItem.class);
                newItem.uuid().setValue(uid);
            }

            if (YardiTrace.trace) {
                log.debug("add lease charge {} {}", tr.getCharge().getDetail().getChargeCode(), tr.getCharge().getDetail().getAmount());
            }

            newItems.add(fillBillableItem(tr.getCharge().getDetail(), newItem));
        }

        return new LeaseMerger().mergeBillableItems(newItems, currentItems, lease, rtd.getExecutionMonitor());
    }

    private boolean expireLeaseProducts(Lease lease) {
        // Do not remove lease charges from submitted lease applications
        if (lease.status().getValue() == Lease.Status.Approved) {
            return false;
        }
        if (BigDecimal.ZERO.compareTo(lease.currentTerm().version().leaseProducts().serviceItem().agreedPrice().getValue(BigDecimal.ZERO)) < 0) {
            lease.currentTerm().version().leaseProducts().serviceItem().agreedPrice().setValue(BigDecimal.ZERO);
            lease.currentTerm().version().leaseProducts().featureItems().clear();

            rtd.getExecutionMonitor().addInfoEvent("Lease", SimpleMessageFormat.format("lease {0} - expire products", lease.leaseId().getValue()));

            return true;
        }
        return false;
    }

    //
    // Some public utils:
    //

    public static String getLeaseID(String leaseId) {
        return leaseId.toLowerCase();
    }

    public static String getLeaseID(RTCustomer rtCustomer) {
        return getLeaseID(rtCustomer.getCustomerID());
    }

    private static Lease retriveLease(Key yardiInterfaceId, String leaseId) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);

        criteria.eq(criteria.proto().leaseId(), getLeaseID(leaseId));
        criteria.eq(criteria.proto().integrationSystemId(), yardiInterfaceId);

        return Persistence.service().retrieve(criteria);
    }

    private LeaseTerm retrieveLeaseTermByYardiLeasePk(Key yardiInterfaceId, String yardiLeasePk) {
        EntityQueryCriteria<LeaseTerm> criteria = EntityQueryCriteria.create(LeaseTerm.class);

        criteria.eq(criteria.proto().lease().integrationSystemId(), yardiInterfaceId);
        criteria.eq(criteria.proto().yardiLeasePk(), yardiLeasePk);

        return Persistence.service().retrieve(criteria);
    }

    private AptUnit retrieveUnit(Key yardiInterfaceId, String propertyCode, String unitNumber) {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);

        criteria.eq(criteria.proto().building().integrationSystemId(), yardiInterfaceId);
        criteria.eq(criteria.proto().building().propertyCode(), propertyCode);
        criteria.eq(criteria.proto().info().number(), unitNumber);

        AptUnit unit = Persistence.service().retrieve(criteria);
        if (unit == null) {
            throw new Error("Unit '" + unitNumber + "' not found in building '" + propertyCode + "'");
        }
        return unit;
    }

    public static boolean isEligibleForProcessing(RTCustomer rtCustomer) {
        Customerinfo info = rtCustomer.getCustomers().getCustomer().get(0).getType();
        // @formatter:off
        // list eligible for processing types here:
        return info.equals(Customerinfo.CURRENT_RESIDENT) ||
               info.equals(Customerinfo.FORMER_RESIDENT)  ||
               info.equals(Customerinfo.FUTURE_RESIDENT);
        // @formatter:on
    }

    public static boolean isCurrentLease(RTCustomer rtCustomer, YardiLease yardiLease) {
        Customerinfo info = rtCustomer.getCustomers().getCustomer().get(0).getType();
        if (useDatesToCalculateLeaseStatus) {
            return (Customerinfo.CURRENT_RESIDENT.equals(info) && !getLogicalDateNotNull(yardiLease.getLeaseToDate())
                    .before(SystemDateManager.getLogicalDate()));
        } else {
            return Customerinfo.CURRENT_RESIDENT.equals(info);
        }
    }

    public static boolean isFormerLease(RTCustomer rtCustomer, YardiLease yardiLease) {
        Customerinfo info = rtCustomer.getCustomers().getCustomer().get(0).getType();
        if (useDatesToCalculateLeaseStatus) {
            return (Customerinfo.FORMER_RESIDENT.equals(info) || getLogicalDateNotNull(yardiLease.getLeaseToDate()).before(SystemDateManager.getLogicalDate()));
        } else {
            return Customerinfo.FORMER_RESIDENT.equals(info);
        }
    }

    public static boolean isFutureLease(RTCustomer rtCustomer, YardiLease yardiLease) {
        Customerinfo info = rtCustomer.getCustomers().getCustomer().get(0).getType();
        if (useDatesToCalculateLeaseStatus) {
            return (Customerinfo.FUTURE_RESIDENT.equals(info) && guessFromDate(yardiLease).after(SystemDateManager.getLogicalDate()));
        } else {
            return Customerinfo.FUTURE_RESIDENT.equals(info);
        }
    }

    public static boolean isOnNotice(RTCustomer rtCustomer, YardiLease yardiLease) {
        Unitleasestatusinfo info = rtCustomer.getRTUnit().getUnit().getInformation().get(0).getUnitLeasedStatus();
        return (Unitleasestatusinfo.ON_NOTICE.equals(info) || Unitleasestatusinfo.LEASED_ON_NOTICE.equals(info));
    }

    /**
     * We badly depends on this termFrom/leaseFrom date - so try to deduct as much as possible in the cases where it absent in Yardi!
     */
    public static LogicalDate guessFromDate(YardiLease yardiLease) {
        LogicalDate date = null;

        if (yardiLease.getLeaseFromDate() != null) {
            date = getLogicalDate(yardiLease.getLeaseFromDate());
        } else if (yardiLease.getActualMoveIn() != null) {
            date = getLogicalDate(yardiLease.getActualMoveIn());
        } else if (yardiLease.getExpectedMoveInDate() != null) {
            date = getLogicalDate(yardiLease.getExpectedMoveInDate());
        } else if (yardiLease.getLeaseSignDate() != null) {
            date = getLogicalDate(yardiLease.getLeaseSignDate());
        } else {
            log.warn("Empty Yardi 'Lease From' date!?");
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
                YardiLease l1 = c1.getCustomers().getCustomer().get(0).getLease();
                YardiLease l2 = c2.getCustomers().getCustomer().get(0).getLease();

                int s1 = (isFormerLease(c1, l1) ? -1 : (isFutureLease(c1, l1) ? 1 : 0));
                int s2 = (isFormerLease(c2, l2) ? -1 : (isFutureLease(c2, l2) ? 1 : 0));

                return (s1 < s2 ? -1 : (s1 > s2 ? 1 : 0));
            }
        });

        return rtCustomers;
    }

    public static void setLeaseChargesComaptibleIds(Lease lease) {
        List<BillableItem> allBillableItems = new ArrayList<>();

        if (!lease.currentTerm().version().leaseProducts().serviceItem().item().isEmpty()) {
            allBillableItems.add(lease.currentTerm().version().leaseProducts().serviceItem());
            for (BillableItem bi : lease.currentTerm().version().leaseProducts().featureItems()) {
                allBillableItems.add(bi);
            }
        }

        Map<String, Integer> chargeCodeItemsCount = new HashMap<>();

        for (BillableItem bi : allBillableItems) {
            Validate.isTrue(!bi.item().product().holder().code().yardiChargeCodes().isEmpty(), "yardiChargeCodes are not mapped to product {0}", bi.item()
                    .product().holder());

            String chargeCode = bi.item().product().holder().code().yardiChargeCodes().get(0).yardiChargeCode().getValue();
            Integer chargeCodeItemNo = chargeCodeItemsCount.get(chargeCode);
            if (chargeCodeItemNo == null) {
                chargeCodeItemNo = 1;
            } else {
                chargeCodeItemNo = chargeCodeItemNo + 1;
            }
            chargeCodeItemsCount.put(chargeCode, chargeCodeItemNo);

            bi.uuid().setValue(billableItemUid(chargeCode, chargeCodeItemNo));
        }
    }

    // @see function in migration PadProcessorInformation.billableItemId  that use the same value
    private static String billableItemUid(String chargeCode, int chargeCodeItemNo) {
        return chargeCode + ":" + chargeCodeItemNo;
    }

    //
    // Internals:
    //

    /**
     * The <MITS:Description> node is the primary key of the person record. The <CustomerID> node is the tenant code related to the person. When a Unit Transfer
     * happens, a new person record (MITS:Description node) will be created with the new property info, unit info, etc., but it will still point to that same
     * tenant code
     */
    private static String getYardiLeasePk(List<YardiCustomer> yardiCustomers) {
        return yardiCustomers.get(0).getDescription();
    }

    private static LogicalDate getLogicalDate(Date date) {
        return (date == null ? null : new LogicalDate(date));
    }

    private static LogicalDate getLogicalDateNotNull(Date date) {
        return (date == null ? new LogicalDate() : new LogicalDate(date));
    }

    private static String getLeaseChargeDescription(ChargeDetail detail) {
        ARCode arCode = new ARCodeAdapter().retrieveARCode(ActionType.Debit, detail.getChargeCode());
        return arCode == null ? detail.getDescription() : arCode.name().getValue();
    }

    private BillableItem fillBillableItem(ChargeDetail detail, BillableItem newItem) {

        newItem.agreedPrice().setValue(new BigDecimal(detail.getAmount()));
        newItem.updated().setValue(SystemDateManager.getLogicalDate());
        newItem.effectiveDate().setValue(getLogicalDate(detail.getServiceFromDate()));
        newItem.expirationDate().setValue(getLogicalDate(detail.getServiceToDate()));
        newItem.description().setValue(getLeaseChargeDescription(detail));
        newItem.yardiChargeCode().setValue(detail.getChargeCode());

        return newItem;
    }

    //
    // Lease state management:
    //
    private Lease manageLeaseState(Lease lease, RTCustomer rtCustomer, YardiLease yardiLease) {
        if (lease.status().getValue().isActive()) {

            if (isCurrentLease(rtCustomer, yardiLease)) {
                // approved -> active transition:
                if (lease.status().getValue() == Status.Approved) {
                    activateLease(lease);
                }

                // notice On/Off mechanics:
                if (isOnNotice(rtCustomer, yardiLease)) {
                    if (lease.completion().getValue() != CompletionType.Notice) {
                        lease = markLeaseOnNotice(lease, yardiLease);
                    }
                } else if (lease.completion().getValue() == CompletionType.Notice) {
                    lease = cancelMarkLeaseOnNotice(lease, yardiLease);
                }

            } else if (isFormerLease(rtCustomer, yardiLease)) {
                // active/approved -> past transition:
                if (lease.status().getValue() == Status.Approved) {
                    activateLease(lease);
                }
                lease = completeLease(lease, yardiLease);
            }

        } else { // past -> active transition (cancel Move Out in Yardi!):
            if (isCurrentLease(rtCustomer, yardiLease) || isFutureLease(rtCustomer, yardiLease)) {
                lease = cancelLeaseCompletion(lease, yardiLease);

                if (isFutureLease(rtCustomer, yardiLease)) {
                    lease.status().setValue(Status.Approved);
                    Persistence.service().merge(lease);
                }
            }
        }

        return lease;
    }

    private static Lease activateLease(Lease lease) {
        ServerSideFactory.create(LeaseFacade.class).activate(lease);
        log.debug("        Activate Lease {}", lease.leaseId().getValue());
        Persistence.service().retrieve(lease);
        return lease;
    }

    private static Lease markLeaseOnNotice(Lease lease, YardiLease yardiLease) {
        ServerSideFactory.create(LeaseFacade.class).createCompletionEvent(lease, CompletionType.Notice, SystemDateManager.getLogicalDate(),
                getLogicalDate(yardiLease.getExpectedMoveOutDate()), null);
        log.debug("        Set NOTICE, Lease {}", lease.leaseId().getValue());
        Persistence.service().retrieve(lease);
        return lease;
    }

    private static Lease cancelMarkLeaseOnNotice(Lease lease, YardiLease yardiLease) {
        ServerSideFactory.create(LeaseFacade.class).cancelCompletionEvent(lease, null, "Yardi notice rollback!");
        log.debug("        Cancel NOTICE, Lease {}", lease.leaseId().getValue());
        Persistence.service().retrieve(lease);
        return lease;
    }

    private static void completeLease(Lease lease) {
        YardiLease yardiLease = new YardiLease();
        yardiLease.setExpectedMoveOutDate(SystemDateManager.getDate());
        yardiLease.setActualMoveOut(SystemDateManager.getDate());

        completeLease(lease, yardiLease);
    }

    private static Lease completeLease(Lease lease, YardiLease yardiLease) {
        // check the building for suspension first:
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.Attached);
        if (lease.unit().building().suspended().getValue(false)) {
            log.debug("        Lease {} Completion ignored because of suspended building {}", lease.leaseId().getValue(), lease.unit().building()
                    .propertyCode().getValue());
            return lease;
        }

        // actual completion mechanics:
        Persistence.ensureRetrieve(lease, AttachLevel.Attached);
        if (lease.status().getValue() == Status.Approved) {
            ServerSideFactory.create(LeaseFacade.class).cancelLease(lease, null, "Yardi import lease cancellation.");
        } else {
            if (lease.completion().isNull()) {
                ServerSideFactory.create(LeaseFacade.class).createCompletionEvent(lease, CompletionType.Termination, SystemDateManager.getLogicalDate(),
                        getLogicalDate(yardiLease.getExpectedMoveOutDate()), getLogicalDate(yardiLease.getActualMoveOut()));
            }

            ServerSideFactory.create(LeaseFacade.class).moveOut(lease, getLogicalDate(yardiLease.getActualMoveOut()));
        }

        log.debug("        Complete Lease {}", lease.leaseId().getValue());
        Persistence.service().retrieve(lease);
        return lease;
    }

    private static Lease cancelLeaseCompletion(Lease lease, YardiLease yardiLease) {
        ServerSideFactory.create(LeaseFacade.class).cancelCompletionEvent(lease, null, "Yardi move out rollback!");
        log.debug("        Cancel Lease {} Completion", lease.leaseId().getValue());
        Persistence.service().retrieve(lease);
        return lease;
    }

    private static List<Lease> retrieveActiveLeases(Key yardiInterfaceId) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);

        criteria.eq(criteria.proto().unit().building().integrationSystemId(), yardiInterfaceId);
        criteria.in(criteria.proto().unit().building().suspended(), Boolean.FALSE);
        criteria.in(criteria.proto().status(), Lease.Status.active());

        return Persistence.service().query(criteria);
    }

    private void closeLeases(Collection<Lease> leases) {
        for (final Lease lease : leases) {
            new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {
                @Override
                public Void execute() {
                    completeLease(lease);
                    return null;
                }
            });
        }
    }
}