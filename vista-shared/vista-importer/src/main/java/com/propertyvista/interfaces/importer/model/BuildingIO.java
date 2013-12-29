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

import javax.xml.bind.annotation.XmlTransient;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.annotations.ToString;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.geo.GeoPoint;
import com.pyx4j.i18n.annotations.I18n;

import com.propertyvista.domain.property.asset.Boiler;
import com.propertyvista.domain.property.asset.Elevator;
import com.propertyvista.domain.property.asset.LockerArea;
import com.propertyvista.domain.property.asset.Roof;
import com.propertyvista.domain.property.asset.building.BuildingInfo.ConstructionType;
import com.propertyvista.domain.property.asset.building.BuildingInfo.FloorType;
import com.propertyvista.domain.property.asset.building.BuildingInfo.FoundationType;
import com.propertyvista.domain.property.asset.building.BuildingInfo.Shape;
import com.propertyvista.domain.property.asset.building.BuildingInfo.StructureType;
import com.propertyvista.domain.property.asset.building.BuildingInfo.Type;
import com.propertyvista.domain.property.asset.building.BuildingInfo.WaterSupply;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreAll)
public interface BuildingIO extends IEntity {

    @XmlTransient
    ImportInformation _import();

    @ToString
    IPrimitive<String> propertyCode();

    @ToString
    IPrimitive<String> externalId();

    IPrimitive<String> legalName();

    IPrimitive<String> website();

    IPrimitive<String> email();

    IList<PropertyPhoneIO> phones();

    IPrimitive<GeoPoint> addressCoordinates();

    IPrimitive<String> propertyManager();

    IPrimitive<String> complexName();

    IPrimitive<Boolean> complexPrimary();

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

    IList<BuildingAmenityIO> amenities();

    IList<UtilityIO> includedUtilities();

    IList<UtilityIO> externalUtilities();

    IList<MediaIO> medias();

    // Other Data

    IList<Elevator> elevators();

    IList<Boiler> boilers();

    IList<Roof> roofs();

    IList<LockerArea> lockerAreas();

    IList<AptUnitIO> units();

}
