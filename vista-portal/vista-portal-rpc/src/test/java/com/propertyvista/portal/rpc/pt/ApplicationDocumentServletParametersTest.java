/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.propertyvista.portal.rpc.pt;

import com.pyx4j.essentials.rpc.report.DownloadFormat;
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
public class ApplicationDocumentServletParametersTest {

    public ApplicationDocumentServletParametersTest() {
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

    @Test
    public void testSUPPORTED_FILE_EXTENSIONS_contains_true() {
        System.out.println("testSUPPORTED_FILE_EXTENSIONS_contains_true");
        assertTrue(ApplicationDocumentServletParameters.SUPPORTED_FILE_EXTENSIONS.contains(DownloadFormat.BMP));
    }

    @Test
    public void testSUPPORTED_FILE_EXTENSIONS_contains_false() {
        System.out.println("testSUPPORTED_FILE_EXTENSIONS_contains_false");
        assertFalse(ApplicationDocumentServletParameters.SUPPORTED_FILE_EXTENSIONS.contains(DownloadFormat.HTML));
    }

}