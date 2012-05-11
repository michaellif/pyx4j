/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 22, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.ArrearsSnapshot;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;

public class ARArrearsManager {

    private static ArrearsSnapshot createArrearsSnapshot(BillingAccount billingAccount) {
        //TODO Artem
        return null;
    }

    static void updateArrearsHistory(BillingAccount billingAccount) {
        //TODO Artem
        // 1. createArrearsSnapshot for current time
        // 2. retrieve previous ArrearsSnapshot 
        // 3. compare 1 and 2 - if it is a difference persist first and update toDate of second otherwise do nothing         
    }

    static Collection<AgingBuckets> getAgingBuckets(BillingAccount billingAccount) {

        List<InvoiceDebit> debits = ARTransactionManager.getNotCoveredDebitInvoiceLineItems(billingAccount);

        Map<DebitType, AgingBuckets> agingBucketsMap = new HashMap<DebitType, AgingBuckets>();

        LogicalDate currentDate = new LogicalDate(SysDateManager.getSysDate());

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, -30);
        LogicalDate date30 = new LogicalDate(calendar.getTime());
        calendar.add(Calendar.DATE, -30);
        LogicalDate date60 = new LogicalDate(calendar.getTime());
        calendar.add(Calendar.DATE, -30);
        LogicalDate date90 = new LogicalDate(calendar.getTime());

        for (InvoiceDebit debit : debits) {
            if (!agingBucketsMap.containsKey(debit.debitType().getValue())) {
                agingBucketsMap.put(debit.debitType().getValue(), createAgingBuckets(debit.debitType().getValue()));
            }
            AgingBuckets agingBuckets = agingBucketsMap.get(debit.debitType().getValue());

            if (debit.postDate().getValue().compareTo(date30) > 0) {
                agingBuckets.bucketCurrent().setValue(agingBuckets.bucketCurrent().getValue().add(debit.outstandingDebit().getValue()));
            } else if (debit.postDate().getValue().compareTo(date30) > 0 && debit.postDate().getValue().compareTo(date60) <= 0) {
                agingBuckets.bucket30().setValue(agingBuckets.bucket30().getValue().add(debit.outstandingDebit().getValue()));
            } else if (debit.postDate().getValue().compareTo(date60) > 0 && debit.postDate().getValue().compareTo(date90) <= 0) {
                agingBuckets.bucket60().setValue(agingBuckets.bucket60().getValue().add(debit.outstandingDebit().getValue()));
            } else if (debit.postDate().getValue().compareTo(date90) > 0) {
                agingBuckets.bucket90().setValue(agingBuckets.bucket90().getValue().add(debit.outstandingDebit().getValue()));
            } else {
                agingBuckets.bucketOver90().setValue(agingBuckets.bucketOver90().getValue().add(debit.outstandingDebit().getValue()));
            }
        }

        return agingBucketsMap.values();
    }

    static AgingBuckets calculateTotalAgingBuckets(Collection<AgingBuckets> agingBucketsCollection) {
        AgingBuckets agingBuckets = createAgingBuckets(DebitType.total);
        for (AgingBuckets typedBuckets : agingBucketsCollection) {
            agingBuckets.bucketCurrent().setValue(agingBuckets.bucketCurrent().getValue().add(typedBuckets.bucketCurrent().getValue()));
            agingBuckets.bucket30().setValue(agingBuckets.bucket30().getValue().add(typedBuckets.bucket30().getValue()));
            agingBuckets.bucket60().setValue(agingBuckets.bucket60().getValue().add(typedBuckets.bucket60().getValue()));
            agingBuckets.bucket90().setValue(agingBuckets.bucket90().getValue().add(typedBuckets.bucket90().getValue()));
            agingBuckets.bucketOver90().setValue(agingBuckets.bucketOver90().getValue().add(typedBuckets.bucketOver90().getValue()));
        }
        return agingBuckets;
    }

    private static AgingBuckets createAgingBuckets(DebitType debitType) {
        AgingBuckets agingBuckets = EntityFactory.create(AgingBuckets.class);
        agingBuckets.bucketCurrent().setValue(new BigDecimal("0.00"));
        agingBuckets.bucket30().setValue(new BigDecimal("0.00"));
        agingBuckets.bucket60().setValue(new BigDecimal("0.00"));
        agingBuckets.bucket90().setValue(new BigDecimal("0.00"));
        agingBuckets.bucketOver90().setValue(new BigDecimal("0.00"));
        agingBuckets.debitType().setValue(debitType);
        return agingBuckets;
    }
}
