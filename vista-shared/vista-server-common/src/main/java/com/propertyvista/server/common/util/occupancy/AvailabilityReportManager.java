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
package com.propertyvista.server.common.util.occupancy;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.RentReadiness;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.Scoping;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.Vacancy;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;

public class AvailabilityReportManager {

    private final AptUnit unit;

    public AvailabilityReportManager(AptUnit unit) {
        this.unit = unit;
    }

    /**
     * Refresh the series of availability statuses based on occupancy: remove the old statuses from the DB, and create new ones.
     * 
     * @param startingOn
     *            Start the computation of statuses on this date.
     * @param unit
     *            The unit we are interested in.
     * 
     */
    public void generateUnitAvailablity(LogicalDate startingOn) {
        removeStatuses(startingOn);
        generateStatuses(occcpancy(startingOn));
    }

    private List<AptUnitOccupancySegment> occcpancy(LogicalDate startingOn) {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = new EntityQueryCriteria<AptUnitOccupancySegment>(AptUnitOccupancySegment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unit));
        criteria.add(PropertyCriterion.ge(criteria.proto().dateTo(), startingOn));
        criteria.asc(criteria.proto().dateFrom());

        List<AptUnitOccupancySegment> occupancy = Persistence.secureQuery(criteria);
        if (occupancy.isEmpty()) {
            throw new IllegalStateException("failed to retreive occupancy for unit pk = " + unit.getPrimaryKey());
        }

        // adjust value to generate correct dates for statuses
        occupancy.get(0).dateFrom().setValue(startingOn);

