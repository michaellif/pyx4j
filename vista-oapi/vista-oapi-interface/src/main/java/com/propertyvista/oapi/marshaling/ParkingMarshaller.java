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
        if (parking == null || parking.isNull()) {
            return null;
        }
        ParkingIO parkingIO = new ParkingIO();
        parkingIO.name = parking.name().getValue();
        parkingIO.description = MarshallerUtils.createIo(StringIO.class, parking.description());
        parkingIO.type = MarshallerUtils.createIo(ParkingTypeIO.class, parking.type());
        parkingIO.levels = MarshallerUtils.createIo(DoubleIO.class, parking.levels());
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
    public Parking unmarshal(ParkingIO parkingIO) {
        Parking parking = EntityFactory.create(Parking.class);
        parking.name().setValue(parkingIO.name);
        MarshallerUtils.setValue(parking.description(), parkingIO.description);
        MarshallerUtils.setValue(parking.type(), parkingIO.type);
        MarshallerUtils.setValue(parking.levels(), parkingIO.levels);
        return parking;
    }

    public List<Parking> unmarshal(Collection<ParkingIO> parkingIOList) {
        List<Parking> parkings = new ArrayList<Parking>();
        for (ParkingIO parkingIO : parkingIOList) {
            Parking parking = EntityFactory.create(Parking.class);
            MarshallerUtils.set(parking, parkingIO, ParkingMarshaller.getInstance());
            parkings.add(parking);
        }
        return parkings;
    }
}
