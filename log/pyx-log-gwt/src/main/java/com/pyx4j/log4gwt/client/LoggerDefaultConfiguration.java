/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Apr 24, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.log4gwt.client;

import com.google.gwt.core.client.GWT;

class LoggerDefaultConfiguration {

    static void setUp() {
        if (GWT.isClient()) {
            if (GWT.isScript()) {
                if (AppenderFirebug.isSupported()) {
                    ClientLogger.addAppender(new AppenderFirebug());
                } else if (AppenderConsole.isSupported()) {
                    ClientLogger.addAppender(new AppenderConsole());
                }
            } else {
                ClientLogger.addAppender(new AppenderStdOut());
                ClientLogger.addAppender(new AppenderHosted());
            }
        } else {
            ClientLogger.addAppender(new AppenderStdOut());
        }
    }
}
