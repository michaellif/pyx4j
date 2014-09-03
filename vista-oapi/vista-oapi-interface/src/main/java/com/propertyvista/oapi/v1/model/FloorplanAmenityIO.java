/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 12, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.v1.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.propertyvista.oapi.v1.model.types.FloorplanAmenityTypeIO;
import com.propertyvista.oapi.xml.AbstractElementIO;
import com.propertyvista.oapi.xml.StringIO;

@XmlType(name = "FloorplanAmenity")
public class FloorplanAmenityIO extends AbstractElementIO {

    @XmlAttribute
    public String name;

    public StringIO newName;

    public FloorplanAmenityTypeIO type;

    public StringIO description;

    @Override
    public boolean equals(Object obj) {
        return name == ((FloorplanAmenityIO) obj).name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
