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
public class BuildingRS {

    //mandatory for portal, unique identifier
    public String propertyCode;

    public BuildingInfoRS info;

    public String contactEmail;

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "phone"))
    @Deprecated
    public List<String> contactPhones = new ArrayList<String>();

    public MarketingRS marketing;

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "floorplan", type = FloorplanRS.class))
    public List<FloorplanRS> floorplans = new ArrayList<FloorplanRS>();

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "media", type = MediaRS.class))
    public List<MediaRS> medias = new ArrayList<MediaRS>();

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "amenity", type = AmenityRS.class))
    public List<AmenityRS> amenities = new ArrayList<AmenityRS>();

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "parking", type = ParkingRS.class))
    public List<ParkingRS> parkings = new ArrayList<ParkingRS>();

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "utility", type = UtilityRS.class))
    public List<UtilityRS> includedUtilities = new ArrayList<UtilityRS>();

    public BuildingRS() {
    }

    public BuildingRS(String propertyCode) {
        this.propertyCode = propertyCode;
    }

}
