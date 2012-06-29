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
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.domain.financial.BillingAccount;

public class ARDateUtils {

    private static final I18n i18n = I18n.get(ARDateUtils.class);

    static LogicalDate calculateDueDate(BillingAccount billingAccount) {
        return calculateDueDate(billingAccount, new LogicalDate(SysDateManager.getSysDate()));
    }

    static LogicalDate calculateDueDate(BillingAccount billingAccount, LogicalDate postDate) {
        LogicalDate dueDate = null;

        switch (billingAccount.billingType().paymentFrequency().getValue()) {
        case Monthly:
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(postDate);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            while (dayOfMonth != billingAccount.billingType().billingCycleStartDay().getValue()) {
                calendar.add(Calendar.DATE, 1);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            }
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

}
