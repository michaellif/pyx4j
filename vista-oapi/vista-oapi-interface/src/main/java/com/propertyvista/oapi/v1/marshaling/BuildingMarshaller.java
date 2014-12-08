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
import com.propertyvista.oapi.v1.model.BuildingAmenityListIO;
import com.propertyvista.oapi.v1.model.BuildingIO;
import com.propertyvista.oapi.v1.model.ContactListIO;
import com.propertyvista.oapi.v1.model.FloorplanListIO;
import com.propertyvista.oapi.v1.model.LeaseListIO;
import com.propertyvista.oapi.v1.model.MediaImageListIO;
import com.propertyvista.oapi.v1.model.ParkingListIO;
import com.propertyvista.oapi.v1.model.UnitListIO;
import com.propertyvista.oapi.v1.model.types.BuildingTypeIO;
import com.propertyvista.oapi.v1.processing.AbstractProcessor;
import com.propertyvista.oapi.v1.service.MarketingService;
import com.propertyvista.oapi.v1.service.PortationService;
import com.propertyvista.oapi.xml.Note;
import com.propertyvista.oapi.xml.StringIO;

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
    protected BuildingIO marshal(Building building) {
        if (building == null || building.isNull()) {
            return null;
        }
        BuildingIO buildingIO = new BuildingIO();
        buildingIO.propertyCode = getValue(building.propertyCode());

        buildingIO.name = createIo(StringIO.class, building.info().name());

        buildingIO.address = AddressMarshaller.getInstance().marshalItem(building.info().address());
        buildingIO.buildingType = createIo(BuildingTypeIO.class, building.info().type());
        buildingIO.marketing = MarketingMarshaller.getInstance().marshalItem(building.marketing());

        Persistence.service().retrieve(building.contacts().propertyContacts());
        buildingIO.contacts = ContactMarshaller.getInstance().marshalCollection(ContactListIO.class, building.contacts().propertyContacts());

        Persistence.ensureRetrieve(building.media(), AttachLevel.Attached);
        buildingIO.medias = MediaMarshaller.getInstance().marshalCollection(MediaImageListIO.class, building.media());

        Persistence.service().retrieveMember(building.amenities());
        buildingIO.amenities = BuildingAmenityMarshaller.getInstance().marshalCollection(BuildingAmenityListIO.class, building.amenities());

        if (AbstractProcessor.getServiceClass() == PortationService.class || !getContext().isCollectionContext()) {
            Persistence.ensureRetrieve(building.parkings(), AttachLevel.Attached);
            buildingIO.parkings = ParkingMarshaller.getInstance().marshalCollection(ParkingListIO.class, building.parkings());
        } else {
            buildingIO.parkings = new ParkingListIO(Note.contentDetached);
        }

        Persistence.ensureRetrieve(building.floorplans(), AttachLevel.Attached);
        buildingIO.floorplans = FloorplanMarshaller.getInstance().marshalCollection(FloorplanListIO.class, building.floorplans());

        if (AbstractProcessor.getServiceClass() == MarketingService.class) {
            buildingIO.units = new UnitListIO(Note.contentDetached);
        } else if (AbstractProcessor.getServiceClass() == PortationService.class || !getContext().isCollectionContext()) {
            Persistence.ensureRetrieve(building.units(), AttachLevel.Attached);
            buildingIO.units = UnitMarshaller.getInstance().marshalCollection(UnitListIO.class, building.units());
        } else {
            buildingIO.units = new UnitListIO(Note.contentDetached);
        }

        buildingIO.leases = new LeaseListIO(Note.contentDetached);
        return buildingIO;
    }

    @Override
    protected Building unmarshal(BuildingIO buildingIO) {
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
