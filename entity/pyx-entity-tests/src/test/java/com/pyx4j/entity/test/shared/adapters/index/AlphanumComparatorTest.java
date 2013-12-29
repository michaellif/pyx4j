/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2012-09-11
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared.adapters.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.shared.adapters.index.AlphanumIndexAdapter;

public class AlphanumComparatorTest extends TestCase {

    private static void assertAlphanum(String value, String expected) {
        assertEquals(value, expected, AlphanumIndexAdapter.alphanum(value));
    }

    public void testAlphanumValues() {
        assertAlphanum("10A", "0000010a");
        assertAlphanum("10A21", "0000010a0000021");
        assertAlphanum("10A21c4", "0000010a0000021c4");
    }

    private static void add(List<String> anum, String... values) {
        anum.addAll(Arrays.asList(values));
    }

    private static void assertEquals(List<String> anum, String... expected) {
        List<String> expectedList = new ArrayList<String>();
        expectedList.addAll(Arrays.asList(expected));
        if (!EqualsHelper.equals(anum, expectedList)) {
            fail("Unexpected Order" + anum + " != " + expectedList);
        }
    }

    public void testSort() {
        List<String> anum = new ArrayList<String>();
        add(anum, "A1", "A2", "A20", "A10");

        Collections.sort(anum);
        assertEquals(anum, "A1", "A10", "A2", "A20");

        Collections.sort(anum, new AlphanumComparator());
        assertEquals(anum, "A1", "A2", "A10", "A20");

    }

    public void testSortSufiexed() {
        List<String> anum = new ArrayList<String>();
        add(anum, "1", "1A", "2", "2A");

        Collections.sort(anum);
        assertEquals(anum, "1", "1A", "2", "2A");

        Collections.sort(anum, new AlphanumComparator());
        assertEquals(anum, "1", "1A", "2", "2A");
    }
}
