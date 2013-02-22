/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-11-27
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance;

import java.util.Date;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.essentials.server.report.ReportTableFormatter;

import com.propertyvista.operations.domain.scheduler.StatisticsRecord;
import com.propertyvista.operations.domain.tenantsure.TenantSureHQUpdateFile;

public interface TenantSureProcessFacade {

    TenantSureHQUpdateFile reciveHQUpdatesFile();

    void processHQUpdate(StatisticsRecord runStats, TenantSureHQUpdateFile fileId);

    void processPayments(StatisticsRecord runStats, LogicalDate dueDate);

    void processCancellations(StatisticsRecord runStats, LogicalDate dueDate);

    ReportTableFormatter startReport();

    void processReportPmc(StatisticsRecord runStats, Date date, ReportTableFormatter formater);

    void completeReport(ReportTableFormatter formater, Date date);

    ReportTableFormatter startTransactionsReport();

    void processTransactionsReport(StatisticsRecord runtStats, Date date, ReportTableFormatter formatter);

    void completeTransactionsReport(ReportTableFormatter formatter, Date date);

}
