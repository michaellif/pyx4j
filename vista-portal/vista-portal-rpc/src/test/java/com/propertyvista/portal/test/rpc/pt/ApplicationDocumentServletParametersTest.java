/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.propertyvista.portal.test.rpc.pt;

import junit.framework.TestCase;
import com.propertyvista.portal.rpc.pt.ApplicationDocumentServletParameters;
import com.pyx4j.essentials.rpc.report.DownloadFormat;

public class ApplicationDocumentServletParametersTest extends TestCase {

    public void testSUPPORTED_FILE_EXTENSIONS_contains_true() {
        assertTrue("BMP", ApplicationDocumentServletParameters.SUPPORTED_FILE_EXTENSIONS.contains(DownloadFormat.BMP));
        assertTrue("JPEG", ApplicationDocumentServletParameters.SUPPORTED_FILE_EXTENSIONS.contains(DownloadFormat.JPEG));
        assertTrue("PDF", ApplicationDocumentServletParameters.SUPPORTED_FILE_EXTENSIONS.contains(DownloadFormat.PDF));
    }

    public void testSUPPORTED_FILE_EXTENSIONS_contains_false() {
        assertFalse(ApplicationDocumentServletParameters.SUPPORTED_FILE_EXTENSIONS.contains(DownloadFormat.HTML));
    }

}