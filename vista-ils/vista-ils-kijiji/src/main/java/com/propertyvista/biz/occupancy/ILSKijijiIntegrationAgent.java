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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Map<Building, ILSProfileBuilding> buildings;

    private Map<Floorplan, ILSProfileFloorplan> floorplans;

    public ILSKijijiIntegrationAgent() {
        // get ILSConfig
        EntityQueryCriteria<ILSVendorConfig> critIls = EntityQueryCriteria.create(ILSVendorConfig.class);
        critIls.eq(critIls.proto().vendor(), ILSVendor.kijiji);
        ilsCfg = Persistence.service().retrieve(critIls);
        if (ilsCfg == null) {
            return;
        }

        // create building profile map
        buildings = new HashMap<Building, ILSProfileBuilding>();
        EntityQueryCriteria<ILSProfileBuilding> critBld = EntityQueryCriteria.create(ILSProfileBuilding.class);
        critBld.eq(critBld.proto().vendor(), ILSVendor.kijiji);
        for (ILSProfileBuilding profile : Persistence.service().query(critBld)) {
            buildings.put(profile.building(), profile);
        }

        // create floorplan profile map
        floorplans = new HashMap<Floorplan, ILSProfileFloorplan>();
        EntityQueryCriteria<ILSProfileFloorplan> critFp = EntityQueryCriteria.create(ILSProfileFloorplan.class);
        critFp.eq(critFp.proto().vendor(), ILSVendor.kijiji);
        critFp.in(critFp.proto().floorplan().building(), buildings.keySet());
        for (ILSProfileFloorplan profile : Persistence.service().query(critFp)) {
            floorplans.put(profile.floorplan(), profile);
        }
    }

    /**
     * Provides a list of available units for publishing with ILS provider. Uses Occupancy model and ILSConfig data
     */
    public List<AptUnit> getUnitListing() {
        // get available units
        EntityQueryCriteria<AptUnit> critUnit = EntityQueryCriteria.create(AptUnit.class);
        critUnit.in(critUnit.proto().floorplan(), floorplans.keySet());
        critUnit.eq(critUnit.proto().unitOccupancySegments().$().status(), AptUnitOccupancySegment.Status.available);
        // TODO - dateTo, dateFrom
        List<AptUnit> units = Persistence.service().query(critUnit);

        // truncate and balance unit quantities according to floorplan priorities
        return truncateUnits(units, ilsCfg.maxDailyAds().getValue(), floorplans);
    }

    private List<AptUnit> truncateUnits(List<AptUnit> units, int maxSize, Map<Floorplan, ILSProfileFloorplan> floorplans) {
        if (units.size() <= ilsCfg.maxDailyAds().getValue()) {
            return units;
        }

        // generate priority map
        final Map<Floorplan, ILSProfileFloorplan.Priority> priority = new HashMap<Floorplan, ILSProfileFloorplan.Priority>();
        for (ILSProfileFloorplan profile : floorplans.values()) {
            priority.put(profile.floorplan(), profile.priority().getValue());
        }

        // simply sort by priority
        Collections.sort(units, new Comparator<AptUnit>() {
            @Override
            public int compare(AptUnit o1, AptUnit o2) {
                // highest priority first
                return priority.get(o1.floorplan()).compareTo(priority.get(o2.floorplan()));
            }
        });

        // truncate to max size
        return units.subList(0, maxSize - 1);
    }
}
