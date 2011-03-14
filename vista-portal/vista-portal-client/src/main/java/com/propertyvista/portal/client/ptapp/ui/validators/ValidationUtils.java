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
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui.validators;

import java.util.Date;

import com.pyx4j.commons.TimeUtils;

public class ValidationUtils {
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
        return TimeUtils.isOlderThen(bithday, 18);
//        if (bithday == null) {
//            return false;
//        } else {
//            Date now = new Date();
//            @SuppressWarnings("deprecation")
//            Date y18 = TimeUtils.createDate(now.getYear() - 18, now.getMonth(), now.getDay());
//            return bithday.before(y18);
//        }
    }
}
