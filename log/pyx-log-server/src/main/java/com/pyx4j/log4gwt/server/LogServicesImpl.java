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
 * Created on Jan 9, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.log4gwt.server;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.log4gwt.rpc.LogServices;
import com.pyx4j.log4gwt.shared.LogEvent;
import com.pyx4j.rpc.shared.IsIgnoreSessionTokenService;
import com.pyx4j.rpc.shared.VoidSerializable;

public abstract class LogServicesImpl {

    public static final String CLIENT_LOGGER_NAME = "client";

    private static final Logger log = LoggerFactory.getLogger(CLIENT_LOGGER_NAME);

    public static class LogImpl implements LogServices.Log, IsIgnoreSessionTokenService {

        @Override
        public VoidSerializable execute(Vector<LogEvent> request) {
            boolean useLog4j = (log.getClass().getName().contains("Log4j"));
            if (useLog4j) {
                for (LogEvent event : request) {
                    ClientLog4j.log(event);
                }
            } else {
                for (LogEvent event : request) {
                    ClientLog.log(event);
                }
            }
            return null;
        }

    }
}
