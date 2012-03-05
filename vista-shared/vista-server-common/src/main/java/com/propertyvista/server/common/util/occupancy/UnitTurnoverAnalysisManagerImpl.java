/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 5, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.util.occupancy;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.Sort;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.dashboard.gadgets.availabilityreport.UnitTurnoversPerMonthInBuilding;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;

public class UnitTurnoverAnalysisManagerImpl implements UnitTurnoverAnalysisManager {

    /** recalculate turnover for the building, for the month that includes "until" */
    @Override
    public void recalculateTurnovers(Key building, LogicalDate until) {

        LogicalDate beginningOfTheMonth = new LogicalDate(until.getYear(), until.getMonth(), 1);
        LogicalDate beginningOfTheNextMonth = new LogicalDate(until.getYear() + (until.getMonth() + 1) / 12, (until.getMonth() + 1) % 12, 1);

        EntityQueryCriteria<AptUnitOccupancySegment> criteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit().belongsTo(), building));
        criteria.add(PropertyCriterion.ge(criteria.proto().dateTo(), beginningOfTheMonth));
        criteria.add(PropertyCriterion.le(criteria.proto().dateFrom(), until));
        criteria.add(PropertyCriterion.le(criteria.proto().status(), AptUnitOccupancySegment.Status.leased));
        List<Sort> sorts = Arrays.asList(new Sort(criteria.proto().dateFrom().getPath().toString(), false), new Sort(criteria.proto().unit().getPath()
                .toString(), false));
        criteria.setSorts(sorts);

        List<AptUnitOccupancySegment> leasedSegs = Persistence.secureQuery(criteria);
        int turnoverCount = 0;
        int leaseCount = 0;
        Key prevUnit = null;
        for (AptUnitOccupancySegment seg : leasedSegs) {
            Key unit = seg.unit().getPrimaryKey();
            ++leaseCount;
            if (!unit.equals(prevUnit)) {
                prevUnit = unit;
                if (leaseCount > 1) {
                    turnoverCount += leaseCount - 1;
                }
                leaseCount = 1;
            }
        }
        if (leaseCount > 1) {
            turnoverCount += leaseCount;
        }

        EntityQueryCriteria<UnitTurnoversPerMonthInBuilding> turnoversCriteria = EntityQueryCriteria.create(UnitTurnoversPerMonthInBuilding.class);
        turnoversCriteria.add(PropertyCriterion.eq(turnoversCriteria.proto().belongsTo(), building));
        turnoversCriteria.add(PropertyCriterion.lt(turnoversCriteria.proto().statsMonth(), beginningOfTheNextMonth));
        turnoversCriteria.add(PropertyCriterion.ge(turnoversCriteria.proto().statsMonth(), beginningOfTheMonth));
        UnitTurnoversPerMonthInBuilding turnovers = Persistence.service().retrieve(turnoversCriteria);
        if (turnovers == null) {
            turnovers = EntityFactory.create(UnitTurnoversPerMonthInBuilding.class);
            turnovers.belongsTo().setPrimaryKey(building);

        }
        turnovers.turnovers().setValue(turnoverCount);
        turnovers.statsMonth().setValue(until);
        Persistence.secureSave(turnovers);
    }
}
