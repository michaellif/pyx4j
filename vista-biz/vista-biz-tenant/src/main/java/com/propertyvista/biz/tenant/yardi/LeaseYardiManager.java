/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 16, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.tenant.yardi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.tenant.LeaseAbstractManager;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.domain.tenant.lease.Lease.Status;

public class LeaseYardiManager extends LeaseAbstractManager {

    private static final Logger log = LoggerFactory.getLogger(LeaseYardiManager.class);

    @Override
    protected BillingAccount createBillingAccount() {
        BillingAccount billingAccount = EntityFactory.create(BillingAccount.class);
        billingAccount.billingPeriod().setValue(BillingPeriod.Monthly);
        return billingAccount;
    }

    @Override
    protected void onLeaseApprovalError(Lease lease, String error) {
        log.warn("Lease approval validation for lease pk='" + lease.getPrimaryKey() + "', id='" + lease.leaseId().getValue()
                + "' has discovered the following errors: " + error);
    }

    @Override
    protected void onLeaseApprovalSuccess(Lease lease, Status leaseStatus) {
        // N/A - internal impl only
    }

    @Override
    protected void ensureLeaseUniqness(Lease lease) {
        // N/A - internal impl only
    }

}
