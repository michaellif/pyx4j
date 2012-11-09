/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 21, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi.mapper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class YardiXmlUtil {

    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");

    public static String strDate(Date date) {
        if (date == null) {
            return "null";
        }
        synchronized (DATE_FORMAT) {
            return DATE_FORMAT.format(date);
        }
    }

    /**
     * <PhysicalProperty xmlns="" xmlns:MITS="http://my-company.com/namespace">
     */
    public static String stripGetUnitInformation(String xml) {

        xml = xml.replace("xmlns=\"\"", "xmlns=\"http://yardi.com/ResidentTransactions20\"");

        return xml;
    }
}
