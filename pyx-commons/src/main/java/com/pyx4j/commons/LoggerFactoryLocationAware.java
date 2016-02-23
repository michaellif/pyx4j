/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Feb 23, 2016
 * @author vlads
 */
package com.pyx4j.commons;

import static org.slf4j.spi.LocationAwareLogger.DEBUG_INT;
import static org.slf4j.spi.LocationAwareLogger.ERROR_INT;
import static org.slf4j.spi.LocationAwareLogger.INFO_INT;
import static org.slf4j.spi.LocationAwareLogger.TRACE_INT;
import static org.slf4j.spi.LocationAwareLogger.WARN_INT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.spi.LocationAwareLogger;

/**
 * Factory to create logger that shifts location information in log to called of the class.
 */
public class LoggerFactoryLocationAware {

    public static Logger getLogger(Class<?> clazz) {
        Logger log = LoggerFactory.getLogger(clazz);
        if (log instanceof LocationAwareLogger) {
            return new LocationAwareLoggerWrapper((LocationAwareLogger) log, clazz);
        } else {
            return log;
        }
    }

    private static class LocationAwareLoggerWrapper implements Logger {

        private final String fqcn;

        private final LocationAwareLogger logger;

        LocationAwareLoggerWrapper(LocationAwareLogger logger, Class<?> clazz) {
            this.logger = logger;
            this.fqcn = clazz.getName();
        }

        @Override
        public String getName() {
            return logger.getName();
        }

        @Override
        public boolean isTraceEnabled() {
            return logger.isTraceEnabled();
        }

        @Override
        public void trace(String message) {
            logger.log(null, fqcn, TRACE_INT, message, null, null);
        }

        @Override
        public void trace(String format, Object arg) {
            logger.log(null, fqcn, TRACE_INT, format, new Object[] { arg }, null);
        }

        @Override
        public void trace(String format, Object arg1, Object arg2) {
            logger.log(null, fqcn, TRACE_INT, format, new Object[] { arg1, arg2 }, null);
        }

        @Override
        public void trace(String format, Object... arguments) {
            logger.log(null, fqcn, TRACE_INT, format, arguments, null);
        }

        @Override
        public void trace(String message, Throwable t) {
            logger.log(null, fqcn, TRACE_INT, message, null, t);
        }

        @Override
        public boolean isTraceEnabled(Marker marker) {
            return logger.isTraceEnabled(marker);
        }

        @Override
        public void trace(Marker marker, String message) {
            logger.log(marker, fqcn, TRACE_INT, message, null, null);
        }

        @Override
        public void trace(Marker marker, String format, Object arg) {
            logger.log(marker, fqcn, TRACE_INT, format, new Object[] { arg }, null);
        }

        @Override
        public void trace(Marker marker, String format, Object arg1, Object arg2) {
            logger.log(marker, fqcn, TRACE_INT, format, new Object[] { arg1, arg2 }, null);
        }

        @Override
        public void trace(Marker marker, String format, Object... argArray) {
            logger.log(marker, fqcn, TRACE_INT, format, argArray, null);
        }

        @Override
        public void trace(Marker marker, String msg, Throwable t) {
            logger.log(marker, fqcn, TRACE_INT, msg, null, t);
        }

        @Override
        public boolean isDebugEnabled() {
            return logger.isDebugEnabled();
        }

        @Override
        public void debug(String message) {
            logger.log(null, fqcn, DEBUG_INT, message, null, null);
        }

        @Override
        public void debug(String format, Object arg) {
            logger.log(null, fqcn, DEBUG_INT, format, new Object[] { arg }, null);
        }

        @Override
        public void debug(String format, Object arg1, Object arg2) {
            logger.log(null, fqcn, DEBUG_INT, format, new Object[] { arg1, arg2 }, null);
        }

        @Override
        public void debug(String format, Object... arguments) {
            logger.log(null, fqcn, DEBUG_INT, format, arguments, null);
        }

        @Override
        public void debug(String message, Throwable t) {
            logger.log(null, fqcn, DEBUG_INT, message, null, t);
        }

        @Override
        public boolean isDebugEnabled(Marker marker) {
            return logger.isDebugEnabled(marker);
        }

        @Override
        public void debug(Marker marker, String message) {
            logger.log(marker, fqcn, DEBUG_INT, message, null, null);
        }

        @Override
        public void debug(Marker marker, String format, Object arg) {
            logger.log(marker, fqcn, DEBUG_INT, format, new Object[] { arg }, null);
        }

        @Override
        public void debug(Marker marker, String format, Object arg1, Object arg2) {
            logger.log(marker, fqcn, DEBUG_INT, format, new Object[] { arg1, arg2 }, null);
        }

        @Override
        public void debug(Marker marker, String format, Object... arguments) {
            logger.log(marker, fqcn, DEBUG_INT, format, arguments, null);
        }

        @Override
        public void debug(Marker marker, String message, Throwable t) {
            logger.log(marker, fqcn, DEBUG_INT, message, null, t);
        }

        @Override
        public boolean isInfoEnabled() {
            return logger.isErrorEnabled();
        }

