/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 17, 2011
 * @author dmitry
 * @version $Id$
 */
package com.propertyvista.yardi.mapper;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.property.asset.AreaMeasurementUnit;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitInfo.EconomicStatus;
import com.propertyvista.domain.property.asset.unit.AptUnitOccupancy;
import com.propertyvista.domain.property.asset.unit.AptUnitType;
import com.propertyvista.domain.tenant.Tenant;
import com.propertyvista.server.common.generator.Model;
import com.propertyvista.yardi.bean.mits.Customer;
import com.propertyvista.yardi.bean.mits.Information;
import com.propertyvista.yardi.bean.resident.Property;
import com.propertyvista.yardi.bean.resident.RTCustomer;
import com.propertyvista.yardi.bean.resident.RTUnit;
import com.propertyvista.yardi.bean.resident.ResidentTransactions;

public class GetResidentTransactionsMapper {

    private final static Logger log = LoggerFactory.getLogger(GetResidentTransactionsMapper.class);

    private final Model model = new Model();

    // TODO for now we are not converting these just yet
    private final List<AptUnitOccupancy> occupancies = new ArrayList<AptUnitOccupancy>();

    // TODO later we will need to do transactions here
    public void map(ResidentTransactions transactions) {
        for (Property property : transactions.getProperties()) {
            map(property);
        }
    }

    public void map(Property property) {
        // convert building, for now only need the identifier
        Building building = EntityFactory.create(Building.class);
        building.propertyCode().setValue(property.getPropertyId().getIdentification().getPrimaryId());
        model.getBuildings().add(building);

        for (RTCustomer customer : property.getCustomers()) {
            map(customer, building);
        }
    }

    public void map(RTCustomer rtCustomer, Building building) {
        map(rtCustomer.getRtunit(), building);
        for (Customer customer : rtCustomer.getCustomers().getCustomers()) {
            map(customer);
        }
    }

    public void map(Customer customer) {
        Tenant tenant = EntityFactory.create(Tenant.class);

        tenant.person().name().firstName().setValue(customer.getName().getFirstName());
        tenant.person().name().lastName().setValue(customer.getName().getLastName());
        if (customer.getName().getMiddleName() != null && !customer.getName().getMiddleName().isEmpty()) {
            tenant.person().name().middleName().setValue(customer.getName().getMiddleName());
        }

        model.getTenants().add(tenant);
    }

    public void map(RTUnit unitFrom, Building building) {
        AptUnit unitTo = EntityFactory.create(AptUnit.class);

        unitTo.belongsTo().set(building);

        // info
        Information info = unitFrom.getUnit().getInformation();
        unitTo.info().name().setValue(unitFrom.getUnitId());
        unitTo.info().number().setValue(info.getUnitId());

        // unit type: prv11b1
        if (info.getFloorplanId().equals("prv11b1")) {
            unitTo.info().type().setValue(AptUnitType.oneBedroom);
        } else {
            log.warn("Unknown unit type {}, need to handle this in code mapping", info.getFloorplanId());
            unitTo.info().type().setValue(AptUnitType.oneBedroom);
        }
        unitTo.info().bedrooms().setValue(info.getUnitBedrooms());
        unitTo.info().bathrooms().setValue(info.getUnitBathrooms());
        unitTo.info().area().setValue(info.getMaxSquareFeet().doubleValue());
        unitTo.info().areaUnits().setValue(AreaMeasurementUnit.sqFeet);

        if (info.getUnitEconomicStatus().equals("residential")) {
            unitTo.info().economicStatus().setValue(EconomicStatus.residential);
        } else {
            log.info("Unknown economic status {}", info.getUnitEconomicStatus());
            unitTo.info().economicStatus().setValue(EconomicStatus.other);
        }

        // marketing
        unitTo.marketing().name().setValue(unitFrom.getUnit().getMarketingName());

        // financial
        unitTo.financial().unitRent().setValue(info.getUnitRent());
        unitTo.financial().marketRent().setValue(info.getMarketRent());

        model.getAptUnits().add(unitTo);
    }

    /**
     * TODO for now we are not using this method
     */
    public void mapOccupancy(Information info) {
        AptUnitOccupancy occupancy = EntityFactory.create(AptUnitOccupancy.class);

        occupancies.add(occupancy);
    }

    public Model getModel() {
        return model;
    }
}
