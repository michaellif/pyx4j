/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Apr 24, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.log4gwt.client;

import com.google.gwt.core.client.GWT;
import com.pyx4j.log4gwt.shared.LogEvent;

public class AppenderHosted implements Appender {

    public AppenderHosted() {

    }

    @Override
    public String getAppenderName() {
        return "hosted";
    }

    @Override
    public void doAppend(LogEvent event) {
        GWT.log(LogFormatter.format(event, LogFormatter.FormatStyle.FULL_HOSTED), event.getThrowable());
    }

}
