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
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.propertvista.generator.util.RandomUtil;

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
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.portal.server.preloader.util.AbstractMockupPreloader;
import com.propertyvista.server.common.charges.PriceCalculationHelpers;

public class MockupAvailabilityStatusPreloader extends AbstractMockupPreloader {

    private static final Random RND = new Random(9001l);

    private static final List<String> REGIONS = Arrays.asList("GTA", "West", "East");

    private static final long MIN_EVENT_DELTA = 1000l * 60l * 60l * 24l; // one day

    private static final long MIN_RESIDENCY_TIME = 1000l * 60l * 60l * 24l * 200l; // approx 6 months

    private static final long MAX_RESIDENCY_TIME = 1000l * 60l * 60l * 24l * 365l * 2; // approx 2 years

    private static final long MIN_STAY_AFTER_NOTICE = 1000l * 60l * 60l * 24l * 30l;

    private static final long MAX_STAY_AFTER_NOTICE = 1000l * 60l * 60l * 24l * 90l;

    private static final long MIN_VACANT_TIME = 1000l * 60l * 60l * 24l * 3l; // approx 3 days

    private static final long MAX_VACANT_TIME = 1000l * 60l * 60l * 24l * 90l; // approx 3 months

    private static final long MAX_WAIT_UNTIL_SCOPED = 1000l * 60l * 60l * 24l * 30l; // 30 DAYS

    private static final long MAX_WAIT_UNTIL_RENO_STARTS = 1000l * 60l * 60l * 24l * 7l;

    private static final long MAX_WAIT_UNTIL_RENO_ENDS = 1000l * 60l * 60l * 24l * 7l;

    private static final long MAX_WAIT_UNTIL_MOVEIN = 1000l * 60l * 60l * 24l * 7l;

    private static final int MAX_NUMBER_OF_UNITS = 500;

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
        int unitCounter = 0;
        final LogicalDate start = new LogicalDate();
        start.setYear(106);
        start.setMonth(0);
        start.setDate(1);
        final LogicalDate end = new LogicalDate();
        end.setTime(end.getTime() - MIN_EVENT_DELTA);
        ArrayList<IEntity> statuses = new ArrayList<IEntity>();
        AptUnit lastUnit = null;
        UnitAvailabilityStatus status = EntityFactory.create(UnitAvailabilityStatus.class);
        for (Building building : Persistence.service().query(new EntityQueryCriteria<Building>(Building.class))) {
            if (++unitCounter > MAX_NUMBER_OF_UNITS) {
                break;
            }
            Persistence.service().retrieve(building.complex());

            EntityQueryCriteria<AptUnit> unitCriteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
            unitCriteria.add(PropertyCriterion.eq(unitCriteria.proto().belongsTo(), building));

            for (AptUnit unit : Persistence.service().query(unitCriteria)) {
                if (++unitCounter > MAX_NUMBER_OF_UNITS) {
                    break;
                }
                lastUnit = unit;
                status.set(null);
                status.statusDate().setValue(start);
                status.unit().setPrimaryKey(unit.getPrimaryKey());

                status.unitName().setValue(unit.info().number().getValue());

                EntityQueryCriteria<Lease> leaseCriteria = new EntityQueryCriteria<Lease>(Lease.class);
                leaseCriteria.add(PropertyCriterion.eq(leaseCriteria.proto().unit(), unit));
                Lease lease = Persistence.service().retrieve(leaseCriteria);
                if (lease != null && !lease.serviceAgreement().isNull() && !lease.serviceAgreement().serviceItem().isNull()) {
                    PriceCalculationHelpers.calculateChargeItemAdjustments(lease.serviceAgreement().serviceItem());
                    unit.financial()._unitRent().setValue(lease.serviceAgreement().serviceItem().agreedPrice().getValue());
                }

                double marketRent = unit.financial()._marketRent().isNull() ? 0d : unit.financial()._marketRent().getValue();
                status.marketRent().setValue(marketRent);
                // TODO get unit rent from the correct place and remove the random generation in moveIn()
                Persistence.service().retrieve(unit.floorplan());
                status.floorplanName().setValue(unit.floorplan().name().getValue());
                status.floorplanMarketingName().setValue(unit.floorplan().marketingName().getValue());

                status.building().setPrimaryKey(building.getPrimaryKey());
                status.buildingName().setValue(building.info().name().getValue());
                status.propertyCode().setValue(building.propertyCode().getValue());
                status.complexName().setValue(building.complex().name().getValue());

                status.common().propertyManger().set(building.propertyManager());
                status.common().region().setValue(randomRegion());
                // TODO fill common().owner() and commmon().portfolio() 

                while (status.statusDate().getValue().before(end)) {
                    statuses.add(status.duplicate());

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
        if (!statuses.isEmpty()) {
            // add 1 turnover at the end
            UnitAvailabilityStatus s = (UnitAvailabilityStatus) statuses.get(statuses.size() - 1);
            status.unit().set(lastUnit);
            status.statusDate().setValue(new LogicalDate(end.getTime() + MIN_EVENT_DELTA));
            status.vacancyStatus().setValue(null);
            statuses.add(status.duplicate());

            status.vacancyStatus().setValue(VacancyStatus.Vacant);
            statuses.add(status.duplicate());

            status.vacancyStatus().setValue(null);
            statuses.add(status.duplicate());

            status.vacancyStatus().setValue(VacancyStatus.Vacant);
            statuses.add(status.duplicate());

            status.vacancyStatus().setValue(null);
            statuses.add(status.duplicate());

            status.vacancyStatus().setValue(VacancyStatus.Vacant);
            statuses.add(status.duplicate());

            status.vacancyStatus().setValue(null);
            statuses.add(status.duplicate());

        }
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
        status.unitRent().setValue(null);
        status.rentDeltaAbsolute().setValue(null);
        status.rentDeltaRelative().setValue(null);
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
            minRentedTime = status.statusDate().getValue().getTime() + MIN_VACANT_TIME;
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

        status.unitRent().setValue(randomUnitRent(status));
        double marketRent = status.marketRent().isNull() ? 0d : status.marketRent().getValue();
        double rentDeltaAbsoute = marketRent - status.unitRent().getValue();
        double rentDeltaRelative = marketRent == 0d ? 0d : rentDeltaAbsoute / marketRent * 100;
        status.rentDeltaAbsolute().setValue(rentDeltaAbsoute);
        status.rentDeltaRelative().setValue(rentDeltaRelative);
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

    private static Double randomUnitRent(UnitAvailabilityStatus status) {
        final double MAX_DIFF_PCT = 0.1;
        return status.marketRent().getValue() * ((1 + MAX_DIFF_PCT) - (2 * RND.nextDouble() * MAX_DIFF_PCT));
    }

    /** return x such that x >= min and x < max */
    private static long rand(long min, long max) {
        return Math.max(min, Math.abs(RND.nextLong()) % max);
    }

    private String randomRegion() {
        return RandomUtil.randomChoice(RND, REGIONS, 1).get(0);
    }
}
