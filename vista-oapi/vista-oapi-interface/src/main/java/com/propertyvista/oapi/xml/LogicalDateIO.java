/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.xml.LogicalDateXmlAdapter;

@XmlType(name = "LogicalDateBase")
public class LogicalDateIO implements PrimitiveIO<LogicalDate> {

    private LogicalDate value;

    private Note note;

    public LogicalDateIO() {
    }

    public LogicalDateIO(LogicalDate value) {
        setValue(value);
    }

    @XmlAttribute
    @Override
    public Note getNote() {
        return note;
    }

    @Override
    public void setNote(Note note) {
        this.note = note;
    }

    @Override
    @XmlValue
    @XmlSchemaType(name = "date")
    @XmlJavaTypeAdapter(LogicalDateXmlAdapter.class)
    public LogicalDate getValue() {
        return value;
    }

    @Override
    public void setValue(LogicalDate value) {
        this.value = value;
    }
}
