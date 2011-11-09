/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 12, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.server.preloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.essentials.server.csv.CSVLoad;
import com.pyx4j.essentials.server.csv.CSVReciver;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyReportEvent;
import com.propertyvista.domain.dashboard.gadgets.vacancyreport.UnitVacancyStatus;

public class MockupVacancyReportEventPreloader extends BaseVistaDevDataPreloader {
    private static final String DATA_SOURCE_FILE = "unit-vacancy-report-events.csv";

    private static final Random RND = new Random(9001l);

    private static final long MIN_EVENT_DELTA = 1000l * 60l * 60l * 24l; // one day

    private static final long MIN_STAY_IN_UNIT_DELTA = 1000l * 60l * 60l * 24l * 365l; // approx 1 year

    private static final long MAX_STAY_IN_UNIT_DELTA = 1000l * 60l * 60l * 24l * 365l * 3l; // approx 3 years

    private static final long MAX_STAY_AFTER_NOTICE = 1000l * 60l * 60l * 24l * 30l;

    private static final long MAX_WAIT_UNTIL_RENTED = 1000l * 60l * 60l * 24l * 90l; // approx 3 months

    private static final long MAX_WAIT_UNTIL_RENTSTART = 1000l * 60l * 60l * 24l * 30l; // approx 1 month

    private static final long MAX_WAIT_UNTIL_SCOPED = 1000l * 60l * 60l * 24l * 30l; // 30 DAYS

    private static final long MAX_WAIT_UNTIL_RENO_ENDS = 1000l * 60l * 60l * 24l * 7l;

