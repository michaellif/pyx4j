/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 6, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertvista.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.tenant.lease.Lease;

/**
 * This one currently knows to generate leased segment that are consistent with
 * 
 */
public class UnitOccupancyGenerator {

    public List<AptUnitOccupancySegment> geterateOccupancy(List<Lease> leases) {
        List<AptUnitOccupancySegment> result = new ArrayList<AptUnitOccupancySegment>();
        Collections.sort(leases, new Comparator<Lease>() {
            @Override
            public int compare(Lease arg0, Lease arg1) {
                return arg0.leaseFrom().compareTo(arg1.leaseFrom());
            }
        });

        Map<Key, List<AptUnitOccupancySegment>> unitOccupancyMap = new HashMap<Key, List<AptUnitOccupancySegment>>();

        for (final Lease lease : leases) {
            if (!lease.unit().id().isNull() & !lease.status().isNull()) {

                List<AptUnitOccupancySegment> occupancy = null;

                if ((occupancy = unitOccupancyMap.get(lease.unit().id().getValue())) == null) {
                    occupancy = new ArrayList<AptUnitOccupancySegment>();
                    unitOccupancyMap.put(lease.unit().id().getValue(), occupancy);
                }

                // TODO update occupancy based on lease
            }
        }
        return result;
    }

    private AptUnitOccupancySegment availableSegment(AptUnit unit, LogicalDate start, LogicalDate end) {
        AptUnitOccupancySegment segment = EntityFactory.create(AptUnitOccupancySegment.class);
        return segment;
    }
}
