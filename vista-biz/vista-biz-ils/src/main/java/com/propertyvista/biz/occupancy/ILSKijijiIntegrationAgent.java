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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.domain.marketing.ils.ILSProfileBuilding;
import com.propertyvista.domain.marketing.ils.ILSProfileFloorplan;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.FloorplanAmenity;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.settings.ILSConfig.ILSVendor;
import com.propertyvista.domain.settings.ILSVendorConfig;
import com.propertyvista.ils.kijiji.mapper.dto.ILSBuildingDTO;
import com.propertyvista.ils.kijiji.mapper.dto.ILSFloorplanDTO;

public class ILSKijijiIntegrationAgent {
    private static Logger log = LoggerFactory.getLogger(ILSKijijiIntegrationAgent.class);

    public static final ILSVendor vendor = ILSVendor.kijiji;

    private ILSVendorConfig ilsCfg;

    private Map<Building, ILSProfileBuilding> buildingMap;

    private Map<Floorplan, ILSProfileFloorplan> floorplanMap;

    private Map<Floorplan, ILSProfileFloorplan.Priority> priorityMap;

    public ILSKijijiIntegrationAgent() {
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
        critBld.ne(critBld.proto().disabled(), true);
        for (ILSProfileBuilding profile : Persistence.service().query(critBld)) {
            buildingMap.put(profile.building(), profile);
        }

        // create floorplan profile map
        floorplanMap = new HashMap<Floorplan, ILSProfileFloorplan>();
        if (buildingMap.size() > 0) {
            EntityQueryCriteria<ILSProfileFloorplan> critFp = EntityQueryCriteria.create(ILSProfileFloorplan.class);
            critFp.eq(critFp.proto().vendor(), vendor);
            critFp.in(critFp.proto().floorplan().building(), buildingMap.keySet());
            for (ILSProfileFloorplan profile : Persistence.service().query(critFp)) {
                floorplanMap.put(profile.floorplan(), profile);
            }
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
    public Map<ILSBuildingDTO, List<ILSFloorplanDTO>> getUnitListing() {
        // get available units
        List<AptUnit> units = null;
        if (floorplanMap.size() > 0) {
            EntityQueryCriteria<AptUnit> critUnit = EntityQueryCriteria.create(AptUnit.class);
            critUnit.in(critUnit.proto().floorplan(), floorplanMap.keySet());
            critUnit.isNotNull(critUnit.proto()._availableForRent());
            units = Persistence.service().query(critUnit);
        } else {
            units = new ArrayList<AptUnit>();
        }

        // extract floorplans and build availability map
        Map<Floorplan, ILSFloorplanDTO> fpDtoMap = new HashMap<Floorplan, ILSFloorplanDTO>();
        final Map<Floorplan, LogicalDate> availMap = new HashMap<Floorplan, LogicalDate>();
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
            // calculate best availability date for each floorplan
            LogicalDate bestAvail = availMap.get(unit.floorplan());
            LogicalDate dateFrom = unit._availableForRent().getValue();
            if (bestAvail == null || bestAvail.after(dateFrom)) {
                availMap.put(unit.floorplan(), dateFrom);
            }
        }

        // order by floorplan priorities and truncate if allowed size is exceeded
        List<Floorplan> floorplans = new ArrayList<Floorplan>(fpDtoMap.keySet());
        floorplans = truncateList(floorplans, availMap);

        // rearrange listing by building
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

            // TODO - check if pets allowed
            fpDto.isPetsAllowed().setValue(false);

            list.add(fpDto);
        }

        // create final listing object
        Map<ILSBuildingDTO, List<ILSFloorplanDTO>> listing = new HashMap<ILSBuildingDTO, List<ILSFloorplanDTO>>();
        for (Building building : _listing.keySet()) {
            ILSBuildingDTO bldDto = createDto(building);
            if (bldDto == null) {
                log.info("ILS Profile missing for building: {}", building.propertyCode().getValue());
                continue;
            }
            listing.put(bldDto, _listing.get(building));
        }

        return listing;
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

    private ILSFloorplanDTO createDto(Floorplan floorplan) {
        ILSProfileFloorplan profile = floorplanMap.get(floorplan);
        if (profile == null) {
            return null;
        }
        ILSFloorplanDTO dto = EntityFactory.create(ILSFloorplanDTO.class);
        dto.floorplan().set(floorplan);
        if (!floorplan.ilsSummary().isEmpty()) {
            int count = floorplan.ilsSummary().size();
            dto.ilsSummary().set(floorplan.ilsSummary().get(DataGenerator.randomInt(count)));
            Persistence.ensureRetrieve(dto.ilsSummary(), AttachLevel.Attached);
        }
        dto.profile().set(profile);
        return dto;
    }

    private List<Floorplan> truncateList(List<Floorplan> list, final Map<Floorplan, LogicalDate> availMap) {
        int maxSize = ilsCfg.maxDailyAds().getValue();
        if (list.size() <= maxSize) {
            return list;
        }

        // sort by total score, highest first
        Collections.sort(list, new Comparator<Floorplan>() {
            @Override
            public int compare(Floorplan f1, Floorplan f2) {
                int score2 = getTotalScore(priorityMap.get(f2), availMap.get(f2));
                int score1 = getTotalScore(priorityMap.get(f1), availMap.get(f1));
                return score2 - score1;
            }
        });

        // truncate to max size
        return list.subList(0, maxSize);
    }

    /** Returns total score 0-10 */
    public static int getTotalScore(ILSProfileFloorplan.Priority priority, LogicalDate availableFrom) {
        // each specific score can range 0-10; the result is the normalized product of scores
        return (int) Math.round(10.0 * (getPriorityScore(priority) / 10.0) * (getAvailabilityScore(availableFrom) / 10.0));
    }

    private static int getPriorityScore(ILSProfileFloorplan.Priority priority) {
        int priorityScore = 0;
        switch (priority) {
        case High:
            priorityScore = 10;
            break;
        case Normal:
            priorityScore = 5;
            break;
        case Low:
            priorityScore = 2;
            break;
        case Disabled:
        default:
            priorityScore = 0;
        }
        return priorityScore;
    }

    private static int getAvailabilityScore(LogicalDate availableFrom) {
        int availabilityScore = 0;
        int MAX_DAYS = 100;
        int days = (int) ((SystemDateManager.getTimeMillis() - availableFrom.getTime()) / (24 * 3600 * 1000));
        if (days < 0) {
            availabilityScore = 10;
        } else if (days > MAX_DAYS) {
            availabilityScore = 0;
        } else {
            availabilityScore = (int) Math.round((MAX_DAYS - days) / 10.0);
        }
        return availabilityScore;
    }
}
