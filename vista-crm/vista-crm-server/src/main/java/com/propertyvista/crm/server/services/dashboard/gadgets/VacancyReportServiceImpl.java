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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.rpc.EntitySearchResult;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.lister.EntityLister;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.criterion.EntityListCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.criterion.PropertyCriterion.Restriction;
import com.pyx4j.entity.shared.meta.MemberMeta;

import com.propertyvista.crm.rpc.services.dashboard.gadgets.VacancyReportService;
import com.propertyvista.domain.dashboard.gadgets.CustomComparator;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyStatus;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyStatus.RentReady;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyStatus.VacancyStatus;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportEvent;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportSummaryDTO;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportTurnoverAnalysisDTO;
import com.propertyvista.domain.dashboard.gadgets.UnitVacancyReportTurnoverAnalysisDTO.AnalysisResolution;

public class VacancyReportServiceImpl implements VacancyReportService {

    private static final Object TRANSIENT_PROPERTIES_MUTEX = new Object();

    private static TransientPropertySortEngine<UnitVacancyStatus> TRANSIENT_PROPERTY_SORT_ENGINE = null;

    @Override
    public void unitStatusList(AsyncCallback<EntitySearchResult<UnitVacancyStatus>> callback, Vector<String> buildings, LogicalDate from, LogicalDate to,
            Vector<Sort> sortingCriteria, int pageNumber, int pageSize) {

        EntitySearchResult<UnitVacancyStatus> units = null;

        // some of the fields in records are transient (in the IEntity sense), hence we have to extract them from the criteria and sorts the results for ourselves
        List<Sort> transientSortCriteria = null;
        transientSortCriteria = getTransientPropertySortEngine().extractSortCriteriaForTransientProperties(sortingCriteria);

        EntityListCriteria<UnitVacancyStatus> criteria = new EntityListCriteria<UnitVacancyStatus>(UnitVacancyStatus.class);
        criteria.setSorts(sortingCriteria);
        if (!buildings.isEmpty()) {
            // TODO add protection from SQL injection if in the future buildings are still represented as strings
            criteria.add(new PropertyCriterion(criteria.proto().propertyCode(), Restriction.IN, buildings));
        }

        if (!transientSortCriteria.isEmpty()) {
            // here we have to filter and sort all the data 'manually'
            // really fun and amazing stuff begins

            final ICursorIterator<UnitVacancyStatus> unfiltered = Persistence.service().query(null, criteria);

            PriorityQueue<UnitVacancyStatus> queue = new PriorityQueue<UnitVacancyStatus>(100, getTransientPropertySortEngine().getComparator(
                    transientSortCriteria));
            try {
                while (unfiltered.hasNext()) {
                    UnitVacancyStatus unit = unfiltered.next();
                    computeTransientFields(unit, from, to);
                    queue.add(unit);
                }
            } finally {
                unfiltered.completeRetrieval();
            }

            final int totalRows = queue.size();

            Vector<UnitVacancyStatus> unitsData = new Vector<UnitVacancyStatus>();
            int currentPage = 0;
            int currentPagePosition = 0;
            boolean hasMoreRows = false;
            while (!queue.isEmpty()) {
                UnitVacancyStatus unit = queue.poll();
                ++currentPagePosition;
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
            units = new EntitySearchResult<UnitVacancyStatus>();
            units.setData(unitsData);
            units.setTotalRows(totalRows);
            units.hasMoreData(hasMoreRows);
            //units.setEncodedCursorReference(?);
        } else {
            criteria.setPageSize(pageSize);
            criteria.setPageNumber(pageNumber);
            units = EntityLister.secureQuery(criteria);

            for (UnitVacancyStatus unit : units.getData()) {
                computeTransientFields(unit, from, to);
            }
        }

        callback.onSuccess(units);
    }

    @Override
    public void summary(AsyncCallback<UnitVacancyReportSummaryDTO> callback, Vector<String> buildings, LogicalDate fromDate, LogicalDate toDate) {
        if (buildings == null | fromDate == null | toDate == null) {
            callback.onFailure(new Error("one of the required arguments was not set."));
            return;
        }

        EntityQueryCriteria<UnitVacancyStatus> criteria = new EntityQueryCriteria<UnitVacancyStatus>(UnitVacancyStatus.class);
        if (!buildings.isEmpty()) {
            // TODO dependency injection check for buildings if they are still represented as strings or ask if that check performed on more deeper level
            criteria.add(new PropertyCriterion(criteria.proto().propertyCode(), Restriction.IN, buildings));
        }

        List<UnitVacancyStatus> units = Persistence.service().query(criteria);

        UnitVacancyReportSummaryDTO summary = EntityFactory.create(UnitVacancyReportSummaryDTO.class);

        long fromTime = fromDate.getTime();
        long toTime = toDate.getTime();

        int total = 0;

        int vacant = 0;
        int vacantRented = 0;

        int occupied = 0;

        int notice = 0;
        int noticeRented = 0;

        double netExposure = 0.0;

        for (UnitVacancyStatus unit : units) {
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

        if ((toTime - fromTime) / (resolution.addTo(fromTime) - fromTime) > MAX_SUPPORTED_INTERVALS) {
            callback.onFailure(new Error("the date range that was specified is too big"));
            return;
        }

        Vector<UnitVacancyReportTurnoverAnalysisDTO> result = new Vector<UnitVacancyReportTurnoverAnalysisDTO>();

        EntityQueryCriteria<UnitVacancyReportEvent> criteria = new EntityQueryCriteria<UnitVacancyReportEvent>(UnitVacancyReportEvent.class);
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

        List<UnitVacancyReportEvent> events = Persistence.service().query(criteria);
        // TODO consider using ordering of results by unitKey and then date (if possible) instead of hashmap 
        HashMap<String, Boolean> someoneMovedOut = new HashMap<String, Boolean>();

        long intervalStart = fromDate.getTime();
        long intervalEnd = resolution.addTo(intervalStart);
        int turnovers = 0;
        int total = 0;

        for (UnitVacancyReportEvent event : events) {
            long eventTime = event.eventDate().getValue().getTime();
            while (eventTime >= intervalEnd) {
                someoneMovedOut = new HashMap<String, Boolean>();
                UnitVacancyReportTurnoverAnalysisDTO analysis = EntityFactory.create(UnitVacancyReportTurnoverAnalysisDTO.class);
                analysis.fromDate().setValue(new LogicalDate(intervalStart));
                analysis.toDate().setValue(new LogicalDate(intervalEnd));
                analysis.unitsTurnedOverAbs().setValue(turnovers);
                result.add(analysis);

                intervalStart = intervalEnd;
                intervalEnd = resolution.addTo(intervalStart);
                turnovers = 0;
            }
            // FIXME Really Slow: hope there will be unique key someday !!!
            String unitKey = "" + event.propertyCode() + event.unit();
            String eventType = event.eventType().getValue();
            Boolean isMovedOut = someoneMovedOut.get(unitKey);
            if (isMovedOut == null && eventType.equals("moveout")) {
                someoneMovedOut.put(unitKey, Boolean.TRUE);
            } else if (isMovedOut == Boolean.TRUE & eventType.equals("movein")) {
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

        // now add some data if we don't have more events but still haven't reached till the end of the rest of time time 
        while (endReportTime >= intervalEnd) {
            UnitVacancyReportTurnoverAnalysisDTO analysis = EntityFactory.create(UnitVacancyReportTurnoverAnalysisDTO.class);
            analysis.fromDate().setValue(new LogicalDate(intervalStart));
            analysis.toDate().setValue(new LogicalDate(intervalEnd));
            analysis.unitsTurnedOverAbs().setValue(0);
            result.add(analysis);

            intervalStart = intervalEnd;
            intervalEnd = resolution.addTo(intervalStart);
        }

        for (UnitVacancyReportTurnoverAnalysisDTO analysis : result) {
            if (total > 0) {
                analysis.unitsTurnedOverPct().setValue(((double) analysis.unitsTurnedOverAbs().getValue()) / total * 100);
            } else {
                analysis.unitsTurnedOverPct().setValue(0d);
            }
        }

        callback.onSuccess(result);
    }

    private static void computeTransientFields(final UnitVacancyStatus unit, final LogicalDate fromDate, final LogicalDate toDate) {
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
    private static void computeState(final UnitVacancyStatus unit, final LogicalDate fromDate, final LogicalDate toDate) {
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

    private static void computeRentDelta(UnitVacancyStatus unit) {
        double unitMarketRent = unit.marketRent().getValue();
        double unitRent = unit.unitRent().getValue();

        double rentDeltaAbsoute = unitMarketRent - unitRent;
        double rentDeltaRelative = rentDeltaAbsoute / unitMarketRent * 100;

        unit.rentDeltaAbsolute().setValue(rentDeltaAbsoute);
        unit.rentDeltaRelative().setValue(rentDeltaRelative);
    }

    private static boolean isRevenueLost(final UnitVacancyStatus unit) {
        return VacancyStatus.Vacant.equals(unit.vacancyStatus().getValue()) & unit.moveOutDay().getValue() != null;
    }

    private static void computeDaysVacantAndRevenueLost(final UnitVacancyStatus unit, final long startOfTime, final long endOfTime) {
        long availableFrom = 1 + unit.moveOutDay().getValue().getTime();
        availableFrom = Math.max(startOfTime, availableFrom);
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

    private static TransientPropertySortEngine<UnitVacancyStatus> getTransientPropertySortEngine() {
        TransientPropertySortEngine<UnitVacancyStatus> sortEngine = TRANSIENT_PROPERTY_SORT_ENGINE;
        if (sortEngine == null) {
            synchronized (TRANSIENT_PROPERTIES_MUTEX) {
                if (sortEngine == null) {
                    sortEngine = TRANSIENT_PROPERTY_SORT_ENGINE = new TransientPropertySortEngine<UnitVacancyStatus>(UnitVacancyStatus.class);
                }
            }
        }
        return sortEngine;
    }

    /**
     * This is supposed to be thread safe sort engine/comparator factory for transient fields. Maybe I'm going to write detailed usage information when it works
     */
    public static class TransientPropertySortEngine<X extends IEntity> {
        @SuppressWarnings("rawtypes")
        private final Map<String, Comparator> transientProperties;

        @SuppressWarnings("rawtypes")
        public TransientPropertySortEngine(Class<X> clazz) {
            Map<String, Comparator> temp = new HashMap<String, Comparator>();
            IEntity proto = EntityFactory.getEntityPrototype(clazz);

            for (String memberName : proto.getEntityMeta().getMemberNames()) {
                String propertyName;
                Comparator propertyComparator = null;
                MemberMeta memberMeta = proto.getMember(memberName).getMeta();

                if (memberMeta.getAnnotation(Transient.class) != null) {
                    IObject<?> member = proto.getMember(memberName);
                    propertyName = member.getPath().toString();
                    CustomComparator customComparatorAnnotation = memberMeta.getAnnotation(CustomComparator.class);
                    if (customComparatorAnnotation != null) {
                        try {
                            propertyComparator = customComparatorAnnotation.clazz().newInstance();
                        } catch (InstantiationException e) {
                            // TODO do something (log maybe) (ask Vlad how to access the logger)
                        } catch (IllegalAccessException e) {
                            // TODO do something (log maybe) (ask Vlad how to access the logger)
                        }
                    }
                    temp.put(propertyName, propertyComparator);
                }
            }
            transientProperties = Collections.unmodifiableMap(temp);
        }

        public Comparator<? super X> getComparator(List<Sort> sortCriteria) {
            return new CombinedComparator(sortCriteria);
        }

        /**
         * Filter the given sorting criteria: remove all the criteria for transient fields and return them.
         * 
         * @param sortingCriteria
         *            list of criteria that is to be filtered
         * @return sorting criteria for transient members
         */
        public List<Sort> extractSortCriteriaForTransientProperties(List<Sort> sortingCriteria) {
            List<Sort> extractedSorts = new LinkedList<Sort>();

            Iterator<Sort> i = sortingCriteria.iterator();
            while (i.hasNext()) {
                Sort s = i.next();
                if (getTransientProperties().containsKey(s.getPropertyName())) {
                    i.remove();
                    extractedSorts.add(s);
                }
            }
            return extractedSorts;
        }

        @SuppressWarnings("rawtypes")
        public void sort(List<X> unsortedList, List<Sort> sortCriteria) {
            List<Sort> relevantSortCriteria = new LinkedList<Sort>();
            List<Comparator> comparators = new LinkedList<Comparator>();

            for (Sort sortCriterion : sortCriteria) {
                Comparator comparator = getTransientProperties().get(sortCriterion.getPropertyName());
                if (comparator != null) {
                    relevantSortCriteria.add(sortCriterion);
                    comparators.add(comparator);
                }
            }

            Collections.sort(unsortedList, getComparator(relevantSortCriteria));
        }

        @SuppressWarnings("rawtypes")
        public Map<String, Comparator> getTransientProperties() {
            return transientProperties;
        }

        private class CombinedComparator implements Comparator<X> {
            @SuppressWarnings("rawtypes")
            final List<Comparator> comps;

            final List<Sort> sortCriteria;

            @SuppressWarnings("rawtypes")
            public CombinedComparator(List<Sort> relevantSortCriteria) {
                comps = new LinkedList<Comparator>();
                sortCriteria = relevantSortCriteria;

                for (Sort sortCriterion : sortCriteria) {
                    final Comparator cmp = getTransientProperties().get(sortCriterion.getPropertyName());
                    if (cmp == null) {
                        // TODO maybe throw exception/log error (someone is trying to sort something that has no associated comparator)
                        continue;
                    }
                    if (!sortCriterion.isDescending()) {
                        comps.add(new Comparator() {

                            @SuppressWarnings("unchecked")
                            @Override
                            public int compare(Object paramT1, Object paramT2) {
                                return -cmp.compare(paramT1, paramT2);
                            }

                        });
                    } else {
                        comps.add(cmp);
                    }
                }
            }

            @SuppressWarnings("unchecked")
            @Override
            public int compare(X paramT1, X paramT2) {
                @SuppressWarnings("rawtypes")
                Iterator<Comparator> ci = comps.iterator();
                Iterator<Sort> si = sortCriteria.iterator();
                while (ci.hasNext() & si.hasNext()) {
                    Sort sortCriterion = si.next();
                    @SuppressWarnings("rawtypes")
                    Comparator cmp = ci.next();
                    Object val1 = paramT1.getMember(new Path(sortCriterion.getPropertyName())).getValue();
                    Object val2 = paramT2.getMember(new Path(sortCriterion.getPropertyName())).getValue();
                    int result = cmp.compare(val1, val2);
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        }
    }

}
