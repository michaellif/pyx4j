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

import com.pyx4j.commons.Key;
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
import com.pyx4j.entity.test.shared.domain.Department;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Task;
import com.pyx4j.entity.test.shared.domain.inherit.AddressExt;
import com.pyx4j.entity.test.shared.domain.inherit.Base1Entity;
import com.pyx4j.entity.test.shared.domain.inherit.Base2Entity;
import com.pyx4j.entity.test.shared.domain.inherit.Concrete1Entity;
import com.pyx4j.entity.test.shared.domain.inherit.ConcreteEntity;

public class EntityMetaTest extends InitializerTestCase {

    public void testEmployeeMemberList() {
        Employee emp = EntityFactory.create(Employee.class);

        assertEquals("Entity Caption", "Laborer", EntityFactory.getEntityMeta(Employee.class).getCaption());

        assertEquals("Member Caption defined", "Home address", emp.homeAddress().getMeta().getCaption());
        assertEquals("Member Caption implicit", "Hire Date", emp.from().getMeta().getCaption());
        assertEquals("Member Caption implicit", "Work Address", emp.workAddress().getMeta().getCaption());

        MemberMeta setMemberMeta = emp.tasks().getMeta();
        assertEquals("ISet Meta valueClass", Task.class, setMemberMeta.getValueClass());
        assertEquals("ISet Meta valueClass", ObjectClassType.EntitySet, setMemberMeta.getObjectClassType());
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

        assertTrue("has field city", meta.getMemberNames().contains(addressExt.city().getFieldName()));

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

        Base1Entity memberEmpty = ent2.refference();
        assertFalse("Right class " + memberEmpty.getClass(), memberEmpty instanceof ConcreteEntity);
        assertEquals("No cast required", true, memberEmpty.isObjectClassSameAsDef());

        assertTrue("Has member name1", ent2.refference().getEntityMeta().getMemberNames().contains("name1"));
        assertFalse("Has member name2", ent2.refference().getEntityMeta().getMemberNames().contains("name2"));
        assertFalse("Has member name", ent2.refference().getEntityMeta().getMemberNames().contains("name"));

        ent2.refference().set(ent1);

        Base1Entity member = ent2.refference();
        assertTrue("Right class " + member.getClass(), member instanceof Base1Entity);
        assertFalse("Right class " + member.getClass(), member instanceof ConcreteEntity);
        assertEquals("Cast required", false, member.isObjectClassSameAsDef());

        ConcreteEntity ent1x;
        // This does not work.
        //ent1x = (ConcreteEntity) ent2.refference();
        ent1x = ent2.refference().cast();

        assertEquals("value of name1", "v-name1", ent1x.name1().getValue());
        assertEquals("value of name2", "v-name2", ent1x.name2().getValue());
        assertEquals("value of name", "v-name", ent1x.name().getValue());

        ent1x.name1().setValue("v-name1-mod");
        assertEquals("value of name1 change", "v-name1-mod", ent2.refference().name1().getValue());
    }

    public void testDownCast() {
        ConcreteEntity ent1 = EntityFactory.create(ConcreteEntity.class);
        ent1.setPrimaryKey(new Key(10));
        ent1.name().setValue("v-name");
        ent1.name2().setValue("v-name2");

        Base2Entity ent2 = ent1.clone(Base2Entity.class);
        assertFalse("Members not removed", ent2.containsMemberValue(ent1.name().getFieldName()));

        assertEquals("value of name2", "v-name2", ent2.name2().getValue());
        assertEquals("PK preserved", ent1.getPrimaryKey(), ent2.getPrimaryKey());

        try {
            ent1.clone(Concrete1Entity.class);
            fail("Allow invalid cast");
        } catch (ClassCastException ok) {

        }
    }

    public void testAbstractMemberEquals() {
        ConcreteEntity root = EntityFactory.create(ConcreteEntity.class);

        Concrete1Entity ent1 = EntityFactory.create(Concrete1Entity.class);
        ent1.setPrimaryKey(new Key(11));

        root.refference().set(ent1);

        assertTrue("Abstact member equals", ent1.equals(root.refference()));
    }

}
