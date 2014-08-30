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
package com.propertyvista.oapi.v1.model;

import com.propertyvista.oapi.v1.model.types.BuildingStructureTypeIO;
import com.propertyvista.oapi.v1.model.types.BuildingTypeIO;
import com.propertyvista.oapi.v1.model.types.ConstructionTypeIO;
import com.propertyvista.oapi.v1.model.types.FloorTypeIO;
import com.propertyvista.oapi.v1.model.types.FoundationTypeIO;
import com.propertyvista.oapi.v1.model.types.WaterSupplyTypeIO;
import com.propertyvista.oapi.xml.AbstractElementIO;
import com.propertyvista.oapi.xml.BooleanIO;
import com.propertyvista.oapi.xml.IntegerIO;
import com.propertyvista.oapi.xml.StringIO;

public class BuildingInfoIO extends AbstractElementIO {

    //mandatory for portal
    public StringIO name;

    public StringIO description;

    public BuildingTypeIO buildingType;

    public BuildingStructureTypeIO structureType;

    //mandatory for portal
    public AddressIO address;

    public StringIO shape;

    public StringIO totalStoreys;

    public StringIO residentialStoreys;

    // TODO this can later be date
//    @XmlSchemaType(name = "date")
//    @XmlElement(required = true)
//    @XmlJavaTypeAdapter(LogicalDateXmlAdapter.class)
//    public Date structureBuildYear;
    public StringIO structureBuildYear;

    public ConstructionTypeIO constructionType;

    public FoundationTypeIO foundationType;

    public FloorTypeIO floorType;

    public StringIO landArea;

    public WaterSupplyTypeIO waterSupply;

    public BooleanIO centralAir;

    public BooleanIO centralHeat;

    public IntegerIO unitCount;

}
