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
package com.propertyvista.yardi.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.mits.Address;
import com.yardi.entity.mits.PropertyIDType;
import com.yardi.entity.mits.Unit;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.SimpleMessageFormat;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.yardi.mappers.BuildingsMapper;
import com.propertyvista.yardi.mappers.MappingUtils;
import com.propertyvista.yardi.mappers.UnitsMapper;
import com.propertyvista.yardi.mergers.BuildingsMerger;
import com.propertyvista.yardi.mergers.UnitsMerger;

public class YardiBuildingProcessor {
    private final static Logger log = LoggerFactory.getLogger(YardiBuildingProcessor.class);

    private final ExecutionMonitor executionMonitor;

    public YardiBuildingProcessor() {
        this(null);
    }

    public YardiBuildingProcessor(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
    }

    public Building updateBuilding(Key yardiInterfaceId, PropertyIDType propertyId) throws YardiServiceException {
        Building building = getBuilding(propertyId);

        building.integrationSystemId().setValue(yardiInterfaceId);
        MappingUtils.ensureCountryOfOperation(building);

        return new BuildingsMerger().merge(building, MappingUtils.getBuilding(yardiInterfaceId, building.propertyCode().getValue()));
    }

    public AptUnit updateUnit(Building building, Unit unit) throws YardiServiceException {
        AptUnit importedUnit = new UnitsMapper().map(unit);
        if (building.floorplans().getAttachLevel() != AttachLevel.Attached) {
            Persistence.service().retrieveMember(building.floorplans(), AttachLevel.Attached);
        }
        return updateUnitForBuilding(importedUnit, building);

    }

    private AptUnit updateUnitForBuilding(AptUnit importedUnit, Building building) throws YardiServiceException {
        if (building == null) {
            throw new YardiServiceException("Unable to update units for building: null");
        }
        return new UnitsMerger().merge(building, importedUnit, getUnit(building, importedUnit.info().number().getValue()));
    }

    private AptUnit getUnit(Building building, String unitNumber) {
        EntityQueryCriteria<AptUnit> criteria = EntityQueryCriteria.create(AptUnit.class);
        criteria.eq(criteria.proto().building(), building);
        criteria.eq(criteria.proto().info().number(), unitNumber);
        return Persistence.service().retrieve(criteria);
    }

    public Building getBuilding(PropertyIDType propertyId) {
        Building building = new BuildingsMapper().map(propertyId);

        Address address = propertyId.getAddress().get(0);
        StringBuilder importedAddressToString = new StringBuilder();
        importedAddressToString.append("(");
        importedAddressToString.append("address1: ").append(address.getAddress1()).append(", ");
        importedAddressToString.append("address2: ").append(address.getAddress2()).append(", ");
        importedAddressToString.append("city: ").append(address.getCity()).append(", ");
        importedAddressToString.append("state: ").append(address.getState()).append(", ");
        importedAddressToString.append("province: ").append(address.getProvince()).append(", ");
        importedAddressToString.append("postalCode: ").append(address.getPostalCode()).append(", ");
        importedAddressToString.append("country: ").append(address.getCountry()).append(", ");
        importedAddressToString.append("countyName: ").append(address.getCountyName()).append("");
        importedAddressToString.append(")");

        log.debug("Property PrimaryID={}, SecondaryID={}: Trying to import building address: '{}'", propertyId.getIdentification().getPrimaryID(), propertyId
                .getIdentification().getSecondaryID(), importedAddressToString.toString());

        StringBuilder addrErr = new StringBuilder();
        building.info().address().set(MappingUtils.getAddress(address, addrErr));
        if (addrErr.length() > 0) {
            String msg = SimpleMessageFormat.format(
                    "Property PrimaryID={0,choice,null#null|!null#{0}}, SecondaryID={1,choice,null#null|!null#{1}}: Got invalid address: {2}", propertyId
                            .getIdentification().getPrimaryID(), propertyId.getIdentification().getSecondaryID(), addrErr);
            log.warn(msg);
            if (executionMonitor != null) {
                executionMonitor.addInfoEvent("ParseAddress", msg);
            }
        } else {
            log.debug("Property PrimaryID={}, SecondaryID={}: Address was imported successfully", propertyId.getIdentification().getPrimaryID(), propertyId
                    .getIdentification().getSecondaryID());
        }

        if (building.info().address().province().isNull()) {
            String errorMsg = SimpleMessageFormat.format(
                    "Property PrimaryID={0,choice,null#null|!null#{0}}, SecondaryID={1,choice,null#null|!null#{1}}: has no province.", propertyId
                            .getIdentification().getPrimaryID(), propertyId.getIdentification().getSecondaryID());
            throw new RuntimeException(errorMsg);
        }

        return building;
    }
}
