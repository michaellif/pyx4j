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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "building")
public class BuildingIO {

    //mandatory for portal, unique identifier
    public String propertyCode;

    public BuildingInfoIO info;

    public List<ContactIO> contacts = new ArrayList<ContactIO>();

    public MarketingIO marketing;

    //mandatory for portal
    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "unit", type = UnitIO.class))
    public List<UnitIO> units = new ArrayList<UnitIO>();

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "media", type = MediaIO.class))
    public List<MediaIO> medias = new ArrayList<MediaIO>();

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "amenity", type = AmenityIO.class))
    public List<AmenityIO> amenities = new ArrayList<AmenityIO>();

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "parking", type = ParkingIO.class))
    public List<ParkingIO> parkings = new ArrayList<ParkingIO>();

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "utility", type = UtilityIO.class))
    public List<UtilityIO> includedUtilities = new ArrayList<UtilityIO>();

    public BuildingIO() {
    }

    public BuildingIO(String propertyCode) {
        this.propertyCode = propertyCode;
    }

}
