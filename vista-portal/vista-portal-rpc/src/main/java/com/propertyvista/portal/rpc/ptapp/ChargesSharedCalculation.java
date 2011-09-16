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
package com.propertyvista.portal.rpc.ptapp;

import java.util.Date;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;

import com.propertyvista.domain.Pet;
import com.propertyvista.domain.PetChargeRule;
import com.propertyvista.domain.charges.Charge;
import com.propertyvista.domain.charges.ChargeLine;
import com.propertyvista.domain.charges.ChargeLine.ChargeType;
import com.propertyvista.domain.charges.ChargeLineList;
import com.propertyvista.domain.charges.ChargeLineSelectable;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.TenantCharge;
import com.propertyvista.portal.domain.ptapp.TenantChargeList;

public class ChargesSharedCalculation {

    public static boolean calculateCharges(Charges charges) {
        return (calculateMonthlyAndProrateCharges(charges) && calculateApplicationCharges(charges) && calculatePaymentSplitCharges(charges));
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
    public static boolean calculateMonthlyAndProrateCharges(Charges charges) {

        // take all monthly charges and get their total
        double rentTotal = calculateTotal(charges.monthlyCharges());
        double upgradesTotal = calculateSelectableTotal(charges.monthlyCharges().upgradeCharges());
        double monthlyTotal = rentTotal + upgradesTotal;
        charges.monthlyCharges().total().set(DomainUtil.createMoney(monthlyTotal));

        // take the rentStart date
        Date rentStart = charges.rentStart().getValue();
        if (rentStart == null) {
            return true;
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
        return true;
    }

    public static boolean calculateApplicationCharges(Charges charges) {
        calculateTotal(charges.applicationCharges());
        return true;
    }

    /**
     * As % for other applicants are entered, the % for the main applicant is
     * proportionately decreased. Note the total amount includes all monthly payments
     * (rent and upgrades).
     */
    public static boolean calculatePaymentSplitCharges(Charges charges) {

        int totalSplitPrc = -1; // sum %, paid by co-applicants

        // check % correctness: 
        for (TenantCharge charge : charges.paymentSplitCharges().charges()) {
            if (totalSplitPrc == -1) { // first (main) applicant
                totalSplitPrc = 0; // reset first (main) applicant flag
            } else {
                totalSplitPrc += charge.percentage().getValue();
            }
        }

        if (totalSplitPrc > 100) {
            return false; // something incorrect here!..
        }

        totalSplitPrc = 0; // perform actual calculation:
        double totalSplitVal = 0d; // sum value, paid by co-applicants
        TenantCharge mainApplicantCharge = null;
        for (TenantCharge charge : charges.paymentSplitCharges().charges()) {
            // !N.B. charge.tenant().status()  is not available here.  charge.tenant() never loaded to GWT!
            // Consider the first is Applicant
            if (mainApplicantCharge == null) {
                mainApplicantCharge = charge;
            } else {
                charge.charge().amount()
                        .setValue(DomainUtil.roundMoney(charges.monthlyCharges().total().amount().getValue() * charge.percentage().getValue() / 100d));

                totalSplitPrc += charge.percentage().getValue();
                totalSplitVal += charge.charge().amount().getValue();
            }
        }

        if (mainApplicantCharge != null) {
            mainApplicantCharge.charge().amount().setValue(charges.monthlyCharges().total().amount().getValue() - totalSplitVal);
            mainApplicantCharge.percentage().setValue(100 - totalSplitPrc);
        }

        calculateTotal(charges.paymentSplitCharges());
        return true;
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
