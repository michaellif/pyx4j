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
package com.propertyvista.biz.occupancy;

import java.util.Date;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;
import com.pyx4j.commons.Pair;
import com.pyx4j.entity.core.criterion.Criterion;

import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.CancelMoveOutConstraintsDTO;
import com.propertyvista.crm.rpc.dto.occupancy.opconstraints.MakeVacantConstraintsDTO;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;

// TODO add explicit exceptions
public interface OccupancyFacade {

    public static final LogicalDate MIN_DATE = new LogicalDate(0, 0, 1); // 1900-1-1

    public static final LogicalDate MAX_DATE = new LogicalDate(1100, 0, 1); // 3000-1-1

    /**
     * @return an occupancy segment of the unit that contains the provided date.
     */
    // TODO refactor this function it's not good: should return only minimal information: i.e sometimes occupancy model is not available
    AptUnitOccupancySegment getOccupancySegment(AptUnit unit, LogicalDate date);

    void setupNewUnit(AptUnit unit);

    void migrateStart(AptUnit unitStub, Lease leaseStub);

    boolean isMigrateStartAvailable(AptUnit unitStub);

    void migratedApprove(AptUnit unitStub);

    boolean isMigratedApproveAvailable(AptUnit unitStub);

    void migratedCancel(AptUnit unitStub);

    boolean isMigratedCancelAvailable(AptUnit unitStub);

    /**
     * Applied to {@link Status#pending}: convert's it to offMarket
     * 
     * @param unit
     * @param type
     *            not {@link OffMarketType#construction}
     */
    void scopeOffMarket(Key unitId, OffMarketType type);

    boolean isScopeOffMarketAvailable(Key unitId);

    /**
     * Applied to {@link Status#pending}
     * 
     * @param renovationEndDate
     */
    void scopeRenovation(Key unitId, LogicalDate renovationEndDate);

    /**
     * @return minimum renovation end date or null if not available
     */
    LogicalDate isRenovationAvailable(Key unitId);

    /**
     * Converts {@link Status#pending} to {@link Status#available} from now to the future (the past part of the vacant segment stays vacant). At most one
     * vacant/available segment can be present in the present to future timeline.
     */
    void scopeAvailable(Key unitId);

    boolean isScopeAvailableAvailable(Key unitId);

    /**
     * Applied starting from {@link Status#offMarket} or {@link Status#available}, starts vacant segment starting <code>vacantFrom</code> date, and removes all
     * future segments.
     */
    void makeVacant(Key unitId, LogicalDate vacantFrom);

    /**
     * make make vacant can be done if there are no "leased" or "reserved" segments in the future.
     * 
     * @return minimal vacantFrom range, for makeVacant operation, or null if it cannot be done
     */
    MakeVacantConstraintsDTO getMakeVacantConstraints(Key unitId);

    /**
     * Create lease in draft mode. To create lease it first must be created in draft mode via {@link #reserve()} and then {@link #approveLease()}.
     */
    @Deprecated
    void reserve(Key unitId, Lease lease);

    /**
     * Has UofW
     */
    void reserve(Lease lease, int durationHours);

    /**
     * 
     * @param unitId
     * @return empty pair in case of no reservation exist; Date - reservation to (until);
     */

    Pair<Date, Lease> isReserved(Key unitId);

    /**
     * @param lease
     *            with unit Id
     * @return true if unit was unreserved
     *         do not have UofW
     */
    boolean unreserveIfReservered(Lease lease);

    // Used only in Yardi for now
    void setAvailability(AptUnit unit, LogicalDate availableForRent);

    Criterion buildAvalableCriteria(AptUnit unitProto, AptUnitOccupancySegment.Status status, Date from, Date fromDeadline);

    /**
     * @return the starting day of the 'available' segment, i.e. the minimum day that can be given to lease.leasFrom; or null, if it can't be done
     */
    LogicalDate isReserveAvailable(Key unitId);

    /**
     * Cancel lease draft.
     */
    @Deprecated
    void unreserve(Key unitId);

    @Deprecated
    boolean isUnreserveAvailable(Key unitId);

    void occupy(Lease leaseId);

    boolean isOccupyAvaialble(Key unitId);

    /**
     * Defines the ending date of a lease, and sets the rest of unit occupancy as {@link Status#pending}
     * 
     * @param moveOutDate
     *            a last day of of the lease: a <code>leased</code> occupancy segment that is connected to current lease will end on this date (inclusive)
     */
    void moveOut(Key unitId, LogicalDate moveOutDate, Lease leaseId) throws OccupancyOperationException;

    /** Cancels the definition of a lease that is currently active and has a defined end date in the future */
    void cancelMoveOut(Key unitId) throws OccupancyOperationException;

    CancelMoveOutConstraintsDTO getCancelMoveOutConstraints(Key unitId);

    /**
     * Lease availability.
     */
    boolean isAvailableForExistingLease(Key unitId);
}
