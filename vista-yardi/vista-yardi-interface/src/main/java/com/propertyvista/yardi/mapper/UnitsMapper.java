/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 4, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.yardi.mapper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.mits.Address;
import com.yardi.entity.mits.Information;
import com.yardi.entity.mits.Uniteconstatusinfo;
import com.yardi.entity.resident.RTCustomer;
import com.yardi.entity.resident.RTUnit;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.contact.AddressStructured;
import com.propertyvista.domain.property.asset.AreaMeasurementUnit;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitInfo.EconomicStatus;
import com.propertyvista.server.common.util.CanadianStreetAddressParser;
import com.propertyvista.server.common.util.StreetAddressParser.StreetAddress;

/**
 * Maps units information from YARDI System to domain entities.
 * 
 * @author Mykola
 * 
 */
public class UnitsMapper {

    private final static Logger log = LoggerFactory.getLogger(UnitsMapper.class);

    /**
     * Maps units from YARDI System to VISTA domain units
     * 
     */
    public AptUnit map(RTCustomer rtCustomer) {
        RTUnit unitFrom = rtCustomer.getRTUnit();
        AptUnit unitTo = EntityFactory.create(AptUnit.class);
        Information info = unitFrom.getUnit().getInformation().get(0);

        if (StringUtils.isEmpty(info.getUnitID())) {
            throw new IllegalStateException("Illegal UnitId. Can not be empty or null");
        }

        //floorplan
        Floorplan floorplan = EntityFactory.create(Floorplan.class);
        String floorplanName = info.getFloorplanName();
        if (StringUtils.isEmpty(floorplanName)) {
            StringBuilder builder = new StringBuilder();
            floorplanName = builder.append(info.getUnitBedrooms()).append("bed").append(info.getUnitBathrooms()).append("bath").toString();
        }

        floorplan.name().setValue(floorplanName);
        floorplan.bedrooms().setValue(info.getUnitBedrooms() != null ? info.getUnitBedrooms().intValue() : null);
        floorplan.bathrooms().setValue(info.getUnitBathrooms() != null ? info.getUnitBathrooms().intValue() : null);

        unitTo.floorplan().set(floorplan);

        // info
        unitTo.info().number().setValue(info.getUnitID());
        unitTo.info()._bedrooms().setValue(info.getUnitBedrooms() != null ? info.getUnitBedrooms().intValue() : null);
        unitTo.info()._bathrooms().setValue(info.getUnitBathrooms() != null ? info.getUnitBathrooms().intValue() : null);
        unitTo.info().area().setValue(info.getMaxSquareFeet() != null ? info.getMaxSquareFeet().doubleValue() : null);
        unitTo.info().areaUnits().setValue(AreaMeasurementUnit.sqFeet);

        if (info.getUnitEcomomicStatus() == Uniteconstatusinfo.RESIDENTIAL) {
            unitTo.info().economicStatus().setValue(EconomicStatus.residential);
        } else {
            log.debug("Unknown economic status {}", info.getUnitEcomomicStatus());
            unitTo.info().economicStatus().setValue(EconomicStatus.other);
        }
        unitTo.info().economicStatusDescription().setValue(info.getUnitEconomicStatusDescription());

        // Legal address
        if (rtCustomer.getCustomers().getCustomer().get(0).getAddress().size() > 0) {
            Address addressImported = rtCustomer.getCustomers().getCustomer().get(0).getAddress().get(0);
            StringBuilder address2 = new StringBuilder();
            for (String addressPart : addressImported.getAddress2()) {
                if (address2.length() > 0) {
                    address2.append("\n");
                }
                address2.append(addressPart);
            }
            StreetAddress streetAddress = new CanadianStreetAddressParser().parse(CommonsStringUtils.nvl(addressImported.getAddress1()), address2.toString());

            AddressStructured address = EntityFactory.create(AddressStructured.class);
            address.streetNumber().setValue(streetAddress.streetNumber);
            address.streetName().setValue(streetAddress.streetName);
            address.streetType().setValue(streetAddress.streetType);
            address.streetDirection().setValue(streetAddress.streetDirection);
            address.city().setValue(addressImported.getCity());

            address.province().code().setValue(addressImported.getState());

            String importedCountry = addressImported.getCountry();
            address.country().name().setValue(StringUtils.isEmpty(importedCountry) ? MappingUtils.getCountry(addressImported.getState()) : importedCountry);

            address.postalCode().setValue(addressImported.getPostalCode());

            unitTo.info().legalAddress().set(address);
        }

        // marketing
        unitTo.marketing().name().setValue(unitFrom.getUnit().getMarketingName());

        // financial
        unitTo.financial()._unitRent().setValue(info.getUnitRent());
        unitTo.financial()._marketRent().setValue(info.getMarketRent());

        return unitTo;
    }
}
