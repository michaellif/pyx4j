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

import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.oapi.model.ParkingIO;
import com.propertyvista.oapi.model.ParkingTypeIO;
import com.propertyvista.oapi.xml.DoubleIO;
import com.propertyvista.oapi.xml.StringIO;

public class ParkingMarshaller implements Marshaller<Parking, ParkingIO> {

    @Override
    public ParkingIO unmarshal(Parking parking) {
        ParkingIO parkingIO = new ParkingIO();
        parkingIO.name = parking.name().getValue();
        parkingIO.description = new StringIO(parking.description().getValue());
        parkingIO.type = new ParkingTypeIO(parking.type().getValue());
        parkingIO.levels = new DoubleIO(parking.levels().getValue());

        return parkingIO;
    }

    @Override
    public Parking marshal(ParkingIO parkingIO) throws Exception {
        Parking parking = EntityFactory.create(Parking.class);
        parking.name().setValue(parkingIO.name);
        parking.description().setValue(parkingIO.description.value);
        parking.type().setValue(parkingIO.type.value);
        parking.levels().setValue(parkingIO.levels.value);
        return null;
    }
}
