/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-06-19
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.financial.payment;

import java.util.List;

import com.pyx4j.commons.LogicalDate;

import com.propertyvista.domain.property.asset.building.Building;

public final class PreauthorizedPaymentsReportCriteria {

    public final LogicalDate padGenerationDate;

    public final List<Building> selectedBuildings;

    public final boolean hasExpectedMoveOutFilter;

    public final LogicalDate minExpectedMoveOut;

    public final LogicalDate maxExpectedMoveOut;

    public PreauthorizedPaymentsReportCriteria(LogicalDate padGenerationDate, List<Building> selectedBuildings) {
        this.padGenerationDate = padGenerationDate;
        this.selectedBuildings = selectedBuildings;
        this.hasExpectedMoveOutFilter = false;
        this.minExpectedMoveOut = null;
        this.maxExpectedMoveOut = null;
    }

    public PreauthorizedPaymentsReportCriteria(LogicalDate padGenerationDate, List<Building> selectedBuildings, LogicalDate minExpectedMoveOut,
            LogicalDate maxExpectedMoveOut) {
        this.padGenerationDate = padGenerationDate;
        this.selectedBuildings = selectedBuildings;
        this.hasExpectedMoveOutFilter = true;
        this.minExpectedMoveOut = minExpectedMoveOut;
        this.maxExpectedMoveOut = maxExpectedMoveOut;
    }

}