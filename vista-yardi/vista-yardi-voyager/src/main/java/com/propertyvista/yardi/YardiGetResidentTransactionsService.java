/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 13, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.yardi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.yardi.bean.Properties;
import com.propertyvista.yardi.bean.mits.Identification;
import com.propertyvista.yardi.bean.resident.Property;
import com.propertyvista.yardi.bean.resident.RTCustomer;
import com.propertyvista.yardi.bean.resident.RTUnit;
import com.propertyvista.yardi.bean.resident.ResidentTransactions;
import com.propertyvista.yardi.mapper.BuildingsMapper;
import com.propertyvista.yardi.mapper.UnitsMapper;
import com.propertyvista.yardi.merger.BuildingsMerger;
import com.propertyvista.yardi.merger.UnitsMerger;

/**
 * Implementation functionality for updating properties/units/leases/tenants basing on getResidentTransactions from YARDI api
 * 
 * @author Mykola
 * 
 */
public class YardiGetResidentTransactionsService {

    private final static Logger log = LoggerFactory.getLogger(YardiGetResidentTransactionsService.class);

    /**
     * Updates/creates entities basing on data from YARDI System.
     * 
     * @param yp
     *            the YARDI System connection parameters
     * @throws YardiServiceException
     *             if operation fails
     */
    public void updateAll(YardiParameters yp) throws YardiServiceException {
        validate(yp);

        YardiClient client = new YardiClient(yp.getServiceURL());

        log.info("Get properties information...");
        List<String> propertyCodes = getPropertyCodes(client, yp);

        log.info("Get all resident transactions...");
        List<ResidentTransactions> allTransactions = getAllResidentTransactions(client, yp, propertyCodes);

        updateBuildingsAndUnits(allTransactions);

        updateLeases(allTransactions);

        updateCharges(allTransactions);

    }

    private void updateBuildingsAndUnits(List<ResidentTransactions> allTransactions) {

        List<Building> importedBuildings = getBuildings(allTransactions);
        Map<String, List<AptUnit>> importedUnits = getUnits(allTransactions);

        log.info("Update buildings...");
        merge(importedBuildings, getBuildings());

        log.info("Update units...");
        updateUnitsForBuildings(importedUnits, getBuildings());
    }

    private void updateLeases(List<ResidentTransactions> allTransactions) {
        // TODO Auto-generated method stub

    }

    private void updateCharges(List<ResidentTransactions> allTransactions) {
        // TODO Auto-generated method stub

    }

    private void merge(List<Building> imported, List<Building> existing) {
        List<Building> merged = new BuildingsMerger().merge(imported, existing);
        for (Building building : merged) {
            update(building);
        }
    }

    private void updateUnitsForBuildings(Map<String, List<AptUnit>> importedUnits, List<Building> buildings) {
        for (Building building : buildings) {
            String propertyCode = building.propertyCode().getValue();
            if (importedUnits.containsKey(propertyCode)) {
                Persistence.service().retrieve(building.floorplans());
                mergeUnits(building, importedUnits.get(propertyCode), getUnits(propertyCode));
            }
        }
    }

    private void mergeUnits(Building building, List<AptUnit> imported, List<AptUnit> existing) {
        List<AptUnit> merged = new UnitsMerger().merge(building, imported, existing);
        for (AptUnit unit : merged) {
            update(unit);
        }
    }

    private void update(Building building) {
        try {
            Persistence.service().persist(building);
            log.info("Building with property code {} successfully updated", building.propertyCode().getValue());
        } catch (Exception e) {
            log.error(String.format("Errors during updating building %s", building.propertyCode().getValue()), e);
        }
    }

