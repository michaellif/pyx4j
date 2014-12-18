/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 24, 2012
 * @author michaellif
 */
package com.propertyvista.biz.financial.billingcycle;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.entity.shared.UniqueConstraintUserRuntimeException;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.billing.BillDateUtils;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.domain.LeaseBillingTypePolicyItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.rpc.shared.BillingException;
import com.propertyvista.shared.config.VistaFeatures;

class BillingCycleManager {
    private final Logger log = LoggerFactory.getLogger(BillingCycleManager.class);

    private final I18n i18n = I18n.get(BillingCycleManager.class);

    private static final long MILIS_IN_DAY = 1000 * 60 * 60 * 24;

    /**
     * Use 1-Jan-2012 as odd week Sunday ref to calculate odd/even week
     */
    private static final long REF_SUNDAY = new LogicalDate(112, 0, 1).getTime();

    private BillingCycleManager() {
    }

    private static class SingletonHolder {
        public static final BillingCycleManager INSTANCE = new BillingCycleManager();
    }

    static BillingCycleManager instance() {
        return SingletonHolder.INSTANCE;
    }

    /** Find/Create BillingType for a new/draft Lease based on the BillingType policy */
    public BillingType ensureBillingType(Lease lease) {
        Persistence.service().retrieve(lease.unit().building());

        return ensureBillingType(lease.unit().building(), lease.billingAccount().billingPeriod().getValue(), lease.leaseFrom().getValue());
    }

