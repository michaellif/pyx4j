/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-05-30
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.simulator.pad;

import java.math.BigDecimal;
import java.util.Date;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.TimeUtils;

public class PadSimUtils {

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
            if (len <= 2) {
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
        return TimeUtils.simpleFormat(value, "yyyyMMdd");
    }

    public static LogicalDate parsDate(String value) {
        return new LogicalDate(TimeUtils.simpleParse(value, "yyyyMMdd"));
    }
}
