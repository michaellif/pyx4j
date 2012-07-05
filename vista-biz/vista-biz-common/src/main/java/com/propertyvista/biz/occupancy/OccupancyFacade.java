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

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.property.asset.unit.occupancy.opconstraints.MakeVacantConstraintsDTO;
import com.propertyvista.domain.tenant.lease.Lease;

public interface OccupancyFacade {

    public static final LogicalDate MIN_DATE = new LogicalDate(0, 0, 1); // 1900-1-1

    public static final LogicalDate MAX_DATE = new LogicalDate(1100, 0, 1); // 3000-1-1

    @Deprecated
    public static final boolean TODO = true;

    void setupNewUnit(AptUnit unit);

    void migrateStart(AptUnit unitStub, Lease leaseStub);

    boolean isMigrateStartAvailable(AptUnit unitStub);

    void migrateApprove(AptUnit unitStub);

    boolean isMigrateApproveAvailable(AptUnit unitStub);

    void migrateCancel(AptUnit unitStub);

    boolean isMigrateCancelAvailable(AptUnit unitStub);

    /**
     * Applied to {@link Status#vacant}: convert's it to offMarket
     * 
     * @param unit
     * @param type
     *            not {@link OffMarketType#construction}
     */
    void scopeOffMarket(Key unitId, OffMarketType type);

    boolean isScopeOffMarketAvailable(Key unitId);

    /**
     * Applied to {@link Status#vacant}
     * 
     * @param renovationEndDate
     */
    void scopeRenovation(Key unitId, LogicalDate renovationEndDate);

    /**
     * @return minimum renovation end date or null if not available
     */
    LogicalDate isRenovationAvailable(Key unitId);

    /**
     * Converts {@link Status#vacant} to {@link Status#available} from now to the future (the past part of the vacant segment stays vacant). At most one
     * vacant/availble segment can be present in the present to future timeline.
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
    void reserve(Key unitId, Lease lease);

    /**
     * @return the starting day of the 'available' segment, i.e. the minimum day that can be given to lease.leasFrom; or null, if it can't be done
     */
    LogicalDate isReserveAvailable(Key unitId);

    /**
     * Cancel lease draft.
     */
    void unreserve(Key unitId);

    boolean isUnreserveAvailable(Key unitId);

    void approveLease(Key unitId);

    boolean isApproveLeaseAvaialble(Key unitId);

    /**
     * Source: CRM/Lease form/Button or Portal (becomes {@link Status#vacant})).
     */
    void endLease(Key unitId);

    boolean isEndLeaseAvailable(Key unitId);

    void cancelEndLease(Key unitId);

    boolean isCancelEndLeaseAvaialble(Key unitId);

}
