/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 10, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.biz.occupancy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.marketing.ils.ILSProfileBuilding;
import com.propertyvista.domain.marketing.ils.ILSProfileFloorplan;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.settings.ILSConfig.ILSVendor;
import com.propertyvista.domain.settings.ILSVendorConfig;
import com.propertyvista.ils.gottarent.mapper.dto.ILSBuildingDTO;
import com.propertyvista.ils.gottarent.mapper.dto.ILSFloorplanDTO;
import com.propertyvista.ils.gottarent.mapper.dto.ILSReportDTO;
import com.propertyvista.ils.gottarent.mapper.dto.ILSUnitDTO;

/**
 * Currently copy of kijiji agent + minor changes.
 * 
 * @author smolka
 * 
 */
// TODO: Smolka, make common solution based on strategy
public class ILSGottarentIntegrationAgent {
    private static Logger log = LoggerFactory.getLogger(ILSGottarentIntegrationAgent.class);

    public static final ILSVendor vendor = ILSVendor.kijiji;// TODO: Smolka, should be ILSVendor.gottarent

    private ILSVendorConfig ilsCfg;

    private Map<Building, ILSProfileBuilding> buildingMap;

    private Map<Floorplan, ILSProfileFloorplan> floorplanMap;

    private Map<Floorplan, ILSProfileFloorplan.Priority> priorityMap;

    public ILSGottarentIntegrationAgent() {
        // get ILSConfig
        EntityQueryCriteria<ILSVendorConfig> critIls = EntityQueryCriteria.create(ILSVendorConfig.class);
        critIls.eq(critIls.proto().vendor(), vendor);
        try {
            ilsCfg = Persistence.service().retrieve(critIls);
        } catch (Exception ignore) {
            // noop
        }
        if (ilsCfg == null) {
            return;
        }

        // create building profile map
        buildingMap = new HashMap<Building, ILSProfileBuilding>();
        EntityQueryCriteria<ILSProfileBuilding> critBld = EntityQueryCriteria.create(ILSProfileBuilding.class);
        critBld.eq(critBld.proto().vendor(), vendor);
        for (ILSProfileBuilding profile : Persistence.service().query(critBld)) {
            buildingMap.put(profile.building(), profile);
        }

        // create floorplan profile map
        floorplanMap = new HashMap<Floorplan, ILSProfileFloorplan>();
        EntityQueryCriteria<ILSProfileFloorplan> critFp = EntityQueryCriteria.create(ILSProfileFloorplan.class);
        critFp.eq(critFp.proto().vendor(), vendor);
        critFp.in(critFp.proto().floorplan().building(), buildingMap.keySet());
        for (ILSProfileFloorplan profile : Persistence.service().query(critFp)) {
            floorplanMap.put(profile.floorplan(), profile);
        }

        // generate priority map
        priorityMap = new HashMap<Floorplan, ILSProfileFloorplan.Priority>();
        for (ILSProfileFloorplan profile : floorplanMap.values()) {
            priorityMap.put(profile.floorplan(), profile.priority().getValue());
        }
    }

