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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.spi.LoggingEvent;

import com.pyx4j.log4gwt.shared.LogEvent;

/**
 * Redirect log events org.apache.log4j
 */
class ClientLogback {

    private static final Logger log = LoggerFactory.getLogger(LogServicesImpl.CLIENT_LOGGER_NAME);

    private static ch.qos.logback.classic.Level toLogbackLevel(com.pyx4j.log4gwt.shared.Level level) {
        switch (level) {
        case ERROR:
            return ch.qos.logback.classic.Level.ERROR;
        case WARN:
            return ch.qos.logback.classic.Level.WARN;
        case INFO:
            return ch.qos.logback.classic.Level.INFO;
        case DEBUG:
            return ch.qos.logback.classic.Level.DEBUG;
        case TRACE:
            return ch.qos.logback.classic.Level.TRACE;
        default:
            return ch.qos.logback.classic.Level.TRACE;
        }
    }

    static void log(LogEvent event) {
        LoggingEvent le = new LoggingEvent(log.getName(), ((ch.qos.logback.classic.Logger) log), toLogbackLevel(event.getLevel()),
                event.getFormatedMessageWithThrowable(), event.getThrowable(), null);
        ((ch.qos.logback.classic.Logger) log).callAppenders(le);
    }
}
