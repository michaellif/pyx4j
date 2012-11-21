/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 19, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.financial.billingext.ExternalBillingFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge;
import com.propertyvista.domain.financial.billing.InvoiceProductCharge.Period;
import com.propertyvista.domain.tenant.lease.Lease;

public class ExternalBillingFacadeImpl implements ExternalBillingFacade {
    @Override
    public boolean reconcileCharge(InvoiceProductCharge charge, final String leaseId) {
        BillingAccount billingAccount = getBillingAccount(leaseId);
        if (billingAccount == null) {
            return false;
        }
        charge.billingAccount().set(billingAccount);
        charge.period().setValue(Period.next);
        charge.claimed().setValue(false);
        charge.dueDate().setValue(ARDateUtils.calculateDueDate(billingAccount));
        charge.postDate().setValue(new LogicalDate(SysDateManager.getSysDate()));

        return true;
    }

    private BillingAccount getBillingAccount(final String leaseId) {
        EntityQueryCriteria<Lease> leaseQry = EntityQueryCriteria.create(Lease.class);
        leaseQry.add(PropertyCriterion.eq(leaseQry.proto().leaseId(), leaseId));
        Lease lease = Persistence.service().retrieve(leaseQry);
        return lease == null ? null : lease.billingAccount();
    }
}
