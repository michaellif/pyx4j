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
 * Created on 2011-04-01
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.shared;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.test.shared.domain.Counter;
import com.pyx4j.entity.test.shared.domain.Country;
import com.pyx4j.entity.test.shared.domain.Employee;
import com.pyx4j.entity.test.shared.domain.Province;
import com.pyx4j.entity.test.shared.domain.Task;
import com.pyx4j.entity.test.shared.domain.bidir.Child;
import com.pyx4j.entity.test.shared.domain.bidir.Master;
import com.pyx4j.entity.test.shared.domain.graphwalking.AbstractOwnerB;
import com.pyx4j.entity.test.shared.domain.graphwalking.BaseEntity;
import com.pyx4j.entity.test.shared.domain.graphwalking.ClutterEntity;
import com.pyx4j.entity.test.shared.domain.graphwalking.OwnedEntity;
import com.pyx4j.entity.test.shared.domain.graphwalking.OwnerA;
import com.pyx4j.entity.test.shared.domain.graphwalking.OwnerB;
import com.pyx4j.entity.test.shared.domain.ownership.LeafReference;
import com.pyx4j.entity.test.shared.domain.ownership.OwnedLeaf;
import com.pyx4j.entity.test.shared.domain.ownership.RootEnity;

public class EntityGraphTest extends InitializerTestBase {

    public void testSimpleGraphIteration() {

        Country rootEntity = EntityFactory.create(Country.class);

        {
            Province prov = EntityFactory.create(Province.class);
            prov.setPrimaryKey(new Key(1));
            rootEntity.province().add(prov);
        }

        {
            Province prov = EntityFactory.create(Province.class);
            prov.setPrimaryKey(new Key(2));
            rootEntity.province().add(prov);
        }

        final Counter counter = EntityFactory.create(Counter.class);
        counter.number().setValue(0);

        EntityGraph.applyRecursivelyAllObjects(rootEntity, new EntityGraph.ApplyMethod() {
            @Override
            public boolean apply(IEntity entity) {
                counter.number().setValue(counter.number().getValue() + 1);
                return true;
            }
        });

        assertEquals("Iteration over All Entites", 3, counter.number().getValue().intValue());

        // --- add two unique objects
        {
            Province prov = EntityFactory.create(Province.class);
            rootEntity.province().add(prov);
        }
        {
            Province prov = EntityFactory.create(Province.class);
            rootEntity.province().add(prov);
        }

        assertEquals("Set size", 4, rootEntity.province().size());

        counter.number().setValue(0);
        EntityGraph.applyRecursivelyAllObjects(rootEntity, new EntityGraph.ApplyMethod() {
            @Override
            public boolean apply(IEntity entity) {
                counter.number().setValue(counter.number().getValue() + 1);
                return true;
            }
        });

        assertEquals("Iteration over All Entites by Identity", 5, counter.number().getValue().intValue());

        // --
        counter.number().setValue(0);
        EntityGraph.applyRecursively(rootEntity, new EntityGraph.ApplyMethod() {
            @Override
            public boolean apply(IEntity entity) {
                counter.number().setValue(counter.number().getValue() + 1);
                return true;
            }
        });

        assertEquals("Iteration over All Entites by Pk", 5, counter.number().getValue().intValue());
    }

    public void testDuplicationGraphIteration() {
        Employee emp = EntityFactory.create(Employee.class);
        Task task1 = EntityFactory.create(Task.class);
        task1.setPrimaryKey(new Key(1));
        emp.tasks().add(task1);

        Task task2 = EntityFactory.create(Task.class);
        task2.setPrimaryKey(new Key(1));
        emp.tasksSorted().add(task2);

        final Counter counter = EntityFactory.create(Counter.class);
        counter.number().setValue(0);

        EntityGraph.applyRecursively(emp, new EntityGraph.ApplyMethod() {
            @Override
            public boolean apply(IEntity entity) {
                if (entity instanceof Task) {
                    counter.number().setValue(counter.number().getValue() + 1);
                }
                return true;
            }
        });

        assertEquals("Iteration over Master and Child by Pk", 1, counter.number().getValue().intValue());

        // ---
        counter.number().setValue(0);
        EntityGraph.applyRecursivelyAllObjects(emp, new EntityGraph.ApplyMethod() {
            @Override
            public boolean apply(IEntity entity) {
                if (entity instanceof Task) {
                    counter.number().setValue(counter.number().getValue() + 1);
                }
                return true;
            }
        });

        assertEquals("Iteration over Master and Child by Identity", 2, counter.number().getValue().intValue());
    }

