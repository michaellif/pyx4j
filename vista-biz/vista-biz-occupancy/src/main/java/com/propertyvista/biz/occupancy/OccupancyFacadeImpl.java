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
import static com.propertyvista.biz.occupancy.AptUnitOccupancyManagerHelper.retrieveOccupancy;
import static com.propertyvista.biz.occupancy.AptUnitOccupancyManagerHelper.retrieveOccupancySegment;
import static com.propertyvista.biz.occupancy.AptUnitOccupancyManagerHelper.split;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.occupancy.AptUnitOccupancyManagerHelper.MergeHandler;
import com.propertyvista.domain.financial.offering.ProductItem;
import com.propertyvista.domain.financial.offering.Service;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.property.asset.unit.occupancy.opconstraints.MakeVacantConstraintsDTO;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.misc.VistaTODO;

public class OccupancyFacadeImpl implements OccupancyFacade {

    private static final I18n i18n = I18n.get(OccupancyFacadeImpl.class);

    private static final Vector<Service.ServiceType> SERVICES_PROVIDED_BY_UNIT = new Vector<Service.ServiceType>(Arrays.asList(
            Service.ServiceType.residentialUnit, Service.ServiceType.commercialUnit));

    @Override
    public void setupNewUnit(AptUnit unit) {
        updateUnitAvailableFrom(unit.getPrimaryKey(), null);

        // for new unit, create a vacant occupancy segment: 
        AptUnitOccupancySegment vacant = EntityFactory.create(AptUnitOccupancySegment.class);
        vacant.unit().set(unit);
        vacant.status().setValue(Status.pending);
        vacant.dateFrom().setValue(new LogicalDate());
        vacant.dateTo().setValue(OccupancyFacade.MAX_DATE);
        Persistence.service().persist(vacant);
    }

    @Override
    public void scopeAvailable(Key unitPk) {
        LogicalDate now = new LogicalDate(Persistence.service().getTransactionSystemTime());

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

        // Find the first pending segment and convert it
        AptUnitOccupancySegment firstPendingSegment = null;
        LogicalDate splittingDay = null;
        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.pending) {
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
            // TODO (i know that this is very stupid stupid and has to refactored)
            EntityQueryCriteria<AptUnitOccupancySegment> criteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
            criteria.add(PropertyCriterion.ne(criteria.proto().id(), prevSegment.getPrimaryKey()));
            criteria.add(PropertyCriterion.eq(criteria.proto().status(), Status.available));
            criteria.add(PropertyCriterion.ge(criteria.proto().dateTo(), now));
            Persistence.service().delete(criteria);

            prevSegment.dateTo().setValue(OccupancyFacade.MAX_DATE);
            Persistence.service().persist(prevSegment);
        }
        updateUnitAvailableFrom(unitPk, splittingDay);

