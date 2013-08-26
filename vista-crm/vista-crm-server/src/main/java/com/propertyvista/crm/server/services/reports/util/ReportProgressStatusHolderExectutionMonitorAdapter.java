/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-08-26
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.server.services.reports.util;

import com.pyx4j.essentials.server.services.reports.ReportProgressStatus;
import com.pyx4j.essentials.server.services.reports.ReportProgressStatusHolder;

import com.propertyvista.biz.ExecutionMonitor;

public class ReportProgressStatusHolderExectutionMonitorAdapter extends ReportProgressStatusHolder {

    private ExecutionMonitor executionMonitor;

    private boolean isTerminationRequested;

    public ReportProgressStatusHolderExectutionMonitorAdapter(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
    }

    public ReportProgressStatusHolderExectutionMonitorAdapter() {
        this(null);
    }

    public synchronized void setExecutionMonitor(ExecutionMonitor executionMonitor) {
        this.executionMonitor = executionMonitor;
    }

    public synchronized ExecutionMonitor getExecutionMonitor() {
        return this.executionMonitor;
    }

    @Override
    public synchronized ReportProgressStatus get() {
        if (executionMonitor == null) {
            return super.get();
        } else {
            return new ReportProgressStatus(super.get().stage, super.get().stageNum, super.get().stagesCount, executionMonitor.getProcessed(),
                    executionMonitor.getExpectedTotal());
        }
    }

    public synchronized void requestTermination() {
        if (executionMonitor == null) {
            this.isTerminationRequested = true;
        } else {
            executionMonitor.requestTermination();
        }
    }

    public synchronized boolean isTerminationRequested() {
        if (executionMonitor == null) {
            return isTerminationRequested;
        } else {
            return executionMonitor.isTerminationRequested();
        }
    }
}
