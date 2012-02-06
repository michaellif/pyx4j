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

import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.OffMarketType;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;
import com.propertyvista.domain.tenant.lease.Lease;

public interface AptUnitOccupancyManager {

    /**
     * Applied to {@link Status#vacant}
     * 
     * @param unit
     * @param type
     *            not {@link OffMarketType#construction}
     */
    void scopeOffMarket(OffMarketType type, LogicalDate startDate);

    /**
     * Applied to {@link Status#vacant}
     * 
     * @param renovationEndDate
     */
    void scopeRenovation(LogicalDate renovationEndDate);

    /**
     * Converts {@link Status#vacant} to {@link Status#available} from now to the future (the past part of the vacant segment stays vacant). At most one
     * vacant/availble segment can be present in the present to future timeline.
     */
    void scopeAvailable();

    /**
     * Applied to {@link Status#offMarket}
     */
    void makeVacant(LogicalDate vacantFrom);

    /**
     * Create lease in draft mode. To create lease it first must be created in draft mode via {@link #reserve()} and then {@link #approveLease()}.
     */
    void reserve(Lease lease);

    /**
     * Cancel lease draft.
     */
    void unreserve();

    void approveLease();

    /**
     * Source: CRM/Lease form/Button or Portal (becomes {@link Status#vacant)).
     */
    void endLease();
}
