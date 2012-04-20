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
 * @author ArtyomB
 * @version $Id$
 */
package com.pyxj4.forms.client.validators.password;

import static com.pyx4j.forms.client.validators.password.PasswordStrengthAssessmentUtils.LOWERCASE_LETTERS;
import static com.pyx4j.forms.client.validators.password.PasswordStrengthAssessmentUtils.UPPERCASE_LETTERS;
import static com.pyx4j.forms.client.validators.password.PasswordStrengthAssessmentUtils.consecutive;
import static com.pyx4j.forms.client.validators.password.PasswordStrengthAssessmentUtils.middleNumbersOrSymbols;
import static com.pyx4j.forms.client.validators.password.PasswordStrengthAssessmentUtils.repeated;
import static junit.framework.Assert.assertEquals;

import org.junit.Test;

public class PasswordStrengthAssessmentUtilsTest {

    @Test
    public void testConsecutive() {

        assertEquals(0, consecutive(LOWERCASE_LETTERS, "aBaBaBaBa"));
        assertEquals(0, consecutive(UPPERCASE_LETTERS, "aBaBaBaBa"));
        assertEquals(3, consecutive(LOWERCASE_LETTERS, "abA123abc"));
        assertEquals(3, consecutive(LOWERCASE_LETTERS, "abA1a23abc"));
        assertEquals(4, consecutive(LOWERCASE_LETTERS, "abA1af23abc"));

    }

    @Test
    public void testRepeated() {

        assertEquals(0, repeated("abcdefgh"));
        assertEquals(1, repeated("abcddefg"));
        assertEquals(2, repeated("aacddefg"));
        assertEquals(3, repeated("aaaddefg"));
        assertEquals(5, repeated("aaaddefDDDg"));

    }

    @Test
    public void testMiddleNumbersOrSymbols() {

        assertEquals(0, middleNumbersOrSymbols("12"));
        assertEquals(0, middleNumbersOrSymbols("1$"));
        assertEquals(0, middleNumbersOrSymbols("1abcdefaga$"));
        assertEquals(1, middleNumbersOrSymbols("1abc1efaga$"));
        assertEquals(3, middleNumbersOrSymbols("1abc1e56aga$"));
        assertEquals(5, middleNumbersOrSymbols("1abc1e56aafga()ga$"));
        assertEquals(5, middleNumbersOrSymbols("1abc1e56aafga()ga$"));
        assertEquals(5, middleNumbersOrSymbols("abc1e56aafga()ga"));
        assertEquals(8, middleNumbersOrSymbols("a!50c1e56aafga()ga"));

    }

}
