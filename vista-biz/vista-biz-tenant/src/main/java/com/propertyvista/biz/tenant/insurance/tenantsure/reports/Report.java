/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-26
 * @author ArtyomB
 */
package com.propertyvista.biz.tenant.insurance.tenantsure.reports;

import java.util.Date;

import com.pyx4j.essentials.server.docs.sheet.ReportTableFormatter;

import com.propertyvista.biz.ExecutionMonitor;

public interface Report {

    void start(ReportTableFormatter formatter);

    /**
     * Processes report in the context for a single PMC namespace
     * 
     * @throws IllegalStateException
     *             if called before start() or after complete()
     */
    void processReport(ExecutionMonitor executionMonitor, Date date, ReportTableFormatter formatter) throws IllegalStateException;

    void complete(ReportFileCreator reportFileCreator, ReportTableFormatter formatter);

}
