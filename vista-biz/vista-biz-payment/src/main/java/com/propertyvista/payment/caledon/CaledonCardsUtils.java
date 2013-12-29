/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 13, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.caledon;

import java.math.BigDecimal;

import com.pyx4j.commons.CommonsStringUtils;

public class CaledonCardsUtils {

    public static String formatAmount(BigDecimal value) {
        BigDecimal centValue = value.multiply(new BigDecimal("100"));
        return centValue.setScale(0).toString();
    }

    public static BigDecimal parsAmount(String value) {
        if (CommonsStringUtils.isEmpty(value)) {
            return null;
        } else {
            String valueCents;
            String valueDollars;
            int len = value.length();
            if (len == 1) {
                valueCents = "0" + value;
                valueDollars = "0";
            } else if (len == 2) {
                valueCents = value;
                valueDollars = "0";
            } else {
                valueCents = value.substring(len - 2, len);
                valueDollars = value.substring(0, len - 2);
            }

            BigDecimal money = new BigDecimal(valueDollars + "." + valueCents);
            return money.setScale(2);
        }
    }
}
