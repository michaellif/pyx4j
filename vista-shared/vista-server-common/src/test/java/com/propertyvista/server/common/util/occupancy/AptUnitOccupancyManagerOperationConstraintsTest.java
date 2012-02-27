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
import org.junit.Ignore;
import org.junit.Test;

import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;

@Ignore
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
    public void testIsAvailableToVacantAvailableWhenAvailable() {
        setup().from("2010-01-01").to("2010-05-01").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2010-05-02").toTheEndOfTime().status(Status.available).x();

        now("2010-04-30");
        Assert.assertTrue(getUOM().isAvailableToVacantAvailable());

        now("2010-05-05");
        Assert.assertTrue(getUOM().isAvailableToVacantAvailable());
    }

    @Test
    public void testIsAvailableToVacantAvailableWhenNotAvailable() {
        setup().from("2010-01-01").to("2010-05-01").status(Status.offMarket).withOffMarketType(OffMarketType.down).x();
        setup().from("2010-05-02").toTheEndOfTime().status(Status.vacant).x();

        now("2010-04-30");
        Assert.assertFalse(getUOM().isAvailableToVacantAvailable());

        now("2010-05-05");
        Assert.assertFalse(getUOM().isAvailableToVacantAvailable());
    }

    @Test
    public void testIsMakeVacantAvailableWhenAvaialble() {
        Lease lease = createLease("2010-01-01", "2010-01-02");
        setup().from("2010-01-01").to("2010-01-02").status(Status.leased).withLease(lease).x();
        setup().from("2010-01-03").toTheEndOfTime().status(Status.offMarket).withOffMarketType(OffMarketType.down).x();

        now("2010-01-01");
        Assert.assertEquals(asDate("2010-01-03"), getUOM().isMakeVacantAvailable());

        now("2010-02-01");
        Assert.assertEquals(asDate("2010-02-01"), getUOM().isMakeVacantAvailable());
    }

    @Test
    public void testIsMakeVacantAvailableWhenNotAvailable() {
        Lease lease = createLease("2010-01-01", "2010-01-02");
        setup().from("2010-01-01").to("2010-01-02").status(Status.leased).withLease(lease).x();
        setup().from("2010-01-03").toTheEndOfTime().status(Status.available).x();

        now("2010-01-01");
        Assert.assertNull(getUOM().isMakeVacantAvailable());

        now("2010-02-01");
        Assert.assertNull(getUOM().isMakeVacantAvailable());
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
        Assert.assertFalse(getUOM().isEndLeaseAvailable());

        now("2010-01-15");
        Assert.assertFalse(getUOM().isEndLeaseAvailable());
    }

}
