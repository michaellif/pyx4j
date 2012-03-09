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
package com.propertyvista.server.common.util.occupancy.mk2;

import org.junit.Test;

import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;

public class UnitOccupancyManagerTest extends UnitOccupancyManagerTestBase {

    @Test
    public void testInitialization() {
        s().from("2011-01-01").toTheEndOfTime().status(Status.vacant).x();

        om().scopeAvailable(unitId(), asDate("2011-02-03"));

        e().from("2011-01-01").to("2011-02-02").status(Status.vacant).x();
        e().from("2011-02-03").toTheEndOfTime().status(Status.available).x();
        assertExpectedTimeline();
    }

    @Test
    public void testReserveWhenAvailable() {
        s().fromTheBeginning().toTheEndOfTime().status(Status.available).x();

        Lease lease = lease("2011-02-15", "2011-10-25");
        om().reserve(unitId(), asDate("2011-02-03"), lease);

        e().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        e().from("2011-02-03").toTheEndOfTime().status(Status.reserved).lease(lease).x();
        assertExpectedTimeline();
    }

    @Test
    public void testReserveWhenLeased() {
        Lease lease1 = lease("2011-02-02", "2011-10-01");
        s().fromTheBeginning().to("2011-02-01").status(Status.available).x();
        s().from("2011-02-02").to("2011-10-02").status(Status.leased).lease(lease1).x();
        s().from("2011-10-03").toTheEndOfTime().status(Status.available).x();

        Lease lease2 = lease("2011-10-05", "2012-12-31");
        om().reserve(unitId(), asDate("2011-10-03"), lease2);

        e().fromTheBeginning().to("2011-02-01").status(Status.available).x();
        e().from("2011-02-02").to("2011-10-02").status(Status.leased).lease(lease1).x();
        e().from("2011-10-03").toTheEndOfTime().status(Status.reserved).lease(lease2).x();
        assertExpectedTimeline();
    }

    @Test
    public void testCancelFutureReserved() {
        Lease lease = lease("2011-02-15", "2011-10-25");
        s().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        s().from("2011-02-03").toTheEndOfTime().status(Status.reserved).lease(lease).x();

        om().cancelReservation(unitId(), asDate("2011-02-03"));

        e().fromTheBeginning().toTheEndOfTime().status(Status.available).x();
        assertExpectedTimeline();
    }

    @Test
    public void testCancelPresentReserved() {
        Lease lease = lease("2011-02-15", "2011-10-25");
        s().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        s().from("2011-02-03").toTheEndOfTime().status(Status.reserved).lease(lease).x();

        om().cancelReservation(unitId(), asDate("2011-02-05"));

        e().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        e().from("2011-02-03").to("2011-02-04").status(Status.reserved).lease(lease).x();
        e().from("2011-02-05").toTheEndOfTime().status(Status.available).x();
        assertExpectedTimeline();
    }

    @Test
    public void testApproveLeaseWhenReservedInFuture() {
        Lease lease = lease("2011-02-15", "2011-10-25");
        s().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        s().from("2011-02-03").toTheEndOfTime().status(Status.reserved).lease(lease).x();

        om().approveLease(unitId(), asDate("2011-02-15"));

        e().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        e().from("2011-02-03").to("2011-02-14").status(Status.reserved).lease(lease).x();
        e().from("2011-02-15").toTheEndOfTime().status(Status.leased).lease(lease).x();
        assertExpectedTimeline();
    }

    @Test
    public void testEndLease() {
        Lease lease = lease("2011-02-15", "2011-10-25");

        s().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        s().from("2011-01-03").to("2011-02-14").status(Status.reserved).lease(lease).x();
        s().from("2011-01-15").toTheEndOfTime().status(Status.leased).lease(lease).x();

        // TODO Artyom: I couldn't find any specification for this issue, but for now let's pretend "moveOutNotice" in lease means the date when the move out notice was given  
        lease.moveOutNotice().setValue(asDate("2011-08-01"));
        lease.expectedMoveOut().setValue(asDate("2011-10-23")); // two days before leaseTo() date
        updateLease(lease);

        om().endLease(unitId(), asDate("2011-10-25"));

        e().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        e().from("2011-01-03").to("2011-02-14").status(Status.reserved).lease(lease).x();
        e().from("2011-01-15").to("2011-10-25").status(Status.leased).lease(lease).x();
        e().from("2011-10-26").toTheEndOfTime().status(Status.vacant).x();
        assertExpectedTimeline();
    }

    @Test
    public void testScopeAvailable() {
        Lease lease = lease("2011-02-15", "2011-10-25");

        s().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        s().from("2011-01-03").to("2011-02-14").status(Status.reserved).lease(lease).x();
        s().from("2011-01-15").to("2011-10-25").status(Status.leased).lease(lease).x();
        s().from("2011-10-26").toTheEndOfTime().status(Status.vacant).x();

        om().scopeAvailable(unitId(), asDate("2011-10-26"));

        e().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        e().from("2011-01-03").to("2011-02-14").status(Status.reserved).lease(lease).x();
        e().from("2011-01-15").to("2011-10-25").status(Status.leased).lease(lease).x();
        e().from("2011-10-26").toTheEndOfTime().status(Status.available).x();
        assertExpectedTimeline();
    }

