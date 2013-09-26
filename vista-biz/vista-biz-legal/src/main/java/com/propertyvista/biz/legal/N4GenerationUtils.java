/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Sep 26, 2013
 * @author Artyom
 * @version $Id$
 */
package com.propertyvista.biz.legal;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pyx4j.commons.SimpleMessageFormat;

public class N4GenerationUtils {

    private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");

    private static final Pattern phonePattern = Pattern.compile("\\((\\d\\d\\d)\\) (\\d\\d\\d)-(\\d\\d\\d\\d)( x\\d+)?");

    public static String[] splitCurrency(BigDecimal amount) {
        if (amount.compareTo(new BigDecimal("99999.99")) > 0) {
            throw new Error("The current amount could not be formatted (greater than $99,999.99 and cannot fit into the form field)");
        }
        String spaces = "     ";
        String formattedAmount = SimpleMessageFormat.format("{0,number,#,##0.00}", amount);
        // pad with spaces
        formattedAmount = spaces.substring(0, 9 - formattedAmount.length()) + formattedAmount;

        String[] splittedCurrency = new String[3];
        int indexOfComma = formattedAmount.indexOf(",");
        splittedCurrency[0] = indexOfComma != -1 ? formattedAmount.substring(0, indexOfComma) : "  ";
        int indexOfDot = formattedAmount.indexOf(".");
        splittedCurrency[1] = formattedAmount.substring(indexOfDot - 3, indexOfDot);
        splittedCurrency[2] = formattedAmount.substring(indexOfDot + 1, indexOfDot + 3);

        return splittedCurrency;
    }

    public static String[] splitDate(Date date) {
        String[] splittedDate = new String[3];
        String formattedDate = dateFormat.format(date);
        splittedDate[0] = formattedDate.substring(0, 2);
        splittedDate[1] = formattedDate.substring(3, 5);
        splittedDate[2] = formattedDate.substring(6, 10);
        return splittedDate;
    }

    public static String[] splitPhoneNumber(String phoneNumber) {
        Matcher m = phonePattern.matcher(phoneNumber);
        if (m.matches()) {
            String[] splitNumber = new String[3];
            splitNumber[0] = m.group(1);
            splitNumber[1] = m.group(2);
            splitNumber[2] = m.group(3);
            return splitNumber;
        } else {
            return null;
        }
    }
}
