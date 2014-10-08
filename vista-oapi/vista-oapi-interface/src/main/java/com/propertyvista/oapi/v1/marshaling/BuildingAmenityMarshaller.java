/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 24, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.v1.marshaling;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.oapi.AbstractMarshaller;
import com.propertyvista.oapi.v1.model.BuildingAmenityIO;
import com.propertyvista.oapi.v1.model.types.BuildingAmenityTypeIO;
import com.propertyvista.oapi.v1.processing.AbstractProcessor;
import com.propertyvista.oapi.v1.service.PortationService;
import com.propertyvista.oapi.xml.Note;
import com.propertyvista.oapi.xml.StringIO;

public class BuildingAmenityMarshaller extends AbstractMarshaller<BuildingAmenity, BuildingAmenityIO> {

    private static class SingletonHolder {
        public static final BuildingAmenityMarshaller INSTANCE = new BuildingAmenityMarshaller();
    }

    private BuildingAmenityMarshaller() {
    }

    public static BuildingAmenityMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    protected BuildingAmenityIO marshal(BuildingAmenity amenity) {
        if (amenity == null || amenity.isNull()) {
            return null;
        }
        BuildingAmenityIO amenityIO = new BuildingAmenityIO();

        amenityIO.name = getValue(amenity.name());
        if (AbstractProcessor.getServiceClass() == PortationService.class || !getContext().hasParentCollectionOf(Building.class)) {
            amenityIO.description = createIo(StringIO.class, amenity.description());
            amenityIO.type = createIo(BuildingAmenityTypeIO.class, amenity.type());
        } else {
            amenityIO.setNote(Note.contentDetached);
        }
        return amenityIO;
    }

    @Override
    protected BuildingAmenity unmarshal(BuildingAmenityIO amenityIO) {
        BuildingAmenity amenity = EntityFactory.create(BuildingAmenity.class);
        amenity.name().setValue(amenityIO.name);
        setValue(amenity.description(), amenityIO.description);
        setValue(amenity.type(), amenityIO.type);
        return amenity;
    }
}
