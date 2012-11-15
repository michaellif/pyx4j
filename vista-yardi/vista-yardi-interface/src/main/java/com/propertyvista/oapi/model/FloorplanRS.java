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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.xml.LogicalDateXmlAdapter;

public class FloorplanRS {

    //mandatory for portal
    public String name;

    //mandatory for portal
    public String marketingName;

    public String description;

    public Integer floorCount;

    public Integer unitCount;

    //mandatory for portal
    public Integer bedrooms;

    public Integer dens;

    //mandatory for portal
    public Integer bathrooms;

    public Integer halfBath;

    public BigDecimal rentFrom;

    public BigDecimal rentTo;

    public Integer sqftFrom;

    public Integer sqftTo;

    @XmlSchemaType(name = "date")
    @XmlJavaTypeAdapter(LogicalDateXmlAdapter.class)
    public LogicalDate availableFrom;

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "amenity", type = AmenityRS.class))
    public List<AmenityRS> amenities = new ArrayList<AmenityRS>();

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "media", type = MediaRS.class))
    public List<MediaRS> medias = new ArrayList<MediaRS>();

    //mandatory for portal
    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "aptUnit", type = AptUnitRS.class))
    public List<AptUnitRS> units = new ArrayList<AptUnitRS>();

}
