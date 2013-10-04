/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on 2012-05-29
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.log4j;

import java.util.Map;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

class LogbackAdapter {

    static void init() {
        if (false) {
            ILoggerFactory lc = LoggerFactory.getILoggerFactory();
            if (lc instanceof LoggerContext) {
                LoggerContext context = (LoggerContext) lc;
                context.reset();
                context.setName(LoggerConfig.getContextName());
                for (Map.Entry<String, String> me : LoggerConfig.nameVariables.entrySet()) {
                    context.putProperty(me.getKey(), me.getValue());
                }
                try {
                    new ContextInitializer(context).autoConfig();
                } catch (JoranException je) {
                }
                StatusPrinter.printInCaseOfErrorsOrWarnings(context);
            }
        }
    }

    static void shutdown() {
        ILoggerFactory lc = LoggerFactory.getILoggerFactory();
        if (lc instanceof LoggerContext) {
            ((LoggerContext) lc).stop();
        }
    }
}
