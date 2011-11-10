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

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.shared.ApplicationMode;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.RentReadinessStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.VacancyStatus;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class MockupAvailabilityStatusPreloader extends BaseVistaDevDataPreloader {
    private static final Random RND = new Random(9001l);

    private static final long MIN_EVENT_DELTA = 1000l * 60l * 60l * 24l; // one day

    private static final long MIN_RESIDENCY_TIME = 1000l * 60l * 60l * 24l * 365l; // approx 1 year

    private static final long MAX_RESIDENCY_TIME = 1000l * 60l * 60l * 24l * 365l * 5; // approx 5 years

    private static final long MIN_STAY_AFTER_NOTICE = 1000l * 60l * 60l * 24l * 30l;

    private static final long MAX_STAY_AFTER_NOTICE = 1000l * 60l * 60l * 24l * 60l;

    private static final long MAX_VACANT_TIME = 1000l * 60l * 60l * 24l * 30l; // approx 1 month

    private static final long MAX_WAIT_UNTIL_SCOPED = 1000l * 60l * 60l * 24l * 30l; // 30 DAYS

    private static final long MAX_WAIT_UNTIL_RENO_STARTS = 1000l * 60l * 60l * 24l * 7l;

    private static final long MAX_WAIT_UNTIL_RENO_ENDS = 1000l * 60l * 60l * 24l * 7l;

    @Override
    public String create() {
        return generateRandom();
    }

    @Override
    public String delete() {
        if (ApplicationMode.isDevelopment()) {
            return deleteAll(UnitAvailabilityStatus.class);
        } else {
            return "This is production";
        }
    }

    @SuppressWarnings("deprecation")
    private String generateRandom() {
        if ((config().minimizePreloadTime)) {
            return null;
        }
        int statusesCount = 0;
        final LogicalDate start = new LogicalDate();
        start.setYear(106);
        start.setMonth(0);
        start.setDate(1);
        final LogicalDate end = new LogicalDate();

        for (Key unitPk : getRelevantUnits()) {
            UnitAvailabilityStatus status = EntityFactory.create(UnitAvailabilityStatus.class);
            status.statusDate().setValue(start);
            status.belongsTo().setPrimaryKey(unitPk);
            while (status.statusDate().getValue().before(end)) {
                // create a 'new' entity (I hope it works)
                status.setPrimaryKey(null);

                if (status.vacancyStatus().isNull()) {
                    notice(status);

                } else if (status.vacancyStatus().getValue().equals(VacancyStatus.Notice)) {
                    if (status.isScoped().isBooleanTrue()) {
                        // throw a 'fair' coin in order to decide if someone is going to rent the unit or move out will take place
                        if ((RND.nextInt(2) == 0)
                                & (!RentedStatus.Rented.equals(status.rentedStatus().getValue()) & RentReadinessStatus.RentReady.equals(status
                                        .rentReadinessStatus().getValue()))) {
                            rented(status);
                        } else {
                            moveOut(status);
                        }
                    } else { /* notice, but not scoped */
                        // throw a 'fair' coin in order to decide if someone is going to move out or scoping is going to happen
                        if ((RND.nextInt(2) == 0)) {
                            scoped(status);
                        } else {
                            moveOut(status);
                        }
                    }
                } else { /* Vacant */
                    if (status.isScoped().isBooleanTrue()) {
                        if (RentedStatus.Rented.equals(status.rentedStatus().getValue())) {
                            moveIn(status);
                        } else if (RentReadinessStatus.RentReady.equals(status.rentReadinessStatus().getValue())) {
                            rented(status);
                        } else if (RentReadinessStatus.NeedsRepairs.equals(status.rentReadinessStatus().getValue())) {
                            renoInProgress(status);
                        } else if (RentReadinessStatus.RenoInProgress.equals(status.rentReadinessStatus().getValue())) {
                            renoFinished(status);
                        }
                    } else {
                        scoped(status);
                    }
                }
                ++statusesCount;

                Persistence.service().retrieve(status.belongsTo());
                status.unit().setValue(status.belongsTo().info().number().getValue());
                status.unitRent().setValue(status.belongsTo().financial().unitRent().isNull() ? 0d : status.belongsTo().financial().unitRent().getValue());
                status.marketRent()
                        .setValue(status.belongsTo().financial().marketRent().isNull() ? 0d : status.belongsTo().financial().marketRent().getValue());
                double unitMarketRent = status.marketRent().getValue();
                double unitRent = status.unitRent().getValue();

                double rentDeltaAbsoute = unitMarketRent - unitRent;
                double rentDeltaRelative = unitMarketRent == 0d ? 0d : rentDeltaAbsoute / unitMarketRent * 100;
                status.rentDeltaAbsolute().setValue(rentDeltaAbsoute);
                status.rentDeltaRelative().setValue(rentDeltaRelative);

                Persistence.service().retrieve(status.belongsTo().floorplan());
                status.floorplanName().setValue(status.belongsTo().floorplan().name().getValue());
                status.floorplanMarketingName().setValue(status.belongsTo().floorplan().marketingName().getValue());

                Persistence.service().retrieve(status.belongsTo().belongsTo());
                status.buildingBelongsTo().setPrimaryKey(status.belongsTo().getPrimaryKey());
                status.buildingName().setValue(status.belongsTo().belongsTo().info().name().getValue());
                status.propertyCode().setValue(status.belongsTo().belongsTo().propertyCode().getValue());
                status.propertyManagerName().setValue(status.belongsTo().belongsTo().propertyManager().name().getValue());

                Persistence.service().retrieve(status.belongsTo().belongsTo().complex());
                status.complexName().setValue(status.belongsTo().belongsTo().complex().name().getValue());

                //status.owner().setValue(status.belongsTo().belongsTo().info())
                // TODO fill other things
                Persistence.service().persist(status);
            } // end of unit status creation loop

        } // end of unit iteration loop
        return "Created " + statusesCount + " mockup unit statuses for Availability Gadgets";
    }

    private static void notice(UnitAvailabilityStatus status) {
        LogicalDate eventDate = new LogicalDate(status.statusDate().getValue().getTime() + rand(MIN_RESIDENCY_TIME, MAX_RESIDENCY_TIME));
        status.statusDate().setValue(eventDate);
        status.vacancyStatus().setValue(VacancyStatus.Notice);
        status.rentedStatus().setValue(RentedStatus.Unrented);
        status.isScoped().setValue(false);
        status.moveOutDay().setValue(new LogicalDate(eventDate.getTime() + rand(MIN_STAY_AFTER_NOTICE, MAX_STAY_AFTER_NOTICE)));
    }

    private static void moveOut(UnitAvailabilityStatus status) {
        status.statusDate().setValue(status.moveOutDay().getValue());
        status.vacancyStatus().setValue(VacancyStatus.Vacant);
    }

    private static void moveIn(UnitAvailabilityStatus status) {
        status.statusDate().setValue(status.moveInDay().getValue());
        status.vacancyStatus().setValue(null);
        status.isScoped().setValue(null);
        status.rentReadinessStatus().setValue(null);
        status.rentedStatus().setValue(null);
        status.rentedFromDate().setValue(null);
        status.moveOutDay().setValue(null);
        status.moveInDay().setValue(null);
    }

    private static void scoped(UnitAvailabilityStatus status) {
        long minScopingTime;
        long maxScopingTime;
        if (status.vacancyStatus().equals(VacancyStatus.Notice)) {
            minScopingTime = status.statusDate().getValue().getTime() + MIN_EVENT_DELTA;
            maxScopingTime = status.moveOutDay().getValue().getTime() - MIN_EVENT_DELTA;
        } else { // Vacant
            minScopingTime = status.moveOutDay().getValue().getTime() + MIN_EVENT_DELTA;
            maxScopingTime = minScopingTime + MAX_WAIT_UNTIL_SCOPED;
        }
        if (minScopingTime < maxScopingTime) {
            LogicalDate eventDate = new LogicalDate(rand(minScopingTime, maxScopingTime));
            status.statusDate().setValue(eventDate);
            status.rentReadinessStatus().setValue(RND.nextInt(5) > 1 ? RentReadinessStatus.RentReady : RentReadinessStatus.NeedsRepairs);
            status.isScoped().setValue(true);
        }
    }

    private static void rented(UnitAvailabilityStatus status) {
        long minRentedTime;
        long maxRentedTime;
        if (VacancyStatus.Notice.equals(status.vacancyStatus().getValue())) {
            minRentedTime = status.statusDate().getValue().getTime() + MIN_EVENT_DELTA;
            maxRentedTime = status.moveOutDay().getValue().getTime() - MIN_EVENT_DELTA;
        } else { // VacancyStatus == Vacant
            minRentedTime = status.statusDate().getValue().getTime() + MIN_EVENT_DELTA;
            maxRentedTime = minRentedTime + MAX_VACANT_TIME;
        }
        if (minRentedTime < maxRentedTime) {
            status.statusDate().setValue((new LogicalDate(rand(minRentedTime, maxRentedTime))));
            status.rentedFromDate().setValue(status.statusDate().getValue());
            status.rentedStatus().setValue(RentedStatus.Rented);
            LogicalDate moveInDay = new LogicalDate(rand(status.moveOutDay().getValue().getTime() + MIN_EVENT_DELTA, MAX_VACANT_TIME));
            status.moveInDay().setValue(moveInDay);
        }
    }

    private static void renoInProgress(UnitAvailabilityStatus status) {
        status.statusDate().setValue(new LogicalDate(rand(status.statusDate().getValue().getTime() + MIN_EVENT_DELTA, MAX_WAIT_UNTIL_RENO_STARTS)));
        status.rentReadinessStatus().setValue(RentReadinessStatus.RenoInProgress);
    }

    private static void renoFinished(UnitAvailabilityStatus status) {
        status.statusDate().setValue(new LogicalDate(rand(status.statusDate().getValue().getTime() + MIN_EVENT_DELTA, MAX_WAIT_UNTIL_RENO_ENDS)));
        status.rentReadinessStatus().setValue(RentReadinessStatus.RentReady);
        status.rentedStatus().setValue(RentedStatus.Unrented);
        status.vacancyStatus().setValue(VacancyStatus.Vacant);
    }

    private static List<Key> getRelevantUnits() {
        EntityQueryCriteria<AptUnit> criteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
        return Persistence.service().queryKeys(criteria);
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
}
