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
import java.util.EnumMap;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityGraph;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.ArrearsSnapshot;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.property.asset.building.Building;

public class ARArrearsManager {

    private static ArrearsSnapshot createArrearsSnapshot(BillingAccount billingAccount) {
        ArrearsSnapshot arrearsSnapshot = createZeroArrearsSnapshot();
        arrearsSnapshot.agingBuckets().addAll(getAgingBuckets(billingAccount));
        arrearsSnapshot.totalAgingBuckets().set(calculateTotalAgingBuckets(arrearsSnapshot.agingBuckets()));
        BigDecimal arrearsAmount = arrearsSnapshot.totalAgingBuckets().bucket30().getValue();
        arrearsAmount = arrearsAmount.add(arrearsSnapshot.totalAgingBuckets().bucket60().getValue());
        arrearsAmount = arrearsAmount.add(arrearsSnapshot.totalAgingBuckets().bucket90().getValue());
        arrearsAmount = arrearsAmount.add(arrearsSnapshot.totalAgingBuckets().bucketOver90().getValue());

        arrearsSnapshot.arrearsAmount().setValue(arrearsAmount);
        arrearsSnapshot.arrearsAmount().setValue(arrearsAmount);
        arrearsSnapshot.fromDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        arrearsSnapshot.fromDate().setValue(arrearsSnapshot.toDate().getValue());
        return arrearsSnapshot;
    }

    private static ArrearsSnapshot createArrearsSnapshot(Building building) {
        EntityQueryCriteria<BillingAccount> billingAccountsCriteria = EntityQueryCriteria.create(BillingAccount.class);
        billingAccountsCriteria.add(PropertyCriterion.eq(billingAccountsCriteria.proto().lease().unit().belongsTo(), building));
        Iterator<BillingAccount> billingAccountsIter = Persistence.service().query(null, billingAccountsCriteria, AttachLevel.IdOnly);

        // initialize accumulators - we accumulate aging buckets for each category separately in order to increase performance         
        ArrearsSnapshot arrearsSnapshotAcc = createZeroArrearsSnapshot();
        EnumMap<DebitType, AgingBuckets> agingBucketsAcc = new EnumMap<InvoiceDebit.DebitType, AgingBuckets>(DebitType.class);
        for (DebitType debitType : DebitType.values()) {
            agingBucketsAcc.put(debitType, createAgingBuckets(debitType));
        }

        // accumulate
        while (billingAccountsIter.hasNext()) {
            ArrearsSnapshot arrearsSnapshot = createArrearsSnapshot(billingAccountsIter.next());
            for (AgingBuckets agingBuckets : arrearsSnapshot.agingBuckets()) {
                addInPlace(agingBucketsAcc.get(agingBuckets.debitType().getValue()), agingBuckets);
            }
            addInPlace(arrearsSnapshotAcc.totalAgingBuckets(), arrearsSnapshot.totalAgingBuckets());
            arrearsSnapshotAcc.arrearsAmount().setValue(arrearsSnapshotAcc.arrearsAmount().getValue().add(arrearsSnapshot.arrearsAmount().getValue()));
            arrearsSnapshotAcc.creditAmount().setValue(arrearsSnapshotAcc.creditAmount().getValue().add(arrearsSnapshot.creditAmount().getValue()));
        }

        // put accumulated agingBuckets by category back to the general snapshot acccumulator
        arrearsSnapshotAcc.agingBuckets().addAll(agingBucketsAcc.values());

        return arrearsSnapshotAcc;
    }

