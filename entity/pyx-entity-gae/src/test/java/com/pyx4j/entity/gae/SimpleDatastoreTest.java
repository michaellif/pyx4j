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
 * Created on Dec 24, 2009
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.gae;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.pyx4j.entity.test.server.DatastoreTestBase;
import com.pyx4j.entity.test.server.PersistenceEnvironment;

public class SimpleDatastoreTest extends DatastoreTestBase {

    @Override
    protected PersistenceEnvironment getPersistenceEnvironment() {
        return GAEPersistenceEnvironmentFactory.getPersistenceEnvironment();
    }

    public void testSinglePutSuccess() throws EntityNotFoundException {

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Key key = KeyFactory.createKey("Kind1", "Name1");

        Entity entityToPut = new Entity(key);
        entityToPut.setProperty("Property1", "Property1Value");

        Key keyFromPut = datastore.put(entityToPut);

        assertEquals("Keys should be equal", key, keyFromPut);

        Entity entityToGet = datastore.get(keyFromPut);

        assertEquals("Entities should be equal", entityToPut, entityToGet);
    }

}
