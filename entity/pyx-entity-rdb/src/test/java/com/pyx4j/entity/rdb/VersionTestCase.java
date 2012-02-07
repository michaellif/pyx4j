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

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.shared.domain.version.ItemA;
import com.pyx4j.entity.test.shared.domain.version.ItemAVersion;
import com.pyx4j.entity.test.shared.domain.version.RefToCurrent;
import com.pyx4j.entity.test.shared.domain.version.RefToVersioned;

public abstract class VersionTestCase extends DatastoreTestBase {

    public void testVersionPrototype() {
        String testId = uniqueString();

        // Initial item
        ItemA itemA1 = EntityFactory.create(ItemA.class);
        srv.persist(itemA1);

        final String origName = "V1" + uniqueString();
        itemA1.current().version().setValue(1);
        itemA1.current().name().setValue(origName);
        itemA1.current().testId().setValue(testId);
        srv.persist(itemA1.current());

        srv.persist(itemA1);

        RefToCurrent refToCurrent1 = EntityFactory.create(RefToCurrent.class);
        refToCurrent1.name().setValue(uniqueString());
        refToCurrent1.testId().setValue(testId);
        refToCurrent1.itemA().set(itemA1);
        srv.persist(refToCurrent1);

        RefToVersioned refToVersioned1 = EntityFactory.create(RefToVersioned.class);
        refToVersioned1.name().setValue(uniqueString());
        refToVersioned1.testId().setValue(testId);
        refToVersioned1.itemA().set(itemA1.current());
        srv.persist(refToVersioned1);

        // Make new version
        makeNewVersion(itemA1);
        final String newName = "V2" + uniqueString();
        itemA1.current().name().setValue(newName);
        srv.persist(itemA1.current());

        // Retrieve original reference
        RefToCurrent refToCurrent1r1 = srv.retrieve(RefToCurrent.class, refToCurrent1.getPrimaryKey());
        assertEquals("ref updated", newName, refToCurrent1r1.itemA().current().name().getValue());

        // Retrieve new version
        RefToVersioned refToVersioned1r1 = srv.retrieve(RefToVersioned.class, refToVersioned1.getPrimaryKey());
        assertEquals("ref not updated", origName, refToVersioned1r1.itemA().name().getValue());
    }

    private void makeNewVersion(ItemA itemA1) {
        ItemAVersion newVersion = itemA1.current().duplicate();
        newVersion.setPrimaryKey(null);
        newVersion.version().setValue(itemA1.current().version().getValue() + 1);
        srv.persist(newVersion);

        itemA1.current().set(newVersion);
        srv.persist(itemA1);

    }
}