        new AvailabilityReportManager(unitPk).generateUnitAvailablity(now);
    }

    @Override
    public void scopeOffMarket(Key unitPk, final OffMarketType type) {
        LogicalDate now = new LogicalDate(Persistence.service().getTransactionSystemTime());
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
        LogicalDate now = new LogicalDate(Persistence.service().getTransactionSystemTime());
        if (AptUnitOccupancyManagerHelper.isOccupancyListEmpty(unitPk)) {
            AptUnitOccupancySegment segment = EntityFactory.create(AptUnitOccupancySegment.class);
            segment.status().setValue(Status.pending);
            segment.dateFrom().setValue(now);
            segment.dateTo().setValue(OccupancyFacade.MAX_DATE);
            segment.lease().setValue(null);
            segment.unit().setPrimaryKey(unitPk);
            Persistence.service().merge(segment);
        }

        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unitPk, renovationEndDate);
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
                updateUnitAvailableFrom(unitPk, addDay(renovationEndDate));
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
        LogicalDate now = new LogicalDate(Persistence.service().getTransactionSystemTime());
        MakeVacantConstraintsDTO constraints = getMakeVacantConstraints(unitPk);
        LogicalDate min = constraints.minVacantFrom().getValue();
        LogicalDate max = constraints.maxVacantFrom().getValue();
        if (//@formatter:off
             constraints == null
                 || !((vacantFrom.after(min) | vacantFrom.equals(min))                             
                         & (max == null || (vacantFrom.before(max) | vacantFrom.equals(max))))) { //@formatter:on
            throw new IllegalArgumentException(SimpleMessageFormat.format("vacantFrom {0} doesn't match the constraints", vacantFrom));
        }

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(vacantFrom);
        cal.add(GregorianCalendar.DAY_OF_YEAR, -1);
        AptUnitOccupancySegment preMakeVacantDateSegment = retrieveOccupancySegment(unitPk, new LogicalDate(cal.getTime()));

        if (preMakeVacantDateSegment.status().getValue() == Status.pending) {

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

        updateUnitAvailableFrom(unitPk, null);
        new AvailabilityReportManager(unitPk).generateUnitAvailablity(now);
    }

    @Override
    public void reserve(Key unitPk, final Lease lease) {
        LogicalDate leaseFrom = lease.leaseFrom().getValue();
        LogicalDate now = new LogicalDate(Persistence.service().getTransactionSystemTime());

        if (VistaTODO.checkLeaseDatesOnUnitReservation & leaseFrom.before(now)) {
            throw new IllegalStateException(i18n.tr("Operation 'reserve unit' is not permitted, the lease from date ({0}) is before the present date ({1})",
                    leaseFrom, now));
        }

        if (AptUnitOccupancyManagerHelper.isOccupancyListEmpty(unitPk)) {
            createAvailableSegment(unitPk, leaseFrom);
        }

        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unitPk, leaseFrom);
        if (occupancy.size() != 1) {
            throw new UserRuntimeException(i18n.tr("Operation 'reserve unit' is not permitted, the unit is not available after {0}", leaseFrom));
        }
        AptUnitOccupancySegment segment = occupancy.get(0);
        assertStatus(occupancy.get(0), Status.available);

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
        updateUnitAvailableFrom(unitPk, null);
        new AvailabilityReportManager(unitPk).generateUnitAvailablity(now);
    }

    @Override
    public void unreserve(Key unitPk) {
        LogicalDate now = new LogicalDate(Persistence.service().getTransactionSystemTime());
        AptUnitOccupancySegment nowSegment = retrieveOccupancySegment(unitPk, now);
        if (nowSegment == null) {
            throw new IllegalStateException("unable to find current occupancy for 'unreserve' operation");
        } else if (nowSegment.status().getValue() == Status.reserved) {
            split(unitPk, now, new SplittingHandler() {
                @Override
                public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {

                }

                @Override
                public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                }
            });
        }
        merge(unitPk, now, Arrays.asList(Status.available, Status.reserved), new MergeHandler() {
            @Override
            public void onMerged(AptUnitOccupancySegment merged, AptUnitOccupancySegment s1, AptUnitOccupancySegment s2) {
                merged.status().setValue(Status.available);
                merged.lease().set(null);
            }

            @Override
            public boolean isMergeable(AptUnitOccupancySegment s1, AptUnitOccupancySegment s2) {
                return true;
            }
        });
        new AvailabilityReportManager(unitPk).generateUnitAvailablity(now);
    }

    @Override
    public void approveLease(Key unitPk) {
        LogicalDate now = new LogicalDate(Persistence.service().getTransactionSystemTime());
        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unitPk, now);
        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.reserved) {
                split(segment, segment.lease().leaseFrom().getValue(), new SplittingHandler() {
                    @Override
                    public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
                        // already checked that we are in 'reserved'
                    }

                    @Override
                    public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                        segment.status().setValue(Status.leased);
                    }
                });
                new AvailabilityReportManager(unitPk).generateUnitAvailablity(now);
                return;
            }
        }
        throw new IllegalStateException("'approveLease' operation failed: a 'reserved' segment was not found");
    }

    @Override
    public void endLease(Key unitPk) {
        LogicalDate now = new LogicalDate(Persistence.service().getTransactionSystemTime());
        AptUnitOccupancySegment segment = retrieveOccupancySegment(unitPk, now);
        assertStatus(segment, Status.leased);
        split(unitPk, addDay(segment.lease().leaseTo().getValue()), new SplittingHandler() {

            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
                // already checked we splitting the correct status
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.pending);
                segment.lease().setValue(null);
            }
        });
        new AvailabilityReportManager(unitPk).generateUnitAvailablity(now);
    }

    @Override
    public void cancelEndLease(Key unitPk) {
        if (!isCancelEndLeaseAvaialble(unitPk)) {
            throw new IllegalStateException("cancel end lease operation is impossible in the current state of occupancy");
        }

        LogicalDate now = new LogicalDate(Persistence.service().getTransactionSystemTime());
        AptUnitOccupancySegment leasedSegment = retrieveOccupancySegment(unitPk, now);
        EntityQueryCriteria<AptUnitOccupancySegment> delete = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        delete.add(PropertyCriterion.ge(delete.proto().dateFrom(), addDay(leasedSegment.dateTo().getValue())));
        Persistence.service().delete(delete);
        leasedSegment.dateTo().setValue(OccupancyFacade.MAX_DATE);
        Persistence.secureSave(leasedSegment);
        updateUnitAvailableFrom(unitPk, null);
        new AvailabilityReportManager(unitPk).generateUnitAvailablity(now);
    }

    @Override
    public boolean isScopeOffMarketAvailable(Key unitPk) {
        LogicalDate start = new LogicalDate(Persistence.service().getTransactionSystemTime());
        if (AptUnitOccupancyManagerHelper.isOccupancyListEmpty(unitPk)) {
            return true; // newly created unit - can be scoped to any state!.. 
        }

        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unitPk, start);
        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.pending) {
                return true;
            }
        }

        return false;
    }

    @Override
    public LogicalDate isRenovationAvailable(Key unitPk) {
        LogicalDate start = new LogicalDate(Persistence.service().getTransactionSystemTime());
        if (AptUnitOccupancyManagerHelper.isOccupancyListEmpty(unitPk)) {
            return start; // newly created unit - can be scoped to any state!.. 
        }

        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unitPk, start);
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
        LogicalDate start = new LogicalDate(Persistence.service().getTransactionSystemTime());
        if (AptUnitOccupancyManagerHelper.isOccupancyListEmpty(unitPk)) {
            return true; // newly created unit - can be scoped to any state!.. 
        }

        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unitPk, start);
        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.pending) {
                return true;
            }
        }

        return false;
    }

    @Override
    public MakeVacantConstraintsDTO getMakeVacantConstraints(Key unitPk) {
        LogicalDate start = new LogicalDate(Persistence.service().getTransactionSystemTime());
        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unitPk, start);

        LogicalDate minVacantFromCandidate = null;
        LogicalDate maxVacantFromCandidate = null;
        for (AptUnitOccupancySegment segment : occupancy) {
            Status segStatus = segment.status().getValue();
            if ((segStatus == Status.reserved | segStatus == Status.leased) & minVacantFromCandidate != null) {
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
        LogicalDate start = new LogicalDate(Persistence.service().getTransactionSystemTime());
        if (AptUnitOccupancyManagerHelper.isOccupancyListEmpty(unitPk)) {
            return start; // newly created unit - can be scoped to any state!.. 
        }

        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unitPk, start);
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
        LogicalDate start = new LogicalDate(Persistence.service().getTransactionSystemTime());
        if (AptUnitOccupancyManagerHelper.isOccupancyListEmpty(unitPk)) {
            return true; // newly created unit - can be scoped to any state!.. 
        }

        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unitPk, start);
        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.reserved) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isApproveLeaseAvaialble(Key unitPk) {
        LogicalDate start = new LogicalDate(Persistence.service().getTransactionSystemTime());
        if (AptUnitOccupancyManagerHelper.isOccupancyListEmpty(unitPk)) {
            return true; // newly created unit - can be scoped to any state!.. 
        }

        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unitPk, start);
        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.reserved) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isEndLeaseAvailable(Key unitPk) {
        LogicalDate start = new LogicalDate(Persistence.service().getTransactionSystemTime());
        AptUnitOccupancySegment segment = retrieveOccupancySegment(unitPk, start);
        return segment != null && segment.status().getValue() == Status.leased && segment.dateTo().getValue().equals(OccupancyFacade.MAX_DATE);

    }

    @Override
    public boolean isCancelEndLeaseAvaialble(Key unitPk) {
        LogicalDate start = new LogicalDate(Persistence.service().getTransactionSystemTime());
        AptUnitOccupancySegment segment = retrieveOccupancySegment(unitPk, start);
        if (segment != null && segment.status().getValue() == Status.leased && !segment.dateTo().getValue().equals(OccupancyFacade.MAX_DATE)) {
            List<AptUnitOccupancySegment> rest = retrieveOccupancy(unitPk, addDay(segment.dateTo().getValue()));
            for (AptUnitOccupancySegment seg : rest) {
                if (seg.status().getValue() == Status.leased | seg.status().getValue() == Status.reserved) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void migrateStart(AptUnit unitStub, final Lease leaseStub) {

        LogicalDate now = new LogicalDate(Persistence.service().getTransactionSystemTime());

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
        updateUnitAvailableFrom(unitStub.getPrimaryKey(), null);
    }

    @Override
    public void migratedApprove(AptUnit unitStub) {

        if (!isMigratedApproveAvailable(unitStub)) {
            throw new IllegalStateException(i18n.tr("Operation 'migrate approve' is not permitted"));
        }

        LogicalDate now = new LogicalDate(Persistence.service().getTransactionSystemTime());
        split(unitStub, now, new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.leased);
            }
        });
        new AvailabilityReportManager(unitStub.getPrimaryKey()).generateUnitAvailablity(now);
    }

    @Override
    public void migratedCancel(AptUnit unitStub) {
        if (!isMigratedCancelAvailable(unitStub)) {
            throw new IllegalStateException(i18n.tr("Operation 'migrate cancel' is not permitted"));
        }

        LogicalDate now = new LogicalDate(Persistence.service().getTransactionSystemTime());
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
        LogicalDate now = new LogicalDate(Persistence.service().getTransactionSystemTime());
        AptUnitOccupancySegment unitOccupancySegment = retrieveOccupancySegment(unitStub, now);
        return unitOccupancySegment != null
                && (((unitOccupancySegment.status().getValue() == Status.pending) | (unitOccupancySegment.status().getValue() == Status.available)) & unitOccupancySegment
                        .dateTo().getValue().equals(OccupancyFacade.MAX_DATE));
    }

    @Override
    public boolean isMigratedApproveAvailable(AptUnit unitStub) {
        LogicalDate now = new LogicalDate(Persistence.service().getTransactionSystemTime());
        AptUnitOccupancySegment unitOccupancySegment = retrieveOccupancySegment(unitStub, now);
        return unitOccupancySegment != null
                && ((unitOccupancySegment.status().getValue() == Status.migrated) & unitOccupancySegment.dateTo().getValue().equals(OccupancyFacade.MAX_DATE));
    }

    @Override
    public boolean isMigratedCancelAvailable(AptUnit unitStub) {
        LogicalDate now = new LogicalDate(Persistence.service().getTransactionSystemTime());
        AptUnitOccupancySegment unitOccupancySegment = retrieveOccupancySegment(unitStub, now);
        return unitOccupancySegment != null
                && ((unitOccupancySegment.status().getValue() == Status.migrated) & unitOccupancySegment.dateTo().getValue().equals(OccupancyFacade.MAX_DATE));
    }

    private void updateUnitAvailableFrom(Key unitPk, LogicalDate newAvaialbleFrom) {
        AptUnit unit = Persistence.service().retrieve(AptUnit.class, unitPk);
        unit._availableForRent().setValue(newAvaialbleFrom);
        unit.financial()._unitRent().setValue(null);
        Persistence.secureSave(unit);
    }

    private boolean isInProductCatalog(Key unitPk) {

        AptUnit unit = Persistence.service().retrieve(AptUnit.class, unitPk);

        EntityQueryCriteria<Service> criteria = EntityQueryCriteria.create(Service.class);

        criteria.add(PropertyCriterion.eq(criteria.proto().catalog().building(), unit.building()));
        criteria.add(PropertyCriterion.in(criteria.proto().version().type(), SERVICES_PROVIDED_BY_UNIT));

        List<Service> services = Persistence.secureQuery(criteria);
        for (Service service : services) {
            Persistence.service().retrieve(service.version().items());
            for (ProductItem item : service.version().items()) {
                if (item.element().getInstanceValueClass().equals(AptUnit.class) & item.element().getPrimaryKey().equals(unitPk)) {
                    return true;
                }
            }
        }

        return false;
    }

    /*
     * Used in work-flow logic for newly created units in case of assigning them to the existing (pre-dated) lease!
     */
    private void createAvailableSegment(Key unitPk, LogicalDate from) {
        AptUnitOccupancySegment segment = EntityFactory.create(AptUnitOccupancySegment.class);
        segment.status().setValue(Status.available);
        segment.dateFrom().setValue(from);
        segment.dateTo().setValue(OccupancyFacade.MAX_DATE);
        segment.lease().setValue(null);
        segment.unit().setPrimaryKey(unitPk);
        updateUnitAvailableFrom(unitPk, segment.dateFrom().getValue());
        Persistence.service().merge(segment);
    }

}
