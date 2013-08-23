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
package com.propertyvista.biz.tenant.internal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.gwt.server.DateUtils;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.billing.BillingFacade;
import com.propertyvista.biz.tenant.LeaseAbstractManager;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.BillingAccount.BillingPeriod;
import com.propertyvista.domain.financial.billing.Bill;
import com.propertyvista.domain.financial.billing.Bill.BillType;
import com.propertyvista.domain.tenant.lease.Lease;

public class LeaseInternalManager extends LeaseAbstractManager {

    private static final I18n i18n = I18n.get(LeaseInternalManager.class);

    @Override
    protected BillingAccount createBillingAccount() {
        BillingAccount billingAccount = EntityFactory.create(BillingAccount.class);
        billingAccount.billingPeriod().setValue(BillingPeriod.Monthly);
        billingAccount.billCounter().setValue(0);
        return billingAccount;
    }

    @Override
    protected void onLeaseApprovalError(Lease lease, String error) {
        throw new UserRuntimeException(i18n.tr("This lease cannot be approved due to following validation errors:\n{0}", error));
    }

    @Override
    protected void onLeaseApprovalSuccess(Lease lease, Lease.Status leaseStatus) {
        Bill bill = ServerSideFactory.create(BillingFacade.class).runBilling(lease);

        if (bill.billStatus().getValue() == Bill.BillStatus.Failed) {
            throw new UserRuntimeException(i18n.tr("This lease cannot be approved due to failed first time bill"));
        }

        if (bill.billStatus().getValue() != Bill.BillStatus.Confirmed) {
            ServerSideFactory.create(BillingFacade.class).confirmBill(bill);
        }

        if (Lease.Status.ExistingLease.equals(leaseStatus)) {
            LogicalDate curDate = new LogicalDate(SystemDateManager.getDate());
            LogicalDate nextExecDate = ServerSideFactory.create(BillingFacade.class).getNextBillBillingCycle(lease).targetBillExecutionDate().getValue();
            if (BillType.ZeroCycle.equals(bill.billType().getValue()) && !curDate.before(nextExecDate)) {
                ServerSideFactory.create(BillingFacade.class).runBilling(lease);
            }
        }
    }

    @Override
    /**
     * Terminate-complete all other active, but completion marked (Evicted/Skipped/etc.) leases on the unit.
     * 
     * @param lease
     *            - primary lease.
     */
    protected void ensureLeaseUniqness(Lease lease) {
        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), lease.unit()));
        criteria.add(PropertyCriterion.eq(criteria.proto().status(), Lease.Status.Active));
        criteria.add(PropertyCriterion.ne(criteria.proto().id(), lease.getPrimaryKey()));

        for (Lease concurrent : Persistence.service().query(criteria)) {
            if (concurrent.completion().isNull()) {
                throw new IllegalStateException("Lease has no completion mark");
            }
            // set termination date to day before current(new) lease:
            concurrent.terminationLeaseTo().setValue(DateUtils.daysAdd(lease.leaseFrom().getValue(), -1));
            persist(concurrent);
            complete(concurrent);
        }
    }

}
