/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Jul 21, 2014
 * @author vlads
 */
package com.pyx4j.security.test.server;

import junit.framework.TestCase;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.entity.security.InstanceAccess;
import com.pyx4j.security.server.ReflectionEnabledAclBuilder;
import com.pyx4j.security.shared.AclCreator;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.security.test.shared.domain.SecureEntity1;
import com.pyx4j.unit.server.mock.TestLifecycle;

public class AccessControlContextTest extends TestCase {

    @SuppressWarnings("serial")
    static class SecureEntity1InstanceAccess implements InstanceAccess {

        @Override
        public boolean implies(IEntity contextEntity) {
            return ((SecureEntity1) contextEntity).name().getValue().contains("allow");
        }

    }

    static class AccessControlList1 extends ReflectionEnabledAclBuilder {

        AccessControlList1() {
            grant(new EntityPermission(SecureEntity1.class, EntityPermission.READ));
            grant(new EntityPermission(SecureEntity1.class, new SecureEntity1InstanceAccess(), EntityPermission.UPDATE));
            freeze();
        }

    }

    static class ServerSideConfiguration1 extends ServerSideConfiguration {
        @Override
        public AclCreator getAclCreator() {
            return new AccessControlList1();
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ServerSideConfiguration.setInstance(null);
    }

    @Override
    protected void tearDown() throws Exception {
        TestLifecycle.tearDown();
        ServerSideConfiguration.setInstance(null);
    }

    public void testAccess() {
        ServerSideConfiguration.setInstance(new ServerSideConfiguration1());

        {
            SecureEntity1 ent1 = EntityFactory.create(SecureEntity1.class);
            ent1.name().setValue("other");

            assertTrue(SecurityController.check(ent1, EntityPermission.permissionRead(SecureEntity1.class)));
            assertFalse(SecurityController.check(ent1, EntityPermission.permissionUpdate(SecureEntity1.class)));
        }

        assertTrue(SecurityController.check(EntityPermission.permissionUpdate(SecureEntity1.class)));

        {
            SecureEntity1 ent2 = EntityFactory.create(SecureEntity1.class);
            ent2.name().setValue("allow");

            assertTrue(SecurityController.check(ent2, EntityPermission.permissionRead(SecureEntity1.class)));
            assertTrue(SecurityController.check(ent2, EntityPermission.permissionUpdate(SecureEntity1.class)));
        }
    }

}
