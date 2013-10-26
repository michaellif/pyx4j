/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 2, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.welcomewizardmockup;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class Utils {

    public static BigDecimal[] asBigDecimals(Integer... integers) {
        BigDecimal[] bigDecimals = new BigDecimal[integers.length];
        for (int i = 0; i < integers.length; ++i) {
            bigDecimals[i] = new BigDecimal(new BigInteger(integers[i].toString()), new MathContext(2));
        }
        return bigDecimals;
    }

    public static String formatMoney(Integer value) {
        assert value >= 0;
        if (value == 0) {
            return "No";
        } else {
            StringBuilder formatted = new StringBuilder();
            formatted.append('$');
            String stringValue = String.valueOf(value);
            for (int i = 0; i < stringValue.length(); ++i) {
                if (i != 0 & ((stringValue.length() - i) % 3 == 0)) {
                    formatted.append(",");
                }
                formatted.append(stringValue.charAt(i));
            }
            return formatted.toString();
        }
    }

    public static String formatMoney(BigDecimal money) {
        assert money.compareTo(BigDecimal.ZERO) >= 0;
        if (money.compareTo(BigDecimal.ZERO) == 0) {
            return "No";
        } else {
            String[] stringValue = money.toPlainString().split("\\.");
            StringBuilder formatted = new StringBuilder();
            formatted.append("$");
            for (int a = 0; a < stringValue.length; ++a) {
                if (a == 1) {
                    formatted.append(".");
                }
                for (int i = 0; i < stringValue[a].length(); ++i) {
                    if (((stringValue[a].length() - i) % 3 == 0) & i != 0) {
                        formatted.append(",");
                    }
                    formatted.append(stringValue[a].charAt(i));
                }
            }
            return formatted.toString();
        }
    }
}
