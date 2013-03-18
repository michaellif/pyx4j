/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 16, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.billing.internal;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.billing.BillDateUtils;
import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.financial.billing.BillingUtils;
import com.propertyvista.biz.policy.PolicyFacade;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.policy.policies.domain.LeaseBillingTypePolicyItem;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.LeaseAdjustment;
import com.propertyvista.domain.tenant.lease.LeaseTerm;

public final class BillingInternalFacadeImpl implements BillingFacade {

    private static class SingletonHolder {
        public static final BillingInternalFacadeImpl INSTANCE = new BillingInternalFacadeImpl();
    }

    public static BillingInternalFacadeImpl instance() {
        return SingletonHolder.INSTANCE;
    }

    private BillingInternalFacadeImpl() {

    }

    @Override
    public Bill runBilling(Lease leaseId) {
        return BillingManager.instance().runBilling(leaseId, false);
    }

    @Override
    public Bill runBilling(Lease leaseId, BillingCycle cycle) {
        return BillingManager.instance().runBilling(leaseId, cycle, false);
    }

    @Override
    public Bill runBillingPreview(Lease leaseId) {
        return BillingManager.instance().runBilling(leaseId, true);
    }

    @Override
    public Bill getBill(Lease lease, int billSequenceNumber) {
        return BillingManager.instance().getBill(lease, billSequenceNumber);
    }

    @Override
    public Bill getLatestConfirmedBill(Lease lease) {
        return BillingManager.instance().getLatestConfirmedBill(lease);
    }

    @Override
    public Bill getLatestBill(Lease lease) {
        return BillingManager.instance().getLatestBill(lease);
    }

    @Override
    public boolean isLatestBill(Bill bill) {
        return BillingManager.instance().isLatestBill(bill);
    }

    @Override
    public Bill confirmBill(Bill bill) {
        return BillingManager.instance().confirmBill(bill);
    }

    @Override
    public Bill rejectBill(Bill bill, String reason) {
        return BillingManager.instance().rejectBill(bill, reason);
    }

    @Override
    public BillingCycle getNextBillBillingCycle(Lease lease) {
        return BillingManager.instance().getNextBillBillingCycle(lease);
    }

    @Override
    public void updateLeaseAdjustmentTax(LeaseAdjustment adjustment) {
        BillingManager.instance().updateLeaseAdjustmentTax(adjustment);
    }

    @Override
    public BigDecimal getMaxLeaseTermMonthlyTotal(LeaseTerm leaseTerm) {
        return BillingUtils.getMaxLeaseTermMonthlyTotal(leaseTerm);
    }

    @Override
    public void onLeaseBillingPolicyChange(LeaseBillingPolicy oldPolicy, LeaseBillingPolicy newPolicy) {
        // get all affected buildings
        List<Building> buildings = ServerSideFactory.create(PolicyFacade.class).getGovernedNodesOfType(newPolicy, Building.class);
        if (buildings == null || buildings.size() == 0) {
            return;
        }

        // update future BillingCycles if execution dates have changed
        Map<BillingPeriod, LeaseBillingTypePolicyItem> policyMap = new HashMap<BillingPeriod, LeaseBillingTypePolicyItem>();
        for (LeaseBillingTypePolicyItem item : newPolicy.availableBillingTypes()) {
            policyMap.put(item.billingPeriod().getValue(), item);
        }

        for (LeaseBillingTypePolicyItem oldItem : oldPolicy.availableBillingTypes()) {
            LeaseBillingTypePolicyItem newItem = policyMap.get(oldItem.billingPeriod().getValue());
            if (// @formatter:off
                oldItem.billExecutionDayOffset().getValue() == newItem.billExecutionDayOffset().getValue() &&
                oldItem.padCalculationDayOffset().getValue() == newItem.padCalculationDayOffset().getValue() &&
                oldItem.padExecutionDayOffset().getValue() == newItem.padExecutionDayOffset().getValue()
                // @formatter:on
            ) {
                continue;
            }
            // iterate over future billing cycles
            EntityQueryCriteria<BillingCycle> criteria = new EntityQueryCriteria<BillingCycle>(BillingCycle.class);
            criteria.add(PropertyCriterion.in(criteria.proto().building(), buildings));
            criteria.add(PropertyCriterion.eq(criteria.proto().billingType().billingPeriod(), oldItem.billingPeriod()));
            criteria.add(PropertyCriterion.gt(criteria.proto().targetBillExecutionDate(), SystemDateManager.getDate()));
            for (BillingCycle billingCycle : Persistence.service().query(criteria)) {
                // Only update cycle if all new dates are in the future
                LogicalDate startDate = billingCycle.billingCycleStartDate().getValue();

                LogicalDate billExecDate = BillDateUtils.calculateBillingCycleDateByOffset(newItem.billExecutionDayOffset().getValue(), startDate);
                if (!billExecDate.after(SystemDateManager.getDate())) {
                    continue;
                }

                LogicalDate padCalcDate = BillDateUtils.calculateBillingCycleDateByOffset(newItem.padCalculationDayOffset().getValue(), startDate);
                if (!padCalcDate.after(SystemDateManager.getDate())) {
                    continue;
                }

                LogicalDate padExecDate = BillDateUtils.calculateBillingCycleDateByOffset(newItem.padExecutionDayOffset().getValue(), startDate);
                if (!padExecDate.after(SystemDateManager.getDate())) {
                    continue;
                }

                billingCycle.targetBillExecutionDate().setValue(billExecDate);
                billingCycle.targetPadGenerationDate().setValue(padCalcDate);
                billingCycle.targetPadExecutionDate().setValue(padExecDate);

                Persistence.service().persist(billingCycle);
            }
        }
    }

}
