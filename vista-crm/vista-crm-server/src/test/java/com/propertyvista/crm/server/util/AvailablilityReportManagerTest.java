/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 13, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.util;

import org.junit.Ignore;
import org.junit.Test;

import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.RentReadiness;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.Scoping;
import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitAvailabilityStatus.Vacancy;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;

// TODO market rent, unit rent (and the deltas)
// TODO add tenant's name and contact information to the status
@Ignore
public class AvailablilityReportManagerTest extends AvailablilityReportManagerTestBase {

    @Test
    public void testAvailable() {
        setup().fromTheBeginning().toTheEndOfTime().status(Status.available);

        computeAvailabilityOn("2012-02-17");

        expectAvailability().on("2012-02-17").vacancy(Vacancy.Vacant).scoping(Scoping.Scoped).readiness(RentReadiness.RentReady).notrented().x();
    }

    @Test
    public void testVacant() {
        setup().fromTheBeginning().toTheEndOfTime().status(Status.vacant);

        computeAvailabilityOn("2012-02-17");

        expectAvailability().on("2012-02-17").vacancy(Vacancy.Vacant).scoping(Scoping.Scoped).notrented().x();
    }

    @Test
    public void testLeased() {
        Lease lease = createLease("2012-02-20", "2013-02-30");
        setup().fromTheBeginning().to("2012-02-17").status(Status.available).x();
        setup().from("2012-02-18").to("2012-02-19").status(Status.reserved).withLease(lease).x();
        setup().from("2012-02-20").toTheEndOfTime().status(Status.leased).withLease(lease).x();

        computeAvailabilityOn("2012-02-18");

        expectAvailability().on("2012-02-18").vacancy(Vacancy.Vacant).scoping(Scoping.Scoped).readiness(RentReadiness.RentReady).rented()
                .rentStartsOn("2012-12-20").x();
        expectAvailability().on("2012-02-20").occupied().x();
    }

    @Test
    public void testLeasedNoReservedSegment() {
        Lease lease = createLease("2012-02-20", "2013-03-01");

        setup().fromTheBeginning().to("2012-02-17").status(Status.available).x();
        setup().from("2012-02-20").toTheEndOfTime().status(Status.leased).withLease(lease).x();

        computeAvailabilityOn("2012-02-20");

        expectAvailability().on("2012-02-20").occupied().x();
    }

    @Test
    public void testReserved() {
        Lease lease = createLease("2012-02-20", "2013-02-30");
        setup().fromTheBeginning().to("2012-02-17").status(Status.available).x();
        setup().from("2012-02-18").toTheEndOfTime().status(Status.reserved).withLease(lease).x();

        computeAvailabilityOn("2012-02-18");

        expectAvailability().on("2012-02-18").vacancy(Vacancy.Vacant).scoping(Scoping.Scoped).readiness(RentReadiness.RentReady).rented()
                .rentStartsOn("2012-12-20").x();
    }

    @Test
    public void testEndLeaseNotScoped() {
        Lease lease = createLease("2012-02-20", "2013-03-01");
        setup().fromTheBeginning().to("2012-02-17").status(Status.available).x();
        setup().from("2012-02-20").to("2013-03-01").status(Status.leased).withLease(lease).x();
        setup().from("2013-03-02").toTheEndOfTime().status(Status.vacant).x();

        computeAvailabilityOn("2013-02-01");

        expectAvailability().on("2013-03-01").vacancy(Vacancy.Notice).rentEndsOn("2012-03-02").scoping(Scoping.Unscoped).notrented().x();
        expectAvailability().on("2013-03-02").vacancy(Vacancy.Vacant).scoping(Scoping.Unscoped).notrented().x();
    }

    @Test
    public void testEndLeaseScopedAvailable() {
        Lease lease = createLease("2012-02-20", "2013-03-01");
        setup().fromTheBeginning().to("2012-02-17").status(Status.available).x();
        setup().from("2012-02-20").to("2013-03-01").status(Status.leased).withLease(lease).x();
        setup().from("2013-03-02").toTheEndOfTime().status(Status.available).x();

        computeAvailabilityOn("2013-02-01");

        expectAvailability().on("2013-02-01").vacancy(Vacancy.Notice).rentEndsOn("2012-03-02").scoping(Scoping.Scoped).readiness(RentReadiness.RentReady)
                .notrented().x();
        expectAvailability().on("2013-03-02").vacancy(Vacancy.Vacant).scoping(Scoping.Scoped).readiness(RentReadiness.RentReady).notrented().x();
    }

