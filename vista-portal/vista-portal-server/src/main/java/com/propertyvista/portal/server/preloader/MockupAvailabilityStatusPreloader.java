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
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.RentReadinessStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.VacancyStatus;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;

public class MockupAvailabilityStatusPreloader extends AbstractMockupPreloader {

    private static final Random RND = new Random(9001l);

    private static final long MIN_EVENT_DELTA = 1000l * 60l * 60l * 24l; // one day

    private static final long MIN_RESIDENCY_TIME = 1000l * 60l * 60l * 24l * 200l; // approx 6 months

    private static final long MAX_RESIDENCY_TIME = 1000l * 60l * 60l * 24l * 365l * 2; // approx 2 years

    private static final long MIN_STAY_AFTER_NOTICE = 1000l * 60l * 60l * 24l * 30l;

    private static final long MAX_STAY_AFTER_NOTICE = 1000l * 60l * 60l * 24l * 90l;

    private static final long MAX_VACANT_TIME = 1000l * 60l * 60l * 24l * 90l; // approx 3 months

    private static final long MAX_WAIT_UNTIL_SCOPED = 1000l * 60l * 60l * 24l * 30l; // 30 DAYS

    private static final long MAX_WAIT_UNTIL_RENO_STARTS = 1000l * 60l * 60l * 24l * 7l;

    private static final long MAX_WAIT_UNTIL_RENO_ENDS = 1000l * 60l * 60l * 24l * 7l;

    private static final long MAX_WAIT_UNTIL_MOVEIN = 1000l * 60l * 60l * 24l * 7l;

    @Override
    public String createMockup() {
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
        final LogicalDate start = new LogicalDate();
        start.setYear(106);
        start.setMonth(0);
        start.setDate(1);
        final LogicalDate end = new LogicalDate();
        ArrayList<IEntity> statuses = new ArrayList<IEntity>();

        UnitAvailabilityStatus status = EntityFactory.create(UnitAvailabilityStatus.class);
        for (Building building : Persistence.service().query(new EntityQueryCriteria<Building>(Building.class))) {
            Persistence.service().retrieve(building.complex());

            EntityQueryCriteria<AptUnit> unitCriteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
            unitCriteria.add(PropertyCriterion.eq(unitCriteria.proto().belongsTo(), building));

            for (AptUnit unit : Persistence.service().query(unitCriteria)) {
                status.set(null);
                status.statusDate().setValue(start);
                status.belongsTo().setPrimaryKey(unit.getPrimaryKey());

                status.unit().setValue(unit.info().number().getValue());

                double unitRent = unit.financial()._unitRent().isNull() ? 0d : unit.financial()._unitRent().getValue();
                double marketRent = status.belongsTo().financial()._marketRent().isNull() ? 0d : unit.financial()._marketRent().getValue();
                double rentDeltaAbsoute = marketRent - unitRent;
                double rentDeltaRelative = marketRent == 0d ? 0d : rentDeltaAbsoute / marketRent * 100;

                status.unitRent().setValue(unitRent);
                status.marketRent().setValue(marketRent);
                status.rentDeltaAbsolute().setValue(rentDeltaAbsoute);
                status.rentDeltaRelative().setValue(rentDeltaRelative);

                Persistence.service().retrieve(unit.floorplan());
                status.floorplanName().setValue(unit.floorplan().name().getValue());
                status.floorplanMarketingName().setValue(unit.floorplan().marketingName().getValue());

                status.buildingBelongsTo().setPrimaryKey(building.getPrimaryKey());
                status.buildingName().setValue(building.info().name().getValue());
                status.propertyCode().setValue(building.propertyCode().getValue());
                status.propertyManagerName().setValue(building.propertyManager().name().getValue());
                status.complexName().setValue(building.complex().name().getValue());

                // TODO fill other things

                while (status.statusDate().getValue().before(end)) {
                    statuses.add(status.cloneEntity());

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

                    if (!status.moveOutDay().isNull()) {
                        // TODO actually should be equal to unit.availableForRent();
                        LogicalDate availableFromDay = new LogicalDate(status.moveOutDay().getValue());
                        availableFromDay.setTime(availableFromDay.getTime() + 24l * 60l * 60l * 1000l);
                        status.availableFromDay().setValue(availableFromDay);
                    }
                } // end of unit status creation loop
            } // end of unit iteration loop
        } // end of building iteration loop
        persistArray(statuses);
        return "Created " + statuses.size() + " mockup unit statuses for Availability Gadgets";
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
        status.availableFromDay().setValue(null);
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
        LogicalDate moveInDay = null;
        if (VacancyStatus.Notice.equals(status.vacancyStatus().getValue())) {
            minRentedTime = status.statusDate().getValue().getTime() + MIN_EVENT_DELTA;
            maxRentedTime = status.moveOutDay().getValue().getTime() - MIN_EVENT_DELTA;

            moveInDay = new LogicalDate(rand(status.moveOutDay().getValue().getTime() + MIN_EVENT_DELTA, MAX_VACANT_TIME));

        } else { // VacancyStatus == Vacant
            minRentedTime = status.statusDate().getValue().getTime() + MIN_EVENT_DELTA;
            maxRentedTime = minRentedTime + MAX_VACANT_TIME;
        }
        if (minRentedTime < maxRentedTime) {
            status.statusDate().setValue((new LogicalDate(rand(minRentedTime, maxRentedTime))));
            status.rentedFromDate().setValue(status.statusDate().getValue());
            status.rentedStatus().setValue(RentedStatus.Rented);
            if (moveInDay == null) {
                moveInDay = new LogicalDate(rand(status.rentedFromDate().getValue().getTime() + MIN_EVENT_DELTA, status.rentedFromDate().getValue().getTime()
                        + MAX_WAIT_UNTIL_MOVEIN));
                status.moveOutDay().setValue(null);
            }
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
