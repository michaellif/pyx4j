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

import com.propertyvista.domain.property.asset.Parking;
import com.propertyvista.oapi.model.ParkingIO;
import com.propertyvista.oapi.model.types.ParkingTypeIO;
import com.propertyvista.oapi.xml.DoubleIO;
import com.propertyvista.oapi.xml.StringIO;

public class ParkingMarshaller implements Marshaller<Parking, ParkingIO> {

    private static class SingletonHolder {
        public static final ParkingMarshaller INSTANCE = new ParkingMarshaller();
    }

    private ParkingMarshaller() {
    }

    public static ParkingMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public ParkingIO marshal(Parking parking) {
        ParkingIO parkingIO = new ParkingIO();
        parkingIO.name = parking.name().getValue();
        parkingIO.description = new StringIO(parking.description().getValue());
        parkingIO.type = new ParkingTypeIO(parking.type().getValue());
        parkingIO.levels = new DoubleIO(parking.levels().getValue());
        return parkingIO;
    }

    public List<ParkingIO> marshal(Collection<Parking> parkings) {
        List<ParkingIO> parkingIOList = new ArrayList<ParkingIO>();
        for (Parking parking : parkings) {
            parkingIOList.add(marshal(parking));
        }
        return parkingIOList;
    }

    @Override
    public Parking unmarshal(ParkingIO parkingIO) throws Exception {
        Parking parking = EntityFactory.create(Parking.class);
        parking.name().setValue(parkingIO.name);
        MarshallerUtils.ioToEntity(parking.description(), parkingIO.description);
        MarshallerUtils.ioToEntity(parking.type(), parkingIO.type);
        MarshallerUtils.ioToEntity(parking.levels(), parkingIO.levels);
        return parking;
    }

    public List<Parking> unmarshal(Collection<ParkingIO> parkingIOList) throws Exception {
        List<Parking> parkings = new ArrayList<Parking>();
        for (ParkingIO parkingIO : parkingIOList) {
            parkings.add(unmarshal(parkingIO));
        }
        return parkings;
    }
}
