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
 * Created on Jan 8, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.webstorage.test.client;

import junit.framework.TestCase;

import com.google.gwt.storage.client.Storage;

public class HTML5LocalStorageGWTTest extends TestCase {

    private static final String TEST_NAME_PREFIX = HTML5LocalStorageGWTTest.class.getName();

    private static final String TEST_DATA = "Bob";

    public void testIsSupported() {
        assertTrue("IsSupported", Storage.isSupported());
    }

    public void testClear() {
        Storage storage = Storage.getLocalStorageIfSupported();
        storage.clear();
        assertEquals("Clean Length", 0, storage.getLength());
    }

    private String getTestKey() {
        return TEST_NAME_PREFIX + "1";
    }

    public void testWrite() {
        Storage storage = Storage.getLocalStorageIfSupported();
        storage.removeItem(getTestKey());
        int len = storage.getLength();
        storage.setItem(getTestKey(), TEST_DATA);
        assertEquals("Added one item Length", 1, storage.getLength() - len);
        assertEquals("read data", TEST_DATA, storage.getItem(getTestKey()));
    }

    public void testReadFromPreviousWrite() {
        Storage storage = Storage.getLocalStorageIfSupported();
        assertEquals("read data", TEST_DATA, storage.getItem(getTestKey()));
    }

    public void testRemoveFromPreviousWrite() {
        Storage storage = Storage.getLocalStorageIfSupported();
        assertEquals("read data", TEST_DATA, storage.getItem(getTestKey()));
        storage.removeItem(getTestKey());
        assertNull("read data", storage.getItem(getTestKey()));
    }
}
