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
 * Created on Apr 2, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.unit.test;

import junit.framework.TestCase;

import com.pyx4j.security.shared.CoreBehavior;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.unit.server.mock.TestLifecycle;

public class ContextTest extends TestCase {

    @Override
    protected void tearDown() throws Exception {
        TestLifecycle.tearDown();
    }

    public void testSession() {
        TestLifecycle.beginRequest();

        assertNull("No Session", Context.getSession());
        assertNull("No Visit", Context.getVisit());
        Lifecycle.beginAnonymousSession();
        assertNotNull("Has Session", Context.getSession());
        assertNotNull("Has Visit", Context.getVisit());
        TestLifecycle.endRequest();

        assertNull("No Session", Context.getSession());

        TestLifecycle.beginRequest();
        assertNotNull("Has Session in request 2", Context.getSession());
        assertNotNull("Has Visit in request 2", Context.getVisit());

    }

    public void testLifecycle() {
        TestLifecycle.testSession(new UserVisit(), CoreBehavior.USER);

        assertNull("No Visit", Context.getVisit());

        TestLifecycle.beginRequest();
        try {
            assertTrue("Does not have Behavior.USER", SecurityController.checkBehavior(CoreBehavior.USER));
            assertTrue("Has Behavior.DEVELOPER", !SecurityController.checkBehavior(CoreBehavior.DEVELOPER));
        } finally {
            TestLifecycle.endRequest();
        }

    }
}
