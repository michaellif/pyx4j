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
 * Created on Feb 8, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import java.util.List;

import junit.framework.Assert;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.criterion.EntityQueryCriteria;
import com.pyx4j.entity.core.criterion.PropertyCriterion;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.BidirectionalOneToManyPlmSTChildA;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.BidirectionalOneToManyPlmSTChildAC;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.BidirectionalOneToManyPlmSTChildB;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.BidirectionalOneToManyPlmSTParent;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.BidirectionalOneToOnePlmSTP2CChildA;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.BidirectionalOneToOnePlmSTP2CParent;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.HasManagedListSTPlmParentA;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.HasManagedListSTPlmParentB;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.HasManagedListSTPlmParentBase;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.ManagedChild;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.UnidirectionalOneToOnePlmChild;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.UnidirectionalOneToOnePlmChildA;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.UnidirectionalOneToOnePlmChildB;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.UnidirectionalOneToOnePlmParent;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.st2.BidirectionalOneToMany2PlmSTChildA;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.st2.BidirectionalOneToMany2PlmSTChildAC;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.st2.BidirectionalOneToMany2PlmSTChildB;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.st2.BidirectionalOneToMany2PlmSTParent;

public abstract class PolymorphicOwnedEntityTestCase extends AssociationMappingTestCase {

    public void testUnidirectionalOneToOnePersist() {
        testUnidirectionalOneToOneSave(TestCaseMethod.Persist);
    }

    public void testUnidirectionalOneToOneMerge() {
        testUnidirectionalOneToOneSave(TestCaseMethod.Merge);
    }

    public void testUnidirectionalOneToOneSave(TestCaseMethod testCaseMethod) {
        String testId = uniqueString();
        UnidirectionalOneToOnePlmParent o = EntityFactory.create(UnidirectionalOneToOnePlmParent.class);
        o.testId().setValue(testId);
        o.name().setValue(uniqueString());

        UnidirectionalOneToOnePlmChildA a = EntityFactory.create(UnidirectionalOneToOnePlmChildA.class);
        a.testId().setValue(testId);
        a.name().setValue(uniqueString());

        o.child().set(a);

        // Save child and owner
        srvSave(o, testCaseMethod);

        Assert.assertNotNull("Id Assigned", o.child().getPrimaryKey());

        // Get Parent and see that Child is retrieved, then verify values
        {
            UnidirectionalOneToOnePlmParent parent = srv.retrieve(UnidirectionalOneToOnePlmParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.child().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.child().name(), parent.child().name());
        }

        // Get Child and see that child is retrieved, then verify values
        {
            UnidirectionalOneToOnePlmChild child = srv.retrieve(UnidirectionalOneToOnePlmChildA.class, o.child().getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("correct data retrieved", o.child().name(), child.name());
        }

        // update child and owner
        o.name().setValue(uniqueString());
        o.child().name().setValue(uniqueString());
        srvSave(o, testCaseMethod);

        // Get Parent and see that Child is retrieved, then verify values
        {
            UnidirectionalOneToOnePlmParent parent = srv.retrieve(UnidirectionalOneToOnePlmParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.child().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.child().name(), parent.child().name());
        }

        // update only child
        o.child().name().setValue(uniqueString());
        srvSave(o, testCaseMethod);

        // Get Parent and see that Child is retrieved, then verify values
        {
            UnidirectionalOneToOnePlmParent parent = srv.retrieve(UnidirectionalOneToOnePlmParent.class, o.getPrimaryKey());
            Assert.assertNotNull("data retrieved ", parent);
            Assert.assertEquals("child data retrieved", AttachLevel.Attached, parent.child().getAttachLevel());
            Assert.assertEquals("correct data retrieved", o.child().name(), parent.child().name());
        }

        // Get Child and see that child is retrieved, then verify values
        {
            UnidirectionalOneToOnePlmChild child = srv.retrieve(UnidirectionalOneToOnePlmChildA.class, o.child().getPrimaryKey());
            Assert.assertNotNull("data retrieved ", child);
            Assert.assertEquals("correct data retrieved", o.child().name(), child.name());
        }

        // Query Parent By Child
        {
            EntityQueryCriteria<UnidirectionalOneToOnePlmParent> criteria = EntityQueryCriteria.create(UnidirectionalOneToOnePlmParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().child(), a));
            // This can't be done generally
            //criteria.add(PropertyCriterion.eq(criteria.proto().child().name(), o.child().name()));

            List<UnidirectionalOneToOnePlmParent> parents = srv.query(criteria);
            Assert.assertEquals("result set size", 1, parents.size());
            Assert.assertEquals("correct data retrieved", o.child().name(), parents.get(0).child().name());
        }

        // Query Parent By Child Class
        {
            EntityQueryCriteria<UnidirectionalOneToOnePlmParent> criteria = EntityQueryCriteria.create(UnidirectionalOneToOnePlmParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().child(), UnidirectionalOneToOnePlmChildA.class));
            // This can't be done generally
            //criteria.add(PropertyCriterion.eq(criteria.proto().child().name(), o.child().name()));

            List<UnidirectionalOneToOnePlmParent> parents = srv.query(criteria);
            Assert.assertEquals("result set size", 1, parents.size());
            Assert.assertEquals("correct data retrieved", o.child().name(), parents.get(0).child().name());
        }

