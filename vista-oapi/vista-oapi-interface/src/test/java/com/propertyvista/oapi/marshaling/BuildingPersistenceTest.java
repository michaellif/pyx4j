/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 29, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.pmc.IntegrationSystem;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.domain.property.asset.building.BuildingInfo;
import com.propertyvista.oapi.binder.BuildingPersister;
import com.propertyvista.oapi.model.AddressIO;
import com.propertyvista.oapi.model.AdvertisingBlurbIO;
import com.propertyvista.oapi.model.BuildingAmenityIO;
import com.propertyvista.oapi.model.BuildingIO;
import com.propertyvista.oapi.model.BuildingInfoIO;
import com.propertyvista.oapi.model.ContactIO;
import com.propertyvista.oapi.model.MarketingIO;
import com.propertyvista.oapi.model.MediaImageIO;
import com.propertyvista.oapi.model.ParkingIO;
import com.propertyvista.oapi.model.UnitIO;
import com.propertyvista.oapi.model.types.BuildingAmenityTypeIO;
import com.propertyvista.oapi.model.types.BuildingTypeIO;
import com.propertyvista.oapi.model.types.ParkingTypeIO;
import com.propertyvista.oapi.model.types.StreetTypeIO;
import com.propertyvista.oapi.ws.WSOapiTestBase;
import com.propertyvista.oapi.xml.DoubleIO;
import com.propertyvista.oapi.xml.IntegerIO;
import com.propertyvista.oapi.xml.StringIO;

public class BuildingPersistenceTest extends WSOapiTestBase {

    private final static Logger log = LoggerFactory.getLogger(BuildingPersistenceTest.class);

    @Before
    public void init() throws Exception {
        preloadData();
    }

    @Test
    public void testNewBuildingPersistance() throws Exception {
        BuildingIO buildingIO = createBuilding();

        Building building = BuildingMarshaller.getInstance().unmarshal(buildingIO);
        building.integrationSystemId().setValue(IntegrationSystem.internal);

        log.debug("++++++++++ {}", building);

        new BuildingPersister().persist(building);

        building = new BuildingPersister().retrieve(building);

        log.debug("++++++++++ {}", building);

        BuildingIO buildingIO2 = BuildingMarshaller.getInstance().marshal(building);

        assertEquals(buildingIO.propertyCode, buildingIO2.propertyCode);

        // address
        assertEquals(buildingIO.info.address.city.getValue(), buildingIO2.info.address.city.getValue());
        assertEquals(buildingIO.info.address.country.getValue(), buildingIO2.info.address.country.getValue());
        assertEquals(buildingIO.info.address.postalCode.getValue(), buildingIO2.info.address.postalCode.getValue());
        assertEquals(buildingIO.info.address.province.getValue(), buildingIO2.info.address.province.getValue());
        assertEquals(buildingIO.info.address.streetName.getValue(), buildingIO2.info.address.streetName.getValue());
        assertEquals(buildingIO.info.address.streetNumber.getValue(), buildingIO2.info.address.streetNumber.getValue());
        assertEquals(buildingIO.info.address.streetType.getValue(), buildingIO2.info.address.streetType.getValue());
        assertEquals(buildingIO.info.buildingType.getValue(), buildingIO2.info.buildingType.getValue());

        // marketing
        assertEquals(buildingIO.marketing.name, buildingIO2.marketing.name);
        assertEquals(buildingIO.marketing.description.getValue(), buildingIO2.marketing.description.getValue());

        // amenities
        for (int i = 0; i < buildingIO.amenities.size(); i++) {
            assertEquals(buildingIO.amenities.get(i).name, buildingIO2.amenities.get(i).name);
            assertEquals(buildingIO.amenities.get(i).description.getValue(), buildingIO2.amenities.get(i).description.getValue());
            assertEquals(buildingIO.amenities.get(i).type.getValue(), buildingIO2.amenities.get(i).type.getValue());
        }

        // parkings
        for (int i = 0; i < buildingIO.parkings.size(); i++) {
            ParkingIO parking = buildingIO2.parkings.get(buildingIO2.parkings.indexOf(buildingIO.parkings.get(i)));
            assertEquals(buildingIO.parkings.get(i).name, parking.name);
            assertEquals(buildingIO.parkings.get(i).description.getValue(), parking.description.getValue());
            assertEquals(buildingIO.parkings.get(i).levels.getValue(), parking.levels.getValue());
            assertEquals(buildingIO.parkings.get(i).type.getValue(), parking.type.getValue());

        }

        // contacts
        for (int i = 0; i < buildingIO.contacts.size(); i++) {
            assertEquals(buildingIO.contacts.get(i).name, buildingIO2.contacts.get(i).name);
            assertEquals(buildingIO.contacts.get(i).email.getValue(), buildingIO2.contacts.get(i).email.getValue());
            assertEquals(buildingIO.contacts.get(i).phone.getValue(), buildingIO2.contacts.get(i).phone.getValue());
        }

        // medias
        for (int i = 0; i < buildingIO.medias.size(); i++) {
            assertEquals(buildingIO.medias.get(i).caption.getValue(), buildingIO2.medias.get(i).caption.getValue());
        }

        // units
        for (int i = 0; i < buildingIO.units.size(); i++) {
            assertEquals(buildingIO.units.get(i).number, buildingIO2.units.get(i).number);
            assertEquals(buildingIO.units.get(i).propertyCode, buildingIO2.units.get(i).propertyCode);
            assertEquals(buildingIO.units.get(i).baths.getValue(), buildingIO2.units.get(i).baths.getValue());
            assertEquals(buildingIO.units.get(i).beds.getValue(), buildingIO2.units.get(i).beds.getValue());
            assertEquals(buildingIO.units.get(i).floorplanName.getValue(), buildingIO2.units.get(i).floorplanName.getValue());
        }
    }

