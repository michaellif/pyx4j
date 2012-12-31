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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.yardi.bean.Properties;
import com.propertyvista.yardi.bean.Property;
import com.propertyvista.yardi.mapper.GetPropertyConfigurationsMapper;
import com.propertyvista.yardi.merger.BuildingsMerger;

/**
 * Represents property service functionality based on YARDI System api
 * 
 * @author Mykola
 * 
 */
public class YardiPropertyService {

    private final static Logger log = LoggerFactory.getLogger(YardiPropertyService.class);

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
                    merge(getBuilding(propertyCode), property);
                    return;
                }
            }
        } catch (Exception e) {
            throw new YardiServiceException(String.format("Fail to update building with property code %s by data from YARDI System", propertyCode), e);
        }

        if (log.isDebugEnabled()) {
            log.debug("Did not find a building for property code {}", propertyCode);
        }
    }

    private Building getBuilding(String propertyCode) {
        EntityQueryCriteria<Building> buildingCriteria = EntityQueryCriteria.create(Building.class);
        buildingCriteria.eq(buildingCriteria.proto().propertyCode(), propertyCode);
        List<Building> buildings = Persistence.service().query(buildingCriteria);

        return !buildings.isEmpty() ? buildings.get(0) : null;
    }

    private void merge(Building existing, Property property) {
        if (existing != null) {
            merge(Arrays.asList(existing), Arrays.asList(property));
        }
    }

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

        YardiClient client = new YardiClient(yp.getServiceURL());
        try {
            Properties properties = YardiTransactions.getPropertyConfigurations(client, yp);
            merge(getBuildings(), properties.getProperties());
        } catch (Exception e) {
            throw new YardiServiceException("Fail to update buildings by data from YARDI System", e);
        }
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
        Persistence.service().persist(building);

        if (log.isDebugEnabled()) {
            log.debug("Building with property code {} successfully updated", building.propertyCode().getValue());
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
