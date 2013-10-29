/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 4, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.yardi.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.mits.Identification;
import com.yardi.entity.mits.PropertyIDType;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.property.asset.building.Building;

/**
 * Maps buildings information from YARDI System to domain entities.
 * 
 * @author Mykola
 * 
 */
public class BuildingsMapper {

    private final static Logger log = LoggerFactory.getLogger(BuildingsMapper.class);

    /**
     * Maps property from YARDI System to building
     * 
     * @param property
     *            the property to map
     * @return the building
     */
    public Building map(PropertyIDType propertyID) {
        Building building = EntityFactory.create(Building.class);

        Identification identification = propertyID.getIdentification();
        building.propertyCode().setValue(identification.getPrimaryID());
        building.marketing().name().setValue(identification.getMarketingName());

        return building;
    }
}
