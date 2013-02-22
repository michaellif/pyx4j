/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-22
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.operations.server.proc;

import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.operations.domain.scheduler.ExecutionReport;
import com.propertyvista.operations.domain.scheduler.StatisticsRecord;

class StatisticsConversionUtils {

    static StatisticsRecord createStatisticsRecord(ExecutionReport executionReport) {
        //TODO  use POJO
        StatisticsRecord runStats = EntityFactory.create(StatisticsRecord.class);

        runStats.total().setValue(executionReport.total().getValue());
        runStats.processed().setValue(executionReport.processed().getValue());
        runStats.failed().setValue(executionReport.failed().getValue());
        runStats.erred().setValue(executionReport.erred().getValue());

        return runStats;
    }

    static void updateExecutionReport(StatisticsRecord runStats, ExecutionReport executionReport) {
        updateExecutionReportMajorStats(runStats, executionReport);
        //TODO Add messages to executionReport
    }

    static void updateExecutionReportMajorStats(StatisticsRecord runStats, ExecutionReport executionReport) {
        executionReport.total().setValue(runStats.total().getValue());
        executionReport.processed().setValue(runStats.processed().getValue());
        executionReport.failed().setValue(runStats.failed().getValue());
        executionReport.erred().setValue(runStats.erred().getValue());
    }

}
