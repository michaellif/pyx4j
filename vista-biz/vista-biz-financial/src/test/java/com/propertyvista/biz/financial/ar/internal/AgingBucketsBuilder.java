/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-17
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.financial.ar.internal;

import java.math.BigDecimal;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.financial.ARCode;
import com.propertyvista.domain.financial.billing.AgingBuckets;

public class AgingBucketsBuilder<A extends AgingBuckets<?>> {

    private final A buckets;

    public AgingBucketsBuilder(Class<A> klass) {
        buckets = EntityFactory.create(klass);
    }

    public AgingBucketsBuilder<A> arCode(ARCode.Type arCodeType) {
        buckets.arCode().setValue(arCodeType);
        return this;
    }

    public AgingBucketsBuilder<A> m(String amount) {
        buckets.bucketThisMonth().setValue(new BigDecimal(amount));
        return this;
    }

    public AgingBucketsBuilder<A> b30(String amount) {
        buckets.bucket30().setValue(new BigDecimal(amount));
        return this;
    }

    public AgingBucketsBuilder<A> b60(String amount) {
        buckets.bucket60().setValue(new BigDecimal(amount));
        return this;
    }

    public AgingBucketsBuilder<A> b90(String amount) {
        buckets.bucket90().setValue(new BigDecimal(amount));
        return this;
    }

    public AgingBucketsBuilder<A> o90(String amount) {
        buckets.bucketOver90().setValue(new BigDecimal(amount));
        return this;
    }

    public AgingBucketsBuilder<A> t(String amount) {
        buckets.bucketOver90().setValue(new BigDecimal(amount));
        return this;
    }

    public A build() {
        return buckets;
    }

}
