/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-10
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.operations.business;

import com.pyx4j.essentials.server.report.ReportTableFormatter;

import com.propertyvista.biz.ExecutionMonitor;

public class VistaBusinessFacadeImpl implements VistaBusinessFacade {

    @Override
    public ReportTableFormatter startStatsReport() {
        return VistaBusinessStatsReport.startStatsReport();
    }

    @Override
    public void processStatsReportsPmc(ExecutionMonitor executionMonitor, ReportTableFormatter formatter) {
        VistaBusinessStatsReport.processStatsReportsPmc(executionMonitor, formatter);
    }

    @Override
    public void completeStatsReport(ReportTableFormatter formatter) {
        VistaBusinessStatsReport.completeStatsReport(formatter);
    }

    @Override
    public ReportTableFormatter startCaledonReport() {
        return CaledonReport.startStatsReport();
    }

    @Override
    public void processCaledonReportPmc(ExecutionMonitor executionMonitor, ReportTableFormatter formatter) {
        CaledonReport.processStatsReportsPmc(executionMonitor, formatter);
    }

    @Override
    public void completeCaledonReport(ReportTableFormatter formatter) {
        CaledonReport.completeStatsReport(formatter);
    }
}
