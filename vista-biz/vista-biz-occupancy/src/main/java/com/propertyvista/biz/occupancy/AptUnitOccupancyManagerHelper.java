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
package com.propertyvista.biz.occupancy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.gwt.server.DateUtils;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;

/**
 * This class contains utility methods for unit occupancy manager.
 */
public class AptUnitOccupancyManagerHelper {

    private static final Logger log = LoggerFactory.getLogger(AptUnitOccupancyManagerHelper.class);

    public static void merge(AptUnit unit, LogicalDate startingAt, List<Status> relevantStatuses, MergeHandler handler) {
        merge(unit.getPrimaryKey(), startingAt, relevantStatuses, handler);
    }

    public static void merge(Key unitId, LogicalDate startingAt, List<Status> relevantStatuses, MergeHandler handler) {
        if (relevantStatuses.isEmpty()) {
            return;
        }
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = new EntityQueryCriteria<AptUnitOccupancySegment>(AptUnitOccupancySegment.class);
        criteria.asc(criteria.proto().dateFrom());
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unitId));
        criteria.add(PropertyCriterion.ge(criteria.proto().dateTo(), startingAt));
        criteria.add(PropertyCriterion.in(criteria.proto().status(), new ArrayList<Status>(relevantStatuses)));

        List<AptUnitOccupancySegment> segments = Persistence.service().query(criteria);
        LinkedList<AptUnitOccupancySegment> deleteCandidates = new LinkedList<AptUnitOccupancySegment>();

        Iterator<AptUnitOccupancySegment> i = segments.iterator();

        if (!i.hasNext()) {
            return;
        }
        AptUnitOccupancySegment s1 = i.next();

        if (i.hasNext()) {
            while (i.hasNext()) {
                AptUnitOccupancySegment s2 = i.next();
                if (s1.dateTo().getValue().equals(DateUtils.daysAdd(s2.dateFrom().getValue(), -1)) & handler.isMergeable(s1, s2)) {
                    AptUnitOccupancySegment merged = EntityFactory.create(AptUnitOccupancySegment.class);
                    merged.unit().id().setValue(unitId);
                    merged.dateFrom().setValue(s1.dateFrom().getValue());
                    merged.dateTo().setValue(s2.dateTo().getValue());
                    merged.status().setValue(s1.status().getValue());
                    handler.onMerged(merged, s1, s2);

                    deleteCandidates.add(s1);
                    deleteCandidates.add(s2);

                    Persistence.service().merge(merged);

                    s1 = merged;
                } else {
                    if (handler.isMergeable(s2, s2)) {
                        handler.onMerged(s2, s2, s2);
                        Persistence.service().merge(s2);
                        s1 = s2;
                    }
                }
            }
            for (AptUnitOccupancySegment s : deleteCandidates) {
                Persistence.service().delete(s);
            }
        } else {
            // here we treat the special case when there's only one segment to be merged
            if (handler.isMergeable(s1, s1)) {
                handler.onMerged(s1, s1, s1);
                Persistence.service().merge(s1);
            }
        }
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
    public static AptUnitOccupancySegment split(AptUnitOccupancySegment segment, LogicalDate splitDay, SplittingHandler handler) {
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

            segment.dateTo().setValue(DateUtils.daysAdd(splitDay, -1));
            handler.updateBeforeSplitPointSegment(segment);

            Persistence.service().merge(segment);
            Persistence.service().merge(newSegment);
            return newSegment;
        }
    }

    public static AptUnitOccupancySegment split(AptUnit unit, LogicalDate splitDay, SplittingHandler handler) {
        return split(unit.getPrimaryKey(), splitDay, handler);
    }

    public static AptUnitOccupancySegment split(Key unitId, LogicalDate splitDay, SplittingHandler handler) {
        AptUnitOccupancySegment segment = retrieveOccupancySegment(unitId, splitDay);
        if (segment == null) {
            throw new IllegalStateException("failed to retrieve segment that contains the " + SimpleDateFormat.getDateInstance().format(splitDay));
        } else {
            return split(segment, splitDay, handler);
        }
    }

    /**
     *
     * @param unit
     * @param dateContainedByTheFirstSegment
     *            the first segment in the retrieved list must contain this date
     * @return
     */
    public static List<AptUnitOccupancySegment> retrieveOccupancy(Key unitPk, LogicalDate dateContainedByTheFirstSegment) {
        return retrieveOccupancy(unitPk, dateContainedByTheFirstSegment, false);
    }

    /**
     *
     * @param unit
     * @param dateContainedByTheFirstSegment
     *            the first segment in the retrieved list must contain this date
     * @param segStatus
     *            desired segments status, null - any status
     * @return
     */
    public static List<AptUnitOccupancySegment> retrieveOccupancy(Key unitPk, LogicalDate dateContainedByTheFirstSegment, Status segStatus) {
        return retrieveOccupancy(unitPk, dateContainedByTheFirstSegment, segStatus, false);
    }

    /**
     *
     * @param unit
     * @param dateContainedByTheFirstSegment
     *            the first segment in the retrieved list must contain this date
     * @param dateFromDescending
     *            true for sorting by dateFrom in reverse order, false - ascending order
     * @return
     */
    public static List<AptUnitOccupancySegment> retrieveOccupancy(Key unitPk, LogicalDate dateContainedByTheFirstSegment, boolean dateFromDescending) {
        return retrieveOccupancy(unitPk, dateContainedByTheFirstSegment, null, false);
    }

    /**
     *
     * @param unit
     * @param dateContainedByTheFirstSegment
     *            the first segment in the retrieved list must contain this date
     * @param segStatus
     *            desired segments status, null - any status
     * @param dateFromDescending
     *            true for sorting by dateFrom in reverse order, false - ascending order
     * @return
     */
    public static List<AptUnitOccupancySegment> retrieveOccupancy(Key unitPk, LogicalDate dateContainedByTheFirstSegment, Status segStatus,
            boolean dateFromDescending) {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = new EntityQueryCriteria<AptUnitOccupancySegment>(AptUnitOccupancySegment.class);

        criteria.eq(criteria.proto().unit().id(), unitPk);
        criteria.ge(criteria.proto().dateTo(), dateContainedByTheFirstSegment);
        if (segStatus != null) {
            criteria.eq(criteria.proto().status(), segStatus);
        }
        if (dateFromDescending) {
            criteria.desc(criteria.proto().dateFrom());
        } else {
            criteria.asc(criteria.proto().dateFrom());
        }

        return Persistence.service().query(criteria);
    }

    public static void dumpOccupancy(Key unitPk) {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = new EntityQueryCriteria<AptUnitOccupancySegment>(AptUnitOccupancySegment.class);
        criteria.eq(criteria.proto().unit().id(), unitPk);
        for (AptUnitOccupancySegment s : Persistence.service().query(criteria)) {
            log.info("{} OccupancySegment: {} - {} {}; lease {}", unitPk, s.dateFrom(), s.dateTo(), s.status(), s.lease().getPrimaryKey());
        }
    }

    public static boolean isOccupancyListEmpty(Key unitPk) {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = new EntityQueryCriteria<AptUnitOccupancySegment>(AptUnitOccupancySegment.class);

        criteria.add(PropertyCriterion.eq(criteria.proto().unit().id(), unitPk));

        return !Persistence.service().exists(criteria);
    }

    /**
     * Retrieve occupancy segment for a <code>unit</code> that contains <code>contained</code> date.
     *
     * @param unit
     * @param contained
     * @return
     */
    public static AptUnitOccupancySegment retrieveOccupancySegment(AptUnit unit, LogicalDate contained) {
        return retrieveOccupancySegment(unit.getPrimaryKey(), contained);
    }

    public static AptUnitOccupancySegment retrieveOccupancySegment(Key unitId, LogicalDate contained) {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = new EntityQueryCriteria<AptUnitOccupancySegment>(AptUnitOccupancySegment.class);

        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), unitId));
        criteria.add(PropertyCriterion.le(criteria.proto().dateFrom(), contained));
        criteria.add(PropertyCriterion.ge(criteria.proto().dateTo(), contained));

        return Persistence.secureRetrieve(criteria);
    }

    public static void assertStatus(AptUnitOccupancySegment segment, Status status) throws IllegalStateException {
        if (segment == null) {
            throw new IllegalStateException("expected segment with status that equals " + status + " but got NULL");
        } else if (segment.status().getValue() != status) {
            throw new IllegalStateException("expected segment with status that equals " + status + " but got " + segment.status().getValue());
        }
    }

    public static LogicalDate minDate(LogicalDate d1, LogicalDate d2) {
        return d1.before(d2) ? d1 : d2;
    }

    public static interface SegmentPredicate {
        boolean isMergeApplicableTo(AptUnitOccupancySegment segment);
    }

    public static interface MergeHandler {

        boolean isMergeable(AptUnitOccupancySegment s1, AptUnitOccupancySegment s2);

        void onMerged(AptUnitOccupancySegment merged, AptUnitOccupancySegment s1, AptUnitOccupancySegment s2);
    }

    public static Lease retriveCurrentLease(AptUnit unitId) {
        {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.eq(criteria.proto().unit(), unitId);
            criteria.in(criteria.proto().status(), Lease.Status.Active);
            // set sorting by 'from date' to get last active lease first.
            criteria.desc(criteria.proto().leaseFrom());
            Lease lease = Persistence.service().retrieve(criteria);
            if (lease != null) {
                return lease;
            }
        }

        // No Active Lease, return all the rest
        {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.eq(criteria.proto().unit(), unitId);
            criteria.in(criteria.proto().status(), Lease.Status.current());
            // set sorting by 'from date' to get last active lease first:
            criteria.desc(criteria.proto().leaseFrom());
            Lease lease = Persistence.service().retrieve(criteria);
            if (lease != null) {
                return lease;
            }
        }

        // Just find Present.
        {
            EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
            criteria.eq(criteria.proto().unit(), unitId);
            criteria.in(criteria.proto().status(), Lease.Status.present());
            // set sorting by 'from date' to get last active lease first:
            criteria.desc(criteria.proto().leaseFrom());
            return Persistence.service().retrieve(criteria);
        }
    }
}
