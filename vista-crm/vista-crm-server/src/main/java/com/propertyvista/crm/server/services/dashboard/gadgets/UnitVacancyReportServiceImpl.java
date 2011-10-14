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
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.lister.EntityLister;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.Criterion;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;
import com.pyx4j.site.rpc.services.AbstractCrudService;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.UnitVacancyReportService;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReport;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReport.RentReady;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReport.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReport.VacancyStatus;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportEvent;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportSummaryDTO;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportTurnoverAnalysisDTO;

public class UnitVacancyReportServiceImpl implements UnitVacancyReportService {

//    private static final class Counter {
//        private int initialValue;
//
//        public Counter(int initialValue) {
//            this.initialValue = initialValue;
//        }
//
//        public Counter() {
//            this(0);
//        }
//
//        public void inc() {
//            ++initialValue;
//        }
//
//        public void dec() {
//            --initialValue;
//        }
//
//        public void assign(int newValue) {
//            initialValue = 0;
//        }
//
//        public int getValue() {
//            return initialValue;
//        }
//
//        @Override
//        public String toString() {
//            return Integer.toString(initialValue);
//        }
//
//    }

    @Override
    public void list(AsyncCallback<EntitySearchResult<UnitVacancyReport>> callback, EntityListCriteria<UnitVacancyReport> criteria) {

        LogicalDate[] dateConstraints = null;
        dateConstraints = extractDatesFromCriteria(criteria);
        if (dateConstraints == null) {
            callback.onFailure(new Exception("no date constrains provided"));
            return;
        }

        EntitySearchResult<UnitVacancyReport> unitResult = EntityLister.secureQuery(criteria);

        long toReportTime = dateConstraints[1].getTime();

        for (UnitVacancyReport unit : unitResult.getData()) {
            computeState(unit, dateConstraints[0], dateConstraints[1]);
            computeRentDelta(unit);
            if (isRevenueLost(unit)) {
                computeDaysVacantAndRevenueLost(unit, toReportTime);
            }
        }

        callback.onSuccess(unitResult);

    }

