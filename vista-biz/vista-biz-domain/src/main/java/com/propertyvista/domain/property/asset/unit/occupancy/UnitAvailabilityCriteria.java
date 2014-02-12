/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 12, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.domain.property.asset.unit.occupancy;

import java.util.Date;

import com.pyx4j.entity.core.criterion.Criterion;

import com.propertyvista.domain.property.asset.unit.occupancy.AptUnitOccupancySegment.Status;

@SuppressWarnings("serial")
public class UnitAvailabilityCriteria implements Criterion {

    private AptUnitOccupancySegment.Status status;

    private Date from;

    protected UnitAvailabilityCriteria() {
    }

    public UnitAvailabilityCriteria(Status status, Date from) {
        super();
        this.status = status;
        this.from = from;
    }

    public AptUnitOccupancySegment.Status getStatus() {
        return status;
    }

    public void setStatus(AptUnitOccupancySegment.Status status) {
        this.status = status;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

}
