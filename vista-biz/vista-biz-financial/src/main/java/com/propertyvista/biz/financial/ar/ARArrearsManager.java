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
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityGraph;

import com.propertyvista.biz.financial.SysDateManager;
import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.ArrearsSnapshot;
import com.propertyvista.domain.financial.billing.BuildingArrearsSnapshot;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;
import com.propertyvista.domain.financial.billing.LeaseArrearsSnapshot;
import com.propertyvista.domain.property.asset.building.Building;

public class ARArrearsManager {

    private static LeaseArrearsSnapshot createArrearsSnapshot(BillingAccount billingAccount) {
        LeaseArrearsSnapshot arrearsSnapshot = createZeroArrearsSnapshot(LeaseArrearsSnapshot.class);
        arrearsSnapshot.agingBuckets().addAll(getAgingBuckets(billingAccount));
        arrearsSnapshot.totalAgingBuckets().set(calculateTotalAgingBuckets(arrearsSnapshot.agingBuckets()));

        arrearsSnapshot.fromDate().setValue(new LogicalDate(Persistence.service().getTransactionSystemTime()));
        arrearsSnapshot.fromDate().setValue(arrearsSnapshot.toDate().getValue());

        arrearsSnapshot.lmrToUnitRentDifference().setValue(lastMonthRentDeposit(billingAccount).subtract(unitRent(billingAccount)));
        return arrearsSnapshot;
    }

    private static BigDecimal unitRent(BillingAccount billingAccount) {
        return new BigDecimal("0.00"); // TODO how to fetch unit rent + taxes;
    }

    private static BigDecimal lastMonthRentDeposit(BillingAccount billingAccount) {
        return new BigDecimal("0.00");// TODO how to get last month rent deposit and taxes
    }

    private static BuildingArrearsSnapshot createArrearsSnapshot(Building building) {
        EntityQueryCriteria<BillingAccount> billingAccountsCriteria = EntityQueryCriteria.create(BillingAccount.class);
        billingAccountsCriteria.add(PropertyCriterion.eq(billingAccountsCriteria.proto().lease().unit().belongsTo(), building));
        Iterator<BillingAccount> billingAccountsIter = Persistence.service().query(null, billingAccountsCriteria, AttachLevel.IdOnly);

        // initialize accumulators - we accumulate aging buckets for each category separately in order to increase performance         
        BuildingArrearsSnapshot arrearsSnapshotAcc = createZeroArrearsSnapshot(BuildingArrearsSnapshot.class);
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
        }

        // put accumulated agingBuckets by category back to the general snapshot acccumulator
        arrearsSnapshotAcc.agingBuckets().addAll(agingBucketsAcc.values());

