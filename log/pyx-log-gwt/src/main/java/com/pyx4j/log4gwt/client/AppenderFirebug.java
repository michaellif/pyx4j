/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 24, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.log4gwt.client;

import com.pyx4j.log4gwt.shared.LogEvent;

public class AppenderFirebug implements Appender {

    public static boolean isSupported() {
        return supported();
    }

    public AppenderFirebug() {

    }

    @Override
    public String getAppenderName() {
        return "firebug";
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
        case DEBUG:
            debug(LogFormatter.format(event, LogFormatter.FormatStyle.LINE));
            break;
        }
    }

    private static native boolean supported() /*-{
        return $wnd.console != null && $wnd.console.firebug;
    }-*/;

    private native void debug(String message) /*-{
        $wnd.console.debug(message);
    }-*/;

    private native void info(String message) /*-{
        $wnd.console.info(message);
    }-*/;

    private native void warn(String message) /*-{
        $wnd.console.warn(message);
    }-*/;

    private native void error(String message) /*-{
        $wnd.console.error(message);
    }-*/;

}
