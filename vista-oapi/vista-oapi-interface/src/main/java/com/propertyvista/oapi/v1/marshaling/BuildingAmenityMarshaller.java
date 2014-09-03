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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.property.asset.building.BuildingAmenity;
import com.propertyvista.oapi.AbstractMarshaller;
import com.propertyvista.oapi.v1.model.BuildingAmenityIO;
import com.propertyvista.oapi.v1.model.BuildingAmenityListIO;
import com.propertyvista.oapi.v1.model.types.BuildingAmenityTypeIO;
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
    public BuildingAmenityIO marshal(BuildingAmenity amenity) {
        if (amenity == null || amenity.isNull()) {
            return null;
        }
        BuildingAmenityIO amenityIO = new BuildingAmenityIO();

        amenityIO.name = getValue(amenity.name());
        amenityIO.description = createIo(StringIO.class, amenity.description());
        amenityIO.type = createIo(BuildingAmenityTypeIO.class, amenity.type());
        return amenityIO;
    }

    public BuildingAmenityListIO marshalCollection(Collection<BuildingAmenity> amenity) {
        BuildingAmenityListIO ioList = new BuildingAmenityListIO();
        for (BuildingAmenity item : amenity) {
            ioList.add(marshal(item));
        }
        return ioList;
    }

    @Override
    public BuildingAmenity unmarshal(BuildingAmenityIO amenityIO) {
        BuildingAmenity amenity = EntityFactory.create(BuildingAmenity.class);
        amenity.name().setValue(amenityIO.name);
        setValue(amenity.description(), amenityIO.description);
        setValue(amenity.type(), amenityIO.type);
        return amenity;
    }

    public List<BuildingAmenity> unmarshalCollection(BuildingAmenityListIO listIO) {
        List<BuildingAmenity> list = new ArrayList<BuildingAmenity>();
        for (BuildingAmenityIO ioItem : listIO.getList()) {
            list.add(unmarshal(ioItem));
        }
        return list;
    }
}
