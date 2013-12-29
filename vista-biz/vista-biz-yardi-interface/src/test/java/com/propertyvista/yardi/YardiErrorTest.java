/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 31, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi;

import junit.framework.TestCase;

public class YardiErrorTest extends TestCase {

    public void testXmlError1() {
        String error = "There are no work orders found for these input values.";
        String xml = "<ServiceRequests><ServiceRequest><ErrorMessages><Error>" + error + "</Error></ErrorMessages></ServiceRequest></ServiceRequests>";
        assertEquals("Error Message", error, yardiErrorCheck(xml));
    }

    public void testXmlError2() {
        String error = "Could not find Property:B1.";
        String xml = "<ServiceRequests><ServiceRequest><ServiceRequestId>0</ServiceRequestId><PropertyCode>B1</PropertyCode><UnitCode>#100</UnitCode><ErrorMessages><Error>"
                + error + "</Error></ErrorMessages></ServiceRequest>";
        assertEquals("Error Message", error, yardiErrorCheck(xml));
    }

    public void testXmlError3() {
        String error = "Interface 'Property Vista-Maintenance' is not Configured for property 'gibb0380'";
        String xml = "<ServiceRequests><ServiceRequest><PropertyCode>gibb0380</PropertyCode><UnitCode>0100</UnitCode><ErrorMessage>" + error
                + "</ErrorMessage></ServiceRequest></ServiceRequests>";
        assertEquals("Error Message", error, yardiErrorCheck(xml));
    }

    private String yardiErrorCheck(String s) {
        {
            String regex = ".*<ErrorMessages><Error>(.*)</Error></ErrorMessages>.*";
            if (s.matches(regex)) {
                return s.replaceFirst(regex, "$1");
            }
        }
        {
            String regex = ".*<ErrorMessage>(.*)</ErrorMessage>.*";
            if (s.matches(regex)) {
                return s.replaceFirst(regex, "$1");
            }
        }
        return null;
    }

}
