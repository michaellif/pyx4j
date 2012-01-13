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
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy.OffMarketType;
import com.propertyvista.domain.tenant.lease.Lease;

public class AvailabilityReportManager {

    public static void handleOccupancyChange(LogicalDate when, AptUnit unit) {
        EntityQueryCriteria<UnitAvailabilityStatus> criteria = new EntityQueryCriteria<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unit));
        criteria.add(PropertyCriterion.ge(criteria.proto().statusDate(), when));
        Persistence.service().delete(criteria);

        List<AptUnitOccupancy> occupancy = occupancy(unit);
        List<UnitAvailabilityStatus> availability = generateUnitAvaialbility(when, unit, occupancy);

        // TODO convert to array save
        AvailabilityReferencedData refs = retrieveReferencedValues(unit);
        for (UnitAvailabilityStatus status : availability) {
            setReferencedValues(status, refs);
            Persistence.service().merge(status);
        }
    }

    private static List<AptUnitOccupancy> occupancy(AptUnit unit) {
        EntityQueryCriteria<AptUnitOccupancy> criteria = new EntityQueryCriteria<AptUnitOccupancy>(AptUnitOccupancy.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unit));
        return Persistence.service().query(criteria);
    }

    public static List<UnitAvailabilityStatus> generateUnitAvaialbility(LogicalDate when, AptUnit unit, List<AptUnitOccupancy> occupancy) {
        List<UnitAvailabilityStatus> result = new ArrayList<UnitAvailabilityStatus>();

        ListIterator<AptUnitOccupancy> i = occupancyAtTime(when, occupancy);
        do {
            result.add(calculateUnitAvailabilityStatus(when, occupancy));
            when = i.next().dateFrom().getValue();
        } while (i.hasNext());

        return result;
    }

    /**
     * @param occupancy
     *            sorted (in the ascending order by {@link AptUnitOccupancy#dateFrom()}) non empty list of occupancy intervals covering the whole possible
     *            time
     * @return
     */
    public static UnitAvailabilityStatus calculateUnitAvailabilityStatus(LogicalDate when, List<AptUnitOccupancy> occupancy) {
        final UnitAvailabilityStatus status = EntityFactory.create(UnitAvailabilityStatus.class);

        final ListIterator<AptUnitOccupancy> i = occupancyAtTime(when, occupancy);

        AptUnitOccupancy currentOccupancyStatus = i.next();
        if (currentOccupancyStatus.status().equals(AptUnitOccupancy.Status.leased)) {
            // if we have other statuses, we know that this lease will end eventually, because we have another status
            // so we set the vacancy status to NOTICE
            if (i.hasNext()) {
                status.vacancyStatus().setValue(VacancyStatus.Notice);
                status.moveOutDay().setValue(retrieveMoveOutDateFromLease(currentOccupancyStatus.lease()));

                AptUnitOccupancy nextOccupancyStatus = i.next();
                if (AptUnitOccupancy.Status.vacant.equals(nextOccupancyStatus.status().getValue())) {
                    status.isScoped().setValue(false);
                } else {
                    status.isScoped().setValue(true);
                    if (AptUnitOccupancy.Status.offMarket.equals(nextOccupancyStatus.status().getValue())
                            && nextOccupancyStatus.offMarket().equals(AptUnitOccupancy.OffMarketType.construction)) {
                        status.rentReadinessStatus().setValue(RentReadinessStatus.NeedsRepairs);
                    } else {
                        status.rentReadinessStatus().setValue(RentReadinessStatus.RentReady);
                    }
                }

                status.rentedStatus().setValue(
                        status.isScoped().isBooleanTrue() && status.rentReadinessStatus().equals(RentReadinessStatus.NeedsRepairs) ? RentedStatus.OffMarket
                                : RentedStatus.Unrented);

                if (AptUnitOccupancy.Status.leased.equals(nextOccupancyStatus.status().getValue())) {
                    status.rentedStatus().setValue(RentedStatus.Rented);
                    status.moveInDay().setValue(retrieveMoveInDateFromLease(nextOccupancyStatus.lease()));
                }
                while (i.hasNext()) {
                    nextOccupancyStatus = i.next();

                    if (AptUnitOccupancy.Status.leased.equals(nextOccupancyStatus.status().getValue())) {
                        status.rentedStatus().setValue(RentedStatus.Rented);
                        status.moveInDay().setValue(retrieveMoveInDateFromLease(nextOccupancyStatus.lease()));
                        break;
                    }
                }
            }
        } else if (currentOccupancyStatus.status().equals(AptUnitOccupancy.Status.vacant)) {
            // work under assumption that AptUnitOccupancy.Status.vacant explicitly means the following:
            status.vacancyStatus().setValue(VacancyStatus.Vacant);
            status.isScoped().setValue(false);
            // so we get:
            // "not scoped", hence it's unknown if it's in a condition that is acceptable to be rented,
            // and then:
            status.rentedStatus().setValue(RentedStatus.Unrented);

        } else if (currentOccupancyStatus.status().equals(AptUnitOccupancy.Status.available)) {
            status.vacancyStatus().setValue(VacancyStatus.Vacant);
            status.rentReadinessStatus().setValue(RentReadinessStatus.RentReady);
            status.isScoped().setValue(true);
            status.rentedStatus().setValue(RentedStatus.Unrented);
            while (i.hasNext()) {
                AptUnitOccupancy nextOccupancyStatus = i.next();
                if (AptUnitOccupancy.Status.leased.equals(nextOccupancyStatus)) {
                    status.rentedStatus().setValue(RentedStatus.Rented);
                    status.moveInDay().setValue(retrieveMoveInDateFromLease(nextOccupancyStatus.lease()));
                    break;
                }
            }
        } else if (currentOccupancyStatus.status().equals(AptUnitOccupancy.Status.offMarket)) {
            status.rentedStatus().setValue(RentedStatus.OffMarket);
            if (currentOccupancyStatus.offMarket().equals(OffMarketType.construction)) {
                status.vacancyStatus().setValue(VacancyStatus.Vacant);
                status.rentReadinessStatus().setValue(RentReadinessStatus.RenoInProgress);
            }
        } else if (currentOccupancyStatus.status().equals(AptUnitOccupancy.Status.reserved)) {
            status.vacancyStatus().setValue(VacancyStatus.Vacant);
            status.rentedStatus().setValue(RentedStatus.OffMarket);
        }

        return status;
    }

    public static ListIterator<AptUnitOccupancy> occupancyAtTime(LogicalDate when, List<AptUnitOccupancy> occupancy) {
        AptUnitOccupancy key = EntityFactory.create(AptUnitOccupancy.class);
        key.dateFrom().setValue(when);

        int index = Collections.binarySearch(occupancy, key, new Comparator<AptUnitOccupancy>() {
            @Override
            public int compare(AptUnitOccupancy paramT1, AptUnitOccupancy paramT2) {
                return paramT1.dateFrom().compareTo(paramT2.dateFrom());
            }
        });
        if (index < 0) {
            index = (-index) - 1;
        }
        return occupancy.listIterator(index);
    }

    public static LogicalDate retrieveMoveOutDateFromLease(Lease lease) {
        //@formatter:off
        LogicalDate moveout = 
                    !lease.actualMoveOut().isNull() ? lease.actualMoveOut().getValue()
                    : !lease.expectedMoveOut().isNull() ? lease.expectedMoveOut().getValue() : null;        
        //@formatter:on
        return moveout;
    }

    public static LogicalDate retrieveMoveInDateFromLease(Lease lease) {
        //@formatter:off
        LogicalDate moveIn = 
                    !lease.actualMoveIn().isNull() ? lease.actualMoveIn().getValue()
                    : !lease.expectedMoveIn().isNull() ? lease.expectedMoveIn().getValue() : null;        
        //@formatter:on
        return moveIn;
    }

    /**
     * Return the availability status.
     * 
     * @param unit
     * @param when
     * @return
     */
    public static UnitAvailabilityStatus unitAvailabilityStatus(AptUnit unit, LogicalDate when) {
        EntityQueryCriteria<UnitAvailabilityStatus> criteria = new EntityQueryCriteria<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unitName(), unit));
        criteria.add(PropertyCriterion.le(criteria.proto().statusDate(), when));
        criteria.sort(new Sort(criteria.proto().statusDate().getFieldName(), true));

        UnitAvailabilityStatus status = Persistence.service().retrieve(criteria);

        if (status == null) {
            status = initNewStatus(unit, when);
        }
        return status;
    }

    public static UnitAvailabilityStatus initNewStatus(AptUnit unit, LogicalDate statusDate) {
        UnitAvailabilityStatus status = EntityFactory.create(UnitAvailabilityStatus.class);
        initReferencedValues(status, unit);
        status.vacancyStatus().setValue(VacancyStatus.Vacant);
        status.rentedStatus().setValue(RentedStatus.OffMarket);
        status.rentReadinessStatus().setValue(RentReadinessStatus.RentReady);
        status.marketRent().setValue(marketRent(unit));
        return status;
    }

    public static void initReferencedValues(UnitAvailabilityStatus status, AptUnit unit) {
        setReferencedValues(status, retrieveReferencedValues(unit));
    }

    public static AvailabilityReferencedData retrieveReferencedValues(AptUnit unit) {
        Building building = Persistence.service().retrieve(Building.class, unit.belongsTo().getPrimaryKey());
        Floorplan floorplan = Persistence.service().retrieve(Floorplan.class, unit.floorplan().getPrimaryKey());
        Complex complex = building.complex().isNull() ? null : Persistence.service().retrieve(Complex.class, building.complex().getPrimaryKey());
        Double marketRent = marketRent(unit);
        return new AvailabilityReferencedData(complex, building, floorplan, unit, marketRent);
    }

    public static Double marketRent(AptUnit unit) {
        // TODO fetch market rent from service catalog
        // TODO shouldn't market rent be bound to time?        
        return 100.0;
    }

    public static void setReferencedValues(UnitAvailabilityStatus status, AvailabilityReferencedData data) {
        status.building().set(data.building);
        status.unit().set(data.unit);
        status.floorplan().set(data.floorplan);
        status.marketRent().setValue(data.marketRent);
    }

    public static class AvailabilityReferencedData {

        public final Building building;

        public final Floorplan floorplan;

        public final AptUnit unit;

        public final Complex complex;

        public final Double marketRent;

        public AvailabilityReferencedData(Complex complex, Building building, Floorplan floorplan, AptUnit unit, Double marketRent) {
            this.complex = complex;
            this.building = building;
            this.floorplan = floorplan;
            this.unit = unit;
            this.marketRent = marketRent;
        }

    }
}
