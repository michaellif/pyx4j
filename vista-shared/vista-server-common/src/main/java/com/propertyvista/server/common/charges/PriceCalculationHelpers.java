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

import com.propertyvista.domain.tenant.lease.BillableItem;
import com.propertyvista.domain.tenant.lease.BillableItemAdjustment;

public class PriceCalculationHelpers {

    public static Double calculateChargeItemAdjustments(BillableItem item) {
        Double adjustedPrice = item.originalPrice().getValue();

        for (BillableItemAdjustment adjustment : item.adjustments()) {
            Double calculated = calculateChargeItemAdjustments(adjustedPrice, adjustment);
            if (!calculated.isNaN()) {
                adjustedPrice = calculated;
            }
        }

        return adjustedPrice;
    }

    public static Double calculateChargeItemAdjustments(Double startPrice, BillableItemAdjustment adjustment) {
        // preconditions:
        if (adjustment.isNull() || adjustment.adjustmentType().isNull() || adjustment.termType().isNull()) {
            return Double.NaN; // not fully filled adjustment!.. 
        }

        // Calculate adjustments:
        Double adjustedPrice = startPrice;
        if (adjustment.termType().getValue().equals(BillableItemAdjustment.TermType.term)) {
            if (adjustment.adjustmentType().getValue().equals(BillableItemAdjustment.AdjustmentType.free)) {
                adjustedPrice = 0.0;
            } else {
                if (adjustment.value().isNull()) {
                    return Double.NaN; // value is necessary on this stage!..
                }

                switch (adjustment.adjustmentType().getValue()) {
                case monetary:
                    switch (adjustment.chargeType().getValue()) {
                    case discount:
                        adjustedPrice -= adjustment.value().getValue();
                        break;
                    case priceCorrection:
                        adjustedPrice += adjustment.value().getValue();
                        break;
                    }
                    break;

                case percentage:
                    switch (adjustment.chargeType().getValue()) {
                    case discount:
                        adjustedPrice *= 1 - adjustment.value().getValue() / 100;
                        break;
                    case priceCorrection:
                        adjustedPrice *= 1 + adjustment.value().getValue() / 100;
                        break;
                    }
                    break;
                }
            }
        }

        return adjustedPrice;
    }
}
