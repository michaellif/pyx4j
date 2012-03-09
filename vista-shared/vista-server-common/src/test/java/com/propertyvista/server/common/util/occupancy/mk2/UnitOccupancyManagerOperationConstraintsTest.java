/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 27, 2012
 * @author Artyom
 * @version $Id$
 */
package com.propertyvista.server.common.util.occupancy.mk2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.ApproveLeaseConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.CancelEndLeaseConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.CancelReservationConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.EndLeaseConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.MakeVacantConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.ReserveConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.ScopeAvailableConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.ScopeOffMarketConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.ScopeRenovationConstraintsDTO;

public class UnitOccupancyManagerOperationConstraintsTest extends UnitOccupancyManagerTestBase {

    @Test
    public void testGetScopeAvailableConstraintsWhenAvailable() {
        s().from("2010-01-01").to("2010-05-01").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2010-05-02").toTheEndOfTime().status(Status.vacant).x();

        ScopeAvailableConstraintsDTO constraints = om().getScopeAvailableConstrants(unitId());

        assertEquals(asDate("2010-05-02"), constraints.minAvaiableFrom().getValue());
    }

    @Test
    public void testGetScopeAvailableConstraintsWhenNotAvailable() {
        s().from("2010-01-01").to("2010-05-01").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2010-05-02").toTheEndOfTime().status(Status.available).x();

        ScopeAvailableConstraintsDTO constraints = om().getScopeAvailableConstrants(unitId());

        assertTrue(constraints.minAvaiableFrom().isNull());
    }

    @Test
    public void testGetScopeOffMarketConstraintsWhenAvailable() {
        s().from("2010-01-01").to("2010-05-01").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2010-05-02").toTheEndOfTime().status(Status.vacant).x();

        ScopeOffMarketConstraintsDTO constraints = om().getScopeOffMarketConstrants(unitId());

        assertEquals(asDate("2010-05-02"), constraints.minOffMarketFrom().getValue());
    }

    @Test
    public void testGetScopeOffMarketConstraintsWhenNotAvailable() {
        s().from("2010-01-01").to("2010-05-01").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2010-05-02").toTheEndOfTime().status(Status.available).x();

        ScopeOffMarketConstraintsDTO constraints = om().getScopeOffMarketConstrants(unitId());

        assertTrue(constraints.minOffMarketFrom().isNull());
    }

    @Test
    public void testGetScopeRenovationWhenAvailable() {
        s().from("2010-01-01").to("2010-05-01").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2010-05-02").toTheEndOfTime().status(Status.vacant).x();

        ScopeRenovationConstraintsDTO constraints = om().getScopeRenovationConstraints(unitId());

        assertEquals(asDate("2010-05-02"), constraints.minRenovationStart().getValue());
        assertEquals(asDate("2010-05-02"), constraints.minRenovationEnd().getValue());
    }

    @Test
    public void testGetScopeRenovationWhenNotAvailable() {
        s().from("2010-01-01").to("2010-05-01").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2010-05-02").toTheEndOfTime().status(Status.available).x();

        ScopeRenovationConstraintsDTO constraints = om().getScopeRenovationConstraints(unitId());

        assertTrue(constraints.minRenovationStart().isNull());
        assertTrue(constraints.minRenovationEnd().isNull());
    }

    @Test
    public void testGetMakeVacantConstraintsWhenMaxIsLimitedByAvailable() {
        Lease lease = lease("2010-01-01", "2010-01-02");
        s().from("2010-01-01").to("2010-01-02").status(Status.leased).lease(lease).x();
        s().from("2010-01-03").to("2010-02-05").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2010-02-06").toTheEndOfTime().status(Status.available).x();

        MakeVacantConstraintsDTO constraints = om().getMakeVacantConstraints(unitId());

        assertEquals(asDate("2010-01-03"), constraints.minVacantFrom().getValue());
        assertEquals(asDate("2010-02-06"), constraints.maxVacantFrom().getValue());
    }

    @Test
    public void testgetMakeVacantConstraintsWhenMaxIsLimitedByVacant() {
        Lease lease = lease("2010-01-01", "2010-01-02");
        s().from("2010-01-01").to("2010-01-02").status(Status.leased).lease(lease).x();
        s().from("2010-01-03").to("2010-02-05").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2010-02-06").toTheEndOfTime().status(Status.vacant).x();

        MakeVacantConstraintsDTO constraints = om().getMakeVacantConstraints(unitId());

        assertEquals(asDate("2010-01-03"), constraints.minVacantFrom().getValue());
        assertEquals(asDate("2010-02-05"), constraints.maxVacantFrom().getValue());
    }

    @Test
    public void testgetMakeVacantConstraintsWhenMaxIsNotLimited() {
        Lease lease = lease("2010-01-01", "2010-01-02");
        s().from("2010-01-01").to("2010-01-02").status(Status.leased).lease(lease).x();
        s().from("2010-01-03").to("2010-02-05").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2010-02-06").toTheEndOfTime().status(Status.offMarket).offMarketType(OffMarketType.employee).x();

        MakeVacantConstraintsDTO constraints = om().getMakeVacantConstraints(unitId());
        assertEquals(asDate("2010-01-03"), constraints.minVacantFrom().getValue());
        assertTrue(constraints.maxVacantFrom().isNull());
    }

