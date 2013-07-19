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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.charges.ChargeLine;
import com.propertyvista.domain.charges.ChargeLine.ChargeType;
import com.propertyvista.domain.charges.ChargeLineList;
import com.propertyvista.domain.charges.Charge_OLD;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.portal.domain.ptapp.Charges;
import com.propertyvista.portal.domain.ptapp.TenantCharge;
import com.propertyvista.portal.domain.ptapp.TenantChargeList;

public class ChargesSharedCalculation {

    public static boolean calculateCharges(Charges charges) {
        return (calculateMonthlyAndProrateCharges(charges) && calculateOneTimeCharges(charges) && calculateApplicationCharges(charges) && calculatePaymentSplitCharges(charges));
    }

    public static boolean calculateMonthlyAndProrateCharges(Charges charges) {

        // take all monthly charges and get their total
        calculateTotal(charges.monthlyCharges());

        // take the rentStart date
        if (charges.rentStart().isNull()) {
            return true;
        }

        charges.proratedCharges().charges().clear();
        for (ChargeLine charge : charges.monthlyCharges().charges()) {
            charges.proratedCharges().charges().add(calculateProrateCharge(charges.rentStart().getValue(), charge));
        }

        calculateTotal(charges.proratedCharges());
        return true;
    }

    /**
     * Use TimeUtils.simpleFormat(, "MMM")
     */
    @Deprecated
    public static String[] MONTH_NAMES_SHORT = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    private static ChargeLine calculateProrateCharge(Date rentStart, ChargeLine charge) {
        BigDecimal monthly = charge.amount().getValue();

        // month end
        int currentDay = rentStart.getDate();
        int monthDays = TimeUtils.maxMonthDays(rentStart);
        int numDays = monthDays - currentDay;

        // build label
        StringBuilder sb = new StringBuilder();
        sb.append(charge.label().getStringView() + " (");
        sb.append(MONTH_NAMES_SHORT[rentStart.getMonth()]);
        sb.append(" ").append(currentDay).append(" - ");
        sb.append(MONTH_NAMES_SHORT[rentStart.getMonth()]);
        sb.append(" ").append(monthDays).append(")");

        BigDecimal propation = new BigDecimal(numDays).divide(new BigDecimal(monthDays), 2, RoundingMode.HALF_UP);
        BigDecimal proratedTotal = DomainUtil.roundMoney(monthly.multiply(propation));

        ChargeLine proratedCharge = EntityFactory.create(ChargeLine.class);
        proratedCharge.type().setValue(ChargeType.prorated);
        proratedCharge.label().setValue(sb.toString());
        proratedCharge.amount().setValue(proratedTotal);

        return proratedCharge;
    }

    public static boolean calculateOneTimeCharges(Charges charges) {
        calculateTotal(charges.oneTimeCharges());
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
        BigDecimal totalSplitPrc = BigDecimal.ZERO; // sum %, paid by co-applicants

        // check % correctness:
        for (TenantCharge charge : charges.paymentSplitCharges().charges()) {
            if (charge.tenant().role().getValue() == LeaseTermParticipant.Role.CoApplicant) {
            }
        }
        if (totalSplitPrc.compareTo(new BigDecimal(1)) > 0) {
            return false; // something incorrect here!..
        }

        totalSplitPrc = BigDecimal.ZERO; // perform actual calculation:
        BigDecimal totalSplitVal = BigDecimal.ZERO; // sum value, paid by co-applicants
        TenantCharge mainApplicantCharge = null;
        for (TenantCharge charge : charges.paymentSplitCharges().charges()) {
            switch (charge.tenant().role().getValue()) {
            case Applicant:
                mainApplicantCharge = charge;
                break;
            case CoApplicant:
                charge.amount().setValue(DomainUtil.roundMoney(charges.monthlyCharges().total().getValue()));

                totalSplitVal = totalSplitVal.add(charge.amount().getValue());
                break;
            default:
                break;
            }
        }

        if (mainApplicantCharge != null) {
            mainApplicantCharge.amount().setValue(charges.monthlyCharges().total().getValue().subtract(totalSplitVal));
        }

        calculateTotal(charges.paymentSplitCharges());
        return true;
    }

    public static void calculateTotal(ChargeLineList charges) {
        BigDecimal total = BigDecimal.ZERO;
        for (Charge_OLD charge : charges.charges()) {
            total = total.add(charge.amount().getValue());
        }
        charges.total().setValue(total);
    }

    public static void calculateTotal(TenantChargeList charges) {
        BigDecimal total = BigDecimal.ZERO;
        for (Charge_OLD charge : charges.charges()) {
            total = total.add(charge.amount().getValue());
        }
        charges.total().setValue(total);
    }

}
