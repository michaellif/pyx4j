/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 10, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.policy;

import java.util.Random;

/**
 * @see <a href="http://jira.birchwoodsoftwaregroup.com/wiki/display/VISTA/Account+Numbers"/>
 */
public class AccountNumberGenerator {

    public static final int ACCOUNT_NUMBER_LENGTH = 13;

    private static final int[] SUM_OF_DOUBLE_DIGIT = new int[] { 0, 2, 4, 6, 8, 1, 3, 5, 7, 9 };

    private static final char[] CHAR_OF = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    private final RandomDigitGenerator digitGenerator;

    public AccountNumberGenerator(final Random rnd) {
        this(new RandomDigitGenerator() {
            @Override
            public int nextRandomDigit() {
                return rnd.nextInt(10);
            }
        });
    }

    protected AccountNumberGenerator(RandomDigitGenerator digitGenerator) {
        this.digitGenerator = digitGenerator;
    }

    public String generateRandomAccountNumber() {
        int sum = 0;
        char[] digits = new char[ACCOUNT_NUMBER_LENGTH + 1]; // add one more for the checksum digit        
        for (int i = 0; i < ACCOUNT_NUMBER_LENGTH; ++i) {
            int digit = digitGenerator.nextRandomDigit();
            digits[ACCOUNT_NUMBER_LENGTH - 1 - i] = CHAR_OF[digit];
            sum += (i & 1) == 0 ? SUM_OF_DOUBLE_DIGIT[digit] : digit;
        }
        sum %= 10;
        digits[ACCOUNT_NUMBER_LENGTH] = CHAR_OF[sum == 0 ? 0 : 10 - sum];

        return String.valueOf(digits);
    }

    // add one more for the checksum digit
    public static String addChecksum(String accountNumberBase) {
        int sum = 0;
        assert (accountNumberBase.length() == ACCOUNT_NUMBER_LENGTH) : " expected 13 characters got '" + accountNumberBase + "' " + accountNumberBase.length();
        for (int i = 0; i < accountNumberBase.length(); ++i) {
            int digit = Integer.valueOf(String.valueOf(accountNumberBase.charAt(i)));
            sum += (i & 1) == 0 ? SUM_OF_DOUBLE_DIGIT[digit] : digit;
        }
        sum %= 10;
        return accountNumberBase + CHAR_OF[sum == 0 ? 0 : 10 - sum];
    }

    /**
     * Generator of digits of an account from right to left (less significant to most significant)
     */
    protected interface RandomDigitGenerator {

        int nextRandomDigit();

    }

    public static String formatAccountNumber(String accountNumber) {
        String numbers = accountNumber.trim().replaceAll("\\s", "").replaceAll("-", "");
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < numbers.length(); ++i) {
            b.append(numbers.charAt(i));
            if (i % 3 == 2) {
                b.append('-');
            }
        }
        return b.toString();
    }
}
