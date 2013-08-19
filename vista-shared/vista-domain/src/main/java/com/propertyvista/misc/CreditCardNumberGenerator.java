/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.misc;

import java.util.List;
import java.util.Vector;

import com.propertyvista.domain.payment.CreditCardInfo.CreditCardType;

public class CreditCardNumberGenerator {

    private static final String[] VISA_PREFIX_LIST = new String[] { "4539", "4556", "4916", "4532", "4929", "40240071", "4485", "4716", "4" };

    private static final String[] VISA_DEBIT_PREFIX_LIST = new String[] { "400447" /* , "402371", "402372" */};

    private static final String[] MASTERCARD_PREFIX_LIST = new String[] { "51", "52", "53", "54", "55" };

    private static final String[] AMEX_PREFIX_LIST = new String[] { "34", "37" };

    private static final String[] DISCOVER_PREFIX_LIST = new String[] { "6011" };

    private static final String[] DINERS_PREFIX_LIST = new String[] { "300", "301", "302", "303", "36", "38" };

    private static final String[] ENROUTE_PREFIX_LIST = new String[] { "2014", "2149" };

    private static final String[] JCB_16_PREFIX_LIST = new String[] { "3088", "3096", "3112", "3158", "3337", "3528" };

    private static final String[] JCB_15_PREFIX_LIST = new String[] { "2100", "1800" };

    private static final String[] VOYAGER_PREFIX_LIST = new String[] { "8699" };

    private static String strrev(String str) {
        if (str == null)
            return "";
        String revstr = "";
        for (int i = str.length() - 1; i >= 0; i--) {
            revstr += str.charAt(i);
        }

        return revstr;
    }

/*
 * 'prefix' is the start of the CC number as a string, any number of digits.
 * 'length' is the length of the CC number to generate. Typically 13 or 16
 */
    private static String completedNumber(String prefix, int length) {

        String ccnumber = prefix;

        // generate digits

        while (ccnumber.length() < (length - 1)) {
            ccnumber += new Double(Math.floor(Math.random() * 10)).intValue();
        }

        // reverse number and convert to int

        String reversedCCnumberString = strrev(ccnumber);

        List<Integer> reversedCCnumberList = new Vector<Integer>();
        for (int i = 0; i < reversedCCnumberString.length(); i++) {
            reversedCCnumberList.add(new Integer(String.valueOf(reversedCCnumberString.charAt(i))));
        }

        // calculate sum

        int sum = 0;
        int pos = 0;

        Integer[] reversedCCnumber = reversedCCnumberList.toArray(new Integer[reversedCCnumberList.size()]);
        while (pos < length - 1) {

            int odd = reversedCCnumber[pos] * 2;
            if (odd > 9) {
                odd -= 9;
            }

            sum += odd;

            if (pos != (length - 2)) {
                sum += reversedCCnumber[pos + 1];
            }
            pos += 2;
        }

        // calculate check digit

        int checkdigit = new Double(((Math.floor(sum / 10) + 1) * 10 - sum) % 10).intValue();
        ccnumber += checkdigit;

        return ccnumber;

    }

    private static String generateCardNumber(String[] prefixList, int length) {
        int randomArrayIndex = (int) Math.floor(Math.random() * prefixList.length);
        String ccnumber = prefixList[randomArrayIndex];
        return completedNumber(ccnumber, length);
    }

    public static String generateMasterCardNumber() {
        return generateCardNumber(MASTERCARD_PREFIX_LIST, 16);
    }

    public static String generateVisaCardNumber() {
        return generateCardNumber(VISA_PREFIX_LIST, 16);
    }

    public static String generateVisaDebitCardNumber() {
        return generateCardNumber(VISA_DEBIT_PREFIX_LIST, 16);
    }

    public static String generateCardNumber(CreditCardType type) {
        switch (type) {
        case MasterCard:
            return generateMasterCardNumber();
        case Visa:
            return generateVisaCardNumber();
        case VisaDebit:
            return generateVisaDebitCardNumber();
        default:
            throw new IllegalArgumentException();
        }

    }

}
