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

import java.util.List;
import java.util.ListIterator;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.property.asset.unit.AptUnitOccupancySegment;

public class AptUnitOccupancyManager {

    private static final long MILLIS_IN_DAY = 1000l * 60l * 60l * 24l;

    /**
     * @param occupancy
     *            assume that time is discrete, and one day is a minimum unit of time.
     *            A valid occupancy:
     * 
     *            <li>occupancy.size() > 1</li>
     * 
     *            <li>no overlapping segments</li>
     * 
     *            <li>the following holds for each i: <code>occupancy.get(i).dateFrom().getValue().before(occupancy.get(i + 1).dateFrom().getValue())</code></li>
     * 
     *            <li>all the segments cover the whole time.</li>
     * @param from
     * @param to
     * @return
     */
    public static ListIterator<AptUnitOccupancySegment> insertSegment(List<AptUnitOccupancySegment> occupancy, LogicalDate from, LogicalDate to) {

        AptUnitOccupancySegment newSegment = EntityFactory.create(AptUnitOccupancySegment.class);

        newSegment.dateFrom().setValue(from);
        newSegment.dateTo().setValue(to);

        ListIterator<AptUnitOccupancySegment> i = occupancy.listIterator();
        AptUnitOccupancySegment currentSegment = null;

        // TODO: optimization: maybe use binary search.
        while ((newSegment.dateFrom().getValue().before((currentSegment = i.next()).dateFrom().getValue()))) {
            // just iteration matters   
        }
        AptUnitOccupancySegment firstCollision = currentSegment;

        // insert the element, and handle the only two possible have two cases:
        //    1. inserting in the middle of current segment: need to split the segment
        //    2. inserting and overlapping the end of the segment:
        //       need to remove all the existing segments that the new one overlaps completely,  
        //       then if we still have segment, adjust the start date of the last segment.
        i.add(newSegment);
        final int insertedAtMemo = i.previousIndex();

        if (firstCollision.dateTo().getValue().after(to)) {
            AptUnitOccupancySegment remainderOfFirstCollision = currentSegment.duplicate();
            remainderOfFirstCollision.setPrimaryKey(null);
            remainderOfFirstCollision.dateFrom().setValue(addDay(newSegment.dateTo().getValue()));
            i.add(remainderOfFirstCollision);
        } else {
            LogicalDate newDateTo = newSegment.dateTo().getValue();
            currentSegment = null;
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
        firstCollision.dateTo().setValue(substractDay(newSegment.dateFrom().getValue()));

        return occupancy.listIterator(insertedAtMemo);
    }

    public static LogicalDate substractDay(LogicalDate date) {
        return new LogicalDate(date.getTime() - MILLIS_IN_DAY);
    }

    public static LogicalDate addDay(LogicalDate date) {
        return new LogicalDate(date.getTime() + MILLIS_IN_DAY);
    }

}
