/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 17, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.tenant.yardi;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.financial.billingcycle.BillingCycleFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.Lease;

public class YardiLeaseIntegrationAgent {

    public static BillingCycle getBillingCycleForDate(Building building, LogicalDate date) {
        // create dummy lease
        Lease yardiLease = EntityFactory.create(Lease.class);
        yardiLease.billingAccount().set(EntityFactory.create(BillingAccount.class));
        yardiLease.billingAccount().billingPeriod().setValue(BillingPeriod.Monthly);
        yardiLease.billingAccount().billingType().billingPeriod().setValue(BillingPeriod.Monthly);
        yardiLease.billingAccount().billingType().billingCycleStartDay().setValue(1);
        yardiLease.unit().building().set(building);

        return ServerSideFactory.create(BillingCycleFacade.class).getBillingCycleForDate(yardiLease, date);
    }

    public static void updateBillabelItem(Lease lease, BillableItem item) {
//        new LeaseYardiManager().persistBillabelItem(lease, item);
    }

}
