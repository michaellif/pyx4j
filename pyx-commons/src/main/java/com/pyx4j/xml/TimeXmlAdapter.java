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
 * Created on 2012-05-22
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.xml;

import java.sql.Time;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class TimeXmlAdapter extends XmlAdapter<XMLGregorianCalendar, java.sql.Time> {

    @SuppressWarnings("deprecation")
    @Override
    public java.sql.Time unmarshal(XMLGregorianCalendar v) throws Exception {
        return new java.sql.Time(v.getHour(), v.getMinute(), v.getSecond());
    }

    @SuppressWarnings("deprecation")
    @Override
    public XMLGregorianCalendar marshal(Time v) throws Exception {
        return DatatypeFactory.newInstance().newXMLGregorianCalendarTime(v.getHours(), v.getMinutes(), v.getSeconds(), DatatypeConstants.FIELD_UNDEFINED);
    }

}
