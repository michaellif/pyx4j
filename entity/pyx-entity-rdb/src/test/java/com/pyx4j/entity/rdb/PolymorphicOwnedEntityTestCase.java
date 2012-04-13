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

import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.BidirectionalOneToManyPlmChildA;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.BidirectionalOneToManyPlmChildB;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.BidirectionalOneToManyPlmParent;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.UnidirectionalOneToOnePlmChild;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.UnidirectionalOneToOnePlmChildA;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.UnidirectionalOneToOnePlmChildB;
import com.pyx4j.entity.test.shared.domain.ownership.polymorphic.UnidirectionalOneToOnePlmParent;

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

    public void testOwnedPolymorphismInSingleTable() {
        String testId = uniqueString();

        // setup
        BidirectionalOneToManyPlmParent o1 = EntityFactory.create(BidirectionalOneToManyPlmParent.class);
        o1.testId().setValue(testId);
        o1.value().setValue("papa1");

        BidirectionalOneToManyPlmChildA a1ofPapa1 = EntityFactory.create(BidirectionalOneToManyPlmChildA.class);
        a1ofPapa1.valueA().setValue("a1");
        a1ofPapa1.value().setValue("a");

        BidirectionalOneToManyPlmChildA a2ofPapa1 = EntityFactory.create(BidirectionalOneToManyPlmChildA.class);
        a2ofPapa1.valueA().setValue("a2");
        a2ofPapa1.value().setValue("x");

        BidirectionalOneToManyPlmChildB b1ofPapa1 = EntityFactory.create(BidirectionalOneToManyPlmChildB.class);
        b1ofPapa1.valueB().setValue("b1");
        b1ofPapa1.value().setValue("b");

        BidirectionalOneToManyPlmChildB b2ofPapa1 = EntityFactory.create(BidirectionalOneToManyPlmChildB.class);
        b2ofPapa1.valueB().setValue("b2");
        b2ofPapa1.value().setValue("x");

        o1.children().add(a1ofPapa1);
        o1.children().add(b1ofPapa1);
        o1.children().add(a2ofPapa1);
        o1.children().add(b2ofPapa1);

        srv.persist(o1);

        BidirectionalOneToManyPlmParent o2 = EntityFactory.create(BidirectionalOneToManyPlmParent.class);
        o2.testId().setValue(testId);
        o2.value().setValue("papa2");

        BidirectionalOneToManyPlmChildA a1OfPapa2 = EntityFactory.create(BidirectionalOneToManyPlmChildA.class);
        a1OfPapa2.valueA().setValue("a1");
        a1OfPapa2.value().setValue("b");

        o2.children().add(a1OfPapa2);

        srv.persist(o2);
        {
            // test retrieval of owned polymorphic children        
            BidirectionalOneToManyPlmParent papaFromDB = srv.retrieve(BidirectionalOneToManyPlmParent.class, o1.getPrimaryKey());
            assertNotNull("we must retrieve something", papaFromDB);

            srv.retrieveMember(papaFromDB.children());
            assertEquals("assert list size", 4, papaFromDB.children().size());

            assertEquals(BidirectionalOneToManyPlmChildA.class, papaFromDB.children().get(0).getInstanceValueClass());
            assertEquals(BidirectionalOneToManyPlmChildB.class, papaFromDB.children().get(1).getInstanceValueClass());
            assertEquals(BidirectionalOneToManyPlmChildA.class, papaFromDB.children().get(2).getInstanceValueClass());
            assertEquals(BidirectionalOneToManyPlmChildB.class, papaFromDB.children().get(3).getInstanceValueClass());
        }

        // TODO
        {
            // test search
            EntityQueryCriteria<BidirectionalOneToManyPlmParent> criteria = EntityQueryCriteria.create(BidirectionalOneToManyPlmParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().children().$().value(), "x"));
            List<BidirectionalOneToManyPlmParent> papas = srv.query(criteria);

            assertNotNull(papas);
            assertEquals(1, papas.size());
            assertEquals("papa1", papas.get(0).value().getValue());
        }

        {
            EntityQueryCriteria<BidirectionalOneToManyPlmParent> criteria = EntityQueryCriteria.create(BidirectionalOneToManyPlmParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().children().$().value(), "b"));
            criteria.desc(criteria.proto().value());
            List<BidirectionalOneToManyPlmParent> papas = srv.query(criteria);

            assertNotNull(papas);
            assertEquals(2, papas.size());
            assertEquals("papa2", papas.get(0).value().getValue());
            assertEquals("papa1", papas.get(1).value().getValue());
        }

    }
}
