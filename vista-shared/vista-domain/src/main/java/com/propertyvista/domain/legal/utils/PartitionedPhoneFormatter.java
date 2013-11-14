/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-14
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.legal.utils;

public class PartitionedPhoneFormatter implements PdfFormFieldFormatter {

    @Override
    public String format(Object object) {
        String value = (String) object;
        // TODO add check for format correctness
        value = value.trim().replace("(", "").replace(")", "").replace("-", "").replace(" ", "");
        return value;
    }

}
