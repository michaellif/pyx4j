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

import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.oapi.model.FloorplanAmenityIO;
import com.propertyvista.oapi.model.types.FloorplanAmenityTypeIO;
import com.propertyvista.oapi.xml.StringIO;

public class FloorplanAmenityMarshaller implements Marshaller<FloorplanAmenity, FloorplanAmenityIO> {

    private static class SingletonHolder {
        public static final FloorplanAmenityMarshaller INSTANCE = new FloorplanAmenityMarshaller();
    }

    private FloorplanAmenityMarshaller() {
    }

    public static FloorplanAmenityMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public FloorplanAmenityIO marshal(FloorplanAmenity amenity) {
        FloorplanAmenityIO amenityIO = new FloorplanAmenityIO();
        amenityIO.name = new StringIO(amenity.name().getValue());
        amenityIO.description = new StringIO(amenity.description().getValue());
        amenityIO.type = new FloorplanAmenityTypeIO(amenity.type().getValue());
        return amenityIO;
    }

    public List<FloorplanAmenityIO> marshal(Collection<FloorplanAmenity> amenities) {
        List<FloorplanAmenityIO> amenityIOList = new ArrayList<FloorplanAmenityIO>();
        for (FloorplanAmenity amenity : amenities) {
            amenityIOList.add(marshal(amenity));
        }
        return amenityIOList;
    }

    @Override
    public FloorplanAmenity unmarshal(FloorplanAmenityIO amenityIO) throws Exception {
        FloorplanAmenity amenity = EntityFactory.create(FloorplanAmenity.class);
        amenity.name().setValue(amenityIO.name.value);
        amenity.description().setValue(amenityIO.description.value);
        amenity.type().setValue(amenityIO.type.value);
        return amenity;
    }

    public List<FloorplanAmenity> unmarshal(Collection<FloorplanAmenityIO> amenityIOList) throws Exception {
        List<FloorplanAmenity> amenities = new ArrayList<FloorplanAmenity>();
        for (FloorplanAmenityIO amenityIO : amenityIOList) {
            amenities.add(unmarshal(amenityIO));
        }
        return amenities;
    }
}
