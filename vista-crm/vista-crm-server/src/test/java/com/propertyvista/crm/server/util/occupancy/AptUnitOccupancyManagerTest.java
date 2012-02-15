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

import org.junit.Ignore;
import org.junit.Test;

import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;

// TODO add test cases for future in present where applicable
@Ignore
public class AptUnitOccupancyManagerTest extends AptUnitOccupancyManagerTestBase {

    @Test
    public void testInitialization() {
        setup().from("2011-01-01").toTheEndOfTime().status(Status.vacant).x();

        now("2011-02-03");
        getUOM().scopeAvailable();

        expect().from("2011-01-01").to("2011-02-02").status(Status.vacant).x();
        expect().from("2011-02-03").toTheEndOfTime().status(Status.available).x();
        assertExpectedTimeline();
    }

    @Test
    public void testReserveWhenAvailable() {
        setup().fromTheBeginning().toTheEndOfTime().status(Status.available).x();

        now("2011-02-03");

        Lease lease = createLease("2011-02-15", "2011-10-25");
        getUOM().reserve(lease);

        expect().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        expect().from("2011-02-03").toTheEndOfTime().status(Status.reserved).withLease(lease).x();
        assertExpectedTimeline();
    }

    @Test
    public void testReserveWhenLeasedNoSegmentSplitting() {
        Lease lease1 = createLease("2011-02-02", "2011-10-01");
        setup().fromTheBeginning().to("2011-02-01").status(Status.available).x();
        setup().from("2011-02-02").to("2011-10-02").status(Status.leased).withLease(lease1).x();
        setup().from("2011-10-03").toTheEndOfTime().status(Status.available).x();

        now("2011-09-03");

        Lease lease2 = createLease("2011-10-03", "2012-12-31");
        getUOM().reserve(lease2);

        expect().fromTheBeginning().to("2011-02-01").status(Status.available).x();
        expect().from("2011-02-02").to("2011-10-02").status(Status.leased).withLease(lease1).x();
        expect().from("2011-10-03").toTheEndOfTime().status(Status.leased).withLease(lease2).x();
        assertExpectedTimeline();
    }

    @Test
    public void testReserveWhenLeasedWithSegmentSplitting() {
        Lease lease1 = createLease("2011-02-02", "2011-10-01");
        setup().fromTheBeginning().to("2011-02-01").status(Status.available).x();
        setup().from("2011-02-02").to("2011-10-02").status(Status.leased).withLease(lease1).x();
        setup().from("2011-10-03").toTheEndOfTime().status(Status.available).x();

        now("2011-09-03");

        Lease lease2 = createLease("2011-12-31", "2012-12-31");
        getUOM().reserve(lease2);

        expect().fromTheBeginning().to("2011-02-01").status(Status.available).x();
        expect().from("2011-02-02").to("2011-10-02").status(Status.leased).withLease(lease1).x();
        expect().from("2011-10-03").toTheEndOfTime().status(Status.reserved).withLease(lease2).x();
        expect().from("2011-12-31").toTheEndOfTime().status(Status.leased).withLease(lease2).x();
        assertExpectedTimeline();
    }

    @Test
    public void testUnreserveFutureReserved() {
        Lease lease = createLease("2011-02-15", "2011-10-25");
        setup().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        setup().from("2011-02-03").toTheEndOfTime().status(Status.reserved).withLease(lease).x();

        now("2011-02-01");

        expect().fromTheBeginning().toTheEndOfTime().status(Status.available).x();
        assertExpectedTimeline();
    }

    @Test
    public void testUnreservePresentReserved() {
        Lease lease = createLease("2011-02-15", "2011-10-25");
        setup().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        setup().from("2011-02-03").toTheEndOfTime().status(Status.reserved).withLease(lease).x();

        setup().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        setup().from("2011-02-03").toTheEndOfTime().status(Status.reserved).withLease(lease).x();

        now("2011-02-10");

        getUOM().approveLease();

        expect().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        expect().from("2011-01-03").to("2011-02-14").status(Status.reserved).x();
        expect().from("2011-01-15").toTheEndOfTime().status(Status.leased).withLease(lease).x();
        assertExpectedTimeline();
    }

    @Test
    public void testApproveLeaseWhenReservedInFuture() {
        throw new RuntimeException("this test has not yet been implemented");
    }

