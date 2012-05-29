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
package com.propertyvista.biz.financial.billing;

import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.BillingRun;
import com.propertyvista.domain.tenant.lease.Lease;

public class BillingFacadeImpl implements BillingFacade {

    @Override
    public BillingRun runBilling(Lease lease) {
        return BillingLifecycleManager.runBilling(lease);
    }

    @Override
    public Bill getLatestConfirmedBill(Lease lease) {
        return BillingLifecycleManager.getLatestConfirmedBill(lease);
    }

    @Override
    public Bill getLatestBill(Lease lease) {
        return BillingLifecycleManager.getLatestBill(lease);
    }

    @Override
    public Bill confirmBill(Bill bill) {
        return BillingLifecycleManager.confirmBill(bill);
    }

    @Override
    public Bill rejectBill(Bill bill) {
        return BillingLifecycleManager.rejectBill(bill);
    }

}
