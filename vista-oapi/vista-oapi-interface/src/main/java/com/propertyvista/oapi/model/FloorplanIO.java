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
package com.propertyvista.oapi.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;

import com.propertyvista.oapi.xml.AbstractElementIO;
import com.propertyvista.oapi.xml.BigDecimalIO;
import com.propertyvista.oapi.xml.IntegerIO;
import com.propertyvista.oapi.xml.LogicalDateIO;
import com.propertyvista.oapi.xml.StringIO;

public class FloorplanIO extends AbstractElementIO {

    //mandatory for portal
    @XmlAttribute
    public String name;

    public String newName;

    //mandatory for portal
    public StringIO marketingName;

    public StringIO description;

    public IntegerIO floorCount;

    public IntegerIO unitCount;

    //mandatory for portal
    public IntegerIO bedrooms;

    public IntegerIO dens;

    //mandatory for portal
    public IntegerIO bathrooms;

    public IntegerIO halfBath;

    public BigDecimalIO rentFrom;

    public BigDecimalIO rentTo;

    public IntegerIO sqftFrom;

    public IntegerIO sqftTo;

    public LogicalDateIO availableFrom;

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "amenity", type = FloorplanAmenityIO.class))
    public List<FloorplanAmenityIO> amenities = new ArrayList<FloorplanAmenityIO>();

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "media", type = MediaImageIO.class))
    public List<MediaImageIO> medias = new ArrayList<MediaImageIO>();

    @Override
    public boolean equals(Object obj) {
        return name == ((FloorplanIO) obj).name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

}
