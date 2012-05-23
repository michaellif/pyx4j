/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 23, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar;

import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.policy.policies.ARPolicy;

public class InvoiceDebitComparator implements Comparator<InvoiceDebit> {

    private final ARPolicy arPolicy;

    public InvoiceDebitComparator(ARPolicy arPolicy) {
        this.arPolicy = arPolicy;
    }

    @Override
    public int compare(InvoiceDebit debit1, InvoiceDebit debit2) {
        if (arPolicy.creditDebitRule().getValue() == ARPolicy.CreditDebitRule.byDueDate) {
            return debit1.dueDate().getValue().compareTo(debit2.dueDate().getValue());
        } else if (arPolicy.creditDebitRule().getValue() == ARPolicy.CreditDebitRule.byDebitType) {
            return -debit1.debitType().getValue().compareTo(debit2.debitType().getValue());
        } else if (arPolicy.creditDebitRule().getValue() == ARPolicy.CreditDebitRule.byAgingBucketAndDebitType) {
            int ageComparison = compareBucketAge(debit1, debit2);
            if (ageComparison == 0) {
                return -debit1.debitType().getValue().compareTo(debit2.debitType().getValue());
            } else {
                return ageComparison;
            }
        }
        return 0;
    }

    static int compareBucketAge(InvoiceDebit debit1, InvoiceDebit debit2) {
        LogicalDate currentDate = new LogicalDate(SysDateManager.getSysDate());
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, -90);
        LogicalDate date90 = new LogicalDate(calendar.getTime());

        if (debit1.dueDate().getValue().compareTo(currentDate) >= 0 && debit2.dueDate().getValue().compareTo(currentDate) >= 0) {
            return 0;
        } else if (debit1.dueDate().getValue().compareTo(date90) < 0 && debit2.dueDate().getValue().compareTo(date90) < 0) {
            return 0;
        }

        long u = debit1.dueDate().getValue().getTime() - debit2.dueDate().getValue().getTime();
        int uDays = (int) (u / (24 * 1000 * 60 * 60));
        if (new Integer(Math.abs(uDays)) >= 30) {
            return uDays;
        } else {
            long v = currentDate.getTime() - debit1.dueDate().getValue().getTime();
            int vDays = (int) (v / (24 * 1000 * 60 * 60)) % 30;
            if ((uDays + vDays) >= 0 && (uDays + vDays) < 30) {
                return 0;
            } else {
                return uDays;
            }
        }
    }
}