        // Get Child, change and save independently
        {
            UnidirectionalOneToOnePlmChild child = srv.retrieve(UnidirectionalOneToOnePlmChildA.class, o.child().getPrimaryKey());
            child.name().setValue(child.name().getValue() + "#");
            srvSave(child, testCaseMethod);

            child = srv.retrieve(UnidirectionalOneToOnePlmChildA.class, o.child().getPrimaryKey());
            Assert.assertTrue("child update", child.name().getValue().endsWith("#"));

            UnidirectionalOneToOnePlmParent parent = srv.retrieve(UnidirectionalOneToOnePlmParent.class, o.getPrimaryKey());
            Assert.assertTrue("child update", parent.child().name().getValue().endsWith("#"));

        }

        // Change child to other type
        {
            UnidirectionalOneToOnePlmParent parent = srv.retrieve(UnidirectionalOneToOnePlmParent.class, o.getPrimaryKey());
            UnidirectionalOneToOnePlmChildB child = EntityFactory.create(UnidirectionalOneToOnePlmChildB.class);
            child.testId().setValue(testId);
            child.name().setValue(uniqueString());
            parent.child().set(child);

            srvSave(parent, testCaseMethod);

            parent = srv.retrieve(UnidirectionalOneToOnePlmParent.class, o.getPrimaryKey());
            Assert.assertEquals("correct data retrieved", parent.child().name(), child.name());

            // See that Child was removed
            {
                UnidirectionalOneToOnePlmChildA childVerify = srv.retrieve(UnidirectionalOneToOnePlmChildA.class, a.getPrimaryKey());
                //TODO The persist method is inconsistent for now
                switch (testCaseMethod) {
                case Merge:
                    Assert.assertNull("child NOT removed", childVerify);
                    break;
                case Persist:
                    Assert.assertNotNull("child removed", childVerify);
                }
            }

        }

    }

    // TODO This is bad name
    public void testOneToOneOwnedPolymorphismInSigleTable() {
        String testId = uniqueString();

        // setup
        UnidirectionalOneToOnePlmParent parentA = EntityFactory.create(UnidirectionalOneToOnePlmParent.class);
        parentA.testId().setValue(testId);
        parentA.name().setValue("parentA");

        UnidirectionalOneToOnePlmChildA childA = EntityFactory.create(UnidirectionalOneToOnePlmChildA.class);
        childA.testId().setValue(testId);
        childA.name().setValue("A");

        parentA.child().set(childA);
        srv.persist(childA);

        UnidirectionalOneToOnePlmParent parentB = EntityFactory.create(UnidirectionalOneToOnePlmParent.class);
        parentB.testId().setValue(testId);
        parentB.name().setValue("parentB");

        UnidirectionalOneToOnePlmChildB childB = EntityFactory.create(UnidirectionalOneToOnePlmChildB.class);
        childB.testId().setValue(testId);
        childB.name().setValue("B");

        parentB.child().set(childB);
        srv.persist(parentB);

    }

    public void testOneToOneOwnedPolymorphismInSigleTableReffrenceFromParent() {
        String testId = uniqueString();

        // setup
        BidirectionalOneToOnePlmSTP2CParent parentA = EntityFactory.create(BidirectionalOneToOnePlmSTP2CParent.class);
        parentA.testId().setValue(testId);
        parentA.name().setValue("parentA");
        parentA.child().set(EntityFactory.create(BidirectionalOneToOnePlmSTP2CChildA.class));

        srv.persist(parentA);
    }

    public void TODO_testQueryByValueInPolymorphicEntity() {
        String testId = uniqueString();
        String searchBy = uniqueString();

        // setup
        BidirectionalOneToOnePlmSTP2CParent parentA = EntityFactory.create(BidirectionalOneToOnePlmSTP2CParent.class);
        parentA.testId().setValue(testId);
        parentA.name().setValue("parentA");

        BidirectionalOneToOnePlmSTP2CChildA childA = EntityFactory.create(BidirectionalOneToOnePlmSTP2CChildA.class);
        childA.propA().setValue(searchBy);
        parentA.child().set(childA);

        srv.persist(parentA);

        // idea using path
        if (false) {
            EntityQueryCriteria<BidirectionalOneToOnePlmSTP2CParent> criteria = EntityQueryCriteria.create(BidirectionalOneToOnePlmSTP2CParent.class);
            criteria.eq(criteria.proto().testId(), testId);
            //criteria.eq(criteria.proto().child().$asInstanceOf(BidirectionalOneToOnePlmSTP2CChildA.class).propA(), searchBy);
            List<BidirectionalOneToOnePlmSTP2CParent> found = srv.query(criteria);
            Assert.assertEquals("retrieved size", 1, found.size());
        }

        {
            EntityQueryCriteria<BidirectionalOneToOnePlmSTP2CParent> criteria = EntityQueryCriteria.create(BidirectionalOneToOnePlmSTP2CParent.class);
            criteria.eq(criteria.proto().testId(), testId);

            {
                EntityQueryCriteria<BidirectionalOneToOnePlmSTP2CChildA> subCriteria = EntityQueryCriteria.create(BidirectionalOneToOnePlmSTP2CChildA.class);
                subCriteria.eq(subCriteria.proto().propA(), searchBy);
                criteria.in(criteria.proto().child(), subCriteria);
            }

            List<BidirectionalOneToOnePlmSTP2CParent> found = srv.query(criteria);
            Assert.assertEquals("retrieved size", 1, found.size());
        }
    }

    public void testOwnedPolymorphismInSingleTable() {
        String testId = uniqueString();

        // setup
        BidirectionalOneToManyPlmSTParent o1 = EntityFactory.create(BidirectionalOneToManyPlmSTParent.class);
        o1.testId().setValue(testId);
        o1.value().setValue("papa1");

        BidirectionalOneToManyPlmSTChildA c1A1 = EntityFactory.create(BidirectionalOneToManyPlmSTChildA.class);
        c1A1.testId().setValue(testId);
        c1A1.valueA().setValue("a1");
        c1A1.value().setValue("a");

        BidirectionalOneToManyPlmSTChildA c1A2 = EntityFactory.create(BidirectionalOneToManyPlmSTChildA.class);
        c1A2.testId().setValue(testId);
        c1A2.valueA().setValue("a2");
        c1A2.value().setValue("x");

        BidirectionalOneToManyPlmSTChildB c1B1 = EntityFactory.create(BidirectionalOneToManyPlmSTChildB.class);
        c1B1.testId().setValue(testId);
        c1B1.valueB().setValue("b1");
        c1B1.value().setValue("b");

        BidirectionalOneToManyPlmSTChildB c1B2 = EntityFactory.create(BidirectionalOneToManyPlmSTChildB.class);
        c1B2.testId().setValue(testId);
        c1B2.valueB().setValue("b2");
        c1B2.value().setValue("x");

        o1.children().add(c1A1);
        o1.children().add(c1B1);
        o1.children().add(c1A2);
        o1.children().add(c1B2);

        srv.persist(o1);

        BidirectionalOneToManyPlmSTParent o2 = EntityFactory.create(BidirectionalOneToManyPlmSTParent.class);
        o2.testId().setValue(testId);
        o2.value().setValue("papa2");

        BidirectionalOneToManyPlmSTChildA c2A1 = EntityFactory.create(BidirectionalOneToManyPlmSTChildA.class);
        c2A1.testId().setValue(testId);
        c2A1.valueA().setValue("a1");
        c2A1.value().setValue("b");

        o2.children().add(c2A1);

        srv.persist(o2);
        {
            // test retrieval of owned polymorphic children
            BidirectionalOneToManyPlmSTParent papaFromDB = srv.retrieve(BidirectionalOneToManyPlmSTParent.class, o1.getPrimaryKey());
            assertNotNull("we must retrieve something", papaFromDB);

            srv.retrieveMember(papaFromDB.children());
            assertEquals("assert list size", 4, papaFromDB.children().size());

            assertEquals(BidirectionalOneToManyPlmSTChildA.class, papaFromDB.children().get(0).getInstanceValueClass());
            assertEquals(BidirectionalOneToManyPlmSTChildB.class, papaFromDB.children().get(1).getInstanceValueClass());
            assertEquals(BidirectionalOneToManyPlmSTChildA.class, papaFromDB.children().get(2).getInstanceValueClass());
            assertEquals(BidirectionalOneToManyPlmSTChildB.class, papaFromDB.children().get(3).getInstanceValueClass());
        }

        {
            // test search
            EntityQueryCriteria<BidirectionalOneToManyPlmSTParent> criteria = EntityQueryCriteria.create(BidirectionalOneToManyPlmSTParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().children().$().value(), "x"));
            List<BidirectionalOneToManyPlmSTParent> papas = srv.query(criteria);

            assertNotNull(papas);
            assertEquals(1, papas.size());
            assertEquals("papa1", papas.get(0).value().getValue());
        }

        {
            EntityQueryCriteria<BidirectionalOneToManyPlmSTParent> criteria = EntityQueryCriteria.create(BidirectionalOneToManyPlmSTParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().children().$().value(), "b"));
            criteria.desc(criteria.proto().value());
            List<BidirectionalOneToManyPlmSTParent> papas = srv.query(criteria);

            assertNotNull(papas);
            assertEquals(2, papas.size());
            assertEquals("papa2", papas.get(0).value().getValue());
            assertEquals("papa1", papas.get(1).value().getValue());
        }

        {
            // test search by Class
            EntityQueryCriteria<BidirectionalOneToManyPlmSTParent> criteria = EntityQueryCriteria.create(BidirectionalOneToManyPlmSTParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().children(), BidirectionalOneToManyPlmSTChildA.class));
            List<BidirectionalOneToManyPlmSTParent> papas = srv.query(criteria);

            assertNotNull(papas);
            assertEquals(2, papas.size());
        }
        {
            // test search by Class
            EntityQueryCriteria<BidirectionalOneToManyPlmSTParent> criteria = EntityQueryCriteria.create(BidirectionalOneToManyPlmSTParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().children(), BidirectionalOneToManyPlmSTChildB.class));
            List<BidirectionalOneToManyPlmSTParent> papas = srv.query(criteria);

            assertNotNull(papas);
            assertEquals(1, papas.size());
            assertEquals("papa1", papas.get(0).value().getValue());
        }

        // setup for query
        BidirectionalOneToManyPlmSTParent o3 = EntityFactory.create(BidirectionalOneToManyPlmSTParent.class);
        o3.testId().setValue(testId);
        o3.value().setValue("papa3");
        srv.persist(o3);

        BidirectionalOneToManyPlmSTChildAC c3AC1 = EntityFactory.create(BidirectionalOneToManyPlmSTChildAC.class);
        c3AC1.testId().setValue(testId);
        c3AC1.valueA().setValue("ac1");
        c3AC1.value().setValue("ac");
        c3AC1.parent().set(o3);
        srv.persist(c3AC1);

        // test search IN criteria by Classes
        {
            EntityQueryCriteria<BidirectionalOneToManyPlmSTParent> criteria = EntityQueryCriteria.create(BidirectionalOneToManyPlmSTParent.class);
            criteria.eq(criteria.proto().testId(), testId);
            criteria.in(criteria.proto().children(), BidirectionalOneToManyPlmSTChildA.class, BidirectionalOneToManyPlmSTChildAC.class);
            List<BidirectionalOneToManyPlmSTParent> papas = srv.query(criteria);

            assertNotNull(papas);
            assertEquals(3, papas.size());
        }

        // test search IN criteria by Values
        {
            EntityQueryCriteria<BidirectionalOneToManyPlmSTParent> criteria = EntityQueryCriteria.create(BidirectionalOneToManyPlmSTParent.class);
            criteria.eq(criteria.proto().testId(), testId);
            criteria.in(criteria.proto().children(), c1A1, c1B1, c2A1, c3AC1);
            List<BidirectionalOneToManyPlmSTParent> papas = srv.query(criteria);

            assertNotNull(papas);
            assertEquals(3, papas.size());
        }

        // Query children by itself use multiple inheritance
        {
            EntityQueryCriteria<BidirectionalOneToManyPlmSTChildA> criteria = EntityQueryCriteria.create(BidirectionalOneToManyPlmSTChildA.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));

            List<BidirectionalOneToManyPlmSTChildA> children = srv.query(criteria);

            assertEquals(4, children.size());
        }

    }

    public void testOwnedDualListPolymorphismInSingleTable() {
        String testId = uniqueString();

        // setup
        BidirectionalOneToMany2PlmSTParent o1 = EntityFactory.create(BidirectionalOneToMany2PlmSTParent.class);
        o1.testId().setValue(testId);
        o1.value().setValue("papa1");

        BidirectionalOneToMany2PlmSTChildA c1A1 = EntityFactory.create(BidirectionalOneToMany2PlmSTChildA.class);
        c1A1.testId().setValue(testId);
        c1A1.valueA().setValue("a1");
        c1A1.value().setValue("a");

        BidirectionalOneToMany2PlmSTChildA c1A2 = EntityFactory.create(BidirectionalOneToMany2PlmSTChildA.class);
        c1A2.testId().setValue(testId);
        c1A2.valueA().setValue("a2");
        c1A2.value().setValue("x");

        BidirectionalOneToMany2PlmSTChildB c1B1 = EntityFactory.create(BidirectionalOneToMany2PlmSTChildB.class);
        c1B1.testId().setValue(testId);
        c1B1.valueB().setValue("b1");
        c1B1.value().setValue("b");

        BidirectionalOneToMany2PlmSTChildB c1B2 = EntityFactory.create(BidirectionalOneToMany2PlmSTChildB.class);
        c1B2.testId().setValue(testId);
        c1B2.valueB().setValue("b2");
        c1B2.value().setValue("x");

        o1.childrenA().add(c1A1);
        o1.childrenB().add(c1B1);
        o1.childrenA().add(c1A2);
        o1.childrenB().add(c1B2);

        srv.persist(o1);

        BidirectionalOneToMany2PlmSTParent o2 = EntityFactory.create(BidirectionalOneToMany2PlmSTParent.class);
        o2.testId().setValue(testId);
        o2.value().setValue("papa2");

        BidirectionalOneToMany2PlmSTChildA c2A1 = EntityFactory.create(BidirectionalOneToMany2PlmSTChildA.class);
        c2A1.testId().setValue(testId);
        c2A1.valueA().setValue("a1");
        c2A1.value().setValue("b");

        o2.childrenA().add(c2A1);

        srv.persist(o2);

        {
            // test retrieval of owned polymorphic children
            BidirectionalOneToMany2PlmSTParent papaFromDB = srv.retrieve(BidirectionalOneToMany2PlmSTParent.class, o1.getPrimaryKey());
            assertNotNull("we must retrieve something", papaFromDB);

            assertEquals("assert list size", 2, papaFromDB.childrenA().size());
            assertEquals("assert list size", 2, papaFromDB.childrenB().size());

            assertEquals(BidirectionalOneToMany2PlmSTChildA.class, papaFromDB.childrenA().get(0).getInstanceValueClass());
            assertEquals(BidirectionalOneToMany2PlmSTChildA.class, papaFromDB.childrenA().get(1).getInstanceValueClass());
            assertEquals(BidirectionalOneToMany2PlmSTChildB.class, papaFromDB.childrenB().get(0).getInstanceValueClass());
            assertEquals(BidirectionalOneToMany2PlmSTChildB.class, papaFromDB.childrenB().get(1).getInstanceValueClass());
        }

        {
            // test search
            EntityQueryCriteria<BidirectionalOneToMany2PlmSTParent> criteria = EntityQueryCriteria.create(BidirectionalOneToMany2PlmSTParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().childrenA().$().value(), "x"));
            List<BidirectionalOneToMany2PlmSTParent> papas = srv.query(criteria);

            assertNotNull(papas);
            assertEquals(1, papas.size());
            assertEquals("papa1", papas.get(0).value().getValue());
        }

        {
            EntityQueryCriteria<BidirectionalOneToMany2PlmSTParent> criteria = EntityQueryCriteria.create(BidirectionalOneToMany2PlmSTParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().childrenA().$().value(), "b"));
            criteria.desc(criteria.proto().value());
            List<BidirectionalOneToMany2PlmSTParent> papas = srv.query(criteria);

            assertNotNull(papas);
            assertEquals(1, papas.size());
            assertEquals("papa2", papas.get(0).value().getValue());
        }

        {
            // test search by Class
            EntityQueryCriteria<BidirectionalOneToMany2PlmSTParent> criteria = EntityQueryCriteria.create(BidirectionalOneToMany2PlmSTParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().childrenA(), BidirectionalOneToMany2PlmSTChildA.class));
            List<BidirectionalOneToMany2PlmSTParent> papas = srv.query(criteria);

            assertNotNull(papas);
            assertEquals(2, papas.size());
        }

        {
            // test search by Class
            EntityQueryCriteria<BidirectionalOneToMany2PlmSTParent> criteria = EntityQueryCriteria.create(BidirectionalOneToMany2PlmSTParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.notExists(criteria.proto().childrenB()));
            List<BidirectionalOneToMany2PlmSTParent> papas = srv.query(criteria);

            assertNotNull(papas);
            assertEquals(1, papas.size());
            assertEquals("papa2", papas.get(0).value().getValue());
        }

        {
            // test search not exists
            EntityQueryCriteria<BidirectionalOneToMany2PlmSTParent> criteria = EntityQueryCriteria.create(BidirectionalOneToMany2PlmSTParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.notExists(criteria.proto().childrenB()));
            List<BidirectionalOneToMany2PlmSTParent> papas = srv.query(criteria);

            assertNotNull(papas);
            assertEquals(1, papas.size());
            assertEquals("papa2", papas.get(0).value().getValue());
        }

        // setup for query
        BidirectionalOneToMany2PlmSTParent o3 = EntityFactory.create(BidirectionalOneToMany2PlmSTParent.class);
        o3.testId().setValue(testId);
        o3.value().setValue("papa3");
        srv.persist(o3);

        BidirectionalOneToMany2PlmSTChildAC c3AC1 = EntityFactory.create(BidirectionalOneToMany2PlmSTChildAC.class);
        c3AC1.testId().setValue(testId);
        c3AC1.valueA().setValue("ac1");
        c3AC1.value().setValue("ac");
        c3AC1.parent().set(o3);
        srv.persist(c3AC1);

        // test search IN criteria by Classes
        {
            EntityQueryCriteria<BidirectionalOneToMany2PlmSTParent> criteria = EntityQueryCriteria.create(BidirectionalOneToMany2PlmSTParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion
                    .in(criteria.proto().childrenA(), BidirectionalOneToMany2PlmSTChildA.class, BidirectionalOneToMany2PlmSTChildAC.class));
            List<BidirectionalOneToMany2PlmSTParent> papas = srv.query(criteria);

            assertNotNull(papas);
            assertEquals(3, papas.size());
        }

        // test search IN criteria by Values
        {
            EntityQueryCriteria<BidirectionalOneToMany2PlmSTParent> criteria = EntityQueryCriteria.create(BidirectionalOneToMany2PlmSTParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.in(criteria.proto().childrenA(), c1A1, c2A1, c3AC1));
            List<BidirectionalOneToMany2PlmSTParent> papas = srv.query(criteria);

            assertNotNull(papas);
            assertEquals(3, papas.size());
        }

        // Query children by itself use multiple inheritance
        {
            EntityQueryCriteria<BidirectionalOneToMany2PlmSTChildA> criteria = EntityQueryCriteria.create(BidirectionalOneToMany2PlmSTChildA.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));

            List<BidirectionalOneToMany2PlmSTChildA> children = srv.query(criteria);

            assertEquals(4, children.size());
        }

    }

    public void testPolymorphicSTManagedOneToMany() {
        String testId = uniqueString();

        // SET UP:
        // create parents A1 {"aa1", "aa2"}, A2{"x1", "x2"}, B {"ab1", "ab1"}
        {
            HasManagedListSTPlmParentA a1 = EntityFactory.create(HasManagedListSTPlmParentA.class);
            a1.testId().setValue(testId);
            a1.foo().setValue("A1");
            a1.name().setValue("T1");

            ManagedChild childAA1 = EntityFactory.create(ManagedChild.class);
            childAA1.testId().setValue(testId);
            childAA1.value().setValue("aa1");
            a1.children().add(childAA1);

            ManagedChild childAA2 = EntityFactory.create(ManagedChild.class);
            childAA2.testId().setValue(testId);
            childAA2.value().setValue("aa2");
            a1.children().add(childAA2);

            {
                ManagedChild childAB = EntityFactory.create(ManagedChild.class);
                childAB.testId().setValue(testId);
                childAB.value().setValue("bc3");
                a1.children().add(childAB);
            }

            srv.persist(a1);
        }

        {
            HasManagedListSTPlmParentA a2 = EntityFactory.create(HasManagedListSTPlmParentA.class);
            a2.testId().setValue(testId);
            a2.foo().setValue("A2");

            ManagedChild childX1 = EntityFactory.create(ManagedChild.class);
            childX1.testId().setValue(testId);
            childX1.value().setValue("x1");
            a2.children().add(childX1);

            ManagedChild childX2 = EntityFactory.create(ManagedChild.class);
            childX2.testId().setValue(testId);
            childX2.value().setValue("x2");
            a2.children().add(childX2);
            srv.persist(a2);
        }

        {
            HasManagedListSTPlmParentB b = EntityFactory.create(HasManagedListSTPlmParentB.class);
            b.name().setValue("T3");
            b.testId().setValue(testId);
            b.bar().setValue(1);

            ManagedChild childAB1 = EntityFactory.create(ManagedChild.class);
            childAB1.testId().setValue(testId);
            childAB1.value().setValue("ab1");
            b.children().add(childAB1);

            ManagedChild childAB2 = EntityFactory.create(ManagedChild.class);
            childAB2.testId().setValue(testId);
            childAB2.value().setValue("ab2");
            b.children().add(childAB2);

            {
                ManagedChild childAB = EntityFactory.create(ManagedChild.class);
                childAB.testId().setValue(testId);
                childAB.value().setValue("cb3");
                b.children().add(childAB);
            }

            srv.persist(b);
        }

        // TEST SEARCH and SORT
        {
            EntityQueryCriteria<HasManagedListSTPlmParentBase> criteria = EntityQueryCriteria.create(HasManagedListSTPlmParentBase.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.like(criteria.proto().children().$().value(), "a%"));
            criteria.asc(criteria.proto().children().$().value());

            try {
                List<HasManagedListSTPlmParentBase> result = srv.query(criteria);

                Assert.fail("Sort by collections is unsupported");

                // for future
                {
                    assertEquals(2, result.size());
                    assertEquals(HasManagedListSTPlmParentA.class, result.get(0).getInstanceValueClass());
                    assertEquals("A1", result.get(0).<HasManagedListSTPlmParentA> cast().foo().getValue());
                    assertEquals(HasManagedListSTPlmParentB.class, result.get(1).getInstanceValueClass());
                }

            } catch (Error expected) {
                // Ok
            }
        }
        {
            // now sort in reverse order
            EntityQueryCriteria<HasManagedListSTPlmParentBase> criteria = EntityQueryCriteria.create(HasManagedListSTPlmParentBase.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.like(criteria.proto().children().$().value(), "a%"));
            criteria.desc(criteria.proto().children().$().value());

            try {
                List<HasManagedListSTPlmParentBase> result = srv.query(criteria);

                Assert.fail("Sort by collections is unsupported");

                // for future
                {
                    assertEquals(2, result.size());
                    assertEquals(HasManagedListSTPlmParentB.class, result.get(0).getInstanceValueClass());
                    assertEquals(HasManagedListSTPlmParentA.class, result.get(1).getInstanceValueClass());
                    assertEquals("A1", result.get(1).<HasManagedListSTPlmParentA> cast().foo().getValue());
                }
            } catch (Error expected) {
                // Ok
            }
        }
        {
            EntityQueryCriteria<HasManagedListSTPlmParentBase> criteria = EntityQueryCriteria.create(HasManagedListSTPlmParentBase.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.like(criteria.proto().children().$().value(), "x%"));

            List<HasManagedListSTPlmParentBase> result = srv.query(criteria);
            assertEquals(1, result.size());
            assertEquals(HasManagedListSTPlmParentA.class, result.get(0).getInstanceValueClass());
        }

        // TEST SEARCH and SORT by 3rd child
        {
            EntityQueryCriteria<HasManagedListSTPlmParentBase> criteria = EntityQueryCriteria.create(HasManagedListSTPlmParentBase.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.like(criteria.proto().children().$().value(), "*3"));
            // This limits the criteria to first child!
            criteria.asc(criteria.proto().children().$().value());

            try {
                List<HasManagedListSTPlmParentBase> result = srv.query(criteria);
                Assert.fail("Sort by collections is unsupported");

                // for future
                {

                    assertEquals(0, result.size());
                }
            } catch (Error expected) {
                // Ok
            }
        }
    }
}