    public void testBidirectionalRelationshipGraphIteration() {
        Master m = EntityFactory.create(Master.class);
        Child c = EntityFactory.create(Child.class);
        m.child().set(c);

        final Counter counter = EntityFactory.create(Counter.class);
        counter.number().setValue(0);

        EntityGraph.applyRecursively(m, new EntityGraph.ApplyMethod() {
            @Override
            public boolean apply(IEntity entity) {
                counter.number().setValue(counter.number().getValue() + 1);
                return true;
            }
        });

        assertEquals("Iteration over Master and Child by Pk", 2, counter.number().getValue().intValue());

        counter.number().setValue(0);
        EntityGraph.applyRecursivelyAllObjects(m, new EntityGraph.ApplyMethod() {
            @Override
            public boolean apply(IEntity entity) {
                counter.number().setValue(counter.number().getValue() + 1);
                return true;
            }
        });

        assertEquals("Iteration over Master and Child by Identity", 2, counter.number().getValue().intValue());
    }

    public void testRemoveOwnedEntityFromGrapth() {
        RootEnity root = EntityFactory.create(RootEnity.class);

        OwnedLeaf ol = EntityFactory.create(OwnedLeaf.class);
        ol.setPrimaryKey(new Key(55));
        ol.name().setValue("To be removed");

        root.otherEntity().setPrimaryKey(ol.getPrimaryKey());

        final OwnedLeaf olBase = (OwnedLeaf) ol.duplicate();

        root.ownedLeaf().set(ol);
        assertTrue(ol.equals(root.ownedLeaf()));

        root.leafReference().leaf().set(ol);

        LeafReference leafReference1 = EntityFactory.create(LeafReference.class);
        leafReference1.name().setValue("leafReference1");
        leafReference1.leaf().set(ol);
        root.leafReferences().add(leafReference1);

        LeafReference leafReference2 = EntityFactory.create(LeafReference.class);
        leafReference2.name().setValue("leafReference2");
        leafReference2.leafs().add(ol);
        root.leafReferences().add(leafReference2);

        assertTrue(ol.equals(root.leafReference().leaf()));

        //System.out.println("root : " + root);
        //Remove from root
        root.ownedLeaf().set(null);
        //System.out.println("root : " + root);

        EntityGraph.applyRecursively(root, new EntityGraph.ApplyMethod() {
            @Override
            public boolean apply(IEntity entity) {
                //System.out.println("verify : " + entity.getPath() + " " + entity);
                assertFalse("OwnedLeaf found in Graph" + entity.getPath(), olBase.equals(entity));
                return true;
            }
        });

        assertEquals("Other not removed", ol.getPrimaryKey(), root.otherEntity().getPrimaryKey());
    }

    public void testRemoveOwnedEntityFromGrapthLists() {
        RootEnity root = EntityFactory.create(RootEnity.class);

        OwnedLeaf ol = EntityFactory.create(OwnedLeaf.class);
        ol.setPrimaryKey(new Key(55));
        ol.name().setValue("To be removed");

        final OwnedLeaf olBase = (OwnedLeaf) ol.duplicate();

        // Add to root
        root.ownedLeafs().add(ol);

        root.leafReference().leaf().set(ol);

        LeafReference leafReference1 = EntityFactory.create(LeafReference.class);
        leafReference1.name().setValue("leafReference1");
        leafReference1.leaf().set(ol);
        root.leafReferences().add(leafReference1);

        LeafReference leafReference2 = EntityFactory.create(LeafReference.class);
        leafReference2.name().setValue("leafReference2");
        leafReference2.leafs().add(ol);
        root.leafReferences().add(leafReference2);

        assertTrue(ol.equals(root.leafReference().leaf()));

        //System.out.println("root : " + root);
        // Remove ol from position where it is owned and see if it disappear from Reference
        root.ownedLeafs().remove(ol);
        //root.ownedLeafs().remove(0);

        //System.out.println("root : " + root);

        EntityGraph.applyRecursively(root, new EntityGraph.ApplyMethod() {
            @Override
            public boolean apply(IEntity entity) {
                //System.out.println("verify : " + entity.getPath() + " " + entity);
                assertFalse("OwnedLeaf found in Graph" + entity.getPath(), olBase.equals(entity));
                return true;
            }
        });

    }

