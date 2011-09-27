/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Aug 30, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.pyx4j.commons.LogicalDate;

public class LogicalDateXmlAdapter extends XmlAdapter<XMLGregorianCalendar, LogicalDate> {

    @Override
    public LogicalDate unmarshal(XMLGregorianCalendar v) throws Exception {
        return new LogicalDate(v.getYear() - 1900, v.getMonth() - 1, v.getDay());
    }

    @Override
    public XMLGregorianCalendar marshal(LogicalDate v) throws Exception {
        return DatatypeFactory.newInstance().newXMLGregorianCalendarDate(1900 + v.getYear(), v.getMonth() + 1, v.getDate(), DatatypeConstants.FIELD_UNDEFINED);
    }

}
