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

import com.yardi.entity.mits.PropertyIDType;
import com.yardi.entity.mits.Unit;
import com.yardi.entity.resident.Property;
import com.yardi.entity.resident.ResidentTransactions;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.yardi.mapper.BuildingsMapper;
import com.propertyvista.yardi.mapper.MappingUtils;
import com.propertyvista.yardi.mapper.UnitsMapper;
import com.propertyvista.yardi.merger.BuildingsMerger;
import com.propertyvista.yardi.merger.UnitsMerger;

public class YardiBuildingProcessor {

    public Building updateBuilding(Key yardiInterfaceId, PropertyIDType propertyId) throws YardiServiceException {
        Building building = getBuildingFromProperty(propertyId);
        building.integrationSystemId().setValue(yardiInterfaceId);
        MappingUtils.ensureCountryOfOperation(building);
        String propertyCode = building.propertyCode().getValue();
        return merge(building, MappingUtils.getBuilding(yardiInterfaceId, propertyCode));
    }

    public AptUnit updateUnit(Building building, Unit unit) throws YardiServiceException {
        AptUnit importedUnit = new UnitsMapper().map(unit);
        if (building.floorplans().getAttachLevel() != AttachLevel.Attached) {
            Persistence.service().retrieveMember(building.floorplans(), AttachLevel.Attached);
        }
        AptUnit merged = new UnitsMerger().merge(building, importedUnit, getUnit(building, importedUnit.info().number().getValue()));
//      update(merged);
        return merged;

    }

    private Building merge(Building imported, Building existing) {
        Building merged = new BuildingsMerger().merge(imported, existing);
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

    public Building getBuildingFromProperty(PropertyIDType propertyId) {
        BuildingsMapper mapper = new BuildingsMapper();
        return mapper.map(propertyId);
    }

    public List<Property> getProperties(ResidentTransactions transaction) {
        List<Property> properties = new ArrayList<Property>();
        for (Property property : transaction.getProperty()) {
            properties.add(property);
        }
        return properties;
    }

}
