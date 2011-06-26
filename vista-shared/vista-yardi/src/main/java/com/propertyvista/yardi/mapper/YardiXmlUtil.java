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

public class YardiXmlUtil {

    /**
     * <ResidentTransactions xmlns="http://yardi.com/ResidentTransactions20"
     * xmlns:MITS="http://my-company.com/namespace"
     * xsi:schemaLocation="http://yardi.com/ResidentTransactions20 .\Itf_MITS_ResidentTransactions2.0.xsd">
     * 
     */
    public static String stripGetResidentTransactions(String xml) {

//        String token = "xsi:schemaLocation=\"http://yardi.com/ResidentTransactions20 .\\Itf_MITS_ResidentTransactions2.0.xsd\"";
//        xml = xml.replace(token, "");

//        token = "xmlns=\"http://yardi.com/ResidentTransactions20\"";
//        xml = xml.replace(token, "");

        return xml;
    }

}
