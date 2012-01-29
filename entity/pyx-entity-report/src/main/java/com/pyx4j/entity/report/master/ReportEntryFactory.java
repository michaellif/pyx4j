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
 * Created on Jan 28, 2012
 * @author DavidG
 * @version $Id$
 */
package com.pyx4j.entity.report.master;

import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.shared.EntityFactory;

public class ReportEntryFactory {

    /**
     * Create a report entry containing one full-width report
     * 
     * @param fullReport
     *            the full subreport for the entry
     * @return a new report entry
     */
    public static ReportEntry create(JasperReportModel fullReport) {
        ReportEntry entity = EntityFactory.create(ReportEntry.class);
        entity.fullReport().setValue(fullReport);
        return entity;
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
    public static ReportEntry create(JasperReportModel leftReport, JasperReportModel rightReport) {
        ReportEntry entity = EntityFactory.create(ReportEntry.class);
        entity.leftReport().setValue(leftReport);
        entity.rightReport().setValue(rightReport);
        return entity;
    }

}
