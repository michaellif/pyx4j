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
 * @author dmitry
 */
package com.propertyvista.oapi.model;

import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.domain.property.asset.building.BuildingInfo.ConstructionType;
import com.propertyvista.domain.property.asset.building.BuildingInfo.FloorType;
import com.propertyvista.domain.property.asset.building.BuildingInfo.FoundationType;
import com.propertyvista.domain.property.asset.building.BuildingInfo.WaterSupply;

public class BuildingInfoIO {

    public enum BuildingType {

        agricultural,

        commercial,

        mixed_residential,

        residential,

        industrial,

        military,

        parking_storage,

        other;

    }

    //mandatory for portal
    public String name;

    public String description;

    public BuildingType buildingType;

    //mandatory for portal
    public AddressIO address;

    public String shape;

    public String totalStoreys;

    public String residentialStoreys;

    public BuildingInfo.StructureType structureType;

    // TODO this can later be date
//    @XmlSchemaType(name = "date")
//    @XmlElement(required = true)
//    @XmlJavaTypeAdapter(LogicalDateXmlAdapter.class)
//    public Date structureBuildYear;
    public String structureBuildYear;

    public ConstructionType constructionType;

    public FoundationType foundationType;

    public FloorType floorType;

    public String landArea;

    public WaterSupply waterSupply;

    public Boolean centralAir;

    public Boolean centralHeat;

    public Integer unitCount;

}
