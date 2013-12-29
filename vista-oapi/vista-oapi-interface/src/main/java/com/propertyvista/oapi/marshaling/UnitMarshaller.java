/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 16, 2012
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.oapi.marshaling;

import java.util.List;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.oapi.model.UnitIO;
import com.propertyvista.oapi.xml.IntegerIO;
import com.propertyvista.oapi.xml.StringIO;

public class UnitMarshaller implements Marshaller<AptUnit, UnitIO> {

    private static class SingletonHolder {
        public static final UnitMarshaller INSTANCE = new UnitMarshaller();
    }

    private UnitMarshaller() {
    }

    public static UnitMarshaller getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public UnitIO marshal(AptUnit unit) {
        if (unit == null || unit.isNull()) {
            return null;
        }
        UnitIO unitIO = new UnitIO();
        unitIO.number = MarshallerUtils.getValue(unit.info().number());
        unitIO.propertyCode = MarshallerUtils.getValue(unit.building().propertyCode());

        unitIO.floorplanName = MarshallerUtils.createIo(StringIO.class, unit.floorplan().name());
        unitIO.baths = MarshallerUtils.createIo(IntegerIO.class, unit.floorplan().bathrooms());
        unitIO.beds = MarshallerUtils.createIo(IntegerIO.class, unit.floorplan().bedrooms());
        return unitIO;
    }

    @Override
    public AptUnit unmarshal(UnitIO unitIO) {
        AptUnit unit = EntityFactory.create(AptUnit.class);
        unit.info().number().setValue(unitIO.number);

        // building
        if (unitIO.propertyCode == null) {
            throw new Error("Building not found for the unit.");
        }
        if (unitIO.floorplanName == null || unitIO.floorplanName.getValue() == null) {
            throw new Error("Floorplan not found for the unit.");
        }
        if (unitIO.baths == null || unitIO.baths.getValue() == null) {
            throw new Error("Number of baths not found for a unit");
        }
        if (unitIO.beds == null || unitIO.beds.getValue() == null) {
            throw new Error("Number of beds not found for a unit");
        }
        if (unitIO.floorplanName == null || unitIO.floorplanName.getValue() == null) {
            throw new Error("Floorplan Name not found in unit.");
        }

        // building
        Building building;
        {
            EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
            criteria.eq(criteria.proto().propertyCode(), unitIO.propertyCode);
            List<Building> buildings = Persistence.service().query(criteria);
            if (buildings.size() > 0) {
                building = buildings.get(0);
            } else {
                throw new Error("Building not found in the database");
            }
        }
        unit.building().set(building);

        // floorplan
        Persistence.service().retrieveMember(building.floorplans());
        for (Floorplan floorplan : building.floorplans()) {
            if (floorplan.name().getValue().equals(unitIO.floorplanName)) {
                if (!floorplan.bathrooms().getValue().equals(unitIO.baths.getValue())) {
                    throw new Error("There is a problem with number of bathrooms in unit " + unitIO.number);
                }
                if (!floorplan.bedrooms().getValue().equals(unitIO.beds.getValue())) {
                    throw new Error("There is a problem with number of bedrooms in unit " + unitIO.number);
                }
                unit.floorplan().set(floorplan);
            }
        }
        if (unit.floorplan().isNull()) {
            Floorplan floorplan = EntityFactory.create(Floorplan.class);
            MarshallerUtils.setValue(floorplan.name(), unitIO.floorplanName);
            MarshallerUtils.setValue(floorplan.bedrooms(), unitIO.beds);
            MarshallerUtils.setValue(floorplan.bathrooms(), unitIO.baths);
            floorplan.building().set(building);
            Persistence.service().persist(floorplan);
            unit.floorplan().set(floorplan);
        }

        return unit;
    }
}