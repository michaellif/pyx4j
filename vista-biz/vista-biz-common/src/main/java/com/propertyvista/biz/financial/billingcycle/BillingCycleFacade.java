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

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.financial.billing.BillingType;
import com.propertyvista.domain.tenant.lease.Lease;

public interface BillingCycleFacade {

    BillingType getBillingType(Lease lease);

    BillingCycle getLeaseFirstBillingCycle(Lease lease);

    BillingCycle getBillingCycleForDate(Lease lease, LogicalDate date);

    BillingCycle getSubsequentBillingCycle(BillingCycle billingCycle);

    BillingCycle getPriorBillingCycle(BillingCycle billingCycle);
}
