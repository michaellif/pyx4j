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
 * Created on Oct 4, 2013
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.test.server;

import junit.framework.TestCase;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.security.server.ReflectionEnabledAclBuilder;
import com.pyx4j.security.shared.AclCreator;
import com.pyx4j.security.shared.CoreBehavior;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.unit.server.mock.TestLifecycle;

public class UnitTestConfigurationChangeTest extends TestCase {

    static class AccessControlList1 extends ReflectionEnabledAclBuilder {

        AccessControlList1() {
            grant(new EntityPermission("EntP1", "*"));
            grant(CoreBehavior.DEVELOPER, new EntityPermission("EntD1", "*"));
            freeze();
        }

    }

    static class AccessControlList2 extends ReflectionEnabledAclBuilder {

        AccessControlList2() {
            grant(new EntityPermission("EntP2", "*"));
            grant(CoreBehavior.DEVELOPER, new EntityPermission("EntD2", "*"));
            freeze();
        }

    }

    static class ServerSideConfiguration1 extends ServerSideConfiguration {
        @Override
        public AclCreator getAclCreator() {
            System.out.println("111");
            return new AccessControlList1();
        }
    }

    static class ServerSideConfiguration2 extends ServerSideConfiguration {
        @Override
        public AclCreator getAclCreator() {
            System.out.println("222");
            return new AccessControlList2();
        }
    }

    @Override
    protected void tearDown() throws Exception {
        TestLifecycle.tearDown();
    }

    /**
     * Target to allow different tests to have different configurations.
     * SecurityController should be reset on configuration change.
     */
    public void testUnitTestConfigurationChange() {
        // initialize default configuration
        assertTrue(SecurityController.checkPermission(new EntityPermission("EntP1", EntityPermission.READ)));
        assertTrue(SecurityController.checkPermission(new EntityPermission("EntD1", EntityPermission.READ)));

        ServerSideConfiguration.setInstance(new ServerSideConfiguration1());

        assertTrue(SecurityController.checkPermission(new EntityPermission("EntP1", EntityPermission.READ)));
        assertFalse(SecurityController.checkPermission(new EntityPermission("EntD1", EntityPermission.READ)));
    }
}
