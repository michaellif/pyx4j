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
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.server.Persistence;
import com.pyx4j.site.rpc.customization.CustomizationOverwriteAttemptException;

public class CustomizationPersistenceHelperTest {

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
        customizationPersistenceHelper().save("foo", entity, true);
        customizationPersistenceHelper().save("bar", entity, true);

        // test
        {
            EntityQueryCriteria<CustomizationHolderTable> criteriaFoo = EntityQueryCriteria.create(CustomizationHolderTable.class);
            criteriaFoo.add(PropertyCriterion.eq(criteriaFoo.proto().className(), CustomizationTestEntity.class.getSimpleName()));
            criteriaFoo.add(PropertyCriterion.eq(criteriaFoo.proto().identifierKey(), "foo"));
            CustomizationHolderTable holder = Persistence.service().retrieve(criteriaFoo);

            Assert.assertNotNull(holder);
        }

        {
            EntityQueryCriteria<CustomizationHolderTable> criteriaBar = EntityQueryCriteria.create(CustomizationHolderTable.class);
            criteriaBar.add(PropertyCriterion.eq(criteriaBar.proto().className(), CustomizationTestEntity.class.getSimpleName()));
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
        customizationPersistenceHelper().save("testSaveOverwrite", entity, true);

        // try to save with the same name, an exception must be thrown in the process
        customizationPersistenceHelper().save("testSaveOverwrite", entity, false);

    }

    @Test(expected = Error.class)
    public void testSaveMustNotAllowToOverwriteReadonlyProperty() {
        // setup
        CustomizationTestEntity entity = EntityFactory.create(CustomizationTestEntity.class);
        entity.valueStr().setValue("strValue");
        customizationPersistenceHelper().save("testSaveMustNotAllowToOverwriteReadonlyProperty", entity, true);

        // save again (MUST Fail)
        entity.readOnlyProperty().setValue(7);
        customizationPersistenceHelper().save("testSaveMustNotAllowToOverwriteReadonlyProperty", entity, true);
    }

    @Test
    public void testSaveMustAllowToOverwriteNullInReadonlyProperty() {
        // setup
        CustomizationTestEntity entity = EntityFactory.create(CustomizationTestEntity.class);
        entity.valueStr().setValue("strValue");
        customizationPersistenceHelper().save("testSaveMustAllowToOverwriteNullInReadonlyProperty", entity, true);

        // save again (MUST not fail)
        entity.readOnlyAllowOverwritreNull().setValue(7);
        customizationPersistenceHelper().save("testSaveMustAllowToOverwriteNullInReadonlyProperty", entity, true);
    }

    @Test(expected = Error.class)
    public void testSaveMustNotAllowToOverwriteReadonlyPropertyAfterOverwrittenNull() {
        // setup
        CustomizationTestEntity entity = EntityFactory.create(CustomizationTestEntity.class);
        entity.valueStr().setValue("strValue");
        customizationPersistenceHelper().save("testSaveMustNotAllowToOverwriteReadonlyPropertyAfterOverwrittenNull", entity, true);

        // save again (MUST not fail)
        entity.readOnlyAllowOverwritreNull().setValue(7);
        customizationPersistenceHelper().save("testSaveMustNotAllowToOverwriteReadonlyPropertyAfterOverwrittenNull", entity, true);

        // save again (MUST fail)
        entity.readOnlyAllowOverwritreNull().setValue(11);
        customizationPersistenceHelper().save("testSaveMustNotAllowToOverwriteReadonlyPropertyAfterOverwrittenNull", entity, true);
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
        customizationPersistenceHelper().save("foo", entity, true);

        entity.valueStr().setValue("bar value");
        customizationPersistenceHelper().save("bar", entity, true);

        {
            // load
            CustomizationTestEntity testEntity = customizationPersistenceHelper().load("bar", EntityFactory.getEntityPrototype(CustomizationTestEntity.class));

            // test
            Assert.assertEquals("bar value", testEntity.valueStr().getValue());
        }

        {
            // load another one
            CustomizationTestEntity testEntity = customizationPersistenceHelper().load("foo", EntityFactory.getEntityPrototype(CustomizationTestEntity.class));

            // test
            Assert.assertEquals("foo value", testEntity.valueStr().getValue());

        }

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

        customizationPersistenceHelper().save("foo", entity, true);

        CustomizationTestEntity loadedEntity;
        loadedEntity = customizationPersistenceHelper().load("foo", (CustomizationTestEntity) EntityFactory.getEntityPrototype(entity.getInstanceValueClass()));
        Assert.assertNotNull(loadedEntity);

        // perform 'delete', and check that the customization was actually deleted
        customizationPersistenceHelper().delete("foo", (CustomizationTestEntity) EntityFactory.getEntityPrototype(entity.getInstanceValueClass()));

        loadedEntity = customizationPersistenceHelper().load("foo", (CustomizationTestEntity) EntityFactory.getEntityPrototype(entity.getInstanceValueClass()));
        Assert.assertNull(loadedEntity);

    }

    @Test
    public void testDeleteMatching() {
        // setup
        CustomizationTestEntity entity0 = EntityFactory.create(CustomizationTestEntity.class);
        entity0.valueStr().setValue("foo");
        customizationPersistenceHelper().save("foo", entity0, true);

        CustomizationTestEntity entity1 = EntityFactory.create(CustomizationTestEntity.class);
        entity1.valueStr().setValue("boABCo");
        customizationPersistenceHelper().save("boABCo", entity1, true);

        CustomizationTestEntity entity2 = EntityFactory.create(CustomizationTestEntity.class);
        entity2.valueStr().setValue("moABCD");
        customizationPersistenceHelper().save("moABCD", entity2, true);

        // perform 'deleteMatching'
        customizationPersistenceHelper().deleteMatching("o%o", EntityFactory.getEntityPrototype(CustomizationTestEntity.class));

        // test
        Assert.assertNull(customizationPersistenceHelper().load("foo", EntityFactory.getEntityPrototype(CustomizationTestEntity.class)));
        Assert.assertNull(customizationPersistenceHelper().load("boo", EntityFactory.getEntityPrototype(CustomizationTestEntity.class)));
        Assert.assertNotNull(customizationPersistenceHelper().load("moABCD", EntityFactory.getEntityPrototype(CustomizationTestEntity.class)));
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

            customizationPersistenceHelper().save("" + i, entity, true);
        }

        // test List
        int num = 0;
        Iterator<String> listIterator = customizationPersistenceHelper().list(EntityFactory.getEntityPrototype(CustomizationTestEntity.class)).iterator();
        while (listIterator.hasNext()) {
            listIterator.next();
            ++num;
        }
        Assert.assertEquals(10, num);
    }

    private CustomizationPersistenceHelper<CustomizationTestEntity> customizationPersistenceHelper() {
        return new CustomizationPersistenceHelper<CustomizationTestEntity>(CustomizationHolderTable.class);
    }

}
