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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.report.JasperReportHTMLAdapter;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.test.shared.domain.Simple1;
import com.pyx4j.gwt.server.IOUtils;

public class HTMLMarkupReportTest extends ReportsTestBase {

    private static final Logger log = LoggerFactory.getLogger(HTMLMarkupReportTest.class);

    private Simple1 createFragment(String title, String resourceName, boolean htmlFilter) throws IOException {
        Simple1 ent = EntityFactory.create(Simple1.class);
        ent.testId().setValue(title);
        String html = IOUtils.getTextResource(resourceName, this.getClass());
        if (htmlFilter) {
            html = JasperReportHTMLAdapter.makeJasperCompatibleHTML(html);

            FileUtils.writeStringToFile(debugFileName(resourceName, ".html"), html);

        }
        ent.name().setValue(html);
        return ent;
    }

    private List<IEntity> createFragments(String title, String resourceName) throws IOException {
        List<IEntity> data = new ArrayList<>();
        data.add(createFragment(title, resourceName, false));
        data.add(createFragment(title + " * (filtered)", resourceName, true));
        return data;
    }

    @Test
    public void testHtmlText() throws Exception {
        List<IEntity> data = new ArrayList<>();
//        data.addAll(createFragments("Bold Font", "htmlMarkup-bold.html"));
//        data.addAll(createFragments("No Font Family", "htmlMarkup-no-family.html"));

        data.addAll(createFragments("Font proportions", "htmlMarkup-proportions.html"));
//        data.addAll(createFragments("Indent More", "htmlMarkup-indent-more.html"));

        //data.addAll(createFragments("Lines test", "htmlMarkup-lines.html"));

        createReport(new JasperReportModel("reports.HTMLMarkup", data, null));

        if (ServerSideConfiguration.isStartedUnderEclipse()) {
            log.info("TextItems {}", textItems);
        }
    }

}
