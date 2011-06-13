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
package com.pyx4j.unit.server.mock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import com.pyx4j.rpc.shared.RemoteService;
import com.pyx4j.security.shared.Behavior;
import com.pyx4j.security.shared.UserVisit;
import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Lifecycle;
import com.pyx4j.server.contexts.Visit;

public class TestLifecycle {

    static class TestContext {

        MockHttpSession session;

        UserVisit userVisit;

        Set<Behavior> behaviours;

    }

    static final ThreadLocal<TestContext> threadLocalContext = new ThreadLocal<TestContext>() {
        @Override
        protected TestContext initialValue() {
            return new TestContext();
        }
    };

    public static void testSession(UserVisit userVisit, Behavior... behaviours) {
        TestContext testContext = threadLocalContext.get();
        testContext.session = null;
        testContext.userVisit = userVisit;
        testContext.behaviours = new HashSet<Behavior>(Arrays.asList(behaviours));
    }

    public static void beginRequest() {
        MockHttpServletRequest httprequest = new MockHttpServletRequest();
        HttpServletResponse httpresponse = new MockHttpServletResponse();
        Lifecycle.beginRequest(httprequest, httpresponse);

        // Auto init test sessions
        TestContext testContext = threadLocalContext.get();
        if ((testContext.session == null) && ((testContext.userVisit != null) || (testContext.behaviours != null))) {
            Lifecycle.beginSession(testContext.userVisit, testContext.behaviours);
        }
        //TODO create Client side Context
        Visit visit = Context.getVisit();
        if (visit != null) {
            httprequest.setHeader(RemoteService.SESSION_TOKEN_HEADER, visit.getSessionToken());
        }
    }

    public static void endRequest() {
        Lifecycle.endRequest();
    }

    public static void tearDown() {
        if (Context.getRequest() != null) {
            Lifecycle.endRequest();
        }
        threadLocalContext.remove();
    }

}
