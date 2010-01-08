/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Jan 8, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.webstorage.test.client;

import junit.framework.TestCase;

import com.pyx4j.webstorage.client.HTML5LocalStorage;

public class HTML5LocalStorageGWTTest extends TestCase {

    private static final String TEST_NAME_PREFIX = HTML5LocalStorageGWTTest.class.getName();

    private static final String TEST_DATA = "Bob";

    public void testIsSupported() {
        assertTrue("IsSupported", HTML5LocalStorage.isSupported());
    }

    public void testClear() {
        HTML5LocalStorage storage = HTML5LocalStorage.getLocalStorage();
        storage.clear();
        assertEquals("Clean Length", 0, storage.getLength());
    }

    private String getTestKey() {
        return TEST_NAME_PREFIX + "1";
    }

    public void testWrite() {
        HTML5LocalStorage storage = HTML5LocalStorage.getLocalStorage();
        storage.removeItem(getTestKey());
        int len = storage.getLength();
        storage.setItem(getTestKey(), TEST_DATA);
        assertEquals("Added one item Length", 1, storage.getLength() - len);
        assertEquals("read data", TEST_DATA, storage.getItem(getTestKey()));
    }

    public void testReadFromPreviousWrite() {
        HTML5LocalStorage storage = HTML5LocalStorage.getLocalStorage();
        assertEquals("read data", TEST_DATA, storage.getItem(getTestKey()));
    }

    public void testRemoveFromPreviousWrite() {
        HTML5LocalStorage storage = HTML5LocalStorage.getLocalStorage();
        assertEquals("read data", TEST_DATA, storage.getItem(getTestKey()));
        storage.removeItem(getTestKey());
        assertNull("read data", storage.getItem(getTestKey()));
    }
}
