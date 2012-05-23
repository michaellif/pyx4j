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

import java.util.Comparator;

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

    private int compareBucketAge(InvoiceDebit debit1, InvoiceDebit debit2) {
        long diff = debit1.dueDate().getValue().getTime() - debit2.dueDate().getValue().getTime();
        int diffDays = (int) (diff / (24 * 1000 * 60 * 60));
        if (new Integer(Math.abs(diffDays)) >= 30) {
            return diffDays;
        } else {
//TODO 1
        }
        return 0;
    }
}
