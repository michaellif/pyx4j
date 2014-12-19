/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-04
 * @author ArtyomB
 */
package com.propertyvista.biz.occupancy;

import java.util.List;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.dashboard.gadgets.availability.UnitTurnoversPerInterval;
import com.propertyvista.domain.dashboard.gadgets.common.TimeIntervalSize;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.tenant.lease.Lease;

public interface UnitTurnoverAnalysisManager {

    /** Update turnover statistics if applicable, a lease must be active */
    void updateUnitTurnover(Lease leaseId);

    /**
     * Returns turnover stats for a given range of dates divided to intervals according to <code>timeIntervalSize</code>, for the requested buildings.
     * <code>form</code> and <code>to</code> dates will be rounded to the interval beginning and interval end, for example if <code>timeIntervalSize</code> is
     * month, and <code>from</code> = "3 Nov 2012", <code>to</code> = "10 Jan 2012", then stats will at Nov 1 and end on Jan 31.
     */
    List<UnitTurnoversPerInterval> turnovers(TimeIntervalSize timeIntervalSize, LogicalDate from, LogicalDate to, List<Building> buildingIds);
}
