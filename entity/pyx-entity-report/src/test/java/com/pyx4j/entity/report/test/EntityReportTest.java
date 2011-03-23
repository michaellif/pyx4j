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

import org.junit.BeforeClass;
import org.junit.Test;

import com.pyx4j.entity.report.JRIEntityCollectionDataSource;
import com.pyx4j.entity.report.test.domain.Entity;
import com.pyx4j.entity.shared.EntityFactory;

public class EntityReportTest extends ReportsTestBase {

    @BeforeClass
    public static void init() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("ReportTitle", "Entity Report");

        Collection<Entity> entityCollection = new ArrayList<Entity>();

        Entity entity = EntityFactory.create(Entity.class);
        entity.field1().setValue("Field AA");
        entity.field2().field3().setValue("Field AAAA");
        entityCollection.add(entity);

        entity = EntityFactory.create(Entity.class);
        entity.field1().setValue("Field BB");
        entity.field2().field3().setValue("Field BBBB");
        entityCollection.add(entity);

        createReport("target/test-classes/reports/Entity.jrxml", parameters, new JRIEntityCollectionDataSource<Entity>(entityCollection));
    }

    @Test
    public void testStaticText() throws Exception {
        Assert.assertEquals("'Entity Text' not found, ", "Entity text", evaluate("/jasperPrint/page/text[1]/textContent"));
    }

    @Test
    public void testDynamicText() throws Exception {
        Assert.assertEquals("'Field AA' not found, ", "Field AA", evaluate("/jasperPrint/page/text[2]/textContent"));
        Assert.assertEquals("'Field AAAA' not found, ", "Field AAAA", evaluate("/jasperPrint/page/text[3]/textContent"));
        Assert.assertEquals("'Field BB' not found, ", "Field BB", evaluate("/jasperPrint/page/text[4]/textContent"));
        Assert.assertEquals("'Field BBBB' not found, ", "Field BBBB", evaluate("/jasperPrint/page/text[5]/textContent"));
    }

}
