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
package com.propertyvista.server.common.util.occupancy;

import org.junit.Assert;
import org.junit.Test;

import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.property.asset.unit.occupancy.opconstraints.MakeVacantConstraintsDTO;
import com.propertyvista.domain.tenant.lease.Lease;

public class AptUnitOccupancyManagerOperationConstraintsTest extends AptUnitOccupancyManagerTestBase {

    @Test
    public void testIsScopeOffMarketAvailableWhenAvaialble() {
        setup().from("2010-01-01").to("2010-05-01").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2010-05-02").toTheEndOfTime().status(Status.vacant).x();

        now("2010-04-30");
        Assert.assertTrue(getUOM().isScopeOffMarketAvailable());

        now("2010-05-05");
        Assert.assertTrue(getUOM().isScopeOffMarketAvailable());
    }

    @Test
    public void testIsScopeOffMarketAvailableWhenNotAvaialble() {
        setup().from("2010-01-01").to("2010-05-01").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2010-05-02").toTheEndOfTime().status(Status.available).x();

        now("2010-04-30");
        Assert.assertFalse(getUOM().isScopeOffMarketAvailable());

        now("2010-05-05");
        Assert.assertFalse(getUOM().isScopeOffMarketAvailable());
    }

    @Test
    public void testIsRenovationAvailableWhenAvailable() {
        setup().from("2010-01-01").to("2010-05-01").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2010-05-02").toTheEndOfTime().status(Status.vacant).x();

        now("2010-04-30");
        Assert.assertEquals(asDate("2010-05-02"), getUOM().isRenovationAvailable());

        now("2010-05-05");
        Assert.assertEquals(asDate("2010-05-05"), getUOM().isRenovationAvailable());
    }

    @Test
    public void testIsRenovationAvailableWhenNotAvailable() {
        setup().from("2010-01-01").to("2010-05-01").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2010-05-02").toTheEndOfTime().status(Status.available).x();

        now("2010-04-30");
        Assert.assertNull(getUOM().isRenovationAvailable());

        now("2010-05-05");
        Assert.assertNull(getUOM().isRenovationAvailable());
    }

    @Test
    public void testIsScopeAvailableAvailableWhenAvaialble() {
        setup().from("2010-01-01").to("2010-05-01").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2010-05-02").toTheEndOfTime().status(Status.vacant).x();

        now("2010-04-30");
        Assert.assertTrue(getUOM().isScopeAvailableAvailable());

        now("2010-05-05");
        Assert.assertTrue(getUOM().isScopeAvailableAvailable());
    }

    @Test
    public void testIsScopeAvailableAvailableWhenNotAvaialble() {
        setup().from("2010-01-01").to("2010-05-01").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2010-05-02").toTheEndOfTime().status(Status.available).x();

        now("2010-04-30");
        Assert.assertFalse(getUOM().isScopeAvailableAvailable());

        now("2010-05-05");
        Assert.assertFalse(getUOM().isScopeAvailableAvailable());
    }

    @Test
    public void testgetMakeVacantConstraintsWhenMaxIsLimitedByAvaialble() {
        Lease lease = createLease("2010-01-01", "2010-01-02");
        setup().from("2010-01-01").to("2010-01-02").status(Status.leased).withLease(lease).x();
        setup().from("2010-01-03").to("2010-02-05").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2010-02-06").toTheEndOfTime().status(Status.available).x();

        now("2010-01-01");
        MakeVacantConstraintsDTO constraints = getUOM().getMakeVacantConstraints();
        Assert.assertEquals(asDate("2010-01-03"), constraints.minVacantFrom().getValue());
        Assert.assertEquals(asDate("2010-02-06"), constraints.maxVacantFrom().getValue());

        now("2010-02-01");
        constraints = getUOM().getMakeVacantConstraints();
        Assert.assertEquals(asDate("2010-02-01"), constraints.minVacantFrom().getValue());
        Assert.assertEquals(asDate("2010-02-06"), constraints.maxVacantFrom().getValue());

        now("2010-02-06");
        constraints = getUOM().getMakeVacantConstraints();
        Assert.assertEquals(asDate("2010-02-06"), constraints.minVacantFrom().getValue());
        Assert.assertEquals(asDate("2010-02-06"), constraints.maxVacantFrom().getValue());

        now("2010-02-10");
        constraints = getUOM().getMakeVacantConstraints();
        Assert.assertEquals(asDate("2010-02-10"), constraints.minVacantFrom().getValue());
        Assert.assertEquals(asDate("2010-02-10"), constraints.maxVacantFrom().getValue());
    }

