/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 13, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import com.propertyvista.oapi.xml.AbstractElementIO;
import com.propertyvista.oapi.xml.StringIO;

@XmlRootElement(name = "building")
public class BuildingIO extends AbstractElementIO {

    //mandatory for portal, unique identifier
    @XmlAttribute
    public String propertyCode;

    public StringIO newPropertyCode;

    public BuildingInfoIO info;

    public MarketingIO marketing;

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "amenity"))
    public List<BuildingAmenityIO> amenities = new ArrayList<BuildingAmenityIO>();

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "utility"))
    public List<UtilityIO> includedUtilities = new ArrayList<UtilityIO>();

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "parking"))
    public List<ParkingIO> parkings = new ArrayList<ParkingIO>();

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "contact"))
    public List<ContactIO> contacts = new ArrayList<ContactIO>();

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "media"))
    public List<MediaImageIO> medias = new ArrayList<MediaImageIO>();

    //mandatory for portal
    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "unit"))
    public List<UnitIO> units = new ArrayList<UnitIO>();

    public BuildingIO() {
    }

    public BuildingIO(String propertyCode) {
        this.propertyCode = propertyCode;
    }

    @Override
    public boolean equals(Object obj) {
        return propertyCode == ((BuildingIO) obj).propertyCode;
    }

    @Override
    public int hashCode() {
        return propertyCode.hashCode();
    }

}
