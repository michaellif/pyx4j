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

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.oapi.model.BuildingIO;

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
    public BuildingIO marshal(Building building) {
        if (building == null || building.isNull()) {
            return null;
        }
        BuildingIO buildingIO = new BuildingIO();
        buildingIO.propertyCode = MarshallerUtils.getValue(building.propertyCode());

        buildingIO.info = BuildingInfoMarshaller.getInstance().marshal(building.info());
        buildingIO.marketing = MarketingMarshaller.getInstance().marshal(building.marketing());

        Persistence.service().retrieve(building.contacts().propertyContacts());
        buildingIO.contacts = ContactMarshaller.getInstance().marshal(building.contacts().propertyContacts());

        Persistence.ensureRetrieveMember(building.media(), AttachLevel.Attached);
        buildingIO.medias = MediaMarshaller.getInstance().marshal(building.media());

        Persistence.service().retrieveMember(building.amenities());
        buildingIO.amenities = BuildingAmenityMarshaller.getInstance().marshal(building.amenities());

        Persistence.service().retrieveMember(building.parkings());
        buildingIO.parkings = ParkingMarshaller.getInstance().marshal(building.parkings());

        return buildingIO;
    }

    @Override
    public Building unmarshal(BuildingIO buildingIO) {
        Building building = EntityFactory.create(Building.class);
        building.propertyCode().setValue(buildingIO.propertyCode);

        MarshallerUtils.set(building.info(), buildingIO.info, BuildingInfoMarshaller.getInstance());
        MarshallerUtils.set(building.marketing(), buildingIO.marketing, MarketingMarshaller.getInstance());

        building.contacts().propertyContacts().addAll(ContactMarshaller.getInstance().unmarshal(buildingIO.contacts));
        building.media().addAll(MediaMarshaller.getInstance().unmarshal(buildingIO.medias));
        building.amenities().addAll(BuildingAmenityMarshaller.getInstance().unmarshal(buildingIO.amenities));
        building.parkings().addAll(ParkingMarshaller.getInstance().unmarshal(buildingIO.parkings));
        return building;
    }
}
