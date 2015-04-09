/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jul 27, 2014
 * @author vlads
 */
package com.pyx4j.essentials.server.services.reports;

import com.pyx4j.site.shared.domain.reports.ReportTemplate;

public abstract class ReportGeneratorFactory {

    public abstract Class<? extends ReportGenerator> getReportGeneratorClass(Class<? extends ReportTemplate> reportMetadataClass);

    public ReportGenerator getReportGenerator(Class<? extends ReportTemplate> reportMetadataClass) {
        Class<? extends ReportGenerator> reportGeneratorClass = getReportGeneratorClass(reportMetadataClass);
        if (reportGeneratorClass != null) {
            ReportGenerator reportGenerator;
            try {
                reportGenerator = reportGeneratorClass.newInstance();
            } catch (Throwable e) {
                throw new Error("report generation failed: failed to instantiate report generator class '" + reportGeneratorClass.getName() + "'", e);
            }
            return reportGenerator;
        } else {
            throw new Error("report generation failed: report generator for report type '" + reportMetadataClass.getName() + "' was not found");
        }
    }
}