    public void testBuisnessDuplicate() {
        Employee original = EntityFactory.create(Employee.class);
        original.id().setValue(new Key(9000l));
        original.firstName().setValue("George");
        original.flagDouble().setValue(1000.0);
        {
            Task task = EntityFactory.create(Task.class);
            task.id().setValue(new Key(1l));
            task.finished().setValue(false);
            task.description().setValue("Drink a cup of coffee");
            original.tasks().add(task);
        }

        Employee duplicate = EntityGraph.businessDuplicate(original);

        assertNull(EntityGraph.getChangedDataPath(original, duplicate));

        assertNotNull(original.id().getValue());
        assertNull(duplicate.id().getValue());
        assertEquals(duplicate.tasks().size(), original.tasks().size());

        for (Task task : original.tasks()) {
            assertNotNull(task.id().getValue());
        }
        for (Task task : duplicate.tasks()) {
            assertNull(task.id().getValue());
        }

    }

    public void testApplyToOwners() {
        ClutterEntity clutter = EntityFactory.create(ClutterEntity.class);
        clutter.clutterValue().setValue("clutter");

        // set up
        OwnedEntity ownedEntity1 = EntityFactory.create(OwnedEntity.class);
        ownedEntity1.valueStr().setValue("owned1");
        ownedEntity1.valueInt().setValue(7);

        OwnedEntity ownedEntity2 = EntityFactory.create(OwnedEntity.class);
        ownedEntity2.valueStr().setValue("owned2");
        ownedEntity2.valueInt().setValue(7);

        OwnerB ownerB1 = EntityFactory.create(OwnerB.class);
        ownerB1.valueStr().setValue("ownerB1");
        ownerB1.valueInt().setValue(1);
        ownerB1.clutterA().set(clutter);
        ownerB1.clutterB().set(clutter);
        ownerB1.ownedEntity().set(ownedEntity1);

        AbstractOwnerB ownerB2 = EntityFactory.create(OwnerB.class);
        ownerB2.valueStr().setValue("ownerB2");
        ownerB2.valueInt().setValue(-1);
        ownerB2.clutterA().set(clutter);
        ownerB2.clutterB().set(clutter);
        ((OwnerB) ownerB2).ownedEntity().set(ownedEntity2);
        // downcast to test that the function can deal with polymorphism 
        ownerB2 = ownerB2.duplicate(AbstractOwnerB.class);
        ownedEntity2 = ownerB2.ownedEntity();

        OwnerA ownerA = EntityFactory.create(OwnerA.class);
        ownerA.valueStr().setValue("ownerA");
        ownerA.valueInt().setValue(0);
        ownerA.clutterA().set(clutter);
        ownerA.clutterB().set(clutter);

        ownerA.children().add(ownerB1);
        ownerA.children().add(ownerB2);

        EntityGraph.ApplyMethod f = new EntityGraph.ApplyMethod() {
            @Override
            public boolean apply(IEntity entity) {
                if (((BaseEntity) entity).valueInt().getValue() == -1) {
                    ((BaseEntity) entity).valueStr().setValue("stopped here");
                    return false;
                } else {
                    ((BaseEntity) entity).valueStr().setValue(":)");
                    return true;
                }
            }
        };
        // execute function
        EntityGraph.applyToOwners(ownedEntity1, f);

        // assert
        assertEquals("owned1", ownedEntity1.valueStr().getValue());
        assertEquals(":)", ownerB1.valueStr().getValue());
        assertEquals(":)", ownerA.valueStr().getValue());

        assertEquals("owned2", ownedEntity2.valueStr().getValue());
        assertEquals("ownerB2", ownerB2.valueStr().getValue());

        // set up to check stopping
        ownedEntity1.valueStr().setValue("owned1");
        ownerB1.valueStr().setValue("ownerB1");
        ownerA.valueStr().setValue("ownerA");

        // execute function
        EntityGraph.applyToOwners(ownedEntity2, f);

        // assert
        assertEquals("owned1", ownedEntity1.valueStr().getValue());
        assertEquals("ownerB1", ownerB1.valueStr().getValue());
        assertEquals("ownerA", ownerA.valueStr().getValue());

        assertEquals("owned2", ownedEntity2.valueStr().getValue());
        assertEquals("stopped here", ownerB2.valueStr().getValue());

    }
}