    @Test
    public void testEndLease() {
        Lease lease = createLease("2011-02-15", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        setup().from("2011-01-03").to("2011-02-14").status(Status.reserved).x();
        setup().from("2011-01-15").toTheEndOfTime().status(Status.leased).withLease(lease).x();

        now("2011-08-01");

        // TODO Artyom: I couldn't find any specification for this issue, but for now let's pretend "moveOutNotice" in lease means the date when the move out notice was given  
        lease.moveOutNotice().setValue(asDate("2011-08-01"));
        lease.expectedMoveOut().setValue(asDate("2011-10-23")); // two days before leaseTo() date
        updateLease(lease);

        getUOM().endLease();

        expect().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        expect().from("2011-01-03").to("2011-02-14").status(Status.reserved).x();
        expect().from("2011-01-15").to("2011-10-25").status(Status.leased).withLease(lease).x();
        expect().from("2011-10-26").toTheEndOfTime().status(Status.vacant).x();
        assertExpectedTimeline();
    }

    @Test
    public void testScopeAvailable() {
        Lease lease = createLease("2011-02-15", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        setup().from("2011-01-03").to("2011-02-14").status(Status.reserved).x();
        setup().from("2011-01-15").to("2011-10-25").status(Status.leased).withLease(lease).x();
        setup().from("2011-10-26").toTheEndOfTime().status(Status.vacant).x();

        now("2011-09-25");
        getUOM().scopeAvailable();

        expect().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        expect().from("2011-01-03").to("2011-02-14").status(Status.reserved).x();
        expect().from("2011-01-15").to("2011-10-25").status(Status.leased).withLease(lease).x();
        expect().from("2011-10-26").toTheEndOfTime().status(Status.available).x();
        assertExpectedTimeline();
    }

    @Test
    public void testScopeRenovation() {
        Lease lease = createLease("2011-02-15", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        setup().from("2011-01-03").to("2011-02-14").status(Status.reserved).x();
        setup().from("2011-01-15").to("2011-10-25").status(Status.leased).withLease(lease).x();
        setup().from("2011-10-26").toTheEndOfTime().status(Status.vacant).x();

        now("2011-09-25");
        getUOM().scopeRenovation(asDate("2011-11-10"));

        expect().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        expect().from("2011-01-03").to("2011-02-14").status(Status.reserved).x();
        expect().from("2011-01-15").to("2011-10-25").status(Status.leased).withLease(lease).x();
        expect().from("2011-10-26").to("2011-11-10").status(Status.renovation).x();
        expect().from("2011-11-11").toTheEndOfTime().status(Status.available).x();
        assertExpectedTimeline();
    }

    @Test
    public void testScopeOffMarketSingle() {
        setup().fromTheBeginning().to("2011-02-02").status(Status.vacant).x();

        now("2011-05-01");

        getUOM().scopeOffMarket(OffMarketType.down, asDate("2011-05-20"));

        expect().fromTheBeginning().to("2011-05-19").status(Status.vacant).x();
        expect().from("2011-05-20").toTheEndOfTime().status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        assertExpectedTimeline();
    }

    @Test
    public void testScopeOffMarketCanAddAnotherOffMarketSegment() {
        setup().fromTheBeginning().to("2011-02-02").status(Status.vacant).x();
        setup().from("2011-05-20").toTheEndOfTime().status(Status.offMarket).withOffMarketType(OffMarketType.down).x();

        now("2011-05-02");

        getUOM().scopeOffMarket(OffMarketType.model, asDate("2011-07-20"));

        expect().fromTheBeginning().to("2011-05-19").status(Status.vacant).x();
        expect().from("2011-05-20").to("2011-07-19").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        expect().from("2011-07-19").toTheEndOfTime().status(Status.offMarket).withOffMarketType(OffMarketType.model).x();
        assertExpectedTimeline();
    }

    @Test
    public void testMakeVacant() {
        setup().fromTheBeginning().to("2011-05-19").status(Status.vacant).x();
        setup().from("2011-05-20").to("2011-07-19").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2011-07-19").toTheEndOfTime().status(Status.offMarket).withOffMarketType(OffMarketType.model).x();

        now("2011-05-01");

        getUOM().makeVacant(asDate("2011-06-15"));

        expect().fromTheBeginning().to("2011-05-19").status(Status.vacant).x();
        expect().from("2011-05-20").to("2011-06-14").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        expect().from("2011-06-15").toTheEndOfTime().status(Status.vacant).x();
        assertExpectedTimeline();
    }

}
