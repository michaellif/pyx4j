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
        BigDecimal adjustedPrice = item.originalPrice().getValue();
        for (BillableItemAdjustment adjustment : item.adjustments()) {
            adjustedPrice = calculateChargeItemAdjustments(adjustedPrice, adjustment);
        }
        return adjustedPrice;
    }

    public static BigDecimal calculateChargeItemAdjustments(BigDecimal startPrice, BillableItemAdjustment adjustment) {
        // preconditions:
        if (adjustment.isNull() || adjustment.adjustmentType().isNull() || adjustment.termType().isNull()) {
            return startPrice; // not fully filled adjustment!.. 
        }

        // Calculate adjustments:
        BigDecimal adjustedPrice = startPrice;
        if (adjustment.termType().getValue().equals(BillableItemAdjustment.TermType.term)) {
            if (adjustment.adjustmentType().getValue().equals(BillableItemAdjustment.AdjustmentType.free)) {
                adjustedPrice = new BigDecimal(0);
            } else {
                if (adjustment.value().isNull()) {
                    return startPrice; // value is necessary on this stage!..
                }

                switch (adjustment.adjustmentType().getValue()) {
                case monetary:
                    switch (adjustment.chargeType().getValue()) {
                    case discount:
                        adjustedPrice = adjustedPrice.subtract(adjustment.value().getValue());
                        break;
                    case priceCorrection:
                        adjustedPrice = adjustedPrice.add(adjustment.value().getValue());
                        break;
                    }
                    break;

                case percentage:
                    switch (adjustment.chargeType().getValue()) {
                    //TODO we need sign with the amount - ChargeType is pure description
                    case negotiation:
                    case discount:
                        adjustedPrice = adjustedPrice.multiply(new BigDecimal(1).subtract(adjustment.value().getValue()).divide(new BigDecimal(100)));
                        break;
                    case priceCorrection:
                        adjustedPrice = adjustedPrice.multiply(new BigDecimal(1).add(adjustment.value().getValue()).divide(new BigDecimal(100)));
                        break;
                    }
                    break;
                }
            }
        }

        return adjustedPrice;
    }
}
