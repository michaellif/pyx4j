/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Apr 20, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.validators.password;

import static com.pyx4j.forms.client.validators.password.PasswordStrengthAssessmentUtils.LOWERCASE_LETTERS;
import static com.pyx4j.forms.client.validators.password.PasswordStrengthAssessmentUtils.NUMBERS;
import static com.pyx4j.forms.client.validators.password.PasswordStrengthAssessmentUtils.UPPERCASE_LETTERS;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class DefaultPasswordStrengthRule implements PasswordStrengthRule {

    private final Collection<String> dictionary;

    private int minimalLenght = 6;

    public DefaultPasswordStrengthRule() {
        this.dictionary = new HashSet<String>();
    }

    public int getMinimalLenght() {
        return minimalLenght;
    }

    public void setMinimalLenght(int minimalLenght) {
        this.minimalLenght = minimalLenght;
    }

    public void setDictionary(Collection<String> dictionary) {
        this.dictionary.clear();
        for (String word : dictionary) {
            this.dictionary.addAll(split(word));
        }
    }

    private Collection<String> split(String word) {
        Collection<String> c = new HashSet<String>();
        word = word.toLowerCase();
        c.add(word);
        c.addAll(Arrays.asList(word.split(" ")));
        c.addAll(Arrays.asList(word.split("@")));
        return c;
    }

    @Override
    public PasswordStrengthVerdict getPasswordVerdict(String password) {
        if (password == null || password.length() == 0) {
            return null;
        }
        int passwordLength = password.length();
        if (passwordLength < minimalLenght) {
            return PasswordStrengthVerdict.TooShort;
        }
        String word = password.toLowerCase();
        if (dictionary.contains(word)) {
            return PasswordStrengthVerdict.Invalid;
        }
        // Just letters or just numbers
        if (word.matches("[a-z]+") || word.matches("[0-9]+")) {
            return PasswordStrengthVerdict.Weak;
        }

        boolean hasLetters = word.matches(".*[a-z]+.*");
        boolean mixedCaseLetters = !word.equals(password);
        boolean hasNumbers = word.matches(".*[0-9]+.*");
        boolean hasSpecialCharacters = word.matches(".*[\"'!@#$%^&*(){}[\\\\]|\\/?.,<>\\-_+=~`].*");

        int countCharacterClass = 0;
        if (hasLetters) {
            countCharacterClass++;
        }
        if (mixedCaseLetters) {
            countCharacterClass++;
        }
        if (hasNumbers) {
            countCharacterClass++;
        }
        if (hasSpecialCharacters) {
            countCharacterClass++;
        }
        if (countCharacterClass < 3) {
            return PasswordStrengthVerdict.Weak;
        }
        if ((!mixedCaseLetters) && (!hasNumbers) && (!hasSpecialCharacters)) {
            return PasswordStrengthVerdict.Weak;
        }

        if (PasswordStrengthAssessmentUtils.middleNumbersOrSymbols(password) == 0) {
            return PasswordStrengthVerdict.Weak;
        }

        boolean hasDictionary = false;
        for (String dictionaryWord : dictionary) {
            if (word.contains(dictionaryWord)) {
                hasDictionary = true;
                if ((passwordLength - dictionaryWord.length()) <= 4) {
                    return PasswordStrengthVerdict.Weak;
                }
            }
        }
        // We eliminated the obvious cases of Weak passwords

        int consecutive = PasswordStrengthAssessmentUtils.consecutive(UPPERCASE_LETTERS, password)
                + PasswordStrengthAssessmentUtils.consecutive(LOWERCASE_LETTERS, password) + PasswordStrengthAssessmentUtils.consecutive(NUMBERS, password);
        int repeated = PasswordStrengthAssessmentUtils.repeated(password);
        double passwordValue = passwordLength * 13 - consecutive * 4 - repeated * 4;

        double valueCoefficient;

        //TODO The code below does not suggest if the the password is actually Good or Strong.
        if (hasDictionary) {
            return PasswordStrengthVerdict.Weak;
        } else if ((countCharacterClass == 4) && (passwordLength >= 8)) {
            valueCoefficient = 1.0;
        } else {
            valueCoefficient = 0.8;
        }

        // normalize
        if (passwordValue > 100) {
            passwordValue = 100;
        } else if (passwordValue < 0) {
            passwordValue = 0;
        }

        // perform discretization
        passwordValue *= valueCoefficient;

        if (passwordValue < 10) {
            return PasswordStrengthVerdict.Invalid;
        } else if (passwordValue < 30) {
            return PasswordStrengthVerdict.Weak;
        } else if (passwordValue < 50) {
            return PasswordStrengthVerdict.Fair;
        } else if (passwordValue < 70) {
            return PasswordStrengthVerdict.Good;
        } else {
            return PasswordStrengthVerdict.Strong;
        }

    }
}
