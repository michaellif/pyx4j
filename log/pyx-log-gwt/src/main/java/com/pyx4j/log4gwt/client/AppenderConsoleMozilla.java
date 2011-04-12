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
 * Created on Mar 14, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.log4gwt.client;

import com.pyx4j.log4gwt.shared.LogEvent;

/**
 * Mozilla Error Console?
 */
public class AppenderConsoleMozilla implements Appender {

    public static boolean isSupported() {
        return supported();
    }

    public AppenderConsoleMozilla() {

    }

    @Override
    public String getAppenderName() {
        return "consoleMozilla";
    }

    @Override
    public void doAppend(LogEvent event) {
        switch (event.getLevel()) {
        case ERROR:
            error(LogFormatter.format(event, LogFormatter.FormatStyle.LINE));
            break;
        case WARN:
            warn(LogFormatter.format(event, LogFormatter.FormatStyle.LINE));
            break;
        case INFO:
            info(LogFormatter.format(event, LogFormatter.FormatStyle.LINE));
            break;
        case TRACE:
        case DEBUG:
            debug(LogFormatter.format(event, LogFormatter.FormatStyle.LINE));
            break;
        }
    }

    private static native boolean supported() /*-{
		return (typeof console != "undefined");
    }-*/;

    private native void debug(String message) /*-{
		console.log(message);
    }-*/;

    private native void info(String message) /*-{
		console.info(message);
    }-*/;

    private native void warn(String message) /*-{
		console.warn(message);
    }-*/;

    private native void error(String message) /*-{
		console.error(message);
    }-*/;

}
