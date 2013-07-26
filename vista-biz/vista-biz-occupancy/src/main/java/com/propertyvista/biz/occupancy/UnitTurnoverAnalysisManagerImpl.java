/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-04
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.occupancy;

import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.dashboard.gadgets.availability.UnitTurnoversPerInterval;
import com.propertyvista.domain.dashboard.gadgets.common.TimeInterval;
import com.propertyvista.domain.dashboard.gadgets.common.TimeIntervalSize;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.tenant.lease.Lease;

public class UnitTurnoverAnalysisManagerImpl implements UnitTurnoverAnalysisManager {

    @Override
    public void updateUnitTurnover(Lease leaseId) {
        // TODO not required: reserved for future use / optimizations 
    }

    @Override
    public List<UnitTurnoversPerInterval> turnovers(TimeIntervalSize timeIntervalSize, LogicalDate from, LogicalDate to, List<Building> buildingIds) {
        TimeInterval currentInterval = new TimeInterval(timeIntervalSize.start(from), timeIntervalSize.end(from));
        List<UnitTurnoversPerInterval> result = new LinkedList<UnitTurnoversPerInterval>();

        while (currentInterval.getTo().compareTo(to) <= 0) {
            UnitTurnoversPerInterval turnoversPerInterval = new UnitTurnoversPerInterval(currentInterval, countTurnovers(currentInterval.getFrom(),
                    currentInterval.getTo(), buildingIds));
            result.add(turnoversPerInterval);

            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(currentInterval.getTo());
            cal.add(GregorianCalendar.DAY_OF_MONTH, 1);
            currentInterval = new TimeInterval(timeIntervalSize.start(new LogicalDate(cal.getTime())), timeIntervalSize.end(new LogicalDate(cal.getTime())));
        }

        return result;
    }

    private int countTurnovers(LogicalDate from, LogicalDate to, List<Building> buildingIds) {
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        if (!buildingIds.isEmpty()) {
            criteria.in(criteria.proto().unit().building(), buildingIds);
        }
        criteria.ge(criteria.proto().dateTo(), from);
        criteria.le(criteria.proto().dateTo(), to);
        Persistence.applyDatasetAccessRule(criteria);
        return Persistence.service().count(criteria);
    }
}