    @Test
    public void testEndLeaseScopedAvailableThenLeasedAgain() {

        Lease lease = createLease("2012-02-20", "2013-03-01");
        Lease lease2 = createLease("2012-04-01", "2014-04-01");
        setup().fromTheBeginning().to("2012-02-17").status(Status.available).x();
        setup().from("2012-02-20").to("2013-03-01").status(Status.leased).withLease(lease).x();
        setup().from("2013-03-02").to("2013-03-31").status(Status.reserved).withLease(lease2).x();
        setup().from("2013-04-01").toTheEndOfTime().status(Status.leased).withLease(lease2).x();

        computeAvailabilityOn("2013-02-01");

        expectAvailability().on("2013-02-01").vacancy(Vacancy.Notice).rentEndsOn("2012-03-02").scoping(Scoping.Scoped).readiness(RentReadiness.RentReady)
                .rented().rentStartsOn("2013-02-01").x();
        expectAvailability().on("2013-03-02").vacancy(Vacancy.Vacant).scoping(Scoping.Scoped).readiness(RentReadiness.RentReady).rented()
                .rentStartsOn("2013-02-01").x();
        expectAvailability().on("2013-04-01").occupied().x();
    }

    @Test
    public void testEndLeaseScopedRenovationNeeded() {
        Lease lease = createLease("2012-02-20", "2013-03-01");
        setup().fromTheBeginning().to("2012-02-17").status(Status.available).x();
        setup().from("2012-02-20").to("2013-03-01").status(Status.leased).withLease(lease).x();
        setup().from("2013-03-02").to("2013-03-10").status(Status.renovation).x();
        setup().from("2013-03-11").toTheEndOfTime().status(Status.available).x();

        computeAvailabilityOn("2013-02-01");

        expectAvailability().on("2013-02-01").vacancy(Vacancy.Notice).rentEndsOn("2012-03-02").scoping(Scoping.Scoped).readiness(RentReadiness.NeedsRepairs)
                .notrented().x();
        expectAvailability().on("2013-03-02").vacancy(Vacancy.Vacant).scoping(Scoping.Scoped).readiness(RentReadiness.RenoInProgress).notrented().x();
    }

    @Test
    public void testEndLeaseScopedRenovationNeededThenLeasedAgain() {
        Lease lease1 = createLease("2012-02-20", "2013-03-01");
        Lease lease2 = createLease("2012-03-21", "2015-03-01");
        setup().fromTheBeginning().to("2012-02-17").status(Status.available).x();
        setup().from("2012-02-20").to("2013-03-01").status(Status.leased).withLease(lease1).x();
        setup().from("2013-03-02").to("2013-03-10").status(Status.renovation).x();
        setup().from("2013-03-15").to("2013-03-20").status(Status.reserved).withLease(lease2).x();
        setup().from("2013-03-21").toTheEndOfTime().status(Status.leased).withLease(lease2).x();

        computeAvailabilityOn("2013-02-01");

        expectAvailability().on("2013-02-01").vacancy(Vacancy.Notice).rentEndsOn("2012-03-02").scoping(Scoping.Scoped).readiness(RentReadiness.NeedsRepairs)
                .rented().rentStartsOn("2013-03-21").x();
        expectAvailability().on("2013-03-02").vacancy(Vacancy.Vacant).scoping(Scoping.Scoped).readiness(RentReadiness.RenoInProgress).rented()
                .rentStartsOn("2013-03-21").x();
        expectAvailability().on("2013-03-15").vacancy(Vacancy.Vacant).scoping(Scoping.Scoped).readiness(RentReadiness.RentReady).rented()
                .rentStartsOn("2013-03-21").x();
        expectAvailability().on("2013-03-15").occupied();
    }

    @Test
    public void testEndLeaseScopedOffMarket() {
        Lease lease1 = createLease("2012-02-20", "2013-03-01");
        setup().fromTheBeginning().to("2012-02-17").status(Status.available).x();
        setup().from("2012-02-20").to("2013-03-01").status(Status.leased).withLease(lease1).x();
        setup().from("2013-03-02").toTheEndOfTime().status(Status.offMarket).withOffMarketType(OffMarketType.down).x();

        computeAvailabilityOn("2013-02-01");

        expectAvailability().on("2013-02-01").vacancy(Vacancy.Notice).rentEndsOn("2012-03-02").scoping(Scoping.Scoped).readiness(RentReadiness.RentReady)
                .offMarket().x();
        expectAvailability().on("2013-03-02").vacancy(Vacancy.Vacant).scoping(Scoping.Scoped).readiness(RentReadiness.RentReady).offMarket().x();
    }

    @Test
    public void testMakeVacant() {
        setup().from("2013-03-02").to("2013-03-15").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2013-03-16").toTheEndOfTime().status(Status.vacant).withOffMarketType(OffMarketType.down).x();

        computeAvailabilityOn("2013-03-14");
        expectAvailability().on("2013-03-14").vacancy(Vacancy.Vacant).scoping(Scoping.Unscoped).offMarket().x();
        expectAvailability().on("2013-03-16").vacancy(Vacancy.Vacant).scoping(Scoping.Unscoped).notrented().x();

    }
}
