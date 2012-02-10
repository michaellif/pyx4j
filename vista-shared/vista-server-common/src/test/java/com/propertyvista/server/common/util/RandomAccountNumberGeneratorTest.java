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
package com.propertyvista.server.common.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.propertyvista.server.common.util.RandomAccountNumberGenerator;
import com.propertyvista.server.common.util.RandomAccountNumberGenerator.RandomDigitGenerator;

public class RandomAccountNumberGeneratorTest {

    @Test
    public void testGenerateRandomAccountNumber() {
        test("12345678912341");
        test("43219876543211");
        test("43219876543229");
        test("43209876543220");
    }

    private void test(String accountNumberWithChecksum) {
        RandomAccountNumberGenerator generator = generatorOf(accountNumberWithChecksum.substring(0, accountNumberWithChecksum.length() - 1));
        String generatedAccountNumberWithChecksum = generator.generateRandomAccountNumber();
        assertEquals("generation failed!", accountNumberWithChecksum, generatedAccountNumberWithChecksum);
    }

    private RandomAccountNumberGenerator generatorOf(final String accountNumber) {
        String validationMessage = "account number must be a string of length " + RandomAccountNumberGenerator.ACCOUNT_NUMBER_LENGTH
                + " and contain only digits";
        if (accountNumber.length() != RandomAccountNumberGenerator.ACCOUNT_NUMBER_LENGTH | !accountNumber.matches("\\d*")) {
            throw new IllegalStateException(validationMessage);
        }
        return new RandomAccountNumberGenerator(new RandomDigitGenerator() {
            int i = accountNumber.length();

            @Override
            public int nextRandomDigit() {
                return Integer.valueOf("" + accountNumber.charAt(--i));
            }
        });
    }
}