        return arrearsSnapshotAcc;
    }

    static void updateArrearsHistory(BillingAccount billingAccount) {
        // 1. createArrearsSnapshot for current time
        LeaseArrearsSnapshot currentSnapshot = createArrearsSnapshot(billingAccount);

        // 2. retrieve previous ArrearsSnapshot
        LogicalDate asOfNow = new LogicalDate(Persistence.service().getTransactionSystemTime());
        LeaseArrearsSnapshot previousSnapshot = getArrearsSnapshot(billingAccount, asOfNow);

        // 3. compare 1 and 2 - if it is a difference persist first and update toDate of second otherwise do nothing
        currentSnapshot.billingAccount().set(billingAccount);
        saveIfChanged(currentSnapshot, previousSnapshot);

    }

    static void updateArrearsHistory(Building building) {
        // 1. createArrearsSnapshot for current time
        BuildingArrearsSnapshot currentSnapshot = createArrearsSnapshot(building);

        // 2. retrieve previous ArrearsSnapshot
        LogicalDate asOf = new LogicalDate(Persistence.service().getTransactionSystemTime());
        BuildingArrearsSnapshot previousSnapshot = getArrearsSnapshot(building, asOf);

        // 3. compare 1 and 2 - if it is a difference persist first and update toDate of second otherwise do nothing
        saveIfChanged(currentSnapshot, previousSnapshot);
    }

    static LeaseArrearsSnapshot getArrearsSnapshot(BillingAccount billingAccount, LogicalDate date) {
        EntityQueryCriteria<LeaseArrearsSnapshot> criteria = EntityQueryCriteria.create(LeaseArrearsSnapshot.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.add(PropertyCriterion.ge(criteria.proto().toDate(), date));
        criteria.add(PropertyCriterion.le(criteria.proto().fromDate(), date));

        LeaseArrearsSnapshot snapshot = Persistence.service().retrieve(criteria);
        return snapshot;
    }

    static BuildingArrearsSnapshot getArrearsSnapshot(Building building, LogicalDate date) {
        EntityQueryCriteria<BuildingArrearsSnapshot> criteria = EntityQueryCriteria.create(BuildingArrearsSnapshot.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
        criteria.add(PropertyCriterion.ge(criteria.proto().toDate(), date));
        criteria.add(PropertyCriterion.le(criteria.proto().fromDate(), date));

        return Persistence.service().retrieve(criteria);
    }

    /**
     * @param sortCriteria
     * @param searchCriteria
     * @return return of a roster of arrearsSnapshots per billing accounts of the selected building.
     */
    static EntitySearchResult<LeaseArrearsSnapshot> getArrearsSnapshotRoster(LogicalDate asOf, List<Building> buildings, Vector<Criterion> searchCriteria,
            Vector<Sort> sortCriteria, int pageNumber, int pageSize) {

        Vector<LeaseArrearsSnapshot> arrearsRoster = new Vector<LeaseArrearsSnapshot>();

        EntityListCriteria<LeaseArrearsSnapshot> criteria = new EntityListCriteria<LeaseArrearsSnapshot>(LeaseArrearsSnapshot.class);
        if (!buildings.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().billingAccount().lease().unit().belongsTo(), new Vector<Building>(buildings)));
        }

        criteria.setPageNumber(pageNumber);
        criteria.setPageSize(pageSize);

        criteria.add(PropertyCriterion.ge(criteria.proto().toDate(), asOf));
        criteria.add(PropertyCriterion.le(criteria.proto().fromDate(), asOf));

        criteria.addAll(searchCriteria);
        criteria.setSorts(sortCriteria);

        arrearsRoster = new Vector<LeaseArrearsSnapshot>(Persistence.service().query(criteria));

        EntitySearchResult<LeaseArrearsSnapshot> result = new EntitySearchResult<LeaseArrearsSnapshot>();
        result.setTotalRows(Persistence.service().count(criteria));
        result.hasMoreData(result.getTotalRows() > (pageSize * pageNumber));
        result.setData(arrearsRoster);

        return result;

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

        // TODO calculate prepayments

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

    static AgingBuckets calculateTotalAgingBuckets(Collection<AgingBuckets> agingBucketsCollection) {
        AgingBuckets agingBuckets = createAgingBuckets(DebitType.total);
        for (AgingBuckets typedBuckets : agingBucketsCollection) {
            agingBuckets.bucketCurrent().setValue(agingBuckets.bucketCurrent().getValue().add(typedBuckets.bucketCurrent().getValue()));
            agingBuckets.bucket30().setValue(agingBuckets.bucket30().getValue().add(typedBuckets.bucket30().getValue()));
            agingBuckets.bucket60().setValue(agingBuckets.bucket60().getValue().add(typedBuckets.bucket60().getValue()));
            agingBuckets.bucket90().setValue(agingBuckets.bucket90().getValue().add(typedBuckets.bucket90().getValue()));
            agingBuckets.bucketOver90().setValue(agingBuckets.bucketOver90().getValue().add(typedBuckets.bucketOver90().getValue()));
            agingBuckets.arrearsAmount().setValue(agingBuckets.arrearsAmount().getValue().add(typedBuckets.arrearsAmount().getValue()));
            agingBuckets.creditAmount().setValue(agingBuckets.creditAmount().getValue().add(typedBuckets.creditAmount().getValue()));
            agingBuckets.totalBalance().setValue(agingBuckets.totalBalance().getValue().add(typedBuckets.totalBalance().getValue()));
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
        agingBuckets.arrearsAmount().setValue(new BigDecimal("0.00"));
        agingBuckets.creditAmount().setValue(new BigDecimal("0.00"));
        agingBuckets.totalBalance().setValue(new BigDecimal("0.00"));
        agingBuckets.debitType().setValue(debitType);
        return agingBuckets;
    }

    private static <ARREARS_SNAPSHOT extends ArrearsSnapshot> ARREARS_SNAPSHOT createZeroArrearsSnapshot(Class<ARREARS_SNAPSHOT> arrearsSnapshotClass) {
        ARREARS_SNAPSHOT snapshot = EntityFactory.create(arrearsSnapshotClass);
        snapshot.totalAgingBuckets().set(createAgingBuckets(DebitType.total));
        return snapshot;
    }

    private static void addInPlace(AgingBuckets buckets1, AgingBuckets buckets2) {
        buckets1.bucketCurrent().setValue(buckets1.bucketCurrent().getValue().add(buckets2.bucketCurrent().getValue()));
        buckets1.bucket30().setValue(buckets1.bucket30().getValue().add(buckets2.bucket30().getValue()));
        buckets1.bucket60().setValue(buckets1.bucket60().getValue().add(buckets2.bucket60().getValue()));
        buckets1.bucket90().setValue(buckets1.bucket90().getValue().add(buckets2.bucket90().getValue()));
        buckets1.bucketOver90().setValue(buckets1.bucketOver90().getValue().add(buckets2.bucketOver90().getValue()));

        buckets1.arrearsAmount().setValue(buckets1.arrearsAmount().getValue().add(buckets2.arrearsAmount().getValue()));
        buckets1.creditAmount().setValue(buckets1.creditAmount().getValue().add(buckets2.creditAmount().getValue()));
        buckets1.totalBalance().setValue(buckets1.totalBalance().getValue().add(buckets2.totalBalance().getValue()));
    }

    private static boolean areDifferent(ArrearsSnapshot currentSnapshot, ArrearsSnapshot previousSnapshot) {
        if (!EntityGraph.fullyEqualValues(currentSnapshot.totalAgingBuckets(), previousSnapshot.totalAgingBuckets())) {
            return true;
        }
        if (currentSnapshot.agingBuckets().size() != previousSnapshot.agingBuckets().size()) {
            return true;
        }

        EnumMap<DebitType, AgingBuckets> currentBuckets = new EnumMap<InvoiceDebit.DebitType, AgingBuckets>(DebitType.class);
        for (AgingBuckets buckets : currentSnapshot.agingBuckets()) {
            currentBuckets.put(buckets.debitType().getValue(), buckets);
        }
        for (AgingBuckets previous : previousSnapshot.agingBuckets()) {
            AgingBuckets current = currentBuckets.get(previous.debitType().getValue());
            if (current == null || !EntityGraph.fullyEqualValues(current, previous)) {
                return true;
            }
        }

        return false;
    }

    private static void saveIfChanged(ArrearsSnapshot currentSnapshot, ArrearsSnapshot previousSnapshot) {
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
            Persistence.service().persist(currentSnapshot);
        }
    }

}
