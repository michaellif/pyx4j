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

import com.propertyvista.domain.financial.offering.ChargeItem;
import com.propertyvista.domain.financial.offering.ChargeItemAdjustment;

public class PriceCalculationHelpers {

    public static Double calculateChargeItemAdjustments(ChargeItem item) {
        Double adjustedPrice = item.price().getValue();

        for (ChargeItemAdjustment adjustment : item.adjustments()) {
            Double calculated = calculateChargeItemAdjustments(adjustedPrice, adjustment);
            if (!calculated.isNaN()) {
                adjustedPrice = calculated;
            }
        }

        // update Value:
        item.adjustedPrice().setValue(adjustedPrice);
        return adjustedPrice;
    }

    public static Double calculateChargeItemAdjustments(Double startPrice, ChargeItemAdjustment adjustment) {
        // preconditions:
        if (adjustment.isNull() || adjustment.type().isNull() || adjustment.termType().isNull()) {
            return Double.NaN; // not fully filled adjustment!.. 
        }

        // Calculate adjustments:
        Double adjustedPrice = startPrice;
        if (adjustment.termType().getValue().equals(ChargeItemAdjustment.TermType.term)) {
            if (adjustment.type().getValue().equals(ChargeItemAdjustment.Type.free)) {
                adjustedPrice = 0.0;
            } else {
                if (adjustment.value().isNull()) {
                    return Double.NaN; // value is necessary on this stage!..
                }

                switch (adjustment.type().getValue()) {
                case monetary:
                    switch (adjustment.chargeType().getValue()) {
                    case discount:
                        adjustedPrice -= adjustment.value().getValue();
                        break;
                    case priceRaise:
                        adjustedPrice += adjustment.value().getValue();
                        break;
                    }
                    break;

                case percentage:
                    switch (adjustment.chargeType().getValue()) {
                    case discount:
                        adjustedPrice *= 1 - adjustment.value().getValue() / 100;
                        break;
                    case priceRaise:
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