    private void update(AptUnit unit) {
        try {
            Persistence.service().retrieve(unit.building());
            Persistence.service().persist(unit);
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

    private List<Building> getBuildings(List<ResidentTransactions> allTransactions) {
        BuildingsMapper mapper = new BuildingsMapper();
        return mapper.map(getProperties(allTransactions));
    }

    private Map<String, List<AptUnit>> getUnits(List<ResidentTransactions> allTransactions) {
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
            for (Property property : transaction.getProperties()) {

                String propertyId = getPropertyId(property);
                if (StringUtils.isNotEmpty(propertyId) && !properties.containsKey(propertyId)) {
                    properties.put(propertyId, property);
                }
            }
        }

        return new ArrayList<Property>(properties.values());
    }

    private Map<String, List<RTUnit>> getYardiUnits(List<ResidentTransactions> allTransactions) {
        Map<String, List<RTUnit>> unitsMap = new HashMap<String, List<RTUnit>>();
        for (ResidentTransactions transaction : allTransactions) {
            for (Property property : transaction.getProperties()) {

                String propertyId = getPropertyId(property);
                if (unitsMap.containsKey(propertyId)) {
                    continue;
                }

                Map<String, RTUnit> map = new HashMap<String, RTUnit>();
                for (RTCustomer customer : property.getCustomers()) {
                    String unitId = getUnitId(customer);
                    if (StringUtils.isNotEmpty(unitId) && !map.containsKey(unitId)) {
                        map.put(unitId, customer.getRtunit());
                    }
                }

                unitsMap.put(propertyId, new ArrayList<RTUnit>(map.values()));
            }
        }

        return unitsMap;
    }

    private String getPropertyId(Property property) {
        return property.getPropertyId() != null ? getPropertyId(property.getPropertyId().getIdentification()) : null;
    }

    private String getPropertyId(Identification identification) {
        return identification != null ? (identification.getPrimaryId()) : null;
    }

    private String getUnitId(RTCustomer customer) {
        return customer.getRtunit() != null ? customer.getRtunit().getUnitId() : null;
    }

    private void validate(YardiParameters yp) {
        Validate.notEmpty(yp.getServiceURL(), "ServiceURL parameter can not be empty or null");
        Validate.notEmpty(yp.getUsername(), "Username parameter can not be empty or null");
        Validate.notEmpty(yp.getPassword(), "Password parameter can not be empty or null");
        Validate.notEmpty(yp.getServerName(), "ServerName parameter can not be empty or null");
        Validate.notEmpty(yp.getDatabase(), "Database parameter can not be empty or null");
        Validate.notEmpty(yp.getPlatform(), "Platform parameter can not be empty or null");
        Validate.notEmpty(yp.getInterfaceEntity(), "InterfaceEntity parameter can not be empty or null");
    }

    private List<String> getPropertyCodes(YardiClient client, YardiParameters yp) throws YardiServiceException {
        List<String> propertyCodes = new ArrayList<String>();
        try {
            Properties properties = YardiTransactions.getPropertyConfigurations(client, yp);
            for (com.propertyvista.yardi.bean.Property property : properties.getProperties()) {
                if (StringUtils.isNotEmpty(property.getCode())) {
                    propertyCodes.add(property.getCode());
                }
            }
            return propertyCodes;
        } catch (Exception e) {
            throw new YardiServiceException("Fail to get properties information from YARDI System", e);
        }
    }

    private List<ResidentTransactions> getAllResidentTransactions(YardiClient client, YardiParameters yp, List<String> propertyCodes) {
        List<ResidentTransactions> transactions = new ArrayList<ResidentTransactions>();
        for (String propertyCode : propertyCodes) {
            try {
                ResidentTransactions residentTransactions = YardiTransactions.getResidentTransactions(client, yp, propertyCode);
                transactions.add(residentTransactions);
            } catch (Exception e) {
                log.error(String.format("Errors during call getResidentTransactions operation for building %s", propertyCode), e);
            }
        }

        return transactions;
    }

    private List<AptUnit> getUnits(String propertyCode) {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().building().propertyCode(), propertyCode));
        List<AptUnit> units = Persistence.service().query(criteria);
        return units;
    }

}
