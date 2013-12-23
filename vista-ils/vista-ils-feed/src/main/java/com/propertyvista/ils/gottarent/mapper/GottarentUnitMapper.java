/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 5, 2013
 * @author smolka
 * @version $Id$
 */
package com.propertyvista.ils.gottarent.mapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.gottarent.rs.BuildingVacancies;
import com.gottarent.rs.BuildingVacancy;
import com.gottarent.rs.ObjectFactory;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.util.DomainUtil;
import com.propertyvista.ils.gottarent.mapper.dto.ILSFloorplanDTO;
import com.propertyvista.ils.gottarent.mapper.dto.ILSUnitDTO;

public class GottarentUnitMapper {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final short MAX_PRICE = 9999;

    private final ObjectFactory factory;

    private final com.propertyvista.domain.property.asset.building.Building building;

    public GottarentUnitMapper(ObjectFactory factory, com.propertyvista.domain.property.asset.building.Building building) {
        this.factory = factory;
        this.building = building;
    }

    public BuildingVacancies createBuildingFloorplanVacancies(Collection<ILSFloorplanDTO> fpList) {
        BuildingVacancies to = factory.createBuildingVacancies();
        List<BuildingVacancy> toVacancies = to.getBuildingVacancy();
        for (ILSFloorplanDTO fpDto : fpList) {
            List<BuildingVacancy> vacancies = createVacancies(fpDto);
            if (vacancies.size() > 0) {
                toVacancies.addAll(vacancies);
            }
        }
        return to;
    }

    private List<BuildingVacancy> createVacancies(ILSFloorplanDTO from) {
        List<BuildingVacancy> vacancies = new ArrayList<BuildingVacancy>();
        for (ILSUnitDTO unit : from.units()) {

            String id = getUnitId(unit);
            // if no mandatory fields, continue
            if (id == null || id.trim().isEmpty()) {
                continue;
            }

            BuildingVacancy to = factory.createBuildingVacancy();
            to.setExternalBuildingVacancySuiteID(id);
            to.setBuildingVacancyBedroomSize(GottarentMapperUtils.getBedrooms(from.floorplan().bedrooms().getValue()));
            to.setBuildingVacancyAvailabilityDate(getAvailability(unit.availability().getValue()));
            //to.setBuildingVacancyAltTag(value);// TODO: Smolka

            boolean isAvail = !unit.availability().isNull();
            to.setBuildingVacancyVisible(GottarentMapperUtils.boolean2String(isAvail));
            to.setBuildingVacancyEnabled(GottarentMapperUtils.boolean2String(isAvail));

            setPriceOptionalField(from, to);
            setVacancySizeOptionalField(from, to);
            setBathroomsOptionalField(from, to);

            vacancies.add(to);
        }
        return vacancies;
    }

    private String setBathroomsOptionalField(ILSFloorplanDTO from, BuildingVacancy to) {
        String bathrooms = GottarentMapperUtils.getBathrooms(from.floorplan().bathrooms().getValue(), from.floorplan().halfBath().getValue());
        if (bathrooms != null && bathrooms.isEmpty()) {
            to.setBuildingVacancyBaths(bathrooms);
        }
        return bathrooms;
    }

    private void setVacancySizeOptionalField(ILSFloorplanDTO from, BuildingVacancy to) {
        Integer vacancySize = DomainUtil.getAreaInSqFeet(from.floorplan().area(), from.floorplan().areaUnits());
        if (vacancySize != null && vacancySize.intValue() > 0) {
            to.setBuildingVacancySize(vacancySize.toString());// Smolka m^2 or feets; number or words?
        }
    }

    private void setPriceOptionalField(ILSFloorplanDTO from, BuildingVacancy to) {
        //should be int according to gottarent spec
        int price = from.minPrice().getValue().intValue();
        if (price > 0 && price <= MAX_PRICE) {
            to.setBuildingVacancyPrice(String.valueOf(price));
        }
    }

    private String getUnitId(ILSUnitDTO unitDto) {
        return GottarentBuildingMapper.getBuildingId(building) + "-" + unitDto.unitId().getValue();
    }

    private String getAvailability(LogicalDate available) {
        if (available == null) {
            return "No Vacancy";
        } else if (available.before(new LogicalDate())) {
            return "Available";
        } else {
            return new SimpleDateFormat(DATE_FORMAT).format(available);
        }
    }
}
