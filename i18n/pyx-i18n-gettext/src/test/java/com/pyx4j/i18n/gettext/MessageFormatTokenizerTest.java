/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Apr 7, 2015
 * @author vlads
 */
package com.pyx4j.i18n.gettext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import com.pyx4j.i18n.gettext.MessageFormatTokenizer.I18nConstantsHelper;

public class MessageFormatTokenizerTest extends TestCase {

    private static class I18nConstantsTestsHelper implements I18nConstantsHelper {

        Map<String, String> map = new HashMap<>();

        Set<String> asked = new HashSet<>();

        int translateCallCount = 0;

        @Override
        public String translateConstant(String text) {
            translateCallCount++;
            asked.add(text);
            return map.get(text);
        }

        public void map(String text, String translation) {
            map.put(text, translation);
        }
    }

    private void assertTranslatedFormat(I18nConstantsTestsHelper i18nConstantsHelper, String expected, String pattern) {
        String result = MessageFormatTokenizer.translateTrimSpaces(i18nConstantsHelper, pattern);
        assertEquals(pattern, expected, result);
    }

    public void testQuotedString() {
        {
            I18nConstantsTestsHelper t = new I18nConstantsTestsHelper();
            assertTranslatedFormat(t, "''{0}'' = {1}", "''{0}'' = {1}");
        }
    }

    public void testMultiLine() {
        {
            I18nConstantsTestsHelper t = new I18nConstantsTestsHelper();
            t.map("Are you sure?", "Line1");
            t.map("Press Yes to continue.", "Line2");
            assertTranslatedFormat(t, "Line1\nLine2", "Are you sure?\nPress Yes to continue.");
        }

        {
            I18nConstantsTestsHelper t = new I18nConstantsTestsHelper();
            t.map("Are you", "Line1");
            t.map("Press", "Line2p1");
            t.map("to continue.", "line2p2");
            assertTranslatedFormat(t, "Line1 {0}?\nLine2p1 {1} line2p2", "Are you {0}?\nPress {1} to continue.");
            assertEquals("translateCallCount", 3, t.translateCallCount);
        }

        {
            I18nConstantsTestsHelper t = new I18nConstantsTestsHelper();
            assertTranslatedFormat(t, "{0}\n{1}", "{0}\n{1}");
            assertEquals("translateCallCount", 0, t.translateCallCount);
        }

        {
            I18nConstantsTestsHelper t = new I18nConstantsTestsHelper();
            assertTranslatedFormat(t, "{0}\n{1}\n{2}", "{0}\n{1}\n{2}");
            assertEquals("translateCallCount", 0, t.translateCallCount);
        }

        {
            I18nConstantsTestsHelper t = new I18nConstantsTestsHelper();
            t.map("Are?", "Line1");
            t.map("Continue.", "Line3");
            assertTranslatedFormat(t, "Line1\n{0}\nLine3", "Are?\n{0}\nContinue.");
        }
    }

    public void testI18nConstantsHelper() {
        {
            I18nConstantsTestsHelper t = new I18nConstantsTestsHelper();
            t.map("Yes", "Da");

            assertTranslatedFormat(t, "Da {0} {1,number,percent}", "Yes {0} {1,number,percent}");
            assertTrue(t.asked.contains("Yes"));
            assertEquals("translateCallCount", 1, t.translateCallCount);
        }

        {
            I18nConstantsTestsHelper t = new I18nConstantsTestsHelper();
            t.map("Yes", "Da");
            t.map("No", "Net");

            assertTranslatedFormat(t, "{0,choice,0#Net|1#Da}", "{0,choice,0#No|1#Yes}");
            assertTrue(t.asked.contains("Yes"));
            assertEquals("translateCallCount", 2, t.translateCallCount);
        }

        {
            I18nConstantsTestsHelper t = new I18nConstantsTestsHelper();
            t.map("Has", "Mae");

            assertTranslatedFormat(t, "{1,choice,null#|!null#Mae}", "{1,choice,null#|!null#Has}");
            assertTrue(t.asked.contains("Has"));
            assertEquals("translateCallCount", 1, t.translateCallCount);
        }

        {
            I18nConstantsTestsHelper t = new I18nConstantsTestsHelper();
            assertTranslatedFormat(t, "{1,choice,null#|!null#, {1}}", "{1,choice,null#|!null#, {1}}");
            assertEquals("translateCallCount", 0, t.translateCallCount);
        }

        {
            I18nConstantsTestsHelper t = new I18nConstantsTestsHelper();
            t.map("Total", "Vsego");

            assertTranslatedFormat(t, "{1,choice,null#|!null#, Vsego {1}}", "{1,choice,null#|!null#, Total {1}}");
            assertTrue(t.asked.contains("Total"));
            assertEquals("translateCallCount", 1, t.translateCallCount);
        }

        {
            I18nConstantsTestsHelper t = new I18nConstantsTestsHelper();
            t.map("Waiting", "W");
            t.map("Erred", "E");
            t.map("Processed", "P");
            t.map("Failed", "F");

            assertTranslatedFormat(t, "W {1}, E {2}, P {3}, F {4}", "Waiting {1}, Erred {2}, Processed {3}, Failed {4}");
            assertEquals("translateCallCount", 4, t.translateCallCount);
        }
    }

    public void testNextedChoice() {
        {
            I18nConstantsTestsHelper t = new I18nConstantsTestsHelper();
            t.map("X", "Z");
            assertTranslatedFormat(t, //
                    "{0,choice,7#{1,choice,8#{1}|9#Z}|9#{0}}", //
                    "{0,choice,7#{1,choice,8#{1}|9#X}|9#{0}}");
            assertEquals("translateCallCount", 1, t.translateCallCount);
        }

        {
            I18nConstantsTestsHelper t = new I18nConstantsTestsHelper();
            t.map("Default", "Fault");
            assertTranslatedFormat(t, //
                    "{1,choice,A#{2,choice,null#|!null#Fault {2}}|B#{1}}", //
                    "{1,choice,A#{2,choice,null#|!null#Default {2}}|B#{1}}");
            assertEquals("translateCallCount", 1, t.translateCallCount);
        }
    }

    public void testSpaceStripTranslations() {
        {
            I18nConstantsTestsHelper t = new I18nConstantsTestsHelper();
            t.map("Yes", "Da");
            t.map("Belongs to", "Prinadlezit");

            assertTranslatedFormat(t, "Da {0} Prinadlezit {1}", "Yes {0} Belongs to {1}");
            assertTrue(t.asked.contains("Yes"));
            assertEquals("translateCallCount", 2, t.translateCallCount);
        }

        {
            I18nConstantsTestsHelper t = new I18nConstantsTestsHelper();
            t.map("Yes", "Da");
            t.map("Belongs to", "Prinadlezit");
            t.map("and not to", "ane");

            assertTranslatedFormat(t, "Da {0} Prinadlezit {1} ane {2}", "Yes {0} Belongs to {1} and not to {2}");
            assertTrue(t.asked.contains("Yes"));
            assertTrue(t.asked.contains("and not to"));
            assertEquals("translateCallCount", 3, t.translateCallCount);
        }
    }
}
