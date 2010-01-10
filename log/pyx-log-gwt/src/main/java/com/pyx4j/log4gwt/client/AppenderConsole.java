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
 * Created on Apr 24, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.log4gwt.client;

import com.pyx4j.log4gwt.shared.LogEvent;

/**
 * Console, used by Safari, Chrome and FireFox
 */
public class AppenderConsole implements Appender {

    public static boolean isSupported() {
        return supported();
    }

    public AppenderConsole() {

    }

    @Override
    public String getAppenderName() {
        return "console";
    }

    @Override
    public void doAppend(LogEvent event) {
        log(LogFormatter.format(event, LogFormatter.FormatStyle.LINE));
    }

    private static native boolean supported() /*-{
                                              return $wnd.console != null && typeof($wnd.console.log) == 'function';
                                              }-*/;

    private native void log(String message) /*-{
                                            $wnd.console.log(message);
                                            }-*/;

}
