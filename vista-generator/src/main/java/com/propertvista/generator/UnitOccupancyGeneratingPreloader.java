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

import java.util.List;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerHelper;
import com.propertyvista.server.common.util.occupancy.AptUnitOccupancyManagerImpl;

/**
 * This one currently knows to generate leased segment that are consistent with.
 * 
 */
public class UnitOccupancyGeneratingPreloader {

    public void preloadOccupancy() {
        scopeAvailableForAll();

        EntityQueryCriteria<Lease> criteria = EntityQueryCriteria.create(Lease.class);
        List<Lease> leases = Persistence.service().query(criteria);

        final LogicalDate[] now = new LogicalDate[1];
        AptUnitOccupancyManagerImpl.NowSource nowSource = new AptUnitOccupancyManagerImpl.NowSource() {
            @Override
            public LogicalDate getNow() {
                return now[0];
            }
        };

        for (final Lease lease : leases) {
            if (!lease.unit().id().isNull() & !lease.status().isNull()) {

                switch (lease.status().getValue()) {
                case Active:
                    now[0] = lease.createDate().getValue();
                    new AptUnitOccupancyManagerImpl(lease.unit(), nowSource).reserve(lease);
                    now[0] = lease.approvalDate().getValue();
                    new AptUnitOccupancyManagerImpl(lease.unit(), nowSource).approveLease();
                    if (!lease.moveOutNotice().isNull()) {
                        now[0] = lease.moveOutNotice().getValue();
                        new AptUnitOccupancyManagerImpl(lease.unit(), nowSource).endLease();
                    }
                    break;

                case ApplicationCancelled:
                    // we don't need this lease
                    break;

                case ApplicationInProgress:
                    now[0] = lease.createDate().getValue();
                    new AptUnitOccupancyManagerImpl(lease.unit(), nowSource).reserve(lease);
                    break;

                case Approved:
                    now[0] = lease.createDate().getValue();
                    new AptUnitOccupancyManagerImpl(lease.unit(), nowSource).reserve(lease);
                    now[0] = lease.approvalDate().getValue();
                    new AptUnitOccupancyManagerImpl(lease.unit(), nowSource).approveLease();
                    break;

                case Closed:
                    // TODO ask what does it mean                    
                    break;

                case Completed:
                    now[0] = lease.createDate().getValue();
                    new AptUnitOccupancyManagerImpl(lease.unit(), nowSource).reserve(lease);
                    now[0] = lease.approvalDate().getValue();
                    new AptUnitOccupancyManagerImpl(lease.unit(), nowSource).approveLease();
                    // must have notice so
                    now[0] = lease.actualLeaseTo().getValue();
                    new AptUnitOccupancyManagerImpl(lease.unit(), nowSource).endLease();
                    break;

                case Declined:
                    // we don't need this lease
                    break;

                case FinalBillIssued:
                    // TODO ask what does it mean
                    break;
                case Created:
                    // draft
                    break;
                }
            }
        }
    }

    private void scopeAvailableForAll() {
        // assume that a unit is generated with "vacant" status
        List<AptUnit> units = Persistence.service().query(EntityQueryCriteria.create(AptUnit.class));

        for (AptUnit unit : units) {
//            AptUnitOccupancySegment availableSegment = EntityFactory.create(AptUnitOccupancySegment.class);
//            availableSegment.status().setValue(Status.available);
//            availableSegment.dateFrom().setValue(AptUnitOccupancyManagerHelper.MIN_DATE);
//            availableSegment.dateFrom().setValue(AptUnitOccupancyManagerHelper.MAX_DATE);
//            availableSegment.unit().set(unit);
//            Persistence.service().persist(availableSegment);
            new AptUnitOccupancyManagerImpl(unit, new AptUnitOccupancyManagerImpl.NowSource() {
                @Override
                public LogicalDate getNow() {
                    return AptUnitOccupancyManagerHelper.MIN_DATE;
                }
            }).scopeAvailable();
        }

    }
}
