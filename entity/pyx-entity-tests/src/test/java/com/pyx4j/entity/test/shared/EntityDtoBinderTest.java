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
 * Created on Oct 23, 2011
 * @author vlads
 */
package com.pyx4j.entity.test.shared;

import java.sql.Date;

import org.junit.Assert;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.server.CrudEntityBinder;
import com.pyx4j.entity.shared.utils.EntityBinder;
import com.pyx4j.entity.shared.utils.EntityBinder.ValueConverter;
import com.pyx4j.entity.shared.utils.PolymorphicEntityBinder;
import com.pyx4j.entity.shared.utils.SimpleEntityBinder;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Employee.EmploymentStatus;
import com.pyx4j.entity.test.shared.domain.EmployeeTO;
import com.pyx4j.entity.test.shared.domain.EmployeeTO.EmploymentBusinessStatus;
import com.pyx4j.entity.test.shared.domain.Task;
import com.pyx4j.entity.test.shared.domain.inherit.Concrete2Entity;
import com.pyx4j.entity.test.shared.domain.inherit.ReferenceEntity;
import com.pyx4j.entity.test.shared.domain.inherit.ReferenceEntityDTO;
import com.pyx4j.entity.test.shared.domain.inherit.binder.B1sub1;
import com.pyx4j.entity.test.shared.domain.inherit.binder.B1sub1TO;
import com.pyx4j.entity.test.shared.domain.inherit.binder.B1sub2;
import com.pyx4j.entity.test.shared.domain.inherit.binder.B1sub2TO;
import com.pyx4j.entity.test.shared.domain.inherit.binder.B1super;
import com.pyx4j.entity.test.shared.domain.inherit.binder.B1superHolder;
import com.pyx4j.entity.test.shared.domain.inherit.binder.B1superTO;

public class EntityDtoBinderTest extends InitializerTestBase {

    private static class EmployeeSimpleEntityBinder extends SimpleEntityBinder<Employee, Employee> {

        protected EmployeeSimpleEntityBinder() {
            super(Employee.class, Employee.class);
        }

        @Override
        protected void bind() {
            bind(toProto.firstName(), boProto.firstName());
            bind(toProto.employmentStatus(), boProto.employmentStatus());
            bind(toProto.tasksSorted(), boProto.tasksSorted());
            bind(toProto.workAddress(), boProto.workAddress());
            bind(toProto.department(), boProto.department());
            bind(toProto.department().name(), boProto.department().organization().name());
        }

    }

    public void testGetBoundBOMemberPath() {
        EmployeeSimpleEntityBinder b = new EmployeeSimpleEntityBinder();
        Employee emp1 = EntityFactory.create(Employee.class);
        Assert.assertEquals("Bound", emp1.firstName().getPath(), b.getBoundBOMemberPath(emp1.firstName().getPath()));
        Assert.assertNull("Not Bound", b.getBoundBOMemberPath(emp1.from().getPath()));
        Assert.assertEquals("Bound by full member", emp1.workAddress().streetName().getPath(),
                b.getBoundBOMemberPath(emp1.workAddress().streetName().getPath()));

        Assert.assertEquals("Bound", emp1.department().organization().name().getPath(), b.getBoundBOMemberPath(emp1.department().name().getPath()));
        //TODO test the longest bound path.
    }

    public void testUpdate() {
        Employee emp1 = EntityFactory.create(Employee.class);
        emp1.firstName().setValue("a name");
        emp1.employmentStatus().setValue(Employee.EmploymentStatus.CONTRACT);

        emp1.workAddress().streetName().setValue("street");
        emp1.workAddress().city().name().setValue("city");
        emp1.workAddress().effectiveFrom().setValue(new Date(System.currentTimeMillis()));

        Task task1 = EntityFactory.create(Task.class);
        task1.description().setValue("Something1");
        task1.notes().add("note1");
        emp1.tasksSorted().add(task1);

        Employee emp2 = new EmployeeSimpleEntityBinder().createBO(emp1);
        Assert.assertEquals(emp1.firstName().getValue(), emp2.firstName().getValue());
        Assert.assertEquals(emp1.employmentStatus().getValue(), emp2.employmentStatus().getValue());

        Assert.assertEquals("address.streetName Value", emp1.workAddress().streetName().getValue(), emp2.workAddress().streetName().getValue());
        Assert.assertEquals("address.city Value", emp1.workAddress().city().name().getValue(), emp2.workAddress().city().name().getValue());
        Assert.assertEquals("address.effectiveFrom Value", emp1.workAddress().effectiveFrom().getValue(), emp2.workAddress().effectiveFrom().getValue());

        Assert.assertEquals("List size", 1, emp2.tasksSorted().size());

        Assert.assertEquals(task1.description(), emp2.tasksSorted().get(0).description());
        Assert.assertEquals(1, emp2.tasksSorted().get(0).notes().size());
        Assert.assertEquals("note1", emp2.tasksSorted().get(0).notes().iterator().next());

        emp2 = emp2.duplicate();

        Assert.assertFalse(new EmployeeSimpleEntityBinder().updateBO(emp1, emp2));

        emp1.workAddress().streetName().setValue("street m");

        Assert.assertTrue(new EmployeeSimpleEntityBinder().updateBO(emp1, emp2));
        //Second pass will not change the value
        Assert.assertFalse(new EmployeeSimpleEntityBinder().updateBO(emp1, emp2));
        Assert.assertEquals("address.streetName Value", emp1.workAddress().streetName().getValue(), emp2.workAddress().streetName().getValue());
    }

