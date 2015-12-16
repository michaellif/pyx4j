/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Dec 16, 2015
 * @author vlads
 */
package com.pyx4j.security.test.server;

import org.junit.Assert;
import org.junit.Test;

import com.pyx4j.commons.Consts;
import com.pyx4j.security.server.ThrottleConfig;
import com.pyx4j.server.contexts.AntiDoS;
import com.pyx4j.server.contexts.AntiDoS.AccessCounter;
import com.pyx4j.unit.server.mock.MockHttpServletRequest;

public class AntiDoSTest {

    @Test
    public void testWarmUpRequests() {
        ThrottleConfig config = new ThrottleConfig() {

            @Override
            public int getSystemWarmUpRequestsCount() {
                return 100;
            }

            @Override
            public long getInterval() {
                return 1 * Consts.MIN2MSEC;
            }

            @Override
            public long getMaxTimeUsage() {
                return 2 * Consts.MIN2MSEC;
            }

            @Override
            public long getMaxRequests() {
                return 50;
            }
        };

        AntiDoS antiDoS = new AntiDoS(config);
        long requestStart = System.currentTimeMillis();
        MockHttpServletRequest request = new MockHttpServletRequest("http://test.com");

        for (int i = 0; i < 1100 + 50; i++) {
            AccessCounter counter = antiDoS.beginRequest(request, requestStart + 1);
            Assert.assertNotNull(counter);
            antiDoS.endRequest(counter, requestStart);
        }
        {
            AccessCounter counter = antiDoS.beginRequest(request, requestStart + 1);
            Assert.assertNull("Request Should be Blocked", counter);
        }
    }
}
