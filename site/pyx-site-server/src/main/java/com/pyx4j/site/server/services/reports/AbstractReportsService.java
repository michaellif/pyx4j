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
 * Created on Aug 2, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.site.server.services.reports;

import java.io.Serializable;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.shared.reports.ReportMetadata;
import com.pyx4j.site.rpc.services.reports.IReportsService;

public class AbstractReportsService implements IReportsService {

    private final Map<Class<ReportMetadata>, ReportGenerator> reportGenerators;

    public AbstractReportsService(Map<Class<ReportMetadata>, ReportGenerator> reportGenerators) {
        this.reportGenerators = reportGenerators;
    }

    @Override
    public void generateReport(AsyncCallback<Serializable> callback, ReportMetadata reportMetadata) {

        ReportGenerator reportGenerator = reportGenerators.get(reportMetadata.getInstanceValueClass());
        if (reportGenerator != null) {
            callback.onSuccess(reportGenerator.generateReport(reportMetadata));
        } else {
            throw new Error("report generation failed: report generator for report type '" + reportMetadata.getInstanceValueClass().getName()
                    + "' was not found");
        }

    }

}
