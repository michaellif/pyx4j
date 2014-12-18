/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-03-09
 * @author Vlad
 */
package com.propertyvista.domain.util;

import java.util.Date;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.TimeUtils;

public class ValidationUtils extends com.pyx4j.commons.ValidationUtils {
    /**
     * Generic Luhn algorithm implementation (see
     * http://en.wikipedia.org/wiki/Luhn_algorithm for details) could be useful for other
     * ID verification like CreditCard #, etc.
     */
    public static boolean isLuhnValid(String num) {
        final int[][] sumTable = { { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, { 0, 2, 4, 6, 8, 1, 3, 5, 7, 9 } };
        int sum = 0, flip = 0;

        for (int i = num.length() - 1; i >= 0; i--) {
            sum += sumTable[flip++ & 0x1][Character.digit(num.charAt(i), 10)];
        }
        return (sum % 10 == 0);
    }

    public static boolean isOlderThen18(final Date bithday) {
        return TimeUtils.isOlderThan(bithday, 18);
    }

    public static boolean isSinValid(String sin) {
        if (CommonsStringUtils.isStringSet(sin)) {
            return sin.trim().replaceAll(" ", "").matches("^\\d{3}[\\s]*\\d{3}[\\s]*\\d{3}$") && isLuhnValid(sin.trim().replaceAll("\\s", ""));
        }
        return false;
    }

    public static boolean isCreditCardNumberValid(String ccNumber) {
        if (CommonsStringUtils.isStringSet(ccNumber)) {
            ccNumber = ccNumber.trim().replaceAll("\\s", "");
            return ccNumber.matches("^\\d{13,19}$") && isLuhnValid(ccNumber);
        }
        return false;
    }

    public static boolean isCreditCardNumberIinValid(String[] iinPatterns, String ccNumber) {
        if (CommonsStringUtils.isStringSet(ccNumber) && iinPatterns != null) {
            ccNumber = ccNumber.trim().replaceAll("\\s", "");
            for (String pattern : iinPatterns) {
                int prefixStart;
                int prefixEnd;
                if (pattern.contains("-")) {
                    String[] startAndEnd = pattern.split("-");
                    prefixStart = Integer.parseInt(startAndEnd[0]);
                    prefixEnd = Integer.parseInt(startAndEnd[1]);
                } else {
                    prefixStart = Integer.parseInt(pattern);
                    prefixEnd = prefixStart;
                }

                for (int prefix = prefixStart; prefix <= prefixEnd; ++prefix) {
                    if (ccNumber.startsWith(String.valueOf(prefix))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isCreditCardCodeValid(String num) {
        num = num.trim().replaceAll("\\s", "");
        return num.matches("^\\d{3,4}$");
    }

    public static boolean isVistaAccountNumberValid(String num) {
        num = num.trim().replaceAll("\\s", "");
        return num.matches("^\\d{14}$") && isLuhnValid(num);
    }

    // cheque/e-cheque validation:
    public static boolean isAccountNumberValid(String num) {
        num = num.trim().replaceAll("\\s", "");
        return num.matches("^\\d{1,12}$");
    }

    public static boolean isBranchTransitNumberValid(String num) {
        num = num.trim().replaceAll("\\s", "");
        return num.matches("^\\d{5}$");
    }

    public static boolean isBankIdNumberValid(String num) {
        num = num.trim().replaceAll("\\s", "");
        return num.matches("^\\d{3}$");
    }
}
