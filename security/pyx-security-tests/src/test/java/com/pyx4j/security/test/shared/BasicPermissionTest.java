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
 * @version $Id$
 */
package com.pyx4j.security.test.shared;

import junit.framework.TestCase;

import com.pyx4j.security.shared.BasicPermission;

public class BasicPermissionTest extends TestCase {

    @SuppressWarnings("serial")
    public void testImplies() {
        BasicPermission b1 = new BasicPermission("a.b.c");
        assertTrue(b1.implies(b1));
        assertTrue(b1.implies(new BasicPermission("a.b.c")));
        assertFalse(b1.implies(new BasicPermission("a.b.c.*")));
        assertFalse(b1.implies(new BasicPermission("a.b.c") {
        }));
        assertTrue(new BasicPermission("a.b.*").implies(b1));
        assertTrue(new BasicPermission("a.*").implies(b1));
        assertTrue(new BasicPermission("*").implies(b1));
        assertTrue(new BasicPermission("a.b*").implies(b1));
        assertFalse(new BasicPermission("a.b.c.*").implies(b1));
        assertTrue(new BasicPermission("1.*").implies(new BasicPermission("1.234.*")));
        assertTrue(new BasicPermission("*").implies(new BasicPermission("*")));
    }

}
