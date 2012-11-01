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
package com.propertyvista.biz.occupancy;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;

import com.propertyvista.domain.dashboard.gadgets.availability.UnitTurnoverStats;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.tenant.lease.Lease;

public class UnitTurnoverAnalysisFacadeImpl implements UnitTurnoverAnalysisFacade {

    @Override
    public int turnoversSinceBeginningOfTheMonth(LogicalDate asOf, Key... buildings) {
        int totalTurnovers = 0;
        LogicalDate beginningOfTheMonth = new LogicalDate(asOf.getYear(), asOf.getMonth(), 1);

        for (Key builidngPk : buildings) {
            EntityQueryCriteria<UnitTurnoverStats> criteria = EntityQueryCriteria.create(UnitTurnoverStats.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().building(), builidngPk));
            criteria.add(PropertyCriterion.ge(criteria.proto().updatedOn(), beginningOfTheMonth));
            criteria.add(PropertyCriterion.le(criteria.proto().updatedOn(), asOf));
            criteria.desc(criteria.proto().updatedOn());
            UnitTurnoverStats stats = Persistence.secureRetrieve(criteria);
            if (stats != null) {
                totalTurnovers += stats.turnovers().getValue();
            }
        }

        return totalTurnovers;
    }

    @Override
    public void propagateLeaseActivationToTurnoverReport(Lease lease) {
        if (lease.status().getValue() != Lease.Status.Active) {
            throw new IllegalStateException("this function is only applicable to ACTIVE leases");
        }

        if (hasTurnover(lease)) {
            addTurnover(lease.unit().getPrimaryKey(), lease.currentTerm().termFrom().getValue());
        }
    }

    public boolean hasTurnover(Lease lease) {
        LogicalDate leaseFrom = lease.currentTerm().termFrom().getValue();
        LogicalDate beginningOfTheMonth = new LogicalDate(leaseFrom.getYear(), leaseFrom.getMonth(), 1);

        // check if we have lease that ended on the same month
        EntityQueryCriteria<AptUnitOccupancySegment> criteria = EntityQueryCriteria.create(AptUnitOccupancySegment.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().unit(), lease.unit()));
        criteria.add(PropertyCriterion.ge(criteria.proto().dateTo(), beginningOfTheMonth));
        criteria.add(PropertyCriterion.lt(criteria.proto().dateTo(), leaseFrom));
        criteria.add(PropertyCriterion.eq(criteria.proto().status(), AptUnitOccupancySegment.Status.occupied));

        return Persistence.service().count(criteria) != 0;
    }

    public void addTurnover(Key unitPk, LogicalDate when) {
        // try to retrieve the last known turnover statistics record for the building that owns this builing
        AptUnit unit = Persistence.secureRetrieve(AptUnit.class, unitPk);
        LogicalDate beginningOfTheMonth = new LogicalDate(when.getYear(), when.getMonth(), 1);
        EntityQueryCriteria<UnitTurnoverStats> criteria = EntityQueryCriteria.create(UnitTurnoverStats.class);
        criteria.add(PropertyCriterion.eq(criteria.proto().building(), unit.building()));
        criteria.add(PropertyCriterion.ge(criteria.proto().updatedOn(), beginningOfTheMonth));
        criteria.add(PropertyCriterion.le(criteria.proto().updatedOn(), when));
        criteria.desc(criteria.proto().updatedOn());
        UnitTurnoverStats stats = Persistence.secureRetrieve(criteria);

        if (stats == null) {
            stats = EntityFactory.create(UnitTurnoverStats.class);
            stats.building().set(unit.building());
            stats.turnovers().setValue(1);
        } else {
            UnitTurnoverStats newStats = EntityFactory.create(UnitTurnoverStats.class);
            newStats.building().set(unit.building());
            newStats.turnovers().setValue(stats.turnovers().getValue() + 1);
            stats = newStats;
        }
        stats.updatedOn().setValue(when);
        Persistence.secureSave(stats);
    }
}
