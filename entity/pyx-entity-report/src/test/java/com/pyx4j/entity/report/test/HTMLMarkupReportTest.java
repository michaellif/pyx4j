/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Mar 20, 2016
 * @author vlads
 */
package com.pyx4j.entity.report.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.test.shared.domain.Simple1;
import com.pyx4j.gwt.server.IOUtils;

public class HTMLMarkupReportTest extends ReportsTestBase {

    private static final Logger log = LoggerFactory.getLogger(HTMLMarkupReportTest.class);

    @Test
    public void testHtmlText() throws Exception {
        List<IEntity> data = new ArrayList<>();
        {
            Simple1 ent = EntityFactory.create(Simple1.class);
            ent.testId().setValue("Simple");
            ent.name().setValue("This is <b>bold</b>");
            data.add(ent);
        }
        {
            Simple1 ent = EntityFactory.create(Simple1.class);
            ent.testId().setValue("Lines test");
            ent.name().setValue(IOUtils.getTextResource("htmlMarkup-lines.html", this.getClass()));
            data.add(ent);
        }
        {
            Simple1 ent = EntityFactory.create(Simple1.class);
            ent.testId().setValue("Font proportions");
            ent.name().setValue(IOUtils.getTextResource("htmlMarkup-proportions.html", this.getClass()));
            data.add(ent);
        }

        createReport(new JasperReportModel("reports.HTMLMarkup", data, null));

        if (ServerSideConfiguration.isStartedUnderEclipse()) {
            log.info("TextItems {}", textItems);
        }

        boolean ok = false;
        try {
            Assert.assertTrue("data text not found, ", containsText("Simple"));
            ok = true;
        } finally {
            if (!ok) {
                log.debug("available textItems {}", textItems);
            }
        }
    }

}
