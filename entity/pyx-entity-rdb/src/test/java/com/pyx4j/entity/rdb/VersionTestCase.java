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

import junit.framework.Assert;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.rpc.EntityCriteriaByPK;
import com.pyx4j.entity.server.IEntityPersistenceService.ICursorIterator;
import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IVersionedEntity.SaveAction;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria;
import com.pyx4j.entity.shared.criterion.EntityQueryCriteria.VersionedCriteria;
import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.entity.shared.utils.VersionedEntityUtils;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.version.ItemA;
import com.pyx4j.entity.test.shared.domain.version.ItemA.ItemAVersion;
import com.pyx4j.entity.test.shared.domain.version.ItemAInconclusive;
import com.pyx4j.entity.test.shared.domain.version.ItemB;
import com.pyx4j.entity.test.shared.domain.version.OwnedByVerOneToManyMChild;
import com.pyx4j.entity.test.shared.domain.version.OwnedByVerOneToManyParent;
import com.pyx4j.entity.test.shared.domain.version.OwnedByVerOneToManyUChild;
import com.pyx4j.entity.test.shared.domain.version.PolymorphicVersionedA;
import com.pyx4j.entity.test.shared.domain.version.PolymorphicVersionedB;
import com.pyx4j.entity.test.shared.domain.version.PolymorphicVersionedSuper;
import com.pyx4j.entity.test.shared.domain.version.RefToCurrent;
import com.pyx4j.entity.test.shared.domain.version.RefToVersioned;
import com.pyx4j.gwt.server.DateUtils;

public abstract class VersionTestCase extends DatastoreTestBase {

