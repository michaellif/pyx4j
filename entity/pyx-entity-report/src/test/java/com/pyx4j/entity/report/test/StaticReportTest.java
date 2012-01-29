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
 * Created on Mar 23, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.report.test;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.report.JasperReportModel;

public class StaticReportTest extends ReportsTestBase {

    private static final Logger log = LoggerFactory.getLogger(StaticReportTest.class);

    public void init() throws Exception {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ReportTitle", "Static Report");

        createReport(new JasperReportModel("reports.Static", null, parameters));
    }

    @Test
    public void testStaticText() throws Exception {
        init();

        boolean ok = false;
        try {
            Assert.assertTrue("'Static Text' not found, ", containsText("Static text"));
            ok = true;
        } finally {
            if (!ok) {
                log.debug("available textItems {}", textItems);
            }
        }
    }

}
