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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.PropertyID;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.RTUnit;
import com.yardi.entity.resident.ResidentTransactions;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.biz.occupancy.OccupancyFacade;
import com.propertyvista.biz.preloader.DefaultProductCatalogFacade;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.yardi.mapper.BuildingsMapper;
import com.propertyvista.yardi.mapper.UnitsMapper;
import com.propertyvista.yardi.merger.BuildingsMerger;
import com.propertyvista.yardi.merger.UnitsMerger;

public class YardiBuildingProcessor {
    private final static Logger log = LoggerFactory.getLogger(YardiBuildingProcessor.class);

    public void updateBuildings(List<ResidentTransactions> allTransactions) {

        List<Building> importedBuildings = getBuildings(allTransactions);
        Map<String, List<AptUnit>> importedUnits = getUnits(allTransactions);

        log.info("Updating buildings...");
        merge(importedBuildings, getBuildings());

        log.info("Updating units...");
        updateUnitsForBuildings(importedUnits, getBuildings());
    }

    private void merge(List<Building> imported, List<Building> existing) {
        List<Building> merged = new BuildingsMerger().merge(imported, existing);
        for (Building building : merged) {
            update(building);
            Persistence.service().commit();
        }
    }

    private void updateUnitsForBuildings(Map<String, List<AptUnit>> importedUnits, List<Building> buildings) {
        for (Building building : buildings) {
            String propertyCode = building.propertyCode().getValue();
            if (importedUnits.containsKey(propertyCode)) {
                mergeUnits(building, importedUnits.get(propertyCode), getUnits(propertyCode));
                Persistence.service().commit();
            }
        }
    }

    private List<AptUnit> getUnits(String propertyCode) {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().building().propertyCode(), propertyCode));
        List<AptUnit> units = Persistence.service().query(criteria);
        return units;
    }

    private void mergeUnits(Building building, List<AptUnit> imported, List<AptUnit> existing) {
        Persistence.service().retrieve(building.floorplans());

        List<AptUnit> merged = new UnitsMerger().merge(building, imported, existing);
        for (AptUnit unit : merged) {
            update(unit);
        }
    }

    private void update(Building building) {
        try {
            boolean isNewBuilding = building.updated().isNull();

            Persistence.service().persist(building);

            if (isNewBuilding) {
                ServerSideFactory.create(DefaultProductCatalogFacade.class).createFor(building);
                ServerSideFactory.create(DefaultProductCatalogFacade.class).persistFor(building);
            } else {
                ServerSideFactory.create(DefaultProductCatalogFacade.class).updateFor(building);
            }

            log.info("Building with property code {} successfully updated", building.propertyCode().getValue());
        } catch (Exception e) {
            log.error(String.format("Errors during updating building %s", building.propertyCode().getValue()), e);
        }
    }

    private void update(AptUnit unit) {
        try {
            boolean isNewUnit = unit.updated().isNull();

            Persistence.service().retrieve(unit.building());
            Persistence.service().persist(unit);

            if (isNewUnit) {
                ServerSideFactory.create(OccupancyFacade.class).setupNewUnit((AptUnit) unit.createIdentityStub());
                ServerSideFactory.create(DefaultProductCatalogFacade.class).addUnit(unit.building(), unit, true);
            } else {
                ServerSideFactory.create(DefaultProductCatalogFacade.class).updateUnit(unit.building(), unit);
            }

            log.info("Unit {} for building {} successfully updated", unit.info().number().getValue(), unit.building().propertyCode().getValue());
        } catch (Exception e) {
            log.error(
                    String.format("Errors during updating unit %s for building %s", unit.info().number().getValue(), unit.building().propertyCode().getValue()),
                    e);
        }
    }

    private List<Building> getBuildings() {
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.asc(criteria.proto().propertyCode());
        return Persistence.service().query(criteria);
    }

    public List<Building> getBuildings(List<ResidentTransactions> allTransactions) {
        BuildingsMapper mapper = new BuildingsMapper();
        return mapper.map(getProperties(allTransactions));
    }

    public Map<String, List<AptUnit>> getUnits(List<ResidentTransactions> allTransactions) {
        UnitsMapper mapper = new UnitsMapper();
        Map<String, List<AptUnit>> units = new HashMap<String, List<AptUnit>>();
        Map<String, List<RTUnit>> imported = getYardiUnits(allTransactions);
        for (Map.Entry<String, List<RTUnit>> entry : imported.entrySet()) {
            List<AptUnit> mapped = mapper.map(entry.getValue());
            if (!mapped.isEmpty()) {
                units.put(entry.getKey(), mapped);
            }
        }
        return units;
    }

    private List<Property> getProperties(List<ResidentTransactions> allTransactions) {
        Map<String, Property> properties = new HashMap<String, Property>();
        for (ResidentTransactions transaction : allTransactions) {

            for (Property property : transaction.getProperty()) {

                for (PropertyID propertyID : property.getPropertyID()) {
                    String propertyId = YardiProcessorUtils.getPropertyId(propertyID);
                    if (StringUtils.isNotEmpty(propertyId) && !properties.containsKey(propertyId)) {
                        properties.put(propertyId, property);
                    }
                }
            }
        }

        return new ArrayList<Property>(properties.values());
    }

    private Map<String, List<RTUnit>> getYardiUnits(List<ResidentTransactions> allTransactions) {
        Map<String, List<RTUnit>> unitsMap = new HashMap<String, List<RTUnit>>();
        for (ResidentTransactions transaction : allTransactions) {

            for (Property property : transaction.getProperty()) {

                for (PropertyID propertyID : property.getPropertyID()) {

                    String propertyId = YardiProcessorUtils.getPropertyId(propertyID);
                    if (unitsMap.containsKey(propertyId)) {
                        continue;
                    }

                    Map<String, RTUnit> map = new HashMap<String, RTUnit>();
                    for (RTCustomer customer : property.getRTCustomer()) {
                        String unitId = YardiProcessorUtils.getUnitId(customer);
                        if (StringUtils.isNotEmpty(unitId) && !map.containsKey(unitId)) {
                            map.put(unitId, customer.getRTUnit());
                        }
                    }

                    unitsMap.put(propertyId, new ArrayList<RTUnit>(map.values()));
                }
            }
        }

        return unitsMap;
    }

}
