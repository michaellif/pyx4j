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

import org.junit.Ignore;
import org.junit.Test;

import com.propertyvista.biz.occupancy.OccupancyOperationException;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;

@Ignore
public class AptUnitOccupancyManagerSetMoveOutDateTest extends AptUnitOccupancyManagerTestBase {

    @Test
    public void testSetMoveOut() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        setup().from("2011-01-03").to("2011-02-14").status(Status.reserved).withLease(lease).x();
        setup().from("2011-01-15").toTheEndOfTime().status(Status.leased).withLease(lease).x();

        now("2011-08-01");

        getUOM().moveOut(unitId, asDate("2011-10-25"));

        expect().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        expect().from("2011-01-03").to("2011-02-14").status(Status.reserved).withLease(lease).x();
        expect().from("2011-01-15").to("2011-10-25").status(Status.leased).withLease(lease).x();
        expect().from("2011-10-26").toTheEndOfTime().status(Status.pending).x();

        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    /**
     * sets move out before lease end date
     * 
     * @throws OccupancyOperationException
     */
    @Test
    public void testSetMoveOut2() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        setup().from("2011-01-03").to("2011-02-14").status(Status.reserved).withLease(lease).x();
        setup().from("2011-01-15").toTheEndOfTime().status(Status.leased).withLease(lease).x();

        now("2011-08-01");

        getUOM().moveOut(unitId, asDate("2011-10-20"));

        expect().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        expect().from("2011-01-03").to("2011-02-14").status(Status.reserved).withLease(lease).x();
        expect().from("2011-01-15").to("2011-10-20").status(Status.leased).withLease(lease).x();
        expect().from("2011-10-21").toTheEndOfTime().status(Status.pending).x();
        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    /**
     * tries to reschedule move out
     * 
     * @throws OccupancyOperationException
     */
    @Test
    public void testSetMoveOutRescheduleToFuture() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        setup().from("2011-01-03").to("2011-02-14").status(Status.reserved).withLease(lease).x();
        setup().from("2011-01-15").to("2011-10-20").status(Status.leased).withLease(lease).x();
        setup().from("2011-10-21").toTheEndOfTime().status(Status.pending).x();

        now("2011-08-01");

        getUOM().moveOut(unitId, asDate("2011-10-24"));

        expect().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        expect().from("2011-01-03").to("2011-02-14").status(Status.reserved).withLease(lease).x();
        expect().from("2011-01-15").to("2011-10-24").status(Status.leased).withLease(lease).x();
        expect().from("2011-10-25").toTheEndOfTime().status(Status.pending).x();
        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    /**
     * tries to reschedule move out with future renovation
     * 
     * @throws OccupancyOperationException
     */
    @Test
    public void testSetMoveOutRescheduleToFuture2() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        setup().from("2011-01-03").to("2011-02-14").status(Status.reserved).withLease(lease).x();
        setup().from("2011-01-15").to("2011-10-20").status(Status.leased).withLease(lease).x();
        setup().from("2011-10-21").to("2011-10-23").status(Status.pending).x();
        setup().from("2011-10-24").to("2011-10-25").status(Status.renovation).x();
        setup().from("2011-10-26").toTheEndOfTime().status(Status.available).x();

        now("2011-08-01");

        getUOM().moveOut(unitId, asDate("2011-10-24"));

        expect().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        expect().from("2011-01-03").to("2011-02-14").status(Status.reserved).withLease(lease).x();
        expect().from("2011-01-15").to("2011-10-24").status(Status.leased).withLease(lease).x();
        expect().from("2011-10-24").to("2011-10-25").status(Status.renovation).x();
        expect().from("2011-10-26").toTheEndOfTime().status(Status.available).x();
        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    /**
     * tries to reschedule move out with future renovation (must not erase pending segment)
     * 
     * @throws OccupancyOperationException
     */
    @Test
    public void testSetMoveOutRescheduleToFuture3() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        setup().from("2011-01-03").to("2011-02-14").status(Status.reserved).withLease(lease).x();
        setup().from("2011-01-15").to("2011-10-20").status(Status.leased).withLease(lease).x();
        setup().from("2011-10-21").to("2011-10-23").status(Status.pending).x();
        setup().from("2011-10-24").to("2011-10-25").status(Status.renovation).x();
        setup().from("2011-10-26").toTheEndOfTime().status(Status.available).x();

        now("2011-08-01");

        getUOM().moveOut(unitId, asDate("2011-10-22"));

