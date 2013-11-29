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
package com.propertyvista.biz.occupancy;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.RentReadiness;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.RentedStatus;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.Scoping;
import com.propertyvista.domain.dashboard.gadgets.availability.UnitAvailabilityStatus.Vacancy;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;

// TODO refactor a little bit, create status setting function for various cases and use them 
public class AvailabilityReportManager {

    private final AptUnit unit;

    public static void generateUnitAvailability(Key unitId, LogicalDate startingOn) {
        new AvailabilityReportManager(unitId).generateUnitAvailablity(startingOn);
    }

    public AvailabilityReportManager(AptUnit unit) {
        assert unit != null;
        this.unit = unit;
    }

    public AvailabilityReportManager(Key unitId) {
        this(Persistence.secureRetrieve(AptUnit.class, unitId));
    }

    /**
     * Refresh the series of availability statuses based on occupancy: remove the old statuses from the DB, and create new ones.
     * 
     * @param startingOn
     *            Start the computation of statuses on this date.
     */
    public void generateUnitAvailablity(LogicalDate startingOn) {
        removeFuture(startingOn);
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
            status.statusFrom().setValue(current.dateFrom().getValue());
            status.statusUntil().setValue(current.dateTo().getValue());

            switch (current.status().getValue()) {
            case pending:
                vacant(status, current.dateFrom().getValue());
                break;
            case available:
                available(status, future, current.dateFrom().getValue());
                break;
            case reserved:
                reserved(status, current, current.dateFrom().getValue());
                break;
            case occupied:
                leased(status, current, future);
                break;
            case renovation:
                renovation(status, future);
                break;
            case offMarket:
                offMarket(status);
                break;
            case migrated:
                leased(status, current, future);
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
        status.rentEndDay().setValue(null);

        status.scoping().setValue(Scoping.Scoped);
        status.rentReadinessStatus().setValue(RentReadiness.RentReady);

        status.rentedStatus().setValue(RentedStatus.Unrented);
        status.rentedFromDay().setValue(null);

        for (AptUnitOccupancySegment segment : future) {
            if (segment.status().getValue() == Status.occupied | segment.status().getValue() == Status.reserved) {
                status.rentedStatus().setValue(RentedStatus.Rented);
                status.rentedFromDay().setValue(segment.lease().currentTerm().termFrom().getValue());
                status.moveInDay().setValue(segment.lease().expectedMoveIn().getValue());
                status.unitRent().setValue(segment.lease().currentTerm().version().leaseProducts().serviceItem().agreedPrice().getValue());
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
        status.rentedFromDay().setValue(current.lease().currentTerm().termFrom().getValue());
        status.moveInDay().setValue(current.lease().expectedMoveIn().getValue());
        status.unitRent().setValue(current.lease().currentTerm().version().leaseProducts().serviceItem().agreedPrice().getValue());
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
            case pending:
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
            case occupied:
                if (status.rentedStatus() != null) {
                    if (status.scoping().getValue() == null) {
                        status.scoping().setValue(Scoping.Scoped);
                        status.rentReadinessStatus().setValue(RentReadiness.RentReady);
                    }
                    status.rentedStatus().setValue(RentedStatus.Rented);
                    status.rentedFromDay().setValue(segment.lease().currentTerm().termFrom().getValue());
                    status.moveInDay().setValue(segment.lease().expectedMoveIn().getValue());
                    status.unitRent().setValue(segment.lease().currentTerm().version().leaseProducts().serviceItem().agreedPrice().getValue());
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
            if (segment.status().getValue() == Status.occupied | segment.status().getValue() == Status.reserved) {
                status.rentedStatus().setValue(RentedStatus.Rented);
                status.rentedFromDay().setValue(segment.lease().currentTerm().termFrom().getValue());
                status.moveInDay().setValue(segment.lease().expectedMoveIn().getValue());
                status.unitRent().setValue(segment.lease().currentTerm().version().leaseProducts().serviceItem().agreedPrice().getValue());
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
        status.building().set(unit.building());
        status.floorplan().set(unit.floorplan());

        BigDecimal marketRent = getMarketRent();
        BigDecimal unitRent = status.unitRent().getValue();

        MathContext ctx = MathContext.DECIMAL128;
        status.unitRent().setValue(unitRent);
        status.marketRent().setValue(marketRent);

        if (unitRent != null & marketRent != null) {
            status.rentDeltaAbsolute().setValue(unitRent.subtract(marketRent, ctx));
            if (marketRent.compareTo(BigDecimal.ZERO) != 0) {
                status.rentDeltaRelative().setValue(status.rentDeltaAbsolute().getValue().divide(marketRent, ctx));
            }
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

    private void removeFuture(LogicalDate startingOn) {
        // update current's status date to
        EntityQueryCriteria<UnitAvailabilityStatus> criteriaCurrrent = new EntityQueryCriteria<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);
        criteriaCurrrent.add(PropertyCriterion.eq(criteriaCurrrent.proto().unit(), unit));
        criteriaCurrrent.add(PropertyCriterion.ge(criteriaCurrrent.proto().statusUntil(), startingOn));
        criteriaCurrrent.add(PropertyCriterion.le(criteriaCurrrent.proto().statusFrom(), startingOn));
        UnitAvailabilityStatus currentStatus = Persistence.service().retrieve(criteriaCurrrent);
        if (currentStatus != null) {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(startingOn);
            cal.add(GregorianCalendar.DAY_OF_YEAR, -1);
            currentStatus.statusUntil().setValue(new LogicalDate(cal.getTime()));
            Persistence.service().merge(currentStatus);
        }

        // remove all future statuses
        EntityQueryCriteria<UnitAvailabilityStatus> criteriaFuture = new EntityQueryCriteria<UnitAvailabilityStatus>(UnitAvailabilityStatus.class);
        criteriaFuture.add(PropertyCriterion.eq(criteriaFuture.proto().unit(), unit));
        criteriaFuture.add(PropertyCriterion.ge(criteriaFuture.proto().statusFrom(), startingOn));
        Persistence.service().delete(criteriaFuture);

    }

    private BigDecimal getMarketRent() {
        BigDecimal residentialUnitMarketRent = null;

        EntityQueryCriteria<Service> criteria = EntityQueryCriteria.create(Service.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().catalog().building(), unit.building()));
        criteria.add(PropertyCriterion.in(criteria.proto().code().type(), ARCode.Type.unitRelatedServices()));

        for (Service service : Persistence.secureQuery(criteria)) {
            Persistence.service().retrieve(service.version().items());
            for (ProductItem item : service.version().items()) {
                if (item.element() == null || item.element().isNull()) {
                    System.out.println("++++++++++++++==");
                }
                if (item.element().getInstanceValueClass().equals(AptUnit.class) & item.element().getPrimaryKey().equals(unit.getPrimaryKey())) {
                    if (service.code().type().getValue() == ARCode.Type.Residential) {
                        residentialUnitMarketRent = item.price().getValue();
                        break;
                    }
                }
            }
        }

        return residentialUnitMarketRent;
    }
}
