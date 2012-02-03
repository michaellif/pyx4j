/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 2, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.util;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancy;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;

public class AptUnitOccupancyManager {

    public static final LogicalDate MIN_DATE = new LogicalDate(0, 0, 1); // 1900-1-1

    public static final LogicalDate MAX_DATE = new LogicalDate(1100, 0, 1); // 3000-0-1

    private static final long MILLIS_IN_DAY = 1000l * 60l * 60l * 24l;

    /**
     * @param occupancyTimeline
     * @param from
     * @param to
     * @param initializer
     *            not <code>null</code>
     * @return index stuch that occupancy.timeline().get(0) == new segment;
     */
    public static int insertSegment(AptUnitOccupancy occupancy, LogicalDate from, LogicalDate to, SegmentInitializer initializer) {

        AptUnitOccupancySegment newSegment = EntityFactory.create(AptUnitOccupancySegment.class);
        newSegment.dateFrom().setValue(from);
        newSegment.dateTo().setValue(to);

        // TODO IList does not return list iterator

        List<AptUnitOccupancySegment> occupancyTimeline = new LinkedList<AptUnitOccupancySegment>(occupancy.timeline());
        ListIterator<AptUnitOccupancySegment> i = occupancyTimeline.listIterator();
        AptUnitOccupancySegment currentSegment = null;

        // TODO: optimization: maybe use binary search.
        while ((newSegment.dateFrom().getValue().before((currentSegment = i.next()).dateFrom().getValue()))) {
            // just iteration matters   
        }
        AptUnitOccupancySegment firstCollision = currentSegment;
        AptUnitOccupancySegment remainderOfFirstCollision = null;
        // insert the element, and handle the only two possible have two cases:
        //    1. inserting in the middle of current segment: need to split the segment
        //    2. inserting and overlapping the end of the segment:
        //       need to remove all the existing segments that the new one overlaps completely,  
        //       then if we still have segment, adjust the start date of the last segment.
        i.add(newSegment);
        final int insertedAtMemo = i.previousIndex();

        if (firstCollision.dateTo().getValue().after(to)) {
            remainderOfFirstCollision = currentSegment.duplicate();
            remainderOfFirstCollision.setPrimaryKey(null);
            remainderOfFirstCollision.dateFrom().setValue(addDay(newSegment.dateTo().getValue()));
            i.add(remainderOfFirstCollision);
        } else {
            LogicalDate newDateTo = newSegment.dateTo().getValue();
            currentSegment = null;
            if (i.hasNext()) {
                while (i.hasNext() && newDateTo.after((currentSegment = i.next()).dateTo().getValue())) {
                    i.remove();
                }
                if (currentSegment != null && (newDateTo.before(currentSegment.dateTo().getValue()))) {
                    currentSegment.dateFrom().setValue(addDay(newSegment.dateTo().getValue()));
                } else {
                    // if current is not before and is not after, then they are equal and we must perform:
                    i.remove();
                }
            }
        }

        // adjust statuses and dates
        firstCollision.dateTo().setValue(substractDay(newSegment.dateFrom().getValue()));
        initializer.initAddedStatus(newSegment);
        if (remainderOfFirstCollision != null) {
            initializer.initAddedStatus(remainderOfFirstCollision);
        }

        occupancy.timeline().clear();
        occupancy.timeline().addAll(occupancyTimeline);
        return insertedAtMemo;
    }

    public static LogicalDate substractDay(LogicalDate date) {
        return new LogicalDate(date.getTime() - MILLIS_IN_DAY);
    }

    public static LogicalDate addDay(LogicalDate date) {
        return new LogicalDate(date.getTime() + MILLIS_IN_DAY);
    }

    public static interface SegmentInitializer {

        void initAddedStatus(AptUnitOccupancySegment addedSegment);

        void initRemainderOfTheSplitStatus(AptUnitOccupancySegment splitStatus);
    }
}
