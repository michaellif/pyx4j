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
 * Created on 2012-12-27
 * @author vlads
 */
package com.pyx4j.gwt.test.shared;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.pyx4j.gwt.shared.DownloadFormat;

public class DownloadFormatTest {
    /**
     * Test of values method, of class DownloadFormat.
     */
    @Test
    public void testValues() {
        DownloadFormat[] result = DownloadFormat.values();
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    /**
     * Test of valueOf method, of class DownloadFormat.
     */
    @Test
    public void testValueOf() {
        String name = "BMP";
        DownloadFormat expResult = DownloadFormat.BMP;
        DownloadFormat result = DownloadFormat.valueOf(name);
        assertEquals(expResult, result);
    }

    /**
     * Test of valueOf method, of class DownloadFormat.
     */
    @Test
    public void testValueOf_tofail() {
        String name = "blahblahblah";
        try {
            DownloadFormat.valueOf(name);
        } catch (IllegalArgumentException e) {
            //as expected
            return;
        }
        fail("The test expected to throw IllegalArgumentException");
    }

    /**
     * Test of getExtension method, of class DownloadFormat.
     */
    @Test
    public void testGetExtension() {
        DownloadFormat instance = DownloadFormat.BMP;
        String expResult = "bmp";
        String result = instance.getExtension();
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class DownloadFormat.
     */
    @Test
    public void testToString() {
        DownloadFormat instance = DownloadFormat.BMP;
        String expResult = "Image";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of getName method, of class DownloadFormat.
     */
    @Test
    public void testGetName() {
        DownloadFormat instance = DownloadFormat.BMP;
        String expResult = "Image";
        String result = instance.getName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getExtensions method, of class DownloadFormat.
     */
    @Test
    public void testGetExtensions() {
        DownloadFormat instance = DownloadFormat.JPEG;
        String[] expResult = new String[] { "jpg", "jpeg" };
        String[] result = instance.getExtensions();
        assertArrayEquals(expResult, result);
    }

    /**
     * Test of valueByExtension method, of class DownloadFormat.
     */
    @Test
    public void testValueByExtension() {
        String ext = "jpeg";
        DownloadFormat expResult = DownloadFormat.JPEG;
        DownloadFormat result = DownloadFormat.valueByExtension(ext);
        assertEquals(expResult, result);
    }

    /**
     * Test of valueByExtension method, of class DownloadFormat.
     */
    @Test
    public void testValueByExtension_tofail() {
        String ext = "blahblahblah";
        try {
            DownloadFormat.valueByExtension(ext);
        } catch (IllegalArgumentException e) {
            //as expected
            return;
        }
        fail("The test expected to throw IllegalArgumentException");
    }

}