        return occupancy;

    }

    private void generateStatuses(List<AptUnitOccupancySegment> occupancy) {
        if (occupancy.isEmpty()) {
            return;
        } else {
            AptUnitOccupancySegment current = first(occupancy);
            List<AptUnitOccupancySegment> future = rest(occupancy);

            UnitAvailabilityStatus status = EntityFactory.create(UnitAvailabilityStatus.class);
            status.statusDate().setValue(current.dateFrom().getValue());

            switch (current.status().getValue()) {
            case vacant:
                vacant(status, current.dateFrom().getValue());
                break;
            case available:
                available(status, future, current.dateFrom().getValue());
                break;
            case reserved:
                reserved(status, current, current.dateFrom().getValue());
                break;
            case leased:
                leased(status, current, future);
                break;
            case renovation:
                renovation(status, future);
                break;
            case offMarket:
                offMarket(status);
                break;
            }

            setReferences(status);

            Persistence.service().persist(status);

            generateStatuses(future);
        }
    }

    private void vacant(UnitAvailabilityStatus status, LogicalDate vacantSince) {
        status.vacancyStatus().setValue(Vacancy.Vacant);
        status.vacantSince().setValue(vacantSince);
        // implicit: status.moveOutDay().setValue(null);

        status.scoping().setValue(Scoping.Unscoped);
        // implicit: status.rentReadinessStatus().setValue(null);

        status.rentedStatus().setValue(RentedStatus.Unrented);
        // implicit: status.rentedFromDate().setValue(null);
    }

    private void available(UnitAvailabilityStatus status, List<AptUnitOccupancySegment> future, LogicalDate vacantSince) {
        status.vacancyStatus().setValue(Vacancy.Vacant);
        status.vacantSince().setValue(vacantSince);
        // implicit: status.moveOutDay().setValue(null);

        status.scoping().setValue(Scoping.Scoped);
        status.rentReadinessStatus().setValue(RentReadiness.RentReady);

        status.rentedStatus().setValue(RentedStatus.Unrented);
        // implicit: status.rentedFromDate().setValue(null);

        for (AptUnitOccupancySegment segment : future) {
            if (segment.status().getValue() == Status.leased | segment.status().getValue() == Status.reserved) {
                status.rentedStatus().setValue(RentedStatus.Rented);
                status.rentedFromDay().setValue(segment.lease().leaseFrom().getValue());
                status.moveInDay().setValue(segment.lease().expectedMoveIn().getValue());
                break;
            }
        }
    }

    private void reserved(UnitAvailabilityStatus status, AptUnitOccupancySegment current, LogicalDate vacantSince) {
        status.vacancyStatus().setValue(Vacancy.Vacant);
        status.vacantSince().setValue(vacantSince);
        // implicit: status.moveOutDay().setValue(null)

        status.scoping().setValue(Scoping.Scoped);
        status.rentReadinessStatus().setValue(RentReadiness.RentReady);

        status.rentedStatus().setValue(RentedStatus.Rented);
        status.rentedFromDay().setValue(current.lease().leaseFrom().getValue());
        status.moveInDay().setValue(current.lease().expectedMoveIn().getValue());
    }

    private void leased(UnitAvailabilityStatus status, AptUnitOccupancySegment current, List<AptUnitOccupancySegment> future) {

        Iterator<AptUnitOccupancySegment> segments = future.iterator();
        AptUnitOccupancySegment segment = null;

        if (segments.hasNext()) {
            segment = segments.next();

            status.vacancyStatus().setValue(Vacancy.Notice);
            status.rentEndDay().setValue(current.dateTo().getValue());

            status.rentedStatus().setValue(RentedStatus.Unrented);
            // implicit: status.rentedFromDate().setValue(null);
        }

        while (segment != null) {
            switch (segment.status().getValue()) {
            case vacant:
                status.scoping().setValue(Scoping.Unscoped);
                // implicit status.rentReadinessStatus().setValue(null);
                break;
            case available:
                if (status.scoping().getValue() == null) {
                    status.scoping().setValue(Scoping.Scoped);
                    status.rentReadinessStatus().setValue(RentReadiness.RentReady);
                }
                break;
            case renovation:
                if (status.scoping().getValue() == null) {
                    status.scoping().setValue(Scoping.Scoped);
                    status.rentReadinessStatus().setValue(RentReadiness.NeedsRepairs);
                }
                break;
            case offMarket:
                if (status.rentedStatus() != null) {
                    status.scoping().setValue(Scoping.Scoped);
                    status.rentedStatus().setValue(RentedStatus.OffMarket);
                }
                break;
            case reserved:
            case leased:
                if (status.rentedStatus() != null) {
                    if (status.scoping().getValue() == null) {
                        status.scoping().setValue(Scoping.Scoped);
                        status.rentReadinessStatus().setValue(RentReadiness.RentReady);
                    }
                    status.rentedStatus().setValue(RentedStatus.Rented);
                    status.rentedFromDay().setValue(segment.lease().leaseFrom().getValue());
                    status.moveInDay().setValue(segment.lease().expectedMoveIn().getValue());
                }
                break;
            default:
                // do nothing
            }

            segment = segments.hasNext() ? segments.next() : null;
        }

    }

    private void renovation(UnitAvailabilityStatus status, List<AptUnitOccupancySegment> future) {
        status.vacancyStatus().setValue(Vacancy.Vacant);

        status.scoping().setValue(Scoping.Scoped);
        status.rentReadinessStatus().setValue(RentReadiness.RenoInProgress);

        status.rentedStatus().setValue(RentedStatus.Unrented);
        // implicit: status.rentedFromDate().setValue(null);

        for (AptUnitOccupancySegment segment : future) {
            if (segment.status().getValue() == Status.leased | segment.status().getValue() == Status.reserved) {
                status.rentedStatus().setValue(RentedStatus.Rented);
                status.rentedFromDay().setValue(segment.lease().leaseFrom().getValue());
                status.moveInDay().setValue(segment.lease().expectedMoveIn().getValue());
                break;
            }
        }
    }

    private void offMarket(UnitAvailabilityStatus status) {
        status.vacancyStatus().setValue(Vacancy.Vacant);

        status.scoping().setValue(Scoping.Scoped);
        // implicit: status.rentReadinessStatus().setValue(null);

        status.rentedStatus().setValue(RentedStatus.OffMarket);
        // implicit: status.rentedFromDate().setValue(null);
    }

    private void setReferences(UnitAvailabilityStatus status) {
        status.unit().set(unit);
        status.building().set(unit.belongsTo());

        // REFERENCED DATA
        if (unit.belongsTo().isValueDetached()) {
            Persistence.service().retrieveMember(unit.belongsTo());
        }
        status.propertyCode().setValue(unit.belongsTo().propertyCode().getValue());
        status.buildingName().setValue(unit.belongsTo().info().name().getValue());
        status.address().set(unit.belongsTo().info().address());
        status.complexName().setValue(unit.belongsTo().complex().name().getValue());
        status.unitName().setValue(unit.info().number().getValue());

        if (unit.floorplan().isValueDetached()) {
            Persistence.service().retrieveMember(unit.floorplan());
        }
        status.floorplanName().setValue(unit.floorplan().name().getValue());
        status.floorplanMarketingName().setValue(unit.floorplan().marketingName().getValue());

        if (status.rentedStatus().getValue() == RentedStatus.Rented) {
            // TODO set the rest of stuff - fill unit rent and market rent
        }

    }

    private AptUnitOccupancySegment first(List<AptUnitOccupancySegment> occupancy) {
        return occupancy.get(0);
    }

    private List<AptUnitOccupancySegment> rest(List<AptUnitOccupancySegment> occupancy) {
        int length = occupancy.size();
        if (length == 1) {
            return Collections.emptyList();
        } else {
            return occupancy.subList(1, length);
        }
    }

    private void removeStatuses(LogicalDate startingOn) {
        EntityQueryCriteria<UnitAvailabilityStatus> criteria = new EntityQueryCriteria<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unit));
        criteria.add(PropertyCriterion.ge(criteria.proto().statusDate(), startingOn));
        Persistence.service().delete(criteria);
    }
}
