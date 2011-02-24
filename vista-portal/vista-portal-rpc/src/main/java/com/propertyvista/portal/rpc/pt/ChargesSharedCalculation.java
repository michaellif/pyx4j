/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-20
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.pt;

import com.propertyvista.portal.domain.pt.Charge;
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.ChargeLineList;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.TenantChargeList;
import com.propertyvista.portal.domain.util.DomainUtil;

public class ChargesSharedCalculation {

    public static void calculateCharges(Charges charges) {
        calculateTotal(charges.rentCharges());
        calculateSelectableTotal(charges.upgradeCharges());
        calculateProrateCharges(charges);
        calculateApplicationCharges(charges);
        calculatePaymentSplitCharges(charges);

    }

    public static void calculateProrateCharges(Charges charges) {
        calculateTotal(charges.proRatedCharges());
    }

    public static void calculateApplicationCharges(Charges charges) {
        calculateTotal(charges.applicationCharges());
    }

    public static void calculatePaymentSplitCharges(Charges charges) {
        calculateTotal(charges.paymentSplitCharges());
    }

    public static void calculateSelectableTotal(ChargeLineList charges) {
        double total = 0d;
        for (ChargeLine charge : charges.charges()) {
            if (charge.selected().isBooleanTrue()) {
                total += charge.charge().amount().getValue();
            }
        }
        charges.total().set(DomainUtil.createMoney(total));
    }

    public static void calculateTotal(ChargeLineList charges) {
        double total = 0d;
        for (Charge charge : charges.charges()) {
            total += charge.charge().amount().getValue();
        }
        charges.total().set(DomainUtil.createMoney(total));
    }

    public static void calculateTotal(TenantChargeList charges) {
        double total = 0d;
        for (Charge charge : charges.charges()) {
            total += charge.charge().amount().getValue();
        }
        charges.total().set(DomainUtil.createMoney(total));
    }

}
