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
 * Created on Mar 31, 2016
 * @author ernestog
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.pyx4j.entity.report.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.report.JasperReportModel;
import com.pyx4j.entity.report.adpater.JasperReportStyledAdapter;
import com.pyx4j.entity.report.adpater.NodesIterationStyledAdapterStrategy;
import com.pyx4j.gwt.server.IOUtils;

public class STYLEDMarkupReportTest extends ReportsTestBase {

    private static final Logger log = LoggerFactory.getLogger(STYLEDMarkupReportTest.class);

    @Transient
    public interface TestEntity extends IEntity {

        IPrimitive<String> source();

        IPrimitive<String> styled();

        IPrimitive<String> rendered();

    }

    protected TestEntity createFragment(String resourceName) throws IOException {
        TestEntity ent = EntityFactory.create(TestEntity.class);

        String htmlSource = IOUtils.getTextResource(resourceName, this.getClass());
        ent.source().setValue(htmlSource);

        JasperReportStyledAdapter styledAdapter = new JasperReportStyledAdapter(new NodesIterationStyledAdapterStrategy());
        String htmlStyled = styledAdapter.makeJasperCompatibleStyled(htmlSource);

        ent.styled().setValue(htmlStyled);

        ent.rendered().setValue(htmlStyled);
        return ent;
    }

    protected List<IEntity> createFragments(String title, String resourceName) throws IOException {
        List<IEntity> data = new ArrayList<>();
        data.add(createFragment(resourceName));
        return data;
    }

    @Test
    public void testStyledText() throws Exception {
        List<IEntity> data = new ArrayList<>();
        data.addAll(createFragments("Jasper Styled text", "jasper-styled-text.html"));
        createReport(new JasperReportModel("reports.STYLEDMarkup", data, null));

        if (ServerSideConfiguration.isStartedUnderEclipse()) {
            log.info("TextItems {}", textItems);
        }
    }

}
