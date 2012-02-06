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

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityGraph;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancy;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;

public class AptUnitOccupancyManager {

    public static void lease(AptUnit unit, final Lease lease, final LogicalDate leaseStart) throws IllegalStateException {
        updateOccupancy(unit, new UpdateOccupancyCallback() {
            @Override
            public void onUpdate(AptUnitOccupancy occupancy) throws IllegalStateException {
                lease(occupancy, lease, leaseStart);
            }
        });
    }

    /**
     * 
     * @param occupancy
     * @param lease
     * @param leaseStart
     * @throws IllegalStateException
     *             when leaseStart is not contained by "available" segment.
     */
    public static void lease(AptUnitOccupancy occupancy, final Lease lease, LogicalDate leaseStart) throws IllegalStateException {
        AptUnitOccupancyManagerHelper.splitSegment(occupancy, leaseStart, new SplittingHandler() {

            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) {
                if (segment.status().getValue().equals(AptUnitOccupancySegment.Status.available)) {
                    segment.status().setValue(AptUnitOccupancySegment.Status.reserved);
                } else {
                    throw new IllegalStateException("trying to add 'leased' occupancy segment after " + segment.status().getValue());
                }
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.leased);
                segment.lease().set(lease);
            }
        });
    }

    public static void notice(AptUnit unit, final LogicalDate moveOutDate) throws IllegalStateException {
        updateOccupancy(unit, new UpdateOccupancyCallback() {
            @Override
            public void onUpdate(AptUnitOccupancy occupancy) throws IllegalStateException {
                notice(occupancy, moveOutDate);
            }
        });
    }

    /**
     * @param occupancy
     * @param moveOutDate
     *            expected to be contained by "leased" segment.
     * @throws IllegalStateException
     *             when moveOutDate is not contained by 'leased' segment
     */
    public static void notice(AptUnitOccupancy occupancy, LogicalDate moveOutDate) throws IllegalStateException {

        AptUnitOccupancyManagerHelper.splitSegment(occupancy, AptUnitOccupancyManagerHelper.addDay(moveOutDate), new SplittingHandler() {
            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) {
                if (!segment.status().getValue().equals(Status.leased)) {
                    throw new IllegalStateException(SimpleMessageFormat
                            .format("requested to set move out date during {0} segment", segment.status().getValue()));
                }
            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.vacant);
            }
        });

    }

    public static void scopeOffMarket(AptUnit unit, final LogicalDate renovationEnd) throws IllegalStateException {
        updateOccupancy(unit, new UpdateOccupancyCallback() {
            @Override
            public void onUpdate(AptUnitOccupancy occupancy) throws IllegalStateException {
                scopeOffMarket(occupancy, renovationEnd);
            }
        });
    }

    /**
     * @param occupancy
     * @param renovationEnd
     *            a day before the day when the unit becomes available for rent.
     */
    public static void scopeOffMarket(AptUnitOccupancy occupancy, LogicalDate renovationEnd) {

        AptUnitOccupancyManagerHelper.splitSegment(occupancy, AptUnitOccupancyManagerHelper.addDay(renovationEnd), new SplittingHandler() {

            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) {
                if (segment.status().getValue().equals(Status.vacant)) {
                    segment.status().setValue(Status.offMarket);
                    segment.offMarket().setValue(OffMarketType.construction);
                } else {
                    throw new IllegalStateException(SimpleMessageFormat.format("can't allow to add a 'renovation' segment in the middle of {0}", segment
                            .status().getValue()));
                }

            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.available);
            }
        });
    }

    public static void scopeAvailable(AptUnit unit, final LogicalDate availableFrom) throws IllegalStateException {
        updateOccupancy(unit, new UpdateOccupancyCallback() {
            @Override
            public void onUpdate(AptUnitOccupancy occupancy) throws IllegalStateException {
                scopeAvailable(occupancy, availableFrom);
            }
        });
    }

    public static void scopeAvailable(AptUnitOccupancy occupancy, LogicalDate availableFrom) throws IllegalStateException {

        AptUnitOccupancyManagerHelper.splitSegment(occupancy, availableFrom, new SplittingHandler() {

            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) {
                if (!segment.status().getValue().equals(Status.vacant)) {
                    throw new IllegalStateException(SimpleMessageFormat.format("can't change {0} segment to 'available", segment.status().getValue()));
                }

            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                segment.status().setValue(Status.available);
            }
        });

    }

    private static void updateOccupancy(AptUnit unit, UpdateOccupancyCallback callback) throws IllegalStateException {
        AptUnitOccupancy occupancy = theMostRecentOccupancyof(unit);
        occupancy = EntityGraph.businessDuplicate(occupancy);
        callback.onUpdate(occupancy);
        occupancy.updatedOn().setValue(new LogicalDate());
        Persistence.service().merge(occupancy);
    }

    private static AptUnitOccupancy theMostRecentOccupancyof(AptUnit unit) {
        EntityQueryCriteria<AptUnitOccupancy> criteria = new EntityQueryCriteria<AptUnitOccupancy>(AptUnitOccupancy.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unit));
        criteria.desc(criteria.proto().updatedOn());
        AptUnitOccupancy occupancy = Persistence.service().retrieve(criteria);
        return occupancy;
    }

    private interface UpdateOccupancyCallback {

        void onUpdate(AptUnitOccupancy occupancy) throws IllegalStateException;

    }
}