    @Test
    public void testGetMakeVacantConstraintsWhenNotAvailableBecauseLeased() {
        Lease lease = lease("2010-02-02", "2011-02-02");
        s().from("2010-01-03").to("2010-02-01").status(Status.available).x();
        s().from("2010-02-02").toTheEndOfTime().status(Status.leased).lease(lease).x();

        MakeVacantConstraintsDTO constraints = om().getMakeVacantConstraints(unitId());

        assertTrue(constraints.minVacantFrom().isNull());
        assertTrue(constraints.maxVacantFrom().isNull());

    }

    @Test
    public void testGetMakeVacantConstraintsWhenNotAvailableBecauseReserved() {
        Lease lease = lease("2010-02-02", "2011-02-02");
        s().from("2010-01-03").to("2010-02-01").status(Status.available).x();
        s().from("2010-02-02").toTheEndOfTime().status(Status.reserved).lease(lease).x();

        MakeVacantConstraintsDTO constraints = om().getMakeVacantConstraints(unitId());

        assertTrue(constraints.minVacantFrom().isNull());
        assertTrue(constraints.maxVacantFrom().isNull());
    }

    @Test
    public void testGetReserveConstraintsWhenAvailable() {
        s().from("2010-01-01").to("2010-01-10").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2010-01-11").toTheEndOfTime().status(Status.available).x();

        ReserveConstraintsDTO constraints = om().getReserveConstraints(unitId());

        assertEquals(asDate("2010-01-11"), constraints.minReserveFrom().getValue());
    }

    @Test
    public void testGetReserveConstraintsWhenNotAvailable() {
        s().from("2010-01-01").to("2010-01-10").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2010-01-11").toTheEndOfTime().status(Status.vacant).x();

        ReserveConstraintsDTO constraints = om().getReserveConstraints(unitId());

        assertTrue(constraints.minReserveFrom().isNull());

    }

    @Test
    public void testGetCancelReservationConstraintsWhenAvailable() {
        Lease lease = lease("2010-01-11", "2011-11-11");
        s().from("2010-01-01").to("2010-01-10").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2010-01-11").toTheEndOfTime().status(Status.reserved).lease(lease).x();

        CancelReservationConstraintsDTO constraints = om().getCancelReservationConstraints(unitId());

        assertEquals(asDate("2010-01-11"), constraints.minCancelFrom().getValue());
    }

    @Test
    public void testGetCancelReservationConstraintsWhenNotAvailable() {
        s().from("2010-01-01").to("2010-01-10").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2010-01-11").toTheEndOfTime().status(Status.available).x();

        CancelReservationConstraintsDTO constraints = om().getCancelReservationConstraints(unitId());

        assertTrue(constraints.minCancelFrom().isNull());
    }

    @Test
    public void testGetApproveLeaseConstraintsWhenAvailable() {
        Lease lease = lease("2010-01-11", "2011-11-11");
        s().from("2010-01-01").to("2010-01-10").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2010-01-11").toTheEndOfTime().status(Status.reserved).lease(lease).x();

        ApproveLeaseConstraintsDTO constraints = om().getApproveLeaseConstraints(unitId());

        assertEquals(asDate("2010-01-11"), constraints.minLeaseFrom().getValue());
    }

    @Test
    public void testGetApproveLeaseConstraintsWhenNotAvailable() {
        s().from("2010-01-01").to("2010-01-10").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2010-01-11").toTheEndOfTime().status(Status.available).x();

        ApproveLeaseConstraintsDTO constraints = om().getApproveLeaseConstraints(unitId());

        assertTrue(constraints.minLeaseFrom().isNull());
    }

    @Test
    public void testGetEndLeaseConstraints() {
        Lease lease = lease("2010-01-11", "2011-11-11");
        s().from("2010-01-01").to("2010-01-10").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2010-01-11").toTheEndOfTime().status(Status.leased).lease(lease).x();

        EndLeaseConstraintsDTO constraints = om().getEndLeaseConstraints(unitId());

        assertEquals(asDate("2010-01-11"), constraints.minLeaseEnd().getValue());
    }

    @Test
    public void testCancelEndLeaseConstraintsWhenAvaialble() {
        Lease lease = lease("2010-01-11", "2011-11-11");
        s().from("2010-01-01").to("2010-01-10").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2010-01-11").to("2011-11-11").status(Status.leased).lease(lease).x();
        s().from("2011-11-12").toTheEndOfTime().status(Status.available).x();

        CancelEndLeaseConstraintsDTO constraints = om().getCancelEndLeaseConstraints(unitId());

        assertTrue(constraints.isCancellable().isBooleanTrue());
    }

    @Test
    public void testCancelEndLeaseConstraintsWhenNotAvaialble() {
        Lease lease = lease("2010-01-11", "2011-11-11");
        Lease lease2 = lease("2011-01-12", "2012-01-01");

        s().from("2010-01-01").to("2010-01-10").status(Status.offMarket).offMarketType(OffMarketType.down).x();
        s().from("2010-01-11").to("2011-11-11").status(Status.leased).lease(lease).x();
        s().from("2011-01-12").toTheEndOfTime().status(Status.leased).lease(lease2).x();

        CancelEndLeaseConstraintsDTO constraints = om().getCancelEndLeaseConstraints(unitId());

        assertFalse(constraints.isCancellable().isBooleanTrue());
    }

}
