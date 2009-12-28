/**
 * Pyx4j framework
 * Copyright (C) 2006-2009 pyx4j.com.
 *
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.log4gwt.client;

import com.pyx4j.log4gwt.shared.LogEvent;

public interface Appender {

    /**
     * Get the name of this appender. The name uniquely identifies the appender.
     */
    public String getAppenderName();

    public void doAppend(LogEvent event);

}