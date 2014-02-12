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
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.essentials.server.preloader.DataGenerator;

import com.propertyvista.domain.marketing.ils.ILSProfileBuilding;
import com.propertyvista.domain.marketing.ils.ILSProfileFloorplan;
import com.propertyvista.domain.marketing.ils.ILSProfileFloorplan.Priority;
import com.propertyvista.domain.marketing.ils.ILSSummaryBuilding;
import com.propertyvista.domain.marketing.ils.ILSSummaryFloorplan;
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

    private Map<Building, ILSProfileBuilding> buildingProfileMap;

    private Map<Building, List<ILSSummaryBuilding>> buildingSummaryMap;

    private Map<Floorplan, ILSProfileFloorplan> floorplanProfileMap;

    private Map<Floorplan, List<ILSSummaryFloorplan>> floorplanSummaryMap;

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
        buildingProfileMap = new HashMap<Building, ILSProfileBuilding>();
        buildingSummaryMap = new HashMap<Building, List<ILSSummaryBuilding>>();
        EntityQueryCriteria<ILSProfileBuilding> critBld = EntityQueryCriteria.create(ILSProfileBuilding.class);
        critBld.eq(critBld.proto().vendor(), vendor);
        critBld.or(PropertyCriterion.isNull(critBld.proto().disabled()), PropertyCriterion.eq(critBld.proto().disabled(), false));
        for (ILSProfileBuilding profile : Persistence.service().query(critBld)) {
            buildingProfileMap.put(profile.building(), profile);
            // add ils summary
            EntityQueryCriteria<ILSSummaryBuilding> crit = EntityQueryCriteria.create(ILSSummaryBuilding.class);
            crit.eq(crit.proto().building(), profile.building());
            buildingSummaryMap.put(profile.building(), Persistence.service().query(crit));
        }

        // create floorplan profile map
        floorplanProfileMap = new HashMap<Floorplan, ILSProfileFloorplan>();
        floorplanSummaryMap = new HashMap<Floorplan, List<ILSSummaryFloorplan>>();
        if (buildingProfileMap.size() > 0) {
            EntityQueryCriteria<ILSProfileFloorplan> critFp = EntityQueryCriteria.create(ILSProfileFloorplan.class);
            critFp.eq(critFp.proto().vendor(), vendor);
            critFp.ne(critFp.proto().priority(), Priority.Disabled);
            critFp.in(critFp.proto().floorplan().building(), buildingProfileMap.keySet());
            for (ILSProfileFloorplan profile : Persistence.service().query(critFp)) {
                floorplanProfileMap.put(profile.floorplan(), profile);
                EntityQueryCriteria<ILSSummaryFloorplan> crit = EntityQueryCriteria.create(ILSSummaryFloorplan.class);
                crit.eq(crit.proto().floorplan(), profile.floorplan());
                floorplanSummaryMap.put(profile.floorplan(), Persistence.service().query(crit));
            }
        }

        // generate priority map
        priorityMap = new HashMap<Floorplan, ILSProfileFloorplan.Priority>();
        for (ILSProfileFloorplan profile : floorplanProfileMap.values()) {
            priorityMap.put(profile.floorplan(), profile.priority().getValue());
        }
    }

    /**
     * Provides a list of available units for publishing with ILS provider. Uses Occupancy model and ILSConfig data
     */
    public Map<ILSBuildingDTO, List<ILSFloorplanDTO>> getUnitListing() {
        // get available units
        List<AptUnit> units = null;
        if (floorplanProfileMap.size() > 0) {
            EntityQueryCriteria<AptUnit> critUnit = EntityQueryCriteria.create(AptUnit.class);
            critUnit.in(critUnit.proto().floorplan(), floorplanProfileMap.keySet());
            critUnit.isNotNull(critUnit.proto().availability().availableForRent());
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
            LogicalDate dateFrom = unit.availability().availableForRent().getValue();
            if (bestAvail == null || bestAvail.after(dateFrom)) {
                availMap.put(unit.floorplan(), dateFrom);
            }
        }

        // rearrange listing by building
        Map<Building, List<ILSFloorplanDTO>> _listing = new HashMap<Building, List<ILSFloorplanDTO>>();
        for (Floorplan floorplan : fpDtoMap.keySet()) {
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
        int availSize = ilsCfg.maxDailyAds().getValue(0);
        for (Building building : _listing.keySet()) {
            // order by floorplan priorities and truncate if allowed size is exceeded
            int bldMaxAds = buildingProfileMap.get(building).maxAds().getValue(0);
            List<ILSFloorplanDTO> floorplans = truncateList(_listing.get(building), availMap, bldMaxAds, availSize);
            if (floorplans == null || floorplans.size() == 0) {
                continue;
            }

            ILSBuildingDTO bldDto = createDto(building);
            if (bldDto == null) {
                log.info("ILS Profile missing for building: {}", building.propertyCode().getValue());
                continue;
            }
            listing.put(bldDto, floorplans);
            availSize -= floorplans.size();
        }

        return listing;
    }

    private ILSBuildingDTO createDto(Building building) {
        ILSProfileBuilding profile = buildingProfileMap.get(building);
        if (profile == null) {
            return null;
        }
        ILSBuildingDTO dto = EntityFactory.create(ILSBuildingDTO.class);
        dto.building().set(building);
        // set ils summary
        List<ILSSummaryBuilding> summary = buildingSummaryMap.get(building);
        if (summary != null && summary.size() > 0) {
            dto.ilsSummary().set(summary.get(DataGenerator.randomInt(summary.size())));
            Persistence.ensureRetrieve(dto.ilsSummary(), AttachLevel.Attached);
        }
        // set ils profile
        dto.profile().set(profile);
        return dto;
    }

    private ILSFloorplanDTO createDto(Floorplan floorplan) {
        ILSProfileFloorplan profile = floorplanProfileMap.get(floorplan);
        if (profile == null) {
            return null;
        }
        ILSFloorplanDTO dto = EntityFactory.create(ILSFloorplanDTO.class);
        dto.floorplan().set(floorplan);
        // set ils summary
        List<ILSSummaryFloorplan> summary = floorplanSummaryMap.get(floorplan);
        if (summary != null && summary.size() > 0) {
            dto.ilsSummary().set(summary.get(DataGenerator.randomInt(summary.size())));
            Persistence.ensureRetrieve(dto.ilsSummary(), AttachLevel.Attached);
        }
        // set ils profile
        dto.profile().set(profile);
        return dto;
    }

    private List<ILSFloorplanDTO> truncateList(List<ILSFloorplanDTO> list, final Map<Floorplan, LogicalDate> availMap, int maxAds, int availSize) {
        int maxSize = Math.min(maxAds, Math.max(0, availSize));
        if (list.size() <= maxSize) {
            return list;
        }

        // sort by total score, highest first
        Collections.sort(list, new Comparator<ILSFloorplanDTO>() {
            @Override
            public int compare(ILSFloorplanDTO dto1, ILSFloorplanDTO dto2) {
                int score2 = getTotalScore(priorityMap.get(dto2.floorplan()), availMap.get(dto2.floorplan()));
                int score1 = getTotalScore(priorityMap.get(dto1.floorplan()), availMap.get(dto1.floorplan()));
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
        int days = (int) ((availableFrom.getTime() - SystemDateManager.getTimeMillis()) / (24 * 3600 * 1000));
        if (days < 0) {
            // avail now
            availabilityScore = 10;
        } else if (days > MAX_DAYS) {
            // not avail within MAX_DAYS
            availabilityScore = 0;
        } else {
            // avail within MAX_DAYS - the sooner the better
            availabilityScore = (int) Math.round((MAX_DAYS - days) / 10.0);
        }
        return availabilityScore;
    }
}
