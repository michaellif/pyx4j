/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-07-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.domain.tenant.lease;

import java.math.BigDecimal;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.OrderBy;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.financial.offering.ProductItem;

@ToStringFormat("{0}")
public interface BillableItem extends IEntity {

    @ToString(index = 0)
    @Caption(name = "Product Item")
    ProductItem item();

    @Owned
    @OrderBy(BillableItemAdjustment.OrderId.class)
    IList<BillableItemAdjustment> adjustments();

    BillableItemExtraData extraData();

    @Format("MM/dd/yyyy")
    IPrimitive<LogicalDate> effectiveDate();

    IPrimitive<Integer> billingPeriodNumber();

    /**
     * Current price: contractual price value (ProductItem.price + adjustments),
     * should be recalculated (@link PriceCalculationHelpers.calculateBillableItemAdjustments()) before use!..
     */
    @ToString(index = 1)
    @Format("#0.00")
    @Caption(name = "Price")
    IPrimitive<BigDecimal> _currentPrice();
}
