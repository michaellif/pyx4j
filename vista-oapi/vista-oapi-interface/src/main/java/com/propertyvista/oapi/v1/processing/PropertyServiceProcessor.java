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
package com.propertyvista.oapi.v1.processing;

import java.util.List;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.asset.BuildingFacade;
import com.propertyvista.biz.preloader.DefaultProductCatalogFacade;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.oapi.ServiceType;
import com.propertyvista.oapi.v1.marshaling.BuildingMarshaller;
import com.propertyvista.oapi.v1.marshaling.UnitMarshaller;
import com.propertyvista.oapi.v1.model.BuildingIO;
import com.propertyvista.oapi.v1.model.BuildingListIO;
import com.propertyvista.oapi.v1.model.UnitIO;
import com.propertyvista.oapi.v1.model.UnitListIO;
import com.propertyvista.oapi.v1.persisting.BuildingPersister;
import com.propertyvista.oapi.v1.persisting.UnitPersister;
import com.propertyvista.oapi.v1.service.PropertyService;

public class PropertyServiceProcessor extends AbstractProcessor {

    public PropertyServiceProcessor(ServiceType serviceType) {
        super(PropertyService.class, serviceType);
    }

    public BuildingListIO getBuildings() {

        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
        buildingCriteria.asc(buildingCriteria.proto().propertyCode());

        return BuildingMarshaller.getInstance().marshalCollection(BuildingListIO.class, Persistence.service().query(buildingCriteria));
    }

    public BuildingIO getBuildingByPropertyCode(String propertyCode) {

        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
        buildingCriteria.eq(buildingCriteria.proto().propertyCode(), propertyCode);
        List<Building> buildings = Persistence.service().query(buildingCriteria);
        if (buildings == null || buildings.isEmpty()) {
            return null;
        }
        return BuildingMarshaller.getInstance().marshalItem(buildings.get(0));
    }

    public void updateBuilding(BuildingIO buildingIO) throws Exception {
        Building building = BuildingMarshaller.getInstance().unmarshalItem(buildingIO);

        new BuildingPersister().persist(building);

        ServerSideFactory.create(BuildingFacade.class).persist(building);

        Persistence.service().commit();
    }

    public UnitListIO getUnitsByPropertyCode(String propertyCode) {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.eq(criteria.proto().building().propertyCode(), propertyCode);
        return UnitMarshaller.getInstance().marshalCollection(UnitListIO.class, Persistence.service().query(criteria));
    }

    public UnitIO getUnitByNumber(String propertyCode, String unitNumber) {
        EntityQueryCriteria<AptUnit> unitCriteria = EntityQueryCriteria.create(AptUnit.class);
        unitCriteria.add(PropertyCriterion.eq(unitCriteria.proto().building().propertyCode(), propertyCode));
        unitCriteria.eq(unitCriteria.proto().info().number(), unitNumber);
        List<AptUnit> units = Persistence.service().query(unitCriteria);
        if (units == null || units.isEmpty()) {
            return null;
        }
        AptUnit unit = units.get(0);
        Persistence.service().retrieve(unit.floorplan());
        Persistence.service().retrieve(unit.building());
        return UnitMarshaller.getInstance().marshalItem(unit);

    }

    public void updateUnit(UnitIO unitIO) {
        AptUnit unitDTO = UnitMarshaller.getInstance().unmarshalItem(unitIO);

        new UnitPersister().persist(unitDTO);

        ServerSideFactory.create(DefaultProductCatalogFacade.class).addUnit(unitDTO.building(), new UnitPersister().retrieve(unitDTO));

        Persistence.service().commit();
    }

}
