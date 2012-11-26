/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 16, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.marketing.yield.Amenity;
import com.propertyvista.domain.media.Media;
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.PropertyContact.PropertyContactType;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.oapi.model.AmenityIO;
import com.propertyvista.oapi.model.BuildingIO;
import com.propertyvista.oapi.model.ContactIO;
import com.propertyvista.oapi.model.MediaIO;
import com.propertyvista.oapi.model.ParkingIO;
import com.propertyvista.oapi.model.UtilityIO;

public class BuildingMarshaller implements Marshaller<Building, BuildingIO> {

    @Override
    public BuildingIO unmarshal(Building building) {
        BuildingIO buildingIO = new BuildingIO();
        buildingIO.propertyCode = building.propertyCode().getValue();
        buildingIO.info = new BuildingInfoMarshaller().unmarshal(building.info());
        buildingIO.marketing = new MarketingMarshaller().unmarshal(building.marketing());

        List<ContactIO> contacts = new ArrayList<ContactIO>();
        Persistence.service().retrieve(building.contacts().propertyContacts());
        for (PropertyContact contact : building.contacts().propertyContacts()) {
            if (EnumSet.of(PropertyContactType.mainOffice, PropertyContactType.pointOfSale).contains(contact.type().getValue())) {
                contacts.add(new ContactMarshaller().unmarshal(contact));
            }
        }
        buildingIO.contacts.addAll(contacts);

        List<MediaIO> medias = new ArrayList<MediaIO>();
        for (Media media : building.media()) {
            medias.add(new MediaMarshaller().unmarshal(media));
        }
        buildingIO.medias = medias;

        List<AmenityIO> amenities = new ArrayList<AmenityIO>();
        for (Amenity amenity : building.amenities()) {
            amenities.add(new AmenityMarshaller().unmarshal(amenity));
        }
        buildingIO.amenities = amenities;

        List<ParkingIO> parkings = new ArrayList<ParkingIO>();
        for (Parking parking : building._Parkings()) {
            parkings.add(new ParkingMarshaller().unmarshal(parking));
        }
        buildingIO.parkings = parkings;

        List<UtilityIO> utilities = new ArrayList<UtilityIO>();
        for (Utility utility : building.includedUtilities()) {
            utilities.add(new UtilityMarshaller().unmarshal(utility));
        }
        buildingIO.includedUtilities = utilities;

        return buildingIO;
    }

    @Override
    public Building marshal(BuildingIO buildingIO) throws Exception {
        Building building = EntityFactory.create(Building.class);
        building.propertyCode().setValue(buildingIO.propertyCode);
        building.info().set(new BuildingInfoMarshaller().marshal(buildingIO.info));
        building.marketing().set(new MarketingMarshaller().marshal(buildingIO.marketing));

        return building;
    }
}
