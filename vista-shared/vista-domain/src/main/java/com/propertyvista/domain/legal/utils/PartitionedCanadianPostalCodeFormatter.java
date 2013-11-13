/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-13
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.legal.utils;


public class PartitionedCanadianPostalCodeFormatter implements PdfFormFieldFormatter {

    @Override
    public String format(Object object) {
        String value = ((String) object).trim();
        if (value.length() != 7 && value.charAt(3) != ' ') {
            throw new IllegalArgumentException("got wrong postal code '" + value + "', expecting value inf 'ADA DAD' format");
        }

        return value.toUpperCase().replace(" ", "");
    }
}
