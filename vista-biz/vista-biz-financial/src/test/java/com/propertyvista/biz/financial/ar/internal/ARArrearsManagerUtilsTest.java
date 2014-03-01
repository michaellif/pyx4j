/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-24
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar.internal;

import java.math.BigDecimal;
import java.util.Arrays;

import junit.framework.TestCase;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.biz.financial.ar.ARArreasManagerUtils;
import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.LeaseAgingBuckets;
import com.propertyvista.domain.financial.billing.LeaseArrearsSnapshot;

public class ARArrearsManagerUtilsTest extends TestCase {

    public void testAddInPlace() {
        AgingBuckets buckets1 = EntityFactory.create(AgingBuckets.class);
        buckets1.bucketThisMonth().setValue(new BigDecimal("1.00"));
        buckets1.bucketCurrent().setValue(new BigDecimal("10.00"));
        buckets1.bucket30().setValue(new BigDecimal("100.00"));
        buckets1.bucket60().setValue(new BigDecimal("1000.00"));
        buckets1.bucket90().setValue(new BigDecimal("10000.00"));
        buckets1.bucketOver90().setValue(new BigDecimal("100000.00"));
        buckets1.arrearsAmount().setValue(new BigDecimal("1000000.00"));
        buckets1.creditAmount().setValue(new BigDecimal("10000000.00"));
        buckets1.totalBalance().setValue(new BigDecimal("100000000.00"));

        AgingBuckets buckets2 = EntityFactory.create(AgingBuckets.class);
        buckets2.bucketThisMonth().setValue(new BigDecimal("2.00"));
        buckets2.bucketCurrent().setValue(new BigDecimal("20.00"));
        buckets2.bucket30().setValue(new BigDecimal("200.00"));
        buckets2.bucket60().setValue(new BigDecimal("2000.00"));
        buckets2.bucket90().setValue(new BigDecimal("20000.00"));
        buckets2.bucketOver90().setValue(new BigDecimal("200000.00"));
        buckets2.arrearsAmount().setValue(new BigDecimal("2000000.00"));
        buckets2.creditAmount().setValue(new BigDecimal("20000000.00"));
        buckets2.totalBalance().setValue(new BigDecimal("200000000.00"));

        ARArreasManagerUtils.addInPlace(buckets1, buckets2);
        assertEquals(buckets1.bucketThisMonth().getValue(), new BigDecimal("3.00"));
        assertEquals(buckets1.bucketCurrent().getValue(), new BigDecimal("30.00"));
        assertEquals(buckets1.bucket30().getValue(), new BigDecimal("300.00"));
        assertEquals(buckets1.bucket60().getValue(), new BigDecimal("3000.00"));
        assertEquals(buckets1.bucket90().getValue(), new BigDecimal("30000.00"));
        assertEquals(buckets1.bucketOver90().getValue(), new BigDecimal("300000.00"));
        assertEquals(buckets1.arrearsAmount().getValue(), new BigDecimal("3000000.00"));
        assertEquals(buckets1.creditAmount().getValue(), new BigDecimal("30000000.00"));
        assertEquals(buckets1.totalBalance().getValue(), new BigDecimal("300000000.00"));
    }

