/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 28, 2012
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.yardi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.axis2.AxisFault;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.yardi.bean.Properties;
import com.propertyvista.yardi.bean.Property;
import com.propertyvista.yardi.bean.resident.RTCustomer;
import com.propertyvista.yardi.bean.resident.RTUnit;
import com.propertyvista.yardi.bean.resident.ResidentTransactions;
import com.propertyvista.yardi.mapper.GetPropertyConfigurationsMapper;
import com.propertyvista.yardi.mapper.UnitsMapper;
import com.propertyvista.yardi.merger.BuildingsMerger;

/**
 * Represents property service functionality based on YARDI System api
 * 
 * @author Mykola
 * 
 */
@Deprecated
public class YardiPropertyService {

    private final static Logger log = LoggerFactory.getLogger(YardiPropertyService.class);

    /**
     * Updates/creates buildings basing on property data from YARDI system
     * 
     * @param yp
     *            the YARDI System connection parameters
     * @throws YardiServiceException
     *             if operation fails
     */
    public void updateBuildings(YardiParameters yp) throws YardiServiceException {
        validate(yp);

        //update buildings
        YardiClient client = new YardiClient(yp.getServiceURL());
        try {
            Properties properties = YardiTransactions.getPropertyConfigurations(client, yp);
            merge(getBuildings(), properties.getProperties());
        } catch (Exception e) {
            throw new YardiServiceException("Fail to update buildings", e);
        }

        //update units
        for (Building building : getBuildings()) {
            try {
                updateUnits(building, yp);
            } catch (Exception e) {
                log.error(String.format("Fail to update units for building with property code %s", building.propertyCode().getValue()), e);
            }
        }

    }

    /**
     * Updates/creates building basing on property data from YARDI system
     * 
     * @param propertyCode
     *            the property code
     * @param yp
     *            the YARDI System connection parameters
     * @throws YardiServiceException
     *             if operation fails
     */
    public void updateBuilding(String propertyCode, YardiParameters yp) throws YardiServiceException {
        Validate.notEmpty(propertyCode, "propertyCode parameter can not be empty or null");
        validate(yp);

        YardiClient client = new YardiClient(yp.getServiceURL());
        try {
            Properties properties = YardiTransactions.getPropertyConfigurations(client, yp);
            for (Property property : properties.getProperties()) {
                if (propertyCode.equals(property.getCode())) {

                    Building building = getBuilding(propertyCode);
                    if (building != null) {
                        merge(building, property);
                        updateUnits(building, yp);
                    }

                    return;
                }
            }
        } catch (Exception e) {
            throw new YardiServiceException(String.format("Fail to update building with property code %s", propertyCode), e);
        }

        log.info("Did not find a building for property code {}", propertyCode);
    }

    /**
     * Updates/creates units for building with corresponding <code>propertyCode</code> basing on property data from YARDI system
     * 
     * @param propertyCode
     *            the property code
     * @param yp
     *            the YARDI System connection parameters
     * @throws YardiServiceException
     *             if operation fails
     */
    public void updateUnits(String propertyCode, YardiParameters yp) throws YardiServiceException {
        Validate.notEmpty(propertyCode, "propertyCode parameter can not be empty or null");
        validate(yp);

        Building building = getBuilding(propertyCode);
        if (building == null) {
            throw new YardiServiceException(String.format("Unable to update units for non persisted building with property code %s", propertyCode));
        }

        try {
            updateUnits(building, yp);
        } catch (Exception e) {
            throw new YardiServiceException(String.format("Fail to update units for building with property code %s", propertyCode), e);
        }
    }

    private void updateUnits(Building building, YardiParameters yp) throws YardiServiceException, AxisFault, RemoteException, JAXBException {
        String propertyCode = building.propertyCode().getValue();
        Persistence.service().retrieve(building.floorplans());

        YardiClient client = new YardiClient(yp.getServiceURL());

        ResidentTransactions residentTransactions = YardiTransactions.getResidentTransactions(client, yp, propertyCode);
        mergeUnits(building, getUnits(propertyCode), getYardiUnits(residentTransactions));
    }

