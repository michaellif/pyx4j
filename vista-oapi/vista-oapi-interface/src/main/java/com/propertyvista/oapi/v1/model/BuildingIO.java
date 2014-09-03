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
package com.propertyvista.oapi.v1.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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

@XmlType(name = "Building")
@XmlRootElement(name = "building")
public class BuildingIO extends AbstractElementIO {

    //mandatory for portal, unique identifier
    @XmlAttribute
    public String propertyCode;

    public StringIO newPropertyCode;

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

    public MarketingIO marketing;

    public BuildingIO() {
    }

    public BuildingIO(String propertyCode) {
        this.propertyCode = propertyCode;
    }

    public BuildingAmenityListIO amenities = new BuildingAmenityListIO();

    public UtilityListIO includedUtilities = new UtilityListIO();

    public FloorplanListIO floorplans = new FloorplanListIO();

    public UnitListIO units = new UnitListIO();

    public ParkingListIO parkings = new ParkingListIO();

    public ContactListIO contacts = new ContactListIO();

    public MediaImageListIO medias = new MediaImageListIO();

    public LeaseListIO leases = new LeaseListIO();

    @Override
    public boolean equals(Object obj) {
        return propertyCode == ((BuildingIO) obj).propertyCode;
    }

    @Override
    public int hashCode() {
        return propertyCode.hashCode();
    }

}
