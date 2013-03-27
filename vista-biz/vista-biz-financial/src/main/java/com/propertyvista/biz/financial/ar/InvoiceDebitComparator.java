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
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;

import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.policy.policies.ARPolicy;
import com.propertyvista.domain.policy.policies.PADPolicy;
import com.propertyvista.domain.policy.policies.PADPolicy.OwingBalanceType;
import com.propertyvista.domain.policy.policies.PADPolicyItem;

public class InvoiceDebitComparator implements Comparator<InvoiceDebit> {

    private final ARPolicy arPolicy;

    private final Map<DebitType, OwingBalanceType> padDebitTypes = new HashMap<DebitType, OwingBalanceType>();

    public InvoiceDebitComparator(ARPolicy arPolicy, PADPolicy padPolicy) {
        this.arPolicy = arPolicy;
        // create product map
        for (PADPolicyItem item : padPolicy.debitBalanceTypes()) {
            padDebitTypes.put(item.debitType().getValue(), item.owingBalanceType().getValue());
        }
    }

    @Override
    public int compare(InvoiceDebit debit1, InvoiceDebit debit2) {
        int padComp = padCompare(debit1, debit2);
        return padComp == 0 ? amtCompare(debit1, debit2) : padComp;
    }

    private int amtCompare(InvoiceDebit debit1, InvoiceDebit debit2) {
        // smaller amount first
        return debit1.amount().getValue().compareTo(debit2.amount().getValue());
    }

    private int padCompare(InvoiceDebit debit1, InvoiceDebit debit2) {
        Boolean isPad1 = isPadDebit(debit1), isPad2 = isPadDebit(debit2);
        if (isPad1 && isPad2) {
            // oldest first
            return debit1.dueDate().getValue().compareTo(debit2.dueDate().getValue());
        } else if (!isPad1 && !isPad2) {
            // per ar policy
            return arCompare(debit1, debit2);
        } else {
            // non-pad first
            return isPad1.compareTo(isPad2);
        }
    }

    private int arCompare(InvoiceDebit debit1, InvoiceDebit debit2) {
        if (arPolicy.creditDebitRule().getValue() == ARPolicy.CreditDebitRule.rentDebtLast) {
            return -debit1.debitType().getValue().compareTo(debit2.debitType().getValue());
        } else if (arPolicy.creditDebitRule().getValue() == ARPolicy.CreditDebitRule.oldestDebtFirst) {
            int ageComparison = compareBucketAge(debit1, debit2);
            if (ageComparison == 0) {
                return -debit1.debitType().getValue().compareTo(debit2.debitType().getValue());
            } else {
                return ageComparison;
            }
        }
        return 0;
    }

    private Boolean isPadDebit(InvoiceDebit debit) {
        return padDebitTypes.containsKey(debit.debitType().getValue());
    }

    public static int compareBucketAge(InvoiceDebit debit1, InvoiceDebit debit2) {
        LogicalDate currentDate = new LogicalDate(SystemDateManager.getDate());
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
