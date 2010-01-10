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
 * Redirect log events using java.util.logging
 */
class ClientLog {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(LogServicesImpl.CLIENT_LOGGER_NAME);

    private static java.util.logging.Level toLogLevel(com.pyx4j.log4gwt.shared.Level level) {
        switch (level) {
        case ERROR:
            return java.util.logging.Level.SEVERE;
        case WARN:
            return java.util.logging.Level.WARNING;
        case INFO:
            return java.util.logging.Level.INFO;
        case DEBUG:
            return java.util.logging.Level.FINE;
        case TRACE:
            return java.util.logging.Level.FINEST;
        default:
            return java.util.logging.Level.FINEST;
        }
    }

    static void log(LogEvent event) {
        java.util.logging.LogRecord record = new java.util.logging.LogRecord(toLogLevel(event.getLevel()), event.getFormatedMessageWithThrowable());
        record.setMillis(event.getEventTime());
        log.log(record);
    }
}
