/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.openapi.model.util;

import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class LogicalDateXmlAdapter extends XmlAdapter<XMLGregorianCalendar, Date> {

    // We use second day to avoid time zone conversion problems
    @SuppressWarnings("deprecation")
    @Override
    public Date unmarshal(XMLGregorianCalendar v) throws Exception {
        return new Date(v.getYear() - 1900, v.getMonth(), v.getDay());
    }

    @SuppressWarnings("deprecation")
    @Override
    public XMLGregorianCalendar marshal(Date v) throws Exception {
        return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(1900 + v.getYear(), v.getMonth(), v.getDate(), DatatypeConstants.FIELD_UNDEFINED);
    }

}
