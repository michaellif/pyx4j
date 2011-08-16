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
package com.propertyvista.crm.server.openapi.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;

public class FloorplanRS {

    public String name;

    public String description;

    public Integer floorCount;

    public Double bedrooms;

    public Double bathrooms;

    public Double rentFrom;

    public Double rentTo;

    public Double sqftFrom;

    public Double sqftTo;

    public Date availableFrom;

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "amenity", type = AmenityRS.class))
    public List<AmenityRS> amenities = new ArrayList<AmenityRS>();

    @XmlElementWrapper
    @XmlElements(@XmlElement(name = "media", type = MediaRS.class))
    public List<MediaRS> medias = new ArrayList<MediaRS>();
}
