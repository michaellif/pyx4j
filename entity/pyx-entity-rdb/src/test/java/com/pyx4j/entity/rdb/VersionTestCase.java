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
 * Created on Feb 6, 2012
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rdb;

import java.util.Date;
import java.util.List;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.version.ItemA;
import com.pyx4j.entity.test.shared.domain.version.ItemB;
import com.pyx4j.entity.test.shared.domain.version.OwnedByVerOneToManyChild;
import com.pyx4j.entity.test.shared.domain.version.OwnedByVerOneToManyParent;
import com.pyx4j.entity.test.shared.domain.version.RefToCurrent;
import com.pyx4j.entity.test.shared.domain.version.RefToVersioned;
import com.pyx4j.gwt.server.DateUtils;

public abstract class VersionTestCase extends DatastoreTestBase {

    public void testInitialCreateDraft() {
        String testId = uniqueString();
        srv.startTransaction();
        setDBTime("2010-01-01");
        ItemA itemA1 = EntityFactory.create(ItemA.class);
        itemA1.testId().setValue(testId);

        final String draftName = "D0-" + uniqueString();
        itemA1.version().testId().setValue(testId);
        itemA1.version().name().setValue(draftName);

        //Key == null, Save draft value ?
        // Assumed
        //itemA1.saveAction().setValue(SaveAction.saveAsDraft);
        srv.persist(itemA1);
        // Key assigned and versions not set in the key
        assertEquals("assigned version", Key.VERSION_DRAFT, itemA1.getPrimaryKey().getVersion());

        // Retrieval of item as draft
        {
            ItemA itemA1r = EntityFactory.create(ItemA.class);
            itemA1r.setPrimaryKey(itemA1.getPrimaryKey().asDraftKey());

            srv.retrieve(itemA1r);
            assertTrue("version is not null", !itemA1r.version().isNull());
            assertEquals("getDraft", draftName, itemA1r.version().name().getValue());
        }

        // Retrieval of item as version that was not created
        {
            ItemA itemA1r = srv.retrieve(ItemA.class, itemA1.getPrimaryKey().asCurrentKey());
            assertTrue("current is null", itemA1r.version().isNull());
        }

        // Verify VersionData
        {
            EntityQueryCriteria<ItemA.ItemAVersion> criteria = EntityQueryCriteria.create(ItemA.ItemAVersion.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            assertEquals("VersionData.size()", 1, srv.query(criteria).size());
        }

        // Finalize
        itemA1.saveAction().setValue(SaveAction.saveAsFinal);
        srv.persist(itemA1);

        // Retrieval of item as version
        {
            ItemA itemA1r = srv.retrieve(ItemA.class, itemA1.getPrimaryKey().asCurrentKey());

            assertTrue("version is not null", !itemA1r.version().isNull());
            assertEquals("getDraft", draftName, itemA1r.version().name().getValue());
        }

        // Verify VersionData, there are only final/current version
        {
            EntityQueryCriteria<ItemA.ItemAVersion> criteria = EntityQueryCriteria.create(ItemA.ItemAVersion.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            assertEquals("VersionData.size()", 1, srv.query(criteria).size());
        }
    }

    public void testRetrieve() {
        String testId = uniqueString();

        // Initial item
        ItemA itemA1 = EntityFactory.create(ItemA.class);
        itemA1.testId().setValue(testId);

        itemA1.versions().add(itemA1.versions().$());
        itemA1.versions().add(itemA1.versions().$());
        itemA1.versions().add(itemA1.versions().$());

        final String v1Name = "V1-" + uniqueString();
        itemA1.versions().get(0).fromDate().setValue(DateUtils.detectDateformat("2011-01-01"));
        itemA1.versions().get(0).toDate().setValue(DateUtils.detectDateformat("2011-02-01"));
        itemA1.versions().get(0).name().setValue(v1Name);
        itemA1.versions().get(0).testId().setValue(testId);

        final String currentName = "V0-" + uniqueString();
        itemA1.versions().get(1).fromDate().setValue(DateUtils.detectDateformat("2011-02-01"));
        itemA1.versions().get(1).toDate().setValue(null);
        itemA1.versions().get(1).name().setValue(currentName);
        itemA1.versions().get(1).testId().setValue(testId);

        final String draftName = "Draft-" + uniqueString();
        itemA1.versions().get(2).fromDate().setValue(null);
        itemA1.versions().get(2).toDate().setValue(null);
        itemA1.versions().get(2).name().setValue(draftName);
        itemA1.versions().get(2).testId().setValue(testId);

        //Save
        srv.persist(itemA1);

        // Key assigned and versions not set in the key
        assertEquals("assigned version", Key.VERSION_CURRENT, itemA1.getPrimaryKey().getVersion());

        // Verify VersionData
        {
            EntityQueryCriteria<ItemA.ItemAVersion> criteria = EntityQueryCriteria.create(ItemA.ItemAVersion.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            assertEquals("VersionData.size()", 3, srv.query(criteria).size());
        }

        // Retrieval of item as draft
        {
            ItemA itemA1r = EntityFactory.create(ItemA.class);
            itemA1r.setPrimaryKey(itemA1.getPrimaryKey().asDraftKey());

            srv.retrieve(itemA1r);
            assertTrue("version is not null", !itemA1r.version().isNull());
            assertEquals("getDraft", draftName, itemA1r.version().name().getValue());
        }

        srv.startTransaction();

        // Retrieval of item before current existed
        {
            setDBTime("2010-01-01");
            ItemA itemA1r = srv.retrieve(ItemA.class, itemA1.getPrimaryKey());
            assertTrue("current is null", itemA1r.version().isNull());
        }

        // Retrieval of item for specific date
        {
            setDBTime("2011-03-02");
            ItemA itemA1r = srv.retrieve(ItemA.class, itemA1.getPrimaryKey().asVersionKey(DateUtils.detectDateformat("2011-01-11")));

            srv.retrieve(itemA1r);
            assertTrue("version is not null", !itemA1r.version().isNull());
            assertEquals("getSpecific", v1Name, itemA1r.version().name().getValue());
        }

        // Retrieval of item itself, by default returns current
        {
            setDBTime("2011-03-02");
            ItemA itemA1r = EntityFactory.create(ItemA.class);
            itemA1r.setPrimaryKey(itemA1.getPrimaryKey());

            srv.retrieve(itemA1r);
            assertTrue("version is not null", !itemA1r.version().isNull());
            assertEquals("getCurrent", currentName, itemA1r.version().name().getValue());
        }

    }

    public void testInitialPreload() {
        String testId = uniqueString();
        srv.startTransaction();
        setDBTime("2010-02-01");

        // Initial item
        ItemA itemA1 = EntityFactory.create(ItemA.class);
        itemA1.testId().setValue(testId);

        final String currentName = "V0-" + uniqueString();
        itemA1.version().name().setValue(currentName);
        itemA1.version().testId().setValue(testId);

        //Save initial value
        itemA1.saveAction().setValue(SaveAction.saveAsFinal);
        srv.persist(itemA1);

        // verify all Versions
        {
            ItemA itemA1r = EntityFactory.create(ItemA.class);
            itemA1r.setPrimaryKey(itemA1.getPrimaryKey());
            srv.retrieve(itemA1r);
            srv.retrieveMember(itemA1r.versions());
            assertEquals("One Final version created", 1, itemA1r.versions().size());
            assertEquals("versionNumber", "1", itemA1r.version().versionNumber().getStringView());
        }

        // verify Draft NOT created
        {
            ItemA itemA1r = EntityFactory.create(ItemA.class);
            itemA1r.setPrimaryKey(itemA1.getPrimaryKey().asDraftKey());

            srv.retrieve(itemA1r);
            assertTrue("version is null", itemA1r.version().isNull());
        }

        // verify Version created
        {
            setDBTime("2011-03-02");
            ItemA itemA1r = EntityFactory.create(ItemA.class);
            itemA1r.setPrimaryKey(itemA1.getPrimaryKey());

            srv.retrieve(itemA1r);
            assertTrue("version is not null", !itemA1r.version().isNull());
            assertEquals("getSpecific", currentName, itemA1r.version().name().getValue());
            assertEquals("Version is current starting from system time", DateUtils.detectDateformat("2010-02-01"), itemA1r.version().fromDate().getValue());
        }

        // Create another version
        setDBTime("2010-03-01");
        final String v1Name = "V1-" + uniqueString();
        itemA1.version().name().setValue(v1Name);
        itemA1.version().testId().setValue(testId);

        //Save new value
        itemA1.saveAction().setValue(SaveAction.saveAsFinal);
        srv.persist(itemA1);

        // verify all Versions
        {
            ItemA itemA1r = EntityFactory.create(ItemA.class);
            itemA1r.setPrimaryKey(itemA1.getPrimaryKey());
            srv.retrieve(itemA1r);
            srv.retrieveMember(itemA1r.versions());
            assertEquals("Number of versions created", 2, itemA1r.versions().size());
        }
    }

    public void testSaveDraft() {
        String testId = uniqueString();
        srv.startTransaction();

        // Initial item
        ItemA itemA1 = EntityFactory.create(ItemA.class);
        itemA1.testId().setValue(testId);

        final String currentName = "V0-" + uniqueString();
        itemA1.versions().add(itemA1.versions().$());
        itemA1.versions().get(0).fromDate().setValue(DateUtils.detectDateformat("2011-01-01"));
        itemA1.versions().get(0).name().setValue(currentName);
        itemA1.versions().get(0).testId().setValue(testId);

        //Save initial value
        srv.persist(itemA1);

        ItemA itemA1draft = EntityFactory.create(ItemA.class);
        itemA1draft.setPrimaryKey(itemA1.getPrimaryKey().asDraftKey());

        srv.retrieve(itemA1draft);

        final String draftName = "V1-" + uniqueString();
        itemA1draft.version().name().setValue(draftName);
        itemA1draft.version().testId().setValue(testId);

        //Save Draft
        srv.persist(itemA1draft);

        // Retrieval of item as draft
        {
            ItemA itemA1r = EntityFactory.create(ItemA.class);
            itemA1r.setPrimaryKey(itemA1.getPrimaryKey().asDraftKey());

            srv.retrieve(itemA1r);
            assertTrue("version is not null", !itemA1r.version().isNull());
            assertEquals("getDraft", draftName, itemA1r.version().name().getValue());
            assertEquals("versionNumber", "Draft", itemA1r.version().versionNumber().getStringView());
        }

        // finalize, Save draft as version
        ItemA itemA1f = EntityFactory.create(ItemA.class);
        itemA1f.setPrimaryKey(itemA1.getPrimaryKey().asDraftKey());

        srv.retrieve(itemA1f);

        final String draftV2Name = "V2-" + uniqueString();
        itemA1f.version().name().setValue(draftV2Name);

        setDBTime("2010-02-01");

        // finalize
        itemA1f.saveAction().setValue(SaveAction.saveAsFinal);
        srv.persist(itemA1f);
        srv.commit();

        // verify Draft removed
        {
            ItemA itemA1r = EntityFactory.create(ItemA.class);
            itemA1r.setPrimaryKey(itemA1.getPrimaryKey().asDraftKey());

            srv.retrieve(itemA1r);
            assertTrue("version is null", itemA1r.version().isNull());
        }

        // verify Version updated
        {
            setDBTime("2011-03-02");
            ItemA itemA1r = EntityFactory.create(ItemA.class);
            itemA1r.setPrimaryKey(itemA1.getPrimaryKey());

            srv.retrieve(itemA1r);
            assertTrue("version is not null", !itemA1r.version().isNull());
            assertEquals("getSpecific", draftV2Name, itemA1r.version().name().getValue());
        }

    }

    public void testVersionedReference() {
        String testId = uniqueString();
        srv.startTransaction();

        // Initial item
        ItemA itemA1 = EntityFactory.create(ItemA.class);
        itemA1.testId().setValue(testId);

        final String origName = "V1-" + uniqueString();
        itemA1.version().name().setValue(origName);
        itemA1.version().testId().setValue(testId);

        // Finalize as of (current) date
        setDBTime("2011-01-01");
        itemA1.saveAction().setValue(SaveAction.saveAsFinal);
        srv.persist(itemA1);

        setDBTime("2011-01-15");

        RefToCurrent refToCurrent1 = EntityFactory.create(RefToCurrent.class);
        refToCurrent1.name().setValue(uniqueString());
        refToCurrent1.testId().setValue(testId);
        refToCurrent1.itemA().set(itemA1);

        srv.persist(refToCurrent1);

        RefToVersioned refToVersioned1 = EntityFactory.create(RefToVersioned.class);
        refToVersioned1.name().setValue(uniqueString());
        refToVersioned1.testId().setValue(testId);
        refToVersioned1.itemA().set(itemA1);
        srv.persist(refToVersioned1);

        // Make new version
        // Finalize as of (current) date
        setDBTime("2011-02-01");
        final String newName = "V2-" + uniqueString();
        itemA1.version().name().setValue(newName);
        itemA1.saveAction().setValue(SaveAction.saveAsFinal);
        srv.persist(itemA1);

        //Save another Draft
        final String draftName = "D1-" + uniqueString();
        itemA1.setPrimaryKey(itemA1.getPrimaryKey().asDraftKey());
        itemA1.version().name().setValue(draftName);
        srv.persist(itemA1);

        // Verify values, Current has the value of last  finalize
        {
            RefToCurrent refToCurrent1r1 = srv.retrieve(RefToCurrent.class, refToCurrent1.getPrimaryKey());
            assertEquals("ref updated", newName, refToCurrent1r1.itemA().version().name().getValue());
            //assertEquals("date as of now", DateUtils.detectDateformat("2011-02-01"), refToCurrent1r1.itemA().forDate().getValue());
        }

        // Versioned has value of the time when it was saved
        {
            // Retrieve new version
            RefToVersioned refToVersioned1r1 = srv.retrieve(RefToVersioned.class, refToVersioned1.getPrimaryKey());
            assertEquals("ref not updated", origName, refToVersioned1r1.itemA().version().name().getValue());
            assertEquals("date as of time of save", DateUtils.detectDateformat("2011-01-15"), new Date(refToVersioned1r1.itemA().getPrimaryKey().getVersion()));
        }

        // Retrieval of item itself, by default returns current
        {
            ItemA itemA1r = srv.retrieve(ItemA.class, itemA1.getPrimaryKey().asCurrentKey());
            assertEquals("current", newName, itemA1r.version().name().getValue());
        }

    }

    public void testVersionedGraphReference() {
        String testId = uniqueString();
        srv.startTransaction();

        // First Initial item
        setDBTime("2011-01-01");
        ItemA itemA1 = EntityFactory.create(ItemA.class);
        itemA1.testId().setValue(testId);
        itemA1.name().setValue("A1");

        final String origAName = "V1A1-" + uniqueString();
        itemA1.version().name().setValue(origAName);
        itemA1.version().testId().setValue(testId);

        // Finalize
        itemA1.saveAction().setValue(SaveAction.saveAsFinal);
        srv.persist(itemA1);

        setDBTime("2011-02-01");

        ItemB itemB1 = EntityFactory.create(ItemB.class);
        itemB1.testId().setValue(testId);
        itemB1.name().setValue("B1");

        final String origBName = "V1B1-" + uniqueString();
        itemB1.version().name().setValue(origBName);
        itemB1.version().testId().setValue(testId);
        itemB1.version().itemAFixed().set(itemA1);
        itemB1.saveAction().setValue(SaveAction.saveAsFinal);
        srv.persist(itemB1);

        setDBTime("2011-03-01");
        // Update A1
        itemA1.version().name().setValue("V2A1-" + uniqueString());
        // Finalize
        itemA1.saveAction().setValue(SaveAction.saveAsFinal);
        srv.persist(itemA1);

        // Retrieval of itemB and verify @Versioned
        {
            ItemB itemB1r = srv.retrieve(ItemB.class, itemB1.getPrimaryKey());
            assertEquals("correct data", origBName, itemB1r.version().name().getValue());
            assertEquals("correct data", itemA1.getPrimaryKey().asLong(), itemB1r.version().itemAFixed().getPrimaryKey().asLong());
            assertEquals("AttachLevel", AttachLevel.Attached, itemB1r.version().itemAFixed().getAttachLevel());
            assertEquals("ref not updated", origAName, itemB1r.version().itemAFixed().version().name().getValue());
        }

        {
            setDBTime("2011-04-01");
            //Update Owned VersionedEntity,  Retrieval of itemB as draft and Finalize
            final String updateBName = "V2B1-" + uniqueString();
            {
                ItemB itemB1r = srv.retrieve(ItemB.class, itemB1.getPrimaryKey());
                assertEquals("ref not updated", origAName, itemB1r.version().itemAFixed().version().name().getValue());

                itemB1r.saveAction().setValue(SaveAction.saveAsFinal);
                itemB1r.version().name().setValue(updateBName);

                srv.merge(itemB1r);
            }

            {
                ItemB itemB1r = srv.retrieve(ItemB.class, itemB1.getPrimaryKey());
                assertEquals("correct data", updateBName, itemB1r.version().name().getValue());
                assertEquals("ref not updated", origAName, itemB1r.version().itemAFixed().version().name().getValue());
            }
        }
    }

    //TODO implement in future
    @SuppressWarnings("deprecation")
    public void TODO_testVersionedGraphUnidirectionalOneToOne() {
        String testId = uniqueString();
        srv.startTransaction();

        setDBTime("2011-01-01");
        // Initial item
        ItemB itemB1 = EntityFactory.create(ItemB.class);
        itemB1.testId().setValue(testId);
        itemB1.name().setValue("B1");

        final String origBName = "V1B1-" + uniqueString();
        itemB1.version().name().setValue(origBName);
        itemB1.version().testId().setValue(testId);

        ItemA itemA1 = itemB1.version().itemAOwned();
        itemA1.testId().setValue(testId);
        itemA1.name().setValue("A1");

        final String origA1Name = "V1A1-" + uniqueString();
        itemA1.version().name().setValue(origA1Name);
        itemA1.version().testId().setValue(testId);

        srv.persist(itemB1);

        setDBTime("2011-02-01");

        Key originalItemAKey;
        {
            ItemB itemB1r = srv.retrieve(ItemB.class, itemB1.getPrimaryKey());
            assertEquals("correct data", origBName, itemB1r.version().name().getValue());
            assertEquals("correct data", origA1Name, itemB1r.version().itemAOwned().version().name().getValue());
            originalItemAKey = itemB1r.version().itemAOwned().getPrimaryKey();
        }

        setDBTime("2011-03-01");
        //Update Owned VersionedEntity,  Retrieval of itemB as draft and Finalize
        final String updateBName = "V2B1-" + uniqueString();
        final String updateA2Name = "V2A2-" + uniqueString();
        {
            ItemB itemB1r = EntityFactory.create(ItemB.class);
            itemB1r.setPrimaryKey(itemB1.getPrimaryKey().asDraftKey());
            srv.retrieve(itemB1r);

            itemB1r.setPrimaryKey(itemB1r.getPrimaryKey().asCurrentKey());
            itemB1.version().name().setValue(updateBName);
            itemB1.version().itemAOwned().version().name().setValue(updateA2Name);

            srv.merge(itemB1r);
        }

        {
            ItemB itemB1r = srv.retrieve(ItemB.class, itemB1.getPrimaryKey());
            assertEquals("correct data", updateBName, itemB1r.version().name().getValue());
            assertEquals("correct data", updateA2Name, itemB1r.version().itemAOwned().version().name().getValue());
            assertEquals("correct reference", originalItemAKey, itemB1r.version().itemAOwned().getPrimaryKey());
        }

        // Old version of ItemB
        {
            ItemB itemB1r = EntityFactory.create(ItemB.class);
            itemB1r.setPrimaryKey(itemB1.getPrimaryKey().asVersionKey(DateUtils.detectDateformat("2011-01-11")));
            srv.retrieve(itemB1r);

            assertEquals("correct data", origBName, itemB1r.version().name().getValue());
            assertEquals("correct data", origA1Name, itemB1r.version().itemAOwned().version().name().getValue());
            assertEquals("correct reference", originalItemAKey, itemB1r.version().itemAOwned().getPrimaryKey());
        }
    }

    public void testVersionedGraphWithOneToManyPersist() {
        testVersionedGraphWithOneToManySave(TestCaseMethod.Persist);
    }

    public void testVersionedGraphWithOneToManyMerge() {
        testVersionedGraphWithOneToManySave(TestCaseMethod.Merge);
    }

    public void testVersionedGraphWithOneToManySave(TestCaseMethod testCaseMethod) {
        String testId = uniqueString();
        srv.startTransaction();

        OwnedByVerOneToManyParent o = EntityFactory.create(OwnedByVerOneToManyParent.class);
        o.testId().setValue(testId);
        o.name().setValue(uniqueString());

        o.version().children().add(EntityFactory.create(OwnedByVerOneToManyChild.class));
        o.version().children().add(EntityFactory.create(OwnedByVerOneToManyChild.class));

        o.version().children().get(0).testId().setValue(testId);
        o.version().children().get(0).name().setValue(uniqueString());

        o.version().children().get(1).testId().setValue(testId);
        o.version().children().get(1).name().setValue(uniqueString());

        // Save child and owner
        srvSave(o, testCaseMethod);

        // finalize, Save draft as version
        {
            OwnedByVerOneToManyParent o1r = srv.retrieve(OwnedByVerOneToManyParent.class, o.getPrimaryKey().asDraftKey());
            o1r.saveAction().setValue(SaveAction.saveAsFinal);
            srv.persist(o1r);
        }
    }

    public void testVersionQueryCriteria() {
        String testId = uniqueString();
        srv.startTransaction();

        // Initial item
        ItemA itemA1 = EntityFactory.create(ItemA.class);
        itemA1.testId().setValue(testId);
        itemA1.version().name().setValue("A1-" + uniqueString());
        itemA1.version().testId().setValue(testId);

        //Save initial value
        itemA1.saveAction().setValue(SaveAction.saveAsFinal);
        srv.persist(itemA1);

        ItemA itemA2 = EntityFactory.create(ItemA.class);
        itemA2.testId().setValue(testId);
        itemA2.version().name().setValue("A2-" + uniqueString());
        itemA2.version().testId().setValue(testId);

        //Save initial value
        itemA2.saveAction().setValue(SaveAction.saveAsDraft);
        srv.persist(itemA2);

        if (false) {
            EntityQueryCriteria<ItemA> criteria = EntityQueryCriteria.create(ItemA.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            List<ItemA> list = srv.query(criteria);
            assertEquals("final data", 1, list.size());
        }

    }
}
