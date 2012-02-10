/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 11, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.RentReadinessStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.VacancyStatus;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.tenant.lease.Lease;

public class AvailabilityReportManager {

    public void handleOccupancyChange(LogicalDate when, AptUnit unit) {

        EntityQueryCriteria<UnitAvailabilityStatus> criteria = new EntityQueryCriteria<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unit));
        criteria.add(PropertyCriterion.ge(criteria.proto().statusDate(), when));
        Persistence.service().delete(criteria);

        List<AptUnitOccupancySegment> occupancy = queryOccupancy(unit);
        List<UnitAvailabilityStatus> availability = computeUnitAvaialbility(when, occupancy);

        for (UnitAvailabilityStatus status : availability) {
            Persistence.service().merge(status);
        }
    }

    private List<AptUnitOccupancySegment> queryOccupancy(AptUnit unit) {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = new EntityQueryCriteria<AptUnitOccupancySegment>(AptUnitOccupancySegment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().occupancy().unit(), unit));
        criteria.sort(new Sort(criteria.proto().dateFrom().getPath().toString(), false));
        return Persistence.service().query(criteria);
    }

    /**
     * Compute unit availability states based on given occupancy.
     * 
     * @param startTime
     *            starting point of computation
     * @param occupancy
     *            non empty list of occupancy state interval that cover the whole time, don't overlap, and ordered in ascending order by
     *            {@link AptUnitOccupancySegment#dateFrom()}.
     * @return
     */
    public static List<UnitAvailabilityStatus> computeUnitAvaialbility(LogicalDate startTime, List<AptUnitOccupancySegment> occupancy) {
        List<UnitAvailabilityStatus> result = new ArrayList<UnitAvailabilityStatus>();

        ListIterator<AptUnitOccupancySegment> i = getOccupancyStatusCursorAt(startTime, occupancy);
        do {
            result.add(computeUnitAvailabilityStatus(startTime, occupancy));
            startTime = i.next().dateFrom().getValue();
        } while (i.hasNext());

        return result;
    }

    /**
     * @param occupancy
     *            sorted (in the ascending order by {@link AptUnitOccupancySegment#dateFrom()}) non empty list of occupancy intervals covering the whole
     *            possible
     *            time
     * @return
     */
    public static UnitAvailabilityStatus computeUnitAvailabilityStatus(LogicalDate when, List<AptUnitOccupancySegment> occupancy) {

        // TODO currently use OffMarketType "other" to denote units that have not been scoped yet, i.e. "Unrented" and loosing money

        UnitAvailabilityStatus status = null;

        ListIterator<AptUnitOccupancySegment> i = getOccupancyStatusCursorAt(when, occupancy);
        AptUnitOccupancySegment occupancyStatusAtRequestedTime = i.next();

        switch (occupancyStatusAtRequestedTime.status().getValue()) {
        case available:
            status = available(i);
            break;
        case leased:
            status = leased(occupancyStatusAtRequestedTime.lease(), i);
            break;
        case offMarket:
            status = offMarket(occupancyStatusAtRequestedTime.offMarket().getValue(), i);
            break;
        case reserved:
            reserved(i);
            break;
        case vacant:
            vacant(i);
            break;
        default:
            throw new Error("got unknown occupancy status: '" + occupancyStatusAtRequestedTime.status().getValue() + "'");
        }
        status.statusDate().setValue(when);

        return status;
    }

    private static UnitAvailabilityStatus available(ListIterator<AptUnitOccupancySegment> i) {
        final UnitAvailabilityStatus status = EntityFactory.create(UnitAvailabilityStatus.class);

        // if the unit is available it means it's ready for rent, hence it's been scoped
        // if we have lease in the future we set it as: "Rented"
        status.vacancyStatus().setValue(VacancyStatus.Vacant);

        status.isScoped().setValue(true);
        status.rentReadinessStatus().setValue(RentReadinessStatus.RentReady);
        status.rentedStatus().setValue(RentedStatus.Unrented);

        boolean doLoop = true;
        while (doLoop & i.hasNext()) {
            AptUnitOccupancySegment occupancyStatus = i.next();
            switch (occupancyStatus.status().getValue()) {
            case leased:
                status.rentedStatus().setValue(RentedStatus.Rented);
                status.moveInDay().setValue(getMoveInDate(occupancyStatus.lease()));
                status.unitRent().setValue(getUnitRent(occupancyStatus.lease()));

                doLoop = false;
                break;
            }
        }

        return status;
    }

    private static UnitAvailabilityStatus leased(Lease lease, ListIterator<AptUnitOccupancySegment> i) {

        final UnitAvailabilityStatus status = EntityFactory.create(UnitAvailabilityStatus.class);
        // we have to deduce if the unit has Notice, and if it is we have to do the following:
        //     * set the move out date
        //     * check whether the unit is scoped, and if it is, set the relevant rent readiness status
        //     * check whether the unit is rented, and set the move in date. to check the "Rented" status we use the following logic:
        //            * if lease in the future exists, then 'Rented',
        //            * else if rentReady then 'Unrented'
        //            * else 'OffMarket'

        // if we have other statuses, we know that this lease will end eventually,
        // then we know that the vacancy status is NOTICE
        if (i.hasNext()) {
            status.vacancyStatus().setValue(VacancyStatus.Notice);
            status.moveOutDay().setValue(getMoveOutDate(lease));
            status.rentedStatus().setValue(RentedStatus.Unrented);
            AptUnitOccupancySegment occupancyStatus = i.next();

            // continue to check statuses until we reach the end or find out that the unit is rented
            do {
                switch (occupancyStatus.status().getValue()) {
                case vacant:
                    // TODO i'm not sure how to handle vacant status
                    break;
                case reserved:
                    break;
                case available:
                    if (status.isScoped().isNull()) {
                        status.isScoped().setValue(true);
                        status.rentReadinessStatus().setValue(RentReadinessStatus.RentReady);
                    }
                    break;
                case offMarket:
                    if (status.isScoped().isNull()) {
                        if (OffMarketType.construction.equals(occupancyStatus.offMarket().getValue())) {
                            status.isScoped().setValue(true);
                            status.rentReadinessStatus().setValue(RentReadinessStatus.NeedsRepairs);
                        } else {
                            // TODO not sure about 'offMarket' types other than construction (just ignore for now)
                        }
                    }
                    break;
                case leased:
                    // assume that if it's rented, it means it's already been scoped, and ready for rent
                    status.isScoped().setValue(true);
                    if (status.rentReadinessStatus().isNull()) {
                        status.rentReadinessStatus().setValue(RentReadinessStatus.RentReady);
                    }
                    status.rentedStatus().setValue(RentedStatus.Rented);
                    status.moveInDay().setValue(getMoveInDate(occupancyStatus.lease()));
                    status.unitRent().setValue(getUnitRent(occupancyStatus.lease()));
                }
            } while (RentedStatus.Rented.equals(status.rentedStatus().getValue()) & i.hasNext());

            if (status.isScoped().isNull()) {
                status.isScoped().setValue(false);
            } else if (status.rentedStatus().getValue().equals(RentedStatus.Unrented)
                    & !RentReadinessStatus.RentReady.equals(status.rentReadinessStatus().getValue())) {
                status.rentedStatus().setValue(RentedStatus.OffMarket);
            }
        }

        return status;
    }

    private static UnitAvailabilityStatus offMarket(OffMarketType type, ListIterator<AptUnitOccupancySegment> other) {

        final UnitAvailabilityStatus availablilityStatus = EntityFactory.create(UnitAvailabilityStatus.class);

        if (type.equals(OffMarketType.construction)) {
            availablilityStatus.rentReadinessStatus().setValue(RentReadinessStatus.RenoInProgress);
            availablilityStatus.vacancyStatus().setValue(VacancyStatus.Vacant);
            availablilityStatus.isScoped().setValue(true);
        } else {
            // TODO not sure what about "off market" which is not construction, i.e. if the unit is vacant/scoped or not (for now assume that it is vacant)
            availablilityStatus.vacancyStatus().setValue(VacancyStatus.Vacant);
            availablilityStatus.isScoped().setValue(false);
        }
        // now we what is left is to understand if its rented or not 
        // if the unit is not rented, it means it is off market for good (for the logic see comments for leased() handler)
        boolean doLoop = true;
        while (doLoop & other.hasNext()) {
            AptUnitOccupancySegment occupancyStatus = other.next();
            switch (occupancyStatus.status().getValue()) {
            case leased:
                availablilityStatus.rentedStatus().setValue(RentedStatus.Rented);
                availablilityStatus.moveInDay().setValue(getMoveInDate(occupancyStatus.lease()));
                availablilityStatus.unitRent().setValue(getUnitRent(occupancyStatus.lease()));
                doLoop = false;
                break;
            case available:
                if (availablilityStatus.rentedStatus().isNull()) {
                    availablilityStatus.isScoped().setValue(true);
                    availablilityStatus.rentedStatus().setValue(RentedStatus.Unrented);
                }
                break;
            case offMarket:
                if (OffMarketType.construction.equals(occupancyStatus.offMarket().getValue())) {
                    availablilityStatus.isScoped().setValue(true);
                    availablilityStatus.rentReadinessStatus().setValue(RentReadinessStatus.NeedsRepairs);
                }
                break;
            default:
                break;
            }
        }
        if (availablilityStatus.rentedStatus().isNull()) {
            if (type.equals(OffMarketType.other)) {
                availablilityStatus.rentedStatus().setValue(RentedStatus.Unrented);
            } else {
                availablilityStatus.rentedStatus().setValue(RentedStatus.OffMarket);
            }
        }
        return availablilityStatus;
    }

    private static UnitAvailabilityStatus reserved(ListIterator<AptUnitOccupancySegment> other) {
        // TODO: reserved status handler
        throw new Error("occupancy status handler for 'reserved' has not yet been implemented");
    }

    private static UnitAvailabilityStatus vacant(ListIterator<AptUnitOccupancySegment> other) {
        // TODO: vacant status handler
        throw new Error("occupancy status handler for 'vacant' has not yet been implemented");
    }

    public static ListIterator<AptUnitOccupancySegment> getOccupancyStatusCursorAt(LogicalDate when, List<AptUnitOccupancySegment> occupancy) {
        AptUnitOccupancySegment key = EntityFactory.create(AptUnitOccupancySegment.class);
        key.dateFrom().setValue(when);

        int index = Collections.binarySearch(occupancy, key, new Comparator<AptUnitOccupancySegment>() {
            @Override
            public int compare(AptUnitOccupancySegment paramT1, AptUnitOccupancySegment paramT2) {
                return paramT1.dateFrom().compareTo(paramT2.dateFrom());
            }
        });
        if (index < 0) {
            index = (-index) - 2;
        }
        return occupancy.listIterator(index);
    }

    private static LogicalDate getMoveOutDate(Lease lease) {
        if (!lease.actualMoveOut().isNull()) {
            return lease.actualMoveOut().getValue();
        } else if (!lease.expectedMoveOut().isNull()) {
            return lease.expectedMoveOut().getValue();
        } else {
            return null;
        }
    }

    private static LogicalDate getMoveInDate(Lease lease) {
        if (!lease.actualMoveIn().isNull()) {
            return lease.actualMoveIn().getValue();
        } else if (!lease.expectedMoveIn().isNull()) {
            return lease.expectedMoveIn().getValue();
        } else {
            return null;
        }
    }

    private static BigDecimal getUnitRent(Lease lease) {
        // TODO get unit rent from lease
        return new BigDecimal(0);
    }

    public static interface ReferencedValuesInitializer {

        void setReferencedValues(UnitAvailabilityStatus status);

    }

    /**
     * This is for dependency injection and making {@link AvailabilityReportManager} testable.
     */
    public static interface ReferencedValuesInitializerFactory {

        ReferencedValuesInitializer create();

    }

    public static class PersistenceReferencedValuesInitializer implements ReferencedValuesInitializer {

        public final Building building;

        public final Floorplan floorplan;

        public final AptUnit unit;

        public final Complex complex;

        public final BigDecimal marketRent;

        public PersistenceReferencedValuesInitializer(AptUnit unit) {
            this.unit = unit;
            this.building = Persistence.service().retrieve(Building.class, unit.belongsTo().getPrimaryKey());
            this.floorplan = Persistence.service().retrieve(Floorplan.class, unit.floorplan().getPrimaryKey());
            this.complex = building.complex().isNull() ? null : Persistence.service().retrieve(Complex.class, building.complex().getPrimaryKey());

            // FIXME market rent should be fetched from SeriveCatalog
            this.marketRent = new BigDecimal(100);
        }

        @Override
        public void setReferencedValues(UnitAvailabilityStatus status) {
            status.complex().set(complex);
            status.building().set(building);
            status.floorplan().set(floorplan);
            status.unit().set(unit);
            status.marketRent().setValue(marketRent);
        }
    }
}