    public BuildingIO createBuilding() {

        // address
        AddressIO addressIO = new AddressIO();
        addressIO.city = new StringIO("Toronto");
        addressIO.country = new StringIO("Canada");
        addressIO.postalCode = new StringIO("M9A 4X9");
        addressIO.province = new StringIO("Ontario");
        addressIO.streetName = new StringIO("Bathurst");
        addressIO.streetNumber = new StringIO("255");
        addressIO.streetType = new StreetTypeIO(AddressStructured.StreetType.street);

        BuildingInfoIO info = new BuildingInfoIO();
        info.address = addressIO;
        info.buildingType = new BuildingTypeIO(BuildingInfo.Type.residential);
//        info.centralAir = new BooleanIO(true);
//        info.centralHeat = new BooleanIO(false);
//        info.constructionType = new ConstructionTypeIO(BuildingInfo.ConstructionType.panel);
//        info.description = new StringIO("building description");
//        info.floorType = new FloorTypeIO(BuildingInfo.FloorType.hardwood);
//        info.foundationType = new FoundationTypeIO(BuildingInfo.FoundationType.pile);
//        info.landArea = new StringIO("225 ft");
//        info.name = new StringIO("Test Properties");

        // marketing
        AdvertisingBlurbIO blurb1 = new AdvertisingBlurbIO(), blurb2 = new AdvertisingBlurbIO();
        blurb1.content = new StringIO("blurb content 1");
        blurb2.content = new StringIO("blurb content 2");

        MarketingIO marketing = new MarketingIO();
        marketing.name = "marketingName";
        marketing.description = new StringIO("lorem ipsum description");
        marketing.blurbs.add(blurb1);
        marketing.blurbs.add(blurb2);

        // amenities
        BuildingAmenityIO amenity1 = new BuildingAmenityIO();
        amenity1.description = new StringIO("amenity description");
        amenity1.name = "amenity name";
        amenity1.type = new BuildingAmenityTypeIO(BuildingAmenity.Type.elevator);

        BuildingAmenityIO amenity2 = new BuildingAmenityIO();
        amenity2.description = new StringIO("amenity description 2");
        amenity2.name = "amenity name 2";
        amenity2.type = new BuildingAmenityTypeIO(BuildingAmenity.Type.businessCenter);

        // utilities
//        UtilityIO utility1 = new UtilityIO();
//        utility1.name = "utility name";
//
//        UtilityIO utility2 = new UtilityIO();
//        utility2.name = "utility name 2";

        // parkings
        ParkingIO parking1 = new ParkingIO();
        parking1.name = "parking name";
        parking1.description = new StringIO("parking description");
        parking1.levels = new DoubleIO(3.0);
        parking1.type = new ParkingTypeIO(Parking.Type.surfaceLot);

        ParkingIO parking2 = new ParkingIO();
        parking2.name = "parking name 2";
        parking2.description = new StringIO("parking description 2");
        parking2.levels = new DoubleIO(2.0);
        parking2.type = new ParkingTypeIO(Parking.Type.garageLot);

        // contacts
        ContactIO contact1 = new ContactIO();
        contact1.email = new StringIO("john.smith@gmail.com");
        contact1.name = "John Smith";
        contact1.phone = new StringIO("123-123-1234");

        ContactIO contact2 = new ContactIO();
        contact2.email = new StringIO("bob.smith@gmail.com");
        contact2.name = "Bob Smith";
        contact2.phone = new StringIO("321-321-4321");

        // medias
        MediaImageIO media1 = new MediaImageIO();
        media1.caption = new StringIO("caption");

        MediaImageIO media2 = new MediaImageIO();
        media2.caption = new StringIO("caption 2");

        // units
        UnitIO unit1 = new UnitIO();
        unit1.propertyCode = "building1";
        unit1.number = "1";
        unit1.baths = new IntegerIO(1);
        unit1.beds = new IntegerIO(1);
        unit1.floorplanName = new StringIO("1bdrm");

        UnitIO unit2 = new UnitIO();
        unit2.propertyCode = "building1";
        unit2.number = "2";
        unit2.baths = new IntegerIO(2);
        unit2.beds = new IntegerIO(3);
        unit2.floorplanName = new StringIO("3bdrm");

        // building
        BuildingIO b = new BuildingIO("building1");
        b.info = info;
        b.marketing = marketing;
        b.amenities.add(amenity1);
        b.amenities.add(amenity2);
//        b.includedUtilities.add(utility1);
//        b.includedUtilities.add(utility2);
        b.parkings.add(parking1);
        b.parkings.add(parking2);
        b.contacts.add(contact1);
        b.contacts.add(contact2);
        b.medias.add(media1);
        b.medias.add(media2);
        return b;
    }

}
