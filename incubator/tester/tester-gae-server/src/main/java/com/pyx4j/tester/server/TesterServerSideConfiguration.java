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
 * Created on 2010-09-16
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.tester.server;

import com.pyx4j.commons.Consts;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.config.server.rpc.IServiceFactory;
import com.pyx4j.security.server.ThrottleConfig;

public class TesterServerSideConfiguration extends ServerSideConfiguration {

    @Override
    public IServiceFactory getRPCServiceFactory() {
        return new TesterRPCServiceFactory();
    }

    @Override
    public ThrottleConfig getThrottleConfig() {
        return new ThrottleConfig() {
            @Override
            public long getInterval() {
                return 30 * Consts.SEC2MSEC;
            }

            @Override
            public long getMaxRequests() {
                return 60000;
            }
        };
    }
}
