/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 27, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.server.common.charges;

import java.math.BigDecimal;

import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;

public class PriceCalculationHelpers {

    public static BigDecimal calculateChargeItemAdjustments(BillableItem item) {
        BigDecimal adjustedPrice = item.item().price().getValue();
        for (BillableItemAdjustment adjustment : item.adjustments()) {
            adjustedPrice = calculateBillableItemAdjustments(adjustedPrice, adjustment);
        }

        // update transient Value:
        item._currentPrice().setValue(adjustedPrice);

        return adjustedPrice;
    }

    public static BigDecimal calculateBillableItemAdjustments(BigDecimal startPrice, BillableItemAdjustment adjustment) {
        // preconditions:
        if (adjustment.isNull() || adjustment.adjustmentType().isNull() || adjustment.executionType().isNull()) {
            return startPrice; // not fully filled adjustment!.. 
        }

        // Calculate adjustments:
        BigDecimal adjustedPrice = startPrice;
        if (adjustment.executionType().getValue().equals(BillableItemAdjustment.ExecutionType.inLease)) {
            if (adjustment.value().isNull()) {
                return startPrice; // value is necessary on this stage!..
            }

            switch (adjustment.adjustmentType().getValue()) {
            case monetary:
                adjustedPrice = adjustedPrice.add(adjustment.value().getValue());
                break;

            case percentage:
                adjustedPrice = adjustedPrice.multiply(new BigDecimal(1).add(adjustment.value().getValue()).divide(new BigDecimal(100)));
                break;
            }
        }

        return adjustedPrice;
    }
}
