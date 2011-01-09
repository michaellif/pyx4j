/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jan 11, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import com.pyx4j.entity.annotations.Detached;
import com.pyx4j.entity.annotations.Indexed;
import com.pyx4j.entity.annotations.Owned;
import com.pyx4j.entity.annotations.Owner;
import com.pyx4j.entity.annotations.RpcTransient;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.entity.test.shared.domain.City;
import com.pyx4j.entity.test.shared.domain.Country;
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Status;
import com.pyx4j.entity.test.shared.domain.Task;
import com.pyx4j.entity.test.shared.domain.inherit.AddressExt;
import com.pyx4j.entity.test.shared.domain.inherit.Base1Entity;
import com.pyx4j.entity.test.shared.domain.inherit.ConcreteEntity;

public class EntityMetaTest extends InitializerTestCase {

    public void testEmployeeMemberList() {
        Employee emp = EntityFactory.create(Employee.class);

        assertEquals("Entity Caption", "Laborer", EntityFactory.getEntityMeta(Employee.class).getCaption());

        assertEquals("Memeber Caption defined", "Home address", emp.homeAddress().getMeta().getCaption());
        assertEquals("Memeber Caption implicit", "Hiredate", emp.hiredate().getMeta().getCaption());
        assertEquals("Memeber Caption implicit", "Work Address", emp.workAddress().getMeta().getCaption());

        MemberMeta setMemberMeta = emp.tasks().getMeta();
        assertEquals("ISet Meta valueClass", Task.class, setMemberMeta.getValueClass());
        assertEquals("ISet Meta valueClass", ObjectClassType.EntitySet, setMemberMeta.getObjectClassType());
    }

    public void testStringView() {
        City city = EntityFactory.create(City.class);
        String cityName = "Toronto";
        city.name().setValue(cityName);
        assertEquals("City StringView", cityName, city.getStringView());

        Task task = EntityFactory.create(Task.class);
        task.description().setValue("Something");
        task.status().setValue(Status.DEACTIVATED);
        assertEquals("Task StringView", "Something Deactivated", task.getStringView());

        // ---

        Address address = EntityFactory.create(Address.class);
        String streetName = "1 Bloor St.";
        address.streetName().setValue(streetName);

        Country country = EntityFactory.create(Country.class);
        String countryName = "Canada";
        country.name().setValue(countryName);
        address.country().set(country);
        address.city().set(city);

        assertEquals("Address StringView", "1 Bloor St. " + cityName + " " + countryName, address.getStringView());
    }

    @Transient
    @RpcTransient
    @Owned
    @Owner
    @Detached
    @Indexed
    public void testAnnotations() {
        assertTrue("@Transient", EntityFactory.create(Department.class).transientStuff().getMeta().isTransient());
        assertTrue("@RpcTransient", EntityFactory.create(Employee.class).accessStatus().getMeta().isRpcTransient());
        assertTrue("@Owned", EntityFactory.create(Employee.class).homeAddress().getMeta().isOwnedRelationships());
        assertTrue("@Owner", EntityFactory.create(Department.class).organization().getMeta().isOwner());
        assertFalse("@Owner is Detached", EntityFactory.create(Department.class).organization().getMeta().isDetached());
        assertFalse("@Detached", EntityFactory.create(Employee.class).homeAddress().getMeta().isDetached());
        assertTrue("@Indexed", EntityFactory.create(Employee.class).firstName().getMeta().isIndexed());
        assertFalse("not @Indexed", EntityFactory.create(Employee.class).reliable().getMeta().isIndexed());
    }

    public void testInherited() {
        AddressExt addressExt = EntityFactory.create(AddressExt.class);
        EntityMeta meta = EntityFactory.getEntityMeta(AddressExt.class);

        assertTrue("has filed city", meta.getMemberNames().contains(addressExt.city().getFieldName()));

        assertEquals("caption", "inherited city", meta.getMemberMeta(new Path(addressExt.city())).getCaption());
    }

    public void testInheritedLevel2() {
        Base1Entity base = null;

        //        try {
        //            base = EntityFactory.create(Base1Entity.class);
        //        } catch (Error e) {
        //            // OK
        //        }
        //        if (base != null) {
        //            fail("Should not create AbstractEntity");
        //        }

        base = EntityFactory.create(ConcreteEntity.class);
        assertTrue("Right class", base instanceof ConcreteEntity);
        assertTrue("Has member name1", base.getEntityMeta().getMemberNames().contains("name1"));
        assertTrue("Has member name2", base.getEntityMeta().getMemberNames().contains("name2"));
        assertTrue("Has member name", base.getEntityMeta().getMemberNames().contains("name"));
    }

    public void testAbstractMember() {

        ConcreteEntity ent1 = EntityFactory.create(ConcreteEntity.class);
        ent1.name1().setValue("v-name1");
        ent1.name2().setValue("v-name2");
        ent1.name().setValue("v-name");

        ConcreteEntity ent2 = EntityFactory.create(ConcreteEntity.class);

        assertTrue("Has member name1", ent2.refference().getEntityMeta().getMemberNames().contains("name1"));
        assertFalse("Has member name2", ent2.refference().getEntityMeta().getMemberNames().contains("name2"));
        assertFalse("Has member name", ent2.refference().getEntityMeta().getMemberNames().contains("name"));

        ent2.refference().set(ent1);

        Base1Entity member = ent2.refference();
        // TODO
        //assertTrue("Right class " + member.getClass(), member instanceof ConcreteEntity);

        ConcreteEntity ent1x;
        // This does not work.
        //ent1x = (ConcreteEntity) ent2.refference();
        ent1x = ent2.refference().cast(ConcreteEntity.class);

        assertEquals("value of name1", "v-name1", ent1x.name1().getValue());
        assertEquals("value of name2", "v-name2", ent1x.name2().getValue());
        assertEquals("value of name", "v-name", ent1x.name().getValue());

        ent1x.name1().setValue("v-name1-mod");
        assertEquals("value of name1 change", "v-name1-mod", ent2.refference().name1().getValue());
    }

}
