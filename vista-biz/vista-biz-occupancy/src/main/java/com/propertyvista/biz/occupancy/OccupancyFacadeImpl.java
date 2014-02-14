/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.occupancy;

import static com.propertyvista.biz.occupancy.AptUnitOccupancyManagerHelper.addDay;
import static com.propertyvista.biz.occupancy.AptUnitOccupancyManagerHelper.assertStatus;
import static com.propertyvista.biz.occupancy.AptUnitOccupancyManagerHelper.merge;
import static com.propertyvista.biz.occupancy.AptUnitOccupancyManagerHelper.retrieveOccupancySegment;
import static com.propertyvista.biz.occupancy.AptUnitOccupancyManagerHelper.split;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.Pair;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.AndCriterion;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.occupancy.AptUnitOccupancyManagerHelper.MergeHandler;
import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.CancelMoveOutConstraintsDTO;
import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.CancelMoveOutConstraintsDTO.ConstraintsReason;
import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.MakeVacantConstraintsDTO;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitEffectiveAvailability;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.misc.VistaTODO;

public class OccupancyFacadeImpl implements OccupancyFacade {

    private static final I18n i18n = I18n.get(OccupancyFacadeImpl.class);

    @Override
    public AptUnitOccupancySegment getOccupancySegment(AptUnit unit, LogicalDate date) {
        return retrieveOccupancySegment(unit, date);
    }

    @Override
    public void setupNewUnit(AptUnit unit) {
        setUnitAvailableFrom(unit.getPrimaryKey(), null);

        // for new unit, create a vacant occupancy segment:
        AptUnitOccupancySegment vacant = EntityFactory.create(AptUnitOccupancySegment.class);
        vacant.unit().set(unit);
        vacant.status().setValue(Status.pending);
        vacant.dateFrom().setValue(new LogicalDate(SystemDateManager.getDate()));
        vacant.dateTo().setValue(OccupancyFacade.MAX_DATE);
        Persistence.service().persist(vacant);
    }