    public void testUpdateReference() {
        Employee emp1 = EntityFactory.create(Employee.class);

        emp1.workAddress().setPrimaryKey(new Key(11));
        emp1.workAddress().streetName().setValue("street");
        emp1.workAddress().country().name().setValue("country1");
        emp1.workAddress().country().setPrimaryKey(new Key(22));
        emp1.workAddress().city().name().setValue("city");

        Employee emp2 = new EmployeeSimpleEntityBinder().createBO(emp1);
        emp2 = emp2.duplicate();

        Assert.assertFalse(new EmployeeSimpleEntityBinder().updateBO(emp1, emp2));
        Assert.assertEquals("address.country Value", emp1.workAddress().country().name().getValue(), emp2.workAddress().country().name().getValue());
        Assert.assertEquals("address.country Value", emp1.workAddress().country().id().getValue(), emp2.workAddress().country().id().getValue());
        Assert.assertEquals("owned PK Value", emp1.workAddress().id().getValue(), emp2.workAddress().id().getValue());

        emp1.workAddress().country().name().setValue("country2");
        emp1.workAddress().country().setPrimaryKey(null);
        emp1.workAddress().streetName().setValue("street m");

        Assert.assertTrue(new EmployeeSimpleEntityBinder().updateBO(emp1, emp2));
        Assert.assertEquals("address.country Value", emp1.workAddress().country().name().getValue(), emp2.workAddress().country().name().getValue());
        Assert.assertTrue("address.country Value", emp2.workAddress().country().id().isNull());

        Assert.assertEquals("owned PK Value", emp1.workAddress().id().getValue(), emp2.workAddress().id().getValue());
        Assert.assertEquals("address.streetName Value", emp1.workAddress().streetName().getValue(), emp2.workAddress().streetName().getValue());
    }

    public void testValueConverter() {
        EmployeeTO toProto = EntityFactory.getEntityPrototype(EmployeeTO.class);

        EntityBinder<Employee, EmployeeTO> binder = new CrudEntityBinder<Employee, EmployeeTO>(Employee.class, EmployeeTO.class) {

            @Override
            protected void bind() {
                bindCompleteObject();
            }

        };

        binder.addValueConverter(toProto.employmentBusinessStatus(), new ValueConverter<Employee, EmploymentBusinessStatus>() {
            @Override
            public EmploymentBusinessStatus convertValue(Employee bo) {
                if (bo.employmentStatus().getValue() == EmploymentStatus.DISMISSED) {
                    return EmploymentBusinessStatus.Past;
                } else {
                    return EmploymentBusinessStatus.Current;
                }
            }
        });

        {
            Employee emp1 = EntityFactory.create(Employee.class);
            emp1.employmentStatus().setValue(EmploymentStatus.DISMISSED);

            EmployeeTO empTo = binder.createTO(emp1);
            Assert.assertEquals("Value Converted", EmploymentBusinessStatus.Past, empTo.employmentBusinessStatus().getValue());
        }

    }

    private static class PolymorphicEntityDtoBinder extends SimpleEntityBinder<ReferenceEntityDTO, ReferenceEntity> {

        protected PolymorphicEntityDtoBinder() {
            super(ReferenceEntityDTO.class, ReferenceEntity.class);
        }

        @Override
        protected void bind() {
            bind(toProto.name(), boProto.name());
            bind(toProto.reference(), boProto.reference());
        }

    }

    public void testPolymorphicEntityBinding() {
        ReferenceEntity ent1 = EntityFactory.create(ReferenceEntity.class);
        Concrete2Entity ent12 = EntityFactory.create(Concrete2Entity.class);
        ent12.setPrimaryKey(new Key(22));
        ent12.nameB1().setValue("b1");

        ent1.reference().set(ent12);

        ReferenceEntityDTO ent2 = new PolymorphicEntityDtoBinder().createBO(ent1);

        Assert.assertEquals(ent12.nameB1(), ent2.reference().nameB1());
        Assert.assertEquals("Proper instance", Concrete2Entity.class, ent2.reference().getInstanceValueClass());
    }

