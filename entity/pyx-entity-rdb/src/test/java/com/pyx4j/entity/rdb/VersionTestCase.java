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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IVersionedEntity;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.version.ItemA;
import com.pyx4j.entity.test.shared.domain.version.RefToCurrent;
import com.pyx4j.entity.test.shared.domain.version.RefToVersioned;
import com.pyx4j.gwt.server.DateUtils;

public abstract class VersionTestCase extends DatastoreTestBase {

    /**
     * Emulate Database current time
     */
    private void setDBTime(String dateStr) {
        EntityPersistenceServiceRDB service = ((EntityPersistenceServiceRDB) srv);
        service.setTimeNow(DateUtils.detectDateformat(dateStr));
    }

    public void testSaveDraft() {
        String testId = uniqueString();
        if (true) {
            // return;
        }

        // Initial item
        ItemA itemA1 = EntityFactory.create(ItemA.class);

        final String origName = "V1" + uniqueString();
        itemA1.version().name().setValue(origName);
        itemA1.version().testId().setValue(testId);

        //Save Draft
        srv.persist(itemA1);

        // Retrieval of item itself, by default returns current
        {
            ItemA itemA1r = srv.retrieve(ItemA.class, itemA1.getPrimaryKey());
            assertTrue("current is null", itemA1r.version().isNull());
        }
    }

    private void srv_finalize(IVersionedEntity<?> entity) {
        // TODO create function in  PersistenceService
    }

    public <T extends IVersionedEntity<?>> T srv_retrieveDraft(Class<T> entityClass, Key primaryKey) {
        // TODO create function in  PersistenceService
        return null;
    }

//    TODO in future
//    public <T extends IVersionedEntity<?>> T srv_retrieveVersion(IVersionData versionInfo) {
//        // TODO create function in  PersistenceService
//        return null;
//    }

    public void testVersionedSingleEntity() {
        String testId = uniqueString();
        if (true) {
            return;
        }

        // Initial item
        ItemA itemA1 = EntityFactory.create(ItemA.class);

        final String origName = "V1" + uniqueString();
        itemA1.version().name().setValue(origName);
        itemA1.version().testId().setValue(testId);

        //Save Draft
        srv.persist(itemA1);

        // Finalize as of (current) date
        setDBTime("2011-01-01");
        srv_finalize(itemA1);

        setDBTime("2011-01-02");

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
        final String newName = "V2" + uniqueString();
        itemA1.version().name().setValue(newName);
        srv_finalize(itemA1);

        //Save another Draft
        final String draftName = "D1" + uniqueString();
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
            assertEquals("ref not updated", origName, refToVersioned1r1.itemA().name().getValue());
            assertEquals("date as of time of save", DateUtils.detectDateformat("2011-01-02"), refToVersioned1r1.itemA().forDate().getValue());
        }

        // Retrieval of item itself, by default returns current
        {
            ItemA itemA1r = srv.retrieve(ItemA.class, itemA1.getPrimaryKey());
            assertEquals("current", newName, itemA1r.version().name().getValue());
        }

        // Access to draft version for editing
        {
            ItemA itemA1r = srv_retrieveDraft(ItemA.class, itemA1.getPrimaryKey());
            assertEquals("draft", draftName, itemA1r.version().name().getValue());
        }
    }

    public void TODO_testVersionedGraph() {
    }

}
