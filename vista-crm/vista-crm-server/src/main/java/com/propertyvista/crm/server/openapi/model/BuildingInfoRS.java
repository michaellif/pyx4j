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
package com.propertyvista.crm.server.openapi.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.propertyvista.crm.server.openapi.model.util.LogicalDateXmlAdapter;
import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.domain.property.asset.building.BuildingInfo.ConstructionType;
import com.propertyvista.domain.property.asset.building.BuildingInfo.FloorType;
import com.propertyvista.domain.property.asset.building.BuildingInfo.FoundationType;
import com.propertyvista.domain.property.asset.building.BuildingInfo.WaterSupply;

public class BuildingInfoRS {

    public String name;

    public BuildingInfo.Type type;

    @XmlElement(name = "address")
    public AddressRS address;

    public BuildingInfo.Shape shape;

    public String totalStories;

    public String residentialStories;

    public BuildingInfo.StructureType structureType;

    @XmlSchemaType(name = "date")
    @XmlElement(required = true)
    @XmlJavaTypeAdapter(LogicalDateXmlAdapter.class)
    public Date structureBuildYear;

    public ConstructionType constructionType;

    public FoundationType foundationType;

    public FloorType floorType;

    public String landArea;

    public WaterSupply waterSupply;

    public Boolean centralAir;

    public Boolean centralHeat;
}