    @Test
    public void testgetMakeVacantConstraintsWhenMaxIsLimitedByVacant() {
        Lease lease = createLease("2010-01-01", "2010-01-02");
        setup().from("2010-01-01").to("2010-01-02").status(Status.leased).withLease(lease).x();
        setup().from("2010-01-03").to("2010-02-05").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2010-02-06").toTheEndOfTime().status(Status.vacant).x();

        now("2010-01-01");
        MakeVacantConstraintsDTO constraints = getUOM().getMakeVacantConstraints();
        Assert.assertEquals(asDate("2010-01-03"), constraints.minVacantFrom().getValue());
        Assert.assertEquals(asDate("2010-02-05"), constraints.maxVacantFrom().getValue());

        now("2010-02-01");
        constraints = getUOM().getMakeVacantConstraints();
        Assert.assertEquals(asDate("2010-02-01"), constraints.minVacantFrom().getValue());
        Assert.assertEquals(asDate("2010-02-05"), constraints.maxVacantFrom().getValue());

        now("2010-02-06");
        constraints = getUOM().getMakeVacantConstraints();
        Assert.assertNull(constraints);

        now("2010-02-10");
        constraints = getUOM().getMakeVacantConstraints();
        Assert.assertNull(constraints);
    }

    @Test
    public void testgetMakeVacantConstraintsWhenMaxIsNotLimited() {
        Lease lease = createLease("2010-01-01", "2010-01-02");
        setup().from("2010-01-01").to("2010-01-02").status(Status.leased).withLease(lease).x();
        setup().from("2010-01-03").to("2010-02-05").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2010-02-06").toTheEndOfTime().status(Status.offMarket).withOffMarketType(OffMarketType.employee).x();

        now("2010-01-01");
        MakeVacantConstraintsDTO constraints = getUOM().getMakeVacantConstraints();
        Assert.assertEquals(asDate("2010-01-03"), constraints.minVacantFrom().getValue());
        Assert.assertNull(constraints.maxVacantFrom().getValue());

        now("2010-02-01");
        constraints = getUOM().getMakeVacantConstraints();
        Assert.assertEquals(asDate("2010-02-01"), constraints.minVacantFrom().getValue());
        Assert.assertNull(constraints.maxVacantFrom().getValue());

        now("2010-02-06");
        constraints = getUOM().getMakeVacantConstraints();
        Assert.assertEquals(asDate("2010-02-06"), constraints.minVacantFrom().getValue());
        Assert.assertNull(constraints.maxVacantFrom().getValue());

        now("2010-02-10");
        constraints = getUOM().getMakeVacantConstraints();
        Assert.assertEquals(asDate("2010-02-10"), constraints.minVacantFrom().getValue());
        Assert.assertNull(constraints.maxVacantFrom().getValue());
    }

    @Test
    public void testIsMakeVacantAvailableWhenNotAvailableBecauseLeased() {
        Lease lease = createLease("2010-02-02", "2011-02-02");
        setup().from("2010-01-03").to("2010-02-01").status(Status.available).x();
        setup().from("2010-02-02").toTheEndOfTime().status(Status.leased).withLease(lease).x();

        now("2010-01-01");
        Assert.assertNull(getUOM().getMakeVacantConstraints());

        now("2010-01-02");
        Assert.assertNull(getUOM().getMakeVacantConstraints());

        now("2010-02-02");
        Assert.assertNull(getUOM().getMakeVacantConstraints());

        now("2010-02-04");
        Assert.assertNull(getUOM().getMakeVacantConstraints());
    }

    @Test
    public void testIsMakeVacantAvailableWhenNotAvailableBecauseReserved() {
        Lease lease = createLease("2010-02-02", "2011-02-02");
        setup().from("2010-01-03").to("2010-02-01").status(Status.available).x();
        setup().from("2010-02-02").toTheEndOfTime().status(Status.reserved).withLease(lease).x();

        now("2010-01-01");
        Assert.assertNull(getUOM().getMakeVacantConstraints());

        now("2010-01-02");
        Assert.assertNull(getUOM().getMakeVacantConstraints());

        now("2010-02-02");
        Assert.assertNull(getUOM().getMakeVacantConstraints());

        now("2010-02-04");
        Assert.assertNull(getUOM().getMakeVacantConstraints());
    }

