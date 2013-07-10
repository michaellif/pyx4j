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
 * Created on Jan 15, 2012
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
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.join.AccPrincipal;
import com.pyx4j.entity.test.shared.domain.join.AccPrincipalEdit;
import com.pyx4j.entity.test.shared.domain.join.AccSubject;
import com.pyx4j.entity.test.shared.domain.join.AccSubjectPrincipal;
import com.pyx4j.entity.test.shared.domain.join.AggregatorDTO;
import com.pyx4j.entity.test.shared.domain.join.BRefCascadeChild;
import com.pyx4j.entity.test.shared.domain.join.BRefCascadeOwner;
import com.pyx4j.entity.test.shared.domain.join.BRefPolyReadChild;
import com.pyx4j.entity.test.shared.domain.join.BRefPolyReadOwner1;
import com.pyx4j.entity.test.shared.domain.join.BRefPolyReadOwner2;
import com.pyx4j.entity.test.shared.domain.join.BRefReadChild;
import com.pyx4j.entity.test.shared.domain.join.BRefReadOwner;
import com.pyx4j.entity.test.shared.domain.join.OneToOneReadChild;
import com.pyx4j.entity.test.shared.domain.join.OneToOneReadOwner;
import com.pyx4j.gwt.server.DateUtils;

public abstract class JoinTableTestCase extends DatastoreTestBase {

    public void testJoinTableUpdate() {
        String testId = uniqueString();

        AccPrincipal principal1 = EntityFactory.create(AccPrincipal.class);
        principal1.name().setValue(uniqueString());
        principal1.testId().setValue(testId);
        srv.persist(principal1);

        AccPrincipal principal2 = EntityFactory.create(AccPrincipal.class);
        principal2.name().setValue(uniqueString());
        principal2.testId().setValue(testId);
        srv.persist(principal2);

        AccSubject subject1 = EntityFactory.create(AccSubject.class);
        subject1.name().setValue(uniqueString());
        subject1.testId().setValue(testId);
        srv.persist(subject1);

        AccSubject subject2 = EntityFactory.create(AccSubject.class);
        subject2.name().setValue(uniqueString());
        subject2.testId().setValue(testId);
        srv.persist(subject2);

        AccSubjectPrincipal join11 = EntityFactory.create(AccSubjectPrincipal.class);
        join11.principal().set(principal1);
        join11.subject().set(subject1);
        srv.persist(join11);

        AccSubjectPrincipal join22 = EntityFactory.create(AccSubjectPrincipal.class);
        join22.principal().set(principal2);
        join22.subject().set(subject2);
        srv.persist(join22);

        {
            // Test data not updated with JoinTable - should not update "cascade = false"
            AccSubject subject1r1 = srv.retrieve(AccSubject.class, subject1.getPrimaryKey());
            Assert.assertEquals("Data retrieved using JoinTable", AttachLevel.Detached, subject1r1.access().getAttachLevel());
            subject1r1.access().setAttachLevel(AttachLevel.Attached);
            Assert.assertEquals("Data retrieved using JoinTable", AttachLevel.Attached, subject1r1.access().getAttachLevel());

            subject1r1.access().add(principal2);
            srv.persist(subject1r1);

            // Verify join table itself is not updated
            EntityQueryCriteria<AccSubjectPrincipal> criteria = EntityQueryCriteria.create(AccSubjectPrincipal.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().subject(), subject1));

            List<AccSubjectPrincipal> data = srv.query(criteria);
            Assert.assertEquals("result set size", 1, data.size());
        }

