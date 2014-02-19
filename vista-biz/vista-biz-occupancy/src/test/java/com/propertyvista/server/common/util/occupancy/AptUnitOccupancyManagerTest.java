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
package com.propertyvista.server.common.util.occupancy;

import org.junit.Test;

import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;

public class AptUnitOccupancyManagerTest extends AptUnitOccupancyManagerTestBase {

    @Test
    public void testInitialization() {
        setup().from("2011-01-01").toTheEndOfTime().status(Status.pending).x();

        now("2011-02-03");
        getUOM().scopeAvailable(unitId);

        expect().from("2011-01-01").to("2011-02-02").status(Status.pending).x();
        expect().from("2011-02-03").toTheEndOfTime().status(Status.available).x();
        assertExpectedTimeline();
        assertUnitIsAvailableFrom("2011-02-03");
    }

    @Test
    public void testScopeAvailable() {
        Lease lease = createLease("2011-02-15", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        setup().from("2011-01-15").to("2011-10-25").status(Status.occupied).withLease(lease).x();
        setup().from("2011-10-26").toTheEndOfTime().status(Status.pending).x();

        now("2011-09-25");
        getUOM().scopeAvailable(unitId);

        expect().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        expect().from("2011-01-15").to("2011-10-25").status(Status.occupied).withLease(lease).x();
        expect().from("2011-10-26").toTheEndOfTime().status(Status.available).x();
        assertExpectedTimeline();
        assertUnitIsAvailableFrom("2011-10-26");
    }

    @Test
    public void testScopeAvailableMergeSegments() {

        setup().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        setup().from("2011-02-03").toTheEndOfTime().status(Status.pending).x();

        now("2011-02-03");
        getUOM().scopeAvailable(unitId);

        expect().fromTheBeginning().toTheEndOfTime().status(Status.available).x();
        assertExpectedTimeline();
        assertUnitIsAvailableFrom("2011-02-03");
    }

    @Test
    public void testScopeRenovationWhenLeased() {
        Lease lease = createLease("2011-02-15", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        setup().from("2011-02-15").to("2011-10-25").status(Status.occupied).withLease(lease).x();
        setup().from("2011-10-26").toTheEndOfTime().status(Status.pending).x();

        now("2011-09-25");
        getUOM().scopeRenovation(unitId, asDate("2011-11-10"));

        expect().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        expect().from("2011-02-15").to("2011-10-25").status(Status.occupied).withLease(lease).x();
        expect().from("2011-10-26").to("2011-11-10").status(Status.renovation).x();
        expect().from("2011-11-11").toTheEndOfTime().status(Status.available).x();
        assertExpectedTimeline();
        assertUnitIsAvailableFrom("2011-11-11");
    }

    @Test
    public void testScopeRenovationWhenVacant() {
        Lease lease = createLease("2011-02-15", "2011-10-25");

        setup().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        setup().from("2011-02-15").to("2011-10-25").status(Status.occupied).withLease(lease).x();
        setup().from("2011-10-26").toTheEndOfTime().status(Status.pending).x();

        now("2011-11-05");
        getUOM().scopeRenovation(unitId, asDate("2011-11-10"));

        expect().fromTheBeginning().to("2011-02-14").status(Status.available).x();
        expect().from("2011-02-15").to("2011-10-25").status(Status.occupied).withLease(lease).x();
        expect().from("2011-10-26").to("2011-11-04").status(Status.pending).x();
        expect().from("2011-11-05").to("2011-11-10").status(Status.renovation).x();
        expect().from("2011-11-11").toTheEndOfTime().status(Status.available).x();
        assertExpectedTimeline();
        assertUnitIsAvailableFrom("2011-11-11");
    }

    @Test
    public void testScopeRenovationSegmentMerging() {

        setup().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        setup().from("2011-01-03").to("2011-02-14").status(Status.renovation).x();
        setup().from("2011-02-15").toTheEndOfTime().status(Status.pending).x();

        now("2011-02-05");
        getUOM().scopeRenovation(unitId, asDate("2011-02-20"));

        expect().fromTheBeginning().to("2011-02-02").status(Status.available).x();
        expect().from("2011-01-03").to("2011-02-20").status(Status.renovation).x();
        expect().from("2011-02-21").toTheEndOfTime().status(Status.available).x();
        assertExpectedTimeline();
        assertUnitIsAvailableFrom("2011-02-21");
    }

    @Test
    public void testScopeOffMarketSingle() {
        setup().fromTheBeginning().toTheEndOfTime().status(Status.pending).x();

        now("2011-05-02");

        getUOM().scopeOffMarket(unitId, OffMarketType.down);

        expect().fromTheBeginning().to("2011-05-01").status(Status.pending).x();
        expect().from("2011-05-02").toTheEndOfTime().status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    @Test
    public void testScopeOffMarketCanAddAnotherFutureOffMarketSegment() {
        setup().fromTheBeginning().to("2011-05-19").status(Status.pending).x();
        setup().from("2011-05-20").to("2011-07-19").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2011-07-20").toTheEndOfTime().status(Status.pending).x();

        now("2011-07-18");

        getUOM().scopeOffMarket(unitId, OffMarketType.model);

        expect().fromTheBeginning().to("2011-05-19").status(Status.pending).x();
        expect().from("2011-05-20").to("2011-07-19").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        expect().from("2011-07-20").toTheEndOfTime().status(Status.offMarket).withOffMarketType(OffMarketType.model).x();
        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    @Test
    public void testMakeVacantAppliedToOffMarket() {
        setup().fromTheBeginning().to("2011-05-19").status(Status.pending).x();
        setup().from("2011-05-20").to("2011-07-19").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2011-07-20").toTheEndOfTime().status(Status.offMarket).withOffMarketType(OffMarketType.model).x();

        now("2011-05-01");

        getUOM().makeVacant(unitId, asDate("2011-06-15"));

        expect().fromTheBeginning().to("2011-05-19").status(Status.pending).x();
        expect().from("2011-05-20").to("2011-06-14").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        expect().from("2011-06-15").toTheEndOfTime().status(Status.pending).x();
        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    @Test
    public void testMakeVacantWhenAppliedToOffMarketAndAvailable() {
        setup().fromTheBeginning().to("2011-05-19").status(Status.pending).x();
        setup().from("2011-05-20").to("2011-07-19").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2011-07-20").toTheEndOfTime().status(Status.available).x();

        now("2011-05-01");

        getUOM().makeVacant(unitId, asDate("2011-06-15"));

        expect().fromTheBeginning().to("2011-05-19").status(Status.pending).x();
        expect().from("2011-05-20").to("2011-06-14").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        expect().from("2011-06-15").toTheEndOfTime().status(Status.pending).x();
        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    @Test
    public void testMakeVacantAppliedToAvailable() {
        setup().fromTheBeginning().to("2011-05-19").status(Status.pending).x();
        setup().from("2011-05-20").to("2011-07-19").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2011-07-20").toTheEndOfTime().status(Status.available).x();

        now("2011-05-01");

        getUOM().makeVacant(unitId, asDate("2011-07-20"));

        expect().fromTheBeginning().to("2011-05-19").status(Status.pending).x();
        expect().from("2011-05-20").to("2011-07-19").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        expect().from("2011-07-20").toTheEndOfTime().status(Status.pending).x();
        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    @Test
    public void testMakeVacantJoinsSegments() {
        setup().fromTheBeginning().to("2011-05-19").status(Status.pending).x();
        setup().from("2011-05-20").to("2011-07-19").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2011-07-20").toTheEndOfTime().status(Status.available).x();

        now("2011-05-01");

        getUOM().makeVacant(unitId, asDate("2011-05-20"));

        expect().fromTheBeginning().toTheEndOfTime().status(Status.pending).x();
        assertExpectedTimeline();
        assertUnitIsNotAvailable();
    }

    @Test
    public void testMigrateStart() {
        setup().fromTheBeginning().toTheEndOfTime().status(Status.pending).x();

        now("2001-01-05");

        Lease lease;
        getUOM().migrateStart(unitStub(), lease = createLease("2000-01-01", "2012-12-31"));

        expect().fromTheBeginning().to("2001-01-04").status(Status.pending).x();
        expect().from("2001-01-05").toTheEndOfTime().status(Status.migrated).withLease(lease).x();
        assertUnitIsNotAvailable();
    }

    @Test
    public void testMigrateApprove() {
        Lease lease = createLease("2000-01-01", "2012-12-31");
        setup().fromTheBeginning().to("2001-01-04").status(Status.pending).x();
        setup().from("2001-01-05").toTheEndOfTime().status(Status.migrated).withLease(lease).x();

        now("2001-01-20");
        getUOM().migratedApprove(unitStub());

        expect().fromTheBeginning().to("2001-01-04").status(Status.pending).x();
        expect().from("2001-01-05").to("2001-01-19").status(Status.migrated).withLease(lease).x();
        expect().from("2001-01-20").toTheEndOfTime().status(Status.occupied).withLease(lease).x();
        assertUnitIsNotAvailable();
    }

    @Test
    public void testMigrateApproveOnTheSameDay() {
        Lease lease = createLease("2000-01-01", "2012-12-31");
        setup().fromTheBeginning().to("2001-01-04").status(Status.pending).x();
        setup().from("2001-01-05").toTheEndOfTime().status(Status.migrated).withLease(lease).x();

        now("2001-01-05");
        getUOM().migratedApprove(unitStub());

        expect().fromTheBeginning().to("2001-01-04").status(Status.pending).x();
        expect().from("2001-01-05").toTheEndOfTime().status(Status.occupied).withLease(lease).x();
        assertUnitIsNotAvailable();
    }

    @Test
    public void testMigrateCancel() {
        Lease lease = createLease("2000-01-01", "2012-12-31");
        setup().fromTheBeginning().to("2001-01-04").status(Status.pending).x();
        setup().from("2001-01-05").toTheEndOfTime().status(Status.migrated).withLease(lease).x();

        now("2001-01-20");
        getUOM().migratedCancel(unitStub());

        expect().fromTheBeginning().to("2001-01-04").status(Status.pending).x();
        expect().from("2001-01-05").to("2001-01-19").status(Status.migrated).withLease(lease).x();
        expect().from("2001-01-20").toTheEndOfTime().status(Status.pending).x();
        assertUnitIsNotAvailable();
    }

    @Test
    public void testMigrateCancelOnTheSameDay() {
        Lease lease = createLease("2000-01-01", "2012-12-31");
        setup().fromTheBeginning().to("2001-01-04").status(Status.pending).x();
        setup().from("2001-01-05").toTheEndOfTime().status(Status.migrated).withLease(lease).x();

        now("2001-01-05");
        getUOM().migratedCancel(unitStub());

        expect().fromTheBeginning().toTheEndOfTime().status(Status.pending).x();
        assertUnitIsNotAvailable();
    }

}