    @Override
    public String create() {
        return generateRandom();
    }

    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(UnitVacancyReportEvent.class);
        } else {
            return "This is production";
        }
    }

    @SuppressWarnings("deprecation")
    private String generateRandom() {
        int eventsCount = 0;
        LogicalDate start = new LogicalDate();
        start.setYear(106);
        start.setMonth(0);
        start.setDate(1);

        LogicalDate end = new LogicalDate();

        UnitVacancyReportEvent event = EntityFactory.create(UnitVacancyReportEvent.class);
        event.eventDate().setValue(start);

        for (Key statusPk : getAvailableUnits()) {
            LogicalDate rentedFrom = newDate(start.getTime() + rand(MIN_EVENT_DELTA, MAX_WAIT_UNTIL_RENTSTART));
            do {

                // TODO add rent start without movein

                // create move in event
                event = EntityFactory.create(UnitVacancyReportEvent.class);
                LogicalDate moveinDate = rentedFrom;
                event.eventDate().setValue(moveinDate);
                event.eventType().setValue("movein");
                if (event.eventDate().getValue().getTime() > end.getTime())
                    break;
                persist(statusPk, event);
                ++eventsCount;

                // create notice event (if needed)
                event = EntityFactory.create(UnitVacancyReportEvent.class);
                boolean hasNotice = RND.nextBoolean();
                LogicalDate noticeDate = null;
                LogicalDate moveoutDate = null;
                if (hasNotice) {
                    moveoutDate = newDate(moveinDate.getTime() + rand(MIN_EVENT_DELTA + MAX_STAY_IN_UNIT_DELTA - MAX_STAY_AFTER_NOTICE, MAX_STAY_IN_UNIT_DELTA));
                    noticeDate = newDate(rand(moveoutDate.getTime() - MAX_STAY_AFTER_NOTICE, moveoutDate.getTime() - MIN_EVENT_DELTA));
                    event.eventDate().setValue(noticeDate);
                    event.eventType().setValue("notice");
                    event.moveOutDate().setValue(moveoutDate);
                    if (event.eventDate().getValue().getTime() > end.getTime())
                        break;
                    persist(statusPk, event);
                    ++eventsCount;
                }

                // maybe scoped event                
                boolean isScoped = RND.nextBoolean() & hasNotice;
                boolean isRenoNeeded = RND.nextBoolean() & RND.nextBoolean() & RND.nextBoolean();
                LogicalDate scopedDate = null;
                if (isScoped) {
                    scopedDate = newDate(rand(noticeDate.getTime() + MIN_EVENT_DELTA, moveoutDate.getTime() - MIN_EVENT_DELTA));

                    event = EntityFactory.create(UnitVacancyReportEvent.class);
                    event.eventDate().setValue(scopedDate);
                    event.eventType().setValue("scoped");
                    event.rentReady().setValue(isRenoNeeded ? "needs repairs" : "rentready");
                    if (event.eventDate().getValue().getTime() > end.getTime())
                        break;
                    persist(statusPk, event);
                    ++eventsCount;
                }

                // maybe rented event
                boolean isRented = RND.nextBoolean() & hasNotice & !isRenoNeeded;
                rentedFrom = null;
                LogicalDate rentedDate = null;
                if (isRented) {
                    rentedDate = newDate(rand(noticeDate.getTime() + MIN_EVENT_DELTA, moveoutDate.getTime()));
                    rentedFrom = newDate(moveoutDate.getTime() + rand(MIN_EVENT_DELTA, MAX_WAIT_UNTIL_RENTSTART));
                    event = EntityFactory.create(UnitVacancyReportEvent.class);
                    event.eventDate().setValue(rentedDate);
                    event.eventType().setValue("rented");
                    event.rentFromDate().setValue(rentedFrom);
                    if (event.eventDate().getValue().getTime() > end.getTime())
                        break;
                    persist(statusPk, event);
                    ++eventsCount;
                }

                // create move out event
                event = EntityFactory.create(UnitVacancyReportEvent.class);
                if (!hasNotice) {
                    moveoutDate = newDate(moveinDate.getTime() + rand(MIN_EVENT_DELTA + MIN_STAY_IN_UNIT_DELTA, MAX_STAY_IN_UNIT_DELTA));
                }
                event.eventDate().setValue(moveoutDate);
                event.eventType().setValue("moveout");
                if (event.eventDate().getValue().getTime() > end.getTime())
                    break;
                persist(statusPk, event);
                ++eventsCount;

                // scoped event if not scoped yet
                if (!isScoped) {
                    isRenoNeeded = RND.nextBoolean() & RND.nextBoolean() & RND.nextBoolean();
                    scopedDate = newDate(moveoutDate.getTime() + rand(MIN_EVENT_DELTA, MAX_WAIT_UNTIL_SCOPED));
                    event = EntityFactory.create(UnitVacancyReportEvent.class);
                    event.eventDate().setValue(scopedDate);
                    event.eventType().setValue("scoped");
                    event.rentReady().setValue(isRenoNeeded ? "needs repairs" : "rentready");
                    if (event.eventDate().getValue().getTime() > end.getTime())
                        break;
                    persist(statusPk, event);
                    ++eventsCount;
                }

                // TODO reno if needed -> off market event

                // rented event if not rented yet
                if (!isRented) {
                    if (isRenoNeeded) {
                        rentedDate = newDate(scopedDate.getTime() + rand(MIN_EVENT_DELTA, MAX_WAIT_UNTIL_RENO_ENDS));
                    } else {
                        rentedDate = newDate(moveoutDate.getTime() + rand(MIN_EVENT_DELTA, MAX_WAIT_UNTIL_RENTED));
                    }

                    rentedFrom = newDate(rentedDate.getTime() + rand(MIN_EVENT_DELTA, MAX_WAIT_UNTIL_RENTSTART));

                    event = EntityFactory.create(UnitVacancyReportEvent.class);
                    event.eventDate().setValue(rentedDate);
                    event.eventType().setValue("rented");
                    event.rentFromDate().setValue(rentedFrom);
                    if (event.eventDate().getValue().getTime() > end.getTime())
                        break;
                    persist(statusPk, event);
                    ++eventsCount;
                }
            } while (true);
        }
        return "Created " + eventsCount + " unit (apartment) events for Availability/Vacancy Gadgets";
    }

    private static void persist(Key unitId, UnitVacancyReportEvent event) {
        event.belongsTo().setPrimaryKey(unitId);
        Persistence.service().persist(event);
    }

    private static List<Key> getAvailableUnits() {
        EntityQueryCriteria<UnitVacancyStatus> criteria = new EntityQueryCriteria<UnitVacancyStatus>(UnitVacancyStatus.class);
        List<UnitVacancyStatus> statuses = Persistence.service().query(criteria);
        List<Key> unitPKs = new ArrayList<Key>();

        for (UnitVacancyStatus status : statuses) {
            unitPKs.add(status.getPrimaryKey());
        }

        return randomChoice(unitPKs, 40);
    }

    private static <T> List<T> randomChoice(List<T> list, int howMany) {
        List<T> copy = new ArrayList<T>(list);
        List<T> result = new ArrayList<T>(list);
        if (howMany >= list.size()) {
            return list;
        }
        int minIndex = list.size() - howMany;
        for (int lastIndex = list.size() - 1; lastIndex >= minIndex; --lastIndex) {
            int i = RND.nextInt(lastIndex + 1);
            result.add(copy.get(i));
            copy.set(i, copy.get(lastIndex));
        }
        return copy;

    }

    @SuppressWarnings("unused")
    private String preloadFromFile() {

        final StringBuilder creationReport = new StringBuilder();
        final HashMap<String, Integer> indexOf = new HashMap<String, Integer>();
        final int[] events = new int[] { 0 };

        try {
            CSVLoad.loadFile(IOUtils.resourceFileName(DATA_SOURCE_FILE, MockupVacancyReportUnitPreloader.class), new CSVReciver() {
                int headersLength = 0;

                @Override
                public void onRow(String[] rawValues) {
                    String[] values = new String[headersLength];
                    // make all the fields exist (even if some of them are null
                    int minLength = rawValues.length < headersLength ? rawValues.length : headersLength;
                    for (int i = 0; i < minLength; ++i) {
                        values[i] = rawValues[i];
                        if ("".equals(values[i])) {
                            values[i] = null;
                        }
                    }

                    UnitVacancyReportEvent event = EntityFactory.create(UnitVacancyReportEvent.class);

                    String strVal;

                    strVal = values[indexOf.get("date")];
                    event.eventDate().setValue(MockupVacancyReportUnitPreloader.toLogicalDate(strVal));

                    strVal = values[indexOf.get("propertyCode")];
                    event.propertyCode().setValue(strVal);

                    strVal = values[indexOf.get("unit#")];
                    event.unit().setValue(strVal);

                    strVal = values[indexOf.get("event")];
                    event.eventType().setValue(strVal);

                    strVal = values[indexOf.get("rentready")];
                    event.rentReady().setValue(strVal);

                    strVal = values[indexOf.get("move out date")];
                    event.moveOutDate().setValue(MockupVacancyReportUnitPreloader.toLogicalDate(strVal));

                    strVal = values[indexOf.get("move in date")];
                    event.moveInDate().setValue(MockupVacancyReportUnitPreloader.toLogicalDate(strVal));

                    strVal = values[indexOf.get("rent from date")];
                    event.moveInDate().setValue(MockupVacancyReportUnitPreloader.toLogicalDate(strVal));

                    Persistence.service().persist(event);
                    ++events[0];
                }

                @Override
                public void onHeader(String[] headers) {
                    headersLength = headers.length;
                    for (int i = 0; i < headers.length; i++) {
                        indexOf.put(headers[i], i);
                    }
                }

                @Override
                public boolean canContuneLoad() {
                    return true;
                }
            });

        } catch (Exception e) {
            creationReport.append("failed to fill UnitVacancyReportEvent due to ").append(e).append("; ");
        } finally {
            creationReport.append("Created ").append(events[0]).append(" mockup unit events (for UnitVacancyReportEvent)");
        }
        return creationReport.toString();
    }

    /** return x such that x >= min and x < max */
    private static long rand(long min, long max) {
        return Math.max(min, Math.abs(RND.nextLong()) % max);
    }

    private static LogicalDate newDate(long time) {
        LogicalDate date = new LogicalDate(time);
        return date;
    }

    private static class UnitId {
        String propertyCode;

        String unit;
    }

}