    /**
     * Provides a list of available units for publishing with ILS provider. Uses Occupancy model and ILSConfig data
     */
    public ILSReportDTO getUnitListing() {
        // get available units
        EntityQueryCriteria<AptUnit> critUnit = EntityQueryCriteria.create(AptUnit.class);
        critUnit.in(critUnit.proto().floorplan(), floorplanMap.keySet());
        //TODO: Smolka Each building unit
        //critUnit.isNotNull(critUnit.proto()._availableForRent());
        List<AptUnit> units = Persistence.service().query(critUnit);

        // extract floorplans and build availability map
        Map<Floorplan, ILSFloorplanDTO> fpDtoMap = new HashMap<Floorplan, ILSFloorplanDTO>();
        for (AptUnit unit : units) {
            Persistence.service().retrieve(unit.floorplan());
            ILSFloorplanDTO fpDto = fpDtoMap.get(unit.floorplan());
            if (fpDto == null) {
                fpDto = createDto(unit.floorplan());
                fpDtoMap.put(unit.floorplan(), fpDto);
            }
            if (fpDto.minPrice().isNull()
                    || (!unit.financial()._marketRent().isNull() && fpDto.minPrice().getValue().compareTo(unit.financial()._marketRent().getValue()) > 0)) {
                fpDto.minPrice().set(unit.financial()._marketRent());
            }

            ILSUnitDTO unitDTO = createDto(unit);
            fpDto.units().add(unitDTO);

        }

        // order by floorplan priorities and truncate if allowed size is exceeded
        List<Floorplan> floorplans = new ArrayList<Floorplan>(fpDtoMap.keySet());
        //floorplans = truncateList(floorplans, availMap);

        // rearrange listing by building
        int totalUnits = 0;
        Map<Building, List<ILSFloorplanDTO>> _listing = new HashMap<Building, List<ILSFloorplanDTO>>();
        for (Floorplan floorplan : floorplans) {
            ILSFloorplanDTO fpDto = fpDtoMap.get(floorplan);
            if (fpDto == null) {
                log.info("ILS Profile missing for floorplan: {}", floorplan.name().getValue());
                continue;
            }
            // do some sanity check
            // FIXME - revert compareTo(100) when done testing
            if (fpDto.minPrice().isNull() || fpDto.minPrice().getValue().compareTo(new BigDecimal(/* 10 */0)) < 0) {
                log.info("Market price invalid: {} for {}", fpDto.minPrice().getValue(), floorplan.name().getValue());
                continue;
            }
            Persistence.service().retrieveMember(floorplan.building(), AttachLevel.Attached);

            List<ILSFloorplanDTO> list = _listing.get(floorplan.building());
            if (list == null) {
                list = new ArrayList<ILSFloorplanDTO>();
                _listing.put(floorplan.building(), list);
            }
            // check if furnished
            EntityQueryCriteria<FloorplanAmenity> crit = EntityQueryCriteria.create(FloorplanAmenity.class);
            crit.eq(crit.proto().floorplan(), floorplan);
            crit.eq(crit.proto().type(), FloorplanAmenity.Type.furnished);
            fpDto.isFurnished().setValue(Persistence.service().count(crit) > 0);
            /*
             * // TODO - check if pets allowed
             * fpDto.isPetsAllowed().setValue(false);
             */
            list.add(fpDto);
            totalUnits++;
        }

        // create final listing object
        ILSReportDTO retportDto = createDto(totalUnits);
        for (Building building : _listing.keySet()) {
            Persistence.service().retrieveMember(building.amenities(), AttachLevel.Attached);

            ILSBuildingDTO bldDto = createDto(building);
            if (bldDto == null) {
                log.info("ILS Profile missing for building: {}", building.propertyCode().getValue());
                continue;
            }
            bldDto.floorplans().addAll(_listing.get(building));
            retportDto.buildings().add(bldDto);
        }

        return retportDto;
    }

    private ILSBuildingDTO createDto(Building building) {
        ILSProfileBuilding profile = buildingMap.get(building);
        if (profile == null) {
            return null;
        }
        ILSBuildingDTO dto = EntityFactory.create(ILSBuildingDTO.class);
        dto.building().set(building);
        dto.profile().set(profile);
        return dto;
    }

    private ILSReportDTO createDto(int totalUnits) {
        ILSReportDTO dto = EntityFactory.create(ILSReportDTO.class);

        dto.totalUnits().setValue(new java.lang.Integer(totalUnits));
        return dto;
    }

    private ILSUnitDTO createDto(AptUnit unit) {
        ILSUnitDTO dto = EntityFactory.create(ILSUnitDTO.class);
        dto.availability().setValue(unit._availableForRent().getValue());
        dto.externalId().setValue(unit.getPrimaryKey().toString());
        return dto;
    }

    private ILSFloorplanDTO createDto(Floorplan floorplan) {
        ILSProfileFloorplan profile = floorplanMap.get(floorplan);
        if (profile == null) {
            return null;
        }
        ILSFloorplanDTO dto = EntityFactory.create(ILSFloorplanDTO.class);
        dto.floorplan().set(floorplan);
        dto.profile().set(profile);
        return dto;
    }
}
