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
 */
package com.pyx4j.entity.report.master;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;

import com.pyx4j.entity.report.JasperReportModel;

public class MasterReportModel extends JasperReportModel {

    public static final String REPORT_TITLE = "ReportTitle";

    public static final String REPORT_LOGO = "ReportLogo";

    private static final String REPORT_NAME = "reports.MasterReport";

    private final List<MasterReportEntry> subreports;

    public MasterReportModel(List<MasterReportEntry> subreports) {
        this(subreports, null);
    }

    public MasterReportModel(List<MasterReportEntry> subreports, Map<String, Object> parameters) {
        super(REPORT_NAME, null, parameters);
        this.subreports = subreports;
    }

    @Override
    public JRDataSource createDataSource() {
        return new MasterDataSource(subreports);
    }
}
