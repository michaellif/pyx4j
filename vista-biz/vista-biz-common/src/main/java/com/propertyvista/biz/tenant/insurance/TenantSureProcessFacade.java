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

import com.propertyvista.biz.ExecutionMonitor;
import com.propertyvista.operations.domain.tenantsure.TenantSureHQUpdateFile;

public interface TenantSureProcessFacade {

    TenantSureHQUpdateFile receiveHQUpdatesFile();

    void processHQUpdate(ExecutionMonitor executionMonitor, TenantSureHQUpdateFile fileId);

    void processPayments(ExecutionMonitor executionMonitor, LogicalDate dueDate);

    void processCancellations(ExecutionMonitor executionMonitor, LogicalDate dueDate);

    void processRenewal(ExecutionMonitor executionMonitor, LogicalDate dueDate);

    // --

    ReportTableFormatter startInsuranceStatusReport();

    void processInsuranceStatusReportPmc(ExecutionMonitor executionMonitor, Date date, ReportTableFormatter formater);

    void completeInsuranceStatusReport(ReportTableFormatter formater, Date date);

    // --

    ReportTableFormatter startTransactionsReport();

    void processTransactionsReport(ExecutionMonitor executionMonitor, Date date, ReportTableFormatter formatter);

    void completeTransactionsReport(ReportTableFormatter formatter, Date date);

    // --

    ReportTableFormatter startTenantSureBusinessReport();

    void processTenantSureBusinessReportPmc(ExecutionMonitor executionMonitor, ReportTableFormatter formater);

    void completeTenantSureBusinessReport(ReportTableFormatter formater);

}
