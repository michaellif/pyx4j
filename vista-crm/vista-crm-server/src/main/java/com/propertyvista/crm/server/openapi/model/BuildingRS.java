/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 27, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.crm.server.openapi.model;

import java.util.ArrayList;
import java.util.List;

public class BuildingRS {

    public String propertyCode;

    public BuildingInfoRS info;

    public MarketingRS marketing;

    public List<AmenityRS> amenities = new ArrayList<AmenityRS>();

    public Double rentFrom;

    public Double rentTo;

    public Double sqftFrom;

    public Double sqftTo;

    public Integer unitCount;

    public FloorplansRS floorplans = new FloorplansRS();

}
