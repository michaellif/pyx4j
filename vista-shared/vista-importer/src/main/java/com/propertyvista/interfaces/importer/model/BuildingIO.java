/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.interfaces.importer.model;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IPrimitive;

import com.propertyvista.domain.property.asset.building.BuildingInfo.ConstructionType;
import com.propertyvista.domain.property.asset.building.BuildingInfo.FloorType;
import com.propertyvista.domain.property.asset.building.BuildingInfo.FoundationType;
import com.propertyvista.domain.property.asset.building.BuildingInfo.Shape;
import com.propertyvista.domain.property.asset.building.BuildingInfo.StructureType;
import com.propertyvista.domain.property.asset.building.BuildingInfo.Type;
import com.propertyvista.domain.property.asset.building.BuildingInfo.WaterSupply;

public interface BuildingIO extends IEntity {

    IPrimitive<String> propertyCode();

    IPrimitive<String> legalName();

    AddressIO address();

    IPrimitive<String> totalStoreys();

    IPrimitive<String> residentialStoreys();

    IPrimitive<Type> type();

    IPrimitive<Shape> shape();

    IPrimitive<StructureType> structureType();

    IPrimitive<LogicalDate> structureBuildYear();

    IPrimitive<ConstructionType> constructionType();

    IPrimitive<FoundationType> foundationType();

    IPrimitive<FloorType> floorType();

    IPrimitive<String> landArea();

    IPrimitive<WaterSupply> waterSupply();

    IPrimitive<Boolean> centralAir();

    IPrimitive<Boolean> centralHeat();

    IList<ContactIO> contacts();

    IList<ParkingIO> parkings();

    IList<FloorplanIO> floorplans();

    MarketingIO marketing();

    IList<AmenityIO> amenities();

    IList<MediaIO> medias();

}