    /**
     * Updates/creates unit with <code>unitId</code> for building with <code>propertyCode</code> basing on property data from YARDI system
     * 
     * @param propertyCode
     *            the property code
     * @param unitId
     *            the unit number
     * @param yp
     *            the YARDI System connection parameters
     * @throws YardiServiceException
     *             if operation fails
     */
    public void updateUnit(String propertyCode, String unitId, YardiParameters yp) throws YardiServiceException {
        Validate.notEmpty(propertyCode, "propertyCode parameter can not be empty or null");
        Validate.notEmpty(unitId, "unitId parameter can not be empty or null");
        validate(yp);

        YardiClient client = new YardiClient(yp.getServiceURL());
        Building building = getBuilding(propertyCode);
        if (building == null) {
            throw new YardiServiceException(String.format("Unable to update unit %s for non persisted building with property code %s", unitId, propertyCode));
        }
        Persistence.service().retrieve(building.floorplans());

        try {
            ResidentTransactions residentTransactions = YardiTransactions.getResidentTransactions(client, yp, propertyCode);
            mergeUnit(building, getUnit(unitId, propertyCode), getYardiUnit(unitId, residentTransactions));

        } catch (Exception e) {
            throw new YardiServiceException(String.format("Fail to update unit %s for building with property code %s", unitId, propertyCode), e);
        }
    }

    private Building getBuilding(String propertyCode) {
        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
        buildingCriteria.eq(buildingCriteria.proto().propertyCode(), propertyCode);
        List<Building> buildings = Persistence.service().query(buildingCriteria);

        return !buildings.isEmpty() ? buildings.get(0) : null;
    }

    private void merge(Building existing, Property property) {
        merge(Arrays.asList(existing), Arrays.asList(property));
    }

    private List<Building> getBuildings() {
        EntityQueryCriteria<Building> criteria = EntityQueryCriteria.create(Building.class);
        criteria.asc(criteria.proto().propertyCode());
        return Persistence.service().query(criteria);
    }

    private void merge(List<Building> existing, List<Property> properties) {
        GetPropertyConfigurationsMapper mapper = new GetPropertyConfigurationsMapper();
        List<Building> imported = mapper.map(properties);

        List<Building> merged = new BuildingsMerger().merge(imported, existing);
        for (Building building : merged) {
            update(building);
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

    private List<AptUnit> getUnits(String propertyCode) {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().building().propertyCode(), propertyCode));
        List<AptUnit> units = Persistence.service().query(criteria);
        return units;
    }

    private AptUnit getUnit(String unitId, String propertyCode) {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().building().propertyCode(), propertyCode));
        criteria.eq(criteria.proto().info().number(), unitId);
        List<AptUnit> units = Persistence.service().query(criteria);
        AptUnit unit = !units.isEmpty() ? units.get(0) : null;
        return unit;
    }

    private List<RTUnit> getYardiUnits(ResidentTransactions residentTransactions) {
        List<com.propertyvista.yardi.bean.resident.Property> yardiProperties = residentTransactions.getProperties();

        return yardiProperties.isEmpty() ? new ArrayList<RTUnit>() : getUnits(yardiProperties.get(0).getCustomers());
    }

    private RTUnit getYardiUnit(String unitId, ResidentTransactions residentTransactions) {
        List<RTUnit> yardiUnits = getYardiUnits(residentTransactions);
        for (RTUnit rtUnit : yardiUnits) {
            if (unitId.equals(rtUnit.getUnitId())) {
                return rtUnit;
            }
        }
        return null;
    }

    private List<RTUnit> getUnits(List<RTCustomer> customers) {
        Map<String, RTUnit> units = new HashMap<String, RTUnit>();
        if (customers != null) {
            for (RTCustomer customer : customers) {
                units.put(customer.getRtunit().getUnitId(), customer.getRtunit());
            }
        }

        //list of unique units
        return new ArrayList<RTUnit>(units.values());
    }

    private void mergeUnits(Building building, List<AptUnit> existing, List<RTUnit> yardiUnits) {
        UnitsMapper mapper = new UnitsMapper();
//        List<AptUnit> imported = mapper.map(building, yardiUnits);
//
//        List<AptUnit> merged = new UnitsMerger().merge(imported, existing);
//        for (AptUnit unit : merged) {
//            update(unit);
//        }
    }

    private void mergeUnit(Building building, AptUnit existing, RTUnit yardiUnit) {
        if (existing != null && yardiUnit != null) {
//            mergeUnits(building, Arrays.asList(existing), Arrays.asList(yardiUnit));
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

    private void validate(YardiParameters yp) {
        Validate.notEmpty(yp.getServiceURL(), "ServiceURL parameter can not be empty or null");
        Validate.notEmpty(yp.getUsername(), "Username parameter can not be empty or null");
        Validate.notEmpty(yp.getPassword(), "Password parameter can not be empty or null");
        Validate.notEmpty(yp.getServerName(), "ServerName parameter can not be empty or null");
        Validate.notEmpty(yp.getDatabase(), "Database parameter can not be empty or null");
        Validate.notEmpty(yp.getPlatform(), "Platform parameter can not be empty or null");
        Validate.notEmpty(yp.getInterfaceEntity(), "InterfaceEntity parameter can not be empty or null");
    }

}