    @Test
    public void testScopeRenovationWhenLeased() {
        Lease lease = lease("2011-02-15", "2011-10-25");

        s().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        s().from("2011-01-03").to("2011-02-14").status(Status.reserved).lease(lease).x();
        s().from("2011-02-15").to("2011-10-25").status(Status.leased).lease(lease).x();
        s().from("2011-10-26").toTheEndOfTime().status(Status.vacant).x();

        om().scopeRenovation(unitId(), asDate("2011-10-26"), asDate("2011-11-10"));

        e().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        e().from("2011-01-03").to("2011-02-14").status(Status.reserved).lease(lease).x();
        e().from("2011-02-15").to("2011-10-25").status(Status.leased).lease(lease).x();
        e().from("2011-10-26").to("2011-11-10").status(Status.renovation).x();
        e().from("2011-11-11").toTheEndOfTime().status(Status.available).x();
        assertExpectedTimeline();
    }

    @Test
    public void testScopeRenovationWhenVacant() {
        Lease lease = lease("2011-02-15", "2011-10-25");

        s().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        s().from("2011-01-03").to("2011-02-14").status(Status.reserved).lease(lease).x();
        s().from("2011-02-15").to("2011-10-25").status(Status.leased).lease(lease).x();
        s().from("2011-10-26").toTheEndOfTime().status(Status.vacant).x();

        om().scopeRenovation(unitId(), asDate("2011-11-05"), asDate("2011-11-10"));

        e().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        e().from("2011-01-03").to("2011-02-14").status(Status.reserved).lease(lease).x();
        e().from("2011-02-15").to("2011-10-25").status(Status.leased).lease(lease).x();
        e().from("2011-10-26").to("2011-11-04").status(Status.vacant).x();
        e().from("2011-11-05").to("2011-11-10").status(Status.renovation).x();
        e().from("2011-11-11").toTheEndOfTime().status(Status.available).x();
        assertExpectedTimeline();
    }

    @Test
    public void testScopeOffMarketSingle() {
        s().fromTheBeginning().toTheEndOfTime().status(Status.vacant).x();

        om().scopeOffMarket(unitId(), asDate("2011-05-02"), OffMarketType.down);

        e().fromTheBeginning().to("2011-05-01").status(Status.vacant).x();
        e().from("2011-05-02").toTheEndOfTime().status(Status.offMarket).offMarketType(OffMarketType.down).x();
        assertExpectedTimeline();
    }

    @Test
    public void testScopeOffMarketCanAddAnotherFutureOffMarketSegment() {
        s().fromTheBeginning().to("2011-05-19").status(Status.vacant).x();
        s().from("2011-05-20").to("2011-07-19").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2011-07-20").toTheEndOfTime().status(Status.vacant).x();

        om().scopeOffMarket(unitId(), asDate("2011-07-20"), OffMarketType.model);

        e().fromTheBeginning().to("2011-05-19").status(Status.vacant).x();
        e().from("2011-05-20").to("2011-07-19").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        e().from("2011-07-20").toTheEndOfTime().status(Status.offMarket).offMarketType(OffMarketType.model).x();
        assertExpectedTimeline();
    }

    @Test
    public void testMakeVacantAppliedToOffMarket() {
        s().fromTheBeginning().to("2011-05-19").status(Status.vacant).x();
        s().from("2011-05-20").to("2011-07-19").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2011-07-20").toTheEndOfTime().status(Status.offMarket).offMarketType(OffMarketType.model).x();

        om().makeVacant(unitId(), asDate("2011-06-15"));

        e().fromTheBeginning().to("2011-05-19").status(Status.vacant).x();
        e().from("2011-05-20").to("2011-06-14").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        e().from("2011-06-15").toTheEndOfTime().status(Status.vacant).x();
        assertExpectedTimeline();
    }

    @Test
    public void testMakeVacantWhenAppliedToOffMarketAndAvailable() {
        s().fromTheBeginning().to("2011-05-19").status(Status.vacant).x();
        s().from("2011-05-20").to("2011-07-19").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2011-07-20").toTheEndOfTime().status(Status.available).x();

        om().makeVacant(unitId(), asDate("2011-06-15"));

        e().fromTheBeginning().to("2011-05-19").status(Status.vacant).x();
        e().from("2011-05-20").to("2011-06-14").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        e().from("2011-06-15").toTheEndOfTime().status(Status.vacant).x();
        assertExpectedTimeline();
    }

    @Test
    public void testMakeVacantAppliedToOffMarketAndAvailable2() {
        s().fromTheBeginning().to("2011-05-19").status(Status.vacant).x();
        s().from("2011-05-20").to("2011-07-19").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2011-07-20").toTheEndOfTime().status(Status.available).x();

        om().makeVacant(unitId(), asDate("2011-05-20"));

        e().fromTheBeginning().toTheEndOfTime().status(Status.vacant).x();
        assertExpectedTimeline();
    }

    @Test
    public void testCancelEndLease() {
        Lease lease = lease("2011-05-20", "2012-12-31");
        s().fromTheBeginning().to("2011-05-19").status(Status.vacant).x();
        s().from("2011-05-20").to("2012-12-31").status(Status.leased).lease(lease).x();
        s().from("2013-01-01").toTheEndOfTime().status(Status.available).x();

        om().cancelEndLease(unitId());

        e().fromTheBeginning().to("2011-05-19").status(Status.vacant).x();
        e().from("2011-05-20").toTheEndOfTime().status(Status.leased).lease(lease).x();
    }

}
