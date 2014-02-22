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

import java.util.Date;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.Pair;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.config.server.SystemDateManager;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.AndCriterion;
import com.pyx4j.entity.core.criterion.Criterion;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.server.Executable;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.server.TransactionScopeOption;
import com.pyx4j.entity.server.UnitOfWork;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.biz.system.yardi.YardiApplicationFacade;
import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.CancelMoveOutConstraintsDTO;
import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.MakeVacantConstraintsDTO;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitEffectiveAvailability;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.misc.VistaTODO;

public class OccupancyFacadeYardiImpl implements OccupancyFacade {

    private final static I18n i18n = I18n.get(OccupancyFacadeYardiImpl.class);

    @Override
    public AptUnitOccupancySegment getOccupancySegment(AptUnit unitStub, LogicalDate date) {
        EntityQueryCriteria<AptUnitEffectiveAvailability> criteria = EntityQueryCriteria.create(AptUnitEffectiveAvailability.class);
        criteria.eq(criteria.proto().unit(), unitStub);
        AptUnitEffectiveAvailability availability = Persistence.service().retrieve(criteria);
        AptUnitOccupancySegment s = EntityFactory.create(AptUnitOccupancySegment.class);
        if (!availability.availableForRent().isNull() && date.compareTo(availability.availableForRent().getValue()) > 0) {
            s.status().setValue(Status.available);
            s.dateFrom().setValue(availability.availableForRent().getValue());
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
        setAvailability(unitStub, null);
    }

    @Override
    public boolean isMigrateStartAvailable(AptUnit unitStub) {
        return true;
    }

    @Override
    public void migratedApprove(AptUnit unitStub) {
        setAvailability(unitStub, null);
    }

    @Override
    public boolean isMigratedApproveAvailable(AptUnit unitStub) {
        return true;
    }

    @Override
    public void migratedCancel(AptUnit unitStub) {
        setAvailability(unitStub, SystemDateManager.getLogicalDate());
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
        setAvailability(EntityFactory.createIdentityStub(AptUnit.class, unitId), null);
    }

    @Override
    public LogicalDate isRenovationAvailable(Key unitId) {
        return OccupancyFacade.MIN_DATE;
    }

    @Override
    public void scopeAvailable(Key unitId) {
        setAvailability(EntityFactory.createIdentityStub(AptUnit.class, unitId), SystemDateManager.getLogicalDate());
    }

    @Override
    public boolean isScopeAvailableAvailable(Key unitId) {
        return true;
    }

    @Override
    public void makeVacant(Key unitId, LogicalDate vacantFrom) {
        setAvailability(EntityFactory.createIdentityStub(AptUnit.class, unitId), null);
    }

    @Override
    public MakeVacantConstraintsDTO getMakeVacantConstraints(Key unitId) {
        MakeVacantConstraintsDTO c = EntityFactory.create(MakeVacantConstraintsDTO.class);
        c.minVacantFrom().setValue(OccupancyFacade.MIN_DATE);
        c.maxVacantFrom().setValue(OccupancyFacade.MAX_DATE);
        return c;
    }

    @Override
    public void occupy(Lease leaseId) {
        Lease lease = leaseId.createIdentityStub();
        Persistence.ensureRetrieve(lease.unit(), AttachLevel.IdOnly);
        setAvailability(EntityFactory.createIdentityStub(AptUnit.class, lease.unit().getPrimaryKey()), null);
    }

    @Override
    public void unoccupy(Lease leaseId) {
        Lease lease = leaseId.createIdentityStub();
        Persistence.ensureRetrieve(lease.unit(), AttachLevel.IdOnly);
        setAvailability(EntityFactory.createIdentityStub(AptUnit.class, lease.unit().getPrimaryKey()), SystemDateManager.getLogicalDate());
    }

    @Override
    public boolean isOccupyAvaialble(Key unitId) {
        return true;
    }

    @Override
    public void moveOut(Key unitId, LogicalDate moveOutDate, Lease leaseId) throws OccupancyOperationException {
        setAvailability(EntityFactory.createIdentityStub(AptUnit.class, unitId), moveOutDate);
    }

    @Override
    public void cancelMoveOut(Key unitId) throws OccupancyOperationException {
        setAvailability(EntityFactory.createIdentityStub(AptUnit.class, unitId), null);
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

    @Override
    public void setAvailability(AptUnit unit, LogicalDate availableForRent) {
        EntityQueryCriteria<AptUnitEffectiveAvailability> criteria = EntityQueryCriteria.create(AptUnitEffectiveAvailability.class);
        criteria.eq(criteria.proto().unit(), unit);
        AptUnitEffectiveAvailability availability = Persistence.service().retrieve(criteria);
        availability.availableForRent().setValue(availableForRent);
        Persistence.service().merge(availability);
    }

    @Override
    public void reserve(final Lease lease, final int durationHours) {
        if (!lease.leaseId().isNull()) {
            throw new UserRuntimeException("Reservation can't be made for Lease");
        }
        if (lease.leaseApplication().yardiApplicationId().isNull()) {
            // Application not created, Create it first
            try {
                ServerSideFactory.create(YardiApplicationFacade.class).createApplication(lease);
            } catch (YardiServiceException e) {
                throw new UserRuntimeException(i18n.tr("Posting Application to Yardi failed") + "\n" + e.getMessage(), e);
            }
            lease.set(Persistence.service().retrieve(Lease.class, lease.getPrimaryKey()));
        }

        new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Void, RuntimeException>() {

            @Override
            public Void execute() throws RuntimeException {
                new ReservationManager().reserve(lease, durationHours);
                try {
                    ServerSideFactory.create(YardiApplicationFacade.class).holdUnit(lease);
                } catch (YardiServiceException e) {
                    throw new UserRuntimeException(i18n.tr("Reserve Unit failed") + "\n" + e.getMessage(), e);
                }
                return null;
            }

        });
    }

    @Override
    public boolean unreserveIfReservered(final Lease leaseId) {
        return new UnitOfWork(TransactionScopeOption.RequiresNew).execute(new Executable<Boolean, RuntimeException>() {

            @Override
            public Boolean execute() throws RuntimeException {
                if (new ReservationManager().unreserveIfReservered(leaseId)) {
                    try {
                        ServerSideFactory.create(YardiApplicationFacade.class).unreserveUnit(leaseId);
                    } catch (YardiServiceException e) {
                        throw new UserRuntimeException(i18n.tr("Unreserve Unit failed") + "\n" + e.getMessage(), e);
                    }
                    return true;
                } else {
                    return false;
                }
            }

        });
    }

    @Override
    public Pair<Date, Lease> isReserved(Key unitId) {
        return new ReservationManager().isReserved(unitId);
    }

    @Override
    public Criterion buildAvalableCriteria(AptUnit unitProto, Status status, Date from, Date fromDeadline) {
        AndCriterion criteria = new AndCriterion();

        AndCriterion existsReservation = new AndCriterion();
        existsReservation.ge(unitProto.unitReservation().$().dateTo(), SystemDateManager.getDate());
        existsReservation.le(unitProto.unitReservation().$().dateFrom(), SystemDateManager.getDate());
        criteria.notExists(unitProto.unitReservation(), existsReservation);

        if (VistaTODO.yardi_noUnitOccupancySegments) {
            criteria.le(unitProto.availability().availableForRent(), from);
            if (fromDeadline != null) {
                criteria.gt(unitProto.availability().availableForRent(), fromDeadline);
            }
        } else {
            throw new Error("TODO");
        }

        return criteria;
    }
}
