/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-01
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.util.occupancy;

import org.junit.Test;

import com.propertyvista.biz.occupancy.OccupancyOperationException;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;

public class AptUnitOccupancyManagerMoveOutTest extends AptUnitOccupancyManagerTestBase {

    /** test set move out date for occupied segment without move out date */
    @Test
    public void testMoveOutOnLeaseEndDate() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        setup().from("2011-02-15").toTheEndOfTime().status(Status.occupied).withLease(lease).x();

        now("2011-08-01");

        getUOM().moveOut(unitId, asDate("2011-10-25"), lease);

        expect().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        expect().from("2011-02-15").to("2011-10-25").status(Status.occupied).withLease(lease).x();
        expect().from("2011-10-26").toTheEndOfTime().status(Status.pending).x();

        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    /** test set move out date for occupied segment without move out date: move out date is BEFORE lease end date */
    @Test
    public void testMoveOutBeforeLeaseEnd() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        setup().from("2011-02-15").toTheEndOfTime().status(Status.occupied).withLease(lease).x();

        now("2011-08-01");

        getUOM().moveOut(unitId, asDate("2011-10-20"), lease);

        expect().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        expect().from("2011-02-15").to("2011-10-20").status(Status.occupied).withLease(lease).x();
        expect().from("2011-10-21").toTheEndOfTime().status(Status.pending).x();

        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    @Test
    public void testMoveOutRescheduledWhenMoveOutDateIsOver() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        setup().from("2011-02-15").to("2011-10-20").status(Status.occupied).withLease(lease).x();
        setup().from("2011-10-21").toTheEndOfTime().status(Status.pending).x();

        now("2011-10-24");

        getUOM().moveOut(unitId, asDate("2011-10-24"), lease);

        expect().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        expect().from("2011-02-15").to("2011-10-24").status(Status.occupied).withLease(lease).x();
        expect().from("2011-10-25").toTheEndOfTime().status(Status.pending).x();

        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    /** move out renders unit as 'pending' because it means that unit has to be rescoped */
    @Test
    public void testMoveOutRescheduledWhenMoveOutDateIsOverAndAvailable() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        setup().from("2011-02-15").to("2011-10-20").status(Status.occupied).withLease(lease).x();
        setup().from("2011-10-21").toTheEndOfTime().status(Status.available).x();

        now("2011-10-24");

        getUOM().moveOut(unitId, asDate("2011-10-24"), lease);

        expect().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        expect().from("2011-02-15").to("2011-10-24").status(Status.occupied).withLease(lease).x();
        expect().from("2011-10-25").toTheEndOfTime().status(Status.available).x();

