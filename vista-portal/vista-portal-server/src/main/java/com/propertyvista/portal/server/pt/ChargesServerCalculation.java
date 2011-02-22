/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 20, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.portal.server.pt;

import com.propertyvista.portal.domain.pt.Charge;
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.ChargeLine.ChargeType;
import com.propertyvista.portal.domain.pt.ChargeLineList;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.TenantCharge;
import com.propertyvista.portal.domain.pt.TenantChargeList;
import com.propertyvista.portal.domain.util.DomainUtil;

import com.pyx4j.entity.shared.IList;

public class ChargesServerCalculation {

    public static void dummyPopulate(Charges charges, IList<PotentialTenantInfo> tenants) {

        // rent charges
        charges.rentCharges().charges().add(DomainUtil.createChargeLine(ChargeType.rent, 1500));
        charges.rentCharges().charges().add(DomainUtil.createChargeLine(ChargeType.parking, 100));
        charges.rentCharges().charges().add(DomainUtil.createChargeLine(ChargeType.locker, 25));
        charges.rentCharges().charges().add(DomainUtil.createChargeLine(ChargeType.petCharge, 75));

        // available upgrades
        charges.upgradeCharges().charges().add(DomainUtil.createChargeLine(ChargeType.parking2, 100, true));
        charges.upgradeCharges().charges().add(DomainUtil.createChargeLine(ChargeType.locker, 50, true));

        // pro rated charges
        ChargeLine chargeLine = DomainUtil.createChargeLine(ChargeType.prorated, 350);
        chargeLine.label().setValue("Pro-Rate (May 20 - May 31)");
        charges.proRatedCharges().charges().add(chargeLine);

        // application charges
        charges.applicationCharges().charges().add(DomainUtil.createChargeLine(ChargeType.deposit, 1500));
        charges.applicationCharges().charges().add(DomainUtil.createChargeLine(ChargeType.petDeposit, 100));
        charges.applicationCharges().charges().add(DomainUtil.createChargeLine(ChargeType.applicationFee, 29));

        // payment splits
        for (int i = 0; i < tenants.size(); i++) {
            int percentage = i == 0 ? 100 : 0;
            TenantCharge tenantCharge = DomainUtil.createTenantCharge(percentage, 0);
            tenantCharge.tenant().set(tenants.get(i));
            charges.paymentSplitCharges().charges().add(tenantCharge);
        }
    }

    public static void calculateCharges(Charges charges) {
        calculate(charges.rentCharges());
        calculate(charges.upgradeCharges());
        calculate(charges.proRatedCharges());
        calculate(charges.applicationCharges());
        calculate(charges.paymentSplitCharges());
    }

    public static void calculate(ChargeLineList charges) {
        double total = 0d;
        for (Charge charge : charges.charges()) {
            total += charge.charge().amount().getValue();
        }
        charges.total().set(DomainUtil.createMoney(total));
    }

    public static void calculate(TenantChargeList charges) {
        double total = 0d;
        for (Charge charge : charges.charges()) {
            total += charge.charge().amount().getValue();
        }
        charges.total().set(DomainUtil.createMoney(total));
    }

    public static void calculateApplicationCharges(Charges charges) {
        //TODO
    }

    public static void calculatePaymentSplitCharges(Charges charges) {

    }
}
