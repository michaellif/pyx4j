/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 8, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.server.common.util.occupancy.mk2;

import com.pyx4j.commons.Key;
import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.tenant.lease.Lease;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.ApproveLeaseConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.CancelEndLeaseConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.CancelReservationConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.EndLeaseConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.MakeVacantConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.ReserveConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.ScopeAvailableConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.ScopeOffMarketConstraintsDTO;
import com.propertyvista.server.common.util.occupancy.mk2.opconstraints.ScopeRenovationConstraintsDTO;

//TODO add `now` date for all the functions
public interface IUnitOccupancyManager {

    /**
     * Converts a part of a 'VACANT' segment starting from <code>avaialbleFrom</code> to 'AVAILABLE' segment.
     * 
     * @param unitId
     * @param availableFrom
     */
    void scopeAvailable(Key unitId, LogicalDate availableFrom);

    ScopeAvailableConstraintsDTO getScopeAvailableConstrants(Key unitId);

    /**
     * Converts a part of a 'VACANT' segment starting from <code>offMarketFrom</code> to 'OffMarket' segment.
     * 
     * @param offMarketFrom
     * @param type
     */
    void scopeOffMarket(Key unitId, LogicalDate offMarketFrom, OffMarketType type);

    ScopeOffMarketConstraintsDTO getScopeOffMarketConstrants(Key unitId);

    /**
     * Converts a part of a 'VACANT' segment to 'RENOVATED' from <code>renovationStart</code> until <code>renovationEnd</code>, and then 'AVAILABLE'.
     * 
     * @param unitId
     * @param renovationStart
     * @param renovationEnd
     */
    void scopeRenovation(Key unitId, LogicalDate renovationStart, LogicalDate renovationEnd);

    ScopeRenovationConstraintsDTO getScopeRenovationConstraints(Key unitId);

    /**
     * Cancels all 'OFF_MARKET', 'RENOVATION', 'AND AVAILABLE' stuff starting from <code>vacantFrom</code> (if there are no 'reserved'/'leased' segments in the
     * future)
     * 
     * @param unitId
     * @param vacantFrom
     */
    void makeVacant(Key unitId, LogicalDate vacantFrom);

    MakeVacantConstraintsDTO getMakeVacantConstraints(Key unitId);

    /**
     * Reserves 'AVAILABLE' segment for rent and makes units 'availableFrom' date equal to 0';
     * 
     * @param unitId
     * @param reserveFrom
     * @param lease
     */
    void reserve(Key unitId, LogicalDate reserveFrom, Lease lease);

    ReserveConstraintsDTO getReserveConstraints(Key unitId);

    /**
     * Cancels future 'RESERVED' segment, and converts it to 'AVAILABLE' starting from <code>cancelFrom</code> date.
     * 
     * @param unitId
     * @param cancelFrom
     */
    void cancelReservation(Key unitId, LogicalDate cancelFrom);

    CancelReservationConstraintsDTO getCancelReservationConstraints(Key unitID);

    /**
     * Approves a reserved lease.
     * 
     * @param unitId
     * @param leaseFrom
     */
    void approveLease(Key unitId, LogicalDate leaseFrom);

    ApproveLeaseConstraintsDTO getApproveLeaseConstraints(Key unitId);

    /**
     * 
     * @param unitId
     * @param leaseEnd
     */
    void endLease(Key unitId, LogicalDate leaseEnd);

    EndLeaseConstraintsDTO getEndLeaseConstraints(Key unitId);

    void cancelEndLease(Key unitId);

    CancelEndLeaseConstraintsDTO getCancelEndLeaseConstraints(Key unitId);
}
