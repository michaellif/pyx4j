/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 23, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.domain.occupancy;

import java.util.Vector;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;

public abstract class AbstractAptUnitOccupancyOperation implements IAptUnitOccupancyOperation {

    private final LogicalDate applyStartDate;

    private final Vector<Vector<AptUnitOccupancySegment>> appliedTo;

    public AbstractAptUnitOccupancyOperation(LogicalDate applyStartDate, Vector<Vector<AptUnitOccupancySegment>> appliedTo) {
        this.applyStartDate = applyStartDate != null ? new LogicalDate(applyStartDate) : null; // 'cause immutable
        this.appliedTo = appliedTo;
    }

    @Override
    public boolean isConfigured() {
        return true;
    }

    @Override
    public LogicalDate getApplyStartDate() {
        return new LogicalDate(applyStartDate);
    }

}
