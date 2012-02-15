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
package com.propertyvista.crm.server.util.occupancy;

import static com.propertyvista.crm.server.util.occupancy.AptUnitOccupancyManagerHelper.addDay;
import static com.propertyvista.crm.server.util.occupancy.AptUnitOccupancyManagerHelper.assertStatus;
import static com.propertyvista.crm.server.util.occupancy.AptUnitOccupancyManagerHelper.retrieveOccupancy;
import static com.propertyvista.crm.server.util.occupancy.AptUnitOccupancyManagerHelper.retrieveOccupancySegment;
import static com.propertyvista.crm.server.util.occupancy.AptUnitOccupancyManagerHelper.split;
import static com.propertyvista.crm.server.util.occupancy.AptUnitOccupancyManagerHelper.substractDay;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;

public class AptUnitOccupancyManagerImpl implements AptUnitOccupancyManager {

    private final NowSource nowProvider;

    private final AptUnit unit;

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
    }

    @Override
    public void scopeAvailable() {
        List<AptUnitOccupancySegment> occupancy = AptUnitOccupancyManagerHelper.retrieveOccupancy(unit, nowProvider.getNow());
        Iterator<AptUnitOccupancySegment> i = occupancy.iterator();
        while (i.hasNext()) {
            AptUnitOccupancySegment segment = i.next();
            if (segment.status().getValue() == AptUnitOccupancySegment.Status.vacant) {
                LogicalDate now = nowProvider.getNow();

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
                return;
            }
        }
        throw new IllegalStateException("" + AptUnitOccupancySegment.Status.vacant + " segment was not found 'scope available' operation is impossible!!!!");
    }

    @Override
    public void scopeOffMarket(final OffMarketType type, LogicalDate startDate) {
        AptUnitOccupancyManagerHelper.split(unit, startDate, new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
                if (segment.status().getValue() != AptUnitOccupancySegment.Status.vacant) {
                    throw new IllegalStateException("It's impossible to apply scopeOffMarket operation to " + segment.status().getValue());
                }
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.offMarket);
                segment.offMarket().setValue(type);
                segment.lease().setValue(null);
            }
        });
    }

    @Override
    public void scopeRenovation(LogicalDate renovationEndDate) {
        // there are two possible scenarios:
        // 1. scope happens during lease - during 'leased' segment:
        //   - we have to fetch the next segment that MUST be vacant        
        // 2. scope happens after lease
        //   - then it happens during 'vacant' segment
        LogicalDate now = nowProvider.getNow();
        AptUnitOccupancySegment vacantSegment = null;
        AptUnitOccupancySegment segment = AptUnitOccupancyManagerHelper.retrieveOccupancySegment(unit, now);

        if (segment == null) {

            throw new IllegalStateException("failed to find an segment with status for that contains the following date: "
                    + SimpleDateFormat.getInstance().format(now));

        } else if (segment.status().getValue() == Status.vacant) {

            vacantSegment = segment;

        } else if (segment.status().getValue() == Status.leased) {

            vacantSegment = AptUnitOccupancyManagerHelper.retrieveOccupancySegment(unit, AptUnitOccupancyManagerHelper.addDay(segment.dateTo().getValue()));

        }

        if (vacantSegment == null) {
            throw new IllegalStateException("failed to find a 'vacant' segment for 'scopeRenovation'");
        } else if (vacantSegment.status().getValue() != Status.vacant) {
            throw new IllegalStateException("It's impossible to apply 'scopeRenovation' operation during " + segment.status().getValue());
        }

        if (now.after(vacantSegment.dateFrom().getValue())) {
            AptUnitOccupancySegment newSegment = vacantSegment.duplicate();
            newSegment.id().set(null);
            newSegment.dateFrom().setValue(now);

        }

        AptUnitOccupancySegment renovationSegment = split(vacantSegment, now, new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
                // should be ok: we checked it before
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.renovation);
            }
        });
        if (renovationSegment == null) {
            throw new IllegalStateException("failed to create 'renovation' segment");
        }
        AptUnitOccupancySegment availableSegment = AptUnitOccupancyManagerHelper.split(renovationSegment, renovationEndDate, new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {

            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.available);
            }
        });
        if (availableSegment == null) {
            throw new IllegalStateException("failed to create 'available' segment");
        }
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
    }

    @Override
    public void reserve(final Lease lease) {
        LogicalDate leaseFrom = lease.leaseFrom().getValue();
        LogicalDate now = nowProvider.getNow();
        AptUnitOccupancySegment segment = retrieveOccupancySegment(unit, leaseFrom);
        assertStatus(segment, Status.available);

        LogicalDate splitDay = now.before(leaseFrom) ? segment.dateFrom().getValue() : leaseFrom;
        split(segment, splitDay, new SplittingHandler() {
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
    }

    @Override
    public void unreserve() {
        // for this one we have to implement Merge operation
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
    }

    public interface NowSource {

        LogicalDate getNow();

    }

}