    @Override
    public void summary(AsyncCallback<UnitVacancyReportSummaryDTO> callback, EntityQueryCriteria<UnitVacancyReport> criteria, LogicalDate fromDate,
            LogicalDate toDate) {
        // TODO: think about using another type of query that returns something better than just a list
        extractDatesFromCriteria(criteria);
        List<UnitVacancyReport> units = Persistence.service().query(criteria);

        UnitVacancyReportSummaryDTO summary = EntityFactory.create(UnitVacancyReportSummaryDTO.class);

        long toTime = toDate.getTime();

        int total = 0;

        int vacant = 0;
        int vacantRented = 0;

        int occupied = 0;

        int notice = 0;
        int noticeRented = 0;

        double netExposure = 0.0;

        for (UnitVacancyReport unit : units) {
            ++total;
            computeState(unit, fromDate, toDate);

            VacancyStatus vacancyStatus = unit.vacancyStatus().getValue();

            // check that we have vacancy status, and don't waste the cpu cycles if we don't            
            if (vacancyStatus == null)
                continue;

            if (VacancyStatus.Vacant.equals(vacancyStatus)) {
                ++vacant;
                if (isRevenueLost(unit)) {
                    computeDaysVacantAndRevenueLost(unit, toTime);
                    netExposure += unit.revenueLost().getValue();
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

        summary.netExposure().setValue(netExposure);
        callback.onSuccess(summary);
    }

    // TODO update the interface
    // TODO maybe enum for specyfinying an interval? currently suppose 0 - months, 1 - years.
    // TODO interval 
    public void turnoverAnalysis(AsyncCallback<List<UnitVacancyReportTurnoverAnalysisDTO>> callback, LogicalDate fromDate, LogicalDate toDate, int intervalSize) {
        List<UnitVacancyReportTurnoverAnalysisDTO> result = new ArrayList<UnitVacancyReportTurnoverAnalysisDTO>();

        EntityQueryCriteria<UnitVacancyReportEvent> criteria = new EntityQueryCriteria<UnitVacancyReportEvent>(UnitVacancyReportEvent.class);

        criteria.add(new PropertyCriterion(criteria.proto().eventDate(), Restriction.GREATER_THAN_OR_EQUAL, fromDate));
        criteria.add(new PropertyCriterion(criteria.proto().eventDate(), Restriction.LESS_THAN, toDate));
        criteria.add(new PropertyCriterion(criteria.proto().eventType(), Restriction.EQUAL, "movein"));
        criteria.sort(new Sort(criteria.proto().eventDate().getPath().toString(), false));

        List<UnitVacancyReportEvent> events = Persistence.service().query(criteria);

        long intervalStart = fromDate.getTime();
        long intervalEnd = intervalEnd(intervalStart, intervalSize);
        int turnovers = 0;
        int overall = 0; // I don't use events.size() because it can be a linked list :(

        for (UnitVacancyReportEvent event : events) {
            long eventTime = event.eventDate().getValue().getTime();
            if (eventTime >= intervalEnd) {
                UnitVacancyReportTurnoverAnalysisDTO analysis = EntityFactory.create(UnitVacancyReportTurnoverAnalysisDTO.class);
                analysis.fromDate().setValue(new LogicalDate(intervalStart));
                analysis.toDate().setValue(new LogicalDate(intervalEnd));
                analysis.unitsTurnedOverAbs().setValue(turnovers);
                result.add(analysis);

                intervalStart = intervalEnd;
                intervalEnd = intervalEnd(intervalStart, intervalSize);
                turnovers = 0;
            }
            ++turnovers;
            ++overall;
        }
        if (overall > 0) {
            UnitVacancyReportTurnoverAnalysisDTO analysis = EntityFactory.create(UnitVacancyReportTurnoverAnalysisDTO.class);
            analysis.fromDate().setValue(new LogicalDate(intervalStart));
            analysis.toDate().setValue(new LogicalDate(intervalEnd));
            analysis.unitsTurnedOverAbs().setValue(turnovers);
            result.add(analysis);
        }
        for (UnitVacancyReportTurnoverAnalysisDTO analysis : result) {
            analysis.unitsTurnedOverPct().setValue((analysis.unitsTurnedOverPct().getValue() / overall));
        }

    }

    private static long intervalEnd(long startTime, int intervalSize) {
        // TODO think if there could be issues with locale and time zone
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(startTime);
        if (intervalSize == 0) {
            // add month
            c.set(Calendar.MONTH, (c.get(Calendar.MONTH) + 1) % 12);
        } else {
            // add year
            c.set(Calendar.YEAR, c.get(Calendar.YEAR));
        }
        return c.getTimeInMillis();
    }

    /**
     * Set unit state as it should be on specified date deducting it from the event table (creepy demo mode implementation)
     * 
     * @param unit
     * @param fromDate
     * @param toDate
     */
    private static void computeState(UnitVacancyReport unit, LogicalDate fromDate, LogicalDate toDate) {
        // I miss SQL :`(
        // TODO: this procedure is real waste of CPU cycles!!! think about better implementation!
        // consider the following:
        //      - maybe its worth to limit a query (i.e. we need at most 10 events (maybe even less)
        //      - real implementation should probably have a real key for the unit - no need to use ('propertyCode', 'unitNumber') as key.
        EntityQueryCriteria<UnitVacancyReportEvent> criteria = new EntityQueryCriteria<UnitVacancyReportEvent>(UnitVacancyReportEvent.class);

        String unitNumber = unit.unit().getValue();
        String propertyCode = unit.propertyCode().getValue();

        criteria.add(new PropertyCriterion(criteria.proto().eventDate(), Restriction.LESS_THAN_OR_EQUAL, toDate));
        criteria.add(new PropertyCriterion(criteria.proto().propertyCode(), Restriction.EQUAL, propertyCode));
        criteria.add(new PropertyCriterion(criteria.proto().unit(), Restriction.EQUAL, unitNumber));
        criteria.sort(new Sort(criteria.proto().eventDate().getPath().toString(), true));

        List<UnitVacancyReportEvent> events = Persistence.service().query(criteria);

        // assumptions:
        //      moved in state is the initial state of a unit;
        //      no more than one event on the same day;
        //      'reno in progress' automatically renders "rentedStatus" as 'off market';

        // accumulate events until the most recent "movein" event, then compute the state based on them        
        Stack<UnitVacancyReportEvent> eventStack = new Stack<UnitVacancyReportEvent>();

        for (UnitVacancyReportEvent event : events) {
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
            UnitVacancyReportEvent event = eventStack.pop();

            String eventType = event.eventType().getValue();

            if ("notice".equals(eventType)) {
                unit.vacancyStatus().setValue(VacancyStatus.Notice);
                unit.rentedStatus().setValue(RentedStatus.Unrented);
                unit.isScoped().setValue(false);
                unit.moveOutDay().setValue(event.moveOutDate().getValue());
            } else if ("scoped".equals(eventType)) {
                unit.isScoped().setValue(true);

                String strVal = event.rentReady().getValue();
                RentReady rentReady = rentReadyValueOf(strVal);
                unit.rentReady().setValue(rentReady);

                if (RentReady.RenoInProgress.equals(rentReady)) {
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

    private static void computeRentDelta(UnitVacancyReport unit) {
        double unitMarketRent = unit.marketRent().getValue();
        double unitRent = unit.unitRent().getValue();

        double rentDeltaAbsoute = unitRent - unitMarketRent;
        double rentDeltaRelative = rentDeltaAbsoute / unitMarketRent * 100;

        unit.rentDeltaAbsolute().setValue(rentDeltaAbsoute);
        unit.rentDeltaRelative().setValue(rentDeltaRelative);
    }

    private static boolean isRevenueLost(UnitVacancyReport unit) {
        return VacancyStatus.Vacant.equals(unit.vacancyStatus().getValue()) & unit.moveOutDay().getValue() != null;
    }

    private static void computeDaysVacantAndRevenueLost(UnitVacancyReport unit, final long endOfTime) {
        // TODO if we have start of time : do we have to count the days vacant from the startoftime or the possible history? 
        long availableFrom = 1 + unit.moveOutDay().getValue().getTime();
        long millisecondsVacant = endOfTime - availableFrom;

        int daysVacant = (int) (millisecondsVacant / (1000 * 60 * 60 * 24)); // some really heavy math :)            
        unit.daysVacant().setValue(daysVacant);

        double unitMarketRent = unit.marketRent().getValue();
        double revenueLost = daysVacant * unitMarketRent / 30.0;
        unit.revenueLost().setValue(revenueLost);
    }

    private static RentReady rentReadyValueOf(String strVal) {
        return "rentready".equals(strVal) ? RentReady.RentReady : "need repairs".equals(strVal) ? RentReady.NeedRepairs
                : "reno in progress".equals(strVal) ? RentReady.RenoInProgress : null;
    }

    /**
     * VICIOUS HACK in order to be able to use ListerBase (it has to use {@link AbstractCrudService} in order to function :( )
     * 
     * @param criteria
     * @return tuple of dates in from <code>{fromDate, startDate}</code>, or <code>null</code> if criteria doesn't contain at least on of the dates
     */
    private static LogicalDate[] extractDatesFromCriteria(EntityQueryCriteria<UnitVacancyReport> criteria) {

        LogicalDate fromReportDate = null;
        LogicalDate toReportDate = null;
        List<Criterion> criterionList = criteria.getFilters();
        List<Criterion> filteredList = new LinkedList<Criterion>();

        if (criterionList == null)
            return null;

        for (Criterion c : criterionList) {
            PropertyCriterion pc = (PropertyCriterion) c;
            if (pc.getPropertyName().endsWith("fromDate/")) {
                fromReportDate = (LogicalDate) pc.getValue();
            } else if (pc.getPropertyName().endsWith("toDate/")) {
                toReportDate = (LogicalDate) pc.getValue();
            } else {
                filteredList.add(pc);
            }
        }
        criteria.getFilters().clear();

        for (Criterion c : filteredList) {
            criteria.add(c);
        }

        if (fromReportDate == null | toReportDate == null) {
            return null;
        } else {
            return new LogicalDate[] { fromReportDate, toReportDate };
        }
    }

    @Override
    public void delete(AsyncCallback<Boolean> callback, Key entityId) {
        // Not Used
    }

}
