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
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.policy.policies.ARPolicy;
import com.propertyvista.domain.policy.policies.PADPolicy;
import com.propertyvista.domain.policy.policies.PADPolicy.OwingBalanceType;
import com.propertyvista.domain.policy.policies.PADPolicyItem;

/*
 * The debit comparator is used to prioritize debit items for credit coverage according to to following rules:
 * 1. non-PAD debits have priority over PAD-debits
 * 2. for two non-PAD debits use ARPolicy rules (rentDebtLast or oldestDebtFirst)
 * 3. for two PAD-debits use PADPolicy rules (LastBill or ToDateTotal)
 * 4. for two debits of the same priority, the smallest amount wins
 */
public class InvoiceDebitComparator implements Comparator<InvoiceDebit> {

    private final ARPolicy arPolicy;

    private final boolean padOnly;

    private final Map<ARCode, OwingBalanceType> padDebitTypes = new HashMap<ARCode, OwingBalanceType>();

    public InvoiceDebitComparator(ARPolicy arPolicy, PADPolicy padPolicy, boolean padOnly) {
        this.arPolicy = arPolicy;
        this.padOnly = padOnly;
        // create product map
        for (PADPolicyItem item : padPolicy.debitBalanceTypes()) {
            padDebitTypes.put(item.debitType(), item.owingBalanceType().getValue());
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
        OwingBalanceType padType1 = padDebitType(debit1), padType2 = padDebitType(debit2);
        if (padType1 != null && padType2 != null) {
            if (padType1 == OwingBalanceType.ToDateTotal && padType2 == OwingBalanceType.ToDateTotal) {
                return arCompare(debit1, debit2);
            } else if (padOnly) {
                // LastBill goes first
                return new Boolean(padType2 == OwingBalanceType.LastBill).compareTo(padType1 == OwingBalanceType.LastBill);
            } else {
                // LastBill goes last
                return new Boolean(padType1 == OwingBalanceType.LastBill).compareTo(padType2 == OwingBalanceType.LastBill);
            }
        } else if (padType1 == null && padType2 == null) {
            // per ar policy
            return arCompare(debit1, debit2);
        } else {
            // non-pad first
            return new Boolean(padType1 != null).compareTo(padType2 != null);
        }
    }

    private int arCompare(InvoiceDebit debit1, InvoiceDebit debit2) {
        if (arPolicy.creditDebitRule().getValue() == ARPolicy.CreditDebitRule.rentDebtLast) {
            EnumSet<ARCode.Type> service = ARCode.Type.services();
            return new Boolean(service.contains(debit1.arCode().type())).compareTo(service.contains(debit2.arCode().type()));
        } else if (arPolicy.creditDebitRule().getValue() == ARPolicy.CreditDebitRule.oldestDebtFirst) {
            return compareBucketAge(debit1, debit2);
        }
        return 0;
    }

    private OwingBalanceType padDebitType(InvoiceDebit debit) {
        return padDebitTypes.get(debit.arCode());
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
