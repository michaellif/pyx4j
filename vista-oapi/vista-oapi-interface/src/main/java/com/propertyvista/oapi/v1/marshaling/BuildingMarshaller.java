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
package com.propertyvista.oapi.v1.marshaling;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.oapi.AbstractMarshaller;
import com.propertyvista.oapi.ServiceType;
import com.propertyvista.oapi.v1.model.BuildingIO;
import com.propertyvista.oapi.v1.model.ParkingListIO;
import com.propertyvista.oapi.v1.model.types.BuildingTypeIO;
import com.propertyvista.oapi.v1.processing.AbstractProcessor;
import com.propertyvista.oapi.v1.service.PortationService;
import com.propertyvista.oapi.xml.Action;

public class BuildingMarshaller extends AbstractMarshaller<Building, BuildingIO> {

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
        buildingIO.propertyCode = getValue(building.propertyCode());

        buildingIO.address = AddressMarshaller.getInstance().marshal(building.info().address());
        buildingIO.buildingType = createIo(BuildingTypeIO.class, building.info().type());
        buildingIO.marketing = MarketingMarshaller.getInstance().marshal(building.marketing());

        Persistence.service().retrieve(building.contacts().propertyContacts());
        buildingIO.contacts = ContactMarshaller.getInstance().marshalCollection(building.contacts().propertyContacts());

        Persistence.ensureRetrieve(building.media(), AttachLevel.Attached);
        buildingIO.medias = MediaMarshaller.getInstance().marshalCollection(building.media());

        Persistence.service().retrieveMember(building.amenities());
        buildingIO.amenities = BuildingAmenityMarshaller.getInstance().marshalCollection(building.amenities());

        if (AbstractProcessor.getServiceType() != ServiceType.List || AbstractProcessor.getServiceClass() == PortationService.class) {
            Persistence.ensureRetrieve(building.parkings(), AttachLevel.Attached);
            buildingIO.parkings = ParkingMarshaller.getInstance().marshalCollection(building.parkings());
        } else {
            buildingIO.parkings = new ParkingListIO(Action.notAttached);
        }

        return buildingIO;
    }

    @Override
    public Building unmarshal(BuildingIO buildingIO) {
        Building building = EntityFactory.create(Building.class);
        building.propertyCode().setValue(buildingIO.propertyCode);

        set(building.info().address(), buildingIO.address, AddressMarshaller.getInstance());
        setValue(building.info().type(), buildingIO.buildingType);

        set(building.marketing(), buildingIO.marketing, MarketingMarshaller.getInstance());

        building.contacts().propertyContacts().addAll(ContactMarshaller.getInstance().unmarshalCollection(buildingIO.contacts));
        building.media().addAll(MediaMarshaller.getInstance().unmarshalCollection(buildingIO.medias));
        building.amenities().addAll(BuildingAmenityMarshaller.getInstance().unmarshalCollection(buildingIO.amenities));
        building.parkings().addAll(ParkingMarshaller.getInstance().unmarshalCollection(buildingIO.parkings));
        return building;
    }
}
