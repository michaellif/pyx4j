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
package com.propertyvista.crm.server.util.occupancy;

import java.util.List;
import java.util.ListIterator;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;

/**
 * This class contains utility methods for unit occupancy manager.
 */
public class AptUnitOccupancyManagerHelper {

    public static final LogicalDate MIN_DATE = new LogicalDate(0, 0, 1); // 1900-1-1

    public static final LogicalDate MAX_DATE = new LogicalDate(1100, 0, 1); // 3000-0-1

    private static final long MILLIS_IN_DAY = 1000l * 60l * 60l * 24l;

    public static void merge(AptUnit unit, List<Status> relevantStatuses, SegmentPredicate pred, MergeHandler handler) {

    }

    /**
     * @param occupancyTimeline
     * @param splitDay
     * @param handler
     * @return
     * @throws IllegalStateException
     *             Might be thrown by the {@code handler} methods when the handler is requested to update the wrong occupancy state.
     */
    public static int splitSegment(List<AptUnitOccupancySegment> occupancyTimeline, LogicalDate splitDay, SplittingHandler handler)
            throws IllegalStateException {

        ListIterator<AptUnitOccupancySegment> i = occupancyTimeline.listIterator();
        AptUnitOccupancySegment segment = null;

        while (!splitDay.after((segment = i.next()).dateTo().getValue())) {
            // iterate until split candidate segment is found
        }

        int indexOfTheNewSegment = -1;
        if (splitDay.equals(segment.dateTo().getValue())) {
            indexOfTheNewSegment = i.previousIndex();
        } else {
            AptUnitOccupancySegment newSegment = EntityFactory.create(AptUnitOccupancySegment.class);
            newSegment.dateFrom().setValue(splitDay);
            newSegment.dateTo().setValue(segment.dateTo().getValue());
            handler.updateAfterSplitPointSegment(newSegment);
            if (splitDay.equals(segment.dateFrom().getValue())) {
                i.remove();
            } else {
                segment.dateTo().setValue(substractDay(splitDay));
                handler.updateBeforeSplitPointSegment(segment);
            }
            i.add(newSegment);
            indexOfTheNewSegment = i.previousIndex();
        }

        return indexOfTheNewSegment;
    }

    public static LogicalDate substractDay(LogicalDate date) {
        return new LogicalDate(date.getTime() - MILLIS_IN_DAY);
    }

    public static LogicalDate addDay(LogicalDate date) {
        return new LogicalDate(date.getTime() + MILLIS_IN_DAY);
    }

    public static interface SegmentPredicate {

        boolean check(AptUnitOccupancySegment segment);

    }

    public static interface MergeHandler {

        void onMerged(AptUnitOccupancySegment merged, AptUnitOccupancySegment s1, AptUnitOccupancySegment s2);

    }
}
