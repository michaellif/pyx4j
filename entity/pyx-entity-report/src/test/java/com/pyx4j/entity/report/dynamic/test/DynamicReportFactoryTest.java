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
 * @version $Id$
 */
package com.pyx4j.entity.report.dynamic.test;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.report.dynamic.DynamicReport;
import com.pyx4j.entity.report.dynamic.ExportTo;

public class DynamicReportFactoryTest {
    private static final Logger log = LoggerFactory.getLogger(DynamicReport.class);
    private DynamicReport report;


    @Test
    public void testCreateReport(){
        report = new DynamicReport("C:\\temp\\reports\\logo.png", "Dynamic Report Test");
        report.export(ExportTo.PDF, "C:\\temp\\reports");
    }
}
