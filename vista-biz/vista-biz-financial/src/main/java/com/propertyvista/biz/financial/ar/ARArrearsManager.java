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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.BillingAccount;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.ArrearsSnapshot;
import com.propertyvista.domain.financial.billing.BuildingAgingBuckets;
import com.propertyvista.domain.financial.billing.BuildingArrearsSnapshot;
import com.propertyvista.domain.financial.billing.InvoiceDebit;
import com.propertyvista.domain.financial.billing.LeaseAgingBuckets;
import com.propertyvista.domain.financial.billing.LeaseArrearsSnapshot;
import com.propertyvista.domain.property.asset.building.Building;

public class ARArrearsManager {

    private ARArrearsManager() {
    }

    private static class SingletonHolder {
        public static final ARArrearsManager INSTANCE = new ARArrearsManager();
    }

    public static ARArrearsManager instance() {
        return SingletonHolder.INSTANCE;
    }

    public LeaseArrearsSnapshot retrieveArrearsSnapshot(BillingAccount billingAccount, LogicalDate date) {
        EntityQueryCriteria<LeaseArrearsSnapshot> criteria = EntityQueryCriteria.create(LeaseArrearsSnapshot.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().billingAccount(), billingAccount));
        criteria.add(PropertyCriterion.ge(criteria.proto().toDate(), date));
        criteria.add(PropertyCriterion.le(criteria.proto().fromDate(), date));
        LeaseArrearsSnapshot snapshot = Persistence.service().retrieve(criteria);
        return snapshot;
    }

    public BuildingArrearsSnapshot retrieveArrearsSnapshot(Building building, LogicalDate date, boolean secure) {
        EntityQueryCriteria<BuildingArrearsSnapshot> criteria = EntityQueryCriteria.create(BuildingArrearsSnapshot.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().building(), building));
        criteria.add(PropertyCriterion.ge(criteria.proto().toDate(), date));
        criteria.add(PropertyCriterion.le(criteria.proto().fromDate(), date));
        if (secure) {
            Persistence.applyDatasetAccessRule(criteria);
        }
        return Persistence.service().retrieve(criteria);
    }

    /**
     * @return a list of arrearsSnapshots per billing accounts of the selected buildings.
     */
    // TODO get this function out of this class (actually i'm not sure all these 'retrieveArrearsSnapshot' functions belong here
    public EntitySearchResult<LeaseArrearsSnapshot> retrieveArrearsSnapshotRoster(LogicalDate asOf, List<Building> buildings, Vector<Criterion> searchCriteria,
            Vector<Sort> sortCriteria, int pageNumber, int pageSize) {

        Vector<LeaseArrearsSnapshot> arrearsRoster = new Vector<LeaseArrearsSnapshot>();

        EntityListCriteria<LeaseArrearsSnapshot> criteria = new EntityListCriteria<LeaseArrearsSnapshot>(LeaseArrearsSnapshot.class);
        if (!buildings.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().billingAccount().lease().unit().building(), new Vector<Building>(buildings)));
        }

        // TODO this looks like a hack and i don't like it
        if (pageSize != Integer.MAX_VALUE) {
            criteria.setPageNumber(pageNumber);
            criteria.setPageSize(pageSize);
        }

        criteria.add(PropertyCriterion.ge(criteria.proto().toDate(), asOf));
        criteria.add(PropertyCriterion.le(criteria.proto().fromDate(), asOf));

        criteria.addAll(searchCriteria);
        criteria.setSorts(sortCriteria);

        arrearsRoster = new Vector<LeaseArrearsSnapshot>(Persistence.service().query(criteria));

        EntitySearchResult<LeaseArrearsSnapshot> result = new EntitySearchResult<LeaseArrearsSnapshot>();
        result.setTotalRows(Persistence.service().count(criteria));

        if (pageSize != Integer.MAX_VALUE) {
            result.hasMoreData(result.getTotalRows() > (pageSize * pageNumber));
        } else {
            result.hasMoreData(false);
        }
        result.setData(arrearsRoster);

        return result;
    }

    public void updateArrearsHistory(BillingAccount billingAccount) {
        // 1. createArrearsSnapshot for current time
        LeaseArrearsSnapshot currentSnapshot = takeArrearsSnapshot(billingAccount);

        // 2. retrieve previous ArrearsSnapshot
        LogicalDate asOfNow = new LogicalDate(SystemDateManager.getDate());
        LeaseArrearsSnapshot previousSnapshot = retrieveArrearsSnapshot(billingAccount, asOfNow);

        // 3. compare 1 and 2 - if it is a difference persist first and update toDate of second otherwise do nothing
        currentSnapshot.billingAccount().set(billingAccount);
        persistIfChanged(currentSnapshot, previousSnapshot);
    }

    // 1. createArrearsSnapshot for current time
    public void updateArrearsHistory(Building building) {
        BuildingArrearsSnapshot currentSnapshot = takeArrearsSnapshot(building);

        // 2. retrieve previous ArrearsSnapshot
        LogicalDate asOf = new LogicalDate(SystemDateManager.getDate());
        BuildingArrearsSnapshot previousSnapshot = retrieveArrearsSnapshot(building, asOf, false);

        // 3. compare 1 and 2 - if it is a difference persist first and update toDate of second otherwise do nothing
        currentSnapshot.building().set(building);
        persistIfChanged(currentSnapshot, previousSnapshot);
    }

    public final Collection<LeaseAgingBuckets> getAgingBuckets(BillingAccount account) {
        List<InvoiceDebit> debits = ServerSideFactory.create(ARFacade.class).getNotCoveredDebitInvoiceLineItems(account);
        Collection<LeaseAgingBuckets> buckets = calculateAgingBuckets(debits);
        return buckets;
    }

    protected Collection<LeaseAgingBuckets> calculateAgingBuckets(List<InvoiceDebit> debits) {
        Map<ARCode.Type, LeaseAgingBuckets> agingBucketsMap = new EnumMap<ARCode.Type, LeaseAgingBuckets>(ARCode.Type.class);

        LogicalDate currentDate = new LogicalDate(SystemDateManager.getDate());

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(currentDate);

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        LogicalDate firstDayOfCurrentMonth = new LogicalDate(calendar.getTime());

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        LogicalDate lastDayOfCurrentMonth = new LogicalDate(calendar.getTime());

        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, -30);
        LogicalDate date30 = new LogicalDate(calendar.getTime());
        calendar.add(Calendar.DATE, -30);
        LogicalDate date60 = new LogicalDate(calendar.getTime());
        calendar.add(Calendar.DATE, -30);
        LogicalDate date90 = new LogicalDate(calendar.getTime());

        for (InvoiceDebit debit : debits) {
            ARCode.Type arrearsCategory = debit.arCode().type().getValue();

            if (!agingBucketsMap.containsKey(arrearsCategory)) {
                agingBucketsMap.put(arrearsCategory, ARArreasManagerUtils.createAgingBuckets(LeaseAgingBuckets.class, arrearsCategory));
            }
            LeaseAgingBuckets agingBuckets = agingBucketsMap.get(arrearsCategory);

            if (debit.dueDate().getValue().compareTo(firstDayOfCurrentMonth) >= 0 & debit.dueDate().getValue().compareTo(lastDayOfCurrentMonth) <= 0) {
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

        // TODO calculate prepayments

        for (LeaseAgingBuckets agingBuckets : agingBucketsMap.values()) {
            BigDecimal arrearsAmount = agingBuckets.bucket30().getValue();
            arrearsAmount = arrearsAmount.add(agingBuckets.bucket60().getValue());
            arrearsAmount = arrearsAmount.add(agingBuckets.bucket90().getValue());
            arrearsAmount = arrearsAmount.add(agingBuckets.bucketOver90().getValue());

            agingBuckets.arrearsAmount().setValue(arrearsAmount);
            agingBuckets.totalBalance().setValue(arrearsAmount.subtract(agingBuckets.creditAmount().getValue()));
        }

        return agingBucketsMap.values();
    }

    private LeaseArrearsSnapshot takeArrearsSnapshot(BillingAccount billingAccount) {
        LeaseArrearsSnapshot arrearsSnapshot = createZeroArrearsSnapshot(LeaseArrearsSnapshot.class);

        LeaseAgingBuckets total = arrearsSnapshot.agingBuckets().get(0);
        arrearsSnapshot.agingBuckets().addAll(getAgingBuckets(billingAccount));
        ARArreasManagerUtils.addInPlace(//@formatter:off
                        ARArreasManagerUtils.initAgingBuckets(total, null),
                        arrearsSnapshot.agingBuckets()
        );//@formatter:on        

        // FIXME what the hell is going on with the following two lines???
        arrearsSnapshot.fromDate().setValue(new LogicalDate(SystemDateManager.getDate()));
        arrearsSnapshot.fromDate().setValue(arrearsSnapshot.toDate().getValue());
        arrearsSnapshot.lmrToUnitRentDifference().setValue(lastMonthRentDeposit(billingAccount).subtract(unitRent(billingAccount)));

        return arrearsSnapshot;
    }

    private BuildingArrearsSnapshot takeArrearsSnapshot(Building building) {
        EntityQueryCriteria<BillingAccount> billingAccountsCriteria = EntityQueryCriteria.create(BillingAccount.class);
        billingAccountsCriteria.add(PropertyCriterion.eq(billingAccountsCriteria.proto().lease().unit().building(), building));
        Iterator<BillingAccount> billingAccountsIter = Persistence.service().query(null, billingAccountsCriteria, AttachLevel.IdOnly);

        // initialize accumulators - we accumulate aging buckets for each category separately in order to increase performance

        BuildingArrearsSnapshot arrearsSnapshotAcc = createZeroArrearsSnapshot(BuildingArrearsSnapshot.class);
        BuildingAgingBuckets total = arrearsSnapshotAcc.agingBuckets().get(0);
        EnumMap<ARCode.Type, BuildingAgingBuckets> agingBucketsAcc = new EnumMap<ARCode.Type, BuildingAgingBuckets>(ARCode.Type.class);
        for (ARCode.Type arrearsCategory : ARCode.Type.values()) {
            agingBucketsAcc.put(arrearsCategory, ARArreasManagerUtils.createAgingBuckets(BuildingAgingBuckets.class, arrearsCategory));
        }

        // accumulate        
        while (billingAccountsIter.hasNext()) {
            LeaseArrearsSnapshot arrearsSnapshot = takeArrearsSnapshot(billingAccountsIter.next());
            for (LeaseAgingBuckets agingBuckets : arrearsSnapshot.agingBuckets()) {
                if (agingBuckets.arCode().isNull()) {
                    ARArreasManagerUtils.addInPlace(total, agingBuckets);
                } else {
                    ARArreasManagerUtils.addInPlace(agingBucketsAcc.get(agingBuckets.arCode().getValue()), agingBuckets);
                }
            }
        }
        // put accumulated agingBuckets by category back to the general snapshot accumulator
        arrearsSnapshotAcc.agingBuckets().addAll(agingBucketsAcc.values());
        return arrearsSnapshotAcc;
    }

    private BigDecimal unitRent(BillingAccount billingAccount) {
        return new BigDecimal("0.00"); // TODO how to fetch unit rent + taxes;
    }

    private BigDecimal lastMonthRentDeposit(BillingAccount billingAccount) {
        return new BigDecimal("0.00");// TODO how to get last month rent deposit and taxes
    }

    private static <AGING_BUCKETS extends AgingBuckets<?>, ARREARS_SNAPSHOT extends ArrearsSnapshot<AGING_BUCKETS>> ARREARS_SNAPSHOT createZeroArrearsSnapshot(
            Class<ARREARS_SNAPSHOT> arrearsSnapshotClass) {
        ARREARS_SNAPSHOT snapshot = EntityFactory.create(arrearsSnapshotClass);
        snapshot.agingBuckets().add(ARArreasManagerUtils.initAgingBuckets(snapshot.agingBuckets().$(), null));
        return snapshot;
    }

    private static void persistIfChanged(ArrearsSnapshot<?> currentSnapshot, ArrearsSnapshot<?> previousSnapshot) {
        if (previousSnapshot == null || ARArreasManagerUtils.haveDifferentBucketValues(currentSnapshot, previousSnapshot)) {
            boolean isSameDaySnapshot = false;

            if (previousSnapshot != null) {
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(SystemDateManager.getDate());
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

            currentSnapshot.fromDate().setValue(new LogicalDate(SystemDateManager.getDate()));
            currentSnapshot.toDate().setValue(OccupancyFacade.MAX_DATE);
            Persistence.service().persist(currentSnapshot);
        }
    }

}
