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
 * Created on 2012-09-05
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.site.server.services.customization;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.pyx4j.commons.LogicalDate;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.site.rpc.customization.CustomizationOverwriteAttemptException;

public class CustomizationPersistenceTest {

    static {
        TestWithDB.setUp();
    }

    @Before
    public void setUp() {
        Persistence.service().startTransaction();
        Persistence.service().delete(EntityQueryCriteria.create(CustomizationHolderTable.class));
    }

    @After
    public void tearDown() {
        Persistence.service().commit();
    }

    @Test
    public void testSave() {

        // setup
        CustomizationTestEntity entity = EntityFactory.create(CustomizationTestEntity.class);
        entity.valueStr().setValue("strValue");

        GregorianCalendar cal = new GregorianCalendar();
        cal.set(2000, 1, 1);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        entity.valueDate().setValue(new LogicalDate(cal.getTime()));

        // save
        serviceHelper().save("foo", entity, true);
        serviceHelper().save("bar", entity, true);

        // test
        {
            EntityQueryCriteria<CustomizationHolderTable> criteriaFoo = EntityQueryCriteria.create(CustomizationHolderTable.class);
            criteriaFoo.add(PropertyCriterion.eq(criteriaFoo.proto().className(), CustomizationTestEntity.class.getName()));
            criteriaFoo.add(PropertyCriterion.eq(criteriaFoo.proto().identifierKey(), "foo"));
            CustomizationHolderTable holder = Persistence.service().retrieve(criteriaFoo);

            Assert.assertNotNull(holder);
        }

        {
            EntityQueryCriteria<CustomizationHolderTable> criteriaBar = EntityQueryCriteria.create(CustomizationHolderTable.class);
            criteriaBar.add(PropertyCriterion.eq(criteriaBar.proto().className(), CustomizationTestEntity.class.getName()));
            criteriaBar.add(PropertyCriterion.eq(criteriaBar.proto().identifierKey(), "bar"));
            CustomizationHolderTable holder = Persistence.service().retrieve(criteriaBar);

            Assert.assertNotNull(holder);
        }

    }

    @Test(expected = CustomizationOverwriteAttemptException.class)
    public void testSaveOverwrite() {
        // setup
        CustomizationTestEntity entity = EntityFactory.create(CustomizationTestEntity.class);
        entity.valueStr().setValue("strValue");

        GregorianCalendar cal = new GregorianCalendar();
        cal.set(2000, 1, 1);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        entity.valueDate().setValue(new LogicalDate(cal.getTime()));

        // save
        serviceHelper().save("foo", entity, true);

        // try to save with the same name, an exception must be thrown in the process
        serviceHelper().save("foo", entity, false);

    }

    @Test
    public void testLoad() {
        // setup
        CustomizationTestEntity entity = EntityFactory.create(CustomizationTestEntity.class);

        GregorianCalendar cal = new GregorianCalendar();
        cal.set(2000, 1, 1);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        entity.valueDate().setValue(new LogicalDate(cal.getTime()));

        // save
        entity.valueStr().setValue("foo value");
        serviceHelper().save("foo", entity, true);

        entity.valueStr().setValue("bar value");
        serviceHelper().save("bar", entity, true);

        // load
        CustomizationTestEntity testEntity = serviceHelper().load("bar", EntityFactory.getEntityPrototype(CustomizationTestEntity.class));

        // test
        Assert.assertEquals("bar value", testEntity.valueStr().getValue());

    }

    @Test
    public void testDelete() {

        // setup
        CustomizationTestEntity entity = EntityFactory.create(CustomizationTestEntity.class);
        entity.valueStr().setValue("strValue");

        GregorianCalendar cal = new GregorianCalendar();
        cal.set(2000, 1, 1);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        entity.valueDate().setValue(new LogicalDate(cal.getTime()));

        serviceHelper().save("foo", entity, true);

        CustomizationTestEntity loadedEntity;
        loadedEntity = serviceHelper().load("foo", (CustomizationTestEntity) EntityFactory.getEntityPrototype(entity.getInstanceValueClass()));
        Assert.assertNotNull(loadedEntity);

        // perform 'delete', and check that the customization was actually deleted
        serviceHelper().delete("foo", (CustomizationTestEntity) EntityFactory.getEntityPrototype(entity.getInstanceValueClass()));

        loadedEntity = serviceHelper().load("foo", (CustomizationTestEntity) EntityFactory.getEntityPrototype(entity.getInstanceValueClass()));
        Assert.assertNull(loadedEntity);

    }

    @Test
    public void testList() {

        // prepare data
        for (int i = 0; i < 10; ++i) {
            CustomizationTestEntity entity = EntityFactory.create(CustomizationTestEntity.class);
            entity.valueStr().setValue("strValue" + i);

            GregorianCalendar cal = new GregorianCalendar();
            cal.set(2000, 1, 1);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            entity.valueDate().setValue(new LogicalDate(cal.getTime()));

            serviceHelper().save("" + i, entity, true);
        }

        // test List
        int num = 0;
        Iterator<String> listIterator = serviceHelper().list(EntityFactory.getEntityPrototype(CustomizationTestEntity.class)).iterator();
        while (listIterator.hasNext()) {
            listIterator.next();
            ++num;
        }
        Assert.assertEquals(10, num);
    }

    private CustomizationPersistenceHelper<CustomizationTestEntity> serviceHelper() {
        return new CustomizationPersistenceHelper<CustomizationTestEntity>(CustomizationHolderTable.class);
    }

}