        expect().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        expect().from("2011-01-03").to("2011-02-14").status(Status.reserved).withLease(lease).x();
        expect().from("2011-01-15").to("2011-10-22").status(Status.leased).withLease(lease).x();
        expect().from("2011-10-23").to("2011-10-23").status(Status.pending).x();
        expect().from("2011-10-24").to("2011-10-25").status(Status.renovation).x();
        expect().from("2011-10-26").toTheEndOfTime().status(Status.available).x();
        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    /**
     * tries to reschedule move out with future reservation (must not erase pending segment)
     * 
     * @throws OccupancyOperationException
     */
    public void testSetMoveOutRescheduleToFutureWhenUnitIsReservedInFuture() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");
        Lease leaseFuture = createLease("2011-02-24", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        setup().from("2011-01-03").to("2011-02-14").status(Status.reserved).withLease(lease).x();
        setup().from("2011-01-15").to("2011-10-20").status(Status.leased).withLease(lease).x();
        setup().from("2011-10-21").to("2011-10-23").status(Status.reserved).withLease(leaseFuture).x();
        setup().from("2011-10-24").toTheEndOfTime().status(Status.leased).withLease(leaseFuture).x();

        now("2011-08-01");

        getUOM().moveOut(unitId, asDate("2011-10-22"));

        expect().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        expect().from("2011-01-03").to("2011-02-14").status(Status.reserved).withLease(lease).x();
        expect().from("2011-01-15").to("2011-10-22").status(Status.leased).withLease(lease).x();
        expect().from("2011-10-23").to("2011-10-23").status(Status.reserved).withLease(leaseFuture).x();
        expect().from("2011-10-24").toTheEndOfTime().status(Status.leased).withLease(leaseFuture).x();

        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    /**
     * tries to reschedule move out with future reservation (must erase pending segment)
     * 
     * @throws OccupancyOperationException
     */
    public void testSetMoveOutRescheduleToFutureWhenUnitIsReservedInFuture2() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");
        Lease leaseFuture = createLease("2011-02-24", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        setup().from("2011-01-03").to("2011-02-14").status(Status.reserved).withLease(lease).x();
        setup().from("2011-01-15").to("2011-10-20").status(Status.leased).withLease(lease).x();
        setup().from("2011-10-21").to("2011-10-23").status(Status.reserved).withLease(leaseFuture).x();
        setup().from("2011-10-24").toTheEndOfTime().status(Status.leased).withLease(leaseFuture).x();

        now("2011-08-01");

        getUOM().moveOut(unitId, asDate("2011-10-23"));

        expect().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        expect().from("2011-01-03").to("2011-02-14").status(Status.reserved).withLease(lease).x();
        expect().from("2011-01-15").to("2011-10-23").status(Status.leased).withLease(lease).x();
        expect().from("2011-10-24").toTheEndOfTime().status(Status.leased).withLease(leaseFuture).x();

        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    /**
     * tries to reschedule move out with future reservation must fail because move out date is greater than lease start
     * 
     * @throws OccupancyOperationException
     */
    @Test(expected = OccupancyOperationException.class)
    public void testSetMoveOutRescheduleToFutureFailsWhenUnitIsReservedInFuture() throws OccupancyOperationException {
        Lease lease = createLease("2011-02-15", "2011-10-25");
        Lease leaseFuture = createLease("2011-02-24", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        setup().from("2011-01-03").to("2011-02-14").status(Status.reserved).withLease(lease).x();
        setup().from("2011-01-15").to("2011-10-20").status(Status.leased).withLease(lease).x();
        setup().from("2011-10-21").to("2011-10-23").status(Status.reserved).withLease(leaseFuture).x();
        setup().from("2011-10-24").toTheEndOfTime().status(Status.leased).withLease(leaseFuture).x();

        now("2011-08-01");

        getUOM().moveOut(unitId, asDate("2011-10-25"));

        setup().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        setup().from("2011-01-03").to("2011-02-14").status(Status.reserved).withLease(lease).x();
        setup().from("2011-01-15").to("2011-10-20").status(Status.leased).withLease(lease).x();
        setup().from("2011-10-21").to("2011-10-23").status(Status.reserved).withLease(leaseFuture).x();
        setup().from("2011-10-24").toTheEndOfTime().status(Status.leased).withLease(leaseFuture).x();

        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void testCancelEndLease() throws Exception {
        Lease lease = createLease("2011-05-20", "2012-12-31");
        setup().fromTheBeginning().to("2011-05-19").status(Status.pending).x();
        setup().from("2011-05-20").to("2012-12-31").status(Status.leased).withLease(lease).x();
        setup().from("2013-01-01").toTheEndOfTime().status(Status.available).x();

        now("2011-11-01");

        getUOM().cancelMoveOut(unitId);

        expect().fromTheBeginning().to("2011-05-19").status(Status.pending).x();
        expect().from("2011-05-20").toTheEndOfTime().status(Status.leased).withLease(lease).x();
        assertUnitIsNotAvailable();
    }

}