    @Test
    public void testIsReserveAvailableWhenAvailable() {
        setup().from("2010-01-01").to("2010-01-10").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2010-01-11").toTheEndOfTime().status(Status.available).x();

        now("2010-01-01");
        Assert.assertEquals(asDate("2010-01-11"), getUOM().isReserveAvailable());

        now("2010-01-12");
        Assert.assertEquals(asDate("2010-01-12"), getUOM().isReserveAvailable());
    }

    @Test
    public void testIsReserveAvailableWhenNotAvailable() {
        setup().from("2010-01-01").to("2010-01-10").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2010-01-11").toTheEndOfTime().status(Status.vacant).x();

        now("2010-01-01");
        Assert.assertNull(getUOM().isReserveAvailable());

        now("2010-01-12");
        Assert.assertNull(getUOM().isReserveAvailable());

    }

    @Test
    public void testIsUnreserveAvailableWhenAvailable() {
        Lease lease = createLease("2010-01-11", "2011-11-11");
        setup().from("2010-01-01").to("2010-01-10").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2010-01-11").toTheEndOfTime().status(Status.reserved).withLease(lease).x();

        now("2010-01-01");
        Assert.assertTrue(getUOM().isUnreserveAvailable());

        now("2010-01-11");
        Assert.assertTrue(getUOM().isUnreserveAvailable());
    }

    @Test
    public void testIsUnreserveAvailableWhenNotAvailable() {
        setup().from("2010-01-01").to("2010-01-10").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2010-01-11").toTheEndOfTime().status(Status.available).x();

        now("2010-01-01");
        Assert.assertFalse(getUOM().isUnreserveAvailable());

        now("2010-01-11");
        Assert.assertFalse(getUOM().isUnreserveAvailable());
    }

    @Test
    public void testIsApproveLeaseAvailableWhenAvailable() {
        Lease lease = createLease("2010-01-11", "2011-11-11");
        setup().from("2010-01-01").to("2010-01-10").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2010-01-11").toTheEndOfTime().status(Status.reserved).withLease(lease).x();

        now("2010-01-01");
        Assert.assertTrue(getUOM().isApproveLeaseAvaialble());

        now("2010-01-11");
        Assert.assertTrue(getUOM().isApproveLeaseAvaialble());
    }

    @Test
    public void testIsApproveLeaseAvailableWhenNotAvailable() {
        setup().from("2010-01-01").to("2010-01-10").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2010-01-11").toTheEndOfTime().status(Status.available).x();

        now("2010-01-01");
        Assert.assertFalse(getUOM().isApproveLeaseAvaialble());

        now("2010-01-11");
        Assert.assertFalse(getUOM().isApproveLeaseAvaialble());
    }

    @Test
    public void testIsEndLeaseAvailable() {
        Lease lease = createLease("2010-01-11", "2011-11-11");
        setup().from("2010-01-01").to("2010-01-10").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2010-01-11").toTheEndOfTime().status(Status.leased).withLease(lease).x();

        now("2010-01-01");
        Assert.assertFalse(getUOM().isEndLeaseAvailable());

        now("2010-01-11");
        Assert.assertTrue(getUOM().isEndLeaseAvailable());

        now("2010-01-15");
        Assert.assertTrue(getUOM().isEndLeaseAvailable());
    }

    @Test
    public void testIsCancelEndLeaseAvaialbleWhenAvaialble() {
        Lease lease = createLease("2010-01-11", "2011-11-11");
        setup().from("2010-01-01").to("2010-01-10").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2010-01-11").to("2011-11-11").status(Status.leased).withLease(lease).x();
        setup().from("2010-01-12").toTheEndOfTime().status(Status.available).x();

        now("2010-01-12");
        Assert.assertTrue(getUOM().isCancelEndLeaseAvaialble());
    }

    @Test
    public void testIsCancelEndLeaseAvaialbleWhenNotAvaialble() {
        Lease lease = createLease("2010-01-11", "2011-11-11");
        Lease lease2 = createLease("2011-01-12", "2012-01-01");

        setup().from("2010-01-01").to("2010-01-10").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2010-01-11").to("2011-11-11").status(Status.leased).withLease(lease).x();
        setup().from("2011-01-12").toTheEndOfTime().status(Status.leased).withLease(lease2).x();

        now("2010-01-12");
        Assert.assertFalse(getUOM().isCancelEndLeaseAvaialble());
    }

}