        @Override
        public void info(String message) {
            logger.log(null, fqcn, INFO_INT, message, null, null);
        }

        @Override
        public void info(String format, Object arg) {
            logger.log(null, fqcn, INFO_INT, format, new Object[] { arg }, null);
        }

        @Override
        public void info(String format, Object arg1, Object arg2) {
            logger.log(null, fqcn, INFO_INT, format, new Object[] { arg1, arg2 }, null);
        }

        @Override
        public void info(String format, Object... arguments) {
            logger.log(null, fqcn, INFO_INT, format, arguments, null);
        }

        @Override
        public void info(String message, Throwable t) {
            logger.log(null, fqcn, INFO_INT, message, null, t);
        }

        @Override
        public boolean isInfoEnabled(Marker marker) {
            return logger.isErrorEnabled(marker);
        }

        @Override
        public void info(Marker marker, String message) {
            logger.log(marker, fqcn, INFO_INT, message, null, null);
        }

        @Override
        public void info(Marker marker, String format, Object arg) {
            logger.log(marker, fqcn, INFO_INT, format, new Object[] { arg }, null);
        }

        @Override
        public void info(Marker marker, String format, Object arg1, Object arg2) {
            logger.log(marker, fqcn, INFO_INT, format, new Object[] { arg1, arg2 }, null);
        }

        @Override
        public void info(Marker marker, String format, Object... arguments) {
            logger.log(marker, fqcn, INFO_INT, format, arguments, null);
        }

        @Override
        public void info(Marker marker, String message, Throwable t) {
            logger.log(marker, fqcn, INFO_INT, message, null, t);
        }

        @Override
        public boolean isWarnEnabled() {
            return logger.isWarnEnabled();
        }

        @Override
        public void warn(String message) {
            logger.log(null, fqcn, WARN_INT, message, null, null);
        }

        @Override
        public void warn(String format, Object arg) {
            logger.log(null, fqcn, WARN_INT, format, new Object[] { arg }, null);
        }

        @Override
        public void warn(String format, Object arg1, Object arg2) {
            logger.log(null, fqcn, WARN_INT, format, new Object[] { arg1, arg2 }, null);
        }

        @Override
        public void warn(String format, Object... arguments) {
            logger.log(null, fqcn, WARN_INT, format, arguments, null);
        }

        @Override
        public void warn(String message, Throwable t) {
            logger.log(null, fqcn, WARN_INT, message, null, t);
        }

        @Override
        public boolean isWarnEnabled(Marker marker) {
            return logger.isWarnEnabled(marker);
        }

        @Override
        public void warn(Marker marker, String message) {
            logger.log(marker, fqcn, WARN_INT, message, null, null);
        }

        @Override
        public void warn(Marker marker, String format, Object arg) {
            logger.log(marker, fqcn, WARN_INT, format, new Object[] { arg }, null);
        }

        @Override
        public void warn(Marker marker, String format, Object arg1, Object arg2) {
            logger.log(marker, fqcn, WARN_INT, format, new Object[] { arg1, arg2 }, null);
        }

        @Override
        public void warn(Marker marker, String format, Object... arguments) {
            logger.log(marker, fqcn, WARN_INT, format, arguments, null);
        }

        @Override
        public void warn(Marker marker, String message, Throwable t) {
            logger.log(marker, fqcn, WARN_INT, message, null, t);
        }

        @Override
        public boolean isErrorEnabled() {
            return logger.isErrorEnabled();
        }

        @Override
        public void error(String message) {
            logger.log(null, fqcn, ERROR_INT, message, null, null);
        }

        @Override
        public void error(String format, Object arg) {
            logger.log(null, fqcn, ERROR_INT, format, new Object[] { arg }, null);
        }

        @Override
        public void error(String format, Object arg1, Object arg2) {
            logger.log(null, fqcn, ERROR_INT, format, new Object[] { arg1, arg2 }, null);
        }

        @Override
        public void error(String format, Object... arguments) {
            logger.log(null, fqcn, ERROR_INT, format, arguments, null);
        }

        @Override
        public void error(String message, Throwable t) {
            logger.log(null, fqcn, ERROR_INT, message, null, t);
        }

        @Override
        public boolean isErrorEnabled(Marker marker) {
            return logger.isErrorEnabled(marker);
        }

        @Override
        public void error(Marker marker, String message) {
            logger.log(marker, fqcn, ERROR_INT, message, null, null);
        }

        @Override
        public void error(Marker marker, String format, Object arg) {
            logger.log(marker, fqcn, ERROR_INT, format, new Object[] { arg }, null);
        }

        @Override
        public void error(Marker marker, String format, Object arg1, Object arg2) {
            logger.log(marker, fqcn, ERROR_INT, format, new Object[] { arg1, arg2 }, null);
        }

        @Override
        public void error(Marker marker, String format, Object... arguments) {
            logger.log(marker, fqcn, ERROR_INT, format, arguments, null);
        }

        @Override
        public void error(Marker marker, String message, Throwable t) {
            logger.log(marker, fqcn, ERROR_INT, message, null, t);
        }

    }

}
