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
 * Created on Jan 24, 2012
 * @author DavidG
 * @version $Id$
 */
package com.pyx4j.entity.report.test;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.report.master.MasterReportModel;
import com.pyx4j.entity.report.master.ReportEntry;
import com.pyx4j.entity.report.master.ReportEntryFactory;

public class MasterReportTest extends ReportsTestBase {

    private static final Logger log = LoggerFactory.getLogger(MasterReportTest.class);

    @Test
    public void testMasterReport() throws Exception {
        List<ReportEntry> subreports = new LinkedList<ReportEntry>();
        subreports.add(ReportEntryFactory.create(new DepartmentsReportTest().createReportModel()));
        subreports.add(ReportEntryFactory.create(new OrganizationsReportTest().createReportModel()));
        subreports.add(ReportEntryFactory.create(new StaticReportTest().createReportModel()));

        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(MasterReportModel.REPORT_TITLE, "Master Report");
        try {
            parameters.put(MasterReportModel.REPORT_LOGO,
                    new URL("http://jasperforge.org/uploads/publish/jasperreportswebsite/trunk/images/logo_new.png").openStream());
        } catch (Exception e) {
            log.error("Error", e);
        }

        createReport(new MasterReportModel(subreports, parameters));
    }
}
