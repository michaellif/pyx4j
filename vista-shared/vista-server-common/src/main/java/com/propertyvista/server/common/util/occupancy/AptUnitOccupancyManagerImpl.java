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
package com.propertyvista.server.common.util.occupancy;

import static com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper.addDay;
import static com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper.assertStatus;
import static com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper.merge;
import static com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper.retrieveOccupancy;
import static com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper.retrieveOccupancySegment;
import static com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper.split;
import static com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper.substractDay;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.property.asset.unit.occupancy.opconstraints.MakeVacantConstraintsDTO;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper.MergeHandler;

public class AptUnitOccupancyManagerImpl implements AptUnitOccupancyManager {

    private final NowSource nowProvider;

    private final AptUnit unit;

    private final AvailabilityReportManager availabilityManager;

    public AptUnitOccupancyManagerImpl(Key unitPk) {
        this(Persistence.secureRetrieve(AptUnit.class, unitPk));
    }

    public AptUnitOccupancyManagerImpl(AptUnit unit) {
        this(unit, new NowSource() {
            @Override
            public LogicalDate getNow() {
                return new LogicalDate();
            }
        });
    }

    public AptUnitOccupancyManagerImpl(AptUnit unit, NowSource nowProvider) {
        this.nowProvider = nowProvider;
        this.unit = unit;
        if (unit == null) {
            throw new IllegalArgumentException("unit cannot be null");
        }
        this.availabilityManager = new AvailabilityReportManager(unit);
    }

    @Override
    public void scopeAvailable() {
        List<AptUnitOccupancySegment> occupancy = AptUnitOccupancyManagerHelper.retrieveOccupancy(unit, nowProvider.getNow());
        Iterator<AptUnitOccupancySegment> i = occupancy.iterator();
        LogicalDate now = nowProvider.getNow();
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

                Persistence.service().merge(segment);
                isSucceeded = true;
                break;
            }
        }
        if (isSucceeded) {
            availabilityManager.generateUnitAvailablity(now);
        } else {
            throw new IllegalStateException("" + AptUnitOccupancySegment.Status.vacant + " segment was not found 'scope available' operation is impossible!!!!");
        }
    }

    @Override
    public void scopeOffMarket(final OffMarketType type) {
        LogicalDate now = nowProvider.getNow();
        List<AptUnitOccupancySegment> occupancy = AptUnitOccupancyManagerHelper.retrieveOccupancy(unit, now);
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
                availabilityManager.generateUnitAvailablity(nowProvider.getNow());
                return;
            }
        }

        throw new IllegalStateException("a vacant segment was not found");
    }

    @Override
    public void scopeRenovation(LogicalDate renovationEndDate) {
        LogicalDate now = nowProvider.getNow();
        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unit, renovationEndDate);
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
                availabilityManager.generateUnitAvailablity(now);
                return;
            }
        }
        throw new IllegalStateException("vacant segment was not found");
    }

    @Override
    public void makeVacant(LogicalDate vacantFrom) {
        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unit, vacantFrom);
        Iterator<AptUnitOccupancySegment> i = occupancy.iterator();
        if (i.hasNext()) {
            i.next();
            while (i.hasNext()) {
                Persistence.service().delete(i.next());
            }
        } else {
            throw new IllegalStateException("cannot find a segment to convert it to 'vacant'");
        }
        split(unit, vacantFrom, new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
                assertStatus(segment, Status.offMarket);
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.vacant);
                segment.offMarket().setValue(null);
                segment.dateTo().setValue(AptUnitOccupancyManagerHelper.MAX_DATE);
            }
        });
        availabilityManager.generateUnitAvailablity(nowProvider.getNow());
    }

    @Override
    public void reserve(final Lease lease) {
        LogicalDate leaseFrom = lease.leaseFrom().getValue();
        LogicalDate now = nowProvider.getNow();

        AptUnitOccupancySegment segment = retrieveOccupancySegment(unit, leaseFrom);
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
        availabilityManager.generateUnitAvailablity(now);
    }

    @Override
    public void unreserve() {
        LogicalDate now = nowProvider.getNow();
        AptUnitOccupancySegment nowSegment = retrieveOccupancySegment(unit, now);
        if (nowSegment == null) {
            throw new IllegalStateException("unable to find current occupancy for 'unreserve' operation");
        } else if (nowSegment.status().getValue() == Status.reserved) {
            split(unit, now, new SplittingHandler() {
                @Override
                public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {

                }

                @Override
                public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                }
            });
        }
        merge(unit, now, Arrays.asList(Status.available, Status.reserved), new MergeHandler() {
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

        availabilityManager.generateUnitAvailablity(now);
    }

    @Override
    public void approveLease() {
        LogicalDate now = nowProvider.getNow();
        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unit, now);
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
                return;
            }
        }
        throw new IllegalStateException("'approveLease' operation failed: a 'reserved' segment was not found");
    }

    @Override
    public void endLease() {
        LogicalDate now = nowProvider.getNow();
        AptUnitOccupancySegment segment = retrieveOccupancySegment(unit, now);
        assertStatus(segment, Status.leased);
        split(unit, addDay(segment.lease().leaseTo().getValue()), new SplittingHandler() {

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
        availabilityManager.generateUnitAvailablity(now);
    }

    @Override
    public void cancelEndLease() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isScopeOffMarketAvailable() {
        LogicalDate now = nowProvider.getNow();

        LogicalDate start = nowProvider.getNow();
        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unit, start);

        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.vacant) {
                return true;
            }
        }

        return false;
    }

    @Override
    public LogicalDate isRenovationAvailable() {

        LogicalDate start = nowProvider.getNow();
        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unit, start);

        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.vacant) {
                LogicalDate vacantStart = segment.dateFrom().getValue();
                return vacantStart.before(start) ? start : vacantStart;
            }
        }

        return null;
    }

    @Override
    public boolean isScopeAvailableAvailable() {
        LogicalDate start = nowProvider.getNow();
        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unit, start);

        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.vacant) {
                return true;
            }
        }

        return false;
    }

    @Override
    public MakeVacantConstraintsDTO getMakeVacantConstraints() {
        LogicalDate start = nowProvider.getNow();
        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unit, start);

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
                constraints.maxVacantFrom().setValue(maxVacantFromCandidate.before(AptUnitOccupancyManagerHelper.MAX_DATE) ? maxVacantFromCandidate : null);
            }

            return constraints;
        }
    }

    @Override
    public LogicalDate isReserveAvailable() {
        LogicalDate start = nowProvider.getNow();
        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unit, start);

        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.available) {
                LogicalDate segmentStart = segment.dateFrom().getValue();
                return segmentStart.before(start) ? start : segmentStart;
            }
        }

        return null;
    }

    @Override
    public boolean isUnreserveAvailable() {
        LogicalDate start = nowProvider.getNow();
        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unit, start);

        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.reserved) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isApproveLeaseAvaialble() {
        LogicalDate start = nowProvider.getNow();
        List<AptUnitOccupancySegment> occupancy = retrieveOccupancy(unit, start);

        for (AptUnitOccupancySegment segment : occupancy) {
            if (segment.status().getValue() == Status.reserved) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isEndLeaseAvailable() {
        LogicalDate start = nowProvider.getNow();
        AptUnitOccupancySegment segment = retrieveOccupancySegment(unit, start);
        if (segment != null && segment.status().getValue() == Status.leased) {
            return true;
        } else {
            return false;
        }
    }

    public interface NowSource {

        LogicalDate getNow();

    }
}
