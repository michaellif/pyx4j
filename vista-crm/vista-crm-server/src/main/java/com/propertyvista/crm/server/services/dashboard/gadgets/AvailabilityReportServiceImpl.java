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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.Vacancy;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitVacancyReportSummaryDTO;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitVacancyReportTurnoverAnalysisDTO;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitVacancyReportTurnoverAnalysisDTO.AnalysisResolution;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailability;
import com.propertyvista.domain.dashboard.gadgets.type.UnitAvailability.FilterPreset;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class AvailabilityReportServiceImpl implements AvailabilityReportService {

    private static SortingFactory<UnitAvailabilityStatus> SORTING_FACTORY = new SortingFactory<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);

    @Override
    public void unitStatusList(AsyncCallback<EntitySearchResult<UnitAvailabilityStatus>> callback, Vector<Key> buildings,
            UnitAvailability.FilterPreset filterPreset, LogicalDate on, Vector<Sort> sortingCriteria, int pageNumber, int pageSize) {

        EntityListCriteria<UnitAvailabilityStatus> criteria = new EntityListCriteria<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);

        ArrayList<UnitAvailabilityStatus> allUnitStatuses = new ArrayList<UnitAvailabilityStatus>();

        if (!buildings.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().building(), buildings));
        }
        if (on == null) {
            throw new IllegalArgumentException("the report date cannot be null");
        }
        criteria.add(PropertyCriterion.le(criteria.proto().statusDate(), on));
        criteria.add(PropertyCriterion.ne(criteria.proto().vacancyStatus(), null));

        // use descending order of the status date in order to select the most recent statuses first
        // use unit pk sorting in order to make tacking of already added unit statuses
        criteria.setSorts(Arrays.asList(new Sort(criteria.proto().unit().getPath().toString(), true), new Sort(criteria.proto().statusDate().getPath()
                .toString(), true), new Sort(criteria.proto().id().getPath().toString(), true)));
        List<UnitAvailabilityStatus> unfiltered = Persistence.service().query(criteria);

        Key pervUnitPK = null;
        for (UnitAvailabilityStatus unitStatus : unfiltered) {
            Key thisUnitPK = unitStatus.unit().getPrimaryKey();
            if (!thisUnitPK.equals(pervUnitPK)) {
                allUnitStatuses.add(computeTransientFields(unitStatus, on));
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
        Vector<UnitAvailabilityStatus> unitsStatusPage = new Vector<UnitAvailabilityStatus>();

        Iterator<UnitAvailabilityStatus> i = allUnitStatuses.iterator();
        StatusFilter filter = filterFor(filterPreset);

        while (i.hasNext()) {
            UnitAvailabilityStatus unitStatus = i.next();
            if (filter.isAcceptable(unitStatus)) {
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
            if (filter.isAcceptable(i.next())) {
                ++totalRows;
            }
        }

        clearUnrequiredData(unitsStatusPage);

        EntitySearchResult<UnitAvailabilityStatus> result = new EntitySearchResult<UnitAvailabilityStatus>();

        result.setData(unitsStatusPage);
        result.setTotalRows(totalRows);
        result.hasMoreData(hasMoreRows);

        callback.onSuccess(result);
    }

    private void clearUnrequiredData(Vector<UnitAvailabilityStatus> unitsStatusPage) {
        for (UnitAvailabilityStatus status : unitsStatusPage) {
            Building building = EntityFactory.create(Building.class);
            building.id().setValue(status.building().id().getValue());
            building.propertyCode().setValue(status.building().propertyCode().getValue());
            building.externalId().setValue(status.building().externalId().getValue());
            building.info().name().setValue(status.building().info().name().getValue());
            building.info().address().setValue(status.building().info().address().getValue());
            building.propertyManager().name().setValue(status.building().propertyManager().name().getValue());
            building.complex().name().setValue(status.building().complex().name().getValue());
            status.building().set(building);

            AptUnit unit = EntityFactory.create(AptUnit.class);
            unit.id().setValue(status.unit().id().getValue());
            unit.info().number().setValue(status.unit().info().number().getValue());
            status.unit().set(unit);

            Floorplan floorplan = EntityFactory.create(Floorplan.class);
            floorplan.id().setValue(status.floorplan().id().getValue());
            floorplan.name().setValue(status.floorplan().name().getValue());
            floorplan.marketingName().setValue(status.floorplan().marketingName().getValue());
            status.floorplan().set(floorplan);

        }

    }

    private StatusFilter filterFor(FilterPreset filterPreset) {
        switch (filterPreset) {
        case NetExposure:
            return new StatusFilter() {
                @Override
                public boolean isAcceptable(UnitAvailabilityStatus status) {
                    return status.rentedStatus().getValue() != RentedStatus.Rented;
                }
            };
        case Notice:
            return new StatusFilter() {
                @Override
                public boolean isAcceptable(UnitAvailabilityStatus status) {
                    return status.vacancyStatus().getValue() == Vacancy.Notice;
                }
            };
        case Rented:
            return new StatusFilter() {
                @Override
                public boolean isAcceptable(UnitAvailabilityStatus status) {
                    return status.rentedStatus().getValue() == RentedStatus.Rented;
                }
            };
        case Vacant:
            return new StatusFilter() {
                @Override
                public boolean isAcceptable(UnitAvailabilityStatus status) {
                    return status.vacancyStatus().getValue() == Vacancy.Vacant;
                }
            };
        case VacantAndNotice:
            return new StatusFilter() {
                @Override
                public boolean isAcceptable(UnitAvailabilityStatus status) {
                    return status.vacancyStatus().getValue() != null;
                }
            };
        default:
            throw new IllegalStateException("unknown filter preset: " + filterPreset);
        }
    }

    @Override
    public void summary(AsyncCallback<UnitVacancyReportSummaryDTO> callback, Vector<Key> buildings, LogicalDate toDate) {
        if (buildings == null) {
            callback.onFailure(new Error("the set of buildings was not provided."));
            return;
        }

        EntityQueryCriteria<UnitAvailabilityStatus> criteria = new EntityQueryCriteria<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);
        if (!buildings.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().building(), buildings));
        }
        if (toDate == null) {
            toDate = new LogicalDate();
        }
        criteria.add(PropertyCriterion.le(criteria.proto().statusDate(), toDate));
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
            if (!checkedUnits.add(unitStatus.unit().getPrimaryKey())) {
                continue;
            } else {
                ++total;

                // check that we have vacancy status, and don't waste the cpu cycles if we don't have it
                Vacancy vacancyStatus = unitStatus.vacancyStatus().getValue();
                if (vacancyStatus == null) {
                    continue;

                } else if (Vacancy.Vacant.equals(vacancyStatus)) {
                    ++vacant;
                    if (RentedStatus.Rented.equals(unitStatus.rentedStatus().getValue())) {
                        ++vacantRented;
                    }
                } else if (Vacancy.Notice.equals(vacancyStatus)) {
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
        criteria.setSorts(Arrays.asList(new Sort(criteria.proto().unit().getPath().toString(), false), new Sort(criteria.proto().statusDate().getPath()
                .toString(), false)));
        if (fromDate != null) {
            criteria.add(new PropertyCriterion(criteria.proto().statusDate(), Restriction.GREATER_THAN_OR_EQUAL, fromDate));
        }
        if (toDate != null) {
            criteria.add(new PropertyCriterion(criteria.proto().statusDate(), Restriction.LESS_THAN_OR_EQUAL, toDate));
        }
        if (!buildings.isEmpty()) {
            criteria.add(PropertyCriterion.in(criteria.proto().building(), buildings));
        }

        List<UnitAvailabilityStatus> statuses = Persistence.service().query(criteria);

        Map<Long, TurnoverStats> statsMap = new HashMap<Long, TurnoverStats>();

        long intervalStart = fromDate == null ? 0 : fromDate.getTime();
        long intervalEnd = resolution.intervalEnd(intervalStart);
        int moveins = 0;
        int total = 0;
        int totalForUnit = 0;

        Key unitPK = null;
        Vacancy prevVacancy = null;
        boolean skipFirstEmptyRange = fromDate != null ? false : true;

        Iterator<UnitAvailabilityStatus> i = statuses.iterator();
        while (i.hasNext()) {
            UnitAvailabilityStatus status = i.next();
            Key thisUnitPK = status.unit().getPrimaryKey();
            Vacancy vacancy = status.vacancyStatus().getValue();
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

        int turnovers = moveins > 0 ? moveins - 1 : 0;
        totalForUnit += turnovers;
        // update the last interval that collected some statistics if it did, or we have been explicitly asked for it 
        if ((totalForUnit != 0) | (toDate != null)) {
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

    private static UnitAvailabilityStatus computeTransientFields(final UnitAvailabilityStatus unitStatus, final LogicalDate now) {
        if (isRevenueLost(unitStatus)) {
            computeDaysVacantAndRevenueLost(unitStatus, now.getTime());
        }
        return unitStatus;
    }

    private static boolean isRevenueLost(final UnitAvailabilityStatus unit) {
        return unit.vacancyStatus().getValue() == Vacancy.Vacant & unit.vacantSince().getValue() != null;
    }

    /**
     * Due to performance considerations this method does the both things together to avoid unnecessary invocation of
     * {@link UnitAvailabilityStatusDTO#daysVacant()}.
     */
    private static void computeDaysVacantAndRevenueLost(final UnitAvailabilityStatus unit, final long reportTime) {

        final long MILLIS_IN_DAY = 1000L * 60L * 60L * 24L;
        final long avaialbleFrom = unit.vacantSince().getValue().getTime();
        final long millisecondsVacant = reportTime - avaialbleFrom;

        int daysVacant = (int) (millisecondsVacant / MILLIS_IN_DAY) + 1;
        unit.daysVacant().setValue(daysVacant);

        BigDecimal marketRent = unit.marketRent().getValue();

        MathContext ctx = new MathContext(2, RoundingMode.UP);
        if (marketRent != null && marketRent.compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal revenueLost = marketRent.multiply(new BigDecimal(daysVacant).divide(new BigDecimal(30), ctx), ctx);
            unit.revenueLost().setValue(revenueLost);
        }
    }

    private static interface StatusFilter {

        boolean isAcceptable(UnitAvailabilityStatus status);

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
