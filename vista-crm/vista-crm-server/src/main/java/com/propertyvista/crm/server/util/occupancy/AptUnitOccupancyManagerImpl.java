/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 6, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.util.occupancy;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.tenant.lease.Lease;

public class AptUnitOccupancyManagerImpl implements AptUnitOccupancyManager {

    public AptUnitOccupancyManagerImpl(AptUnit unit) {
        this(unit, new NowSource() {
            @Override
            public LogicalDate getNow() {
                return new LogicalDate();
            }
        });
    }

    public AptUnitOccupancyManagerImpl(AptUnit unit, NowSource source) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void scopeOffMarket(OffMarketType type, LogicalDate startDate) {
        // TODO Auto-generated method stub

    }

    @Override
    public void scopeRenovation(LogicalDate renovationEndDate) {
        // TODO Auto-generated method stub

    }

    @Override
    public void scopeAvailable() {
        // TODO Auto-generated method stub

    }

    @Override
    public void makeVacant(LogicalDate vacantFrom) {
        // TODO Auto-generated method stub

    }

    @Override
    public void reserve(Lease lease) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unreserve() {
        // TODO Auto-generated method stub

    }

    @Override
    public void approveLease() {
        // TODO Auto-generated method stub

    }

    @Override
    public void endLease() {
        // TODO Auto-generated method stub

    }

    public interface NowSource {

        LogicalDate getNow();

    }
}
