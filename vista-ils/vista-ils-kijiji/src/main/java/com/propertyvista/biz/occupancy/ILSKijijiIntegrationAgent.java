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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.marketing.ils.ILSProfileBuilding;
import com.propertyvista.domain.marketing.ils.ILSProfileFloorplan;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.settings.ILSConfig.ILSVendor;
import com.propertyvista.domain.settings.ILSVendorConfig;

public class ILSKijijiIntegrationAgent {

    private ILSVendorConfig ilsCfg;

    private Map<Building, ILSProfileBuilding> buildingMap;

    private Map<Floorplan, ILSProfileFloorplan> floorplanMap;

    private Map<Floorplan, ILSProfileFloorplan.Priority> priorityMap;

    public ILSKijijiIntegrationAgent() {
        // get ILSConfig
        EntityQueryCriteria<ILSVendorConfig> critIls = EntityQueryCriteria.create(ILSVendorConfig.class);
        critIls.eq(critIls.proto().vendor(), ILSVendor.kijiji);
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
        critBld.eq(critBld.proto().vendor(), ILSVendor.kijiji);
        for (ILSProfileBuilding profile : Persistence.service().query(critBld)) {
            buildingMap.put(profile.building(), profile);
        }

        // create floorplan profile map
        floorplanMap = new HashMap<Floorplan, ILSProfileFloorplan>();
        EntityQueryCriteria<ILSProfileFloorplan> critFp = EntityQueryCriteria.create(ILSProfileFloorplan.class);
        critFp.eq(critFp.proto().vendor(), ILSVendor.kijiji);
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
    public Map<Building, List<AptUnit>> getUnitListing() {
        // get available units
        EntityQueryCriteria<AptUnit> critUnit = EntityQueryCriteria.create(AptUnit.class);
        critUnit.in(critUnit.proto().floorplan(), floorplanMap.keySet());
        critUnit.eq(critUnit.proto().unitOccupancySegments().$().status(), AptUnitOccupancySegment.Status.available);
        critUnit.eq(critUnit.proto().unitOccupancySegments().$().dateTo(), OccupancyFacade.MAX_DATE);
        List<AptUnit> units = Persistence.service().query(critUnit);

        // truncate and balance unit quantities according to floorplan priorities
        units = truncateUnits(units);

        Map<Building, List<AptUnit>> listing = new HashMap<Building, List<AptUnit>>();
        for (AptUnit unit : units) {
            List<AptUnit> list = listing.get(unit.building());
            if (list == null) {
                list = new ArrayList<AptUnit>();
                listing.put(unit.building(), list);
            }
            list.add(unit);
        }

        return listing;
    }

    private List<AptUnit> truncateUnits(List<AptUnit> units) {
        int maxSize = ilsCfg.maxDailyAds().getValue();
        if (units.size() <= maxSize) {
            return units;
        }

        // get availability segments
        final Map<AptUnit, LogicalDate> unitAvail = new HashMap<AptUnit, LogicalDate>();
        EntityQueryCriteria<AptUnitOccupancySegment> critAvail = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        critAvail.in(critAvail.proto().unit(), units);
        critAvail.eq(critAvail.proto().status(), AptUnitOccupancySegment.Status.available);
        critAvail.eq(critAvail.proto().dateTo(), OccupancyFacade.MAX_DATE);
        for (AptUnitOccupancySegment avail : Persistence.service().query(critAvail)) {
            unitAvail.put(avail.unit(), avail.dateFrom().getValue());
        }

        // sort by total score, highest first
        Collections.sort(units, new Comparator<AptUnit>() {
            @Override
            public int compare(AptUnit o1, AptUnit o2) {
                int score2 = getTotalScore(priorityMap.get(o2.floorplan()), unitAvail.get(o2));
                int score1 = getTotalScore(priorityMap.get(o1.floorplan()), unitAvail.get(o1));
                return score2 - score1;
            }
        });

        // truncate to max size
        return units.subList(0, maxSize - 1);
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