    public BillingCycle getSubsiquentBillingCycle(BillingCycle billingCycle) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(billingCycle.billingCycleEndDate().getValue());
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return ensureBillingCycleForDate(billingCycle.building(), billingCycle.billingType(), new LogicalDate(calendar.getTime()));
    }

    public BillingCycle getPriorBillingCycle(BillingCycle billingCycle) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(billingCycle.billingCycleStartDate().getValue());
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return ensureBillingCycleForDate(billingCycle.building(), billingCycle.billingType(), new LogicalDate(calendar.getTime()));
    }

    public BillingCycle getLeaseFirstBillingCycle(Lease lease) {
        BillingAccount billingAccount = lease.billingAccount();
        LogicalDate leaseStartDate = null;
        if (!billingAccount.carryforwardBalance().isNull()) {
            if (!lease.leaseFrom().getValue().before(lease.creationDate().getValue())) {
                throw new BillingException("Existing lease start date should be earlier than creation date");
            }
            // for not-approved leases use current date
            leaseStartDate = lease.approvalDate().isNull() ? SystemDateManager.getLogicalDate() : new LogicalDate(lease.approvalDate().getValue());
        } else {
            leaseStartDate = lease.leaseFrom().getValue();
        }
        return ensureBillingCycleForDate(lease, leaseStartDate);
    }

    /** Find/Create BillingCycle based on the lease BillingType */
    public BillingCycle ensureBillingCycleForDate(Lease lease, LogicalDate date) {
        Persistence.ensureRetrieve(lease.unit().building(), AttachLevel.IdOnly);
        Persistence.ensureRetrieve(lease.billingAccount().billingType(), AttachLevel.Attached);

        return ensureBillingCycleForDate(lease.unit().building(), lease.billingAccount().billingType(), date);
    }

    /** Find/Create BillingCycle based on LeaseBillingPolicy and the given BillingPeriod */
    public BillingCycle ensureBillingCycleForStartDate(Building building, BillingPeriod billingPeriod, LogicalDate cycleStartDate) {
        BillingType billingType = ensureBillingType(building, billingPeriod, cycleStartDate);
        return ensureBillingCycleForDate(building, billingType, cycleStartDate);
    }

    /** Find/Create BillingCycle based on LeaseBillingPolicy and the given BillingPeriod */
    public BillingCycle ensureBillingCycleForDate(Building building, BillingPeriod billingPeriod, LogicalDate date) {
        LeaseBillingTypePolicyItem policyItem = retreiveLeaseBillingTypePolicyItem(building, billingPeriod);
        Integer startDay = policyItem.billingCycleStartDay().getValue();
        if (startDay == null || startDay < 1) {
            throw new UserRuntimeException("Billing Cycle Start Day is not defined in Lease Billing Policy.");
        }
        BillingType billingType = ensureBillingType(billingPeriod, startDay);
        return ensureBillingCycleForDate(building, billingType, date);
    }

    // -------- internals -----------

    BillingType ensureBillingType(Building building, BillingPeriod billingPeriod, LogicalDate leaseStartDate) {
        LeaseBillingTypePolicyItem policyItem = retreiveLeaseBillingTypePolicyItem(building, billingPeriod);
        Integer startDay = null;
        if (policyItem.billingCycleStartDay().isNull()) { // by lease start date
            startDay = getBillingCycleStartDay(billingPeriod, leaseStartDate);
        } else { // by policy
            startDay = policyItem.billingCycleStartDay().getValue();
        }
        return ensureBillingType(billingPeriod, startDay);
    }

    /** Find/Create required BillingType - utility method */
    BillingType ensureBillingType(final BillingPeriod billingPeriod, final int billingCycleStartDay) {
        // Try to find existing billing type
        BillingType billingType = retrieveBillingType(billingPeriod, billingCycleStartDay);

        if (billingType == null) {
            // not found - create new one; handle possible concurrent creation attempts
            try {
                billingType = new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<BillingType, RuntimeException>() {
                    @Override
                    public BillingType execute() {
                        return createBillingType(billingPeriod, billingCycleStartDay);
                    }
                });
            } catch (UniqueConstraintUserRuntimeException e) {
                billingType = retrieveBillingType(billingPeriod, billingCycleStartDay);
                if (billingType == null) {
                    throw new UserRuntimeException("Failed to ensure BillingType for start day: " + billingCycleStartDay);
                }
                log.info("BillingType SUCCESSFULLY retrieved after failed concurrent creation attempt.");
            }

        }
        return billingType;
    }

    BillingType retrieveBillingType(final BillingPeriod billingPeriod, final int billingCycleStartDay) {
        EntityQueryCriteria<BillingType> criteria = EntityQueryCriteria.create(BillingType.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingPeriod(), billingPeriod));
        criteria.add(PropertyCriterion.eq(criteria.proto().billingCycleStartDay(), billingCycleStartDay));
        return Persistence.service().retrieve(criteria);
    }

    /**
     * 
     * When billing period required to start on lease start date we have one special case:
     * - for 'monthly' or 'semimonthly' PaymentFrequency and if lease date starts on 29, 30, or 31 we correspond this lease to cycle
     * with billingPeriodStartDay = 1 and prorate days of 29/30/31.
     */
    int getBillingCycleStartDay(BillingPeriod billingPeriod, LogicalDate leaseStartDate) {
        int billingCycleStartDay = 0;
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(leaseStartDate);
        switch (billingPeriod) {
        case Monthly:
            billingCycleStartDay = calendar.get(Calendar.DAY_OF_MONTH);
            if (billingCycleStartDay > 28) {
                billingCycleStartDay = 1;
            }
            break;
        case Weekly:
            billingCycleStartDay = calendar.get(Calendar.DAY_OF_WEEK);
            break;
        case BiWeekly:
            billingCycleStartDay = calendar.get(Calendar.DAY_OF_WEEK);
            if ((leaseStartDate.getTime() - REF_SUNDAY) / MILIS_IN_DAY % 14 >= 7) {
                billingCycleStartDay += 7;
            }
            break;
        case SemiMonthly:
            billingCycleStartDay = calendar.get(Calendar.DAY_OF_MONTH);
            if (billingCycleStartDay > 28) {
                billingCycleStartDay = 1;
            } else if (billingCycleStartDay > 14) {
                billingCycleStartDay = billingCycleStartDay - 14;
            }
            break;
        case SemiAnnyally:
        case Annually:
            throw new Error("Not implemented");
        }
        return billingCycleStartDay;
    }

    BillingType createBillingType(final BillingPeriod billingPeriod, Integer billingCycleStartDay) throws BillingException {
        BillingType billingType = EntityFactory.create(BillingType.class);
        billingType.billingPeriod().setValue(billingPeriod);
        billingType.billingCycleStartDay().setValue(billingCycleStartDay);
        Persistence.service().persist(billingType);
        return billingType;
    }

    /** Find/Create BillingCycle of the given type - utility method */
    BillingCycle ensureBillingCycleForDate(final Building building, final BillingType billingType, final LogicalDate date) {
        // Try to find existing billing cycle
        BillingCycle billingCycle = retrieveBillingCycleForDate(building, billingType, date);
        if (billingCycle == null) {
            // not found - create new one; handle possible concurrent creation attempts
            try {
                billingCycle = new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<BillingCycle, RuntimeException>() {
                    @Override
                    public BillingCycle execute() {
                        return createBillingCycle(building, billingType, date);
                    }
                });
            } catch (UniqueConstraintUserRuntimeException e) {
                billingCycle = retrieveBillingCycleForDate(building, billingType, date);
                if (billingCycle == null) {
                    throw new UserRuntimeException("Failed to ensure BillingCycle for date: " + date);
                }
                log.info("BillingCycle SUCCESSFULLY retrieved after failed concurrent creation attempt.");
            }
        }

        return billingCycle;
    }

    BillingCycle retrieveBillingCycleForDate(final Building building, final BillingType billingType, final LogicalDate date) {
        // Try to find existing billing cycle that the given date falls on
        EntityQueryCriteria<BillingCycle> criteria = EntityQueryCriteria.create(BillingCycle.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingType(), billingType));
        criteria.add(PropertyCriterion.le(criteria.proto().billingCycleStartDate(), date));
        criteria.add(PropertyCriterion.ge(criteria.proto().billingCycleEndDate(), date));
        criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
        return Persistence.service().retrieve(criteria);
    }

    void onLeaseBillingPolicyDelete(List<Building> affectedBuildings) {
        for (Building building : affectedBuildings) {
            LeaseBillingPolicy policy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(building, LeaseBillingPolicy.class);
            updateBillingCycles(policy, Arrays.asList(building));
        }
    }

    /**
     * update future BillingCycles with new execution dates
     */
    void onLeaseBillingPolicyChange(LeaseBillingPolicy policy) {
        // Validation
        if (VistaFeatures.instance().yardiIntegration()) {
            for (LeaseBillingTypePolicyItem item : policy.availableBillingTypes()) {
                if (item.billingPeriod().getValue() != BillingPeriod.Monthly) {
                    throw new UserRuntimeException("Only monthly periods supported in yardi");
                }
            }
        }

        // get all affected buildings
        List<Building> buildings = ServerSideFactory.create(PolicyFacade.class).getGovernedNodesOfType(policy, Building.class);
        updateBillingCycles(policy, buildings);
    }

    private void updateBillingCycles(LeaseBillingPolicy policy, List<Building> buildings) {
        if (buildings == null || buildings.size() == 0) {
            return;
        }

        for (LeaseBillingTypePolicyItem newItem : policy.availableBillingTypes()) {

            // iterate over future billing cycles
            EntityQueryCriteria<BillingCycle> criteria = new EntityQueryCriteria<BillingCycle>(BillingCycle.class);
            criteria.in(criteria.proto().building(), buildings);
            criteria.eq(criteria.proto().billingType().billingPeriod(), newItem.billingPeriod());
            criteria.gt(criteria.proto().billingCycleStartDate(), SystemDateManager.getDate());
            for (BillingCycle billingCycle : Persistence.service().query(criteria)) {
                // For each cycle we can update ANY date that is in the future
                boolean updated = false;
                LogicalDate startDate = billingCycle.billingCycleStartDate().getValue();
                Date now = SystemDateManager.getDate();

                // BillExecutionDate
                if (now.before(billingCycle.targetBillExecutionDate().getValue())) {
                    // Ok, we have not passed that date yet - check that new date is also in the future
                    LogicalDate billExecDate = BillDateUtils.calculateBillingCycleDateByOffset(newItem.billExecutionDayOffset().getValue(), startDate);
                    if (now.before(billExecDate)) {
                        updated |= EntityGraph.updateMember(billingCycle.targetBillExecutionDate(), billExecDate);
                    }
                }
                // PadExecutionDate
                if (now.before(billingCycle.targetAutopayExecutionDate().getValue())) {
                    LogicalDate padExecDate = BillDateUtils.calculateBillingCycleDateByOffset(newItem.autopayExecutionDayOffset().getValue(), startDate);
                    if (now.before(padExecDate)) {
                        updated |= EntityGraph.updateMember(billingCycle.targetAutopayExecutionDate(), padExecDate);
                    }
                }

                if (updated) {
                    Persistence.service().persist(billingCycle);
                }
            }
        }
    }

    LogicalDate calculateBillingCycleStartDate(BillingType billingType, LogicalDate date) {
        return calculateBillingCycleStartDate(billingType.billingPeriod().getValue(), billingType.billingCycleStartDay().getValue(), date);
    }

    LogicalDate calculateBillingCycleStartDate(BillingPeriod billingPeriod, int billingCycleStartDay, LogicalDate date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        switch (billingPeriod) {
        case Monthly:
            while (calendar.get(Calendar.DAY_OF_MONTH) != billingCycleStartDay) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
            }
            break;
        case Weekly:
        case BiWeekly:
        case SemiMonthly:
        case SemiAnnyally:
        case Annually:
            throw new Error("Not implemented");
        }
        return new LogicalDate(calendar.getTime());
    }

    LogicalDate calculateBillingCycleEndDate(BillingType billingType, LogicalDate date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        int billingCycleStartDay = billingType.billingCycleStartDay().getValue();

        switch (billingType.billingPeriod().getValue()) {
        case Monthly:
            do {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            } while (calendar.get(Calendar.DAY_OF_MONTH) != billingCycleStartDay);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            break;
        case Weekly:
        case BiWeekly:
        case SemiMonthly:
        case SemiAnnyally:
        case Annually:
            throw new Error("Not implemented");
        }
        return new LogicalDate(calendar.getTime());
    }

    /** Create BillingCycle based on the BillingType period and start day */
    BillingCycle createBillingCycle(final Building building, final BillingType billingType, LogicalDate leaseStartDate) throws BillingException {
        BillingPeriod billingPeriod = billingType.billingPeriod().getValue();
        LogicalDate startDate = billingType.billingCycleStartDay().isNull() ? leaseStartDate : //
                calculateBillingCycleStartDate(billingPeriod, billingType.billingCycleStartDay().getValue(), leaseStartDate);
        return createBillingCycle(building, billingPeriod, startDate);
    }

    /**
     * Create billing Cycle
     */
    BillingCycle createBillingCycle(final Building building, final BillingPeriod billingPeriod, LogicalDate leaseStartDate) throws BillingException {

        BillingType billingType = ensureBillingType(billingPeriod, getBillingCycleStartDay(billingPeriod, leaseStartDate));

        BillingCycle billingCycle = EntityFactory.create(BillingCycle.class);
        billingCycle.billingType().set(billingType);
        billingCycle.building().set(building);
        billingCycle.billingCycleStartDate().setValue(calculateBillingCycleStartDate(billingType, leaseStartDate));
        billingCycle.billingCycleEndDate().setValue(calculateBillingCycleEndDate(billingType, leaseStartDate));

        LeaseBillingTypePolicyItem policy = retreiveLeaseBillingTypePolicyItem(building, billingPeriod);

        billingCycle.targetBillExecutionDate().setValue(
                BillDateUtils.calculateBillingCycleDateByOffset(policy.billExecutionDayOffset().getValue(), billingCycle.billingCycleStartDate().getValue()));
        billingCycle.targetAutopayExecutionDate()
                .setValue(
                        BillDateUtils.calculateBillingCycleDateByOffset(policy.autopayExecutionDayOffset().getValue(), billingCycle.billingCycleStartDate()
                                .getValue()));

        Persistence.service().persist(billingCycle);
        return billingCycle;
    }

    LeaseBillingTypePolicyItem retreiveLeaseBillingTypePolicyItem(Building building, BillingPeriod billingPeriod) {
        // get execution day offset from policy
        LeaseBillingPolicy leaseBillingPolicy = ServerSideFactory.create(PolicyFacade.class).obtainEffectivePolicy(building, LeaseBillingPolicy.class);
        LeaseBillingTypePolicyItem policy = null;
        for (LeaseBillingTypePolicyItem item : leaseBillingPolicy.availableBillingTypes()) {
            // billingPerios is unique, so just return the first item found
            if (item.billingPeriod().getValue().equals(billingPeriod)) {
                policy = item;
                break;
            }
        }
        if (policy == null) {
            throw new BillingException("LeaseBillingTypePolicy not found for payment frequency: " + billingPeriod);
        }
        return policy;
    }
}
