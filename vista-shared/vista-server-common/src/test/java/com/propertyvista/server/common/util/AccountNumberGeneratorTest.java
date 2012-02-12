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

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.server.common.util.AccountNumberGenerator.RandomDigitGenerator;

public class AccountNumberGeneratorTest {

    @Test
    public void testGenerateRandomAccountNumber() {
        test("12345678912341");
        test("43219876543211");
        test("43219876543229");
        test("43209876543220");
    }

    private void test(String accountNumberWithChecksum) {
        AccountNumberGenerator generator = generatorOf(accountNumberWithChecksum.substring(0, accountNumberWithChecksum.length() - 1));
        String generatedAccountNumberWithChecksum = generator.generateRandomAccountNumber();
        Assert.assertEquals("generation failed!", accountNumberWithChecksum, generatedAccountNumberWithChecksum);
    }

    private AccountNumberGenerator generatorOf(final String accountNumber) {
        String validationMessage = "account number must be a string of length " + AccountNumberGenerator.ACCOUNT_NUMBER_LENGTH + " and contain only digits";
        if (accountNumber.length() != AccountNumberGenerator.ACCOUNT_NUMBER_LENGTH | !accountNumber.matches("\\d*")) {
            throw new IllegalStateException(validationMessage);
        }
        return new AccountNumberGenerator(new RandomDigitGenerator() {
            int i = accountNumber.length();

            @Override
            public int nextRandomDigit() {
                return Integer.valueOf("" + accountNumber.charAt(--i));
            }
        });
    }

    @Test
    public void testCreateAccountNumbers() {
        AccountNumberGenerator generator = new AccountNumberGenerator(new Random(1));
        for (int i = 0; i < 10; i++) {
            validateAndPrint(generator.generateRandomAccountNumber());
        }

    }

    private void validateAndPrint(String accountNomber) {
        Assert.assertTrue("Account validation " + accountNomber, ValidationUtils.isCreditCardNumberValid(accountNomber));
        System.out.println(accountNomber + "\t" + AccountNumberGenerator.formatAccountNumber(accountNomber));
    }

}
