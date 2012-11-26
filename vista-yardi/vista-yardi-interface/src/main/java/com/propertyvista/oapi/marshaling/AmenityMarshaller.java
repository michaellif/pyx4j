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

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.marketing.yield.Amenity;
import com.propertyvista.oapi.model.AmenityIO;
import com.propertyvista.oapi.xml.StringIO;

public class AmenityMarshaller implements Marshaller<Amenity, AmenityIO> {

    private static class SingletonHolder {
        public static final AmenityMarshaller INSTANCE = new AmenityMarshaller();
    }

    private AmenityMarshaller() {
    }

    public static AmenityMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public AmenityIO unmarshal(Amenity amenity) {
        AmenityIO amenityIO = new AmenityIO();
        amenityIO.name = new StringIO(amenity.name().getValue());
        amenityIO.description = new StringIO(amenity.description().getValue());
        return amenityIO;
    }

    @Override
    public Amenity marshal(AmenityIO amenityIO) throws Exception {
        Amenity amenity = EntityFactory.create(Amenity.class);
        amenity.name().setValue(amenityIO.name.value);
        amenity.description().setValue(amenityIO.description.value);
        return amenity;
    }
}