    @Override
    public void scopeAvailable(Key unitPk) {
        LogicalDate now = new LogicalDate(SystemDateManager.getDate());

        if (!isInProductCatalog(unitPk)) {
            throw new UserRuntimeException(i18n.tr("Unable to make this unit available because the unit is not present in product catalog"));
        }

        if (AptUnitOccupancyManagerHelper.isOccupancyListEmpty(unitPk)) {
            createAvailableSegment(unitPk, now);
            return; // newly created unit case!..
        }

        if (!isScopeAvailableAvailable(unitPk)) {
            throw new UserRuntimeException(i18n.tr("operation 'scope available' is not possible in current unit state"));
        }

        List<AptUnitOccupancySegment> occupancy = AptUnitOccupancyManagerHelper.retrieveOccupancy(unitPk, now);

        // Find the first pending segment with undefined end and convert it
        AptUnitOccupancySegment firstPendingSegment = null;
        LogicalDate splittingDay = null;
        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.pending & segment.dateTo().getValue().equals(OccupancyFacade.MAX_DATE)) {
                firstPendingSegment = segment;
                splittingDay = firstPendingSegment.dateFrom().getValue().after(now) ? firstPendingSegment.dateFrom().getValue() : now;
                break;
            }
        }
        if (splittingDay == null) {
            throw new IllegalStateException("pending segment was not found");
        }

        AptUnitOccupancyManagerHelper.split(firstPendingSegment, splittingDay, new SplittingHandler() {

            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {

            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.available);
            }
        });

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(now);
        cal.add(GregorianCalendar.DAY_OF_YEAR, -1);

        // merge if previous was available
        AptUnitOccupancySegment prevSegment = AptUnitOccupancyManagerHelper.retrieveOccupancySegment(unitPk, new LogicalDate(cal.getTime()));
        if (prevSegment != null && (prevSegment.status().getValue() == Status.available)) {

            // remove the segment we have just created:
            // TODO (i know that this is very stupid stupid and has to
            // refactored)
            EntityQueryCriteria<AptUnitOccupancySegment> criteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
            criteria.add(PropertyCriterion.ne(criteria.proto().id(), prevSegment.getPrimaryKey()));
            criteria.add(PropertyCriterion.eq(criteria.proto().status(), Status.available));
            criteria.add(PropertyCriterion.ge(criteria.proto().dateTo(), now));
            Persistence.service().delete(criteria);

            prevSegment.dateTo().setValue(OccupancyFacade.MAX_DATE);
            Persistence.service().persist(prevSegment);
        }
        setUnitAvailableFrom(unitPk, splittingDay);

        new AvailabilityReportManager(unitPk).generateUnitAvailablity(now);
    }

    @Override
    public void scopeOffMarket(Key unitPk, final OffMarketType type) {
        LogicalDate now = new LogicalDate(SystemDateManager.getDate());
        if (AptUnitOccupancyManagerHelper.isOccupancyListEmpty(unitPk)) {
            AptUnitOccupancySegment segment = EntityFactory.create(AptUnitOccupancySegment.class);
            segment.status().setValue(Status.offMarket);
            segment.offMarket().setValue(type);
            segment.dateFrom().setValue(now);
            segment.dateTo().setValue(OccupancyFacade.MAX_DATE);
            segment.lease().setValue(null);
            segment.unit().setPrimaryKey(unitPk);
            Persistence.service().merge(segment);
            new AvailabilityReportManager(unitPk).generateUnitAvailablity(now);
            return; // newly created unit case!..
        }

        List<AptUnitOccupancySegment> occupancy = AptUnitOccupancyManagerHelper.retrieveOccupancy(unitPk, now);
        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.pending) {
                LogicalDate splitDay = segment.dateFrom().getValue().before(now) ? now : segment.dateFrom().getValue();
                AptUnitOccupancyManagerHelper.split(segment, splitDay, new SplittingHandler() {
                    @Override
                    public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
                        // segment must be vacant
                    }

                    @Override
                    public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                        segment.status().setValue(Status.offMarket);
                        segment.offMarket().setValue(type);
                        segment.lease().setValue(null);
                    }
                });
                new AvailabilityReportManager(unitPk).generateUnitAvailablity(now);
                return;
            }
        }

        throw new IllegalStateException("a vacant segment was not found");
    }

    @Override
    public void scopeRenovation(Key unitPk, LogicalDate renovationEndDate) {
        LogicalDate now = new LogicalDate(SystemDateManager.getDate());
        if (AptUnitOccupancyManagerHelper.isOccupancyListEmpty(unitPk)) {
            AptUnitOccupancySegment segment = EntityFactory.create(AptUnitOccupancySegment.class);
            segment.status().setValue(Status.pending);
            segment.dateFrom().setValue(now);
            segment.dateTo().setValue(OccupancyFacade.MAX_DATE);
            segment.lease().setValue(null);
            segment.unit().setPrimaryKey(unitPk);
            Persistence.service().merge(segment);
        }

        List<AptUnitOccupancySegment> occupancy = AptUnitOccupancyManagerHelper.retrieveOccupancy(unitPk, renovationEndDate);
        for (AptUnitOccupancySegment seg : occupancy) {
            if (seg.status().getValue() == Status.pending) {
                LogicalDate renoStartDay = seg.dateFrom().getValue().before(now) ? now : seg.dateFrom().getValue();
                if (!(renoStartDay.before(renovationEndDate) | renoStartDay.equals(renovationEndDate))) {
                    throw new IllegalStateException("reno end day is less then reno start day");
                }
                AptUnitOccupancySegment renoSeg = split(seg, renoStartDay, new SplittingHandler() {
                    @Override
                    public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
                        // we already checked that;
                    }

                    @Override
                    public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                        segment.status().setValue(Status.renovation);
                    }
                });
                split(renoSeg, addDay(renovationEndDate), new SplittingHandler() {

                    @Override
                    public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {

                    }

                    @Override
                    public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                        segment.status().setValue(Status.available);
                    }
                });
                // join segments if needed
                GregorianCalendar cal = new GregorianCalendar();
                cal.setTime(renoStartDay);
                cal.add(GregorianCalendar.DAY_OF_YEAR, -1);

                AptUnitOccupancySegment prevSegment = AptUnitOccupancyManagerHelper.retrieveOccupancySegment(unitPk, new LogicalDate(cal.getTime()));

                if (prevSegment != null && (prevSegment.status().getValue() == Status.renovation)) {
                    EntityQueryCriteria<AptUnitOccupancySegment> deleteRenovatioSegmentCriteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
                    deleteRenovatioSegmentCriteria.add(PropertyCriterion.eq(deleteRenovatioSegmentCriteria.proto().dateFrom(), renoStartDay));
                    deleteRenovatioSegmentCriteria.add(PropertyCriterion.eq(deleteRenovatioSegmentCriteria.proto().unit(), unitPk));
                    Persistence.service().delete(deleteRenovatioSegmentCriteria);

                    prevSegment.dateTo().setValue(renovationEndDate);
                    Persistence.service().persist(prevSegment);
                }
                setUnitAvailableFrom(unitPk, addDay(renovationEndDate));
                new AvailabilityReportManager(unitPk).generateUnitAvailablity(now);
                return;
            }
        }
        throw new IllegalStateException("vacant segment was not found");
    }

    @Override
    public void makeVacant(Key unitPk, LogicalDate vacantFrom) {
        if (vacantFrom == null) {
            throw new IllegalArgumentException("vacantFrom must not be null");
        }
        LogicalDate now = new LogicalDate(SystemDateManager.getDate());
        MakeVacantConstraintsDTO constraints = getMakeVacantConstraints(unitPk);
        LogicalDate min = constraints.minVacantFrom().getValue();
        LogicalDate max = constraints.maxVacantFrom().getValue();
        if (// @formatter:off
		constraints == null
				|| !((vacantFrom.after(min) | vacantFrom.equals(min)) & (max == null || (vacantFrom
						.before(max) | vacantFrom.equals(max))))) { // @formatter:on
            throw new IllegalArgumentException(SimpleMessageFormat.format("vacantFrom {0} doesn't match the constraints", vacantFrom));
        }

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(vacantFrom);
        cal.add(GregorianCalendar.DAY_OF_YEAR, -1);
        AptUnitOccupancySegment preMakeVacantDateSegment = retrieveOccupancySegment(unitPk, new LogicalDate(cal.getTime()));

        if (preMakeVacantDateSegment != null && preMakeVacantDateSegment.status().getValue() == Status.pending) {

            EntityQueryCriteria<AptUnitOccupancySegment> deleteCriteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);

            deleteCriteria.add(PropertyCriterion.eq(deleteCriteria.proto().unit(), unitPk));
            deleteCriteria.add(PropertyCriterion.ge(deleteCriteria.proto().dateTo(), vacantFrom));
            deleteCriteria.add(PropertyCriterion.ne(deleteCriteria.proto().id(), preMakeVacantDateSegment));

            Persistence.service().delete(deleteCriteria);

            preMakeVacantDateSegment.dateTo().setValue(OccupancyFacade.MAX_DATE);
            Persistence.service().merge(preMakeVacantDateSegment);
        } else {
            AptUnitOccupancySegment makeVacantStartSegment = retrieveOccupancySegment(unitPk, vacantFrom);
            split(makeVacantStartSegment, vacantFrom, new SplittingHandler() {
                @Override
                public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
                }

                @Override
                public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                    segment.status().setValue(Status.pending);
                    segment.offMarket().setValue(null);
                    segment.dateTo().setValue(OccupancyFacade.MAX_DATE);
                }
            });

            EntityQueryCriteria<AptUnitOccupancySegment> deleteCriteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
            deleteCriteria.add(PropertyCriterion.eq(deleteCriteria.proto().unit(), unitPk));
            deleteCriteria.add(PropertyCriterion.ge(deleteCriteria.proto().dateTo(), vacantFrom));
            deleteCriteria.add(PropertyCriterion.ne(deleteCriteria.proto().status(), Status.pending));
            Persistence.service().delete(deleteCriteria);
        }

        setUnitAvailableFrom(unitPk, null);
        new AvailabilityReportManager(unitPk).generateUnitAvailablity(now);
    }

    @Override
    public void reserve(Key unitPk, final Lease lease) {
        LogicalDate leaseFrom = lease.currentTerm().termFrom().getValue();
        LogicalDate now = new LogicalDate(SystemDateManager.getDate());

        // FIXME
        if (VistaTODO.checkLeaseDatesOnUnitReservation & leaseFrom.before(now)) {
            throw new IllegalStateException(i18n.tr("Operation 'reserve unit' is not permitted, the lease from date ({0}) is before the present date ({1})",
                    leaseFrom, now));
        }

        if (AptUnitOccupancyManagerHelper.isOccupancyListEmpty(unitPk)) {
            createAvailableSegment(unitPk, leaseFrom);
        }

        List<AptUnitOccupancySegment> occupancy = AptUnitOccupancyManagerHelper.retrieveOccupancy(unitPk, leaseFrom);
        if (occupancy.size() != 1) {
            throw new UserRuntimeException(i18n.tr("Operation 'reserve unit' is not permitted, the unit is not available after {0}", leaseFrom));
        }
        AptUnitOccupancySegment segment = occupancy.get(0);
        assertStatus(segment, Status.available);

        LogicalDate reservedFrom = now.before(segment.dateFrom().getValue()) ? segment.dateFrom().getValue() : now;

        split(segment, reservedFrom, new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
                assertStatus(segment, Status.available);
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.reserved);
                segment.lease().set(lease);
            }
        });
        setUnitAvailableFrom(unitPk, null);
        new AvailabilityReportManager(unitPk).generateUnitAvailablity(now);
    }

    @Override
    public void unreserve(Key unitPk) {
        LogicalDate now = new LogicalDate(SystemDateManager.getDate());
        List<AptUnitOccupancySegment> futureOccupancy = AptUnitOccupancyManagerHelper.retrieveOccupancy(unitPk, now);
        AptUnitOccupancySegment reservedSegment = null;
        for (AptUnitOccupancySegment futureSegment : futureOccupancy) {
            if (futureSegment.status().getValue() == Status.reserved) {
                reservedSegment = futureSegment;
                break;
            }
        }
        if (reservedSegment == null) {
            throw new UserRuntimeException(i18n.tr("This unit is not reserved!"));
        }

        LogicalDate unreserveFrom = reservedSegment.dateFrom().getValue().after(now) ? reservedSegment.dateFrom().getValue() : now;

        split(reservedSegment, unreserveFrom, new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
            }
        });
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        criteria.add(PropertyCriterion.ge(criteria.proto().dateFrom(), unreserveFrom));
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unitPk));
        Persistence.service().delete(criteria);

        AptUnitOccupancySegment availableSegment = EntityFactory.create(AptUnitOccupancySegment.class);
        availableSegment.unit().setPrimaryKey(unitPk);
        availableSegment.status().setValue(Status.available);
        availableSegment.dateFrom().setValue(unreserveFrom);
        availableSegment.dateTo().setValue(OccupancyFacade.MAX_DATE);
        Persistence.service().persist(availableSegment);

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(unreserveFrom);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        merge(unitPk, new LogicalDate(calendar.getTime()), Arrays.asList(Status.available), new MergeHandler() {
            @Override
            public void onMerged(AptUnitOccupancySegment merged, AptUnitOccupancySegment s1, AptUnitOccupancySegment s2) {
                merged.status().setValue(Status.available);
            }

            @Override
            public boolean isMergeable(AptUnitOccupancySegment s1, AptUnitOccupancySegment s2) {
                return true;
            }
        });
        new AvailabilityReportManager(unitPk).generateUnitAvailablity(now);
        setUnitAvailableFrom(unitPk, unreserveFrom);
    }

    @Override
    public void occupy(Lease leaseId) {
        Lease lease = leaseId.duplicate();
        LogicalDate now = new LogicalDate(SystemDateManager.getDate());
        Persistence.ensureRetrieve(lease.unit(), AttachLevel.Attached);
        List<AptUnitOccupancySegment> occupancy = AptUnitOccupancyManagerHelper.retrieveOccupancy(lease.unit().getPrimaryKey(), now);
        AptUnitOccupancySegment segmentToOccupy = null;

        Pair<Date, Lease> reservation = isReserved(lease.unit().getPrimaryKey());
        if (reservation.getB() != null && !reservation.getB().equals(lease)) {
            throw new IllegalStateException("the unit pk=" + lease.unit().getPrimaryKey() + " is reserved for lease pk=" + reservation.getB().getPrimaryKey()
                    + ": cannot occupy it for lease pk=" + lease.getPrimaryKey());
        }

        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.available || segment.status().getValue() == Status.reserved) {
                segmentToOccupy = segment;
                break;
            }
        }

        if (segmentToOccupy != null) {
            split(segmentToOccupy, lease.currentTerm().termFrom().getValue(), new SplittingHandler() {
                @Override
                public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
                    // already checked that we are in 'reserved'
                }

                @Override
                public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                    segment.status().setValue(Status.occupied);
                }
            });

            new AvailabilityReportManager(lease.unit().getPrimaryKey()).generateUnitAvailablity(now);
            return;
        } else {
            throw new IllegalStateException("'approveLease' operation failed: a 'reserved' or 'available' segment was not found");
        }
    }

    @Override
    public void moveOut(Key unitPk, LogicalDate moveOutDate, Lease leaseId) throws OccupancyOperationException {
        assert unitPk != null;
        assert moveOutDate != null;

        LogicalDate unitAvailableFrom = null;
        LogicalDate now = new LogicalDate(SystemDateManager.getDate());

        EntityQueryCriteria<AptUnitOccupancySegment> occupiedSegmentCriteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        occupiedSegmentCriteria.add(PropertyCriterion.eq(occupiedSegmentCriteria.proto().unit(), unitPk));
        occupiedSegmentCriteria.add(PropertyCriterion.eq(occupiedSegmentCriteria.proto().lease(), leaseId));
        occupiedSegmentCriteria.add(PropertyCriterion.eq(occupiedSegmentCriteria.proto().status(), AptUnitOccupancySegment.Status.occupied));
        AptUnitOccupancySegment occupiedSegment = Persistence.service().retrieve(occupiedSegmentCriteria);
        if (occupiedSegment == null) {
            throw new OccupancyOperationException(
                    i18n.tr("Occupied segment was not found. Please ensure that the unit is not leased, reserved or scheduled for renovation"));
        }
        if (moveOutDate.getTime() < occupiedSegment.lease().leaseFrom().getValue().getTime()) {
            throw new OccupancyOperationException(i18n.tr("Impossible to move out: move out date is before lease start date"));
        }
        // get the next segment or create a new next segment if a next segment doesn't exist
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(occupiedSegment.dateTo().getValue());
        cal.add(GregorianCalendar.DAY_OF_YEAR, 1);
        LogicalDate nextSegmentStartDay = new LogicalDate(cal.getTime());
        AptUnitOccupancySegment nextSegment = retrieveOccupancySegment(unitPk, nextSegmentStartDay);
        if (nextSegment == null) {
            nextSegment = EntityFactory.create(AptUnitOccupancySegment.class);
            nextSegment.dateTo().setValue(OccupancyFacade.MAX_DATE);
            nextSegment.unit().setPrimaryKey(unitPk);
            nextSegment.status().setValue(Status.pending);
        }
        // update the occupied segment (we don't want to persist the changes right now because we wan't to roll back
        occupiedSegment.dateTo().setValue(moveOutDate);

        cal.setTime(moveOutDate);
        cal.add(GregorianCalendar.DAY_OF_YEAR, 1);
        LogicalDate dayAfterMoveOutDate = new LogicalDate(cal.getTime());

        switch (nextSegment.status().getValue()) {
        case migrated:
            throw new OccupancyOperationException(i18n.tr("It's impossible to move out, since this unit is leased in the future"));
        case available:
            unitAvailableFrom = dayAfterMoveOutDate;
        case pending:
            nextSegment.dateFrom().setValue(dayAfterMoveOutDate);
            Persistence.service().merge(nextSegment);
            break;
        case reserved:
            if (nextSegment.lease().leaseFrom().getValue().getTime() < dayAfterMoveOutDate.getTime()) {
                throw new OccupancyOperationException(i18n.tr("It's imposible to move out, since the unit is reserved on move out date"));
            }
            nextSegment.dateFrom().setValue(dayAfterMoveOutDate);
            Persistence.service().merge(nextSegment);
            break;
        case occupied:
            if (nextSegment.dateFrom().getValue().before(dayAfterMoveOutDate)) {
                throw new OccupancyOperationException(i18n.tr("It's imposible to move out, since the unit is leased on move out date"));
            } else {
                AptUnitOccupancySegment reservedSegment = EntityFactory.create(AptUnitOccupancySegment.class);
                reservedSegment.status().setValue(Status.reserved);
                reservedSegment.dateFrom().setValue(dayAfterMoveOutDate);

                cal.setTime(nextSegment.dateFrom().getValue());
                cal.add(GregorianCalendar.DAY_OF_YEAR, -1);
                reservedSegment.dateTo().setValue(new LogicalDate(cal.getTime()));
                reservedSegment.unit().setPrimaryKey(unitPk);
                reservedSegment.lease().setPrimaryKey(nextSegment.lease().getPrimaryKey());

                Persistence.service().merge(reservedSegment);
            }
            break;
        case offMarket:
        case renovation:
            if (dayAfterMoveOutDate.compareTo(nextSegment.dateFrom().getValue()) >= 0) {
                throw new OccupancyOperationException(i18n.tr("It's imposible to move out, since the unit is either offMarket or renovated"));
            } else {
                AptUnitOccupancySegment pendingSegment = EntityFactory.create(AptUnitOccupancySegment.class);
                pendingSegment.status().setValue(Status.pending);
                pendingSegment.dateFrom().setValue(dayAfterMoveOutDate);

                cal.setTime(nextSegment.dateFrom().getValue());
                cal.add(GregorianCalendar.DAY_OF_YEAR, -1);
                pendingSegment.dateTo().setValue(new LogicalDate(cal.getTime()));
                pendingSegment.unit().setPrimaryKey(unitPk);
                Persistence.service().merge(pendingSegment);
            }
            break;
        default:
            throw new IllegalStateException("cannot perform move out operation: expected occupancy segment not found");
        }

        // persist occupied segment
        Persistence.service().merge(occupiedSegment);

        // if the move out date of the occupied segment was moved forward, and the occupied segment has overlapped the next one, we need to delete next segment
        if (nextSegment.dateTo().getValue().compareTo(occupiedSegment.dateTo().getValue()) <= 0) {
            Persistence.service().delete(nextSegment);
        }
        new AvailabilityReportManager(unitPk).generateUnitAvailablity(now);
        setUnitAvailableFrom(unitPk, unitAvailableFrom);
    }

    @Override
    public void cancelMoveOut(Key unitPk) throws OccupancyOperationException {
        assert unitPk != null;

        CancelMoveOutConstraintsDTO constraints = getCancelMoveOutConstraints(unitPk);
        if (!constraints.canCancelMoveOut().isBooleanTrue()) {
            throw new OccupancyOperationException("cancelMoveOut cannot be performed due to: " + constraints.reason().getValue());
        }

        LogicalDate now = new LogicalDate(SystemDateManager.getDate());
        AptUnitOccupancySegment leasedSegment = retrieveOccupancySegment(unitPk, now);
        EntityQueryCriteria<AptUnitOccupancySegment> delete = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        delete.add(PropertyCriterion.ge(delete.proto().dateFrom(), addDay(leasedSegment.dateTo().getValue())));
        Persistence.service().delete(delete);
        leasedSegment.dateTo().setValue(OccupancyFacade.MAX_DATE);
        Persistence.service().merge(leasedSegment);

        new AvailabilityReportManager(unitPk).generateUnitAvailablity(now);
        setUnitAvailableFrom(unitPk, null);
    }

    @Override
    public boolean isScopeOffMarketAvailable(Key unitPk) {
        LogicalDate start = new LogicalDate(SystemDateManager.getDate());
        if (AptUnitOccupancyManagerHelper.isOccupancyListEmpty(unitPk)) {
            return true; // newly created unit - can be scoped to any state!..
        }

        List<AptUnitOccupancySegment> occupancy = AptUnitOccupancyManagerHelper.retrieveOccupancy(unitPk, start);
        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.pending) {
                return true;
            }
        }

        return false;
    }

    @Override
    public LogicalDate isRenovationAvailable(Key unitPk) {
        LogicalDate start = new LogicalDate(SystemDateManager.getDate());
        if (AptUnitOccupancyManagerHelper.isOccupancyListEmpty(unitPk)) {
            return start; // newly created unit - can be scoped to any state!..
        }

        List<AptUnitOccupancySegment> occupancy = AptUnitOccupancyManagerHelper.retrieveOccupancy(unitPk, start);
        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.pending) {
                LogicalDate vacantStart = segment.dateFrom().getValue();
                return vacantStart.before(start) ? start : vacantStart;
            }
        }

        return null;
    }

    @Override
    public boolean isScopeAvailableAvailable(Key unitPk) {
        LogicalDate start = new LogicalDate(SystemDateManager.getDate());

        // Check if the unit has entry in the Product Catalog:
        EntityQueryCriteria<AptUnit> criteria = new EntityQueryCriteria<AptUnit>(AptUnit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().id(), unitPk));

        // and finalized current Product only:
        criteria.add(PropertyCriterion.isNotNull(criteria.proto().productItems().$().product().fromDate()));
        criteria.add(PropertyCriterion.isNull(criteria.proto().productItems().$().product().toDate()));

        if (Persistence.service().exists(criteria)) {
            if (AptUnitOccupancyManagerHelper.isOccupancyListEmpty(unitPk)) {
                return true; // newly created unit - can be scoped to any
                             // state!..
            }

            List<AptUnitOccupancySegment> occupancy = AptUnitOccupancyManagerHelper.retrieveOccupancy(unitPk, start);
            for (AptUnitOccupancySegment segment : occupancy) {
                if (segment.status().getValue() == Status.pending & segment.dateTo().getValue().equals(OccupancyFacade.MAX_DATE)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public MakeVacantConstraintsDTO getMakeVacantConstraints(Key unitPk) {
        LogicalDate start = new LogicalDate(SystemDateManager.getDate());
        List<AptUnitOccupancySegment> occupancy = AptUnitOccupancyManagerHelper.retrieveOccupancy(unitPk, start);

        LogicalDate minVacantFromCandidate = null;
        LogicalDate maxVacantFromCandidate = null;
        for (AptUnitOccupancySegment segment : occupancy) {
            Status segStatus = segment.status().getValue();
            if ((segStatus == Status.reserved | segStatus == Status.occupied) & minVacantFromCandidate != null) {
                return null;
            }
            if (minVacantFromCandidate == null) {
                if (segStatus == Status.offMarket | segStatus == Status.available | segStatus == Status.renovation) {
                    LogicalDate segStart = segment.dateFrom().getValue();
                    minVacantFromCandidate = segStart.before(start) ? start : segStart;
                }
            }
            switch (segStatus) {
            case offMarket:
            case renovation:
                maxVacantFromCandidate = segment.dateTo().getValue();
                break;
            case available:
                maxVacantFromCandidate = segment.dateFrom().getValue();
            default:
                break;

            }
        }

        if (minVacantFromCandidate == null) {
            return null;
        } else {
            MakeVacantConstraintsDTO constraints = EntityFactory.create(MakeVacantConstraintsDTO.class);
            constraints.minVacantFrom().setValue(minVacantFromCandidate);
            if (minVacantFromCandidate.after(maxVacantFromCandidate)) {
                constraints.maxVacantFrom().setValue(minVacantFromCandidate);
            } else {
                constraints.maxVacantFrom().setValue(maxVacantFromCandidate.before(OccupancyFacade.MAX_DATE) ? maxVacantFromCandidate : null);
            }

            return constraints;
        }
    }

    @Override
    public LogicalDate isReserveAvailable(Key unitPk) {
        LogicalDate start = new LogicalDate(SystemDateManager.getDate());
        if (AptUnitOccupancyManagerHelper.isOccupancyListEmpty(unitPk)) {
            return start; // newly created unit - can be scoped to any state!..
        }

        List<AptUnitOccupancySegment> occupancy = AptUnitOccupancyManagerHelper.retrieveOccupancy(unitPk, start);
        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.available) {
                LogicalDate segmentStart = segment.dateFrom().getValue();
                return segmentStart.before(start) ? start : segmentStart;
            }
        }

        return null;
    }

    @Override
    public boolean isUnreserveAvailable(Key unitPk) {
        LogicalDate start = new LogicalDate(SystemDateManager.getDate());
        if (AptUnitOccupancyManagerHelper.isOccupancyListEmpty(unitPk)) {
            return true; // newly created unit - can be scoped to any state!..
        }

        List<AptUnitOccupancySegment> occupancy = AptUnitOccupancyManagerHelper.retrieveOccupancy(unitPk, start);
        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.reserved) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isOccupyAvaialble(Key unitPk) {
        LogicalDate start = new LogicalDate(SystemDateManager.getDate());
        if (AptUnitOccupancyManagerHelper.isOccupancyListEmpty(unitPk)) {
            return true; // newly created unit - can be scoped to any state!..
        }

        List<AptUnitOccupancySegment> occupancy = AptUnitOccupancyManagerHelper.retrieveOccupancy(unitPk, start);
        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.reserved) {
                return true;
            }
        }

        return false;
    }

    @Override
    public CancelMoveOutConstraintsDTO getCancelMoveOutConstraints(Key unitPk) {
        LogicalDate start = new LogicalDate(SystemDateManager.getDate());
        AptUnitOccupancySegment segment = retrieveOccupancySegment(unitPk, start);
        if (segment == null) {
            throw new IllegalStateException("current occupancy segment was not found");
        } else if (segment.status().getValue() == Status.occupied & segment.dateTo().getValue().equals(OccupancyFacade.MAX_DATE)) {
            CancelMoveOutConstraintsDTO constraints = EntityFactory.create(CancelMoveOutConstraintsDTO.class);
            constraints.canCancelMoveOut().setValue(false);
            constraints.reason().setValue(ConstraintsReason.MoveOutNotExpected);
            return constraints;
        } else {
            List<AptUnitOccupancySegment> futureOccupancy = AptUnitOccupancyManagerHelper.retrieveOccupancy(unitPk, addDay(segment.dateTo().getValue()));
            CancelMoveOutConstraintsDTO constraints = EntityFactory.create(CancelMoveOutConstraintsDTO.class);
            constraints.canCancelMoveOut().setValue(true);
            for (AptUnitOccupancySegment seg : futureOccupancy) {
                switch (seg.status().getValue()) {
                case pending:
                case available:
                    break;

                case migrated:
                case occupied:
                case reserved:
                    constraints.canCancelMoveOut().setValue(false);
                    constraints.reason().setValue(ConstraintsReason.LeasedOrReserved);
                    constraints.leaseStub().set(seg.lease().createIdentityStub());
                    break;

                case offMarket:
                case renovation:
                    /** {@linkplain CancelMoveOutConstraintsDTO.ConstraintsReason#LeasedOrReserved} has higher priority */
                    if (constraints.reason().getValue() != ConstraintsReason.LeasedOrReserved) {
                        constraints.reason().setValue(ConstraintsReason.RenovatedOrOffMarket);
                        constraints.canCancelMoveOut().setValue(false);
                    }
                    break;
                default:
                    break;
                }

            }
            return constraints;
        }
    }

    @Override
    public void migrateStart(AptUnit unitStub, final Lease leaseStub) {

        LogicalDate now = new LogicalDate(SystemDateManager.getDate());

        if (!isMigrateStartAvailable(unitStub)) {
            throw new IllegalStateException(i18n.tr("Operation 'migrate start' is not permitted"));
        }
        split(unitStub, now, new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.migrated);
                segment.lease().set(leaseStub);
            }
        });
        setUnitAvailableFrom(unitStub.getPrimaryKey(), null);
    }

    @Override
    public void migratedApprove(AptUnit unitStub) {

        if (!isMigratedApproveAvailable(unitStub)) {
            throw new IllegalStateException(i18n.tr("Operation 'migrate approve' is not permitted"));
        }

        LogicalDate now = new LogicalDate(SystemDateManager.getDate());
        split(unitStub, now, new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.occupied);
            }
        });
        new AvailabilityReportManager(unitStub.getPrimaryKey()).generateUnitAvailablity(now);
    }

    @Override
    public void migratedCancel(AptUnit unitStub) {
        if (!isMigratedCancelAvailable(unitStub)) {
            throw new IllegalStateException(i18n.tr("Operation 'migrate cancel' is not permitted"));
        }

        LogicalDate now = new LogicalDate(SystemDateManager.getDate());
        split(unitStub, now, new SplittingHandler() {

            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.pending);
                segment.lease().setValue(null);
            }
        });

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(now);
        cal.add(GregorianCalendar.DAY_OF_MONTH, -1);
        merge(unitStub, new LogicalDate(cal.getTime()), Arrays.asList(Status.pending), new MergeHandler() {

            @Override
            public void onMerged(AptUnitOccupancySegment merged, AptUnitOccupancySegment s1, AptUnitOccupancySegment s2) {
            }

            @Override
            public boolean isMergeable(AptUnitOccupancySegment s1, AptUnitOccupancySegment s2) {
                return true;
            }
        });
    }

    @Override
    public boolean isMigrateStartAvailable(AptUnit unitStub) {
        LogicalDate now = new LogicalDate(SystemDateManager.getDate());
        AptUnitOccupancySegment unitOccupancySegment = retrieveOccupancySegment(unitStub, now);
        return unitOccupancySegment != null
                && (((unitOccupancySegment.status().getValue() == Status.pending) | (unitOccupancySegment.status().getValue() == Status.available)) & unitOccupancySegment
                        .dateTo().getValue().equals(OccupancyFacade.MAX_DATE));
    }

    @Override
    public boolean isMigratedApproveAvailable(AptUnit unitStub) {
        LogicalDate now = new LogicalDate(SystemDateManager.getDate());
        AptUnitOccupancySegment unitOccupancySegment = retrieveOccupancySegment(unitStub, now);
        return unitOccupancySegment != null
                && ((unitOccupancySegment.status().getValue() == Status.migrated) & unitOccupancySegment.dateTo().getValue().equals(OccupancyFacade.MAX_DATE));
    }

    @Override
    public boolean isMigratedCancelAvailable(AptUnit unitStub) {
        LogicalDate now = new LogicalDate(SystemDateManager.getDate());
        AptUnitOccupancySegment unitOccupancySegment = retrieveOccupancySegment(unitStub, now);
        return unitOccupancySegment != null
                && ((unitOccupancySegment.status().getValue() == Status.migrated) & unitOccupancySegment.dateTo().getValue().equals(OccupancyFacade.MAX_DATE));
    }

    @Override
    public boolean isAvailableForExistingLease(Key unitId) {
        LogicalDate now = new LogicalDate(SystemDateManager.getDate());
        AptUnitOccupancySegment segment = retrieveOccupancySegment(unitId, now);
        return segment != null && segment.status().getValue() == Status.pending && segment.dateTo().getValue().equals(OccupancyFacade.MAX_DATE);
    }

    private void setUnitAvailableFrom(Key unitPk, LogicalDate newAvaialbleFrom) {
        EntityQueryCriteria<AptUnitEffectiveAvailability> criteria = EntityQueryCriteria.create(AptUnitEffectiveAvailability.class);
        criteria.eq(criteria.proto().unit(), unitPk);
        AptUnitEffectiveAvailability availability = Persistence.service().retrieve(criteria);
        availability.availableForRent().setValue(newAvaialbleFrom);
        Persistence.service().merge(availability);
    }

    private boolean isInProductCatalog(Key unitPk) {
        AptUnit unit = EntityFactory.createIdentityStub(AptUnit.class, unitPk);
        EntityQueryCriteria<ProductItem> criteria = EntityQueryCriteria.create(ProductItem.class);
        criteria.eq(criteria.proto().product().holder().catalog().building().units(), unit);
        criteria.in(criteria.proto().product().holder().code().type(), ARCode.Type.unitRelatedServices());
        criteria.eq(criteria.proto().element(), unit);
        return Persistence.service().exists(criteria);
    }

    /*
     * Used in work-flow logic for newly created units in case of assigning them
     * to the existing (pre-dated) lease!
     */
    private void createAvailableSegment(Key unitPk, LogicalDate from) {
        AptUnitOccupancySegment segment = EntityFactory.create(AptUnitOccupancySegment.class);
        segment.status().setValue(Status.available);
        segment.dateFrom().setValue(from);
        segment.dateTo().setValue(OccupancyFacade.MAX_DATE);
        segment.lease().setValue(null);
        segment.unit().setPrimaryKey(unitPk);
        setUnitAvailableFrom(unitPk, segment.dateFrom().getValue());
        Persistence.service().merge(segment);
    }

    @Override
    public void reserve(final Lease lease, final int durationHours) {
        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() throws RuntimeException {
                new ReservationManager().reserve(lease, durationHours);
                return null;
            }

        });
    }

    @Override
    public boolean unreserveIfReservered(Lease lease) {
        return new ReservationManager().unreserveIfReservered(lease);
    }

    @Override
    public Pair<Date, Lease> isReserved(Key unitId) {
        return new ReservationManager().isReserved(unitId);
    }

    @Override
    public Criterion buildAvalableCriteria(AptUnit unitProto, Status status, Date from, Date fromDeadline) {
        AndCriterion criteria = new AndCriterion();

        AndCriterion existsReservation = new AndCriterion();
        existsReservation.ge(unitProto.unitReservation().$().dateTo(), SystemDateManager.getDate());
        existsReservation.le(unitProto.unitReservation().$().dateFrom(), SystemDateManager.getDate());
        criteria.notExists(unitProto.unitReservation(), existsReservation);

        criteria.eq(unitProto.unitOccupancySegments().$().status(), status);
        criteria.eq(unitProto.unitOccupancySegments().$().dateTo(), new LogicalDate(1100, 0, 1));
        criteria.le(unitProto.unitOccupancySegments().$().dateFrom(), from);
        if (fromDeadline != null) {
            criteria.gt(unitProto.unitOccupancySegments().$().dateFrom(), fromDeadline);
        }
        return criteria;
    }

    @Override
    public void setAvailability(AptUnit unit, LogicalDate availableForRent) {
        throw new Error("unsupported");
    }
}
