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
package com.propertyvista.domain.financial.offering;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Format;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.ToStringFormat;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

@ToStringFormat("{0}, Price: {1}")
public interface ChargeItem extends IEntity {

    @ToString(index = 0)
    @Caption(name = "Service Item")
    ServiceItem item();

    // base price: originally - comes from item
    @ToString(index = 1)
    @Format("#0.00")
    @Caption(name = "Base Price")
    IPrimitive<Double> price();

    /*
     * adjusted price: case price + adjustments (type of hole-term only!)
     * should be recalculated by service when necessary (use @link PriceCalculationHelpers.calculateChargeItemAdjustments(ChargeItem item))!..
     */
    @Transient
    @Format("#0.00")
    @Caption(name = "Price")
    IPrimitive<Double> adjustedPrice();

    @Owned
    IList<ChargeItemAdjustment> adjustments();
}
