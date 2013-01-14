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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.property.asset.AreaMeasurementUnit;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitInfo.EconomicStatus;
import com.propertyvista.yardi.bean.mits.Information;
import com.propertyvista.yardi.bean.resident.RTUnit;

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
     * @param building
     *            the building where units from
     * @param unitsFrom
     *            the units which map from
     * @return the mapped units
     */
    public List<AptUnit> map(List<RTUnit> unitsFrom) {
        List<AptUnit> mapped = new ArrayList<AptUnit>();
        for (RTUnit rtUnit : unitsFrom) {
            try {
                AptUnit unit = map(rtUnit);
                mapped.add(unit);
            } catch (Exception e) {
                log.error(String.format("Error during imported unit %s mapping", rtUnit.getUnitId()), e);
            }
        }
        return mapped;
    }

    private AptUnit map(RTUnit unitFrom) {
        AptUnit unitTo = EntityFactory.create(AptUnit.class);
        Information info = unitFrom.getUnit().getInformation();

        if (StringUtils.isEmpty(info.getUnitId())) {
            throw new IllegalStateException("Illegal UnitId. Can not be empty or null");
        }

        //floorplan
        if (StringUtils.isEmpty(info.getFloorplanName())) {
            throw new IllegalStateException(String.format("Illegal FloorplanName. FloorplanName for imported unit %s can not be empty or null",
                    info.getUnitId()));
        }

        Floorplan floorplan = EntityFactory.create(Floorplan.class);
        floorplan.name().setValue(info.getFloorplanName());
        floorplan.bedrooms().setValue(info.getUnitBedrooms() != null ? info.getUnitBedrooms().intValue() : null);
        floorplan.bathrooms().setValue(info.getUnitBathrooms() != null ? info.getUnitBathrooms().intValue() : null);

        unitTo.floorplan().set(floorplan);

        // info
        unitTo.info().number().setValue(info.getUnitId());
        unitTo.info()._bedrooms().setValue(info.getUnitBedrooms() != null ? info.getUnitBedrooms().intValue() : null);
        unitTo.info()._bathrooms().setValue(info.getUnitBathrooms() != null ? info.getUnitBathrooms().intValue() : null);
        unitTo.info().area().setValue(info.getMaxSquareFeet() != null ? info.getMaxSquareFeet().doubleValue() : null);
        unitTo.info().areaUnits().setValue(AreaMeasurementUnit.sqFeet);

        if (StringUtils.equals(info.getUnitEconomicStatus(), "residential")) {
            unitTo.info().economicStatus().setValue(EconomicStatus.residential);
        } else {
            log.debug("Unknown economic status {}", info.getUnitEconomicStatus());
            unitTo.info().economicStatus().setValue(EconomicStatus.other);
        }

        // marketing
        unitTo.marketing().name().setValue(unitFrom.getUnit().getMarketingName());

        // financial
        unitTo.financial()._unitRent().setValue(info.getUnitRent());
        unitTo.financial()._marketRent().setValue(info.getMarketRent());

        return unitTo;
    }
}
