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
 * Created on Jan 10, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.log4gwt.server;

import com.pyx4j.log4gwt.shared.LogEvent;

/**
 * Redirect log events org.apache.log4j
 */
class ClientLog4j {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LogServicesImpl.CLIENT_LOGGER_NAME);

    private static org.apache.log4j.Level toLog4jLevel(com.pyx4j.log4gwt.shared.Level level) {
        switch (level) {
        case ERROR:
            return org.apache.log4j.Level.ERROR;
        case WARN:
            return org.apache.log4j.Level.WARN;
        case INFO:
            return org.apache.log4j.Level.INFO;
        case DEBUG:
            return org.apache.log4j.Level.DEBUG;
        case TRACE:
            return org.apache.log4j.Level.TRACE;
        default:
            return org.apache.log4j.Level.TRACE;
        }
    }

    static void log(LogEvent event) {
        log.callAppenders(new org.apache.log4j.spi.LoggingEvent(log.getName(), log, event.getEventTime(), toLog4jLevel(event.getLevel()), event
                .getFormatedMessageWithThrowable(), event.getThrowable()));
    }
}
