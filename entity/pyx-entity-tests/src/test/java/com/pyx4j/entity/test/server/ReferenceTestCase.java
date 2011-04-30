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
 * Created on Oct 2, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.test.server;

import junit.framework.Assert;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.test.shared.domain.Address;
import com.pyx4j.entity.test.shared.domain.Province;

public abstract class ReferenceTestCase extends DatastoreTestBase {

    public void testPersist() {
        Address address = EntityFactory.create(Address.class);

        Province prov = EntityFactory.create(Province.class);
        prov.name().setValue("Ontario" + uniqueString());
        srv.persist(prov);

        address.province().set(prov);

        srv.persist(address);

        Address address2 = srv.retrieve(Address.class, address.getPrimaryKey());
        Assert.assertNotNull("retrieve", address2);

        Assert.assertEquals("address.province Value", prov.name().getValue(), address2.province().name().getValue());
        Assert.assertEquals("address.province Pk", prov.getPrimaryKey(), address2.province().getPrimaryKey());
    }

    public void testPersistCreate() {
        Address address = EntityFactory.create(Address.class);

        Province prov = EntityFactory.create(Province.class);
        prov.name().setValue("Ontario" + uniqueString());
        //Don't save use @Reference feature
        //srv.persist(prov);

        address.province().set(prov);

        srv.persist(address);

        Address address2 = srv.retrieve(Address.class, address.getPrimaryKey());
        Assert.assertNotNull("retrieve by PK " + address.getPrimaryKey(), address2);

        Assert.assertEquals("address.province Value", prov.name().getValue(), address2.province().name().getValue());
        Assert.assertNotNull("address.province Pk", address2.province().getPrimaryKey());
    }

    public void testPersistMerge() {
        Province prov0 = EntityFactory.create(Province.class);
        prov0.name().setValue("Ontario" + uniqueString());
        srv.persist(prov0);

        Address address = EntityFactory.create(Address.class);

        Province prov = EntityFactory.create(Province.class);
        prov.name().setValue(prov0.name().getValue());
        //Don't save use @Reference feature
        //srv.persist(prov);

        address.province().set(prov);

        srv.persist(address);

        Address address2 = srv.retrieve(Address.class, address.getPrimaryKey());
        Assert.assertNotNull("retrieve", address2);

        Assert.assertEquals("address.province Value", prov0.name().getValue(), address2.province().name().getValue());
        Assert.assertEquals("address.province Pk", prov0.getPrimaryKey(), address2.province().getPrimaryKey());
    }

    public void testPersistCreateWithWrongKeys() {

        Province prov = EntityFactory.create(Province.class);
        prov.name().setValue("Ontario" + uniqueString());
        prov.setPrimaryKey(new Long(1234567890));

        boolean saved = false;
        try {
            srv.merge(prov);
            saved = true;
        } catch (Throwable e) {
        }

        if (saved) {
            fail("Managed to update entity with incorrect PK property");
        }

    }

}
