/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-26
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.eft.caledoneft;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;

public class CaledonPadUtils {

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

    public static String formatDate(Date value) {
        return new SimpleDateFormat("yyyyMMdd").format(value);
    }

    private static Date parseAndValidate(String value, String pattern) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setLenient(false);
        Date dateObj = format.parse(value);
        if (!value.equals(format.format(dateObj))) {
            throw new ParseException("Date value [" + value + "] does not match pattern [" + pattern + "]", 0);
        }
        return dateObj;
    }

    public static LogicalDate parsDate(String value) {
        try {
            return new LogicalDate(parseAndValidate(value, "yyyyMMdd"));
        } catch (ParseException e) {
            throw new Error("Invalid date format '" + value + "'");
        }
    }

    /**
     * Temporary parser for Caledon Reconciliations.
     */
    public static LogicalDate parsDateReconciliation(String value) {
        try {
            return new LogicalDate(parseAndValidate(value, "yyyy-MM-dd"));
        } catch (ParseException ignore) {
        }
        // Fallback to generic Caledon spec
        try {
            return new LogicalDate(parseAndValidate(value, "yyyyMMdd"));
        } catch (ParseException e) {
            throw new Error("Invalid date format '" + value + "'");
        }
    }
}
