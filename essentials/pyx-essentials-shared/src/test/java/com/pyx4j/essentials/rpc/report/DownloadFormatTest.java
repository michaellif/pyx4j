/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pyx4j.essentials.rpc.report;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sergei
 */
public class DownloadFormatTest {

    public DownloadFormatTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of values method, of class DownloadFormat.
     */
    @Test
    public void testValues() {
        DownloadFormat[] result = DownloadFormat.values();
        assertNotNull(result);
        assertTrue(result.length>0);
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
        } catch(IllegalArgumentException e) {
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
        } catch(IllegalArgumentException e) {
            //as expected
            return;
        }
        fail("The test expected to throw IllegalArgumentException");
    }

}