/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 15, 2012
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.billingcycle;

import java.util.List;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.domain.policy.policies.LeaseBillingPolicy;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

public interface BillingCycleFacade {

    BillingType getBillingType(Lease lease);

    BillingCycle getLeaseFirstBillingCycle(Lease lease);

    BillingCycle getBillingCycleForDate(Lease lease, LogicalDate date);

    BillingCycle getBillingCycleForDate(Building buildingId, BillingPeriod billingPeriod, Integer billingCycleStartDay, LogicalDate date);

    BillingCycle getSubsequentBillingCycle(BillingCycle billingCycle);

    BillingCycle getPriorBillingCycle(BillingCycle billingCycle);

    void onLeaseBillingPolicyChange(LeaseBillingPolicy newPolicy);

    void onLeaseBillingPolicyDelete(List<Building> affectedBuildings);
}
