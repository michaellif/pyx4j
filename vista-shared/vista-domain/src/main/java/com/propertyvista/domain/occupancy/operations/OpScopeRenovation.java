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
package com.propertyvista.domain.occupancy.operations;

import java.util.List;
import java.util.Vector;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.occupancy.AbstractAptUnitOccupancyOperation;
import com.propertyvista.domain.occupancy.DefineSourceOccupancyStatePrefix;
import com.propertyvista.domain.occupancy.IAptUnitOccupancyOperationSelector;
import com.propertyvista.domain.occupancy.UnitStatus;
import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment;

/**
 * (V) -> (n A)
 */
@DefineSourceOccupancyStatePrefix(value = { UnitStatus.V })
public class OpScopeRenovation extends AbstractAptUnitOccupancyOperation {

    private LogicalDate renoEnd;

    public OpScopeRenovation() {
        // for GWT serialization 
        this(null, null);
    }

    public OpScopeRenovation(LogicalDate applyStartDate, Vector<Vector<AptUnitOccupancySegment>> appliedTo) {
        super(applyStartDate, appliedTo);
    }

    @Override
    public boolean isConfigured() {
        return renoEnd != null;
    }

    public LogicalDate minRenoEnd() {
        return new LogicalDate(Math.max(getApplyStartDate().getTime(), oldState().get(0).dateFrom().getValue().getTime()));
    }

    public void setRenoEnd(LogicalDate renoEnd) {
        this.renoEnd = new LogicalDate(renoEnd);
    }

    public LogicalDate getRenoEnd() {
        return new LogicalDate(renoEnd);
    }

    @Override
    public List<AptUnitOccupancySegment> newState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<AptUnitOccupancySegment> oldState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void populateSelector(IAptUnitOccupancyOperationSelector selector) {
        selector.populateOpScopeRenovation(this);
    }
}
