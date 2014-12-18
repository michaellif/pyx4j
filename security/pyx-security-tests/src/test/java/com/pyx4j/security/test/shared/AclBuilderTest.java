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
 * Created on Jan 13, 2010
 * @author vlads
 */
package com.pyx4j.security.test.shared;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.security.EntityPermission;
import com.pyx4j.rpc.shared.ServiceExecutePermission;
import com.pyx4j.security.shared.Acl;
import com.pyx4j.security.shared.AclBuilder;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.PermissionAntipode;

public class AclBuilderTest extends TestCase {

    private static final Logger log = LoggerFactory.getLogger(AclBuilderTest.class);

    static enum TestBehavior implements Behavior {

        EMPLOYEE,

        MANAGER,

        ADMIN;

    }

    class AccessControlList extends AclBuilder {

        public AccessControlList() {
            grant(new EntityPermission("Public", "*"));
            grant(new ServiceExecutePermission("Read.*"));

            grant(TestBehavior.ADMIN, new EntityPermission("*", "*"));
            grant(TestBehavior.ADMIN, new ServiceExecutePermission("*"));

            grant(TestBehavior.EMPLOYEE, new ServiceExecutePermission("Emps.*"));
            revoke(TestBehavior.EMPLOYEE, new PermissionAntipode(new ServiceExecutePermission("Emps.Reports.*")));
            freeze();
        }
    }

    public void testAccessControlPublic() {
        Acl acl = new AccessControlList().createAcl(null);
        assertTrue(acl.checkPermission(null, new EntityPermission("Public", EntityPermission.READ)));
        assertFalse(acl.checkPermission(null, new EntityPermission("Private", EntityPermission.READ)));

        assertNotNull(acl.getBehaviours());
        assertEquals(0, acl.getBehaviours().size());

        assertTrue(acl.checkPermission(null, new ServiceExecutePermission("Read.Me")));
        assertFalse(acl.checkPermission(null, new ServiceExecutePermission("Write.Me")));

        assertFalse(acl.checkPermission(null, new UndefinedPermission()));
    }

    public void testAccessControlEmployee() {
        Set<Behavior> roles = new HashSet<Behavior>();
        roles.add(TestBehavior.EMPLOYEE);

        Acl acl = new AccessControlList().createAcl(roles);
        log.debug("Emp ACL {}", acl);
        assertNotNull(acl.getBehaviours());
        assertEquals(1, acl.getBehaviours().size());

        assertTrue(acl.checkPermission(null, new EntityPermission("Public", EntityPermission.READ)));
        assertFalse(acl.checkPermission(null, new EntityPermission("Private", EntityPermission.READ)));

        assertTrue(acl.checkPermission(null, new ServiceExecutePermission("Read.Me")));
        assertFalse(acl.checkPermission(null, new ServiceExecutePermission("Write.Me")));

        assertTrue(acl.checkPermission(null, new ServiceExecutePermission("Emps.Do")));
        assertFalse(acl.checkPermission(null, new ServiceExecutePermission("Admin.Do")));
        assertFalse(acl.checkPermission(null, new ServiceExecutePermission("Emps.Reports.Do")));

        assertFalse(acl.checkPermission(null, new UndefinedPermission()));
    }
}
