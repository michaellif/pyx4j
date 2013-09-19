/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 17, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.yardi.services;

import java.util.ArrayList;
import java.util.List;

import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.ResidentTransactions;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.ref.Province;
import com.propertyvista.yardi.mapper.BuildingsMapper;
import com.propertyvista.yardi.mapper.UnitsMapper;
import com.propertyvista.yardi.merger.BuildingsMerger;
import com.propertyvista.yardi.merger.UnitsMerger;

public class YardiBuildingProcessor {

    public Building updateBuilding(Key yardiInterfaceId, Property property) throws YardiServiceException {
        Building building = getBuildingFromProperty(property);
        building.integrationSystemId().setValue(yardiInterfaceId);
        if (!isSameCountry(building)) {
            throw new YardiServiceException("Wrong country in building ");
        }
        String propertyCode = building.propertyCode().getValue();
        return merge(building, getBuilding(yardiInterfaceId, propertyCode));
    }

    public AptUnit updateUnit(Building building, RTCustomer rtCustomer) throws YardiServiceException {
        AptUnit importedUnit = new UnitsMapper().map(rtCustomer, getProvinces());
        if (building.floorplans().getAttachLevel() != AttachLevel.Attached) {
            Persistence.service().retrieveMember(building.floorplans(), AttachLevel.Attached);
        }
        return updateUnitForBuilding(importedUnit, building);

    }

    private Building merge(Building imported, Building existing) {
        Building merged = new BuildingsMerger().merge(imported, existing);
//        update(merged);
        return merged;
    }

    private AptUnit updateUnitForBuilding(AptUnit importedUnit, Building building) throws YardiServiceException {
        if (building == null) {
            throw new YardiServiceException("Unable to update units for building: null");
        }
        return mergeUnit(building, importedUnit, getUnit(building, importedUnit.info().number().getValue()));
    }

    private AptUnit mergeUnit(Building building, AptUnit importedUnit, AptUnit existingUnit) {
        AptUnit merged = new UnitsMerger().merge(building, importedUnit, existingUnit);
//        update(merged);
        return merged;
    }

    private AptUnit getUnit(Building building, String unitNumber) {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.eq(criteria.proto().building(), building);
        criteria.eq(criteria.proto().info().number(), unitNumber);
        List<AptUnit> units = Persistence.service().query(criteria);
        if (units.size() == 0) {
            return null;
        }
        return units.get(0);
    }

    private Building getBuilding(Key yardiInterfaceId, String propertyCode) {
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.eq(criteria.proto().propertyCode(), propertyCode);
        criteria.eq(criteria.proto().integrationSystemId(), yardiInterfaceId);
        List<Building> buildings = Persistence.service().query(criteria);
        return !buildings.isEmpty() ? buildings.get(0) : null;
    }

    public Building getBuildingFromProperty(Property property) {
        BuildingsMapper mapper = new BuildingsMapper();
        return mapper.map(getProvinces(), property);
    }

    public List<Province> getProvinces() {
        EntityQueryCriteria<Province> criteria = EntityQueryCriteria.create(Province.class);
        criteria.asc(criteria.proto().name());
        return Persistence.service().query(criteria);
    }

    public List<Property> getProperties(ResidentTransactions transaction) {
        List<Property> properties = new ArrayList<Property>();
        for (Property property : transaction.getProperty()) {
            properties.add(property);
        }
        return properties;
    }

    private boolean isSameCountry(Building building) throws YardiServiceException {
        Pmc pmc = VistaDeployment.getCurrentPmc();
        String yardiCountry = building.info().address().country().name().getValue();
        String countryOfOperation = pmc.features().countryOfOperation().getValue().toString();
        if (yardiCountry == null) {
            throw new YardiServiceException("Country of Operation not found for this building. Building not imported.");
        }
        return yardiCountry.equals(countryOfOperation);
    }

}
