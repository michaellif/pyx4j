/**
 * Pyx4j framework
 * Copyright (C) 2006-2009 pyx4j.com.
 *
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.log4gwt.client;

import com.pyx4j.log4gwt.shared.Level;
import com.pyx4j.log4gwt.shared.LogEvent;

public class AppenderStdOut implements Appender {

    public AppenderStdOut() {

    }

    @Override
    public String getAppenderName() {
        return "std";
    }

    @Override
    public void doAppend(LogEvent event) {
        if (event.getLevel().ordinal() <= Level.ERROR.ordinal()) {
            System.err.println(LogFormatter.format(event, LogFormatter.FormatStyle.LINE));
        } else {
            System.out.println(LogFormatter.format(event, LogFormatter.FormatStyle.LINE));
        }
    }

}
