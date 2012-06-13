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
        // TODO - this is used to set the due date for immediate charges, which is the start date of
        // next bill, in case it's coming in time. If not, all kind of aspects must be accounted...
        // For now we just return the next billing period start date
        Calendar dueDate = new GregorianCalendar();
        int dayOfMonth = billingAccount.billingType().billingCycleStartDay().getValue();
        dueDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        if (dueDate.before(SysDateManager.getSysDate())) {
            dueDate.add(Calendar.MONTH, 1);
        }
        return new LogicalDate(dueDate.getTime());
    }

}
