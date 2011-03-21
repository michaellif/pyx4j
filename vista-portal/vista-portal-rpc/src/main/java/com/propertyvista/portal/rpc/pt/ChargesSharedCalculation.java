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

import java.util.Date;

import com.propertyvista.portal.domain.pt.Charge;
import com.propertyvista.portal.domain.pt.ChargeLine;
import com.propertyvista.portal.domain.pt.ChargeLine.ChargeType;
import com.propertyvista.portal.domain.pt.ChargeLineList;
import com.propertyvista.portal.domain.pt.ChargeLineSelectable;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.domain.pt.Pet;
import com.propertyvista.portal.domain.pt.PetChargeRule;
import com.propertyvista.portal.domain.pt.TenantCharge;
import com.propertyvista.portal.domain.pt.TenantChargeList;
import com.propertyvista.portal.domain.util.DomainUtil;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;

public class ChargesSharedCalculation {

    public static void calculateCharges(Charges charges) {
        calculateMonthlyAndProrateCharges(charges);
        calculateApplicationCharges(charges);
        calculatePaymentSplitCharges(charges);
    }

    public static void calculatePetCharges(PetChargeRule petChargeRule, Pet pet) {
        if ((petChargeRule == null) || petChargeRule.chargeType().isNull()) {
            pet.chargeLine().set(null);
            return;
        }

        switch (petChargeRule.chargeType().getValue()) {
        case monthly:
            pet.chargeLine().label().setValue("Monthly");
            break;
        case deposit:
            pet.chargeLine().label().setValue("Deposit");
            break;
        case oneTime:
            pet.chargeLine().label().setValue("One time");
            break;
        }
        pet.chargeLine().charge().set(DomainUtil.createMoney(petChargeRule.value().getValue()));

    }

    @SuppressWarnings("deprecation")
    public static void calculateMonthlyAndProrateCharges(Charges charges) {

        // take all monthly charges and get their total
        double rentTotal = calculateTotal(charges.monthlyCharges());
        double upgradesTotal = calculateSelectableTotal(charges.monthlyCharges().upgradeCharges());
        double monthlyTotal = rentTotal + upgradesTotal;
        charges.monthlyCharges().total().set(DomainUtil.createMoney(monthlyTotal));

        // take the rentStart date
        Date rentStart = charges.rentStart().getValue();
        if (rentStart == null) {
            return;
        }

        // month end
        int currentDay = rentStart.getDate();
        int monthDays = TimeUtils.maxMonthDays(rentStart);
        int numDays = monthDays - currentDay;

        // build label
        StringBuilder sb = new StringBuilder();
        sb.append("Pro-Rate (");
        sb.append(TimeUtils.MONTH_NAMES_SHORT[rentStart.getMonth()]);
        sb.append(" ").append(currentDay).append(" - ");
        sb.append(TimeUtils.MONTH_NAMES_SHORT[rentStart.getMonth()]);
        sb.append(" ").append(monthDays).append(")");

        double proratedTotal = DomainUtil.roundMoney((monthlyTotal * numDays) / monthDays);

        ChargeLine proratedCharge = EntityFactory.create(ChargeLine.class);
        proratedCharge.type().setValue(ChargeType.prorated);
        proratedCharge.label().setValue(sb.toString());
        proratedCharge.charge().set(DomainUtil.createMoney(proratedTotal));

        charges.proRatedCharges().charges().clear();
        charges.proRatedCharges().charges().add(proratedCharge);
        charges.proRatedCharges().total().set(DomainUtil.createMoney(proratedTotal));
    }

    public static void calculateApplicationCharges(Charges charges) {
        calculateTotal(charges.applicationCharges());
    }

    /**
     * As % for other applicants are entered, the % for the main applicant is
     * proportionately decreased. Note the total amount includes all monthly payments
     * (rent and upgrades).
     */
    public static void calculatePaymentSplitCharges(Charges charges) {
        double totalSplit = 0d; // everything paid by co-applicants
        double total = charges.monthlyCharges().total().amount().getValue();
        int splitPrc = 0;
        TenantCharge applicantCharge = null;
        for (TenantCharge charge : charges.paymentSplitCharges().charges()) {
            // !N.B. charge.tenant().status()  is not available here.  charge.tenant() never loaded to GWT!
            // Consider the first is Applicant
            if (applicantCharge == null) {
                applicantCharge = charge;
            } else {
                splitPrc += charge.percentage().getValue();
                double v = DomainUtil.roundMoney(total * charge.percentage().getValue() / 100d);
                charge.charge().amount().setValue(v);
                totalSplit += v; // there may be multiple co-applicants
            }
        }
        if (applicantCharge != null) {
            double v = total - totalSplit; // applicant's share of payment
            applicantCharge.charge().amount().setValue(v);
            applicantCharge.percentage().setValue(100 - splitPrc);
        }
        calculateTotal(charges.paymentSplitCharges());
    }

    public static double calculateSelectableTotal(IList<ChargeLineSelectable> charges) {
        double total = 0d;
        for (ChargeLineSelectable charge : charges) {
            if (charge.selected().isBooleanTrue()) {
                total += charge.charge().amount().getValue();
            }
        }
        return total;
    }

    public static double calculateTotal(ChargeLineList charges) {
        double total = 0d;
        for (Charge charge : charges.charges()) {
            total += charge.charge().amount().getValue();
        }
        charges.total().set(DomainUtil.createMoney(total));
        return total;
    }

    public static void calculateTotal(TenantChargeList charges) {
        double total = 0d;
        for (Charge charge : charges.charges()) {
            total += charge.charge().amount().getValue();
        }
        charges.total().set(DomainUtil.createMoney(total));
    }

}
