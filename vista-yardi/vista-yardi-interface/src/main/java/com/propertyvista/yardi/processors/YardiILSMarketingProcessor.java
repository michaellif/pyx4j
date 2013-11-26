/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 4, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.yardi.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yardi.entity.ils.Availability;
import com.yardi.entity.ils.MadeReadyDate;
import com.yardi.entity.ils.VacateDate;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.biz.system.YardiServiceException;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.yardi.mapper.MappingUtils;

public class YardiILSMarketingProcessor {

    private final static Logger log = LoggerFactory.getLogger(YardiILSMarketingProcessor.class);

    private final ExecutionMonitor executionMonitor;

    public YardiILSMarketingProcessor() {
        this(null);
    }

    public YardiILSMarketingProcessor(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
    }

    public void updateAvailability(AptUnit unit, Availability avail) throws YardiServiceException {
        // no availability means "not available"
        LogicalDate dateAvail = null;
        if (avail != null) {
            // use MadeReadyDate if set, otherwise VacateDate
            dateAvail = toDate(avail.getMadeReadyDate());
            if (dateAvail == null) {
                dateAvail = toDate(avail.getVacateDate());
            }
        }
        unit._availableForRent().setValue(dateAvail);
    }

    private LogicalDate toDate(VacateDate vacDate) {
        return MappingUtils.toLogicalDate(vacDate.getYear() + "-" + vacDate.getMonth() + "-" + vacDate.getDay());
    }

    private LogicalDate toDate(MadeReadyDate rdyDate) {
        return MappingUtils.toLogicalDate(rdyDate.getYear() + "-" + rdyDate.getMonth() + "-" + rdyDate.getDay());
    }

}
