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
 * Created on Aug 12, 2014
 * @author arminea
 */
package com.pyx4j.entity.report.dynamic.test;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.report.dynamic.DynamicReport;

public class DynamicReportFactoryTest {
    private static final Logger log = LoggerFactory.getLogger(DynamicReport.class);
    private DynamicReport report;

//test doesn't work on server : doesn't find file created himself
    @Test
    public void testCreateReport() throws IOException {
       /* report = new DynamicReport("/com/pyx4j/entity/report/dynamic/test/logo.png", "Dynamic Report Test");
        Path tmpDir = Files.createTempDirectory("dynamic-report-test");
        report.export(ExportTo.PDF, tmpDir.toString());
        Path pdf = Paths.get(tmpDir.toString(), "Dynamic Report Test.pdf");
        //assertTrue(Files.exists(pdf));
        Files.delete(pdf);
        Files.delete(tmpDir);
        */
    }
}
