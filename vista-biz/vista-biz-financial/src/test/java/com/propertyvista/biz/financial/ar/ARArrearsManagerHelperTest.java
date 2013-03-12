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
package com.propertyvista.biz.financial.ar;

import java.math.BigDecimal;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.billing.AgingBuckets;

public class ARArrearsManagerHelperTest extends ARArrearsManagerHelperTestBase {

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

        ARAbstractArrearsManager.addInPlace(buckets1, buckets2);
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

    public void testCalculateAgingBuckets() {
    }

}