    public void testPolymorphicDetachedEntityBinding() {

        ReferenceEntity ent1 = EntityFactory.create(ReferenceEntity.class);
        Concrete2Entity ent12 = EntityFactory.create(Concrete2Entity.class);
        ent12.setPrimaryKey(new Key(22));
        ent12.setAttachLevel(AttachLevel.IdOnly);

        ent1.reference().set(ent12);

        ReferenceEntityDTO ent2 = new PolymorphicEntityDtoBinder().createBO(ent1);

        Assert.assertEquals("Proper instance", Concrete2Entity.class, ent2.reference().getInstanceValueClass());
    }

    private static class PluralPolymorphicBinder extends PolymorphicEntityBinder<B1super, B1superTO> {

        protected PluralPolymorphicBinder() {
            super(B1super.class, B1superTO.class);
        }

        @Override
        protected void bind() {
            //bind(toProto.nameB1to(), boProto.nameB1());
            bind(B1sub1TO.class, B1sub1.class, new SimpleEntityBinder<B1sub1, B1sub1TO>(B1sub1.class, B1sub1TO.class) {

                @Override
                protected void bind() {
                    bindCompleteObject();
                }
            });

            bind(B1sub2TO.class, B1sub2.class, new SimpleEntityBinder<B1sub2, B1sub2TO>(B1sub2.class, B1sub2TO.class) {

                @Override
                protected void bind() {
                    bindCompleteObject();
                }

                @Override
                public B1sub2TO createTO(B1sub2 bo) {
                    return super.createTO(bo);
                }

                @Override
                public void copyBOtoTO(B1sub2 bo, B1sub2TO to) {
                    super.copyBOtoTO(bo, to);
                    to.nameB1sub2to().setValue("_to_" + bo.nameB1sub2().getValue());
                }

            });
        }

    }

    public void testPluralPolymorphicBinding() {
        {
            B1sub1 ent1 = EntityFactory.create(B1sub1.class);
            ent1.nameB1sub1().setValue(String.valueOf(System.currentTimeMillis()));

            B1superTO ent1to = new PluralPolymorphicBinder().createTO(ent1);

            Assert.assertEquals("Proper instance", B1sub1TO.class, ent1to.getInstanceValueClass());

            Assert.assertEquals("value", ent1.nameB1sub1().getValue(), ent1to.<B1sub1TO> cast().nameB1sub1().getValue());
        }

        // Test InstanceValueClass, binding of Polymorphic member
        {
            B1superHolder holder = EntityFactory.create(B1superHolder.class);
            B1sub1 ent1 = EntityFactory.create(B1sub1.class);
            ent1.nameB1sub1().setValue(String.valueOf(System.currentTimeMillis()));
            holder.item().set(ent1);

            B1superTO ent1to = new PluralPolymorphicBinder().createTO(holder.item());

            Assert.assertEquals("Proper instance", B1sub1TO.class, ent1to.getInstanceValueClass());

            Assert.assertEquals("value", ent1.nameB1sub1().getValue(), ent1to.<B1sub1TO> cast().nameB1sub1().getValue());
        }

        // Test overloaded copy and create
        {
            B1sub2 ent1 = EntityFactory.create(B1sub2.class);
            ent1.nameB1sub2().setValue(String.valueOf(System.currentTimeMillis()));

            B1superTO ent1to = new PluralPolymorphicBinder().createTO(ent1);

            Assert.assertEquals("Proper instance", B1sub2TO.class, ent1to.getInstanceValueClass());

            Assert.assertEquals("value", ent1.nameB1sub2().getValue(), ent1to.<B1sub2TO> cast().nameB1sub2().getValue());
            Assert.assertEquals("value", "_to_" + ent1.nameB1sub2().getValue(), ent1to.<B1sub2TO> cast().nameB1sub2to().getValue());
        }

        B1superHolder holder = EntityFactory.create(B1superHolder.class);
        {
            B1sub2 ent1 = EntityFactory.create(B1sub2.class);
            ent1.nameB1sub2().setValue(String.valueOf(System.currentTimeMillis()));
            holder.item().set(ent1);

            B1superTO ent1to = new PluralPolymorphicBinder().createTO(holder.item());

            Assert.assertEquals("Proper instance", B1sub2TO.class, ent1to.getInstanceValueClass());

            Assert.assertEquals("value", ent1.nameB1sub2().getValue(), ent1to.<B1sub2TO> cast().nameB1sub2().getValue());
            Assert.assertEquals("value", "_to_" + ent1.nameB1sub2().getValue(), ent1to.<B1sub2TO> cast().nameB1sub2to().getValue());
        }
    }
}