    public void testInitialCreateDraft() {
        String testId = uniqueString();
        setDBTime("2010-01-01");
        ItemA itemA1 = EntityFactory.create(ItemA.class);
        itemA1.testId().setValue(testId);

        final String draftName = "D0-" + uniqueString();
        itemA1.version().testId().setValue(testId);
        itemA1.version().name().setValue(draftName);

        //Key == null, Save draft value ?
        itemA1.saveAction().setValue(SaveAction.saveAsDraft);
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

    public void TODO_testFinalizeNonExistingDraft() {
        String testId = uniqueString();
        final String v1Name = "V1-" + uniqueString();

        // Initial item
        ItemA itemA1 = EntityFactory.create(ItemA.class);
        itemA1.testId().setValue(testId);
        itemA1.version().name().setValue(v1Name);

        //Save
        srv.persist(itemA1);

        itemA1 = srv.retrieve(ItemA.class, itemA1.getPrimaryKey().asDraftKey());
        assertEquals("value Ok", v1Name, itemA1.version().name().getValue());

        // Finalize
        itemA1.saveAction().setValue(SaveAction.saveAsFinal);
        srv.persist(itemA1);

        // Try Finalize that should fail
        itemA1 = srv.retrieve(ItemA.class, itemA1.getPrimaryKey().asDraftKey());
        itemA1.saveAction().setValue(SaveAction.saveAsFinal);

        boolean saved = false;
        try {
            srv.persist(itemA1);
            saved = true;
        } catch (Error ok) {
        }

        if (saved) {
            Assert.fail("Should not finalize version when there are no draft");
        }

        // Verify data was not changed
        itemA1 = srv.retrieve(ItemA.class, itemA1.getPrimaryKey().asCurrentKey());
        assertEquals("value Ok", v1Name, itemA1.version().name().getValue());
    }

    public void testRetrieve() {
        String testId = uniqueString();

        // Initial item
        ItemA itemA1 = EntityFactory.create(ItemA.class);
        itemA1.testId().setValue(testId);
        srv.persist(itemA1);
        final String draftName = "Draft-" + uniqueString();
        itemA1.version().name().setValue(draftName);
        itemA1.version().testId().setValue(testId);
        srv.persist(itemA1);

        final String v1Name = "V1-" + uniqueString();
        ItemA.ItemAVersion v1 = EntityFactory.create(ItemA.ItemAVersion.class);
        v1.holder().set(itemA1);
        v1.fromDate().setValue(DateUtils.detectDateformat("2011-01-01"));
        v1.toDate().setValue(DateUtils.detectDateformat("2011-02-01"));
        v1.name().setValue(v1Name);
        v1.testId().setValue(testId);
        srv.persist(v1);

        final String currentName = "V0-" + uniqueString();
        ItemA.ItemAVersion v0 = EntityFactory.create(ItemA.ItemAVersion.class);
        v0.holder().set(itemA1);
        v0.fromDate().setValue(DateUtils.detectDateformat("2011-02-01"));
        v0.toDate().setValue(null);
        v0.name().setValue(currentName);
        v0.testId().setValue(testId);
        srv.persist(v0);

        // Verify VersionData
        {
            EntityQueryCriteria<ItemA.ItemAVersion> criteria = EntityQueryCriteria.create(ItemA.ItemAVersion.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            assertEquals("VersionData.size()", 3, srv.query(criteria).size());
        }

        // Retrieval of item as draft by Pk
        {
            ItemA itemA1r = EntityFactory.create(ItemA.class);
            itemA1r.setPrimaryKey(itemA1.getPrimaryKey().asDraftKey());

            srv.retrieve(itemA1r);
            assertTrue("version is not null", !itemA1r.version().isNull());
            assertEquals("getDraft", draftName, itemA1r.version().name().getValue());
        }
        //Retrieval of item as draft by Query
        {
            ItemA itemA1r = srv.retrieve(EntityCriteriaByPK.create(ItemA.class, itemA1.getPrimaryKey().asDraftKey()));
            assertTrue("version is not null", !itemA1r.version().isNull());
            assertEquals("getDraft", draftName, itemA1r.version().name().getValue());
        }

        // Retrieval of item as draft toStringMemebers
        {
            ItemA itemA1r = srv.retrieve(ItemA.class, itemA1.getPrimaryKey().asDraftKey(), AttachLevel.ToStringMembers);
            assertEquals("ToStringMembers getStringView", " - " + draftName, itemA1r.getStringView());
        }

        // Retrieval of item before current existed by Pk
        {
            setDBTime("2010-01-01");
            ItemA itemA1r = srv.retrieve(ItemA.class, itemA1.getPrimaryKey().asCurrentKey());
            assertTrue("current is null", itemA1r.version().isNull());
        }
        // Retrieval of item before current existed by Query
        {
            setDBTime("2010-01-01");
            ItemA itemA1r = srv.retrieve(EntityCriteriaByPK.create(ItemA.class, itemA1.getPrimaryKey().asCurrentKey()));
            assertTrue("current is null", itemA1r.version().isNull());
        }

        // Retrieval of item for specific date by Pk
        {
            setDBTime("2011-03-02");
            ItemA itemA1r = srv.retrieve(ItemA.class, itemA1.getPrimaryKey().asVersionKey(DateUtils.detectDateformat("2011-01-11")));

            srv.retrieve(itemA1r);
            assertTrue("version is not null", !itemA1r.version().isNull());
            assertEquals("getSpecific", v1Name, itemA1r.version().name().getValue());

            //assertEquals("version of Pk", DateUtils.detectDateformat("2011-01-01").getTime(), itemA1r.getPrimaryKey().getVersion());
        }
        // Retrieval of item for specific date by Query
        {
            setDBTime("2011-03-02");
            ItemA itemA1r = srv.retrieve(EntityCriteriaByPK.create(ItemA.class, itemA1.getPrimaryKey().asVersionKey(DateUtils.detectDateformat("2011-01-11"))));

            srv.retrieve(itemA1r);
            assertTrue("version is not null", !itemA1r.version().isNull());
            assertEquals("getSpecific", v1Name, itemA1r.version().name().getValue());

            //assertEquals("version of Pk", DateUtils.detectDateformat("2011-01-01").getTime(), itemA1r.getPrimaryKey().getVersion());
        }

        // Retrieval of version Data by itself, verify PK of owner/holder
        {
            ItemAVersion itemA1r = srv.retrieve(ItemAVersion.class, v0.getPrimaryKey());
        }

        // Retrieval of item itself, by default returns current
        {
            setDBTime("2011-03-02");
            ItemA itemA1r = EntityFactory.create(ItemA.class);
            itemA1r.setPrimaryKey(itemA1.getPrimaryKey().asCurrentKey());

            srv.retrieve(itemA1r);
            assertTrue("version is not null", !itemA1r.version().isNull());
            assertEquals("getCurrent", currentName, itemA1r.version().name().getValue());
        }

        // Retrieval of item as current toStringMemebers
        {
            ItemA itemA1r = srv.retrieve(ItemA.class, itemA1.getPrimaryKey().asCurrentKey(), AttachLevel.ToStringMembers);
            assertEquals("ToStringMembers getStringView", " - " + currentName, itemA1r.getStringView());
        }

    }

    public void testInitialPreload() {
        String testId = uniqueString();
        setDBTime("2010-02-01");

        // Initial item
        ItemA itemA1 = EntityFactory.create(ItemA.class);
        itemA1.testId().setValue(testId);

        final String currentName = "V0-" + uniqueString();
        itemA1.version().name().setValue(currentName);
        itemA1.version().testId().setValue(testId);

        //Save initial value (Draft then finalize)
        itemA1.saveAction().setValue(SaveAction.saveAsDraft);
        srv.persist(itemA1);
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

        //Save new value (Draft then finalize)
        srv.persist(itemA1);
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

        setDBTime("2011-01-01");

        // Initial item
        ItemA itemA1 = EntityFactory.create(ItemA.class);
        itemA1.testId().setValue(testId);

        final String currentName = "V0-" + uniqueString();
        itemA1.version().name().setValue(currentName);
        itemA1.version().testId().setValue(testId);

        //Save initial value
        itemA1.saveAction().setValue(SaveAction.saveAsFinal);
        srv.persist(itemA1);

        ItemA itemA1draft = EntityFactory.create(ItemA.class);
        itemA1draft.setPrimaryKey(itemA1.getPrimaryKey().asDraftKey());

        srv.retrieve(itemA1draft);

        final String draftName = "V1-" + uniqueString();
        itemA1draft.version().name().setValue(draftName);
        itemA1draft.version().testId().setValue(testId);

        //Save Draft
        itemA1draft.saveAction().setValue(SaveAction.saveAsDraft);
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
            assertFalse("version is not null", itemA1r.version().isNull());
            assertEquals("getSpecific", draftV2Name, itemA1r.version().name().getValue());
        }

    }

    public void testVersionedReference() {
        String testId = uniqueString();

        // Initial item
        ItemA itemA1 = EntityFactory.create(ItemA.class);
        itemA1.testId().setValue(testId);

        final String origName = "V1-" + uniqueString();
        itemA1.version().name().setValue(origName);
        itemA1.version().testId().setValue(testId);

        itemA1.saveAction().setValue(SaveAction.saveAsDraft);
        srv.persist(itemA1);

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
        itemA1.saveAction().setValue(SaveAction.saveAsDraft);
        srv.persist(itemA1);
        itemA1.saveAction().setValue(SaveAction.saveAsFinal);
        srv.persist(itemA1);

        //Save another Draft
        final String draftName = "D1-" + uniqueString();
        itemA1.setPrimaryKey(itemA1.getPrimaryKey().asDraftKey());
        itemA1.version().name().setValue(draftName);
        itemA1.saveAction().setValue(SaveAction.saveAsDraft);
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

        // First Initial item
        setDBTime("2011-01-01");
        ItemA itemA1 = EntityFactory.create(ItemA.class);
        itemA1.testId().setValue(testId);
        itemA1.name().setValue("A1");

        final String origAName = "V1A1-" + uniqueString();
        itemA1.version().name().setValue(origAName);
        itemA1.version().testId().setValue(testId);

        //(Draft then finalize)
        itemA1.saveAction().setValue(SaveAction.saveAsDraft);
        srv.persist(itemA1);
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
        //(Draft then finalize)
        itemB1.saveAction().setValue(SaveAction.saveAsDraft);
        srv.persist(itemB1);
        itemB1.saveAction().setValue(SaveAction.saveAsFinal);
        srv.persist(itemB1);

        setDBTime("2011-03-01");
        // Update A1
        itemA1.version().name().setValue("V2A1-" + uniqueString());
        //(Draft then finalize)
        itemA1.saveAction().setValue(SaveAction.saveAsDraft);
        srv.persist(itemA1);
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

                srv.merge(itemB1r);

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

    public void testInconclusiveVersioningAppoach() {
        String testId = uniqueString();
        setDBTime("2010-01-01");

        final String v1Name = "V1-" + uniqueString();
        final String v1uName = "V1u-" + uniqueString();
        final String v2Name = "V2-" + uniqueString();

        // Initial item
        ItemAInconclusive itemA1 = EntityFactory.create(ItemAInconclusive.class);
        itemA1.testId().setValue(testId);
        itemA1.version().name().setValue(v1Name);
        itemA1.version().testId().setValue(testId);

        //Save
        srv.persist(itemA1);
        {
            ItemAInconclusive itemA1r = srv.retrieve(ItemAInconclusive.class, itemA1.getPrimaryKey().asCurrentKey());
            assertFalse("current is no null", itemA1r.version().isNull());
        }
        {
            ItemAInconclusive itemA1r = srv.retrieve(ItemAInconclusive.class, itemA1.getPrimaryKey().asDraftKey());
            assertTrue("draft is null", itemA1r.version().isNull());
        }

        // Update the same version
        itemA1.version().name().setValue(v1uName);
        srv.persist(itemA1);

        // Add new version
        ItemAInconclusive itemA2 = VersionedEntityUtils.createNextVersion(itemA1);
        itemA2.version().name().setValue(v2Name);
        srv.persist(itemA2);

        // Verify VersionData
        {
            EntityQueryCriteria<ItemAInconclusive.ItemAInconclusiveVersion> criteria = EntityQueryCriteria
                    .create(ItemAInconclusive.ItemAInconclusiveVersion.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            assertEquals("VersionData.size()", 2, srv.query(criteria).size());
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

        OwnedByVerOneToManyParent o = EntityFactory.create(OwnedByVerOneToManyParent.class);
        o.testId().setValue(testId);
        o.name().setValue(uniqueString());

        o.version().childrenM().add(EntityFactory.create(OwnedByVerOneToManyMChild.class));
        o.version().childrenM().add(EntityFactory.create(OwnedByVerOneToManyMChild.class));

        o.version().childrenM().get(0).testId().setValue(testId);
        o.version().childrenM().get(0).name().setValue(uniqueString());

        o.version().childrenM().get(1).testId().setValue(testId);
        String cNameM1 = "om" + uniqueString();
        o.version().childrenM().get(1).name().setValue(cNameM1);

        //
        o.version().childrenU().add(EntityFactory.create(OwnedByVerOneToManyUChild.class));
        o.version().childrenU().get(0).testId().setValue(testId);
        String cNameU1 = "ou" + uniqueString();
        o.version().childrenU().get(0).name().setValue(cNameU1);

        o.version().childE().childrenU().add(EntityFactory.create(OwnedByVerOneToManyUChild.class));
        o.version().childE().childrenU().get(0).testId().setValue(testId);
        String cNameEU1 = "oeu" + uniqueString();
        o.version().childE().childrenU().get(0).name().setValue(cNameEU1);

        // Save child and owner
        srvSave(o, testCaseMethod);

        // finalize, Save draft as version
        {
            OwnedByVerOneToManyParent o1r = srv.retrieve(OwnedByVerOneToManyParent.class, o.getPrimaryKey().asDraftKey());
            Assert.assertEquals(cNameM1, o1r.version().childrenM().get(1).name().getValue());

            o1r.saveAction().setValue(SaveAction.saveAsFinal);
            srv.persist(o1r);
        }

        // Update childM
        {
            OwnedByVerOneToManyParent o2 = srv.retrieve(OwnedByVerOneToManyParent.class, o.getPrimaryKey().asCurrentKey());
            o2.version().set(EntityGraph.businessDuplicate(o2.version()));
            VersionedEntityUtils.setAsDraft(o2.version());

            String cName2 = "uM" + uniqueString();
            o2.version().childrenM().get(1).name().setValue(cName2);

            // Save draft
            srvSave(o2, testCaseMethod);
            {
                OwnedByVerOneToManyParent o1r = srv.retrieve(OwnedByVerOneToManyParent.class, o.getPrimaryKey().asDraftKey());
                Assert.assertEquals(cName2, o1r.version().childrenM().get(1).name().getValue());
            }

            // Save final
            OwnedByVerOneToManyParent o2r = srv.retrieve(OwnedByVerOneToManyParent.class, o.getPrimaryKey().asDraftKey());
            o2r.saveAction().setValue(SaveAction.saveAsFinal);
            srv.persist(o2r);
            {
                OwnedByVerOneToManyParent o2r1 = srv.retrieve(OwnedByVerOneToManyParent.class, o.getPrimaryKey().asCurrentKey());
                Assert.assertEquals(cName2, o2r1.version().childrenM().get(1).name().getValue());
            }
        }

        // Update childU
        {
            OwnedByVerOneToManyParent o2 = srv.retrieve(OwnedByVerOneToManyParent.class, o.getPrimaryKey().asCurrentKey());
            o2.version().set(EntityGraph.businessDuplicate(o2.version()));
            VersionedEntityUtils.setAsDraft(o2.version());

            String cName2 = "uU" + uniqueString();
            o2.version().childrenU().get(0).name().setValue(cName2);

            // Save draft
            srvSave(o2, testCaseMethod);
            {
                OwnedByVerOneToManyParent o1r = srv.retrieve(OwnedByVerOneToManyParent.class, o.getPrimaryKey().asDraftKey());
                Assert.assertEquals(cName2, o1r.version().childrenU().get(0).name().getValue());
            }

            // Save final
            OwnedByVerOneToManyParent o2e = srv.retrieve(OwnedByVerOneToManyParent.class, o.getPrimaryKey().asDraftKey());
            o2e.saveAction().setValue(SaveAction.saveAsFinal);
            srvSave(o2e, testCaseMethod);
            {
                OwnedByVerOneToManyParent o2r1 = srv.retrieve(OwnedByVerOneToManyParent.class, o.getPrimaryKey().asCurrentKey());
                Assert.assertEquals(cName2, o2r1.version().childrenU().get(0).name().getValue());
            }
        }

        // Update childU.childU
        {
            OwnedByVerOneToManyParent o2 = srv.retrieve(OwnedByVerOneToManyParent.class, o.getPrimaryKey().asCurrentKey());
            o2.version().set(EntityGraph.businessDuplicate(o2.version()));
            VersionedEntityUtils.setAsDraft(o2.version());

            String cName2 = "uEU" + uniqueString();
            o2.version().childE().childrenU().get(0).name().setValue(cName2);

            // Save draft
            srvSave(o2, testCaseMethod);
            {
                OwnedByVerOneToManyParent o1r = srv.retrieve(OwnedByVerOneToManyParent.class, o.getPrimaryKey().asDraftKey());
                Assert.assertEquals(cName2, o1r.version().childE().childrenU().get(0).name().getValue());
            }

            // Save final
            OwnedByVerOneToManyParent o2e = srv.retrieve(OwnedByVerOneToManyParent.class, o.getPrimaryKey().asDraftKey());
            o2e.saveAction().setValue(SaveAction.saveAsFinal);
            srvSave(o2e, testCaseMethod);
            {
                OwnedByVerOneToManyParent o2r1 = srv.retrieve(OwnedByVerOneToManyParent.class, o.getPrimaryKey().asCurrentKey());
                Assert.assertEquals(cName2, o2r1.version().childE().childrenU().get(0).name().getValue());
            }
        }
    }

    public void testVersionQueryCriteria() {
        String testId = uniqueString();

        // Initial item without version
        ItemA itemA0 = EntityFactory.create(ItemA.class);
        itemA0.testId().setValue(testId);
        itemA0.name().setValue("A0-" + uniqueString());
        itemA0.saveAction().setValue(SaveAction.saveAsDraft);
        srv.persist(itemA0);

        // Initial item with final
        ItemA itemA1 = EntityFactory.create(ItemA.class);
        itemA1.testId().setValue(testId);
        itemA1.name().setValue("A1-" + uniqueString());
        itemA1.version().name().setValue("VA1-" + uniqueString());
        itemA1.version().testId().setValue(testId);
        //(Draft then finalize)
        itemA1.saveAction().setValue(SaveAction.saveAsDraft);
        srv.persist(itemA1);
        itemA1.saveAction().setValue(SaveAction.saveAsFinal);
        srv.persist(itemA1);

        // Initial item with draft
        ItemA itemA2 = EntityFactory.create(ItemA.class);
        itemA2.testId().setValue(testId);
        itemA2.name().setValue("A2-" + uniqueString());
        itemA2.version().name().setValue("VA2-" + uniqueString());
        itemA2.version().testId().setValue(testId);
        itemA2.saveAction().setValue(SaveAction.saveAsDraft);
        srv.persist(itemA2);

        // Initial item with final and draft
        ItemA itemA3 = EntityFactory.create(ItemA.class);
        itemA3.testId().setValue(testId);
        itemA3.name().setValue("A3-" + uniqueString());
        itemA3.version().name().setValue("VA3v-" + uniqueString());
        itemA3.version().testId().setValue(testId);
        //(Draft then finalize)
        itemA3.saveAction().setValue(SaveAction.saveAsDraft);
        srv.persist(itemA3);
        itemA3.saveAction().setValue(SaveAction.saveAsFinal);
        srv.persist(itemA3);

        itemA3.version().setPrimaryKey(null);
        itemA3.version().name().setValue("VA3d-" + uniqueString());
        itemA3.version().testId().setValue(testId);
        itemA3.saveAction().setValue(SaveAction.saveAsDraft);
        srv.persist(itemA3);

        // List/Find only Finalized
        {
            EntityQueryCriteria<ItemA> criteria = EntityQueryCriteria.create(ItemA.class);
            criteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            List<ItemA> list = srv.query(criteria);
            assertEquals("final data", 2, list.size());
        }

        // List/Find only Finalized by version() data
        {
            EntityQueryCriteria<ItemA> criteria = EntityQueryCriteria.create(ItemA.class);
            criteria.setVersionedCriteria(VersionedCriteria.onlyFinalized);
            criteria.add(PropertyCriterion.eq(criteria.proto().version().testId(), testId));
            List<ItemA> list = srv.query(criteria);
            assertEquals("final data", 2, list.size());
        }

        // List/Find Draft
        {
            EntityQueryCriteria<ItemA> criteria = EntityQueryCriteria.create(ItemA.class);
            criteria.setVersionedCriteria(VersionedCriteria.onlyDraft);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            List<ItemA> list = srv.query(criteria);
            assertEquals("draft data", 3, list.size());
        }

        // List/Find Draft, Ass sort by version date
        // Do not create LEFT JOIN , forced INNER JOIN
        {
            EntityQueryCriteria<ItemA> criteria = EntityQueryCriteria.create(ItemA.class);
            criteria.setVersionedCriteria(VersionedCriteria.onlyDraft);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.asc(criteria.proto().version().name());

            List<ItemA> list = srv.query(criteria);
            assertEquals("draft data", 3, list.size());
        }

        // List/Find Draft by version() data
        {
            EntityQueryCriteria<ItemA> criteria = EntityQueryCriteria.create(ItemA.class);
            criteria.setVersionedCriteria(VersionedCriteria.onlyDraft);
            criteria.add(PropertyCriterion.eq(criteria.proto().version().testId(), testId));
            List<ItemA> list = srv.query(criteria);
            assertEquals("final and draft data", 2, list.size());

            assertFalse("Has version data", list.get(0).version().isNull());
            assertEquals("(0)name", itemA2.name().getValue(), list.get(0).name().getValue());
            assertFalse("Has version data", list.get(1).version().isNull());
            assertEquals("(1)name", itemA3.name().getValue(), list.get(1).name().getValue());

            final ICursorIterator<ItemA> iterator = srv.query(null, criteria, AttachLevel.Attached);
            assertTrue("first", iterator.hasNext());
            ItemA r0 = iterator.next();
            assertEquals("(0)name", itemA2.name().getValue(), r0.name().getValue());
            assertTrue("second", iterator.hasNext());

            ItemA r1 = iterator.next();
            assertEquals("(0)name", itemA3.name().getValue(), r1.name().getValue());

            assertFalse("Only two items", iterator.hasNext());

        }
    }

    public void testDelete() {
        String testId = uniqueString();
        //srv.startTransaction();

        // Initial item
        OwnedByVerOneToManyParent o = EntityFactory.create(OwnedByVerOneToManyParent.class);
        o.testId().setValue(testId);
        o.version().name().setValue(uniqueString());
        o.version().testId().setValue(testId);

        o.version().childrenM().add(EntityFactory.create(OwnedByVerOneToManyMChild.class));
        o.version().childrenM().add(EntityFactory.create(OwnedByVerOneToManyMChild.class));
        o.version().childrenM().get(0).testId().setValue(testId);
        o.version().childrenM().get(0).name().setValue(uniqueString());
        o.version().childrenM().get(1).testId().setValue(testId);

        //(Draft then finalize)
        srv.persist(o);
        //Save initial value
        o.saveAction().setValue(SaveAction.saveAsFinal);
        srv.persist(o);
        {
            OwnedByVerOneToManyParent itemA1r = srv.retrieve(OwnedByVerOneToManyParent.class, o.getPrimaryKey().asDraftKey());
            itemA1r.version().name().setValue(uniqueString());
            itemA1r.version().testId().setValue(testId);
            srv.persist(itemA1r);
        }

        //DO delete
        {
            EntityQueryCriteria<OwnedByVerOneToManyParent> criteria = EntityQueryCriteria.create(OwnedByVerOneToManyParent.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            assertEquals("removed", 1, srv.delete(criteria));
            assertEquals("remain", 0, srv.count(criteria));
        }

        {
            EntityQueryCriteria<OwnedByVerOneToManyMChild> criteriaChildren = EntityQueryCriteria.create(OwnedByVerOneToManyMChild.class);
            criteriaChildren.add(PropertyCriterion.eq(criteriaChildren.proto().testId(), testId));
            assertEquals("ChildrenRemain", 0, srv.count(criteriaChildren));
        }
    }

    public void testPolymorphicVersioned() {
        String testId = uniqueString();

        final String commonNameA = "Ca-" + uniqueString();
        final String commonNameB = "Cb-" + uniqueString();

        final String currentName = "V0-" + uniqueString();
        final String currentNameA = "Va0-" + uniqueString();
        final String currentNameB = "Vb0-" + uniqueString();

        // Initial item
        PolymorphicVersionedA itemA1 = EntityFactory.create(PolymorphicVersionedA.class);
        {
            itemA1.testId().setValue(testId);
            itemA1.name().setValue(commonNameA);

            itemA1.version().name().setValue(currentName);
            itemA1.version().testId().setValue(testId);
            itemA1.version().dataAv().setValue(currentNameA);

            //Save initial value (Draft then finalize)
            itemA1.saveAction().setValue(SaveAction.saveAsDraft);
            srv.persist(itemA1);
            itemA1.saveAction().setValue(SaveAction.saveAsFinal);
            srv.persist(itemA1);
        }

        PolymorphicVersionedB itemB1 = EntityFactory.create(PolymorphicVersionedB.class);
        {
            itemB1.testId().setValue(testId);
            itemB1.name().setValue(commonNameB);

            itemB1.version().name().setValue(currentName);
            itemB1.version().testId().setValue(testId);
            itemB1.version().dataBv().setValue(currentNameB);

            //Save initial value (Draft then finalize)
            itemB1.saveAction().setValue(SaveAction.saveAsDraft);
            srv.persist(itemB1);
            itemB1.saveAction().setValue(SaveAction.saveAsFinal);
            srv.persist(itemB1);
        }

        // Test Query Super by Owner
        {
            @SuppressWarnings("rawtypes")
            EntityQueryCriteria<PolymorphicVersionedSuper.PolymorphicVersionDataSuper> criteria = EntityQueryCriteria
                    .create(PolymorphicVersionedSuper.PolymorphicVersionDataSuper.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().holder(), itemB1));
            @SuppressWarnings("rawtypes")
            List<PolymorphicVersionedSuper.PolymorphicVersionDataSuper> found = srv.query(criteria);
            Assert.assertEquals("retrieved size", 1, found.size());
        }

        // Test Query leaf by Owner Key
        {
            EntityQueryCriteria<PolymorphicVersionedB.PolymorphicVersionDataB> criteria = EntityQueryCriteria
                    .create(PolymorphicVersionedB.PolymorphicVersionDataB.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().holder(), itemB1.getPrimaryKey()));
            List<PolymorphicVersionedB.PolymorphicVersionDataB> found = srv.query(criteria);
            Assert.assertEquals("retrieved size", 1, found.size());
        }

        // Test Query by Super
        {
            EntityQueryCriteria<PolymorphicVersionedSuper> criteria = EntityQueryCriteria.create(PolymorphicVersionedSuper.class);
            criteria.add(PropertyCriterion.eq(criteria.proto().testId(), testId));
            criteria.add(PropertyCriterion.eq(criteria.proto().name(), commonNameA));
            List<PolymorphicVersionedSuper> found = srv.query(criteria);
            Assert.assertEquals("retrieved size", 1, found.size());
        }
    }
}
