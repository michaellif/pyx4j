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
import java.util.Collection;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.InvoiceDebit.DebitType;

public class ARArrearsManagerHelper {

    public static AgingBuckets addInPlace(AgingBuckets buckets1, AgingBuckets buckets2) {
        buckets1.bucketThisMonth().setValue(buckets1.bucketThisMonth().getValue().add(buckets2.bucketThisMonth().getValue()));
        buckets1.bucketCurrent().setValue(buckets1.bucketCurrent().getValue().add(buckets2.bucketCurrent().getValue()));
        buckets1.bucket30().setValue(buckets1.bucket30().getValue().add(buckets2.bucket30().getValue()));
        buckets1.bucket60().setValue(buckets1.bucket60().getValue().add(buckets2.bucket60().getValue()));
        buckets1.bucket90().setValue(buckets1.bucket90().getValue().add(buckets2.bucket90().getValue()));
        buckets1.bucketOver90().setValue(buckets1.bucketOver90().getValue().add(buckets2.bucketOver90().getValue()));

        buckets1.arrearsAmount().setValue(buckets1.arrearsAmount().getValue().add(buckets2.arrearsAmount().getValue()));
        buckets1.creditAmount().setValue(buckets1.creditAmount().getValue().add(buckets2.creditAmount().getValue()));
        buckets1.totalBalance().setValue(buckets1.totalBalance().getValue().add(buckets2.totalBalance().getValue()));

        return buckets1;
    }

    public static AgingBuckets addInPlace(AgingBuckets accumulator, Collection<AgingBuckets> agingBucketsCollection) {
        for (AgingBuckets typedBuckets : agingBucketsCollection) {
            addInPlace(accumulator, typedBuckets);
        }
        return accumulator;
    }

    public static AgingBuckets createAgingBuckets(DebitType debitType) {
        AgingBuckets agingBuckets = EntityFactory.create(AgingBuckets.class);
        agingBuckets.bucketThisMonth().setValue(new BigDecimal("0.00"));
        agingBuckets.bucketCurrent().setValue(new BigDecimal("0.00"));
        agingBuckets.bucket30().setValue(new BigDecimal("0.00"));
        agingBuckets.bucket60().setValue(new BigDecimal("0.00"));
        agingBuckets.bucket90().setValue(new BigDecimal("0.00"));
        agingBuckets.bucketOver90().setValue(new BigDecimal("0.00"));
        agingBuckets.arrearsAmount().setValue(new BigDecimal("0.00"));
        agingBuckets.creditAmount().setValue(new BigDecimal("0.00"));
        agingBuckets.totalBalance().setValue(new BigDecimal("0.00"));
        agingBuckets.debitType().setValue(debitType);
        return agingBuckets;
    }

}
