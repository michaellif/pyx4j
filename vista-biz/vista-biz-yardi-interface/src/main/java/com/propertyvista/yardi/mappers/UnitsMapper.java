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
package com.propertyvista.yardi.mappers;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.mits.Information;
import com.yardi.entity.mits.Unit;

import com.pyx4j.entity.core.EntityFactory;

import com.propertyvista.domain.property.asset.AreaMeasurementUnit;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.AptUnitInfo.EconomicStatus;

/**
 * Maps units information from YARDI System to domain entities.
 * 
 * @author Mykola
 * 
 */
public class UnitsMapper {

    private final static Logger log = LoggerFactory.getLogger(UnitsMapper.class);

    static public String getUnitNumber(String number) {
        return number.toUpperCase();
    }

    static public String getUnitNumber(Unit unit) {
        return getUnitNumber(unit.getInformation().get(0).getUnitID());
    }

    /**
     * Maps units from YARDI System to VISTA domain units
     * 
     */
    public AptUnit map(Unit unit) {
        AptUnit unitTo = EntityFactory.create(AptUnit.class);
        Information info = unit.getInformation().get(0);
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
        floorplan.code().setValue(info.getFloorPlanID());
        floorplan.bedrooms().setValue(info.getUnitBedrooms() != null ? info.getUnitBedrooms().intValue() : null);
        floorplan.bathrooms().setValue(info.getUnitBathrooms() != null ? info.getUnitBathrooms().intValue() : null);
        floorplan.area().setValue(getArea(info.getMinSquareFeet(), info.getMaxSquareFeet()));
        floorplan.areaUnits().setValue(AreaMeasurementUnit.sqFeet);

        unitTo.floorplan().set(floorplan);

        // info
        unitTo.info().number().setValue(getUnitNumber(unit));
        unitTo.info()._bedrooms().set(floorplan.bedrooms());
        unitTo.info()._bathrooms().set(floorplan.bathrooms());
        unitTo.info().area().set(floorplan.area());
        unitTo.info().areaUnits().set(floorplan.areaUnits());
        if ((info.getUnitEcomomicStatus()) != null) {
            switch (info.getUnitEcomomicStatus()) {
            case RESIDENTIAL:
                unitTo.info().economicStatus().setValue(EconomicStatus.residential);
                break;
            case COMMERCIAL:
                unitTo.info().economicStatus().setValue(EconomicStatus.commercial);
                break;
            case OTHER:
                unitTo.info().economicStatus().setValue(EconomicStatus.other);
                break;
            default:
                unitTo.info().economicStatus().setValue(EconomicStatus.other);
                log.debug("Unsupported unit economic status ('{}') for unit {} - imported as 'other'", info.getUnitEcomomicStatus(), info.getUnitID());
                break;
            }
        } else {
            unitTo.info().economicStatus().setValue(EconomicStatus.other);
            log.debug("NULL unit economic status ('{}') for unit {} - imported as 'other'", info.getUnitEcomomicStatus(), info.getUnitID());
        }

        unitTo.info().economicStatusDescription().setValue(info.getUnitEconomicStatusDescription());

        // financial
        unitTo.financial()._marketRent().setValue(info.getMarketRent() == null ? null : info.getMarketRent().setScale(2));

        return unitTo;
    }

    private Double getArea(Integer minArea, Integer maxArea) {
        Double result = null;
        if (minArea != null) {
            result = minArea.doubleValue();
        } else if (maxArea != null) {
            result = maxArea.doubleValue();
        }
        return result;
    }
}