        {
            AccPrincipalEdit edit1 = EntityFactory.create(AccPrincipalEdit.class);
            edit1.name().setValue(uniqueString());
            edit1.testId().setValue(testId);
            edit1.setPrimaryKey(principal1.getPrimaryKey());
            srv.persist(edit1);

            AccPrincipalEdit edit1r1 = srv.retrieve(AccPrincipalEdit.class, principal1.getPrimaryKey());

            Assert.assertEquals("Data retrieved using JoinTable", 1, edit1r1.subjects().size());
            Assert.assertTrue("Inserted value present", edit1r1.subjects().contains(subject1));

            // Update tabe now
            edit1r1.subjects().add(subject2);
            srv.persist(edit1r1);

            AccPrincipalEdit edit1r2 = srv.retrieve(AccPrincipalEdit.class, principal1.getPrimaryKey());
            Assert.assertEquals("Data retrieved using JoinTable", 2, edit1r2.subjects().size());
            Assert.assertTrue("Inserted value present", edit1r2.subjects().contains(subject1));
            Assert.assertTrue("Inserted value present", edit1r2.subjects().contains(subject2));
        }
    }

    public void testJoinTableInsertPersist() {
        testJoinTableInsert(TestCaseMethod.Persist);
    }

    public void testJoinTableInsertMerge() {
        testJoinTableInsert(TestCaseMethod.Merge);
    }

    public void testJoinTableInsert(TestCaseMethod testCaseMethod) {
        String testId = uniqueString();

        AccPrincipal principal1 = EntityFactory.create(AccPrincipal.class);
        principal1.name().setValue(uniqueString());
        principal1.testId().setValue(testId);
        srv.persist(principal1);

        AccSubject subject1 = EntityFactory.create(AccSubject.class);
        subject1.name().setValue(uniqueString());
        subject1.testId().setValue(testId);
        srv.persist(subject1);

        AccPrincipalEdit edit1 = EntityFactory.create(AccPrincipalEdit.class);
        edit1.name().setValue(uniqueString());
        edit1.testId().setValue(testId);
        edit1.setPrimaryKey(principal1.getPrimaryKey());
        edit1.subjects().add(subject1);

        srvSave(edit1, testCaseMethod);

        {
            AccPrincipalEdit edit1r1 = srv.retrieve(AccPrincipalEdit.class, principal1.getPrimaryKey());
            Assert.assertEquals("Data retrieved using JoinTable", 1, edit1r1.subjects().size());
            Assert.assertTrue("Inserted value present", edit1r1.subjects().contains(subject1));
        }

        AccSubject subject2 = EntityFactory.create(AccSubject.class);
        subject2.name().setValue(uniqueString());
        subject2.testId().setValue(testId);
        srv.persist(subject2);

        edit1.subjects().add(subject2);
        edit1.subjects().remove(subject1);

        srvSave(edit1, testCaseMethod);

        {
            AccPrincipalEdit edit1r1 = srv.retrieve(AccPrincipalEdit.class, principal1.getPrimaryKey());
            Assert.assertEquals("Data retrieved using JoinTable", 1, edit1r1.subjects().size());
            Assert.assertTrue("Inserted value present", edit1r1.subjects().contains(subject2));
        }
    }

    private void testJoinTableCascadeAll(TestCaseMethod testCaseMethod) {
        String testId = uniqueString();
        String fixedNameId = "fixed-" + uniqueString();

        AccPrincipal principal1 = EntityFactory.create(AccPrincipal.class);
        principal1.name().setValue(uniqueString());
        principal1.testId().setValue(testId);
        srv.persist(principal1);

        AccSubject subject1 = EntityFactory.create(AccSubject.class);
        subject1.name().setValue(fixedNameId);
        subject1.testId().setValue(testId);
        srv.persist(subject1);

        AccPrincipalEdit edit1 = EntityFactory.create(AccPrincipalEdit.class);
        edit1.name().setValue(uniqueString());
        edit1.testId().setValue(testId);
        edit1.setPrimaryKey(principal1.getPrimaryKey());
        edit1.subjects().add(subject1);

        srvSave(edit1, testCaseMethod);

        // Update AccSubject while editing AccPrincipalEdit, changes should not be saved since it is not Owned
        edit1.name().setValue("changes Ok" + uniqueString());

        AccSubject subject1edit = edit1.subjects().iterator().next();
        subject1edit.name().setValue("other changes not to be saved" + uniqueString());
        subject1edit.updated().setValue(DateUtils.detectDateformat("2011-01-01"));

        srvSave(edit1, testCaseMethod);

        // verify
        {
            AccSubject subject1r1 = srv.retrieve(AccSubject.class, subject1.getPrimaryKey());
            Assert.assertEquals("Data not changes", fixedNameId, subject1r1.name().getValue());
        }

    }

    public void testJoinTableCascadeAllPersist() {
        testJoinTableCascadeAll(TestCaseMethod.Persist);
    }

    public void testJoinTableCascadeAllMerge() {
        testJoinTableCascadeAll(TestCaseMethod.Merge);
    }

    public void testBackreferencesRead() {
        // Setup data
        String testId = uniqueString();

        BRefReadOwner owner1 = EntityFactory.create(BRefReadOwner.class);
        owner1.name().setValue(uniqueString());
        owner1.testId().setValue(testId);
        srv.persist(owner1);

        BRefReadChild c1 = EntityFactory.create(BRefReadChild.class);
        c1.name().setValue(uniqueString());
        c1.testId().setValue(testId);
        c1.sortColumn().setValue(2);
        c1.bRefOwner().set(owner1);
        srv.persist(c1);

        BRefReadChild c2 = EntityFactory.create(BRefReadChild.class);
        c2.name().setValue(uniqueString());
        c2.testId().setValue(testId);
        c2.sortColumn().setValue(1);
        c2.bRefOwner().set(owner1);
        srv.persist(c2);

        {
            BRefReadOwner owner1r = srv.retrieve(BRefReadOwner.class, owner1.getPrimaryKey());
            Assert.assertEquals("Data retrieved using JoinTable", 2, owner1r.children().size());
            Assert.assertEquals("Inserted value2 present", c2, owner1r.children().get(0));
            Assert.assertEquals("Inserted value1 present", c1, owner1r.children().get(1));

        }

    }

    public void testBackreferencesReadOwnerPolymorphic() {
        // Setup data
        String testId = uniqueString();

        BRefPolyReadOwner1 owner1 = EntityFactory.create(BRefPolyReadOwner1.class);
        owner1.name().setValue(uniqueString());
        owner1.testId().setValue(testId);
        srv.persist(owner1);

        BRefPolyReadOwner2 owner2 = EntityFactory.create(BRefPolyReadOwner2.class);
        owner2.name().setValue(uniqueString());
        owner2.testId().setValue(testId);
        srv.persist(owner2);

        BRefPolyReadChild c1 = EntityFactory.create(BRefPolyReadChild.class);
        c1.name().setValue(uniqueString());
        c1.testId().setValue(testId);
        c1.sortColumn().setValue(2);
        c1.bRefOwner().set(owner1);
        srv.persist(c1);

        BRefPolyReadChild c2 = EntityFactory.create(BRefPolyReadChild.class);
        c2.name().setValue(uniqueString());
        c2.testId().setValue(testId);
        c2.sortColumn().setValue(1);
        c2.bRefOwner().set(owner2);
        srv.persist(c2);

        {
            BRefPolyReadOwner1 owner1r = srv.retrieve(BRefPolyReadOwner1.class, owner1.getPrimaryKey());
            Assert.assertEquals("Data retrieved using JoinTable", 1, owner1r.children().size());
            Assert.assertEquals("Inserted value1 present", c1, owner1r.children().get(0));

            BRefPolyReadOwner2 owner2r = srv.retrieve(BRefPolyReadOwner2.class, owner2.getPrimaryKey());
            Assert.assertEquals("Data retrieved using JoinTable", 1, owner2r.children().size());
            Assert.assertEquals("Inserted value1 present", c2, owner2r.children().get(0));
        }
    }

    //TODO cascade persist
    public void TODO_testBackreferencesCascade() {
        // Setup data
        String testId = uniqueString();

        BRefCascadeChild c1 = EntityFactory.create(BRefCascadeChild.class);
        c1.name().setValue(uniqueString());
        c1.testId().setValue(testId);
        c1.sortColumn().setValue(2);
        srv.persist(c1);

        BRefCascadeChild c2 = EntityFactory.create(BRefCascadeChild.class);
        c2.name().setValue(uniqueString());
        c2.testId().setValue(testId);
        c2.sortColumn().setValue(1);
        srv.persist(c2);

        BRefCascadeOwner owner1 = EntityFactory.create(BRefCascadeOwner.class);
        owner1.name().setValue(uniqueString());
        owner1.testId().setValue(testId);
        owner1.children().add(c2);
        owner1.children().add(c1);
        srv.persist(owner1);

        {

        }
    }

    public void testOneToOneOwnedWriteNoCascade() {
        String testId = uniqueString();

        OneToOneReadOwner o = EntityFactory.create(OneToOneReadOwner.class);
        o.name().setValue(uniqueString());
        o.testId().setValue(testId);
        srv.persist(o);

        //o.child().name().setValue(uniqueString());
        srv.persist(o.child());

        {
            OneToOneReadChild c1r = srv.retrieve(OneToOneReadChild.class, o.child().getPrimaryKey());
            Assert.assertEquals("Got child object was assigned to parent", o.getPrimaryKey(), c1r.o2oOwner().getPrimaryKey());
        }

        // The same test with data in DTO

        AggregatorDTO dto = EntityFactory.create(AggregatorDTO.class);
        dto.o2oReadOwner().name().setValue(uniqueString());
        dto.o2oReadOwner().testId().setValue(testId + "-2");
        srv.persist(dto.o2oReadOwner());

        dto.o2oReadOwner().child().name().setValue(uniqueString());
        srv.persist(dto.o2oReadOwner().child());

        {
            OneToOneReadChild c2r = srv.retrieve(OneToOneReadChild.class, dto.o2oReadOwner().child().getPrimaryKey());
            Assert.assertEquals("Got child object was assigned to parent", dto.o2oReadOwner().getPrimaryKey(), c2r.o2oOwner().getPrimaryKey());
        }

    }

    public void testOneToOneRead() {
        String testId = uniqueString();

        OneToOneReadOwner o = EntityFactory.create(OneToOneReadOwner.class);
        o.name().setValue(uniqueString());
        o.testId().setValue(testId);
        srv.persist(o);

        //Test table initialization
        {
            OneToOneReadOwner o1r = srv.retrieve(OneToOneReadOwner.class, o.getPrimaryKey());
            Assert.assertNull("Got child object", o1r.child().getPrimaryKey());
            // There are no data e.g. Value is null, consider it Attached
            Assert.assertEquals("Got child object Attached", AttachLevel.Attached, o1r.child().getAttachLevel());
        }

        OneToOneReadChild c = EntityFactory.create(OneToOneReadChild.class);
        c.name().setValue(uniqueString());
        c.testId().setValue(testId);
        c.o2oOwner().set(o);
        srv.persist(c);

        {
            OneToOneReadOwner o1r = srv.retrieve(OneToOneReadOwner.class, o.getPrimaryKey());
            Assert.assertEquals("Got child object", c.getPrimaryKey(), o1r.child().getPrimaryKey());
            Assert.assertEquals("Got child object Detached", AttachLevel.IdOnly, o1r.child().getAttachLevel());
        }

    }

    public void testOneToOneCascadeUpdatePersist() {
        testOneToOneCascadeUpdate(TestCaseMethod.Persist);
    }

    public void testOneToOneCascadeUpdateMerge() {
        testOneToOneCascadeUpdate(TestCaseMethod.Merge);
    }

    public void testOneToOneCascadeUpdate(TestCaseMethod testCaseMethod) {
        String testId = uniqueString();

        OneToOneReadOwner o = EntityFactory.create(OneToOneReadOwner.class);
        o.name().setValue(uniqueString());
        o.testId().setValue(testId);

        // This will initialize child and it will be created
        //o.child().name().setValue(null);
        srvSave(o, testCaseMethod);

        //Test child table initialization
        {
            OneToOneReadOwner o1r = srv.retrieve(OneToOneReadOwner.class, o.getPrimaryKey());
            Assert.assertNull("Got child object", o1r.child().getPrimaryKey());
            // There are no data e.g. Value is null, consider it Attached
            Assert.assertEquals("Got child object Attached", AttachLevel.Attached, o1r.child().getAttachLevel());
        }

        {
            OneToOneReadOwner o1r = srv.retrieve(OneToOneReadOwner.class, o.getPrimaryKey());
            o1r.child().name().setValue(uniqueString());
            o1r.child().testId().setValue(testId);

            srvSave(o1r, testCaseMethod);

            {
                OneToOneReadOwner o1r2 = srv.retrieve(OneToOneReadOwner.class, o.getPrimaryKey());
                Assert.assertNotNull("Did not load child object", o1r2.child().getPrimaryKey());
                // There are no data e.g. Value is null, consider it Attached
                Assert.assertEquals("Got child object Attached", AttachLevel.IdOnly, o1r2.child().getAttachLevel());
            }
        }

        // ----  Child creation even with null values

        OneToOneReadOwner o2 = EntityFactory.create(OneToOneReadOwner.class);
        o2.name().setValue(uniqueString());
        o2.testId().setValue(testId);

        // This will initialize child and it will be created
        o2.child().name().setValue(null);
        srvSave(o2, testCaseMethod);

        //Test child table initialization
        {
            OneToOneReadOwner o2r = srv.retrieve(OneToOneReadOwner.class, o.getPrimaryKey());
            Assert.assertNotNull("Got child object", o2r.child().getPrimaryKey());
            // There are no data e.g. Value is null, consider it Attached
            Assert.assertEquals("Got child object Attached", AttachLevel.IdOnly, o2r.child().getAttachLevel());
        }

    }

    public void testOneToOneQuery() {
        String testId = uniqueString();

        OneToOneReadOwner o = EntityFactory.create(OneToOneReadOwner.class);
        o.name().setValue(uniqueString());
        o.testId().setValue(testId);
        srv.persist(o);

        // Test implementation of NOT EXISTS
        {
            EntityQueryCriteria<OneToOneReadOwner> criteria = EntityQueryCriteria.create(OneToOneReadOwner.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.notExists(criteria.proto().child()));

            List<OneToOneReadOwner> data = srv.query(criteria);
            Assert.assertEquals("Found using JoinTable", 1, data.size());
            Assert.assertEquals("Got right object", o.getPrimaryKey(), data.get(0).getPrimaryKey());
        }

        OneToOneReadChild c = EntityFactory.create(OneToOneReadChild.class);
        c.name().setValue(uniqueString());
        c.testId().setValue(testId);
        c.o2oOwner().set(o);
        srv.persist(c);

        {
            EntityQueryCriteria<OneToOneReadOwner> criteria = EntityQueryCriteria.create(OneToOneReadOwner.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().child().name(), c.name()));

            List<OneToOneReadOwner> data = srv.query(criteria);
            Assert.assertEquals("Found using JoinTable", 1, data.size());
            Assert.assertEquals("Got right object", o.getPrimaryKey(), data.get(0).getPrimaryKey());
            Assert.assertEquals("Data retrieved using JoinTable", c.getPrimaryKey(), data.get(0).child().getPrimaryKey());
        }

        // Test join search
        c.name().setValue(null);
        srv.persist(c);

        {
            EntityQueryCriteria<OneToOneReadOwner> criteria = EntityQueryCriteria.create(OneToOneReadOwner.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.isNull(criteria.proto().child().name()));

            List<OneToOneReadOwner> data = srv.query(criteria);
            Assert.assertEquals("Found using JoinTable", 1, data.size());
            Assert.assertEquals("Got right object", o.getPrimaryKey(), data.get(0).getPrimaryKey());
            Assert.assertEquals("Data retrieved using JoinTable", c.getPrimaryKey(), data.get(0).child().getPrimaryKey());
        }

    }
}
