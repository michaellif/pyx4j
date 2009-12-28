/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 27, 2009
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.log4gwt.client;

import java.util.List;

import org.slf4j.helpers.MarkerIgnoringBase;

import com.pyx4j.log4gwt.shared.Level;
import com.pyx4j.log4gwt.shared.LogEvent;

@SuppressWarnings("serial")
public class ClientLogger extends MarkerIgnoringBase {

    private static ClientLogger instance;

    private final AppenderCollection appenderCollection;

    private static boolean traceOn = false;

    private static boolean debugOn = false;

    protected ClientLogger() {
        appenderCollection = new AppenderCollection();
    }

    public static ClientLogger instance() {
        if (instance == null) {
            instance = new ClientLogger();
        }
        return instance;
    }

    /**
     * Attach an appender. If the appender is already in the list in won't be added again.
     */
    public static void addAppender(Appender newAppender) {
        instance().appenderCollection.addAppender(newAppender);
    }

    /**
     * Get all attached appenders as an Enumeration. If there are no attached appenders
     * <code>null</code> is returned.
     * 
     * @return Enumeration An enumeration of attached appenders.
     */
    public static List<Appender> getAllAppenders() {
        return instance().appenderCollection.getAllAppenders();
    }

    /**
     * Look for an attached appender named as <code>name</code>.
     * 
     * <p>
     * Return the appender with that name if in the list. Return null otherwise.
     * 
     */
    public static Appender getAppender(String name) {
        return instance().appenderCollection.getAppender(name);
    }

    /**
     * Returns <code>true</code> if the specified appender is in the list of attached
     * appenders, <code>false</code> otherwise.
     */
    public static boolean isAttached(Appender appender) {
        return instance().appenderCollection.isAttached(appender);
    }

    /**
     * Remove and close all previously attached appenders.
     */
    public static void removeAllAppenders() {
        instance().appenderCollection.removeAllAppenders();
    }

    /**
     * Remove the appender passed as parameter form the list of attached appenders.
     */
    public static void removeAppender(Appender appender) {
        instance().appenderCollection.removeAppender(appender);
    }

    /**
     * Remove the appender with the name passed as parameter form the list of appenders.
     */
    public static void removeAppender(String name) {
        instance().appenderCollection.removeAppender(name);
    }

    public static void setTraceOn(boolean traceOn) {
        ClientLogger.traceOn = traceOn;
    }

    public static boolean isTraceOn() {
        return traceOn;
    }

    @Override
    public boolean isTraceEnabled() {
        return ClientLogger.traceOn;
    }

    public static void setDebugOn(boolean debugOn) {
        ClientLogger.debugOn = debugOn;
    }

    public static boolean isDebugOn() {
        return debugOn;
    }

    @Override
    public boolean isDebugEnabled() {
        return ClientLogger.debugOn;
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    private void log(LogEvent event) {
        appenderCollection.callAppenders(event);
    }

    @Override
    public void trace(String msg) {
        if (traceOn) {
            log(new LogEvent(Level.TRACE, msg, null));
        }
    }

    @Override
    public void trace(String format, Object arg) {
        if (traceOn) {
            log(new LogEvent(Level.TRACE, format, null, arg));
        }
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (traceOn) {
            log(new LogEvent(Level.TRACE, format, null, arg1, arg2));
        }
    }

    @Override
    public void trace(String format, Object[] argArray) {
        if (traceOn) {
            log(new LogEvent(Level.TRACE, format, null, argArray));
        }
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (traceOn) {
            log(new LogEvent(Level.TRACE, msg, t));
        }
    }

    @Override
    public void debug(String msg) {
        if (debugOn) {
            log(new LogEvent(Level.DEBUG, msg, null));
        }
    }

    @Override
    public void debug(String format, Object arg) {
        if (debugOn) {
            log(new LogEvent(Level.DEBUG, format, null, arg));
        }
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (debugOn) {
            log(new LogEvent(Level.DEBUG, format, null, arg1, arg2));
        }
    }

    @Override
    public void debug(String format, Object[] argArray) {
        if (debugOn) {
            log(new LogEvent(Level.DEBUG, format, null, argArray));
        }
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (debugOn) {
            log(new LogEvent(Level.DEBUG, msg, t));
        }
    }

    @Override
    public void info(String msg) {
        log(new LogEvent(Level.INFO, msg, null));
    }

    @Override
    public void info(String format, Object arg) {
        log(new LogEvent(Level.INFO, format, null, arg));
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        log(new LogEvent(Level.INFO, format, null, arg1, arg2));
    }

    @Override
    public void info(String format, Object[] argArray) {
        log(new LogEvent(Level.INFO, format, null, argArray));
    }

    @Override
    public void info(String msg, Throwable t) {
        log(new LogEvent(Level.INFO, msg, t));
    }

    @Override
    public void warn(String msg) {
        log(new LogEvent(Level.WARN, msg, null));
    }

    @Override
    public void warn(String format, Object arg) {
        log(new LogEvent(Level.WARN, format, null, arg));
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        log(new LogEvent(Level.WARN, format, null, arg1, arg2));
    }

    @Override
    public void warn(String format, Object[] argArray) {
        log(new LogEvent(Level.WARN, format, null, argArray));
    }

    @Override
    public void warn(String msg, Throwable t) {
        log(new LogEvent(Level.WARN, msg, t));
    }

    @Override
    public void error(String msg) {
        log(new LogEvent(Level.ERROR, msg, null));
    }

    @Override
    public void error(String format, Object arg) {
        log(new LogEvent(Level.ERROR, format, null, arg));
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        log(new LogEvent(Level.ERROR, format, null, arg1, arg2));
    }

    @Override
    public void error(String format, Object[] argArray) {
        log(new LogEvent(Level.ERROR, format, null, argArray));
    }

    @Override
    public void error(String msg, Throwable t) {
        log(new LogEvent(Level.ERROR, msg, t));
    }
}
