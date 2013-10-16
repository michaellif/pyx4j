/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2013-03-08
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.config.server;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.commons.LogicalDate;

public class SystemDateManager {

    private static final Logger log = LoggerFactory.getLogger(SystemDateManager.class);

    private static class DateContext {

        long timedelta;

        // TODO assert call origin

    }

    private static final ThreadLocal<DateContext> threadLocale = new InheritableThreadLocal<DateContext>();

    public static boolean isDateSet() {
        return (threadLocale.get() != null);
    }

    public static long getTimeMillis() {
        DateContext dateContext = threadLocale.get();
        if (dateContext != null) {
            return System.currentTimeMillis() + dateContext.timedelta;
        } else {
            return System.currentTimeMillis();
        }
    }

    public static Date getDate() {
        return new Date(getTimeMillis());
    }

    public static LogicalDate getLogicalDate() {
        return new LogicalDate(getTimeMillis());
    }

    public static void setDate(Date date) {
        DateContext dateContext = threadLocale.get();
        if (dateContext == null) {
            dateContext = new DateContext();
        }
        dateContext.timedelta = date.getTime() - System.currentTimeMillis();
        threadLocale.set(dateContext);
        log.debug("System Date set to {}", date);
    }

    public static void advanceDate(Date date) {
        assert date.after(getDate());
        setDate(date);
    }

    /**
     * set to current computer date
     */
    public static void resetDate() {
        threadLocale.remove();
    }

    public static void removeThreadLocale() {
        threadLocale.remove();
    }

}
