/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 23, 2012
 * @author DavidG
 * @version $Id$
 */
package com.pyx4j.entity.report.master;

import com.pyx4j.entity.report.JasperReportModel;

/**
 * An entry in the master report
 * Comprised of one full-width report to be displayed across the master report
 * or one or two half-width reports to be displayed side-by-side
 */
public class MasterReportEntry {

    private final JasperReportModel fullReport, leftReport, rightReport;

    /**
     * Create a report entry containing one full-width report
     * 
     * @param fullReport
     *            the full subreport for the entry
     * @return a new report entry
     */
    public MasterReportEntry(JasperReportModel fullReport) {
        this(fullReport, null, null);
    }

    /**
     * Create a report entry containing one or two half-width reports
     * 
     * @param leftReport
     *            the left subreport for the entry or null
     * @param rightReport
     *            the right subreport for the entry or null
     * @return a new report entry
     */
    public MasterReportEntry(JasperReportModel leftReport, JasperReportModel rightReport) {
        this(null, leftReport, rightReport);
    }

    private MasterReportEntry(JasperReportModel fullReport, JasperReportModel leftReport, JasperReportModel rightReport) {
        this.fullReport = fullReport;
        this.leftReport = leftReport;
        this.rightReport = rightReport;
    }

    public JasperReportModel getFullReport() {
        return fullReport;
    }

    public JasperReportModel getLeftReport() {
        return leftReport;
    }

    public JasperReportModel getRightReport() {
        return rightReport;
    }

}
