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
import static com.propertyvista.biz.occupancy.AptUnitOccupancyManagerHelper.substractDay;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.occupancy.AptUnitOccupancyManagerHelper.MergeHandler;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.property.asset.unit.occupancy.opconstraints.MakeVacantConstraintsDTO;
import com.propertyvista.domain.tenant.lease.Lease;

public class OccupancyFacadeImpl implements OccupancyFacade {

    @Override
    public void scopeAvailable(Key unitPk) {
        LogicalDate now = new LogicalDate(Persistence.service().getTransactionSystemTime());
        List<AptUnitOccupancySegment> occupancy = AptUnitOccupancyManagerHelper.retrieveOccupancy(unitPk, now);
        Iterator<AptUnitOccupancySegment> i = occupancy.iterator();
        boolean isSucceeded = false;

        while (i.hasNext()) {
            AptUnitOccupancySegment segment = i.next();
            if (segment.status().getValue() == AptUnitOccupancySegment.Status.vacant) {

                // if we got segment that starts in the past then split it
                if (segment.dateFrom().getValue().before(now)) {
                    AptUnitOccupancySegment newSegment = segment.duplicate();
                    newSegment.id().set(null);
                    newSegment.dateFrom().setValue(now);
                    segment.dateTo().setValue(substractDay(now));
                    Persistence.service().merge(segment);
                    segment = newSegment;
                }

                segment.status().setValue(AptUnitOccupancySegment.Status.available);
                segment.lease().setValue(null);
                updateUnitAvailableFrom(unitPk, segment.dateFrom().getValue());

                Persistence.service().merge(segment);
                isSucceeded = true;
                break;
            }
        }
        if (isSucceeded) {
            new AvailabilityReportManager(unitPk).generateUnitAvailablity(now);
        } else {
            throw new IllegalStateException("" + AptUnitOccupancySegment.Status.vacant + " segment was not found 'scope available' operation is impossible!!!!");
        }
    }

    @Override
    public void scopeOffMarket(Key unitPk, final OffMarketType type) {
        LogicalDate now = new LogicalDate(Persistence.service().getTransactionSystemTime());
        List<AptUnitOccupancySegment> occupancy = AptUnitOccupancyManagerHelper.retrieveOccupancy(unitPk, now);
        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.vacant) {
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
        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unitPk, renovationEndDate);
        for (AptUnitOccupancySegment seg : occupancy) {
            if (seg.status().getValue() == Status.vacant) {
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

        AptUnitOccupancySegment makeVacantStartSegment = retrieveOccupancySegment(unitPk, vacantFrom);
        AptUnitOccupancySegment vacantSegment = split(makeVacantStartSegment, vacantFrom, new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {

            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.vacant);
                segment.offMarket().setValue(null);
                segment.dateTo().setValue(OccupancyFacade.MAX_DATE);
            }
        });

        // now remove the rest
        EntityQueryCriteria<AptUnitOccupancySegment> deleteCriteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        deleteCriteria.add(PropertyCriterion.eq(deleteCriteria.proto().unit(), unitPk));
        deleteCriteria.add(PropertyCriterion.ge(deleteCriteria.proto().dateTo(), vacantFrom));
        deleteCriteria.add(PropertyCriterion.ne(deleteCriteria.proto().id(), vacantSegment.id().getValue()));
        Persistence.service().delete(deleteCriteria);

        updateUnitAvailableFrom(unitPk, null);
        new AvailabilityReportManager(unitPk).generateUnitAvailablity(now);
    }

    @Override
    public void reserve(Key unitPk, final Lease lease) {
        LogicalDate leaseFrom = lease.leaseFrom().getValue();
        LogicalDate now = new LogicalDate(Persistence.service().getTransactionSystemTime());

        AptUnitOccupancySegment segment = retrieveOccupancySegment(unitPk, leaseFrom);
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
                segment.status().setValue(Status.vacant);
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

        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unitPk, start);

        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.vacant) {
                return true;
            }
        }

        return false;
    }

    @Override
    public LogicalDate isRenovationAvailable(Key unitPk) {
        LogicalDate start = new LogicalDate(Persistence.service().getTransactionSystemTime());
        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unitPk, start);

        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.vacant) {
                LogicalDate vacantStart = segment.dateFrom().getValue();
                return vacantStart.before(start) ? start : vacantStart;
            }
        }

        return null;
    }

    @Override
    public boolean isScopeAvailableAvailable(Key unitPk) {
        LogicalDate start = new LogicalDate(Persistence.service().getTransactionSystemTime());
        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unitPk, start);

        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.vacant) {
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
                if (segStatus == Status.offMarket | segStatus == Status.available) {
                    LogicalDate segStart = segment.dateFrom().getValue();
                    minVacantFromCandidate = segStart.before(start) ? start : segStart;
                }
            }
            switch (segStatus) {
            case offMarket:
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

    private void updateUnitAvailableFrom(Key unitPk, LogicalDate newAvaialbleFrom) {
        AptUnit unit = Persistence.service().retrieve(AptUnit.class, unitPk);
        unit._availableForRent().setValue(newAvaialbleFrom);
        unit.financial()._unitRent().setValue(null);
        Persistence.secureSave(unit);
    }

}
