/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-24
 * @author ArtyomB
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
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;

public class ARArrearsManagerHelper {

    public static AgingBuckets addInPlace(AgingBuckets buckets1, AgingBuckets buckets2) {
        buckets1.bucketThisMonth().setValue(buckets1.bucketThisMonth().getValue().add(buckets2.bucketThisMonth().getValue()));
        buckets1.bucketCurrent().setValue(buckets1.bucketCurrent().getValue().add(buckets2.bucketCurrent().getValue()));
        buckets1.bucket30().setValue(buckets1.bucket30().getValue().add(buckets2.bucket30().getValue()));
        buckets1.bucket60().setValue(buckets1.bucket60().getValue().add(buckets2.bucket60().getValue()));
        buckets1.bucket90().setValue(buckets1.bucket90().getValue().add(buckets2.bucket90().getValue()));
        buckets1.bucketOver90().setValue(buckets1.bucketOver90().getValue().add(buckets2.bucketOver90().getValue()));

        buckets1.arrearsAmount().setValue(buckets1.arrearsAmount().getValue().add(buckets2.arrearsAmount().getValue()));
        buckets1.creditAmount().setValue(buckets1.creditAmount().getValue().add(buckets2.creditAmount().getValue()));
        buckets1.totalBalance().setValue(buckets1.totalBalance().getValue().add(buckets2.totalBalance().getValue()));

        return buckets1;
    }

    public static AgingBuckets addInPlace(AgingBuckets accumulator, Collection<AgingBuckets> agingBucketsCollection) {
        for (AgingBuckets typedBuckets : agingBucketsCollection) {
            addInPlace(accumulator, typedBuckets);
        }
        return accumulator;
    }

    public static AgingBuckets initAgingBuckets(DebitType debitType) {
        AgingBuckets agingBuckets = EntityFactory.create(AgingBuckets.class);
        agingBuckets.bucketThisMonth().setValue(new BigDecimal("0.00"));
        agingBuckets.bucketCurrent().setValue(new BigDecimal("0.00"));
        agingBuckets.bucket30().setValue(new BigDecimal("0.00"));
        agingBuckets.bucket60().setValue(new BigDecimal("0.00"));
        agingBuckets.bucket90().setValue(new BigDecimal("0.00"));
        agingBuckets.bucketOver90().setValue(new BigDecimal("0.00"));
        agingBuckets.arrearsAmount().setValue(new BigDecimal("0.00"));
        agingBuckets.creditAmount().setValue(new BigDecimal("0.00"));
        agingBuckets.totalBalance().setValue(new BigDecimal("0.00"));
        agingBuckets.debitType().setValue(debitType);
        return agingBuckets;
    }

    public static Collection<AgingBuckets> calculateAgingBuckets(List<InvoiceDebit> debits) {
        Map<DebitType, AgingBuckets> agingBucketsMap = new HashMap<DebitType, AgingBuckets>();

        LogicalDate currentDate = new LogicalDate(SystemDateManager.getDate());

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(currentDate);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        LogicalDate firstDayOfCurrentMonth = new LogicalDate(calendar.getTime());
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, -30);
        LogicalDate date30 = new LogicalDate(calendar.getTime());
        calendar.add(Calendar.DATE, -30);
        LogicalDate date60 = new LogicalDate(calendar.getTime());
        calendar.add(Calendar.DATE, -30);
        LogicalDate date90 = new LogicalDate(calendar.getTime());

        for (InvoiceDebit debit : debits) {
            if (!agingBucketsMap.containsKey(debit.debitType().getValue())) {
                agingBucketsMap.put(debit.debitType().getValue(), ARArrearsManagerHelper.initAgingBuckets(debit.debitType().getValue()));
            }
            AgingBuckets agingBuckets = agingBucketsMap.get(debit.debitType().getValue());

            if (debit.dueDate().getValue().compareTo(firstDayOfCurrentMonth) >= 0 & debit.dueDate().getValue().compareTo(currentDate) < 0) {
                agingBuckets.bucketThisMonth().setValue(agingBuckets.bucketThisMonth().getValue().add(debit.outstandingDebit().getValue()));
            }

            if (debit.dueDate().getValue().compareTo(currentDate) >= 0) {
                agingBuckets.bucketCurrent().setValue(agingBuckets.bucketCurrent().getValue().add(debit.outstandingDebit().getValue()));
            } else if (debit.dueDate().getValue().compareTo(currentDate) < 0 && debit.dueDate().getValue().compareTo(date30) >= 0) {
                agingBuckets.bucket30().setValue(agingBuckets.bucket30().getValue().add(debit.outstandingDebit().getValue()));
            } else if (debit.dueDate().getValue().compareTo(date30) < 0 && debit.dueDate().getValue().compareTo(date60) >= 0) {
                agingBuckets.bucket60().setValue(agingBuckets.bucket60().getValue().add(debit.outstandingDebit().getValue()));
            } else if (debit.dueDate().getValue().compareTo(date60) < 0 && debit.dueDate().getValue().compareTo(date90) >= 0) {
                agingBuckets.bucket90().setValue(agingBuckets.bucket90().getValue().add(debit.outstandingDebit().getValue()));
            } else {
                agingBuckets.bucketOver90().setValue(agingBuckets.bucketOver90().getValue().add(debit.outstandingDebit().getValue()));
            }

        }

        // TODO calculate pre payments

        for (AgingBuckets agingBuckets : agingBucketsMap.values()) {
            BigDecimal arrearsAmount = agingBuckets.bucket30().getValue();
            arrearsAmount = arrearsAmount.add(agingBuckets.bucket60().getValue());
            arrearsAmount = arrearsAmount.add(agingBuckets.bucket90().getValue());
            arrearsAmount = arrearsAmount.add(agingBuckets.bucketOver90().getValue());

            agingBuckets.arrearsAmount().setValue(arrearsAmount);
            agingBuckets.totalBalance().setValue(arrearsAmount.subtract(agingBuckets.creditAmount().getValue()));
        }

        return agingBucketsMap.values();
    }

}
