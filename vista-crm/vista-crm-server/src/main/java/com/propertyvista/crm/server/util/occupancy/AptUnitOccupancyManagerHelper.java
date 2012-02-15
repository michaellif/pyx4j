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
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;

/**
 * This class contains utility methods for unit occupancy manager.
 */
public class AptUnitOccupancyManagerHelper {

    public static final LogicalDate MIN_DATE = new LogicalDate(0, 0, 1); // 1900-1-1

    public static final LogicalDate MAX_DATE = new LogicalDate(1100, 0, 1); // 3000-1-1

    private static final long MILLIS_IN_DAY = 1000l * 60l * 60l * 24l;

    public static void merge(AptUnit unit, List<Status> relevantStatuses, SegmentPredicate pred, MergeHandler handler) {

    }

    /**
     * Must be applied only to segments with valid PK. Works with the persistence: i.e. changes to the <code>segment</code> are persisted in the DB, and the new
     * segment that is created during split is also persisted in the DB.
     * 
     * @param segment
     * @param splitDay
     * @param handler
     * @return a new segment that was created starting from splitDay until segment.dateFrom(), can return <code>null</code> if no splitting happened (in case
     *         spitDay.equals(segment.dateTo()))
     */
    public static AptUnitOccupancySegment splitSegment(AptUnitOccupancySegment segment, LogicalDate splitDay, SplittingHandler handler) {
        if (splitDay.equals(segment.dateFrom().getValue())) {
            handler.updateAfterSplitPointSegment(segment);
            Persistence.service().merge(segment);
            return segment;
        } else if (splitDay.equals(segment.dateTo().getValue())) {
            return null;
        } else {
            AptUnitOccupancySegment newSegment = segment.duplicate();
            newSegment.id().set(null);
            newSegment.dateFrom().setValue(splitDay);
            newSegment.dateTo().setValue(segment.dateTo().getValue());
            handler.updateAfterSplitPointSegment(newSegment);

            segment.dateTo().setValue(substractDay(splitDay));
            handler.updateBeforeSplitPointSegment(segment);

            Persistence.service().merge(segment);
            Persistence.service().merge(newSegment);
            return newSegment;
        }
    }

    /**
     * Find a segment <code>s</code> in a occupancy that contains <code>splitDay</code> and then split it to two parts <code>s1</code> and <code>s2</code>,
     * where <code>s1.dateFrom = s.dateFrom, s1.dateTo = splitDay - day, s2.dateFrom = splitDay, s2.dateTo = s1.dateTo</code>.
     * 
     * @param occupancyTimeline
     * @param splitDay
     *            The beginning of a newly inserted segment: <b>note</b> if the date of <code>splitDay</code> falls on some segments start date, then it's going
     *            to be replaced by the new one, and if it falls on some segments end date, it will not be changed, also in those cases handler for the
     *            new/altered segment, will not be called.
     * @param handler
     *            A handler that performs operations on the split segment, i.e. updates status of the new part.
     * @return index of the new segment in the <code>occupancyTimeline</code> list.
     * @throws IllegalStateException
     *             Might be thrown by the {@code handler} methods when the handler is requested to update the wrong occupancy state.
     */
    public static int splitOccupancy(List<AptUnitOccupancySegment> occupancyTimeline, LogicalDate splitDay, SplittingHandler handler)
            throws IllegalStateException {

        ListIterator<AptUnitOccupancySegment> i = occupancyTimeline.listIterator();
        AptUnitOccupancySegment segment = null;

        while (!splitDay.after((segment = i.next()).dateTo().getValue())) {
            // iterate until candidate segment for splitting operation is found
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

    public static int splitOccupancy(final AptUnit unit, LogicalDate splitDay, final SplittingHandler handler) {

        int i = splitOccupancy(retrieveOccupancy(unit, splitDay), splitDay, new SplittingHandler() {

            @Override
            public void updateBeforeSplitPointSegment(AptUnitOccupancySegment segment) throws IllegalStateException {
                handler.updateBeforeSplitPointSegment(segment);
                Persistence.service().merge(segment);

            }

            @Override
            public void updateAfterSplitPointSegment(AptUnitOccupancySegment segment) {
                handler.updateAfterSplitPointSegment(segment);
                segment.unit().set(unit);
                Persistence.service().merge(segment);
            }
        });

        return i;
    }

    /**
     * 
     * @param unit
     * @param fistSegmentContained
     *            the first segment in the retrieved list must contain this date
     * @return
     */
    public static List<AptUnitOccupancySegment> retrieveOccupancy(AptUnit unit, LogicalDate firstSegmentContained) {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = new EntityQueryCriteria<AptUnitOccupancySegment>(AptUnitOccupancySegment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unit));
        criteria.add(PropertyCriterion.le(criteria.proto().dateTo(), firstSegmentContained));
        criteria.asc(criteria.proto().dateFrom());
        List<AptUnitOccupancySegment> occupancyTimeline = Persistence.secureQuery(criteria);
        return occupancyTimeline;
    }

    /**
     * Retrieve occupancy segment for a <code>unit</code> that contains <code>contained</code> date.
     * 
     * @param unit
     * @param contained
     * @return
     */
    public static AptUnitOccupancySegment retrieveOccupancySegment(AptUnit unit, LogicalDate contained) {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = new EntityQueryCriteria<AptUnitOccupancySegment>(AptUnitOccupancySegment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unit));
        criteria.add(PropertyCriterion.ge(criteria.proto().dateFrom(), contained));
        criteria.add(PropertyCriterion.le(criteria.proto().dateTo(), contained));
        return Persistence.service().retrieve(criteria);
    }

    public static void assertStatus(AptUnitOccupancySegment segment, Status status) throws IllegalStateException {
        if (segment.status().getValue() != status) {
            throw new IllegalStateException("expected segment with status that equals " + status + " but got " + segment.status().getValue());
        }
    }

    public static LogicalDate substractDay(LogicalDate date) {
        return new LogicalDate(date.getTime() - MILLIS_IN_DAY);
    }

    public static LogicalDate addDay(LogicalDate date) {
        return new LogicalDate(date.getTime() + MILLIS_IN_DAY);
    }

    public static LogicalDate minDate(LogicalDate d1, LogicalDate d2) {
        return d1.before(d2) ? d1 : d2;
    }

    public static interface SegmentPredicate {

        boolean check(AptUnitOccupancySegment segment);

    }

    public static interface MergeHandler {

        void onMerged(AptUnitOccupancySegment merged, AptUnitOccupancySegment s1, AptUnitOccupancySegment s2);

    }

}
