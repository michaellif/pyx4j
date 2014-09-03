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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import com.propertyvista.oapi.v1.model.types.BuildingStructureTypeIO;
import com.propertyvista.oapi.v1.model.types.BuildingTypeIO;
import com.propertyvista.oapi.v1.model.types.ConstructionTypeIO;
import com.propertyvista.oapi.v1.model.types.FloorTypeIO;
import com.propertyvista.oapi.v1.model.types.FoundationTypeIO;
import com.propertyvista.oapi.v1.model.types.WaterSupplyTypeIO;
import com.propertyvista.oapi.xml.AbstractElementIO;
import com.propertyvista.oapi.xml.BooleanIO;
import com.propertyvista.oapi.xml.IntegerIO;
import com.propertyvista.oapi.xml.ListIO;
import com.propertyvista.oapi.xml.StringIO;

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

    @XmlElementWrapper
    @XmlElement(name = "amenity")
    public ListIO<BuildingAmenityIO> amenities = new ListIO<BuildingAmenityIO>();

    @XmlElementWrapper
    @XmlElement(name = "utility")
    public ListIO<UtilityIO> includedUtilities = new ListIO<UtilityIO>();

    @XmlElementWrapper
    @XmlElement(name = "floorplan")
    public ListIO<FloorplanIO> floorplans = new ListIO<FloorplanIO>();

    @XmlElementWrapper
    @XmlElement(name = "unit")
    public ListIO<UnitIO> units = new ListIO<UnitIO>();

    @XmlElementWrapper
    @XmlElement(name = "parking")
    public ListIO<ParkingIO> parkings = new ListIO<ParkingIO>();

    @XmlElementWrapper
    @XmlElement(name = "contact")
    public ListIO<ContactIO> contacts = new ListIO<ContactIO>();

    @XmlElementWrapper
    @XmlElement(name = "media")
    public ListIO<MediaImageIO> medias = new ListIO<MediaImageIO>();

    @XmlElementWrapper
    @XmlElement(name = "lease")
    public ListIO<LeaseIO> leases = new ListIO<LeaseIO>();

    @Override
    public boolean equals(Object obj) {
        return propertyCode == ((BuildingIO) obj).propertyCode;
    }

    @Override
    public int hashCode() {
        return propertyCode.hashCode();
    }

}
