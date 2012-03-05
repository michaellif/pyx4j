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

import com.pyx4j.entity.shared.AttachLevel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.version.ItemA;
import com.pyx4j.entity.test.shared.domain.version.RefToCurrent;
import com.pyx4j.entity.test.shared.domain.version.RefToVersioned;
import com.pyx4j.gwt.server.DateUtils;

public abstract class VersionTestCase extends DatastoreTestBase {

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

        // Retrieval of item as draft
        {
            ItemA itemA1r = EntityFactory.create(ItemA.class);
            itemA1r.setPrimaryKey(itemA1.getPrimaryKey());
            itemA1r.draft().setValue(Boolean.TRUE);

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
            ItemA itemA1r = EntityFactory.create(ItemA.class);
            itemA1r.setPrimaryKey(itemA1.getPrimaryKey());
            itemA1r.forDate().setValue(DateUtils.detectDateformat("2011-01-11"));

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
        // TODO fix this in framework
        itemA1.versions().setAttachLevel(AttachLevel.Detached);

        final String currentName = "V0-" + uniqueString();
        itemA1.version().fromDate().setValue(DateUtils.detectDateformat("2011-01-01"));
        itemA1.version().name().setValue(currentName);
        itemA1.version().testId().setValue(testId);

        //Save initial value
        srv.persist(itemA1);

        // verify all Versions
        {
            ItemA itemA1r = EntityFactory.create(ItemA.class);
            itemA1r.setPrimaryKey(itemA1.getPrimaryKey());
            srv.retrieve(itemA1r);
            srv.retrieveMember(itemA1r.versions());
            assertEquals("Two versions created", 2, itemA1r.versions().size());
        }

        // verify Draft created
        {
            ItemA itemA1r = EntityFactory.create(ItemA.class);
            itemA1r.setPrimaryKey(itemA1.getPrimaryKey());
            itemA1r.draft().setValue(Boolean.TRUE);

            srv.retrieve(itemA1r);
            assertTrue("version is not null", !itemA1r.version().isNull());
            assertEquals("getDraft", currentName, itemA1r.version().name().getValue());
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
        srv.persist(itemA1);

        // verify all Versions
        {
            ItemA itemA1r = EntityFactory.create(ItemA.class);
            itemA1r.setPrimaryKey(itemA1.getPrimaryKey());
            srv.retrieve(itemA1r);
            srv.retrieveMember(itemA1r.versions());
            assertEquals("Number of versions created", 3, itemA1r.versions().size());
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
        itemA1draft.setPrimaryKey(itemA1.getPrimaryKey());
        itemA1draft.draft().setValue(Boolean.TRUE);

        srv.retrieve(itemA1draft);

        final String draftName = "V1-" + uniqueString();
        itemA1draft.version().name().setValue(draftName);
        itemA1draft.version().testId().setValue(testId);

        //Save Draft
        srv.persist(itemA1draft);

        // Retrieval of item as draft
        {
            ItemA itemA1r = EntityFactory.create(ItemA.class);
            itemA1r.setPrimaryKey(itemA1.getPrimaryKey());
            itemA1r.draft().setValue(Boolean.TRUE);

            srv.retrieve(itemA1r);
            assertTrue("version is not null", !itemA1r.version().isNull());
            assertEquals("getDraft", draftName, itemA1r.version().name().getValue());
        }

        // finalize, Save draft as version
        ItemA itemA1f = EntityFactory.create(ItemA.class);
        itemA1f.setPrimaryKey(itemA1.getPrimaryKey());
        itemA1f.draft().setValue(Boolean.TRUE);

        srv.retrieve(itemA1f);

        final String draftV2Name = "V2-" + uniqueString();
        itemA1f.version().name().setValue(draftV2Name);

        setDBTime("2010-02-01");

        // finalize
        itemA1f.draft().setValue(Boolean.FALSE);
        srv.persist(itemA1f);
        srv.commit();

        // verify Draft updated
        {
            ItemA itemA1r = EntityFactory.create(ItemA.class);
            itemA1r.setPrimaryKey(itemA1.getPrimaryKey());
            itemA1r.draft().setValue(Boolean.TRUE);

            srv.retrieve(itemA1r);
            assertTrue("version is not null", !itemA1r.version().isNull());
            assertEquals("getDraft", draftV2Name, itemA1r.version().name().getValue());
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

        // TODO fix this in framework
        itemA1.versions().setAttachLevel(AttachLevel.Detached);

        final String origName = "V1-" + uniqueString();
        itemA1.version().name().setValue(origName);
        itemA1.version().testId().setValue(testId);

        // Finalize as of (current) date
        setDBTime("2011-01-01");
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
        srv.persist(itemA1);

        //Save another Draft
        final String draftName = "D1-" + uniqueString();
        itemA1.draft().setValue(Boolean.TRUE);
        itemA1.version().name().setValue(draftName);
        srv.persist(itemA1);

        // Verify values, Current has the value of last  finalize
        {
            RefToCurrent refToCurrent1r1 = srv.retrieve(RefToCurrent.class, refToCurrent1.getPrimaryKey());
            assertEquals("ref updated", newName, refToCurrent1r1.itemA().version().name().getValue());
            assertEquals("date as of now", DateUtils.detectDateformat("2011-02-01"), refToCurrent1r1.itemA().forDate().getValue());
        }

        // Versioned has value of the time when it was saved
        {
            // Retrieve new version
            RefToVersioned refToVersioned1r1 = srv.retrieve(RefToVersioned.class, refToVersioned1.getPrimaryKey());
            assertEquals("ref not updated", origName, refToVersioned1r1.itemA().version().name().getValue());
            assertEquals("date as of time of save", DateUtils.detectDateformat("2011-01-15"), refToVersioned1r1.itemA().forDate().getValue());
        }

        // Retrieval of item itself, by default returns current
        {
            ItemA itemA1r = srv.retrieve(ItemA.class, itemA1.getPrimaryKey());
            assertEquals("current", newName, itemA1r.version().name().getValue());
        }

    }

    public void TODO_testVersionedGraph() {
    }

}
