/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 27, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.log4gwt.client;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class ClientLoggerFactory implements ILoggerFactory {

    public ClientLoggerFactory() {
        LoggerDefaultConfiguration.setUp();
    }

    //TODO Allow multiple/tree loggers with different appenders and levels
    @Override
    public Logger getLogger(String name) {
        return ClientLogger.instance();
    }

}
