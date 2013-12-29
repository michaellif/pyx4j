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
package com.propertyvista.oapi.marshaling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.oapi.model.BuildingAmenityIO;
import com.propertyvista.oapi.model.types.BuildingAmenityTypeIO;
import com.propertyvista.oapi.xml.StringIO;

public class BuildingAmenityMarshaller implements Marshaller<BuildingAmenity, BuildingAmenityIO> {

    private static class SingletonHolder {
        public static final BuildingAmenityMarshaller INSTANCE = new BuildingAmenityMarshaller();
    }

    private BuildingAmenityMarshaller() {
    }

    public static BuildingAmenityMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public BuildingAmenityIO marshal(BuildingAmenity amenity) {
        if (amenity == null || amenity.isNull()) {
            return null;
        }
        BuildingAmenityIO amenityIO = new BuildingAmenityIO();

        amenityIO.name = MarshallerUtils.getValue(amenity.name());
        amenityIO.description = MarshallerUtils.createIo(StringIO.class, amenity.description());
        amenityIO.type = MarshallerUtils.createIo(BuildingAmenityTypeIO.class, amenity.type());
        return amenityIO;
    }

    public List<BuildingAmenityIO> marshal(Collection<BuildingAmenity> amenities) {
        List<BuildingAmenityIO> amenityIOList = new ArrayList<BuildingAmenityIO>();
        for (BuildingAmenity amenity : amenities) {
            amenityIOList.add(marshal(amenity));
        }
        return amenityIOList;
    }

    @Override
    public BuildingAmenity unmarshal(BuildingAmenityIO amenityIO) {
        BuildingAmenity amenity = EntityFactory.create(BuildingAmenity.class);
        amenity.name().setValue(amenityIO.name);
        MarshallerUtils.setValue(amenity.description(), amenityIO.description);
        MarshallerUtils.setValue(amenity.type(), amenityIO.type);
        return amenity;
    }

    public List<BuildingAmenity> unmarshal(Collection<BuildingAmenityIO> amenityIOList) {
        List<BuildingAmenity> amenities = new ArrayList<BuildingAmenity>();
        for (BuildingAmenityIO amenityIO : amenityIOList) {
            BuildingAmenity amenity = EntityFactory.create(BuildingAmenity.class);

            MarshallerUtils.set(amenity, amenityIO, BuildingAmenityMarshaller.getInstance());
            amenities.add(amenity);
        }
        return amenities;
    }
}
