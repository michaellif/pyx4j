/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
