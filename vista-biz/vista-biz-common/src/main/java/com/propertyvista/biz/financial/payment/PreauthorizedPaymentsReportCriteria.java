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

    private boolean hasExpectedMoveOutFilter;

    private LogicalDate minExpectedMoveOut;

    private LogicalDate maxExpectedMoveOut;

    private boolean leasesOnNoticeOnly;

    public PreauthorizedPaymentsReportCriteria(LogicalDate padGenerationDate, List<Building> selectedBuildings) {
        this.padGenerationDate = padGenerationDate;
        this.selectedBuildings = selectedBuildings;
        this.hasExpectedMoveOutFilter = false;
        this.minExpectedMoveOut = null;
        this.maxExpectedMoveOut = null;
    }

    public void setExpectedMoveOutCriteris(LogicalDate minExpectedMoveOut, LogicalDate maxExpectedMoveOut) {
        this.hasExpectedMoveOutFilter = true;
        this.minExpectedMoveOut = minExpectedMoveOut;
        this.maxExpectedMoveOut = maxExpectedMoveOut;
    }

    public boolean hasExpectedMoveOutFilter() {
        return hasExpectedMoveOutFilter;
    }

    public void setHasExpectedMoveOutFilter(boolean hasExpectedMoveOutFilter) {
        this.hasExpectedMoveOutFilter = hasExpectedMoveOutFilter;
    }

    public LogicalDate getMinExpectedMoveOut() {
        return minExpectedMoveOut;
    }

    public void setMinExpectedMoveOut(LogicalDate minExpectedMoveOut) {
        this.minExpectedMoveOut = minExpectedMoveOut;
    }

    public LogicalDate getMaxExpectedMoveOut() {
        return maxExpectedMoveOut;
    }

    public void setMaxExpectedMoveOut(LogicalDate maxExpectedMoveOut) {
        this.maxExpectedMoveOut = maxExpectedMoveOut;
    }

    public LogicalDate getPadGenerationDate() {
        return padGenerationDate;
    }

    public List<Building> getSelectedBuildings() {
        return selectedBuildings;
    }

    public boolean isLeasesOnNoticeOnly() {
        return leasesOnNoticeOnly;
    }

    public void setLeasesOnNoticeOnly(boolean leasesOnNoticeOnly) {
        this.leasesOnNoticeOnly = leasesOnNoticeOnly;
    }

}