/**
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.log4gwt.client;

import com.google.gwt.core.client.GWT;

import com.pyx4j.log4gwt.shared.Level;
import com.pyx4j.log4gwt.shared.LogEvent;

public class AppenderStdOut implements Appender {

    public static interface StackTraceFormater {

        public String format(LogEvent event);

    }

    private static class EmptyStackTraceFormater implements StackTraceFormater {

        @Override
        public String format(LogEvent event) {
            return "";
        }

    }

    StackTraceFormater stackTraceFormater;

    public AppenderStdOut() {
        if (!GWT.isScript()) {
            stackTraceFormater = new HostedStackTraceFormater();
        } else {
            stackTraceFormater = new EmptyStackTraceFormater();
        }
    }

    @Override
    public String getAppenderName() {
        return "std";
    }

    @Override
    public void doAppend(LogEvent event) {
        if (event.getLevel().ordinal() <= Level.ERROR.ordinal()) {
            System.err.println(LogFormatter.format(event, LogFormatter.FormatStyle.LINE) + stackTraceFormater.format(event));
        } else {
            System.out.println(LogFormatter.format(event, LogFormatter.FormatStyle.LINE) + stackTraceFormater.format(event));
        }
    }

}