        assertExpectedTimeline();
        assertUnitIsAvailableFrom(asDate("2011-10-25"));
    }

    @Test
    public void testMoveOutRescheduleForward() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        setup().from("2011-02-15").to("2011-10-20").status(Status.occupied).withLease(lease).x();
        setup().from("2011-10-21").toTheEndOfTime().status(Status.pending).x();

        now("2011-08-01");

        getUOM().moveOut(unitId, asDate("2011-10-24"), lease);

        expect().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        expect().from("2011-02-15").to("2011-10-24").status(Status.occupied).withLease(lease).x();
        expect().from("2011-10-25").toTheEndOfTime().status(Status.pending).x();

        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    @Test
    public void testMoveOutRescheduleForwardWithDissappearingSegment() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        setup().from("2011-02-15").to("2011-10-20").status(Status.occupied).withLease(lease).x();
        setup().from("2011-10-21").to("2011-10-24").status(Status.pending).x();
        setup().from("2011-10-25").to("2011-10-25").status(Status.renovation).x();
        setup().from("2011-10-26").toTheEndOfTime().status(Status.available).x();

        now("2011-08-01");

        getUOM().moveOut(unitId, asDate("2011-10-24"), lease);

        expect().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        expect().from("2011-02-15").to("2011-10-24").status(Status.occupied).withLease(lease).x();
        expect().from("2011-10-25").to("2011-10-25").status(Status.renovation).x();
        expect().from("2011-10-26").toTheEndOfTime().status(Status.available).x();

        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    /**
     * tries to reschedule move out with future renovation (must not erase pending segment)
     */
    @Test
    public void testMoveOutRescheduleForward3() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        setup().from("2011-02-15").to("2011-10-20").status(Status.occupied).withLease(lease).x();
        setup().from("2011-10-21").to("2011-10-23").status(Status.pending).x();
        setup().from("2011-10-24").to("2011-10-25").status(Status.renovation).x();
        setup().from("2011-10-26").toTheEndOfTime().status(Status.available).x();

        now("2011-08-01");

        getUOM().moveOut(unitId, asDate("2011-10-22"), lease);

        expect().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        expect().from("2011-02-15").to("2011-10-22").status(Status.occupied).withLease(lease).x();
        expect().from("2011-10-23").to("2011-10-23").status(Status.pending).x();
        expect().from("2011-10-24").to("2011-10-25").status(Status.renovation).x();
        expect().from("2011-10-26").toTheEndOfTime().status(Status.available).x();

        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    /**
     * tries to reschedule move out with future reservation (must not erase available segment)
     */
    @Test
    public void testMoveOutRescheduleForward4() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");
        Lease leaseFuture = createLease("2011-10-24", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        setup().from("2011-02-15").to("2011-10-20").status(Status.occupied).withLease(lease).x();
        setup().from("2011-10-21").to("2011-10-23").status(Status.available).x();
        setup().from("2011-10-24").toTheEndOfTime().status(Status.occupied).withLease(leaseFuture).x();

        now("2011-08-01");

        getUOM().moveOut(unitId, asDate("2011-10-22"), lease);

        expect().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        expect().from("2011-02-15").to("2011-10-22").status(Status.occupied).withLease(lease).x();
        expect().from("2011-10-23").to("2011-10-23").status(Status.available).x();
        expect().from("2011-10-24").toTheEndOfTime().status(Status.occupied).withLease(leaseFuture).x();

        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    /**
     * tries to reschedule move out with future reservation (must erase available segment)
     */
    @Test
    public void testMoveOutRescheduleForwardWidthDisapperaingSegment2() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");
        Lease leaseFuture = createLease("2011-10-24", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        setup().from("2011-02-15").to("2011-10-20").status(Status.occupied).withLease(lease).x();
        setup().from("2011-10-21").to("2011-10-23").status(Status.available).x();
        setup().from("2011-10-24").toTheEndOfTime().status(Status.occupied).withLease(leaseFuture).x();

        now("2011-08-01");

        getUOM().moveOut(unitId, asDate("2011-10-23"), lease);

        expect().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        expect().from("2011-02-15").to("2011-10-23").status(Status.occupied).withLease(lease).x();
        expect().from("2011-10-24").toTheEndOfTime().status(Status.occupied).withLease(leaseFuture).x();

        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    /**
     * tries to reschedule move out with future reservation must fail because move out date is greater than lease start date of the next lease
     */
    @Test(expected = OccupancyOperationException.class)
    public void testMoveOutRescheduleForwardFailsWhenUnitIsReservedInForward() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");
        Lease leaseFuture = createLease("2011-02-24", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        setup().from("2011-02-15").to("2011-10-20").status(Status.occupied).withLease(lease).x();
        setup().from("2011-10-21").to("2011-10-23").status(Status.available).x();
        setup().from("2011-10-24").toTheEndOfTime().status(Status.occupied).withLease(leaseFuture).x();

        now("2011-08-01");

        getUOM().moveOut(unitId, asDate("2011-10-25"), lease);
    }

    @Test
    public void testMoveOutRescheduleBack() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        setup().from("2011-02-15").to("2011-10-20").status(Status.occupied).withLease(lease).x();
        setup().from("2011-10-21").toTheEndOfTime().status(Status.pending).x();

        now("2011-08-01");

        getUOM().moveOut(unitId, asDate("2011-10-19"), lease);

        expect().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        expect().from("2011-02-15").to("2011-10-19").status(Status.occupied).withLease(lease).x();
        expect().from("2011-10-20").toTheEndOfTime().status(Status.pending).x();

        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    @Test
    public void testMoveOutRescheduleBackWhenHasNextLease() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");
        Lease nextlease = createLease("2011-10-26", "2011-10-28");

        setup().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        setup().from("2011-02-15").to("2011-10-25").status(Status.occupied).withLease(lease).x();
        setup().from("2011-10-26").toTheEndOfTime().status(Status.occupied).withLease(nextlease).x();

        now("2011-08-01");

        getUOM().moveOut(unitId, asDate("2011-10-23"), lease);

        expect().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        expect().from("2011-02-15").to("2011-10-23").status(Status.occupied).withLease(lease).x();
        expect().from("2011-10-24").to("2011-10-25").status(Status.available).x();
        expect().from("2011-10-26").toTheEndOfTime().status(Status.occupied).withLease(nextlease).x();

        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    @Test
    public void testMoveOutRescheduleBackToTheBeginningOfTheLease() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        setup().from("2011-02-15").to("2011-10-20").status(Status.occupied).withLease(lease).x();
        setup().from("2011-10-21").toTheEndOfTime().status(Status.pending).x();

        now("2011-02-15");

        getUOM().moveOut(unitId, asDate("2011-02-15"), lease);

        expect().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        expect().from("2011-02-15").to("2011-02-15").status(Status.occupied).withLease(lease).x();
        expect().from("2011-02-16").toTheEndOfTime().status(Status.pending).x();

        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    @Test(expected = OccupancyOperationException.class)
    public void testMoveOutRescheduleBackMustFailWhenMoveOutIsBeforeLeaseStart() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        setup().from("2011-02-15").to("2011-10-20").status(Status.occupied).withLease(lease).x();
        setup().from("2011-10-21").toTheEndOfTime().status(Status.pending).x();

        now("2011-02-15");

        getUOM().moveOut(unitId, asDate("2011-02-14"), lease);
    }

    @Test
    public void testCancelMoveOut() throws Exception {
        Lease lease = createLease("2011-05-20", "2012-12-31");
        setup().fromTheBeginning().to("2011-05-19").status(Status.pending).x();
        setup().from("2011-05-20").to("2012-12-31").status(Status.occupied).withLease(lease).x();
        setup().from("2013-01-01").toTheEndOfTime().status(Status.available).x();

        now("2011-11-01");

        getUOM().cancelMoveOut(unitId);

        expect().fromTheBeginning().to("2011-05-19").status(Status.pending).x();
        expect().from("2011-05-20").toTheEndOfTime().status(Status.occupied).withLease(lease).x();
        assertUnitIsNotAvailable();
    }

}
