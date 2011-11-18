/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 6, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.server.services.dashboard.gadgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.AvailabilityReportService;
import com.propertyvista.crm.server.util.SortingFactory;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.VacancyStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatusDTO;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyReportSummaryDTO;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyReportTurnoverAnalysisDTO;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyReportTurnoverAnalysisDTO.AnalysisResolution;

public class AvailabilityReportServiceImpl implements AvailabilityReportService {
    private static SortingFactory<UnitAvailabilityStatusDTO> SORTING_FACTORY = new SortingFactory<UnitAvailabilityStatusDTO>(UnitAvailabilityStatusDTO.class);

    @Override
    public void unitStatusList(AsyncCallback<EntitySearchResult<UnitAvailabilityStatusDTO>> callback, Vector<Key> buildings, boolean displayOccupied,
            boolean displayVacant, boolean displayNotice, boolean displayRented, boolean displayNotRented, LogicalDate to, Vector<Sort> sortingCriteria,
            int pageNumber, int pageSize) {
        try {
            EntityListCriteria<UnitAvailabilityStatus> criteria = new EntityListCriteria<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);

            ArrayList<UnitAvailabilityStatusDTO> allUnitStatuses = new ArrayList<UnitAvailabilityStatusDTO>();
            if (!buildings.isEmpty()) {
                criteria.add(PropertyCriterion.in(criteria.proto().buildingBelongsTo(), buildings));
            }
            if (to == null) {
                to = new LogicalDate();
            }
            criteria.add(new PropertyCriterion(criteria.proto().statusDate(), Restriction.LESS_THAN_OR_EQUAL, to));

            // use descending order of the status date in order to select the most recent statuses first
            // use unit pk sorting in order to make tacking of already added unit statuses
            criteria.setSorts(Arrays.asList(new Sort(criteria.proto().belongsTo().getPath().toString(), true), new Sort(criteria.proto().statusDate().getPath()
                    .toString(), true)));
            List<UnitAvailabilityStatus> unfiltered = Persistence.service().query(criteria);

            Key pervUnitPK = null;
            for (UnitAvailabilityStatus unitStatus : unfiltered) {
                Key thisUnitPK = unitStatus.belongsTo().getPrimaryKey();
                if (!thisUnitPK.equals(pervUnitPK)) {
                    allUnitStatuses.add(computeTransientFields(unitStatus, to));
                    pervUnitPK = thisUnitPK;
                }
            }

            if (!sortingCriteria.isEmpty()) {
                Collections.sort(allUnitStatuses, SORTING_FACTORY.createDtoComparator(sortingCriteria));
            }

            int currentPage = 0;
            int currentPagePosition = 0;
            int totalRows = 0;
            boolean hasMoreRows = false;
            Vector<UnitAvailabilityStatusDTO> unitsStatusPage = new Vector<UnitAvailabilityStatusDTO>();

            Iterator<UnitAvailabilityStatusDTO> i = allUnitStatuses.iterator();
            while (i.hasNext()) {
                UnitAvailabilityStatusDTO unitStatus = i.next();
                if (isAcceptable(unitStatus, displayOccupied, displayVacant, displayNotice, displayRented, displayNotRented)) {
                    ++currentPagePosition;
                    ++totalRows;
                    if (currentPagePosition > pageSize) {
                        ++currentPage;
                        currentPagePosition = 1;
                    }
                    if (currentPage < pageNumber) {
                        continue;
                    } else if (currentPage == pageNumber) {
                        unitsStatusPage.add(unitStatus);
                    } else {
                        hasMoreRows = true;
                        break;
                    }
                }
            }
            while (i.hasNext()) {
                UnitAvailabilityStatusDTO unitStatus = i.next();
                if (isAcceptable(unitStatus, displayOccupied, displayVacant, displayNotice, displayRented, displayNotRented)) {
                    ++totalRows;
                }
            }

            EntitySearchResult<UnitAvailabilityStatusDTO> result = new EntitySearchResult<UnitAvailabilityStatusDTO>();
            result.setData(unitsStatusPage);
            result.setTotalRows(totalRows);
            result.hasMoreData(hasMoreRows);

            callback.onSuccess(result);
        } catch (Throwable error) {
            callback.onFailure(new Error(error));
        }
    }

    private static boolean isAcceptable(UnitAvailabilityStatusDTO unit, boolean displayOccupied, boolean displayVacant, boolean displayNotice,
            boolean displayRented, boolean displayNotRented) {

        VacancyStatus vacancyStatus = unit.vacancyStatus().getValue();
        RentedStatus rentedStatus = unit.rentedStatus().getValue();

        return (displayOccupied & vacancyStatus == null) //
                | ((displayVacant & vacancyStatus == VacancyStatus.Vacant) & ((displayRented & rentedStatus == RentedStatus.Rented) | (displayNotRented & rentedStatus != RentedStatus.Rented))) //
                | ((displayNotice & vacancyStatus == VacancyStatus.Notice) & ((displayRented & rentedStatus == RentedStatus.Rented) | (displayNotRented & rentedStatus != RentedStatus.Rented)));

    }

    @Override
    public void summary(AsyncCallback<UnitVacancyReportSummaryDTO> callback, Vector<Key> buildings, LogicalDate toDate) {
        if (buildings == null) {
            callback.onFailure(new Error("the set of buildings was not provided."));
            return;
        }

        EntityQueryCriteria<UnitAvailabilityStatus> criteria = new EntityQueryCriteria<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);
        if (!buildings.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().buildingBelongsTo(), buildings));
        }
        if (toDate == null) {
            toDate = new LogicalDate();
        }
        criteria.add(new PropertyCriterion(criteria.proto().statusDate(), Restriction.LESS_THAN_OR_EQUAL, toDate));
        // use descending order of the status date in order to select the most recent statuses first
        criteria.desc(criteria.proto().statusDate().getPath().toString());

        List<UnitAvailabilityStatus> unitStatuses = Persistence.service().query(criteria);

        UnitVacancyReportSummaryDTO summary = EntityFactory.create(UnitVacancyReportSummaryDTO.class);

        int total = 0;

        int vacant = 0;
        int vacantRented = 0;

        int occupied = 0;

        int notice = 0;
        int noticeRented = 0;

        int netExposure = 0;

        // use hash to mark units, since we need only the last date
        // TODO use proper sorting by unit PK to avoid the usage of hash 
        HashSet<Key> checkedUnits = new HashSet<Key>(unitStatuses.size());

        for (UnitAvailabilityStatus unitStatus : unitStatuses) {
            if (!checkedUnits.add(unitStatus.belongsTo().getPrimaryKey())) {
                continue;
            } else {
                ++total;

                // check that we have vacancy status, and don't waste the cpu cycles if we don't have it
                VacancyStatus vacancyStatus = unitStatus.vacancyStatus().getValue();
                if (vacancyStatus == null) {
                    continue;

                } else if (VacancyStatus.Vacant.equals(vacancyStatus)) {
                    ++vacant;
                    if (RentedStatus.Rented.equals(unitStatus.rentedStatus().getValue())) {
                        ++vacantRented;
                    }
                } else if (VacancyStatus.Notice.equals(vacancyStatus)) {
                    ++notice;
                    if (RentedStatus.Rented.equals(unitStatus.rentedStatus().getValue())) {
                        ++noticeRented;
                    }
                }
            }
        }

        summary.total().setValue(total);

        summary.vacancyAbsolute().setValue(vacant);
        summary.vacancyRelative().setValue(vacant / ((double) total) * 100d);
        summary.vacantRented().setValue(vacantRented);

        occupied = total - vacant;
        summary.occupancyAbsolute().setValue(occupied);
        summary.occupancyRelative().setValue(occupied / ((double) total) * 100d);

        summary.noticeAbsolute().setValue(notice);
        summary.noticeRelative().setValue(notice / ((double) total) * 100d);
        summary.noticeRented().setValue(noticeRented);

        netExposure = vacant + notice - (vacantRented + noticeRented);
        summary.netExposureAbsolute().setValue(netExposure);
        summary.netExposureRelative().setValue(netExposure / ((double) total) * 100d);
        callback.onSuccess(summary);
    }

    @Override
    public void turnoverAnalysis(AsyncCallback<Vector<UnitVacancyReportTurnoverAnalysisDTO>> callback, Vector<Key> buildings, LogicalDate fromDate,
            LogicalDate toDate, AnalysisResolution resolution) {
        if (resolution == null) {
            callback.onFailure(new Error("resolution is required"));
            return;
        }
        if (fromDate != null && toDate != null && fromDate.after(toDate)) {
            callback.onFailure(new Error("end date is greater than from date"));
            return;
        }

        // FIXME refactor this one: separate generic aggregation and intervals creation from the actual computations
        // Definition: TURNOVER
        //      the number of times the unit switched hands during the specified interval.
        // 
        // so then it's basically: number of 'moveins' during specified period minus 1        

        EntityQueryCriteria<UnitAvailabilityStatus> criteria = new EntityQueryCriteria<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);
        // sort the results by unitKey and then date so that we can check data for each unit separately  
        criteria.setSorts(Arrays.asList(new Sort(criteria.proto().belongsTo().getPath().toString(), false), new Sort(criteria.proto().statusDate().getPath()
                .toString(), false)));
        if (fromDate != null) {
            criteria.add(new PropertyCriterion(criteria.proto().statusDate(), Restriction.GREATER_THAN_OR_EQUAL, fromDate));
        }
        if (toDate != null) {
            criteria.add(new PropertyCriterion(criteria.proto().statusDate(), Restriction.LESS_THAN_OR_EQUAL, toDate));
        }
        if (!buildings.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().buildingBelongsTo(), buildings));
        }

        List<UnitAvailabilityStatus> statuses = Persistence.service().query(criteria);

        Map<Long, TurnoverStats> statsMap = new HashMap<Long, TurnoverStats>();

        long intervalStart = fromDate == null ? 0 : fromDate.getTime();
        long intervalEnd = resolution.intervalEnd(intervalStart);
        int moveins = 0;
        int total = 0;
        int totalForUnit = 0;

        Key unitPK = null;
        VacancyStatus prevVacancy = null;
        boolean skipFirstEmptyRange = fromDate != null ? false : true;

        Iterator<UnitAvailabilityStatus> i = statuses.iterator();
        while (i.hasNext()) {
            UnitAvailabilityStatus status = i.next();
            Key thisUnitPK = status.belongsTo().getPrimaryKey();
            VacancyStatus vacancy = status.vacancyStatus().getValue();
            long statusTime = status.statusDate().getValue().getTime();

            if (!thisUnitPK.equals(unitPK)) {
                unitPK = thisUnitPK;
                total += totalForUnit;
                totalForUnit = 0;
                moveins = 0;
                prevVacancy = vacancy;

                intervalStart = fromDate == null ? 0 : fromDate.getTime();
                intervalEnd = resolution.intervalEnd(intervalStart);
            }
            if (statusTime >= intervalEnd) {
                // add new/update interval with the collected statistics
                int turnovers = moveins > 0 ? moveins - 1 : 0;
                totalForUnit += turnovers;
                moveins = 0;

                if (skipFirstEmptyRange & (totalForUnit == 0)) {
                    // skip to the interval that contains the next event
                    intervalStart = resolution.intervalStart(statusTime);
                    intervalEnd = resolution.addTo(intervalStart);
                } else {
                    updateIntervalStats(statsMap, intervalStart, intervalEnd, turnovers);
                    intervalStart = intervalEnd;
                    intervalEnd = resolution.addTo(intervalStart);

                    // add new/update intervals with for the ranges that don't contain any statuses
                    while (statusTime >= intervalEnd) {
                        updateIntervalStats(statsMap, intervalStart, intervalEnd, 0);
                        intervalStart = intervalEnd;
                        intervalEnd = resolution.addTo(intervalStart);
                    }
                }
            }

            if (vacancy != prevVacancy) {
                if (vacancy == null) {
                    ++moveins;
                }
                prevVacancy = vacancy;
            }
        }

        // update the last interval that collected some statistics if it did, or we have been explicitly asked for it 
        if ((totalForUnit != 0) | (toDate != null)) {
            int turnovers = moveins > 0 ? moveins - 1 : 0;
            updateIntervalStats(statsMap, intervalStart, intervalEnd, turnovers);
            total += totalForUnit;
            intervalStart = intervalEnd;
            intervalEnd = resolution.addTo(intervalStart);

            // now add some more intervals (empty) if we were requested to show them
            if (toDate != null) {
                Long queryEndTime = toDate.getTime();
                while (intervalEnd <= queryEndTime) {
                    updateIntervalStats(statsMap, intervalStart, intervalEnd, 0);

                    intervalStart = intervalEnd;
                    intervalEnd = resolution.addTo(intervalStart);
                }
            }
        }

        ArrayList<TurnoverStats> almostResuls = new ArrayList<TurnoverStats>(statsMap.values());
        Collections.sort(almostResuls);
        Vector<UnitVacancyReportTurnoverAnalysisDTO> results = new Vector<UnitVacancyReportTurnoverAnalysisDTO>(almostResuls.size());
        if (total > 0) {
            for (TurnoverStats stats : almostResuls) {
                UnitVacancyReportTurnoverAnalysisDTO entityStats = stats.toEntity();
                entityStats.unitsTurnedOverPct().setValue(((double) stats.turnovers) / total * 100);
                results.add(entityStats);
            }
        } else {
            for (TurnoverStats stats : almostResuls) {
                UnitVacancyReportTurnoverAnalysisDTO entityStats = stats.toEntity();
                entityStats.unitsTurnedOverPct().setValue(0.0);
                results.add(entityStats);
            }
        }

        callback.onSuccess(results);
    }

    private static UnitAvailabilityStatusDTO computeTransientFields(final UnitAvailabilityStatus unitStatus, final LogicalDate toDate) {
        UnitAvailabilityStatusDTO unitDTO = unitStatus.clone(UnitAvailabilityStatusDTO.class);
        if (isRevenueLost(unitDTO)) {
            computeDaysVacantAndRevenueLost(unitDTO, toDate.getTime());
        }
        return unitDTO;
    }

    private static boolean isRevenueLost(final UnitAvailabilityStatusDTO unit) {
        // TODO review: why the check "(moveOutDay != null)" is performed here? isn't VACANT status a sufficient condition for it? 
        return VacancyStatus.Vacant.equals(unit.vacancyStatus().getValue()) & unit.moveOutDay().getValue() != null;
    }

    /**
     * Due to performance considerations this method does the both things together to avoid unnecessary invocation of
     * {@link UnitAvailabilityStatusDTO#daysVacant()}.
     */
    private static void computeDaysVacantAndRevenueLost(final UnitAvailabilityStatusDTO unit, final long reportTime) {
        long millisecondsVacant = reportTime - unit.availableFromDay().getValue().getTime();

        int daysVacant = (int) (millisecondsVacant / (1000 * 60 * 60 * 24)); // some really heavy math :)            
        unit.daysVacant().setValue(daysVacant);

        double marketRent = unit.marketRent().getValue();
        double revenueLost = daysVacant * marketRent / 30.0;
        unit.revenueLost().setValue(revenueLost);
    }

    private static void updateIntervalStats(Map<Long, TurnoverStats> statsMap, long intervalStart, long intervalEnd, int turnovers) {
        TurnoverStats stats = statsMap.get(intervalStart);
        if (stats != null) {
            stats.turnovers += turnovers;
        } else {
            statsMap.put(intervalStart, new TurnoverStats(intervalStart, intervalEnd, turnovers));
        }
    }

    private static class IntervalStats implements Comparable<IntervalStats> {
        public final long intervalStart;

        public final long intervalEnd;

        public final int hashCode;

        public IntervalStats(long intervalStart, long intervalEnd) {
            this.intervalStart = intervalStart;
            this.intervalEnd = intervalEnd;

            this.hashCode = (int) (intervalStart % Integer.MAX_VALUE); // I hope it's a right way to do it 
        }

        @Override
        final public int hashCode() {
            return hashCode;
        }

        @Override
        final public boolean equals(Object obj) {
            if (obj instanceof IntervalStats) {
                IntervalStats other = (IntervalStats) obj;
                return this.intervalStart == other.intervalStart & this.intervalEnd == other.intervalEnd;
            } else {
                return false;
            }
        }

        @Override
        final public int compareTo(IntervalStats o) {
            // we don't use equals() to avoid instanceof check and casting
            if (this.intervalStart < o.intervalStart) {
                return -1;
            } else if (this.intervalStart > o.intervalStart) {
                return 1;
            } else if (this.intervalEnd > o.intervalEnd) {
                return 1;
            } else if (this.intervalEnd < o.intervalEnd) {
                return -1;
            } else {
                return 0;
            }
        }

    }

    private static class TurnoverStats extends IntervalStats {
        public int turnovers;

        public TurnoverStats(long intervalStart, long intervalEnd, int turnovers) {
            super(intervalStart, intervalEnd);
            this.turnovers = turnovers;
        }

        public UnitVacancyReportTurnoverAnalysisDTO toEntity() {
            UnitVacancyReportTurnoverAnalysisDTO entity = EntityFactory.create(UnitVacancyReportTurnoverAnalysisDTO.class);
            entity.fromDate().setValue(new LogicalDate(intervalStart));
            entity.toDate().setValue(new LogicalDate(intervalEnd));
            entity.unitsTurnedOverAbs().setValue(turnovers);
            return entity;
        }

    }
}
