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
import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.PropertyContact.PropertyContactType;
import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.domain.property.asset.Utility;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.oapi.model.AmenityIO;
import com.propertyvista.oapi.model.BuildingIO;
import com.propertyvista.oapi.model.ContactIO;
import com.propertyvista.oapi.model.ParkingIO;
import com.propertyvista.oapi.model.UtilityIO;

public class BuildingMarshaller implements Marshaller<Building, BuildingIO> {

    private static class SingletonHolder {
        public static final BuildingMarshaller INSTANCE = new BuildingMarshaller();
    }

    private BuildingMarshaller() {
    }

    public static BuildingMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public BuildingIO unmarshal(Building building) {
        BuildingIO buildingIO = new BuildingIO();
        buildingIO.propertyCode = building.propertyCode().getValue();
        buildingIO.info = BuildingInfoMarshaller.getInstance().unmarshal(building.info());
        buildingIO.marketing = MarketingMarshaller.getInstance().unmarshal(building.marketing());

        List<ContactIO> contacts = new ArrayList<ContactIO>();
        Persistence.service().retrieve(building.contacts().propertyContacts());
        for (PropertyContact contact : building.contacts().propertyContacts()) {
            if (EnumSet.of(PropertyContactType.mainOffice, PropertyContactType.pointOfSale).contains(contact.type().getValue())) {
                contacts.add(ContactMarshaller.getInstance().unmarshal(contact));
            }
        }
        buildingIO.contacts.addAll(contacts);

        Persistence.service().retrieve(building.media());
        buildingIO.medias = MediaMarshaller.getInstance().unmarshal(building.media());

        Persistence.service().retrieveMember(building.amenities());
        List<AmenityIO> amenities = new ArrayList<AmenityIO>();
        for (Amenity amenity : building.amenities()) {
            amenities.add(AmenityMarshaller.getInstance().unmarshal(amenity));
        }
        buildingIO.amenities = amenities;

        Persistence.service().retrieveMember(building._Parkings());
        List<ParkingIO> parkings = new ArrayList<ParkingIO>();
        for (Parking parking : building._Parkings()) {
            parkings.add(ParkingMarshaller.getInstance().unmarshal(parking));
        }
        buildingIO.parkings = parkings;

        Persistence.service().retrieveMember(building.includedUtilities());
        List<UtilityIO> utilities = new ArrayList<UtilityIO>();
        for (Utility utility : building.includedUtilities()) {
            utilities.add(UtilityMarshaller.getInstance().unmarshal(utility));
        }
        buildingIO.includedUtilities = utilities;

        return buildingIO;
    }

    @Override
    public Building marshal(BuildingIO buildingIO) throws Exception {
        Building building = EntityFactory.create(Building.class);
        building.propertyCode().setValue(buildingIO.propertyCode);
        building.info().set(BuildingInfoMarshaller.getInstance().marshal(buildingIO.info));
        building.marketing().set(MarketingMarshaller.getInstance().marshal(buildingIO.marketing));

        return building;
    }
}
