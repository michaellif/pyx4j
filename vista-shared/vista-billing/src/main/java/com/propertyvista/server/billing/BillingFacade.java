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
package com.propertyvista.server.billing;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingRun;
import com.propertyvista.domain.tenant.lease.Lease;

public class BillingFacade {

    public static BillingRun runBilling(Lease lease) {
        return BillingLifecycle.runBilling(lease);
    }

    public static Bill getLatestBill(BillingAccount billingAccount) {
        return BillingUtils.getLatestBill(billingAccount);
    }

    public static Bill getBill(BillingAccount billingAccount, BillingRun billingRun) {
        return BillingUtils.getBill(billingAccount, billingRun);
    }

    public static void confirmBill(Bill bill) {
        BillingLifecycle.confirmBill(bill);
    }

    public static void rejectBill(Bill bill) {
        BillingLifecycle.rejectBill(bill);
    }

}
