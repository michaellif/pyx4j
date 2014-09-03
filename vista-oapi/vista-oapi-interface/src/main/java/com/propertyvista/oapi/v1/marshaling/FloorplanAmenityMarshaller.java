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

import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.oapi.AbstractMarshaller;
import com.propertyvista.oapi.ServiceType;
import com.propertyvista.oapi.v1.model.FloorplanAmenityIO;
import com.propertyvista.oapi.v1.model.types.FloorplanAmenityTypeIO;
import com.propertyvista.oapi.v1.processing.AbstractProcessor;
import com.propertyvista.oapi.v1.service.PortationService;
import com.propertyvista.oapi.xml.StringIO;

public class FloorplanAmenityMarshaller extends AbstractMarshaller<FloorplanAmenity, FloorplanAmenityIO> {

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
        if (amenity == null || amenity.isNull()) {
            return null;
        }
        FloorplanAmenityIO amenityIO = new FloorplanAmenityIO();

        amenityIO.name = getValue(amenity.name());
        if (AbstractProcessor.getServiceType() != ServiceType.List || AbstractProcessor.getServiceClass() == PortationService.class) {
            amenityIO.description = createIo(StringIO.class, amenity.description());
            amenityIO.type = createIo(FloorplanAmenityTypeIO.class, amenity.type());
        }
        return amenityIO;
    }

    @Override
    public FloorplanAmenity unmarshal(FloorplanAmenityIO amenityIO) {
        FloorplanAmenity amenity = EntityFactory.create(FloorplanAmenity.class);
        amenity.name().setValue(amenityIO.name);
        setValue(amenity.description(), amenityIO.description);
        setValue(amenity.type(), amenityIO.type);
        return amenity;
    }

}
