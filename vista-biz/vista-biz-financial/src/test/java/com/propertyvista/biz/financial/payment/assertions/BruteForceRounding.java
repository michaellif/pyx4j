/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 18, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment.assertions;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BruteForceRounding {

    enum FeeType {

        Percentage,

        Amount
    }

    public static void main(String[] args) {
        int maxD = 10000;
        int max_prc = 99;

        FeeType feeType;
        feeType = FeeType.Percentage;
        //feeType = FeeType.Amount;

        final BigDecimal nundred = new BigDecimal("100");

        for (int i = 0; i <= maxD; i++) {
            for (int c = 1; c <= 100; c++) {
                for (int p = 1; p <= max_prc; p++) {
                    for (int pc = 0; pc <= 99; pc++) {
                        BigDecimal amount = new BigDecimal(String.valueOf(i) + "." + String.valueOf(c));
                        BigDecimal percent = new BigDecimal(String.valueOf(p) + "." + String.valueOf(pc));
                        BigDecimal fee;
                        // Caledon fee calculations
                        switch (feeType) {
                        case Amount:
                            fee = percent;
                            break;
                        case Percentage:
                            fee = amount.multiply(percent).divide(nundred, 2, RoundingMode.HALF_UP);
                            break;
                        default:
                            throw new IllegalArgumentException();
                        }

                        // Our calculations
                        BigDecimal feePercentage = fee.divide(amount, 4, RoundingMode.HALF_UP).multiply(nundred);

                        BigDecimal feeCalculated = amount.multiply(feePercentage).divide(nundred, 2, RoundingMode.HALF_UP);

                        if (feeCalculated.compareTo(fee) != 0) {
                            System.out.println(" amount: " + amount + "$  fee:" + fee + "$  " + percent + "%   != fee calculated " + feeCalculated + "$  "
                                    + feePercentage + "%");
                            return;
                        }
                    }
                }
            }
        }
        System.out.println("END");

    }
}
