/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 28, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.oapi.v1.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.propertyvista.oapi.xml.AbstractElementIO;
import com.propertyvista.oapi.xml.IntegerIO;
import com.propertyvista.oapi.xml.StringIO;

@XmlRootElement(name = "unit")
public class UnitIO extends AbstractElementIO {

    @XmlAttribute
    public String propertyCode;

    //mandatory for portal
    @XmlAttribute
    public String number;

    public StringIO newNumber;

    public StringIO floorplanName;

    public IntegerIO beds;

    public IntegerIO baths;

    @Override
    public boolean equals(Object obj) {
        return (propertyCode == ((UnitIO) obj).propertyCode && number == ((UnitIO) obj).number);
    }

    @Override
    public int hashCode() {
        return (propertyCode + " " + number).hashCode();
    }

}
