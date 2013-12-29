/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-03-13
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.shared.utils.EntityGraph;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.billing.AgingBuckets;
import com.propertyvista.domain.financial.billing.ArrearsSnapshot;

public class ARArreasManagerUtils {

    public static AgingBuckets<?> addInPlace(AgingBuckets<?> buckets1, AgingBuckets<?> buckets2) {
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

    public static <E extends AgingBuckets<?>> E addInPlace(E accumulator, Collection<? extends AgingBuckets<?>> agingBucketsCollection) {
        for (AgingBuckets<?> typedBuckets : agingBucketsCollection) {
            addInPlace(accumulator, typedBuckets);
        }
        return accumulator;
    }

    public static <E extends AgingBuckets<?>> E createAgingBuckets(Class<E> agingBucketsType, ARCode.Type debitType) {
        E agingBuckets = EntityFactory.create(agingBucketsType);
        return initAgingBuckets(agingBuckets, debitType);
    }

    public static <E extends AgingBuckets<?>> E initAgingBuckets(E agingBuckets, ARCode.Type debitType) {
        agingBuckets.arCode().setValue(debitType);

        BigDecimal zero = new BigDecimal("0.00");
        agingBuckets.bucketThisMonth().setValue(zero);
        agingBuckets.bucketCurrent().setValue(zero);
        agingBuckets.bucket30().setValue(zero);
        agingBuckets.bucket60().setValue(zero);
        agingBuckets.bucket90().setValue(zero);
        agingBuckets.bucketOver90().setValue(zero);
        agingBuckets.arrearsAmount().setValue(zero);
        agingBuckets.creditAmount().setValue(zero);
        agingBuckets.totalBalance().setValue(zero);

        return agingBuckets;
    }

    public static boolean haveDifferentBucketValues(ArrearsSnapshot<?> a, ArrearsSnapshot<?> b) {
        if (a.agingBuckets().size() != b.agingBuckets().size()) {
            return true;
        }

        Map<ARCode.Type, AgingBuckets<?>> bucketsMapOfA = new EnumMap<ARCode.Type, AgingBuckets<?>>(ARCode.Type.class);

        AgingBuckets<?> totalAgingBucketsOfA = null;
        for (AgingBuckets<?> buckets : a.agingBuckets()) {
            if (buckets.arCode().isNull()) {
                totalAgingBucketsOfA = buckets;
            } else {
                bucketsMapOfA.put(buckets.arCode().getValue(), buckets);
            }
        }

        // WARNING: in this comparison there's an assumption that total buckets (buckets that thave arType == null) are always present)
        for (AgingBuckets<?> agingBucketsOfB : b.agingBuckets()) {
            if (agingBucketsOfB.arCode().isNull()) {
                if (!EntityGraph.fullyEqualValues(totalAgingBucketsOfA, agingBucketsOfB, agingBucketsOfB.arrearsSnapshot())) {
                    return true;
                }
            } else {
                AgingBuckets<?> agingBucketsOfA = bucketsMapOfA.get(agingBucketsOfB.arCode().getValue());
                if (agingBucketsOfA == null || !EntityGraph.fullyEqualValues(agingBucketsOfA, agingBucketsOfB, agingBucketsOfB.arrearsSnapshot())) {
                    return true;
                }
            }
        }

        return false;
    }

}
