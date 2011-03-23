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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.junit.BeforeClass;
import org.junit.Test;

public class BeanCollectionReportTest extends ReportsTestBase {

    @BeforeClass
    public static void init() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("ReportTitle", "Dynamic Report");

        Collection<Bean> beanCollection = new ArrayList<Bean>();
        beanCollection.add(new Bean("Field A"));
        beanCollection.add(new Bean("Field B"));

        createReport("target/test-classes/reports/Dynamic.jrxml", parameters, new JRBeanCollectionDataSource(beanCollection));
    }

    @Test
    public void testStaticText() throws Exception {
        Assert.assertEquals("'Dynamic Text' not found, ", "Dynamic text", evaluate("/jasperPrint/page/text[1]/textContent"));
    }

    @Test
    public void testDynamicText() throws Exception {
        Assert.assertEquals("'Field A' not found, ", "Field A", evaluate("/jasperPrint/page/text[2]/textContent"));
        Assert.assertEquals("'Field B' not found, ", "Field B", evaluate("/jasperPrint/page/text[3]/textContent"));
    }

    public static class Bean {
        private final String field1;

        public Bean(String field1) {
            this.field1 = field1;
        }

        public String getField1() {
            return field1;
        }

    }
}