    public void testHaveDifference() {
        {
            LeaseArrearsSnapshot a1 = EntityFactory.create(LeaseArrearsSnapshot.class);
            a1.agingBuckets().addAll(Arrays.asList(//@formatter:off
                new LeaseAgingBucketsBuilder().arCode(null).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build()
            ));//@formatter:on
            LeaseArrearsSnapshot b1 = EntityFactory.create(LeaseArrearsSnapshot.class);
            b1.agingBuckets().addAll(Arrays.asList(//@formatter:off
                new LeaseAgingBucketsBuilder().arCode(null).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build()
            ));//@formatter:on
            assertFalse(ARArreasManagerUtils.haveDifferentBucketValues(a1, b1));
        }

        {
            LeaseArrearsSnapshot a2 = EntityFactory.create(LeaseArrearsSnapshot.class);
            a2.agingBuckets().addAll(Arrays.asList(//@formatter:off
                new LeaseAgingBucketsBuilder().arCode(null).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build()
            ));//@formatter:on
            LeaseArrearsSnapshot b2 = EntityFactory.create(LeaseArrearsSnapshot.class);
            b2.agingBuckets().addAll(Arrays.asList(//@formatter:off
                new LeaseAgingBucketsBuilder().arCode(null).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("2.00").build()
            ));//@formatter:on
            assertTrue(ARArreasManagerUtils.haveDifferentBucketValues(a2, b2));
        }

        {
            LeaseArrearsSnapshot a3 = EntityFactory.create(LeaseArrearsSnapshot.class);
            a3.agingBuckets().addAll(Arrays.asList(//@formatter:off
                new LeaseAgingBucketsBuilder().arCode(null).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build()
            ));//@formatter:on
            LeaseArrearsSnapshot b3 = EntityFactory.create(LeaseArrearsSnapshot.class);
            b3.agingBuckets().addAll(Arrays.asList(//@formatter:off
                new LeaseAgingBucketsBuilder().arCode(null).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build(),
                new LeaseAgingBucketsBuilder().arCode(ARCode.Type.AccountCharge).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build()
            ));//@formatter:on
            assertTrue(ARArreasManagerUtils.haveDifferentBucketValues(a3, b3));
        }

        {
            LeaseArrearsSnapshot a4 = EntityFactory.create(LeaseArrearsSnapshot.class);
            a4.agingBuckets().addAll(Arrays.asList(//@formatter:off
                    new LeaseAgingBucketsBuilder().arCode(null).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build(),
                    new LeaseAgingBucketsBuilder().arCode(ARCode.Type.AccountCharge).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build()
                    ));//@formatter:on
            LeaseArrearsSnapshot b4 = EntityFactory.create(LeaseArrearsSnapshot.class);
            b4.agingBuckets().addAll(Arrays.asList(//@formatter:off
                    new LeaseAgingBucketsBuilder().arCode(null).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build(),
                    new LeaseAgingBucketsBuilder().arCode(ARCode.Type.AccountCharge).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build()
                    ));//@formatter:on
            assertFalse(ARArreasManagerUtils.haveDifferentBucketValues(a4, b4));
        }

        {
            LeaseArrearsSnapshot a5 = EntityFactory.create(LeaseArrearsSnapshot.class);
            a5.billingAccount().setPrimaryKey(new Key(1));
            a5.agingBuckets().addAll(Arrays.asList(//@formatter:off
                    new LeaseAgingBucketsBuilder().arCode(null).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build(),
                    new LeaseAgingBucketsBuilder().arCode(ARCode.Type.AccountCharge).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build()
                    ));//@formatter:on
            LeaseArrearsSnapshot b5 = EntityFactory.create(LeaseArrearsSnapshot.class);
            b5.agingBuckets().addAll(Arrays.asList(//@formatter:off
                    new LeaseAgingBucketsBuilder().arCode(null).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build(),
                    new LeaseAgingBucketsBuilder().arCode(ARCode.Type.AccountCharge).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build()
                    ));//@formatter:on
            assertFalse(ARArreasManagerUtils.haveDifferentBucketValues(a5, b5));
        }

        {
            LeaseArrearsSnapshot a7 = EntityFactory.create(LeaseArrearsSnapshot.class);
            a7.agingBuckets().addAll(Arrays.asList(//@formatter:off
                    new LeaseAgingBucketsBuilder().arCode(null).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build(),
                    new LeaseAgingBucketsBuilder().arCode(ARCode.Type.AccountCharge).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build(),
                    new LeaseAgingBucketsBuilder().arCode(ARCode.Type.AddOn).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("5.00").build()
                    ));//@formatter:on
            a7.agingBuckets().get(0).arrearsSnapshot().setPrimaryKey(new Key(1));
            a7.agingBuckets().get(1).arrearsSnapshot().setPrimaryKey(new Key(1));
            a7.agingBuckets().get(2).arrearsSnapshot().setPrimaryKey(new Key(1));
            LeaseArrearsSnapshot b7 = EntityFactory.create(LeaseArrearsSnapshot.class);
            b7.agingBuckets().addAll(Arrays.asList(//@formatter:off
                    new LeaseAgingBucketsBuilder().arCode(null).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build(),
                    new LeaseAgingBucketsBuilder().arCode(ARCode.Type.AccountCharge).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build(),
                    new LeaseAgingBucketsBuilder().arCode(ARCode.Type.AddOn).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("5.00").build()
                    ));//@formatter:on
            b7.agingBuckets().get(0).arrearsSnapshot().setPrimaryKey(new Key(3));
            b7.agingBuckets().get(1).arrearsSnapshot().setPrimaryKey(new Key(3));
            b7.agingBuckets().get(2).arrearsSnapshot().setPrimaryKey(new Key(3));
            assertFalse(ARArreasManagerUtils.haveDifferentBucketValues(a7, b7));
        }

        {
            LeaseArrearsSnapshot a8 = EntityFactory.create(LeaseArrearsSnapshot.class);
            a8.agingBuckets().addAll(Arrays.asList(//@formatter:off
                    new LeaseAgingBucketsBuilder().arCode(null).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build(),
                    new LeaseAgingBucketsBuilder().arCode(ARCode.Type.AccountCharge).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build(),
                    new LeaseAgingBucketsBuilder().arCode(ARCode.Type.AddOn).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("5.00").build()
                    ));//@formatter:on
            a8.agingBuckets().get(0).arrearsSnapshot().setPrimaryKey(new Key(1));
            a8.agingBuckets().get(1).arrearsSnapshot().setPrimaryKey(new Key(1));
            a8.agingBuckets().get(2).arrearsSnapshot().setPrimaryKey(new Key(1));
            LeaseArrearsSnapshot b8 = EntityFactory.create(LeaseArrearsSnapshot.class);
            b8.agingBuckets().addAll(Arrays.asList(//@formatter:off
                    new LeaseAgingBucketsBuilder().arCode(null).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build(),
                    new LeaseAgingBucketsBuilder().arCode(ARCode.Type.DepositSecurity).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build(),
                    new LeaseAgingBucketsBuilder().arCode(ARCode.Type.AddOn).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("5.00").build()
                    ));//@formatter:on
            b8.agingBuckets().get(0).arrearsSnapshot().setPrimaryKey(new Key(3));
            b8.agingBuckets().get(1).arrearsSnapshot().setPrimaryKey(new Key(3));
            b8.agingBuckets().get(2).arrearsSnapshot().setPrimaryKey(new Key(3));
            assertTrue(ARArreasManagerUtils.haveDifferentBucketValues(a8, b8));
        }

        {
            LeaseArrearsSnapshot a6 = EntityFactory.create(LeaseArrearsSnapshot.class);
            a6.agingBuckets().addAll(Arrays.asList(//@formatter:off
                    new LeaseAgingBucketsBuilder().arCode(null).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build(),
                    new LeaseAgingBucketsBuilder().arCode(ARCode.Type.AccountCharge).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build(),
                    new LeaseAgingBucketsBuilder().arCode(ARCode.Type.AddOn).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("5.00").build()
                    ));//@formatter:on
            a6.agingBuckets().get(0).arrearsSnapshot().setPrimaryKey(new Key(1));
            a6.agingBuckets().get(1).arrearsSnapshot().setPrimaryKey(new Key(1));
            a6.agingBuckets().get(2).arrearsSnapshot().setPrimaryKey(new Key(1));
            LeaseArrearsSnapshot b6 = EntityFactory.create(LeaseArrearsSnapshot.class);
            b6.agingBuckets().addAll(Arrays.asList(//@formatter:off
                    new LeaseAgingBucketsBuilder().arCode(ARCode.Type.AccountCharge).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build(),                    
                    new LeaseAgingBucketsBuilder().arCode(ARCode.Type.AddOn).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("5.00").build(),
                    new LeaseAgingBucketsBuilder().arCode(null).m("1.00").b30("0.00").b60("0.00").b90("0.00").o90("0.00").t("1.00").build()
                    ));//@formatter:on
            b6.agingBuckets().get(0).arrearsSnapshot().setPrimaryKey(new Key(3));
            b6.agingBuckets().get(1).arrearsSnapshot().setPrimaryKey(new Key(3));
            b6.agingBuckets().get(2).arrearsSnapshot().setPrimaryKey(new Key(3));
            assertFalse(ARArreasManagerUtils.haveDifferentBucketValues(a6, b6));
        }

    }

    private static class LeaseAgingBucketsBuilder extends AgingBucketsBuilder<LeaseAgingBuckets> {

        public LeaseAgingBucketsBuilder() {
            super(LeaseAgingBuckets.class);
        }

    }
}
