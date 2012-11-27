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

import com.propertyvista.domain.property.PropertyContact;
import com.propertyvista.domain.property.PropertyContact.PropertyContactType;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.oapi.model.BuildingIO;
import com.propertyvista.oapi.model.ContactIO;

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
        buildingIO.amenities = BuildingAmenityMarshaller.getInstance().unmarshal(building.amenities());

        Persistence.service().retrieveMember(building._Parkings());
        buildingIO.parkings = ParkingMarshaller.getInstance().unmarshal(building._Parkings());

        Persistence.service().retrieveMember(building.includedUtilities());
        buildingIO.includedUtilities = UtilityMarshaller.getInstance().unmarshal(building.includedUtilities());

        return buildingIO;
    }

    @Override
    public Building marshal(BuildingIO buildingIO) throws Exception {
        Building building = EntityFactory.create(Building.class);
        building.propertyCode().setValue(buildingIO.propertyCode);
        building.info().set(BuildingInfoMarshaller.getInstance().marshal(buildingIO.info));
        building.marketing().set(MarketingMarshaller.getInstance().marshal(buildingIO.marketing));
        building.contacts().propertyContacts().addAll(ContactMarshaller.getInstance().marshal(buildingIO.contacts));
        building.media().addAll(MediaMarshaller.getInstance().marshal(buildingIO.medias));
        building.amenities().addAll(BuildingAmenityMarshaller.getInstance().marshal(buildingIO.amenities));
        building._Parkings().addAll(ParkingMarshaller.getInstance().marshal(buildingIO.parkings));
        building.includedUtilities().addAll(UtilityMarshaller.getInstance().marshal(buildingIO.includedUtilities));
        return building;
    }
}
