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
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;
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
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.MockupAvailabilityReportEvent;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitAvailabilityStatus.RentReadinessStatus;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitAvailabilityStatus.VacancyStatus;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitAvailabilityStatusDTO;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyReportSummaryDTO;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyReportTurnoverAnalysisDTO;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyReportTurnoverAnalysisDTO.AnalysisResolution;

public class AvailabilityReportServiceImpl implements AvailabilityReportService {

    private static final Object TRANSIENT_PROPERTIES_MUTEX = new Object();

    private static TransientPropertySortEngine<UnitAvailabilityStatusDTO> TRANSIENT_PROPERTY_SORT_ENGINE = null;

    @Override
    public void unitStatusList(AsyncCallback<EntitySearchResult<UnitAvailabilityStatusDTO>> callback, Vector<Key> buildings, boolean displayOccupied,
            boolean displayVacant, boolean displayNotice, boolean displayRented, boolean displayNotRented, LogicalDate from, LogicalDate to,
            Vector<Sort> sortingCriteria, int pageNumber, int pageSize) {
        EntitySearchResult<UnitAvailabilityStatusDTO> units = null;
        try {
            // some of the fields in records are transient (in the IEntity sense), hence we have to extract them from the criteria and sorts the results for ourselves
            List<Sort> transientSortCriteria = null;
            transientSortCriteria = getTransientPropertySortEngine().extractSortCriteriaForTransientProperties(sortingCriteria);

            EntityListCriteria<UnitAvailabilityStatusDTO> criteria = new EntityListCriteria<UnitAvailabilityStatusDTO>(UnitAvailabilityStatusDTO.class);
            criteria.setSorts(sortingCriteria);

            //TODO THIS IS VERY BAD. Do not use Strings Use Keys
            if (!buildings.isEmpty()) {
                // TODO add protection from SQL injection if in the future buildings are still represented as strings
                criteria.add(new PropertyCriterion(criteria.proto().propertyCode(), Restriction.IN, buildings));
            }

            List<UnitAvailabilityStatusDTO> unfiltered = Persistence.service().query(criteria);

            PriorityQueue<UnitAvailabilityStatusDTO> queue = new PriorityQueue<UnitAvailabilityStatusDTO>(100, getTransientPropertySortEngine().getComparator(
                    transientSortCriteria));

            for (UnitAvailabilityStatusDTO unit : unfiltered) {
                computeTransientFields(unit, from, to);
                queue.add(unit);
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

//        VacancyStatus vacancyStatus = unit.vacancyStatus().getValue();
//        RentedStatus rentedStatus = unit.rentedStatus().getValue();
//
//        return (displayOccupied & vacancyStatus == null) //
//                | ((displayVacant & vacancyStatus == VacancyStatus.Vacant) & ((displayRented & rentedStatus == RentedStatus.Rented) | (displayNotRented & rentedStatus != RentedStatus.Rented))) //
//                | ((displayNotice & vacancyStatus == VacancyStatus.Notice) & ((displayRented & rentedStatus == RentedStatus.Rented) | (displayNotRented & rentedStatus != RentedStatus.Rented)));

        return true;
    }

    @Override
    public void summary(AsyncCallback<UnitVacancyReportSummaryDTO> callback, Vector<String> buildings, LogicalDate fromDate, LogicalDate toDate) {
        if (buildings == null | fromDate == null | toDate == null) {
            callback.onFailure(new Error("one of the required arguments was not set."));
            return;
        }

        EntityQueryCriteria<UnitAvailabilityStatusDTO> criteria = new EntityQueryCriteria<UnitAvailabilityStatusDTO>(UnitAvailabilityStatusDTO.class);
        if (!buildings.isEmpty()) {
            // TODO dependency injection check for buildings if they are still represented as strings or ask if that check performed on more deeper level
            criteria.add(new PropertyCriterion(criteria.proto().propertyCode(), Restriction.IN, buildings));
        }

        List<UnitAvailabilityStatusDTO> units = Persistence.service().query(criteria);

        UnitVacancyReportSummaryDTO summary = EntityFactory.create(UnitVacancyReportSummaryDTO.class);

        long fromTime = fromDate.getTime();
        long toTime = toDate.getTime();

        int total = 0;

        int vacant = 0;
        int vacantRented = 0;

        int occupied = 0;

        int notice = 0;
        int noticeRented = 0;

        int netExposure = 0;

        for (UnitAvailabilityStatusDTO unit : units) {
            ++total;
            computeState(unit, fromDate, toDate);

            VacancyStatus vacancyStatus = unit.vacancyStatus().getValue();

            // check that we have vacancy status, and don't waste the cpu cycles if we don't have it            
            if (vacancyStatus == null)
                continue;

            if (VacancyStatus.Vacant.equals(vacancyStatus)) {
                ++vacant;
                if (isRevenueLost(unit)) {
                    computeDaysVacantAndRevenueLost(unit, fromTime, toTime);
                }
                if (RentedStatus.Rented.equals(unit.rentedStatus().getValue())) {
                    ++vacantRented;
                }
            } else if (VacancyStatus.Notice.equals(vacancyStatus)) {
                ++notice;
                if (RentedStatus.Rented.equals(unit.rentedStatus().getValue())) {
                    ++noticeRented;
                }
            }
        }

        summary.total().setValue(total);

        summary.vacancyAbsolute().setValue(vacant);
        summary.vacancyRelative().setValue(vacant / ((double) total) * 100);
        summary.vacantRented().setValue(vacantRented);

        occupied = total - vacant;
        summary.occupancyAbsolute().setValue(occupied);
        summary.occupancyRelative().setValue(occupied / ((double) total) * 100);

        summary.noticeAbsolute().setValue(notice);
        summary.noticeRelative().setValue(notice / ((double) total) * 100);
        summary.noticeRented().setValue(noticeRented);

        netExposure = vacant + notice - (vacantRented + noticeRented);
        summary.netExposureAbsolute().setValue(netExposure);
        summary.netExposureRelative().setValue(netExposure / (double) total);
        callback.onSuccess(summary);
    }

    @Override
    public void turnoverAnalysis(AsyncCallback<Vector<UnitVacancyReportTurnoverAnalysisDTO>> callback, Vector<String> buildings, LogicalDate fromDate,
            LogicalDate toDate, AnalysisResolution resolution) {
        // FIXME refactor this one: separate generic aggregation and intervals creation from the actual computations
        if (callback == null | fromDate == null || toDate == null | resolution == null) {
            callback.onFailure(new Error("at least one of the required parameters is null."));
            return;
        }
        final long fromTime = fromDate.getTime();
        final long toTime = toDate.getTime();

        if (fromTime > toTime) {
            callback.onFailure(new Error("end date is greater than from date."));
            return;
        }

        // FIXME add DOS protection based on calculation timeout rather than range and "SUPPORTED INTERVALS", or just report error if too many intervals have been created

        Vector<UnitVacancyReportTurnoverAnalysisDTO> result = new Vector<UnitVacancyReportTurnoverAnalysisDTO>();

        EntityQueryCriteria<MockupAvailabilityReportEvent> criteria = new EntityQueryCriteria<MockupAvailabilityReportEvent>(
                MockupAvailabilityReportEvent.class);
        if (!buildings.isEmpty()) {
            criteria.add(new PropertyCriterion(criteria.proto().propertyCode(), Restriction.IN, buildings));
        }
        criteria.add(new PropertyCriterion(criteria.proto().eventDate(), Restriction.GREATER_THAN_OR_EQUAL, fromDate));
        criteria.add(new PropertyCriterion(criteria.proto().eventDate(), Restriction.LESS_THAN, toDate));

        ArrayList<String> eventsFilter = new ArrayList<String>();
        eventsFilter.add("movein");
        eventsFilter.add("moveout");
        criteria.add(new PropertyCriterion(criteria.proto().eventType(), Restriction.IN, eventsFilter));
        criteria.sort(new Sort(criteria.proto().eventDate().getPath().toString(), false));

        List<MockupAvailabilityReportEvent> events = Persistence.service().query(criteria);
        // TODO consider using ordering of results by unitKey and then date (if possible) instead of hashmap 
        HashMap<String, Boolean> someoneMovedOut = new HashMap<String, Boolean>();

        long intervalStart = fromDate.getTime();
        long intervalEnd = resolution.intervalEnd(intervalStart);
        int turnovers = 0;
        int total = 0;
        boolean isFirstEmptyRange = true;

        for (MockupAvailabilityReportEvent event : events) {
            long eventTime = event.eventDate().getValue().getTime();
            while (eventTime >= intervalEnd) {
                if (!isFirstEmptyRange) {
                    someoneMovedOut = new HashMap<String, Boolean>();
                    UnitVacancyReportTurnoverAnalysisDTO analysis = EntityFactory.create(UnitVacancyReportTurnoverAnalysisDTO.class);
                    analysis.fromDate().setValue(new LogicalDate(intervalStart));
                    analysis.toDate().setValue(new LogicalDate(intervalEnd));
                    analysis.unitsTurnedOverAbs().setValue(turnovers);
                    result.add(analysis);
                    turnovers = 0;
                    intervalStart = intervalEnd;
                    intervalEnd = resolution.addTo(intervalStart);
                } else {
                    // skip all the first intervals that do not contain events
                    // TODO optimization: the following two lines must skip to the interval that contains the event
                    intervalStart = intervalEnd;
                    intervalEnd = resolution.addTo(intervalStart);
                }
            }
            // FIXME Really Slow: hope there will be unique key someday !!!
            String unitKey = "" + event.propertyCode() + event.unit();
            String eventType = event.eventType().getValue();
            Boolean isMovedOut = someoneMovedOut.get(unitKey);
            if (isMovedOut == null && eventType.equals("moveout")) {
                someoneMovedOut.put(unitKey, Boolean.TRUE);
            } else if (isMovedOut == Boolean.TRUE & eventType.equals("movein")) {
                isFirstEmptyRange = false; // TODO optimize this: separate into two loops
                ++turnovers;
                ++total;
            }
        }

        final long endReportTime = toDate.getTime();
        intervalEnd = endReportTime > intervalEnd ? intervalEnd : endReportTime;

        UnitVacancyReportTurnoverAnalysisDTO lastAnalysis = EntityFactory.create(UnitVacancyReportTurnoverAnalysisDTO.class);
        lastAnalysis.fromDate().setValue(new LogicalDate(intervalStart));
        lastAnalysis.toDate().setValue(new LogicalDate(intervalEnd));
        lastAnalysis.unitsTurnedOverAbs().setValue(turnovers);
        result.add(lastAnalysis);

        intervalStart = intervalEnd;
        intervalEnd = resolution.addTo(intervalStart);

        if (!isFirstEmptyRange) {
            // now add some more intervals if we don't have more events but still haven't reached till the end of the requested report time range 
            while (endReportTime >= intervalEnd) {

                UnitVacancyReportTurnoverAnalysisDTO analysis = EntityFactory.create(UnitVacancyReportTurnoverAnalysisDTO.class);
                analysis.fromDate().setValue(new LogicalDate(intervalStart));
                analysis.toDate().setValue(new LogicalDate(intervalEnd));
                analysis.unitsTurnedOverAbs().setValue(0);
                result.add(analysis);

                intervalStart = intervalEnd;
                intervalEnd = resolution.addTo(intervalStart);
            }
        }

        if (total > 0) {
            for (UnitVacancyReportTurnoverAnalysisDTO analysis : result) {
                analysis.unitsTurnedOverPct().setValue(((double) analysis.unitsTurnedOverAbs().getValue()) / total * 100);
            }
        } else {
            for (UnitVacancyReportTurnoverAnalysisDTO analysis : result) {
                analysis.unitsTurnedOverPct().setValue(0d);
            }
        }

        callback.onSuccess(result);
    }

    private static void computeTransientFields(final UnitAvailabilityStatusDTO unit, final LogicalDate fromDate, final LogicalDate toDate) {
        computeState(unit, fromDate, toDate);
        computeRentDelta(unit);
        if (isRevenueLost(unit)) {
            computeDaysVacantAndRevenueLost(unit, fromDate.getTime(), toDate.getTime());
        }
    }

    /**
     * Set unit state as it should be on specified date deducting it from the event table (creepy demo mode implementation)
     * 
     * @param unit
     * @param fromDate
     * @param toDate
     */
    private static void computeState(final UnitAvailabilityStatusDTO unit, final LogicalDate fromDate, final LogicalDate toDate) {
        // TODO: this procedure is real waste of CPU cycles!!! think about better implementation!
        // consider the following:
        //      - maybe its worth to limit a query (i.e. we need at most 10 events (maybe even less)
        //      - real implementation should probably have a real key for the unit - no need to use ('propertyCode', 'unitNumber') as key.
        EntityQueryCriteria<MockupAvailabilityReportEvent> criteria = new EntityQueryCriteria<MockupAvailabilityReportEvent>(
                MockupAvailabilityReportEvent.class);

        String unitNumber = unit.unit().getValue();
        String propertyCode = unit.propertyCode().getValue();

        criteria.add(new PropertyCriterion(criteria.proto().eventDate(), Restriction.LESS_THAN_OR_EQUAL, toDate));
        criteria.add(new PropertyCriterion(criteria.proto().propertyCode(), Restriction.EQUAL, propertyCode));
        criteria.add(new PropertyCriterion(criteria.proto().unit(), Restriction.EQUAL, unitNumber));
        criteria.sort(new Sort(criteria.proto().eventDate().getPath().toString(), true));

        List<MockupAvailabilityReportEvent> events = Persistence.service().query(criteria);

        // assumptions:
        //      moved in state is the initial state of a unit;
        //      no more than one event on the same day;
        //      'reno in progress' automatically renders "rentedStatus" as 'off market';

        // accumulate events until the most recent "movein" event, then compute the state based on them        
        Stack<MockupAvailabilityReportEvent> eventStack = new Stack<MockupAvailabilityReportEvent>();

        for (MockupAvailabilityReportEvent event : events) {
            String eventType = event.eventType().getValue();
            // TODO: remove this check once the table is full;
            if (eventType == null) {
                continue;
            }

            if ("movein".equals(eventType)) {
                break;
            }
            eventStack.push(event);
        }

        while (!eventStack.isEmpty()) {
            MockupAvailabilityReportEvent event = eventStack.pop();

            String eventType = event.eventType().getValue();

            if ("notice".equals(eventType)) {
                unit.vacancyStatus().setValue(VacancyStatus.Notice);
                unit.rentedStatus().setValue(RentedStatus.Unrented);
                unit.isScoped().setValue(false);
                unit.moveOutDay().setValue(event.moveOutDate().getValue());
            } else if ("scoped".equals(eventType)) {
                unit.isScoped().setValue(true);

                String strVal = event.rentReady().getValue();
                RentReadinessStatus rentReady = rentReadyValueOf(strVal);
                unit.rentReady().setValue(rentReady);

                if (RentReadinessStatus.RenoInProgress.equals(rentReady)) {
                    unit.rentedStatus().setValue(RentedStatus.OffMarket);
                }

            } else if ("moveout".equals(eventType)) {
                unit.moveOutDay().setValue(event.eventDate().getValue());
                unit.vacancyStatus().setValue(VacancyStatus.Vacant);
                if (unit.rentedStatus().isNull()) {
                    unit.rentedStatus().setValue(RentedStatus.Unrented);
                }
                if (unit.isScoped().isNull()) {
                    unit.isScoped().setValue(false);
                }

            } else if ("vacant".equals(eventType)) {
                unit.moveOutDay().setValue(event.moveOutDate().getValue());
                unit.vacancyStatus().setValue(VacancyStatus.Vacant);

            } else if ("rented".equals(eventType)) {
                unit.rentedStatus().setValue(RentedStatus.Rented);
                unit.moveInDay().setValue(event.moveInDate().getValue());
                unit.rentedFromDate().setValue(event.rentFromDate().getValue());

            }
        } // while
    }

    private static void computeRentDelta(UnitAvailabilityStatusDTO unit) {
        double unitMarketRent = unit.marketRent().getValue();
        double unitRent = unit.unitRent().getValue();

        double rentDeltaAbsoute = unitMarketRent - unitRent;
        double rentDeltaRelative = rentDeltaAbsoute / unitMarketRent * 100;

        unit.rentDeltaAbsolute().setValue(rentDeltaAbsoute);
        unit.rentDeltaRelative().setValue(rentDeltaRelative);
    }

    private static boolean isRevenueLost(final UnitAvailabilityStatusDTO unit) {
        return VacancyStatus.Vacant.equals(unit.vacancyStatus().getValue()) & unit.moveOutDay().getValue() != null;
    }

    private static void computeDaysVacantAndRevenueLost(final UnitAvailabilityStatusDTO unit, final long startOfTime, final long endOfTime) {
        long availableFrom = 1 + unit.moveOutDay().getValue().getTime();
        availableFrom = Math.max(startOfTime, availableFrom);
        long millisecondsVacant = endOfTime - availableFrom;

        int daysVacant = (int) (millisecondsVacant / (1000 * 60 * 60 * 24)); // some really heavy math :)            
        unit.daysVacant().setValue(daysVacant);

        double unitMarketRent = unit.marketRent().getValue();
        double revenueLost = daysVacant * unitMarketRent / 30.0;
        unit.revenueLost().setValue(revenueLost);
    }

    private static RentReadinessStatus rentReadyValueOf(String strVal) {
        return "rentready".equals(strVal) ? RentReadinessStatus.RentReady : "needs repairs".equals(strVal) ? RentReadinessStatus.NeedsRepairs
                : "reno in progress".equals(strVal) ? RentReadinessStatus.RenoInProgress : null;
    }

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

}
