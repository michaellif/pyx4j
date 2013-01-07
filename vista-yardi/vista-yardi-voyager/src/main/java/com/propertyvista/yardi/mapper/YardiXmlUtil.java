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

import org.apache.axiom.om.OMElement;
import org.apache.commons.lang.StringUtils;

import com.propertyvista.yardi.YardiConstants;

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

    /**
     * Converts element to string. Updates default namespace URI in case it is missed.
     * 
     * @param element
     *            the element to convert
     * @return string element's representation
     */
    public static String elementToString(OMElement element) {
        return StringUtils.isEmpty(element.getDefaultNamespace().getNamespaceURI()) ? updateDefaultNamespaceURI(element) : element.toString();
    }

    private static String updateDefaultNamespaceURI(final OMElement element) {
        element.declareDefaultNamespace(YardiConstants.NAMESPACE);
        return clearEmptyNamespace(element.toString());
    }

    private static String clearEmptyNamespace(String xml) {
        return xml.replace("xmlns=\"\"", StringUtils.EMPTY);
    }

}
