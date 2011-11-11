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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
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
import com.propertyvista.crm.server.util.TransientPropertySortEngine;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.VacancyStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatusDTO;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyReportSummaryDTO;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyReportTurnoverAnalysisDTO;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyReportTurnoverAnalysisDTO.AnalysisResolution;
import com.propertyvista.domain.property.asset.building.Building;

public class AvailabilityReportServiceImpl implements AvailabilityReportService {
    private static final Object TRANSIENT_PROPERTIES_MUTEX = new Object();

    private static TransientPropertySortEngine<UnitAvailabilityStatusDTO> TRANSIENT_PROPERTY_SORT_ENGINE = null;

    @Override
    public void unitStatusList(AsyncCallback<EntitySearchResult<UnitAvailabilityStatusDTO>> callback, Vector<Key> buildings, boolean displayOccupied,
            boolean displayVacant, boolean displayNotice, boolean displayRented, boolean displayNotRented, LogicalDate to, Vector<Sort> sortingCriteria,
            int pageNumber, int pageSize) {
        EntitySearchResult<UnitAvailabilityStatusDTO> units = null;

        if (to.equals(new LogicalDate(Long.MAX_VALUE))) {
            to = new LogicalDate();
        }
        try {
            // some of the fields in records are transient (in the IEntity sense), hence we have to extract them from the criteria and sorts the results for ourselves
            List<Sort> transientSortCriteria = null;
            transientSortCriteria = getTransientPropertySortEngine().extractSortCriteriaForTransientProperties(sortingCriteria);

            EntityListCriteria<UnitAvailabilityStatus> criteria = new EntityListCriteria<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);
            criteria.setSorts(sortingCriteria);

            if (!buildings.isEmpty()) {
                ArrayList<Building> buildingsList = new ArrayList<Building>();
                for (Key pk : buildings) {
                    Building building = Persistence.service().retrieve(Building.class, pk);
                    if (building != null) {
                        buildingsList.add(building);
                    }
                }
                criteria.add(new PropertyCriterion(criteria.proto().belongsTo().belongsTo(), Restriction.IN, buildingsList));
            }

            criteria.add(new PropertyCriterion(criteria.proto().statusDate(), Restriction.LESS_THAN_OR_EQUAL, to));
            // use descending order of the status date in order to select the most recent statuses first
            criteria.desc(criteria.proto().statusDate().getPath().toString());
            List<UnitAvailabilityStatus> unfiltered = Persistence.service().query(criteria);

            // +1 is for the times when size() == 0 since it's an illegal argument
            PriorityQueue<UnitAvailabilityStatusDTO> queue = new PriorityQueue<UnitAvailabilityStatusDTO>(unfiltered.size() + 1,
                    getTransientPropertySortEngine().getComparator(transientSortCriteria));

            // we use hash map to filter only unit statuses for the requested date 
            HashSet<Key> addedUnits = new HashSet<Key>(unfiltered.size());

            for (UnitAvailabilityStatus unitStatus : unfiltered) {
                Key unitPK = unitStatus.belongsTo().getPrimaryKey();
                if (!addedUnits.contains(unitPK)) {
                    queue.add(computeTransientFields(unitStatus, to));
                    addedUnits.add(unitPK);
                }
            }

            Vector<UnitAvailabilityStatusDTO> unitsData = new Vector<UnitAvailabilityStatusDTO>();

            int currentPage = 0;
            int currentPagePosition = 0;
            int totalRows = 0;
            boolean hasMoreRows = false;

            while (!queue.isEmpty()) {
                UnitAvailabilityStatusDTO unit = queue.poll();
                if (isAcceptable(unit, displayOccupied, displayVacant, displayNotice, displayRented, displayNotRented)) {
                    ++currentPagePosition;
                    ++totalRows;
                    if (currentPagePosition > pageSize) {
                        ++currentPage;
                        currentPagePosition = 1;
                    }
                    if (currentPage < pageNumber) {
                        continue;
                    } else if (currentPage == pageNumber) {
                        unitsData.add(unit);
                    } else {
                        hasMoreRows = true;
                        break;
                    }
                }
            }
            while (!queue.isEmpty()) {
                UnitAvailabilityStatusDTO unit = queue.poll();
                if (isAcceptable(unit, displayOccupied, displayVacant, displayNotice, displayRented, displayNotRented)) {
                    ++totalRows;
                }
            }

            units = new EntitySearchResult<UnitAvailabilityStatusDTO>();
            units.setData(unitsData);
            units.setTotalRows(totalRows);
            units.hasMoreData(hasMoreRows);

            callback.onSuccess(units);
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
    public void summary(AsyncCallback<UnitVacancyReportSummaryDTO> callback, Vector<Key> buildings, LogicalDate fromDate, LogicalDate toDate) {
        if (buildings == null | fromDate == null | toDate == null) {
            callback.onFailure(new Error("one of the required arguments was not set."));
            return;
        }

        EntityQueryCriteria<UnitAvailabilityStatus> criteria = new EntityQueryCriteria<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);
        if (!buildings.isEmpty()) {
            criteria.add(new PropertyCriterion(criteria.proto().buildingBelongsTo(), Restriction.IN, buildings));
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
        // so then it's basically: number of 'moveins' during specified interval minus 1        
        Vector<UnitVacancyReportTurnoverAnalysisDTO> result = new Vector<UnitVacancyReportTurnoverAnalysisDTO>();

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
            // FIXME this doesn't work, need to iterate over buildings and sum the results for each building
            criteria.add(new PropertyCriterion(criteria.proto().buildingBelongsTo(), Restriction.IN, buildings));
        }

        List<UnitAvailabilityStatus> statuses = Persistence.service().query(criteria);

        long intervalStart = fromDate == null ? 0 : fromDate.getTime();
        long intervalEnd = resolution.intervalEnd(intervalStart);
        int moveins = 0;
        int total = 0;

        Key unitPK = null;
        VacancyStatus prevVacancy = null;
        boolean dontSkipTheFirstEmptyRange = fromDate != null ? true : false;
        Iterator<UnitAvailabilityStatus> i = statuses.iterator();
        while (i.hasNext()) {
            UnitAvailabilityStatus status = i.next();
            long statusTime = status.statusDate().getValue().getTime();
            while (statusTime >= intervalEnd) {
                int turnovers = moveins > 0 ? moveins - 1 : 0;
                total += turnovers;
                moveins = 0;

                if (dontSkipTheFirstEmptyRange | total > 0) {
                    result.add(createIntervalStats(intervalStart, intervalEnd, turnovers));
                    intervalStart = intervalEnd;
                    intervalEnd = resolution.addTo(intervalStart);
                } else {
                    intervalStart = resolution.intervalStart(statusTime);
                    intervalEnd = resolution.addTo(intervalStart);
                }
            }

            VacancyStatus vacancy = status.vacancyStatus().getValue();
            Key thisUnitPK = status.belongsTo().getPrimaryKey();
            if (thisUnitPK.equals(unitPK)) {
                if (vacancy != prevVacancy) {
                    if (vacancy == null) {
                        ++moveins;
                    }
                    prevVacancy = vacancy;
                }
            } else {
                unitPK = thisUnitPK;
                prevVacancy = vacancy;
            }
        }

        // add last interval that collected some statistics if it did, or we have been explicitly asked for it 
        if ((total == 0) | (toDate != null)) {
            int turnovers = moveins > 0 ? moveins - 1 : 0;
            result.add(createIntervalStats(intervalStart, intervalEnd, turnovers));
            total += turnovers;
            intervalStart = intervalEnd;
            intervalEnd = resolution.addTo(intervalStart);

            // now add some more intervals (empty) if we were requested to show them
            if (toDate != null) {
                Long queryEndTime = toDate.getTime();
                while (intervalEnd <= queryEndTime) {
                    result.add(createIntervalStats(intervalStart, intervalEnd, 0));

                    intervalStart = intervalEnd;
                    intervalEnd = resolution.addTo(intervalStart);
                }
            }
        }

        if (total > 0) {
            for (UnitVacancyReportTurnoverAnalysisDTO intervalStats : result) {
                intervalStats.unitsTurnedOverPct().setValue(((double) intervalStats.unitsTurnedOverAbs().getValue()) / total * 100);
            }
        } else {
            for (UnitVacancyReportTurnoverAnalysisDTO intervalStats : result) {
                intervalStats.unitsTurnedOverPct().setValue(0d);
            }
        }

        callback.onSuccess(result);
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

    // FIXME this is redundant and unnecessary: don't remember why I did it, use static final instance instead 
    private static TransientPropertySortEngine<UnitAvailabilityStatusDTO> getTransientPropertySortEngine() {
        TransientPropertySortEngine<UnitAvailabilityStatusDTO> sortEngine = TRANSIENT_PROPERTY_SORT_ENGINE;
        if (sortEngine == null) {
            synchronized (TRANSIENT_PROPERTIES_MUTEX) {
                if (sortEngine == null) {
                    sortEngine = TRANSIENT_PROPERTY_SORT_ENGINE = new TransientPropertySortEngine<UnitAvailabilityStatusDTO>(UnitAvailabilityStatusDTO.class);
                }
            }
        }
        return sortEngine;
    }

    private static UnitVacancyReportTurnoverAnalysisDTO createIntervalStats(long intervalStart, long intervalEnd, int turnovers) {
        UnitVacancyReportTurnoverAnalysisDTO stats = EntityFactory.create(UnitVacancyReportTurnoverAnalysisDTO.class);
        stats.fromDate().setValue(new LogicalDate(intervalStart));
        stats.toDate().setValue(new LogicalDate(intervalEnd));
        stats.unitsTurnedOverAbs().setValue(turnovers);
        return stats;
    }
}
