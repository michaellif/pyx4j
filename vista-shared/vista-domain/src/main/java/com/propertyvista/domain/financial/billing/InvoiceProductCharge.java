package com.propertyvista.domain.financial.billing;

/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 20, 2012
 * @author michaellif
 * @version $Id$
 */

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.DiscriminatorValue;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;

/**
 * ProductCharge - total charge for a single item from product catalog for given billing period
 * 
 */
@DiscriminatorValue("ProductCharge")
public interface InvoiceProductCharge extends InvoiceCharge {

    enum ProductType {
        service, recurringFeature, oneTimeFeature
    }

    enum Period {
        previous, current, next
    }

    IPrimitive<Period> period();

    IPrimitive<ProductType> productType();

    IPrimitive<LogicalDate> fromDate();

    IPrimitive<LogicalDate> toDate();

    @Owned
    InvoiceChargeSubLineItem chargeSubLineItem();

    @Detached
    @Owned
    @OrderBy(InvoiceSubLineItem.OrderId.class)
    IList<InvoiceAdjustmentSubLineItem> adjustmentSubLineItems();

    @Detached
    @Owned
    @OrderBy(InvoiceSubLineItem.OrderId.class)
    IList<InvoiceConcessionSubLineItem> concessionSubLineItems();

}
