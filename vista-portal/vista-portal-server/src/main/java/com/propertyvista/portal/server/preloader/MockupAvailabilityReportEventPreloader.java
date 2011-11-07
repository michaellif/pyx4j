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
import java.util.List;
import java.util.Random;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.dashboard.gadgets.vacancyreport.MockupAvailabilityReportEvent;
import com.propertyvista.domain.property.PropertyManager;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class MockupAvailabilityReportEventPreloader extends BaseVistaDevDataPreloader {
    private static final Random RND = new Random(9001l);

    private static final long MIN_EVENT_DELTA = 1000l * 60l * 60l * 24l; // one day

    private static final long MAX_STAY_IN_UNIT_DELTA = 1000l * 60l * 60l * 24l * 365l; // approx 1 year

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
            return deleteAll(MockupAvailabilityReportEvent.class);
        } else {
            return "This is production";
        }
    }

    @SuppressWarnings("deprecation")
    private String generateRandom() {
        if ((config().minimizePreloadTime)) {
            return null;
        }
        int eventsCount = 0;
        LogicalDate start = new LogicalDate();
        start.setYear(106);
        start.setMonth(0);
        start.setDate(1);

        LogicalDate end = new LogicalDate();

        MockupAvailabilityReportEvent event = EntityFactory.create(MockupAvailabilityReportEvent.class);
        event.eventDate().setValue(start);

        for (AptUnit unit : getAvailableUnits()) {
            LogicalDate rentedFrom = newDate(start.getTime() + rand(MIN_EVENT_DELTA, MAX_WAIT_UNTIL_RENTSTART));
            do {

                // TODO add rent start without movein

                // create move in event
                event = EntityFactory.create(MockupAvailabilityReportEvent.class);
                LogicalDate moveinDate = rentedFrom;
                event.eventDate().setValue(moveinDate);
                event.eventType().setValue("movein");
                if (event.eventDate().getValue().getTime() > end.getTime())
                    break;
                persist(unit, event);
                ++eventsCount;

                // create notice event (if needed)
                event = EntityFactory.create(MockupAvailabilityReportEvent.class);
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
                    persist(unit, event);
                    ++eventsCount;
                }

                // maybe scoped event                
                boolean isScoped = RND.nextBoolean() & hasNotice;
                boolean isRenoNeeded = RND.nextBoolean() & RND.nextBoolean() & RND.nextBoolean();
                LogicalDate scopedDate = null;
                if (isScoped) {
                    scopedDate = newDate(rand(noticeDate.getTime() + MIN_EVENT_DELTA, moveoutDate.getTime() - MIN_EVENT_DELTA));

                    event = EntityFactory.create(MockupAvailabilityReportEvent.class);
                    event.eventDate().setValue(scopedDate);
                    event.eventType().setValue("scoped");
                    event.rentReady().setValue(isRenoNeeded ? "needs repairs" : "rentready");
                    if (event.eventDate().getValue().getTime() > end.getTime())
                        break;
                    persist(unit, event);
                    ++eventsCount;
                }

                // maybe rented event
                boolean isRented = RND.nextBoolean() & hasNotice & !isRenoNeeded;
                rentedFrom = null;
                LogicalDate rentedDate = null;
                if (isRented) {
                    rentedDate = newDate(rand(noticeDate.getTime() + MIN_EVENT_DELTA, moveoutDate.getTime()));
                    rentedFrom = newDate(moveoutDate.getTime() + rand(MIN_EVENT_DELTA, MAX_WAIT_UNTIL_RENTSTART));
                    event = EntityFactory.create(MockupAvailabilityReportEvent.class);
                    event.eventDate().setValue(rentedDate);
                    event.eventType().setValue("rented");
                    event.rentFromDate().setValue(rentedFrom);
                    if (event.eventDate().getValue().getTime() > end.getTime())
                        break;
                    persist(unit, event);
                    ++eventsCount;
                }

                // create move out event
                event = EntityFactory.create(MockupAvailabilityReportEvent.class);
                if (!hasNotice) {
                    moveoutDate = newDate(moveinDate.getTime() + rand(MIN_EVENT_DELTA, MAX_STAY_IN_UNIT_DELTA));
                }
                event.eventDate().setValue(moveoutDate);
                event.eventType().setValue("moveout");
                if (event.eventDate().getValue().getTime() > end.getTime())
                    break;
                persist(unit, event);
                ++eventsCount;

                // scoped event if not scoped yet
                if (!isScoped) {
                    isRenoNeeded = RND.nextBoolean() & RND.nextBoolean() & RND.nextBoolean();
                    scopedDate = newDate(moveoutDate.getTime() + rand(MIN_EVENT_DELTA, MAX_WAIT_UNTIL_SCOPED));
                    event = EntityFactory.create(MockupAvailabilityReportEvent.class);
                    event.eventDate().setValue(scopedDate);
                    event.eventType().setValue("scoped");
                    event.rentReady().setValue(isRenoNeeded ? "needs repairs" : "rentready");
                    if (event.eventDate().getValue().getTime() > end.getTime())
                        break;
                    persist(unit, event);
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

                    event = EntityFactory.create(MockupAvailabilityReportEvent.class);
                    event.eventDate().setValue(rentedDate);
                    event.eventType().setValue("rented");
                    event.rentFromDate().setValue(rentedFrom);
                    if (event.eventDate().getValue().getTime() > end.getTime())
                        break;
                    persist(unit, event);
                    ++eventsCount;
                }
            } while (true);
        }
        return "Created " + eventsCount + " mockup availability events for Availability Gadgets";
    }

    private static void persist(AptUnit unit, MockupAvailabilityReportEvent event) {
        event.belongsTo().set(unit);
        Persistence.service().persist(event);
    }

    private static List<AptUnit> getAvailableUnits() {
        EntityQueryCriteria<PropertyManager> pmCriteria = new EntityQueryCriteria<PropertyManager>(PropertyManager.class);
        pmCriteria.add(PropertyCriterion.eq(pmCriteria.proto().name(), "Redridge"));
        List<PropertyManager> pmList = Persistence.service().query(pmCriteria);
        if (pmList.isEmpty()) {
            return new ArrayList<AptUnit>();
        } else {
            EntityQueryCriteria<AptUnit> criteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().belongsTo().propertyManager(), pmList.get(0)));
            return Persistence.service().query(criteria);
        }
    }

    @SuppressWarnings("unused")
    private static <T> List<T> randomChoice(List<T> list, int howMany) {
        List<T> copy = new ArrayList<T>(list);
        List<T> result = new ArrayList<T>(list);

        int minIndex = list.size() - howMany;
        for (int lastIndex = list.size() - 1; lastIndex >= minIndex; --lastIndex) {
            int i = RND.nextInt(lastIndex + 1);
            result.add(copy.get(i));
            copy.set(i, copy.get(lastIndex));
        }
        return copy;

    }

    /** return x such that x >= min and x < max */
    private static long rand(long min, long max) {
        return Math.max(min, Math.abs(RND.nextLong()) % max);
    }

    private static LogicalDate newDate(long time) {
        LogicalDate date = new LogicalDate(time);
        return date;
    }

}
