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

import com.pyx4j.entity.shared.EntityFactory;

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
        BuildingAmenityIO amenityIO = new BuildingAmenityIO();
        amenityIO.name = new StringIO(amenity.name().getValue());
        amenityIO.description = new StringIO(amenity.description().getValue());
        amenityIO.type = new BuildingAmenityTypeIO(amenity.type().getValue());
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
    public BuildingAmenity unmarshal(BuildingAmenityIO amenityIO) throws Exception {
        BuildingAmenity amenity = EntityFactory.create(BuildingAmenity.class);
        amenity.name().setValue(amenityIO.name.value);
        amenity.description().setValue(amenityIO.description.value);
        if (amenityIO.type != null) {
            amenity.type().setValue(amenityIO.type.value);
        }
        return amenity;
    }

    public List<BuildingAmenity> unmarshal(Collection<BuildingAmenityIO> amenityIOList) throws Exception {
        List<BuildingAmenity> amenities = new ArrayList<BuildingAmenity>();
        for (BuildingAmenityIO amenityIO : amenityIOList) {
            amenities.add(unmarshal(amenityIO));
        }
        return amenities;
    }
}
