/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jul 12, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.test.commons;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.pyx4j.commons.EqualsHelper;

import junit.framework.TestCase;

public class EqualsHelperTest extends TestCase {

    public void testEqualsObject() {

        assertTrue("Non null same Object", EqualsHelper.equals(this, this));

        assertFalse("null to Object", EqualsHelper.equals(this, null));

        assertFalse("null to Object", EqualsHelper.equals(null, this));

        assertTrue("null to null", EqualsHelper.equals((Object) null, null));
    }

    public void testEqualsString() {

        assertTrue("Same String", EqualsHelper.equals("X", "X"));

        assertFalse("Diferent String", EqualsHelper.equals("X1", "X2"));

        assertFalse("String and null", EqualsHelper.equals("X1", null));

        assertFalse("String and null", EqualsHelper.equals(null, "X1"));

        assertTrue("Empty String and null", EqualsHelper.equals("", null));

        assertTrue("Empty String and null", EqualsHelper.equals(null, ""));
    }

    public void testEqualsVector() {
        List<String> value1 = new Vector<String>();
        List<String> value2 = new Vector<String>();
        value1.add("Bob");
        value2.add("Bob");
        assertTrue("Vectors are the same", EqualsHelper.equals(value1, value2));
        value2.add("Bob2");
        assertFalse("Vectors are different", EqualsHelper.equals(value1, value2));
        value1.add("Bob2");
        assertTrue("Vectors are the same again", EqualsHelper.equals(value1, value2));

        value2 = new Vector<String>();
        value2.add("Bob3");
        assertFalse("Vectors are different", EqualsHelper.equals(value1, value2));
    }

    public void testEqualsHashSet() {
        Set<String> value1 = new HashSet<String>();
        Set<String> value2 = new HashSet<String>();
        value1.add("Bob");
        value2.add("Bob");
        assertTrue("Sets are the same", EqualsHelper.equals(value1, value2));
        value2.add("Bob2");
        assertFalse("Sets are different", EqualsHelper.equals(value1, value2));
        value1.add("Bob2");
        assertTrue("Sets are the same again", EqualsHelper.equals(value1, value2));

        value1.remove("Bob2");
        value2 = new HashSet<String>();
        value2.add("Bob3");
        assertFalse("Sets are different", EqualsHelper.equals(value1, value2));
    }

    public void tesEqualstMap() {
        Map<String, String> map1 = new HashMap<String, String>();
        Map<String, String> map2 = new HashMap<String, String>();
        map1.put("Bob", "Cat");
        map2.put("Bob", "Cat");
        assertTrue("Maps are the same", EqualsHelper.equals(map1, map2));
        map2.put("Bob", "Dog");
        assertFalse("Maps are different", EqualsHelper.equals(map1, map2));
    }
}