    static void updateArrearsHistory(BillingAccount billingAccount) {
        // 1. createArrearsSnapshot for current time
        ArrearsSnapshot currentSnapshot = createArrearsSnapshot(billingAccount);

        // 2. retrieve previous ArrearsSnapshot
        LogicalDate now = new LogicalDate(Persistence.service().getTransactionSystemTime());

        EntityQueryCriteria<ArrearsSnapshot> criteria = EntityQueryCriteria.create(ArrearsSnapshot.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.add(PropertyCriterion.le(criteria.proto().toDate(), now));
        criteria.desc(criteria.proto().fromDate());
        ArrearsSnapshot previousSnapshot = Persistence.service().retrieve(criteria);

        // 3. compare 1 and 2 - if it is a difference persist first and update toDate of second otherwise do nothing (if 
        if (previousSnapshot == null || areDifferent(currentSnapshot, previousSnapshot)) {
            boolean isSameDaySnapshot = false;

            if (previousSnapshot != null) {
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(Persistence.service().getTransactionSystemTime());
                cal.add(Calendar.DAY_OF_MONTH, -1);
                LogicalDate prevSnapshotToDate = new LogicalDate(cal.getTime());

                if (prevSnapshotToDate.before(previousSnapshot.fromDate().getValue())) {
                    isSameDaySnapshot = true;
                } else {
                    previousSnapshot.toDate().setValue(prevSnapshotToDate);
                    Persistence.service().persist(previousSnapshot);
                }

            }

            if (isSameDaySnapshot) {
                Persistence.service().delete(previousSnapshot);
            }

            currentSnapshot.fromDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
            currentSnapshot.toDate().setValue(OccupancyFacade.MAX_DATE);

            currentSnapshot.billingAccount().set(billingAccount);

            Persistence.service().persist(currentSnapshot);
        }
    }

    static void updateArrearsHistory(Building building) {
        // TODO Aryom
    }

    static ArrearsSnapshot getArrearsSnapshot(BillingAccount billingAccount, LogicalDate date) {
        EntityQueryCriteria<ArrearsSnapshot> criteria = EntityQueryCriteria.create(ArrearsSnapshot.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.add(PropertyCriterion.le(criteria.proto().fromDate(), date));
        criteria.add(PropertyCriterion.ge(criteria.proto().toDate(), date));

        ArrearsSnapshot snapshot = Persistence.service().retrieve(criteria);
        return snapshot;
    }

    static ArrearsSnapshot getArrearsSnapshot(Building building, LogicalDate date) {
        // TODO Artyom
        return null;
    }

    /**
     * @return return of a roster of arrearsSnapshots per billing accounts of the selected building.
     */
    static List<ArrearsSnapshot> getArrearsSnapshotRoster(List<Building> buildings, LogicalDate asOf) {
        List<ArrearsSnapshot> arrearsRoster = new LinkedList<ArrearsSnapshot>();
        for (Building building : buildings) {
            EntityQueryCriteria<BillingAccount> billingAccountsCriteria = EntityQueryCriteria.create(BillingAccount.class);
            billingAccountsCriteria.add(PropertyCriterion.in(billingAccountsCriteria.proto().lease().unit().belongsTo(), building));
            Iterator<BillingAccount> billingAccountsIter = Persistence.service().query(null, billingAccountsCriteria, AttachLevel.IdOnly);

            while (billingAccountsIter.hasNext()) {
                arrearsRoster.add(getArrearsSnapshot(billingAccountsIter.next(), asOf));
            }
        }
        return arrearsRoster;

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

            if (debit.dueDate().getValue().compareTo(currentDate) > 0) {
                agingBuckets.bucketCurrent().setValue(agingBuckets.bucketCurrent().getValue().add(debit.outstandingDebit().getValue()));
            } else if (debit.dueDate().getValue().compareTo(currentDate) <= 0 && debit.dueDate().getValue().compareTo(date30) > 0) {
                agingBuckets.bucket30().setValue(agingBuckets.bucket30().getValue().add(debit.outstandingDebit().getValue()));
            } else if (debit.dueDate().getValue().compareTo(date30) <= 0 && debit.dueDate().getValue().compareTo(date60) > 0) {
                agingBuckets.bucket60().setValue(agingBuckets.bucket60().getValue().add(debit.outstandingDebit().getValue()));
            } else if (debit.dueDate().getValue().compareTo(date60) <= 0 && debit.dueDate().getValue().compareTo(date90) > 0) {
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

    private static ArrearsSnapshot createZeroArrearsSnapshot() {
        ArrearsSnapshot snapshot = EntityFactory.create(ArrearsSnapshot.class);
        snapshot.arrearsAmount().setValue(BigDecimal.ZERO);
        snapshot.creditAmount().setValue(BigDecimal.ZERO);
        snapshot.totalAgingBuckets().set(createAgingBuckets(DebitType.total));
        return snapshot;
    }

    private static void addInPlace(AgingBuckets buckets1, AgingBuckets buckets2) {
        buckets1.bucketCurrent().setValue(buckets1.bucketCurrent().getValue().add(buckets2.bucketCurrent().getValue()));
        buckets1.bucket30().setValue(buckets1.bucket30().getValue().add(buckets2.bucket30().getValue()));
        buckets1.bucket60().setValue(buckets1.bucket60().getValue().add(buckets2.bucket60().getValue()));
        buckets1.bucket90().setValue(buckets1.bucket90().getValue().add(buckets2.bucket90().getValue()));
        buckets1.bucketOver90().setValue(buckets1.bucketOver90().getValue().add(buckets2.bucketOver90().getValue()));
    }

    private static boolean areDifferent(ArrearsSnapshot currentSnapshot, ArrearsSnapshot previousSnapshot) {
        if (!EntityGraph.fullyEqualValues(currentSnapshot.totalAgingBuckets(), previousSnapshot.totalAgingBuckets())) {
            return false;
        }
        if (currentSnapshot.agingBuckets().size() != previousSnapshot.agingBuckets().size()) {
            return false;
        }

        EnumMap<DebitType, AgingBuckets> currentBuckets = new EnumMap<InvoiceDebit.DebitType, AgingBuckets>(DebitType.class);
        for (AgingBuckets buckets : currentSnapshot.agingBuckets()) {
            currentBuckets.put(buckets.debitType().getValue(), buckets);
        }
        for (AgingBuckets previous : previousSnapshot.agingBuckets()) {
            AgingBuckets current = currentBuckets.get(previous.debitType().getValue());
            if (current == null || !EntityGraph.fullyEqualValues(current, previous)) {
                return false;
            }
        }

        return EqualsHelper.equals(currentSnapshot.arrearsAmount().getValue(), previousSnapshot.arrearsAmount().getValue())
                & EqualsHelper.equals(currentSnapshot.creditAmount().getValue(), previousSnapshot.creditAmount().getValue());
    }

}
