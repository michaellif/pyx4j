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
package com.propertyvista.oapi;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.server.IEntityPersistenceService;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.server.contexts.NamespaceManager;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.oapi.marshaling.BuildingMarshaller;
import com.propertyvista.oapi.marshaling.UnitMarshaller;
import com.propertyvista.oapi.model.BuildingIO;
import com.propertyvista.oapi.model.BuildingsIO;
import com.propertyvista.oapi.model.UnitIO;

public class PropertyFacade {

    private static final IEntityPersistenceService service;

    static {
        service = Persistence.service();
    }

    public static BuildingsIO getBuildings() {

        //TODO
        NamespaceManager.setNamespace("vista");

        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
        buildingCriteria.asc(buildingCriteria.proto().propertyCode());
        List<Building> buildings = service.query(buildingCriteria);

        BuildingsIO buildingsRs = new BuildingsIO();

        for (Building building : buildings) {
            BuildingMarshaller marshaller = new BuildingMarshaller();
            buildingsRs.buildings.add(marshaller.unmarshal(building));
        }

        return buildingsRs;
    }

    public static BuildingIO getBuildingByPropertyCode(String propertyCode) {

        //TODO
        NamespaceManager.setNamespace("vista");

        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
        buildingCriteria.eq(buildingCriteria.proto().propertyCode(), propertyCode);
        List<Building> buildings = service.query(buildingCriteria);

        BuildingMarshaller marshaller = new BuildingMarshaller();

        return marshaller.unmarshal(buildings.get(0));
    }

    public static List<UnitIO> getUnitsByPropertyCode(String propertyCode) {
        List<UnitIO> unitsRS = new ArrayList<UnitIO>();

        NamespaceManager.setNamespace("vista");
        EntityQueryCriteria<AptUnit> unitCriteria = EntityQueryCriteria.create(AptUnit.class);
        unitCriteria.add(PropertyCriterion.eq(unitCriteria.proto().building().propertyCode(), propertyCode));
        List<AptUnit> units = service.query(unitCriteria);
        for (AptUnit unit : units) {
            service.retrieve(unit.floorplan());
            service.retrieve(unit.building());
            UnitMarshaller marshaller = new UnitMarshaller();
            unitsRS.add(marshaller.unmarshal(unit));
        }
        return unitsRS;
    }

    public static UnitIO getUnitByNumber(String propertyCode, String unitNumber) {
        NamespaceManager.setNamespace("vista");
        EntityQueryCriteria<AptUnit> unitCriteria = EntityQueryCriteria.create(AptUnit.class);
        unitCriteria.add(PropertyCriterion.eq(unitCriteria.proto().building().propertyCode(), propertyCode));
        unitCriteria.eq(unitCriteria.proto().info().number(), unitNumber);
        List<AptUnit> units = service.query(unitCriteria);
        AptUnit unit = units.get(0);
        service.retrieve(unit.floorplan());
        service.retrieve(unit.building());
        UnitMarshaller marshaller = new UnitMarshaller();
        return marshaller.unmarshal(unit);

    }
}
