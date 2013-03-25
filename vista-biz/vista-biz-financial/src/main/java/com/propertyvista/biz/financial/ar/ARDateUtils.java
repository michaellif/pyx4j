/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 1, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.InternalBillingAccount;
import com.propertyvista.domain.financial.billing.BillingCycle;
import com.propertyvista.domain.tenant.lease.Lease;

public class ARDateUtils {

    private static final I18n i18n = I18n.get(ARDateUtils.class);

    public static LogicalDate calculateDueDate(InternalBillingAccount billingAccount) {
        return calculateDueDate(billingAccount, new LogicalDate(SystemDateManager.getDate()));
    }

    @Deprecated
    public static LogicalDate calculateDueDate(InternalBillingAccount billingAccount, LogicalDate postDate) {
        LogicalDate dueDate = null;

        switch (billingAccount.billingType().billingPeriod().getValue()) {
        case Monthly:
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(postDate);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            while (dayOfMonth != billingAccount.billingType().billingCycleStartDay().getValue()) {
                calendar.add(Calendar.DATE, 1);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            }
            //TODO Use policy to calc duedate (policy should be copied to lease)
            dueDate = new LogicalDate(calendar.getTime());
            break;
        case Weekly:
        case BiWeekly:
        case SemiMonthly:
        case SemiAnnyally:
        case Annually:
            //TODO
            throw new Error("Not implemented");
        }

        return dueDate;
    }

    public static LogicalDate calculateDueDate(BillingAccount account, LogicalDate postDate) {
        Persistence.ensureRetrieve(account.lease(), AttachLevel.Attached);
        Persistence.ensureRetrieve(account.lease().unit(), AttachLevel.Attached);
        Persistence.ensureRetrieve(account.lease().unit().building(), AttachLevel.Attached);

        EntityQueryCriteria<BillingCycle> cycleCrit = EntityQueryCriteria.create(BillingCycle.class);
        cycleCrit.add(PropertyCriterion.eq(cycleCrit.proto().building(), account.lease().unit().building()));
        cycleCrit.add(PropertyCriterion.le(cycleCrit.proto().billingCycleStartDate(), postDate));
        cycleCrit.add(PropertyCriterion.ge(cycleCrit.proto().billingCycleEndDate(), postDate));
        BillingCycle cycle = Persistence.service().retrieve(cycleCrit);

        return getBillingCycleDueDate(account, cycle);
    }

    public static LogicalDate getBillingCycleDueDate(BillingAccount account, BillingCycle cycle) {
        LogicalDate startDate;
        int dueDateOffset;
        Lease lease = Persistence.service().retrieve(Lease.class, account.lease().getPrimaryKey());
        if (lease.leaseTo().isNull() || cycle.billingCycleEndDate().getValue().before(lease.leaseTo().getValue())) {
            // normal cycle
            startDate = cycle.billingCycleStartDate().getValue();
            dueDateOffset = account.paymentDueDayOffset().getValue();
        } else {
            // final cycle
            startDate = lease.leaseTo().getValue();
            dueDateOffset = account.finalDueDayOffset().getValue();
        }
        return getDateByOffset(dueDateOffset, startDate);
    }

    private static LogicalDate getDateByOffset(int offset, LogicalDate fromDate) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(fromDate);
        calendar.add(Calendar.DATE, offset);
        return new LogicalDate(calendar.getTime());
    }

}
