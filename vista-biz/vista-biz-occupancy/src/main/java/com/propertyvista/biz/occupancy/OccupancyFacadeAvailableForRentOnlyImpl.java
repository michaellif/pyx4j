/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-01-29
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.occupancy;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.CancelMoveOutConstraintsDTO;
import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.MakeVacantConstraintsDTO;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;

public class OccupancyFacadeAvailableForRentOnlyImpl implements OccupancyFacade {

    @Override
    public AptUnitOccupancySegment getOccupancySegment(AptUnit unitStub, LogicalDate date) {
        AptUnit unit = retrieveUnit(unitStub);
        AptUnitOccupancySegment s = EntityFactory.create(AptUnitOccupancySegment.class);
        if (!unit._availableForRent().isNull() && date.compareTo(unit._availableForRent().getValue()) > 0) {
            s.status().setValue(Status.available);
            s.dateFrom().setValue(unit._availableForRent().getValue());
            s.dateTo().setValue(OccupancyFacade.MAX_DATE);
        } else {
            s.status().setValue(Status.pending);
            s.dateFrom().setValue(OccupancyFacade.MIN_DATE);
            s.dateTo().setValue(OccupancyFacade.MAX_DATE);
        }
        return s;
    }

    @Override
    public void setupNewUnit(AptUnit unit) {

    }

    @Override
    public void migrateStart(AptUnit unitStub, Lease leaseStub) {
        AptUnit unit = retrieveUnit(unitStub);
        unit._availableForRent().setValue(null);
        Persistence.service().merge(unit);
    }

    @Override
    public boolean isMigrateStartAvailable(AptUnit unitStub) {
        return isAvailableForRent(Persistence.service().retrieve(AptUnit.class, unitStub.getPrimaryKey()));
    }

    @Override
    public void migratedApprove(AptUnit unitStub) {
        AptUnit unit = retrieveUnit(unitStub);
        unit._availableForRent().setValue(null);
        Persistence.service().merge(unit);
    }

    @Override
    public boolean isMigratedApproveAvailable(AptUnit unitStub) {
        return true;
    }

    @Override
    public void migratedCancel(AptUnit unitStub) {
        AptUnit unit = retrieveUnit(unitStub);
        unit._availableForRent().setValue(now());
        Persistence.service().merge(unit);
    }

    @Override
    public boolean isMigratedCancelAvailable(AptUnit unitStub) {
        return true;
    }

    @Override
    public void scopeOffMarket(Key unitId, OffMarketType type) {

    }

    @Override
    public boolean isScopeOffMarketAvailable(Key unitId) {
        return true;
    }

    @Override
    public void scopeRenovation(Key unitId, LogicalDate renovationEndDate) {
        AptUnit unit = retrieveUnit(unitId);
        unit._availableForRent().setValue(null);
        Persistence.service().merge(unit);
    }

    @Override
    public LogicalDate isRenovationAvailable(Key unitId) {
        return OccupancyFacade.MIN_DATE;
    }

    @Override
    public void scopeAvailable(Key unitId) {
        AptUnit unit = retrieveUnit(unitId);
        unit._availableForRent().setValue(now());
        Persistence.service().merge(unit);
    }

    @Override
    public boolean isScopeAvailableAvailable(Key unitId) {
        return true;
    }

    @Override
    public void makeVacant(Key unitId, LogicalDate vacantFrom) {
        AptUnit unit = retrieveUnit(unitId);
        unit._availableForRent().setValue(null);
        Persistence.service().merge(unit);
    }

    @Override
    public MakeVacantConstraintsDTO getMakeVacantConstraints(Key unitId) {
        MakeVacantConstraintsDTO c = EntityFactory.create(MakeVacantConstraintsDTO.class);
        c.minVacantFrom().setValue(OccupancyFacade.MIN_DATE);
        c.maxVacantFrom().setValue(OccupancyFacade.MAX_DATE);
        return c;
    }

    @Override
    public void reserve(Key unitId, Lease lease) {
        AptUnit unit = retrieveUnit(unitId);
        unit._availableForRent().setValue(null);
        Persistence.service().merge(unit);
    }

    @Override
    public LogicalDate isReserveAvailable(Key unitId) {
        return OccupancyFacade.MIN_DATE;
    }

    @Override
    public void unreserve(Key unitId) {
        AptUnit unit = retrieveUnit(unitId);
        unit._availableForRent().setValue(now());
        Persistence.service().merge(unit);
    }

    @Override
    public boolean isUnreserveAvailable(Key unitId) {
        return true;
    }

    @Override
    public void approveLease(Key unitId) {
        AptUnit unit = retrieveUnit(unitId);
        unit._availableForRent().setValue(null);
        Persistence.service().merge(unit);
    }

    @Override
    public boolean isApproveLeaseAvaialble(Key unitId) {
        return true;
    }

    @Override
    public void moveOut(Key unitId, LogicalDate moveOutDate, Lease leaseId) throws OccupancyOperationException {
        AptUnit unit = retrieveUnit(unitId);
        unit._availableForRent().setValue(moveOutDate);
        Persistence.service().merge(unit);
    }

    @Override
    public void cancelMoveOut(Key unitId) throws OccupancyOperationException {
        AptUnit unit = retrieveUnit(unitId);
        unit._availableForRent().setValue(null);
        Persistence.service().merge(unit);
    }

    @Override
    public CancelMoveOutConstraintsDTO getCancelMoveOutConstraints(Key unitId) {
        CancelMoveOutConstraintsDTO c = EntityFactory.create(CancelMoveOutConstraintsDTO.class);
        c.canCancelMoveOut().setValue(true);
        return c;
    }

    @Override
    public boolean isAvailableForExistingLease(Key unitId) {
        return true;
    }

    private LogicalDate now() {
        return new LogicalDate(SystemDateManager.getDate());
    }

    private boolean isAvailableForRent(AptUnit unit) {
        return !unit._availableForRent().isNull() && unit._availableForRent().getValue().compareTo(now()) <= 0;
    }

    private AptUnit retrieveUnit(Key unitId) {
        return Persistence.service().retrieve(AptUnit.class, unitId);
    }

    private AptUnit retrieveUnit(AptUnit unitStub) {
        return retrieveUnit(unitStub.getPrimaryKey());
    }
}
